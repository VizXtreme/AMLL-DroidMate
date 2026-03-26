package com.amll.droidmate.websocket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    }
    
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
    
    private var isConnected = false
    private var serverUrl: String? = null
    private var isHandshakeComplete = false // 标记是否已完成握手
    
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
     */
    fun connect(url: String) {
        if (isConnected) {
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
                        Timber.i("WebSocket 连接成功！服务器响应：${response.code}")
                        isConnected = true
                        
                        // 发送 Initialize 握手消息（V2 协议要求）
                        sendInitializeHandshake(webSocket)
                        
                        // V2 协议下，发送 Initialize 后即视为握手完成
                        // 服务器不会返回确认消息，而是直接进入消息处理循环
                        isHandshakeComplete = true
                        Timber.i("WebSocket 握手完成（V2 协议）")
                        
                        // 通知所有监听器
                        listeners.forEach { listener ->
                            try {
                                listener.onConnected()
                            } catch (e: Exception) {
                                Timber.e(e, "监听器 onConnected 异常")
                            }
                        }
                    }
                    
                    override fun onMessage(webSocket: WebSocket, text: String) {
                        Timber.d("收到 WebSocket 消息：$text")
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
    }
    
    /**
     * 发送 Initialize 握手消息
     */
    private fun sendInitializeHandshake(webSocket: WebSocket) {
        // skia-player 使用 V1 二进制协议，不需要 Initialize 握手
        // 直接发送二进制 Body 消息即可
        Timber.d("V1 二进制协议：跳过 Initialize 握手")
    }
}
