package dev.amll.droidmate.websocket

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.github.zeehan2005.scoremuse.MainActivity
import dev.amll.droidmate.R
import com.amll.droidmate.websocket.AMLLWebSocketClient
import dev.amll.droidmate.global.AMLLSettings
import io.github.zeehan2005.scoremuse.service.MediaInfoService
import io.github.zeehan2005.scoremuse.global.NowPlayingMusic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.URI
import kotlin.jvm.java

/**
 * WebSocket 前台服务 - 保持 WebSocket 连接在后台持续运行
 * 
 * Android 系统会在应用进入后台一段时间后杀死其进程以节省电量。
 * 这个前台服务通过显示一个持久通知，告诉系统"这个应用正在执行重要任务"，
 * 从而防止系统杀死 WebSocket 连接。
 * 
 * 主要功能：
 * 1. 启动并维护 WebSocket 连接
 * 2. 监听媒体播放状态变化
 * 3. 实时同步播放状态到 AMLL 服务
 * 4. 提供用户可交互的通知（停止服务按钮）
 */
class WebSocketForegroundService : Service() {
    
    companion object {
        const val NOTIFICATION_ID = 20042          // 通知 ID（唯一标识）
        const val CHANNEL_ID = "websocket_foreground_channel"  // 通知渠道 ID
        const val ACTION_STOP_SERVICE = "com.amll.droidmate.action.STOP_WEBSOCKET_SERVICE"  // 停止服务的 Action
        
        private var isRunning = false  // 服务运行状态标记
        
        /**
         * 启动前台服务
         * 
         * 这是一个静态方法，用于从任何地方启动 WebSocket 服务。
         * 会检查服务是否已在运行，避免重复启动。
         * 
         * @param context 上下文
         * @param serverUrl WebSocket 服务器地址（如 ws://localhost:8080）
         */
        fun start(context: Context, serverUrl: String) {
            if (isRunning) {
                Timber.w("[WebSocketService] Service already running, skipping start")
                return
            }
            
            Timber.i("[WebSocketService] Starting WebSocketForegroundService")
            val intent = Intent(context, WebSocketForegroundService::class.java).apply {
                putExtra("server_url", serverUrl)  // 传递服务器地址给服务
            }
            
            // Android O+ 必须使用 startForegroundService，旧版本使用 startService
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        /**
         * 停止前台服务
         * 
         * 安全地停止服务并清理资源。
         * 会先检查服务是否在运行，避免无效操作。
         * 
         * @param context 上下文
         */
        fun stop(context: Context) {
            if (!isRunning) {
                Timber.w("[WebSocketService] Service not running, skipping stop")
                return
            }
            
            Timber.i("[WebSocketService] Stopping WebSocketForegroundService")
            val intent = Intent(context, WebSocketForegroundService::class.java)
            context.stopService(intent)
        }
        
        /**
         * 检查服务是否正在运行
         */
        fun isServiceRunning(): Boolean = isRunning
    }
    
    private lateinit var mediaInfoService: MediaInfoService
    private val webSocketClient = AMLLWebSocketClient.getInstance()
    private var serviceJob: Job? = null
    private lateinit var serviceScope: CoroutineScope
    private var serverUrl: String? = null
    
    // 服务状态
    private val _connectionState = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean> = _connectionState
    
    // WebSocket 监听器
    private val webSocketListener = object : AMLLWebSocketClient.Listener {
        override fun onConnected() {
            Timber.d("[WebSocketService] WebSocket connected")
            _connectionState.value = true
            updateNotificationConnectionState(connected = true)
        }
        
        override fun onDisconnected() {
            Timber.w("[WebSocketService] WebSocket disconnected")
            _connectionState.value = false
            updateNotificationConnectionState(connected = false)
        }
        
        override fun onMessageReceived(message: String) {
            Timber.d("[WebSocketService] Received message: $message")
        }
        
        override fun onError(error: Throwable) {
            Timber.e("[WebSocketService] WebSocket error: ${error.message}", error)
            _connectionState.value = false
        }
        
        override fun getCurrentPlayState(): AMLLWebSocketClient.PlayState? {
            // 从 MediaInfoService 获取当前播放状态
            val music = mediaInfoService.nowPlayingMusic.value
            if (music == null) {
                Timber.d("[WebSocketService] No active playback")
                return null
            }
            
            val validMusicId = music.packageName?.takeIf { it.isNotEmpty() && it != "unknown" }
                ?: music.title.takeIf { it.isNotEmpty() && it != "Unknown" }
            
            if (validMusicId == null) {
                Timber.d("[WebSocketService] No valid song info")
                return null
            }
            
            return AMLLWebSocketClient.PlayState(
                musicId = validMusicId,
                musicName = music.title,
                albumName = music.album ?: "",
                artistName = music.artist,
                duration = music.duration,
                progress = music.currentPosition,
                isPlaying = music.isPlaying,
                ttmlLyric = null  // Service 中不处理歌词
            )
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        Timber.i("[WebSocketService] Service created")
        
        serviceScope = CoroutineScope(Dispatchers.Main + Job())
        mediaInfoService = MediaInfoService(this)
        
        // 创建通知渠道
        createNotificationChannel()
        
        // 添加 WebSocket 监听器
        webSocketClient.addListener(webSocketListener)
    }
    
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("[WebSocketService] Service started")
        
        // 获取服务器地址
        serverUrl = intent?.getStringExtra("server_url")
        
        // 启动前台服务
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        isRunning = true
        
        // 开始监听媒体信息
        startListening()
        
        // 连接到 WebSocket（如果提供了服务器地址）
        serverUrl?.let { url ->
            if (AMLLSettings.isWebSocketProtocolEnabled(this)) {
                webSocketClient.connect(url)
            }
        }
        
        // START_STICKY: 确保服务被杀死后能重建
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        // 不支持绑定
        return null
    }
    
    override fun onDestroy() {
        Timber.i("[WebSocketService] Service destroying")
        
        isRunning = false
        
        // 停止监听
        stopListening()
        
        // 移除 WebSocket 监听器
        webSocketClient.removeListener(webSocketListener)
        
        // 断开 WebSocket 连接（但不销毁单例）
        if (webSocketClient.isConnected()) {
            webSocketClient.disconnect()
        }
        
        // 取消协程作用域
        serviceScope.cancel()
        
        super.onDestroy()
    }
    
    /**
     * 创建通知渠道（Android 8.0+）
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        
        val channel = NotificationChannel(
            CHANNEL_ID,
            "WebSocket 传递",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "根据 Android 要求，需要通知权限以保持 WebSocket 连接。"
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            enableVibration(false)
            setSound(null, null)
        }
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        Timber.d("[WebSocketService] Notification channel created: $CHANNEL_ID")
    }
    
    /**
     * 创建前台服务通知
     */
    private fun createNotification(): Notification {
        // 点击通知打开应用
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 停止服务的操作
        val stopIntent = Intent(ACTION_STOP_SERVICE).apply {
            setPackage(packageName)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 构建通知
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WebSocket 传递")
            .setContentText("正在保持 WebSocket 连接...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(openAppPendingIntent)
            // .addAction(R.drawable.ic_stop, "停止", stopPendingIntent) // 可选：添加停止按钮
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }
    
    /**
     * 更新通知中的连接状态
     */
    private fun updateNotificationConnectionState(connected: Boolean) {
        val serverUrl = AMLLSettings.getWebSocketProtocolAddress(applicationContext)
        val urlDisplay = if (serverUrl.isNotEmpty()) {
            try {
                val uri = URI(serverUrl)
                "${uri.host}:${if (uri.port == -1) "默认端口" else uri.port}"
            } catch (e: Exception) {
                serverUrl
            }
        } else {
            "未配置地址"
        }
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("WebSocket 传递")
            .setContentText(if (connected) "已连接 - $urlDisplay" else "未连接 - 正在重试...")
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java).apply {
                        setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    /**
     * 开始监听媒体信息
     */
    private fun startListening() {
        Timber.i("[WebSocketService] Starting media info listening")
        mediaInfoService.startListening()
        
        // 监听媒体信息变化并同步到 WebSocket
        serviceScope.launch {
            mediaInfoService.nowPlayingMusic.collect { music ->
                if (music != null && webSocketClient.isConnected()) {
                    Timber.d("[WebSocketService] Media info changed: ${music.title} - ${music.artist}")
                    // 这里可以根据需要自动同步状态到 WebSocket
                }
            }
        }
    }
    
    /**
     * 停止监听媒体信息
     */
    private fun stopListening() {
        Timber.i("[WebSocketService] Stopping media info listening")
        mediaInfoService.stopListening()
    }
}
