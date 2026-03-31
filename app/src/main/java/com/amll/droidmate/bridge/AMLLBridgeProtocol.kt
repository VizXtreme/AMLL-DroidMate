package com.amll.droidmate.bridge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * AMLL 桥接协议 - 受 Tauri WebSocket 协议启发的 Android 实现
 * 
 * 设计思想：
 * 1. 结构化消息传递，避免散弹式代码
 * 2. 双向通信：Android ↔ WebView
 * 3. 类型安全：使用 Kotlin 序列化保证数据正确性
 * 4. 向后兼容：支持多种消息格式版本
 */

// ==================== 消息结构定义 ====================

/**
 * 顶层消息结构（类似 Tauri 的 MessageV2）
 */
@Serializable
sealed class BridgeMessage {
    abstract val type: String
    abstract val version: Int
    
    companion object {
        const val CURRENT_VERSION = 1
    }
}

/**
 * Android → WebView 的消息（状态更新）
 */
@Serializable
@SerialName("stateUpdate")
data class StateUpdateMessage(
    override val type: String = "stateUpdate",
    override val version: Int = BridgeMessage.CURRENT_VERSION,
    val payload: StatePayload
) : BridgeMessage()

/**
 * WebView → Android 的消息（命令请求）
 */
@Serializable
@SerialName("command")
data class CommandMessage(
    override val type: String = "command",
    override val version: Int = BridgeMessage.CURRENT_VERSION,
    val payload: CommandPayload
) : BridgeMessage()

// ==================== Payload 定义 ====================

/**
 * 状态更新的有效载荷
 */
@Serializable
sealed class StatePayload {
    abstract val updateType: String
}

@Serializable
@SerialName("setMusicInfo")
data class SetMusicInfo(
    override val updateType: String = "setMusicInfo",
    val musicId: String,
    val musicName: String,
    val albumName: String,
    val artistName: String,
    val duration: Long
) : StatePayload()

@Serializable
@SerialName("setLyric")
data class SetLyric(
    override val updateType: String = "setLyric",
    val format: String = "ttml",
    val data: String
) : StatePayload()

@Serializable
@SerialName("progress")
data class ProgressUpdate(
    override val updateType: String = "progress",
    val progress: Long // 毫秒
) : StatePayload()

@Serializable
@SerialName("paused")
data class PausedUpdate(
    override val updateType: String = "paused"
) : StatePayload()

@Serializable
@SerialName("resumed")
data class ResumedUpdate(
    override val updateType: String = "resumed"
) : StatePayload()

@Serializable
@SerialName("volumeChanged")
data class VolumeUpdate(
    override val updateType: String = "volumeChanged",
    val volume: Float // 0.0 - 1.0
) : StatePayload()

/**
 * 命令请求的有效载荷
 */
@Serializable
sealed class CommandPayload {
    abstract val command: String
}

@Serializable
@SerialName("seekPlayProgress")
data class SeekCommand(
    override val command: String = "seekPlayProgress",
    val progress: Long
) : CommandPayload()

@Serializable
@SerialName("pause")
data class PauseCommand(
    override val command: String = "pause"
) : CommandPayload()

@Serializable
@SerialName("resume")
data class ResumeCommand(
    override val command: String = "resume"
) : CommandPayload()

@Serializable
@SerialName("setVolume")
data class SetVolumeCommand(
    override val command: String = "setVolume",
    val volume: Float
) : CommandPayload()

@Serializable
@SerialName("lyricLineClicked")
data class LyricLineClickCommand(
    override val command: String = "lyricLineClicked",
    val lineIndex: Int,
    val time: Long
) : CommandPayload()

// ==================== 编解码器 ====================

/**
 * 多态序列化配置
 */
val bridgeJson = kotlinx.serialization.json.Json {
    ignoreUnknownKeys = true
    isLenient = true
    classDiscriminator = "updateType"
}

// ==================== 桥接管理器 ====================

/**
 * AMLL 桥接管理器
 * 
 * 统一管理 Android 和 WebView 之间的双向通信
 * 灵感来自 Tauri 的事件驱动架构
 */
class AMLLBridgeManager {
    
    interface Listener {
        fun onCommand(command: CommandPayload)
        fun onError(error: Throwable)
    }
    
    private var listener: Listener? = null
    private val messageQueue = mutableListOf<StateUpdateMessage>()
    private var isWebViewReady = false
    
    fun setListener(listener: Listener) {
        this.listener = listener
    }
    
    /**
     * 设置 WebView 就绪状态
     */
    fun onWebViewReady() {
        isWebViewReady = true
        // 发送队列中的消息
        messageQueue.forEach { sendMessage(it) }
        messageQueue.clear()
    }
    
    /**
     * 从 WebView 接收消息
     */
    fun receiveFromWebView(jsonString: String) {
        try {
            val element = kotlinx.serialization.json.Json.parseToJsonElement(jsonString)
            val type = element.jsonObject["type"]?.jsonPrimitive?.content
            
            when (type) {
                "command" -> {
                    val command = bridgeJson.decodeFromString<CommandMessage>(jsonString)
                    listener?.onCommand(command.payload)
                }
                else -> {
                    // 忽略未知消息类型
                }
            }
        } catch (e: Exception) {
            listener?.onError(e)
        }
    }
    
    /**
     * 发送状态更新到 WebView
     */
    fun sendStateUpdate(payload: StatePayload) {
        val message = StateUpdateMessage(payload = payload)
        
        if (isWebViewReady) {
            sendMessage(message)
        } else {
            // WebView 未就绪时加入队列
            messageQueue.add(message)
        }
    }
    
    /**
     * 实际发送消息到 WebView
     */
    private fun sendMessage(message: StateUpdateMessage) {
        val jsonString = bridgeJson.encodeToString(message)
        // 通过 JavascriptInterface 调用
        // 这部分在 AMLLLyricsView 中实现
    }
    
    // ==================== 便捷方法 ====================
    
    fun updateMusicInfo(
        musicId: String,
        musicName: String,
        albumName: String,
        artistName: String,
        duration: Long
    ) {
        sendStateUpdate(
            SetMusicInfo(
                musicId = musicId,
                musicName = musicName,
                albumName = albumName,
                artistName = artistName,
                duration = duration
            )
        )
    }
    
    fun updateLyric(ttmlContent: String) {
        sendStateUpdate(SetLyric(data = ttmlContent))
    }
    
    fun updateProgress(progress: Long) {
        sendStateUpdate(ProgressUpdate(progress = progress))
    }
    
    fun pause() {
        sendStateUpdate(PausedUpdate())
    }
    
    fun resume() {
        sendStateUpdate(ResumedUpdate())
    }
    
    fun updateVolume(volume: Float) {
        sendStateUpdate(VolumeUpdate(volume = volume.coerceIn(0f, 1f)))
    }
}

// ==================== 使用示例 ====================

/**
 * 在 AMLLLyricsView 中的使用示例
 * 
 * 伪代码展示如何使用这个桥接管理器
 */
/*
class AMLLLyricsView @Composable(...) {
    val bridgeManager = remember { AMLLBridgeManager() }
    
    // 初始化 WebView
    AndroidView(factory = { context ->
        WebView(context).apply {
            addJavascriptInterface(
                object {
                    @JavascriptInterface
                    fun postMessage(json: String) {
                        bridgeManager.receiveFromWebView(json)
                    }
                },
                "AndroidBridge"
            )
        }
    })
    
    // 监听命令
    bridgeManager.setListener(object : AMLLBridgeManager.Listener {
        override fun onCommand(command: CommandPayload) {
            when (command) {
                is SeekCommand -> mediaPlayer.seekTo(command.progress)
                is PauseCommand -> mediaPlayer.pause()
                is ResumeCommand -> mediaPlayer.start()
                is SetVolumeCommand -> mediaPlayer.volume = command.volume
                is LyricLineClickCommand -> onLyricClick(command.lineIndex)
            }
        }
        
        override fun onError(error: Throwable) {
            Timber.e("[Bridge] Bridge message error", error)
        }
    })
    
    // 发送状态更新
    LaunchedEffect(currentTime) {
        bridgeManager.updateProgress(currentTime)
    }
}
*/
