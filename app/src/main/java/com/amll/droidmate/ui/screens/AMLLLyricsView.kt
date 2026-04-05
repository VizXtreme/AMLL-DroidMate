package com.amll.droidmate.ui.screens

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.amll.droidmate.domain.model.TTMLLyrics
import com.amll.droidmate.ui.AppSettings
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import android.graphics.Color as AndroidColor

/**
 * AMLL 歌词视图渲染模式
 * 
 * 这个枚举定义了两种不同的 DOM 渲染策略，源自原 AMLL 项目：
 * 
 * **DOM** - 标准 DOM 渲染：
 * - 使用 AMLL Core 的完整 LyricPlayer 组件
 * - 支持所有视觉效果和动画
 * - 性能开销较大，但效果最佳
 * 
 * **DOM_LITE** - 轻量级 DOM 渲染：
 * - 使用简化版的 DOM 渲染（阉割版）
 * - 移除了部分复杂的视觉效果
 * - 性能更好，适合低端设备或省电模式
 */
enum class AMLLRenderMode {
    DOM,         // 标准 DOM 渲染
    DOM_LITE     // 轻量级 DOM 渲染
}

// AMLL 日志标签：用于 WebView 相关的日志输出
private const val AMLL_LOG_TAG = "AMLL"
// AMLL 视图实例计数器：用于调试和内存管理
private val AMLL_VIEW_INSTANCE_COUNTER = AtomicInteger(0)

@Composable
fun AMLLLyricsView(
    // 歌词数据（TTML 格式，包含完整的歌曲结构和时间信息）
    lyrics: TTMLLyrics?,
    // 当前播放进度（毫秒），用于同步歌词高亮
    currentTime: Long,
    // 歌曲唯一标识符（用于去重和状态追踪）
    musicId: String = "",
    // 歌曲名称（显示在界面上）
    musicName: String = "Unknown",
    // 专辑名称（用于元数据显示）
    albumName: String = "",
    // 艺术家名称（显示在界面上）
    artistName: String = "Unknown",
    // 歌曲总时长（毫秒）
    duration: Long = 0L,
    // 专辑封面图片 URI（可以是 file://、content://或 data URL）
    albumArtUri: String? = null,
    // 渲染模式：DOM（完整效果）或 DOM_LITE（轻量版）
    renderMode: AMLLRenderMode = AMLLRenderMode.DOM,
    // 调试来源标签（用于日志输出，区分不同的实例）
    debugSource: String = "unknown",
    // 歌词点击事件回调（用户点击歌词区域时触发）
    onLyricsClick: (() -> Unit)? = null,
    // 歌词行跳转回调（用户点击某行歌词时跳转到指定时间）
    onLineSeek: ((Long) -> Unit)? = null,
    // 是否正在播放（用于同步播放/暂停状态）
    isPlaying: Boolean = true,
    // Compose 修饰符（用于调整大小、背景等样式）
    modifier: Modifier = Modifier
) {
    // 获取 Android Context（用于访问应用设置和资源）
    val context = androidx.compose.ui.platform.LocalContext.current
    // 检查 WebView 是否启用（用户可以在设置中禁用）
    val webViewEnabled = AppSettings.isWebViewEnabled(context)
    
    // ==================== WebSocket 客户端和状态管理 ====================
    // 获取全局唯一的 WebSocket 客户端单例
    val webSocketClient = remember { 
        com.amll.droidmate.websocket.AMLLWebSocketClient.getInstance() 
    }
    // WebSocket 连接状态（用于 UI 显示和逻辑判断）
    var isWebSocketConnected by remember { mutableStateOf(false) }
    
    // 初始化 WebSocket 监听器（无论 WebView 是否启用都执行）
    // 这样即使禁用 WebView，也能通过 WebSocket 同步播放状态
    InitializeWebSocketListener(
        musicId = musicId,
        musicName = musicName,
        albumName = albumName,
        artistName = artistName,
        duration = duration,
        currentTime = currentTime,
        isPlaying = isPlaying,
        lyrics = lyrics,
        debugSource = debugSource,
        onCommandReceived = { command, valueObj ->
            when (command) {
                "pause" -> {
                    Timber.i("[AMLLLyrics] 收到暂停命令，执行暂停操作")
                    // 发送系统广播：媒体按钮事件（暂停）
                    val pauseIntent = android.content.Intent("android.intent.action.MEDIA_BUTTON").apply {
                        putExtra(android.content.Intent.EXTRA_KEY_EVENT, android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_MEDIA_PAUSE))
                        addFlags(android.content.Intent.FLAG_RECEIVER_REGISTERED_ONLY)
                    }
                    context.sendBroadcast(pauseIntent)
                }
                "resume" -> {
                    Timber.i("[AMLLLyrics] 收到恢复播放命令，执行播放操作")
                    // 发送系统广播：媒体按钮事件（播放）
                    val playIntent = android.content.Intent("android.intent.action.MEDIA_BUTTON").apply {
                        putExtra(android.content.Intent.EXTRA_KEY_EVENT, android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_MEDIA_PLAY))
                        addFlags(android.content.Intent.FLAG_RECEIVER_REGISTERED_ONLY)
                    }
                    context.sendBroadcast(playIntent)
                }
                "forwardSong" -> {
                    Timber.i("[AMLLLyrics] 收到下一首命令，执行下一首操作")
                    // 发送系统广播：媒体按钮事件（下一首）
                    val nextIntent = android.content.Intent("android.intent.action.MEDIA_BUTTON").apply {
                        putExtra(android.content.Intent.EXTRA_KEY_EVENT, android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_MEDIA_NEXT))
                        addFlags(android.content.Intent.FLAG_RECEIVER_REGISTERED_ONLY)
                    }
                    context.sendBroadcast(nextIntent)
                }
                "backwardSong" -> {
                    Timber.i("[AMLLLyrics] 收到上一首命令，执行上一首操作")
                    // 发送系统广播：媒体按钮事件（上一首）
                    val prevIntent = android.content.Intent("android.intent.action.MEDIA_BUTTON").apply {
                        putExtra(android.content.Intent.EXTRA_KEY_EVENT, android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS))
                        addFlags(android.content.Intent.FLAG_RECEIVER_REGISTERED_ONLY)
                    }
                    context.sendBroadcast(prevIntent)
                }
                "seekPlayProgress" -> {
                    val progress = valueObj?.get("progress")?.jsonPrimitive?.content?.toLongOrNull()
                    if (progress != null) {
                        Timber.i("[AMLLLyrics] 收到跳转进度命令：$progress ms，执行跳转操作")
                        // 使用 MediaInfoService 进行跳转
                        val mediaInfoService = com.amll.droidmate.service.MediaInfoService(context)
                        mediaInfoService.seekTo(progress)
                    } else {
                        Timber.w("[AMLLLyrics] 跳转进度命令参数无效")
                    }
                }
                "setVolume" -> {
                    val volume = valueObj?.get("volume")?.jsonPrimitive?.content?.toDoubleOrNull()
                    if (volume != null) {
                        Timber.i("[AMLLLyrics] 收到音量设置命令：$volume")
                        // 使用 AudioManager 设置系统音量
                        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                        // 将 0.0-1.0 的音量转换为系统音量级别（0-15）
                        val maxVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
                        val targetVolume = (volume * maxVolume).toInt().coerceIn(0, maxVolume)
                        audioManager.setStreamVolume(
                            android.media.AudioManager.STREAM_MUSIC,
                            targetVolume,
                            0  // 不显示音量 UI
                        )
                        Timber.d("[AMLLLyrics] 音量已设置：${volume} -> $targetVolume/$maxVolume")
                    } else {
                        Timber.w("[AMLLLyrics] 音量设置命令参数无效")
                    }
                }
                "setRepeatMode", "setShuffleMode" -> {
                    Timber.d("[AMLLLyrics] 收到不支持的命令：$command，忽略")
                    // 忽略这些命令，不回复错误（避免频繁发送错误消息）
                }
                else -> {
                    Timber.d("[AMLLLyrics] 未知命令：$command")
                }
            }
        },
        onConnectedCallback = {
            isWebSocketConnected = true
            Timber.d("[AMLLLyrics] WebSocket 已连接，当前歌曲信息：musicId=$musicId, musicName=$musicName, artist=$artistName")
        },
        onErrorCallback = { error ->
            isWebSocketConnected = false
            // 打印更详细的错误信息
            when (error) {
                is java.io.EOFException -> {
                    Timber.e("[AMLLLyrics] 服务器主动断开了连接")
                }
                is java.net.ConnectException -> {
                    Timber.e("[AMLLLyrics] 无法连接到服务器")
                }
                else -> {
                    Timber.e("[AMLLLyrics] 未知错误类型：${error.javaClass.simpleName}")
                }
            }
        }
    )
    
    // 如果 WebView 被禁用，不渲染歌词 UI，但仍保持 WebSocket 通信
    if (!webViewEnabled) {
        Timber.d("[AMLLLyrics] [WebView] [$debugSource] WebView 已禁用，跳过歌词渲染（但 WebSocket 仍在运行）")
        return
    }
    
    // ==================== 内部状态变量定义 ====================
    // 视图实例 ID（用于调试日志，区分多个 AMLLLyricsView 实例）
    val instanceId = remember { AMLL_VIEW_INSTANCE_COUNTER.incrementAndGet() }
    
    // 使用 rememberUpdatedState 确保回调函数始终是最新的
    // 这样可以避免因为闭包捕获旧值而导致的 stale closure 问题
    val onLyricsClickState = rememberUpdatedState(onLyricsClick)
    val onLineSeekState = rememberUpdatedState(onLineSeek)
    val isPlayingState = rememberUpdatedState(isPlaying)
    
    // 页面就绪状态（WebView 加载完成后设为 true）
    var isPageReady by remember { mutableStateOf(false) }
    
    // 上一次配置值的缓存（用于去重，避免重复调用 JavaScript）
    var lastModeValue by remember { mutableStateOf<String?>(null) }
    var lastBackgroundProfileValue by remember { mutableStateOf<String?>(null) }
    var lastLyricSizePreset by remember { mutableStateOf<String?>(null) }
    var lastEnableAdvanceDynamicTime by remember { mutableStateOf<Boolean?>(null) }
    
    // 上一次的歌词数据引用（用于检测歌词是否变化）
    var lastLyrics by remember { mutableStateOf<TTMLLyrics?>(null) }
    // 上一次生成的歌词 JSON 字符串（用于页面刷新后重新注入）
    var lastLyricsPayload by remember { mutableStateOf<String?>(null) }
    
    // 上一次设置的专辑封面 URI（用于去重）
    var lastAlbumArtUri by remember { mutableStateOf<String?>(null) }
    
    // 字体配置相关状态
    var lastFontConfigSignature by remember { mutableStateOf<String?>(null) }
    var lastMotionConfigValue by remember { mutableStateOf<String?>(null) }
    
    // ==================== WebSocket 发送状态记录 ====================
    // 记录上一次发送的状态，用于去重（避免频繁发送相同数据）
    var lastSentMusicId by remember { mutableStateOf<String?>(null) }
    var lastSentMusicName by remember { mutableStateOf<String?>(null) }
    var lastSentAlbumName by remember { mutableStateOf<String?>(null) }
    var lastSentArtistName by remember { mutableStateOf<String?>(null) }
    var lastSentIsPlaying by remember { mutableStateOf<Boolean?>(null) }
    
    /**
     * 发送播放状态到 WebSocket 服务器（V2 JSON 协议）
     * 
     * **优化策略**：
     * - 仅在歌曲信息或播放状态变化时发送
     * - 播放进度惯性除外（不频繁发送进度更新）
     * - 使用 V2 JSON 协议而非二进制协议
     * 
     * @param currentTime 当前播放时间（毫秒）
     * @param isPlaying 是否正在播放
     */
    fun sendPlaybackStatusToWebSocket(currentTime: Long, isPlaying: Boolean) {
        // WebSocket 未连接时直接返回，避免无效操作
        if (!isWebSocketConnected) {
            Timber.d("[AMLLLyrics] [WebSocket] [$debugSource#$instanceId] WebSocket 未连接，跳过发送")
            return
        }
            
        // ==================== 状态变化检测 ====================
        // 检查歌曲基本信息是否变化（ID、名称、专辑、艺术家）
        val musicInfoChanged = musicId != lastSentMusicId ||
                               musicName != lastSentMusicName ||
                               albumName != lastSentAlbumName ||
                               artistName != lastSentArtistName
        
        // 检查播放状态是否变化（播放/暂停）
        val playingStateChanged = isPlaying != lastSentIsPlaying
        
        // ==================== 去重逻辑 ====================
        // 只有状态变化时才发送，避免频繁网络请求
        if (!musicInfoChanged && !playingStateChanged) {
            // 状态无变化，不发送（播放进度惯性除外）
            return
        }
            
        // ==================== V2 JSON 协议消息发送 ====================
        try {
            // Step 1: 如果歌曲信息变化，发送新的歌曲信息
            if (musicInfoChanged) {
                // 使用 WsProtocolV2Helper 创建 SetMusicUpdate 消息
                val message = com.amll.droidmate.websocket.WsProtocolV2Helper.createSetMusicUpdate(
                    musicId = musicId,
                    musicName = musicName,
                    albumName = albumName,
                    artists = listOf(com.amll.droidmate.websocket.WsProtocolV2Helper.Artist("1", artistName)),
                    duration = duration
                )
                Timber.d("[AMLLLyrics] [WebSocket] [$debugSource#$instanceId] 发送歌曲信息 (V2 JSON): $musicName")
                webSocketClient.send(message)
                
                // 更新记录的状态，避免下次重复发送
                lastSentMusicId = musicId
                lastSentMusicName = musicName
                lastSentAlbumName = albumName
                lastSentArtistName = artistName
            }
            
            // Step 2: 如果播放状态变化，发送播放/暂停状态
            if (playingStateChanged) {
                // 根据播放状态选择对应的消息类型
                val stateMessage = if (isPlaying) {
                    com.amll.droidmate.websocket.WsProtocolV2Helper.createResumedUpdate()
                } else {
                    com.amll.droidmate.websocket.WsProtocolV2Helper.createPausedUpdate()
                }
                
                Timber.d("[AMLLLyrics] [WebSocket] [$debugSource#$instanceId] 发送播放状态消息 (V2 JSON): isPlaying=$isPlaying")
                webSocketClient.send(stateMessage)
                
                // 更新记录的状态
                lastSentIsPlaying = isPlaying
            }
            
            // ⭐ 注意：不发送播放进度更新（currentTime），避免频繁网络请求
            // 只在用户主动跳转或歌曲切换时才更新进度
        } catch (e: Exception) {
            Timber.e("[AMLLLyrics] [WebSocket] [$debugSource#$instanceId] 发送 V2 消息失败", e)
        }
    }
    
    // WebSocket 状态同步逻辑 - 当歌曲信息或播放状态变化时立即发送
    LaunchedEffect(musicId, musicName, albumName, artistName, duration, isPlaying) {
        if (AppSettings.isWebSocketProtocolEnabled(context) && isWebSocketConnected) {
            // 当歌曲信息或播放状态变化时，立即发送新状态（播放进度惯性除外）
            sendPlaybackStatusToWebSocket(currentTime, isPlaying)
        }
    }
    
    // 注入 WebSocket 桥接代码到 WebView
    // 使用统一的 TTMLConverter.toTTMLString() 代替本地实现
    

    
    /**
     * 注入 WebSocket 桥接代码到 WebView
     * 
     * 这段 JavaScript 代码会在 WebView 中创建一个全局对象 `AndroidWebSocketBridge`，
     * 允许前端页面通过 JavascriptInterface 发送消息到 Android 端。
     * 
     * **工作原理**：
     * 1. 检查是否已存在桥接对象（避免重复注入）
     * 2. 创建 send() 方法，将消息通过 JSON.stringify 序列化
     * 3. 调用 window.Android.sendWebSocketMessage() 发送到 Android
     */
    fun injectWebSocketBridge(view: WebView) {
        Timber.d("[AMLLLyrics] [$debugSource#$instanceId] 注入 WebSocket 桥接代码")
        
        // 使用 evaluateJavascript 注入 JavaScript 代码
        view.evaluateJavascript(
            """
            (function() {
                // 检查是否已存在桥接对象（避免重复注入）
                if (!window.AndroidWebSocketBridge) {
                    // 创建全局桥接对象
                    window.AndroidWebSocketBridge = {
                        send: function(message) {
                            // 通过 JavascriptInterface 发送到 Android
                            // window.Android 是在 addJavascriptInterface 时注册的对象
                            if (window.Android && window.Android.sendWebSocketMessage) {
                                window.Android.sendWebSocketMessage(JSON.stringify(message));
                            }
                        }
                    };
                    console.log('[AMLL Bridge] WebSocket bridge injected');
                }
            })();
            """.trimIndent(),
            null  // 不需要回调结果
        )
    }
    
    // WebSocket 状态同步逻辑 - 当歌曲信息或播放状态变化时立即发送
    LaunchedEffect(musicId, musicName, albumName, artistName, duration, isPlaying) {
        if (AppSettings.isWebSocketProtocolEnabled(context) && isWebSocketConnected) {
            // 当歌曲信息或播放状态变化时，立即发送新状态（播放进度惯性除外）
            sendPlaybackStatusToWebSocket(currentTime, isPlaying)
        }
    }

    // ==================== WebView 组件定义 ====================
    // 使用 AndroidView 将原生 WebView 嵌入到 Compose 界面中
    AndroidView(
        modifier = modifier,  // 应用传入的修饰符
        factory = { context ->
            // WebView 工厂函数：创建并配置 WebView 实例
            Timber.i("[AMLLLyrics] [$debugSource#$instanceId] Creating AMLL WebView, onLineSeek=${onLineSeekState.value != null}")
            
            // 启用 WebView 调试功能（可在 Chrome DevTools 中调试）
            WebView.setWebContentsDebuggingEnabled(true)
            
            WebView(context).apply {
                // 设置 WebView 的 LayoutParams 为 MATCH_PARENT（填满父容器）
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // ==================== WebViewClient 配置 ====================
                // 监听 WebView 页面加载事件
                webViewClient = object : WebViewClient() {
                    /**
                     * 页面开始加载时回调
                     * - 重置所有就绪状态
                     * - 清空上一次配置的缓存
                     */
                    override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                        isPageReady = false
                        lastModeValue = null
                        lastBackgroundProfileValue = null
                        lastLyrics = null
                        lastLyricsPayload = null
                        lastAlbumArtUri = null
                        lastFontConfigSignature = null
                        Timber.d("[AMLLLyrics] [$debugSource#$instanceId] WebView page started: $url")
                    }

                    /**
                     * 页面加载完成时回调
                     * - 标记页面就绪
                     * - 重新应用歌词和配置
                     * - 注入 WebSocket 桥接代码
                     */
                    override fun onPageFinished(view: WebView, url: String) {
                        isPageReady = true
                        // Force one re-sync after page finishes to avoid losing early bridge calls.
                        lastModeValue = null
                        lastBackgroundProfileValue = null
                        // 页面刷新结束时不主动清空 lastLyrics，让我们知道是否还有有效歌词
                        // lastLyrics = null
                        // 页面刷新完成后如果我们之前有歌词 JSON 且当前仍然有 lyrics（不是因歌曲切换而清空），先立刻重新下发
                        if (lastLyricsPayload != null && lastLyrics != null) {
                            Timber.d("[AMLLLyrics] [$debugSource#$instanceId] reapplying lyrics payload after page finish")
                            view.evaluateJavascript("window.updateLyrics && window.updateLyrics($lastLyricsPayload);", null)
                        }
                        // 不清空 payload，让 update() 继续根据 lyrics 对象决定重新生成
                        // lastLyricsPayload = null
                        lastAlbumArtUri = null
                        lastFontConfigSignature = null
                        // 确保页面加载后背景仍然透明
                        view.setBackgroundColor(AndroidColor.TRANSPARENT)
                        
                        // 注入 WebSocket 桥接代码（如果启用了 WebSocket）
                        if (isWebSocketConnected) {
                            injectWebSocketBridge(view)
                        }
                        
                        Timber.d("[AMLLLyrics] [$debugSource#$instanceId] WebView page finished: $url")
                    }
                }
                // ==================== WebChromeClient 配置 ====================
                // 处理 JavaScript 控制台日志，将其转发到 Timber
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                        val logMessage = "[AMLLLyrics] [WebView] [$debugSource#$instanceId] JS Console(@${consoleMessage.sourceId()}:${consoleMessage.lineNumber()}): ${consoleMessage.message()}"
                        // 根据日志级别分别处理
                        when (consoleMessage.messageLevel()) {
                            ConsoleMessage.MessageLevel.DEBUG -> Timber.d(logMessage)
                            ConsoleMessage.MessageLevel.LOG -> Timber.i(logMessage)
                            ConsoleMessage.MessageLevel.WARNING -> Timber.w(logMessage)
                            ConsoleMessage.MessageLevel.ERROR -> Timber.e(logMessage)
                            else -> Timber.d(logMessage)
                        }
                        return super.onConsoleMessage(consoleMessage)
                    }
                }
                // ==================== WebView 安全配置 ====================
                // 已弃用的 WebView 配置，但为了保持兼容性暂时保留
                @Suppress("DEPRECATION")
                settings.apply {
                    javaScriptEnabled = true       // 启用 JavaScript
                    domStorageEnabled = true       // 启用 DOM 存储（localStorage 等）
                    allowFileAccess = true         // 允许访问文件
                    allowContentAccess = true      // 允许访问内容提供者
                    // 仅允许从本地文件 URI 读取资源（用于专辑封面）
                    // 禁用跨文件访问以提升安全性
                    allowFileAccessFromFileURLs = false
                    allowUniversalAccessFromFileURLs = false
                    // 禁用缓存确保每次加载最新的文件
                    cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                    setRenderPriority(android.webkit.WebSettings.RenderPriority.HIGH)
                }

                // 透明 WebView 配置，允许宿主 Compose 层的专辑图背景透出
                // 先设置背景透明
                setBackgroundColor(AndroidColor.TRANSPARENT)
                // 使用 NONE 让 View 自行决定渲染方式，通常会使用硬件加速
                // 同时避免软件渲染导致的帧率问题
                setLayerType(View.LAYER_TYPE_NONE, null)
                
                // 强制清除所有缓存数据，确保加载最新的 HTML 和 JS
                clearAllCache()

                // keep a reference to the WebView so we can send immediate commands back to
                // the javascript bridge when the user initiates a seek via clicking a lyric.
                // 保存 WebView 引用，以便用户点击歌词时能立即发送命令到 JavaScript
                val webViewRef = this

                // ==================== JavaScript 接口注册 ====================
                // 注册 AMLLInterface 对象为 window.Android，供前端调用
                addJavascriptInterface(
                    AMLLInterface(
                        debugSource,
                        instanceId,
                        onLineSeekState.value,
                        webSocketClient = webSocketClient, // 传递 WebSocket 客户端引用
                        onSeekRequested = { seekTime ->
                            // schedule a UI-thread action so that the webview can immediately
                            // acknowledge the seek and prevent the "lyrics running around" effect.
                            // 在 UI 线程上执行跳转，防止歌词乱跑
                            webViewRef.post {
                                // tell the JS player we are seeking so it can suspend auto-scroll
                                // 告诉 JS 播放器正在跳转，暂停自动滚动
                                webViewRef.evaluateJavascript(
                                    "window.callPlayer && window.callPlayer('setIsSeeking', true);",
                                    null
                                )

                                // update the webview time to the target position right away. this
                                // reduces the window where the old time would cause the view to
                                // scroll back to the previous line before the new position arrives
                                // 立即更新 WebView 时间到新位置，减少旧时间导致的回滚
                                webViewRef.evaluateJavascript(
                                    "window.updateTime && window.updateTime($seekTime);",
                                    null
                                )
                            }
                        },
                        isPlayingProvider = { isPlayingState.value }
                    ),
                    "Android"  // 在前端通过 window.Android 访问
                )
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] JavascriptInterface added as Android")

                // ==================== 点击事件监听 ====================
                // 设置 WebView 点击监听器（用于处理整个歌词区域的点击）
                setOnClickListener {
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] WebView onClick listener fired")
                    onLyricsClickState.value?.invoke()
                }

                // ==================== 加载本地 HTML 资源 ====================
                // 从 assets 目录加载 AMLL 前端页面
                loadUrl("file:///android_asset/amll/index.html")

                // 在消息队列中发布延迟任务，获取 WebView 的实际尺寸
                post {
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] WebView size after layout: width=$width, height=$height, measuredWidth=$measuredWidth, measuredHeight=$measuredHeight")
                }

                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] WebView initialized with URL: file:///android_asset/amll/index.html")
            }
        },
        // ==================== WebView 更新逻辑 ====================
        // update 回调：当 Compose 状态变化时触发，用于同步最新状态到 WebView
        update = { view ->
            // 如果页面还未就绪，跳过本次更新
            if (!isPageReady) {
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge skipped: page not ready")
                return@AndroidView
            }

            Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Update callback - WebView actual size: width=${view.width}, height=${view.height}, measuredWidth=${view.measuredWidth}, measuredHeight=${view.measuredHeight}")

            // ==================== 更新时间同步 ====================
            // 立即更新时间，减少歌词行激活延迟
            Timber.d("[AMLLLyrics] [WebView] [$debugSource#$instanceId] Bridge call: updateTime($currentTime)")
            view.evaluateJavascript("window.updateTime && window.updateTime($currentTime);", null)
            
            // 同时通过 WebSocket 发送到外部服务
            sendPlaybackStatusToWebSocket(currentTime, isPlayingState.value)

            // ==================== 渲染模式配置 ====================
            // 根据 renderMode 设置渲染模式（dom 或 dom-lite）
            val modeValue = if (renderMode == AMLLRenderMode.DOM) "dom" else "dom-lite"
            if (lastModeValue != modeValue) {
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: setRenderMode($modeValue)")
                view.evaluateJavascript("window.setRenderMode && window.setRenderMode('$modeValue');", null)
                lastModeValue = modeValue
            }

            // ==================== 动画 FPS 设置 ====================
            // 使用用户自定义的 FPS 值，不再根据渲染模式强制限制
            val fpsValue = AppSettings.getAmllAnimationFps(view.context).coerceIn(15, 240)

            // ==================== 背景效果配置 ====================
            // 根据渲染模式构建不同的背景效果配置
            val backgroundProfile = if (renderMode == AMLLRenderMode.DOM) {
                // 标准 DOM 模式：高质量渲染
                """{"renderer":"pixi","fps":$fpsValue,"flowSpeed":2.35,"renderScale":0.9,"staticMode":false,"lowFreqVolume":1.0}"""
            } else {
                // DOM_LITE 模式：性能优化版
                """{"renderer":"pixi","fps":$fpsValue,"flowSpeed":1.4,"renderScale":0.65,"staticMode":false,"lowFreqVolume":1.0}"""
            }
            if (lastBackgroundProfileValue != backgroundProfile) {
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: configureBackgroundEffect(profile=$backgroundProfile)")
                view.evaluateJavascript(
                    "window.configureBackgroundEffect && window.configureBackgroundEffect($backgroundProfile);",
                    null
                )
                lastBackgroundProfileValue = backgroundProfile
            }

            // ==================== 歌词动画运动配置 ====================
            // 构建歌词动画的运动配置文件（弹簧、缩放、模糊等效果）
            val motionConfig = """{
                "enableSpring":${AppSettings.isAmllAnimationSpringEnabled(view.context)},
                "enableScale":${AppSettings.isAmllAnimationScaleEnabled(view.context)},
                "enableBlur":${AppSettings.isAmllAnimationBlurEnabled(view.context)},
                "hidePassedLines":${AppSettings.isAmllAnimationHidePassedLinesEnabled(view.context)},
                "wordFadeWidth":${AppSettings.getAmllAnimationWordFadeWidth(view.context)},
                "fps":$fpsValue
            }""".trimIndent().replace("\n", "")

            if (lastMotionConfigValue != motionConfig) {
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: configureLyricMotion(profile=$motionConfig)")
                view.evaluateJavascript("window.configureLyricMotion && window.configureLyricMotion($motionConfig);", null)
                lastMotionConfigValue = motionConfig
            }

            // ==================== 歌词样式配置 ====================
            // 歌词播放器实现（DOM / DOM Lite / Canvas）
            val lyricPlayerImpl = AppSettings.getAmllLyricPlayerImplementation(view.context)
            val renderModeValue = when (lyricPlayerImpl) {
                "dom" -> "dom"
                "dom-slim" -> "dom-lite"
                "canvas" -> "canvas"
                else -> "dom"
            }
            if (lastModeValue != renderModeValue) {
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: setLyricPlayerImplementation($renderModeValue)")
                view.evaluateJavascript("window.setLyricPlayerImplementation && window.setLyricPlayerImplementation('$renderModeValue');", null)
                lastModeValue = renderModeValue
            }

            // 歌词字体大小预设
            val lyricSizePreset = AppSettings.getAmllLyricSizePreset(view.context)
            if (lastLyricSizePreset != lyricSizePreset) {
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: setLyricSizePreset($lyricSizePreset)")
                view.evaluateJavascript("window.setLyricSizePreset && window.setLyricSizePreset('$lyricSizePreset');", null)
                lastLyricSizePreset = lyricSizePreset
            }

            // 翻译歌词开关
            val enableTranslationLine = AppSettings.isAmllTranslationLineEnabled(view.context)
            Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: setEnableTranslationLine($enableTranslationLine)")
            view.evaluateJavascript("window.setEnableTranslationLine && window.setEnableTranslationLine($enableTranslationLine);", null)

            // 音译歌词开关
            val enableRomanLine = AppSettings.isAmllRomanLineEnabled(view.context)
            Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: setEnableRomanLine($enableRomanLine)")
            view.evaluateJavascript("window.setEnableRomanLine && window.setEnableRomanLine($enableRomanLine);", null)

            // 交换音译和翻译位置
            val enableSwapTransRoman = AppSettings.isAmllSwapTransRomanLineEnabled(view.context)
            Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: setEnableSwapTransRomanLine($enableSwapTransRoman)")
            view.evaluateJavascript("window.setEnableSwapTransRomanLine && window.setEnableSwapTransRomanLine($enableSwapTransRoman);", null)

            // 提前歌词行时序
            val enableAdvanceDynamicTime = AppSettings.isAmllAdvanceDynamicLyricTimeEnabled(view.context)
            if (lastEnableAdvanceDynamicTime != enableAdvanceDynamicTime) {
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: setAdvanceLyricDynamicLyricTime($enableAdvanceDynamicTime)")
                view.evaluateJavascript("window.setAdvanceLyricDynamicLyricTime && window.setAdvanceLyricDynamicLyricTime($enableAdvanceDynamicTime);", null)
                lastEnableAdvanceDynamicTime = enableAdvanceDynamicTime
            }
            
            // 字体字重 - 通过 CSS 应用
            val fontWeight = AppSettings.getAmllFontWeight(view.context)
            if (fontWeight > 0) {
              Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: applyFontWeight($fontWeight)")
              view.evaluateJavascript(
                "document.documentElement.style.setProperty('--amll-font-weight', '$fontWeight');",
                null
              )
            }
            
            // 字符间距 - 通过 CSS 应用
            val letterSpacing = AppSettings.getAmllLetterSpacing(view.context)
            if (!letterSpacing.isNullOrBlank()) {
              val escapedLetterSpacing = escapeJsString(letterSpacing)
              Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: applyLetterSpacing('$escapedLetterSpacing')")
              view.evaluateJavascript(
                "document.documentElement.style.setProperty('--amll-letter-spacing', '$escapedLetterSpacing');",
                null
              )
            }

            // ==================== 歌词背景配置 ====================
            val backgroundRenderer = AppSettings.getAmllBackgroundRenderer(view.context)
            val cssBackgroundProperty = AppSettings.getAmllCssBackgroundProperty(view.context)
            val backgroundFps = AppSettings.getAmllBackgroundFps(view.context)
            val backgroundRenderScale = AppSettings.getAmllBackgroundRenderScale(view.context)
            val enableBackgroundStaticMode = AppSettings.isAmllBackgroundStaticModeEnabled(view.context)

            // 根据渲染器类型构建背景配置
            val backgroundConfig = when (backgroundRenderer) {
                "css-bg" -> """{"renderer":"css-bg","cssProperty":"$cssBackgroundProperty"}"""
                "pixi" -> """{"renderer":"pixi","fps":$backgroundFps,"renderScale":$backgroundRenderScale,"staticMode":$enableBackgroundStaticMode}"""
                "mesh" -> """{"renderer":"mesh","fps":$backgroundFps,"renderScale":$backgroundRenderScale,"staticMode":$enableBackgroundStaticMode}"""
                else -> """{"renderer":"mesh","fps":$backgroundFps,"renderScale":$backgroundRenderScale,"staticMode":$enableBackgroundStaticMode}"""
            }

            Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: configureLyricBackground(config=$backgroundConfig)")
            view.evaluateJavascript("window.configureLyricBackground && window.configureLyricBackground($backgroundConfig);", null)

            // ==================== 歌词数据更新 ====================
            // 只在 lyrics 对象引用改变时才重新构建 JSON（避免每秒都构建）
            if (lyrics !== lastLyrics) {
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Lyrics changed: ${lyrics?.lines?.size ?: 0} lines")
                if (lyrics != null && lyrics.lines.isNotEmpty()) {
                    // 构建歌词 JSON 数据结构
                    val lyricsJson = buildLyricsJson(lyrics)
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: updateLyrics(lines=${lyrics.lines.size})")
                    // 添加详细日志，显示前几行歌词内容
                    lyrics.lines.take(3).forEachIndexed { idx, line ->
                        Timber.d("[AMLLLyrics]   Line $idx: text='${line.text}', words=${line.words.size}, isBG=${line.isBG}")
                    }
                    view.evaluateJavascript("window.updateLyrics && window.updateLyrics($lyricsJson);", null)
                    lastLyricsPayload = lyricsJson
                                
                    // 通过 WebSocket 发送歌词更新（V2 协议）
                    if (isWebSocketConnected) {
                        try {
                            // ⭐ 关键修复：使用原始 TTML 字符串而不是结构化 JSON
                            // V2 协议格式：{"type":"state","value":{"update":"setLyric","format":"ttml","data":"<TTML 原始内容>"}}
                            // 注意：data 字段应该是原始 TTML 字符串，不是 Base64 编码，也不是给 WebView 的结构化 JSON
                            val ttmlContent = lyrics.rawTtml
                            if (!ttmlContent.isNullOrBlank()) {
                                val lyricMessage = com.amll.droidmate.websocket.WsProtocolV2Helper.createTTMLLyricUpdate(ttmlContent)
                                webSocketClient.send(lyricMessage)
                                Timber.d("[AMLLLyrics] [WebSocket] [$debugSource#$instanceId] 已通过 WebSocket 发送歌词 (TTML format, size=${ttmlContent.length} chars)")
                            } else {
                                Timber.w("[AMLLLyrics] [WebSocket] [$debugSource#$instanceId] ⚠️ rawTtml is null/empty, skipping WebSocket send")
                            }
                        } catch (e: Exception) {
                            Timber.e("[AMLLLyrics] [WebSocket] [$debugSource#$instanceId] 通过 WebSocket 发送歌词失败", e)
                        }
                    }
                } else {
                    // 如果 lyrics 为空或 null，注入测试歌词以便调试
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] No lyrics provided, injecting test lyrics")
                    val testLyricsJson = """{"metadata":{"title":"Test","artist":"AMLL"},"lines":[{"startTime":0,"endTime":3000,"text":"测试歌词","translatedLyric":"","romanLyric":"","words":[{"word":"测试","startTime":0,"endTime":1500},{"word":"歌词","startTime":1500,"endTime":3000}],"isBG":false,"isDuet":false},{"startTime":3000,"endTime":6000,"text":"第二行歌词","translatedLyric":"","romanLyric":"","words":[{"word":"第二行","startTime":3000,"endTime":4500},{"word":"歌词","startTime":4500,"endTime":6000}],"isBG":false,"isDuet":false}]}"""
                    view.evaluateJavascript("window.updateLyrics && window.updateLyrics($testLyricsJson);", null)
                    lastLyricsPayload = testLyricsJson
                }
                lastLyrics = lyrics
            } else {
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Lyrics reference unchanged")
            }

            // ==================== 专辑封面更新 ====================
            // 专辑图更新：添加数据验证和去重
            if (lastAlbumArtUri != albumArtUri) {
                // 验证专辑图 URI 是否有效
                val isValidAlbumArt = !albumArtUri.isNullOrBlank() && 
                                      albumArtUri.length > 20 // 有效的 data URL 应该有一定长度
                
                if (isValidAlbumArt) {
                    // 将 file:// URI 转换为 base64 data URL，因为 WebView 的 Fetch API 不支持 file:// 协议
                    val albumArtDataUrl = convertFileUriToDataUrl(view.context, albumArtUri)
                    
                    // 再次检查转换后的数据是否有效
                    if (!albumArtDataUrl.isNullOrBlank() && albumArtDataUrl.length > 100) {
                        val escapedAlbumUri = escapeJsString(albumArtDataUrl)
                        Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: updateAlbumArt(uri=present, ${albumArtDataUrl.length} chars)")
                        view.evaluateJavascript("window.updateAlbumArt && window.updateAlbumArt(\"$escapedAlbumUri\");", null)
                        lastAlbumArtUri = albumArtUri
                    } else {
                        Timber.w("[AMLLLyrics] [$debugSource#$instanceId] Album art conversion failed or invalid data URL")
                    }
                } else {
                    // 如果专辑图为空或无效，不发送到前端，避免污染 BackgroundRender 状态
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Album art is empty/invalid, skipping update to avoid dirty state")
                    // 但仍然更新 lastAlbumArtUri，避免重复尝试发送无效数据
                    lastAlbumArtUri = albumArtUri
                }
            }

            // ==================== 字体配置应用 ====================
            // 获取用户配置的字体家族名称
            val configuredFontFamily = AppSettings.getAmllFontFamily(view.context)
            // 获取已安装的字体文件列表
            val fontFiles = AppSettings.getAmllFontFiles(view.context)
                .filter { it.absolutePath.isNotBlank() }
                .mapNotNull { item ->
                    val file = File(item.absolutePath)
                    if (!file.exists()) return@mapNotNull null
                    FontWebEntry(
                        id = item.id,
                        sortKey = item.fontFamilyName,
                        familyName = buildRuntimeFontFamilyName(item.fontFamilyName, item.id),
                        uri = file.toURI().toString()
                    )
                }

            // 获取启用的字体 ID 列表
            val enabledIds = AppSettings.getEnabledAmllFontFileIds(view.context)
            // 解析用户偏好的字体顺序
            val preferredOrder = parsePreferredFontOrder(configuredFontFamily)
            // 根据偏好排序启用的字体
            val enabledFamilies = fontFiles
                .filter { enabledIds.contains(it.id) }
                .sortedWith(
                    compareBy<FontWebEntry> { fontSortPriority(it.sortKey, preferredOrder) }
                        .thenBy { it.sortKey.lowercase() }
                        .thenBy { it.id }
                )
                .map { it.familyName }
                .distinct()

            // 构建最终使用的字体家族栈
            val effectiveFamily = if (enabledFamilies.isNotEmpty()) {
                val enabledStack = enabledFamilies.joinToString(", ") { "\"$it\"" }
                "$enabledStack, $configuredFontFamily"
            } else {
                configuredFontFamily
            }

            // 构建字体配置签名（用于检测变化）
            val fontSignature = buildString {
                append(effectiveFamily)
                append("|")
                append(fontFiles.joinToString(";") { "${it.id}:${it.familyName}:${it.uri}" })
                append("|")
                append(enabledFamilies.joinToString(","))
            }

            // 如果字体配置发生变化，应用新的字体设置
            if (lastFontConfigSignature != fontSignature) {
                val script = buildApplyFontScript(effectiveFamily, fontFiles)
                Timber.d(
                    "[AMLLLyrics] [$debugSource#$instanceId] Bridge call: applyFontSettings(enabled=${enabledFamilies.size}, files=${fontFiles.size})"
                )
                view.evaluateJavascript(script, null)
                lastFontConfigSignature = fontSignature
            }
        },
        // ==================== WebView 销毁回调 ====================
        // 当组件被销毁时，销毁 WebView 以避免内存泄漏
        onRelease = { view ->
            // 当组件被销毁时，销毁 WebView 以避免内存泄漏
            Timber.i("[AMLLLyrics] [$debugSource] Destroying AMLL WebView")
            view.stopLoading()      // 停止加载
            view.clearHistory()     // 清除历史记录
            view.clearCache(true)   // 清除缓存
            view.removeJavascriptInterface("Android")  // 移除 JS 接口
            view.destroy()          // 销毁 WebView
        }
    )
}

private data class FontWebEntry(
    val id: String,
    val sortKey: String,
    val familyName: String,
    val uri: String
)

private fun buildRuntimeFontFamilyName(baseFamilyName: String, fontId: String): String {
    // 直接使用原始字体名称，以便与 CSS 中的 font-family 匹配
    return baseFamilyName
}

private fun parsePreferredFontOrder(configuredFontFamily: String): List<String> {
    return configuredFontFamily
        .split(',')
        .map { normalizeFontToken(it) }
        .filter { it.isNotBlank() }
}

private fun fontSortPriority(sortKey: String, preferredOrder: List<String>): Int {
    if (preferredOrder.isEmpty()) return Int.MAX_VALUE
    val normalizedSortKey = normalizeFontToken(sortKey)
    for (index in preferredOrder.indices) {
        val preferred = preferredOrder[index]
        if (preferred.isBlank()) continue
        if (normalizedSortKey.contains(preferred) || preferred.contains(normalizedSortKey)) {
            return index
        }
    }
    return Int.MAX_VALUE
}

private fun normalizeFontToken(value: String): String {
    return value
        .lowercase()
        .replace(Regex("[^a-z0-9]"), "")
}

private fun buildApplyFontScript(effectiveFamily: String, files: List<FontWebEntry>): String {
    // 将字体家族名称转换为 JSON 安全的字符串
    val familyJson = "\"${escapeJsStringForJson(effectiveFamily)}\""
    
    // 构建文件数组的 JSON 表示
    val filesArrayJson = if (files.isEmpty()) {
        "[]"
    } else {
        val filesEntries = files.joinToString(",") { entry ->
            "{id:\"${escapeJsStringForJson(entry.id)}\",familyName:\"${escapeJsStringForJson(entry.familyName)}\",uri:\"${escapeJsStringForJson(entry.uri)}\"}"
        }
        "[$filesEntries]"
    }

    return buildString {
        append("(function(){")
        append("var effectiveFamily=$familyJson;")
        append("var files=$filesArrayJson;")
        append("var styleId='amll-dynamic-font-face-style';")
        append("var styleNode=document.getElementById(styleId);")
        append("if(!styleNode){styleNode=document.createElement('style');styleNode.id=styleId;document.head.appendChild(styleNode);}")
        append("var css='';")
        append("for(var i=0;i<files.length;i+=1){var item=files[i];if(!item||!item.familyName||!item.uri)continue;if(item.uri.indexOf('data:image/svg+xml')===0)continue;css+='@font-face{font-family:\"'+item.familyName+'\";src:url(\"'+item.uri+'\");font-display:swap;}';}")
        append("styleNode.textContent=css;")
        append("document.documentElement.style.setProperty('--amll-user-font-family',effectiveFamily);")
        append("document.documentElement.style.setProperty('--amll-lp-font-family','var(--amll-user-font-family)');")
        append("var players=document.querySelectorAll('.amll-lyric-player');")
        append("for(var j=0;j<players.length;j+=1){players[j].style.fontFamily='var(--amll-lp-font-family)';}")
        append("})();")
    }
}

private fun escapeJsStringForJson(value: String): String {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("'", "\\'")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}

private fun escapeJsString(value: String): String {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("'", "\\'")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}

/**
 * 清洗背景歌词文本：移除第一个 "(" 和最后一个 ")"
 * 
 * 这个函数用于在生成 JSON 时处理背景歌词，移除括号但保留其他内容。
 * TTML 原始数据中会保留完整的括号格式。
 * 
 * @param text 原始文本
 * @return 清洗后的文本（移除了首尾括号）
 */
private fun cleanBackgroundText(text: String): String {
    // 背景歌词同样遵循可见空格语义：禁止 trim。
    // 仅去除文本中第一个 "(" 和最后一个 ")"，不改动其它内容。
    val firstParenIndex = text.indexOf('(')
    val lastParenIndex = text.lastIndexOf(')')
    
    if (firstParenIndex != -1 && lastParenIndex != -1 && lastParenIndex > firstParenIndex) {
        // 移除第一个 "(" 和最后一个 ")"
        return text.substring(0, firstParenIndex) +
               text.substring(firstParenIndex + 1, lastParenIndex) +
               text.substring(lastParenIndex + 1)
    }
    
    return text
}

private fun buildLyricsJson(lyrics: TTMLLyrics): String {
    val bgLines = lyrics.lines.filter { it.isBG }
    val bgWithTranslation = bgLines.count { !it.translation.isNullOrBlank() }
    val bgWithRoman = bgLines.count { !it.transliteration.isNullOrBlank() }
    val sampleBg = bgLines.firstOrNull()
    Timber.d("[BG-LYRICS-DEBUG] buildLyricsJson summary: total=${lyrics.lines.size}, bg=${bgLines.size}, bgWithTrans=$bgWithTranslation, bgWithRoman=$bgWithRoman, sampleBg='${sampleBg?.text ?: ""}', sampleTrans='${sampleBg?.translation ?: ""}'")

    // 调试日志：限制在 10 行以内，超出的降级为 v 级别
    var debugCount = 0
    
    val linesJson = lyrics.lines.joinToString(",") { line ->
        // 背景歌词清洗：移除第一个 "(" 和最后一个 ")"
        val cleanedText = if (line.isBG) {
            cleanBackgroundText(line.text)
        } else {
            line.text
        }
        
        val text = cleanedText.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
        val translation = line.translation?.replace("\\", "\\\\")?.replace("\"", "\\\"") ?: ""
        val transliteration = line.transliteration?.replace("\\", "\\\\")?.replace("\"", "\\\"") ?: ""
        
        // 构建 words 数组
        val wordsJson = if (line.words.isNotEmpty()) {
            line.words.joinToString(",") { word ->
                // 背景歌词的单词也需要清洗
                val wordText = if (line.isBG) {
                    cleanBackgroundText(word.word).replace("\\", "\\\\").replace("\"", "\\\"")
                } else {
                    word.word.replace("\\", "\\\\").replace("\"", "\\\"")
                }
                """{"word":"$wordText","startTime":${word.startTime},"endTime":${word.endTime}}"""
            }
        } else {
            // 如果没有逐词信息，则使用整行文本作为单词
            val wordText = text.replace("\"", "\\\"")
            """{"word":"$wordText","startTime":${line.startTime},"endTime":${line.endTime}}"""
        }
        
        // 调试日志：只记录前 5 行
        if (line.words.isNotEmpty()) {
            if (debugCount < 5) {
                Timber.d("[AMLLLyrics] Building JSON for line: '${line.text}' with ${line.words.size} words")
                debugCount++
            }
        }
        
        // 调试背景歌词的数据传递
        if (line.isBG) {
            Timber.d("[BG-LYRICS-DEBUG] JSON for BG line: text='$text' translation='$translation' roman='$transliteration' isBG=${line.isBG}")
        }
        
        """{
            "startTime":${line.startTime},
            "endTime":${line.endTime},
            "text":"$text",
            "translatedLyric":"$translation",
            "romanLyric":"$transliteration",
            "words":[$wordsJson],
            "isBG":${line.isBG},
            "isDuet":${line.isDuet}
        }"""
    }

    val title = lyrics.metadata.title.replace("\\", "\\\\").replace("\"", "\\\"")
    val artist = lyrics.metadata.artist.replace("\\", "\\\\").replace("\"", "\\\"")

    return """{"metadata":{"title":"$title","artist":"$artist"},"lines":[$linesJson]}"""
}

/**
 * AMLL JavaScript 接口类
 * 
 * 这个类通过 JavascriptInterface 暴露给 WebView 中的 JavaScript 调用，
 * 实现了前端页面与 Android 原生代码之间的双向通信。
 * 
 * **主要功能**：
 * 1. 日志转发：将前端日志转发到 Timber
 * 2. 歌词点击处理：响应用户点击歌词行的操作
 * 3. 播放状态查询：提供当前播放状态给前端
 * 4. WebSocket 消息桥接：将前端消息转发到 WebSocket 服务器
 * 
 * @param debugSource 调试来源标签
 * @param instanceId 实例 ID（用于区分多个视图）
 * @param onLineSeek 歌词行跳转回调
 * @param webSocketClient WebSocket 客户端引用（可选）
 * @param onSeekRequested 跳转请求回调
 * @param isPlayingProvider 播放状态提供者函数
 */
class AMLLInterface(
    private val debugSource: String,
    private val instanceId: Int,
    private val onLineSeek: ((Long) -> Unit)? = null,
    private val webSocketClient: com.amll.droidmate.websocket.AMLLWebSocketClient? = null, // WebSocket 客户端引用
    private val onSeekRequested: ((Long) -> Unit)? = null,
    private val isPlayingProvider: () -> Boolean = { true }
) {
    /**
     * 日志输出接口（供 JavaScript 调用）
     * 
     * @JavascriptInterface 注解使得这个方法可以被 WebView 中的 JavaScript 直接调用。
     * 
     * @param message 日志消息内容
     * @param level 日志级别（debug/info/warn/error），默认为 "debug"
     */
    @JavascriptInterface
    fun log(message: String, level: String = "debug") {
        val levelUpper = level.uppercase()
        // 根据日志级别分别转发到 Timber
        when (levelUpper) {
            "DEBUG" -> Timber.d("[AMLLLyrics] [WebView] JS: $message")
            "INFO" -> Timber.i("[AMLLLyrics] [WebView] JS: $message")
            "WARN" -> Timber.w("[AMLLLyrics] [WebView] JS: $message")
            "ERROR" -> Timber.e("[AMLLLyrics] [WebView] JS: $message")
            else -> Timber.d("[AMLLLyrics] [WebView] JS: $message")
        }
    }

    /**
     * 歌词行点击处理接口（供 JavaScript 调用）
     * 
     * 当用户点击歌词中的某一行时，JavaScript 会调用这个方法。
     * 
     * @param lineIndex 被点击的歌词行索引
     * @param startTime 该歌词行的开始时间（毫秒）
     */
    @JavascriptInterface
    fun onLineClick(lineIndex: Int, startTime: Long) {
        Timber.i("[AMLLLyrics] [$debugSource#$instanceId] User clicked lyric line: index=$lineIndex, startTime=$startTime, callbackPresent=${onLineSeek != null}")
        // 触发跳转请求
        onSeekRequested?.invoke(startTime)
        // 同时调用外部回调
        onLineSeek?.invoke(startTime)
    }

    /**
     * 查询播放状态接口（供 JavaScript 调用）
     * 
     * JavaScript 可以通过 window.Android.isPlaying() 查询当前是否正在播放。
     * 
     * @return 当前播放状态（true=播放中，false=已暂停）
     */
    @JavascriptInterface
    fun isPlaying(): Boolean {
        return isPlayingProvider()
    }
    
    /**
     * WebSocket 消息发送接口（供 JavaScript 调用）
     * 
     * 当 WebSocket 桥接对象需要发送消息到外部 AMLL 服务时调用此方法。
     * 
     * **支持的消息类型**：
     * - ping/pong：心跳检测
     * - seek：跳转请求
     * - 其他自定义消息
     * 
     * @param message JSON 格式的消息字符串
     */
    @JavascriptInterface
    fun sendWebSocketMessage(message: String) {
        // 通过 WebSocket 发送到外部 AMLL 服务
        Timber.d("[AMLLLyrics] [$debugSource#$instanceId] 发送 WebSocket 消息：$message")
        
        try {
            // 解析消息并转发到 WebSocket 客户端
            val jsonObject = org.json.JSONObject(message)
            val type = jsonObject.optString("type")
            
            when (type) {
                "ping" -> {
                    // 响应 ping 消息（心跳检测）
                    webSocketClient?.send("{\"type\":\"pong\"}")
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] 已响应 ping 消息")
                }
                "seek" -> {
                    // 处理 seek 命令（如果需要）
                    val time = jsonObject.optLong("time", 0L)
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] 收到 seek 命令：time=$time")
                }
                else -> {
                    // 其他类型的消息直接转发
                    webSocketClient?.send(message)
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] 已转发 WebSocket 消息：type=$type")
                }
            }
        } catch (e: Exception) {
            Timber.e("[WebView] [$debugSource#$instanceId] 发送 WebSocket 消息失败", e)
        }
    }
}

/**
 * 将 file:// URI 转换为 base64 data URL，以便 WebView 能够加载本地图片
 * 
 * **为什么需要转换？**
 * - WebView 的 Fetch API 不支持 file:// 协议
 * - data URL 可以直接在 HTML 中使用，无需额外请求
 * - Base64 编码确保二进制数据可以安全传输
 * 
 * **支持的 URI 类型**：
 * - file:// 开头的本地文件路径
 * - content:// 开头的内容提供者 URI
 * - 其他类型直接返回（可能是 data URL）
 * 
 * @param context Android Context
 * @param uriString 要转换的 URI 字符串
 * @return 转换后的 data URL（格式：data:image/jpeg;base64,...），失败返回 null
 */
private fun convertFileUriToDataUrl(context: Context, uriString: String?): String? {
    // URI 为空时直接返回 null
    if (uriString.isNullOrBlank()) {
        return null
    }
    
    return try {
        // 根据 URI 类型选择不同的输入流获取方式
        val inputStream = when {
            uriString.startsWith("file://") -> {
                // file:// URI：直接从文件系统读取
                val path = uriString.removePrefix("file://")
                File(path).inputStream()
            }
            uriString.startsWith("content://") -> {
                // content:// URI：通过 ContentResolver 读取
                val uri = android.net.Uri.parse(uriString)
                context.contentResolver.openInputStream(uri)
            }
            else -> {
                // 其他类型（可能是 data URL）直接返回原始字符串
                Timber.w("[AMLLLyrics] [WebView] Unsupported URI scheme: $uriString")
                return uriString // 直接返回原始字符串（可能是 data URL）
            }
        }
        // 使用 use 自动关闭输入流，避免资源泄漏
        inputStream?.use { stream ->
            // 读取所有字节
            val bytes = stream.readBytes()
            // 获取 MIME 类型（默认为 image/jpeg）
            val mimeType = getMimeType(uriString) ?: "image/jpeg"
            // Base64 编码（NO_WRAP 选项不添加换行符）
            val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
            // 构建 data URL：data:image/jpeg;base64,<base64 数据>
            "data:$mimeType;base64,$base64"
        }
    } catch (e: Exception) {
        Timber.e("[AMLLLyrics] [WebView] Failed to convert file URI to data URL: $uriString", e)
        null
    }
}

/**
 * 根据文件扩展名获取 MIME 类型
 * 
 * **用途**：
 * - 用于 data URL 的 MIME 类型标识
 * - 帮助浏览器正确识别和渲染图片格式
 * 
 * @param uriString 文件 URI 或路径
 * @return MIME 类型字符串，未知类型返回 null
 */
private fun getMimeType(uriString: String): String? {
    return when {
        uriString.endsWith(".png", ignoreCase = true) -> "image/png"
        uriString.endsWith(".jpg", ignoreCase = true) || uriString.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
        uriString.endsWith(".gif", ignoreCase = true) -> "image/gif"
        uriString.endsWith(".webp", ignoreCase = true) -> "image/webp"
        else -> "image/jpeg" // 默认为 JPEG
    }
}

/**
 * 清除 WebView 的所有缓存数据
 * 
 * **清除的内容**：
 * 1. 内存缓存（HTTP 缓存、图片缓存等）
 * 2. DOM 存储（localStorage、sessionStorage）
 * 
 * **为什么需要清除？**
 * - 确保每次加载最新的 HTML 和 JS 文件
 * - 避免旧版本代码导致的兼容性问题
 * - 清理可能的脏数据
 */
private fun WebView.clearAllCache() {
    try {
        // 清除内存缓存（包括 HTTP 缓存、图片缓存等）
        clearCache(true)
        
        // 清除 DOM 存储（localStorage、sessionStorage 等）
        // 先禁用再启用，强制重置 DOM 存储
        settings.domStorageEnabled = false
        settings.domStorageEnabled = true
        
        Timber.d("[AMLLLyrics] WebView cache cleared")
    } catch (e: Exception) {
        Timber.d("[AMLLLyrics] Failed to clear WebView cache: ${e.message}")
    }
}
