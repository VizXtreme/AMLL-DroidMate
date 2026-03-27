package com.amll.droidmate.websocket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import timber.log.Timber
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory
import com.amll.droidmate.domain.model.TTMLLyrics

/**
 * AMLL WebSocket 客户端（单例模式）
 * 
 * 在 Android 端作为 WebSocket 客户端连接到外部 AMLL 服务
 * 实现双向通信：发送播放状态，接收控制命令
 * 
 * 使用方式：
 * val client = AMLLWebSocketClient.getInstance()
 */
class AMLLWebSocketClient private constructor(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {
    
    companion object {
        @Volatile
        private var instance: AMLLWebSocketClient? = null
        
        /**
         * 获取单例实例
         */
        fun getInstance(): AMLLWebSocketClient {
            return instance ?: synchronized(this) {
                instance ?: AMLLWebSocketClient().also { instance = it }
            }
        }
        
        /**
         * 重置单例（用于测试或重新初始化）
         */
        fun resetInstance() {
            instance?.destroy()
            instance = null
        }
    }
    
    interface Listener {
        fun onConnected()
        fun onDisconnected()
        fun onMessageReceived(message: String)
        fun onError(error: Throwable)
        
        /**
         * 当 WebSocket 连接成功时，返回当前播放状态用于同步
         * @return 包含歌曲信息、进度、状态的 PlayState 对象，如果无播放内容则返回 null
         */
        fun getCurrentPlayState(): PlayState? = null
    }
    
    /**
     * 播放状态数据类
     */
    data class PlayState(
        val musicId: String,
        val musicName: String,
        val albumName: String,
        val artistName: String,
        val duration: Long,
        val progress: Long,
        val isPlaying: Boolean,
        val ttmlLyric: String? = null
    )
    
    // 支持多个监听器
    private val listeners = mutableListOf<Listener>()
    
    /**
     * 添加消息监听器
     */
    fun addListener(listener: Listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }
    
    /**
     * 移除监听器
     */
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }
    
    /**
     * 创建一个简单的状态监听器（仅用于 UI 显示连接状态）
     * @param onStateChanged 状态变化回调 (true=已连接，false=未连接)
     * @param onErrorCallback 错误回调（可选）
     */
    fun createStateListener(
        onStateChanged: (Boolean) -> Unit,
        onErrorCallback: ((Throwable) -> Unit)? = null
    ): Listener {
        return object : Listener {
            override fun onConnected() {
                onStateChanged(true)
            }
            
            override fun onDisconnected() {
                onStateChanged(false)
            }
            
            override fun onMessageReceived(message: String) {}
            
            override fun onError(error: Throwable) {
                onStateChanged(false)
                onErrorCallback?.invoke(error)
            }
            
            override fun getCurrentPlayState(): PlayState? = null
        }
    }
    
    /**
     * 创建完整功能的监听器（支持歌曲信息同步和命令处理）
     * @param debugSource 调试标签
     * @param musicId 歌曲 ID
     * @param musicName 歌曲名称
     * @param albumName 专辑名称
     * @param artistName 艺术家名称
     * @param duration 歌曲时长
     * @param currentTime 当前进度
     * @param isPlaying 是否正在播放
     * @param lyrics 歌词数据
     * @param onConnectedCallback 连接成功后的额外操作（可选）
     * @param onCommandReceived 收到命令时的处理（可选）
     * @param onErrorCallback 错误回调（可选）
     */
    fun createFullFeatureListener(
        debugSource: String,
        musicId: String,
        musicName: String,
        albumName: String,
        artistName: String,
        duration: Long,
        currentTime: Long,
        isPlaying: Boolean,
        lyrics: TTMLLyrics?,
        onConnectedCallback: (() -> Unit)? = null,
        onCommandReceived: ((String, kotlinx.serialization.json.JsonObject?) -> Unit)? = null,
        onErrorCallback: ((Throwable) -> Unit)? = null
    ): Listener {
        return object : Listener {
            override fun onConnected() {
                Timber.i("[$debugSource] WebSocket 已连接")
                Timber.d("[$debugSource] 当前歌曲信息：musicId=$musicId, musicName=$musicName, artist=$artistName")
                
                // 执行额外的连接后操作
                onConnectedCallback?.invoke()
            }
            
            override fun onDisconnected() {
                Timber.w("[$debugSource] WebSocket 已断开")
            }
            
            override fun onMessageReceived(message: String) {
                Timber.d("[$debugSource] 收到 WebSocket 消息：$message")
                
                // 解析并处理来自服务器的命令
                try {
                    val json = kotlinx.serialization.json.Json.parseToJsonElement(message)
                    val type = json.jsonObject["type"]?.jsonPrimitive?.content
                    
                    if (type == "command") {
                        val valueObj = json.jsonObject["value"]?.jsonObject
                        val command = valueObj?.get("command")?.jsonPrimitive?.content
                        
                        Timber.i("[$debugSource] 收到命令：$command")
                        Timber.d("[$debugSource] onCommandReceived 引用：$onCommandReceived")
                        
                        if (onCommandReceived != null) {
                            Timber.d("[$debugSource] 准备调用 onCommandReceived，命令：$command")
                            onCommandReceived.invoke(command ?: "unknown", valueObj)
                            Timber.d("[$debugSource] onCommandReceived 执行完成")
                        } else {
                            Timber.w("[$debugSource] onCommandReceived 为 null，跳过命令处理")
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "[$debugSource] 解析命令失败")
                }
            }
            
            override fun onError(error: Throwable) {
                Timber.e(error, "[$debugSource] WebSocket 错误")
                onErrorCallback?.invoke(error)
            }
            
            override fun getCurrentPlayState(): PlayState? {
                // 只有当有有效的歌曲信息时才返回 PlayState
                val validMusicId = musicId.takeIf { it.isNotEmpty() && it != "unknown" }
                val validMusicName = musicName.takeIf { it.isNotEmpty() && it != "Unknown" && it != "等待播放" }
                
                if (validMusicId == null || validMusicName == null) {
                    Timber.d("[$debugSource] getCurrentPlayState: 无有效歌曲信息 (musicId=$musicId, musicName=$musicName)")
                    return null
                }
                
                val ttmlContent = lyrics?.lines?.let {
                    buildTtmlString(lyrics).takeIf { it.isNotBlank() }
                }
                
                val state = PlayState(
                    musicId = validMusicId,
                    musicName = validMusicName,
                    albumName = albumName,
                    artistName = artistName,
                    duration = duration,
                    progress = currentTime,
                    isPlaying = isPlaying,
                    ttmlLyric = ttmlContent
                )
                
                Timber.d("[$debugSource] getCurrentPlayState 返回:")
                Timber.d("  - musicId: ${state.musicId}")
                Timber.d("  - musicName: ${state.musicName}")
                Timber.d("  - artistName: ${state.artistName}")
                Timber.d("  - hasLyrics: ${!state.ttmlLyric.isNullOrBlank()}")
                Timber.d("  - isPlaying: ${state.isPlaying}")
                Timber.d("  - progress: ${state.progress}ms")
                
                return state
            }
        }
    }
    
    // ==================== 内部辅助函数 ====================
    
    /**
     * 构建 TTML 字符串（内部使用）
     */
    private fun buildTtmlString(lyrics: TTMLLyrics?): String {
        if (lyrics == null) return ""
        return lyrics.lines.joinToString("\n") { line ->
            "<p begin=\"${line.startTime}\" end=\"${line.endTime}\">${line.text}</p>"
        }
    }
    
    // ==================== WebSocket 投送辅助函数 ====================
    
    /**
     * 发送歌曲信息到 WebSocket 服务器
     * @param musicId 歌曲 ID
     * @param musicName 歌曲名称
     * @param albumName 专辑名称
     * @param artistName 艺术家名称
     * @param duration 歌曲时长
     */
    fun sendMusicInfo(
        musicId: String,
        musicName: String,
        albumName: String,
        artistName: String,
        duration: Long
    ) {
        if (!isConnected) {
            Timber.w("WebSocket 未连接，跳过发送歌曲信息")
            return
        }
        
        try {
            val message = WsProtocolV2Helper.createSetMusicUpdate(
                musicId = musicId,
                musicName = musicName,
                albumName = albumName,
                artists = listOf(WsProtocolV2Helper.Artist("1", artistName)),
                duration = duration
            )
            send(message)
            Timber.d("已发送歌曲信息：$musicName")
        } catch (e: Exception) {
            Timber.e(e, "发送歌曲信息失败")
        }
    }
    
    /**
     * 发送歌词到 WebSocket 服务器
     * @param ttmlContent TTML 格式的歌词内容
     */
    fun sendLyrics(ttmlContent: String) {
        if (!isConnected) {
            Timber.w("WebSocket 未连接，跳过发送歌词")
            return
        }
        
        try {
            val message = WsProtocolV2Helper.createTTMLLyricUpdate(ttmlContent)
            send(message)
            Timber.d("已发送歌词：size=${ttmlContent.length} chars")
        } catch (e: Exception) {
            Timber.e(e, "发送歌词失败")
        }
    }
    
    /**
     * 发送专辑图到 WebSocket 服务器
     * @param albumArtDataUrl Base64 编码的专辑图数据 URL
     */
    fun sendAlbumArt(albumArtDataUrl: String) {
        if (!isConnected) {
            Timber.w("WebSocket 未连接，跳过发送专辑图")
            return
        }
        
        if (albumArtDataUrl.isBlank()) {
            Timber.d("专辑图为空，跳过发送")
            return
        }
        
        try {
            // TODO: 实现专辑图投送协议
            Timber.d("发送专辑图：${if (albumArtDataUrl.length > 50) albumArtDataUrl.substring(0, 50) + "..." else albumArtDataUrl}")
            // 注意：需要 AMLL Player 服务端支持专辑图接收协议
            // 目前先记录日志，等待服务端协议定义
        } catch (e: Exception) {
            Timber.e(e, "发送专辑图失败")
        }
    }
    private var webSocket: WebSocket? = null
    // 使用固定的本地端口（每次启动时固定）
    private val localPort = 50000 + (System.currentTimeMillis() % 1000).toInt()
    
    // 自定义 SocketFactory 用于绑定本地端口
    private class FixedPortSocketFactory(private val localPort: Int) : SocketFactory() {
        override fun createSocket(): Socket {
            val socket = Socket()
            // 只绑定端口，不绑定特定 IP（让系统选择最佳本地 IP）
            try {
                socket.bind(InetSocketAddress(localPort))
                Timber.d("Socket 绑定到本地端口：$localPort")
            } catch (e: Exception) {
                Timber.w(e, "无法绑定到端口 $localPort，使用系统分配")
            }
            return socket
        }
        
        override fun createSocket(host: String, port: Int): Socket {
            return createSocket().apply { connect(InetSocketAddress(host, port)) }
        }
        
        override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket {
            return createSocket().apply { connect(InetSocketAddress(host, port)) }
        }
        
        override fun createSocket(host: InetAddress, port: Int): Socket {
            return createSocket().apply { connect(InetSocketAddress(host, port)) }
        }
        
        override fun createSocket(host: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket {
            return createSocket().apply { connect(InetSocketAddress(host, port)) }
        }
    }
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS) // 心跳包保持连接
        .socketFactory(FixedPortSocketFactory(localPort))
        .build()
    
    // 连接状态管理（public，供 UI 直接查询）
    @get:JvmName("getConnectionState")
    var isConnected = false
        get() = field
        set(value) {
            field = value
            // 状态变化时通知所有监听器
            if (value) {
                listeners.forEach { 
                    try {
                        it.onConnected()
                    } catch (e: Exception) {
                        Timber.e(e, "监听器 onConnected 异常")
                    }
                }
            } else {
                listeners.forEach { 
                    try {
                        it.onDisconnected()
                    } catch (e: Exception) {
                        Timber.e(e, "监听器 onDisconnected 异常")
                    }
                }
            }
        }
    private var serverUrl: String? = null
    private var isHandshakeComplete = false // 标记是否已完成握手
    private var negotiatedProtocolVersion: WsProtocolVersion = WsProtocolVersion.V2 // 默认 V2
    private val config = WsProtocolConfig() // 协议配置
    private var heartbeatJob: kotlinx.coroutines.Job? = null // 心跳任务
    
    /**
     * 设置消息监听器（已废弃，请使用 addListener）
     * @deprecated 使用 addListener 代替
     */
    @Deprecated("Use addListener instead", ReplaceWith("addListener(listener)"))
    fun setListener(listener: Listener) {
        addListener(listener)
    }
    
    /**
     * 连接到 WebSocket 服务器
     * 
     * @param url WebSocket 服务器地址 (ws://host:port 或 wss://host:port)
     * @param forceReconnect 是否强制重连（即使已连接）
     */
    fun connect(url: String, forceReconnect: Boolean = false) {
        // 如果已连接且不需要重连，则跳过
        if (isConnected && !forceReconnect) {
            Timber.d("WebSocket 已连接，跳过重连：$url")
            return
        }
        
        if (isConnected && forceReconnect) {
            Timber.i("强制重连 WebSocket，断开旧连接")
            disconnect()
        } else if (isConnected) {
            Timber.w("已经连接到 WebSocket 服务器，断开旧连接")
            disconnect()
        }
        
        serverUrl = url
        
        scope.launch {
            try {
                Timber.d("开始连接 WebSocket 服务器：$url")
                
                val request = Request.Builder()
                    .url(url)
                    .build()
                
                webSocket = client.newWebSocket(request, object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        Timber.i("WebSocket 连接成功，准备握手")
                        isConnected = true
                        
                        // 根据协议版本发送握手消息
                        if (config.sendInitialize) {
                            sendInitializeHandshake(webSocket)
                            // V2 协议：服务器不返回确认，等待短暂延迟确保消息发送
                            isHandshakeComplete = true
                            Timber.i("WebSocket 握手完成（V2 协议）")
                        } else {
                            isHandshakeComplete = true
                            Timber.i("WebSocket 已就绪（V1 二进制协议）")
                        }
                        
                        // 先通知监听器并获取初始状态（在心跳之前）
                        var stateSent = false
                        Timber.d("开始遍历监听器，总数：${listeners.size}")
                        listeners.forEachIndexed { index, listener ->
                            try {
                                Timber.d("调用监听器 #$index.onConnected()")
                                listener.onConnected()
                                
                                // 获取并发送当前播放状态
                                Timber.d("调用监听器 #$index.getCurrentPlayState()")
                                val playState = listener.getCurrentPlayState()
                                if (playState != null) {
                                    Timber.d("监听器 #$index 返回有效播放状态：${playState.musicName}, musicId=${playState.musicId}")
                                    sendInitialPlayState(webSocket, playState)
                                    stateSent = true
                                } else {
                                    Timber.d("监听器 #$index 返回 null 播放状态（无播放内容）")
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "监听器 #$index onConnected 异常")
                            }
                        }
                        
                        if (!stateSent) {
                            Timber.w("所有监听器均未提供有效播放状态")
                        }
                        
                        // 最后启动心跳机制
                        if (config.enableHeartbeat) {
                            startHeartbeat()
                        }
                    }
                    
                    override fun onMessage(webSocket: WebSocket, text: String) {
                        Timber.d("收到 WebSocket 消息：$text")
                        
                        // 尝试解析为 V2 协议消息
                        try {
                            val jsonElement = kotlinx.serialization.json.Json.parseToJsonElement(text)
                            val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.content
                            
                            when (type) {
                                "pong" -> {
                                    Timber.d("收到 Pong 心跳响应")
                                    // 心跳响应，无需特殊处理
                                }
                                "command" -> {
                                    Timber.d("收到控制命令")
                                    // 可以在此解析具体的命令
                                }
                                else -> {
                                    Timber.d("收到其他类型消息：$type")
                                }
                            }
                        } catch (e: Exception) {
                            Timber.w(e, "无法解析为 V2 协议消息，可能是旧版格式")
                        }
                        
                        listeners.forEach { listener ->
                            try {
                                listener.onMessageReceived(text)
                            } catch (e: Exception) {
                                Timber.e(e, "监听器 onMessageReceived 异常")
                            }
                        }
                    }
                    
                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        Timber.e(t, "WebSocket 连接失败")
                        isConnected = false
                        listeners.forEach { listener ->
                            try {
                                listener.onError(t)
                            } catch (e: Exception) {
                                Timber.e(e, "监听器 onError 异常")
                            }
                        }
                        
                        // 尝试重连（延迟 3 秒）
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                Thread.sleep(3000)
                            }
                            if (serverUrl != null) {
                                Timber.d("尝试重新连接...")
                                connect(serverUrl!!)
                            }
                        }
                    }
                    
                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        Timber.d("WebSocket 连接关闭：code=$code, reason=$reason")
                        isConnected = false
                        stopHeartbeat()
                        listeners.forEach { listener ->
                            try {
                                listener.onDisconnected()
                            } catch (e: Exception) {
                                Timber.e(e, "监听器 onDisconnected 异常")
                            }
                        }
                    }
                    
                    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                        Timber.d("WebSocket 正在关闭：code=$code, reason=$reason")
                        webSocket.close(code, reason)
                    }
                })
                
            } catch (e: Exception) {
                Timber.e(e, "创建 WebSocket 连接失败")
                listeners.forEach { listener ->
                    try {
                        listener.onError(e)
                    } catch (ex: Exception) {
                        Timber.e(ex, "监听器 onError 异常")
                    }
                }
            }
        }
    }
    
    /**
     * 断开 WebSocket 连接
     */
    fun disconnect() {
        Timber.d("断开 WebSocket 连接")
        webSocket?.close(1000, "用户主动断开")
        webSocket = null
        isConnected = false
        serverUrl = null
    }
    
    /**
     * 发送消息到服务器（文本）
     * 
     * @param message JSON 格式的消息
     */
    fun send(message: String) {
        if (!isConnected()) {
            Timber.w("WebSocket 未连接或握手未完成，无法发送消息：$message")
            return
        }
        
        scope.launch {
            try {
                webSocket?.send(message)
                Timber.d("发送 WebSocket 消息：$message")
            } catch (e: Exception) {
                Timber.e(e, "发送消息失败：$message")
                listeners.forEach { listener ->
                    try {
                        listener.onError(e)
                    } catch (ex: Exception) {
                        Timber.e(ex, "监听器 onError 异常")
                    }
                }
            }
        }
    }
    
    /**
     * 发送二进制消息到服务器
     * 
     * @param data 二进制数据
     */
    fun send(data: ByteArray) {
        if (!isConnected()) {
            Timber.w("WebSocket 未连接，无法发送二进制消息")
            return
        }
        
        scope.launch {
            try {
                val byteString = ByteString.of(*data)
                webSocket?.send(byteString)
                Timber.d("发送 WebSocket 二进制消息：${data.size} bytes, hex=${data.joinToString("") { "%02X".format(it) }}")
            } catch (e: Exception) {
                Timber.e(e, "发送二进制消息失败")
                listeners.forEach { listener ->
                    try {
                        listener.onError(e)
                    } catch (ex: Exception) {
                        Timber.e(ex, "监听器 onError 异常")
                    }
                }
            }
        }
    }
    
    /**
     * 检查是否已连接
     */
    fun isConnected(): Boolean {
        return isConnected && webSocket != null && isHandshakeComplete
    }
    
    /**
     * 销毁客户端，释放资源
     */
    fun destroy() {
        disconnect()
        scope.cancel()
        listeners.clear()
        isHandshakeComplete = false
        stopHeartbeat()
    }
    
    /**
     * 启动心跳机制
     */
    private fun startHeartbeat() {
        heartbeatJob?.cancel() // 取消旧的心跳
        
        heartbeatJob = scope.launch {
            Timber.d("启动心跳机制，间隔：${config.heartbeatIntervalSeconds}秒")
            
            while (isActive && isConnected()) {
                delay(config.heartbeatIntervalSeconds * 1000L)
                
                if (isConnected()) {
                    sendPing()
                }
            }
        }
    }
    
    /**
     * 停止心跳
     */
    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
        Timber.d("心跳已停止")
    }
    
    /**
     * 发送初始播放状态到服务器
     * 用于在连接成功后立即同步当前播放信息
     */
    private fun sendInitialPlayState(webSocket: WebSocket, playState: PlayState) {
        scope.launch {
            try {
                Timber.d("开始发送初始播放状态:")
                Timber.d("  - 歌曲 ID: ${playState.musicId}")
                Timber.d("  - 歌曲名：${playState.musicName}")
                Timber.d("  - 艺术家：${playState.artistName}")
                Timber.d("  - 时长：${playState.duration}ms")
                Timber.d("  - 进度：${playState.progress}ms")
                Timber.d("  - 播放状态：${if (playState.isPlaying) "播放中" else "暂停"}")
                Timber.d("  - 歌词：${if (!playState.ttmlLyric.isNullOrBlank()) "有" else "无"}")
                
                // 1. 发送歌曲信息
                val musicInfoMsg = WsProtocolV2Helper.createSetMusicUpdate(
                    musicId = playState.musicId,
                    musicName = playState.musicName,
                    albumName = playState.albumName,
                    artists = listOf(WsProtocolV2Helper.Artist("1", playState.artistName)),
                    duration = playState.duration
                )
                webSocket.send(musicInfoMsg)
                Timber.d("已发送歌曲信息消息")
                
                // 2. 发送播放进度和状态
                val progressMessage = WsProtocolV2Helper.createProgressUpdate(playState.progress)
                webSocket.send(progressMessage)
                Timber.d("已发送进度更新消息")
                
                val stateMessage = if (playState.isPlaying) {
                    WsProtocolV2Helper.createResumedUpdate()
                } else {
                    WsProtocolV2Helper.createPausedUpdate()
                }
                webSocket.send(stateMessage)
                Timber.d("已发送播放状态消息")
                
                // 3. 如果有歌词，发送歌词
                if (!playState.ttmlLyric.isNullOrBlank()) {
                    val ttmlMessage = WsProtocolV2Helper.createTTMLLyricUpdate(playState.ttmlLyric)
                    webSocket.send(ttmlMessage)
                    Timber.d("已发送歌词消息")
                }
                
                Timber.i("✓ 初始播放状态发送完成")
            } catch (e: Exception) {
                Timber.e(e, "✗ 发送初始播放状态失败：${e.message}")
                throw e // 重新抛出以便上层捕获
            }
        }
    }
    
    /**
     * 发送 Initialize 握手消息
     * V2 协议必须在连接后发送此消息
     */
    private fun sendInitializeHandshake(webSocket: WebSocket) {
        when (negotiatedProtocolVersion) {
            WsProtocolVersion.V2 -> {
                // V2 协议：发送 JSON 格式的 Initialize 消息
                val initializeMessage = """{"type":"initialize"}"""
                webSocket.send(initializeMessage)
                Timber.d("已发送 V2 Initialize 握手消息")
            }
            WsProtocolVersion.V1 -> {
                // V1 协议：不需要 Initialize 握手
                Timber.d("V1 二进制协议：跳过 Initialize 握手")
            }
        }
    }
    
    /**
     * 发送 Ping 心跳消息
     */
    fun sendPing() {
        if (!isConnected()) {
            Timber.w("WebSocket 未连接，无法发送 Ping")
            return
        }
        
        when (negotiatedProtocolVersion) {
            WsProtocolVersion.V2 -> {
                val pingMessage = """{"type":"ping"}"""
                send(pingMessage)
                Timber.d("已发送 Ping 心跳")
            }
            WsProtocolVersion.V1 -> {
                // V1 协议：发送二进制 Ping 消息 (Magic Number = 0)
                val pingData = byteArrayOf(0x00, 0x00)
                send(pingData)
                Timber.d("已发送 V1 Ping 心跳")
            }
        }
    }
    
    /**
     * 发送 Pong 响应
     */
    fun sendPong() {
        if (!isConnected()) {
            Timber.w("WebSocket 未连接，无法发送 Pong")
            return
        }
        
        when (negotiatedProtocolVersion) {
            WsProtocolVersion.V2 -> {
                val pongMessage = """{"type":"pong"}"""
                send(pongMessage)
                Timber.d("已发送 Pong 响应")
            }
            WsProtocolVersion.V1 -> {
                // V1 协议：发送二进制 Pong 消息 (Magic Number = 1)
                val pongData = byteArrayOf(0x01, 0x00)
                send(pongData)
                Timber.d("已发送 V1 Pong 响应")
            }
        }
    }
    
    /**
     * 获取当前协商的协议版本
     */
    fun getNegotiatedProtocol(): WsProtocolVersion {
        return negotiatedProtocolVersion
    }
}
