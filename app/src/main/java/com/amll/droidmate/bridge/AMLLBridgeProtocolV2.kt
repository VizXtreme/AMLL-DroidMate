package com.amll.droidmate.bridge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
import timber.log.Timber
import kotlin.collections.emptyList
import kotlin.let

/**
 * 增强版 AMLL 桥接协议 - 支持 Tauri 风格的协议检测
 * 
 * 特性：
 * 1. 自动协议版本协商（类似 Tauri V1/V2）
 * 2. 支持多种消息格式（JSON / 简化二进制）
 * 3. 握手识别机制
 * 4. 向后兼容旧版本接口
 */

// ==================== 协议版本定义 ====================

/**
 * 协议版本枚举
 */
enum class ProtocolVersion(val version: Int, val description: String) {
    LEGACY(0, "Legacy - 旧版散弹式接口"),
    V1(1, "V1 - 结构化 JSON 协议"),
    V2(2, "V2 - 带握手的增强协议");
    
    companion object {
        fun fromVersion(version: Int): ProtocolVersion {
            return entries.find { it.version == version } ?: LEGACY
        }
    }
}

// ==================== 握手消息 ====================

/**
 * 握手请求（WebView → Android）
 * 类似 Tauri 的 Initialize 消息
 */
@Serializable
data class HandshakeRequest(
    val type: String = "handshake",
    val version: Int,
    val capabilities: List<String> = emptyList()
)

/**
 * 握手响应（Android → WebView）
 */
@Serializable
data class HandshakeResponse(
    val type: String = "handshake_ack",
    val version: Int,
    val accepted: Boolean,
    val serverCapabilities: List<String> = emptyList(),
    val message: String? = null
)

// ==================== 数据结构 ====================

/**
 * 艺术家信息
 */
@Serializable
data class Artist(
    val id: String = "",
    val name: String
)

/**
 * 歌词单词
 */
@Serializable
data class LyricWord(
    val startTime: Long,
    val endTime: Long,
    val word: String,
    val romanWord: String = ""
)

/**
 * 歌词行
 */
@Serializable
data class LyricLine(
    val startTime: Long,
    val endTime: Long,
    val words: List<LyricWord>,
    val translatedLyric: String = "",
    val romanLyric: String = "",
    @SerialName("isBG")
    val isBg: Boolean = false,
    val isDuet: Boolean = false
)

/**
 * 图片数据
 */
@Serializable
data class ImageData(
    val mimeType: String? = null,
    val data: String // Base64 编码
)

// ==================== 状态负载定义 V2 ====================

/**
 * 状态负载 V2 - 支持多种状态更新类型
 */
@Serializable
sealed class StatePayloadV2 {
    abstract val type: String
}

/**
 * 音乐信息 V2
 */
@Serializable
@SerialName("setMusic")
data class SetMusicInfoV2(
    val musicId: String,
    val musicName: String,
    val albumId: String = "",
    val albumName: String,
    val artists: List<Artist>,
    val duration: Long
) : StatePayloadV2() {
    override val type: String = "setMusic"
    
    // 向后兼容的构造函数
    constructor(
        musicId: String,
        musicName: String,
        albumName: String,
        artistName: String,
        duration: Long
    ) : this(
        musicId = musicId,
        musicName = musicName,
        albumName = albumName,
        artists = listOf(Artist(name = artistName)),
        duration = duration
    )
}

/**
 * 专辑封面 V2
 */
@Serializable
sealed class AlbumCoverV2 : StatePayloadV2() {
    override val type: String = "setCover"
    
    @SerialName("uri")
    data class Uri(val url: String) : AlbumCoverV2()
    
    @SerialName("data")
    data class Data(val imageData: ImageData) : AlbumCoverV2()
}

/**
 * 歌词内容 V2 - 支持结构化 JSON 和 TTML 两种格式
 */
@Serializable
sealed class LyricContentV2 : StatePayloadV2() {
    override val type: String = "setLyric"
    
    /**
     * 结构化歌词（JSON 格式）- 推荐使用的格式
     */
    @SerialName("structured")
    data class Structured(val lines: List<LyricLine>) : LyricContentV2()
    
    /**
     * TTML 格式（向后兼容）
     */
    @SerialName("ttml")
    data class Ttml(val data: String) : LyricContentV2()
}

/**
 * 进度更新 V2
 */
@Serializable
@SerialName("progress")
data class ProgressUpdateV2(
    val progress: Long
) : StatePayloadV2() {
    override val type: String = "progress"
}

/**
 * 音量更新 V2
 */
@Serializable
@SerialName("volume")
data class VolumeUpdateV2(
    val volume: Float
) : StatePayloadV2() {
    override val type: String = "volume"
}

/**
 * 暂停状态 V2
 */
@Serializable
@SerialName("paused")
data class PausedUpdateV2(
    val timestamp: Long = System.currentTimeMillis()
) : StatePayloadV2() {
    override val type: String = "paused"
}

/**
 * 恢复状态 V2
 */
@Serializable
@SerialName("resumed")
data class ResumedUpdateV2(
    val timestamp: Long = System.currentTimeMillis()
) : StatePayloadV2() {
    override val type: String = "resumed"
}

/**
 * 播放模式变更 V2
 */
@Serializable
@SerialName("modeChanged")
data class ModeChangedV2(
    val repeat: RepeatMode,
    val shuffle: Boolean
) : StatePayloadV2() {
    override val type: String = "modeChanged"
}

/**
 * 音频数据 V2
 */
@Serializable
@SerialName("audioData")
data class AudioDataV2(
    val data: String // Base64 编码
) : StatePayloadV2() {
    override val type: String = "audioData"
}

/**
 * 重复模式
 */
@Serializable
enum class RepeatMode {
    @SerialName("off")
    OFF,
    
    @SerialName("all")
    ALL,
    
    @SerialName("one")
    ONE
}

// ==================== 命令负载定义 V2 ====================

/**
 * 命令负载 V2 - 支持多种命令类型
 */
@Serializable
sealed class CommandPayloadV2 {
    abstract val command: String
}

/**
 * 暂停命令 V2
 */
@Serializable
@SerialName("pause")
data class PauseCommandV2(
    override val command: String = "pause"
) : CommandPayloadV2()

/**
 * 恢复命令 V2
 */
@Serializable
@SerialName("resume")
data class ResumeCommandV2(
    override val command: String = "resume"
) : CommandPayloadV2()

/**
 * 前进歌曲命令 V2
 */
@Serializable
@SerialName("forwardSong")
data class ForwardSongCommandV2(
    override val command: String = "forwardSong"
) : CommandPayloadV2()

/**
 * 后退歌曲命令 V2
 */
@Serializable
@SerialName("backwardSong")
data class BackwardSongCommandV2(
    override val command: String = "backwardSong"
) : CommandPayloadV2()

/**
 * seek 播放进度命令 V2
 */
@Serializable
@SerialName("seekPlayProgress")
data class SeekCommandV2(
    override val command: String = "seekPlayProgress",
    val progress: Long
) : CommandPayloadV2()

/**
 * 设置音量命令 V2
 */
@Serializable
@SerialName("setVolume")
data class SetVolumeCommandV2(
    override val command: String = "setVolume",
    val volume: Double
) : CommandPayloadV2()

/**
 * 设置重复模式命令 V2
 */
@Serializable
@SerialName("setRepeatMode")
data class SetRepeatModeCommandV2(
    override val command: String = "setRepeatMode",
    val mode: RepeatMode
) : CommandPayloadV2()

/**
 * 设置随机播放命令 V2
 */
@Serializable
@SerialName("setShuffleMode")
data class SetShuffleModeCommandV2(
    override val command: String = "setShuffleMode",
    val enabled: Boolean
) : CommandPayloadV2()

// ==================== 增强的消息结构 ====================

/**
 * 顶层消息（支持版本字段）
 */
@Serializable
sealed class BridgeMessageV2 {
    abstract val type: String
    abstract val version: Int
    
    companion object {
        const val CURRENT_VERSION = 2
    }
}

/**
 * 状态更新消息 V2
 */
@Serializable
@SerialName("stateUpdate")
data class StateUpdateMessageV2(
    override val type: String = "stateUpdate",
    override val version: Int = BridgeMessageV2.CURRENT_VERSION,
    val payload: StatePayloadV2
) : BridgeMessageV2()

/**
 * 命令消息 V2
 */
@Serializable
@SerialName("command")
data class CommandMessageV2(
    override val type: String = "command",
    override val version: Int = BridgeMessageV2.CURRENT_VERSION,
    val payload: CommandPayloadV2
) : BridgeMessageV2()

// ==================== 协议检测器 ====================

/**
 * 协议类型检测结果
 */
sealed class ProtocolDetectionResult {
    data class Success(val version: ProtocolVersion, val capabilities: List<String>) : ProtocolDetectionResult()
    data class Failure(val reason: String) : ProtocolDetectionResult()
    object Legacy : ProtocolDetectionResult() // 旧版本
}

/**
 * 协议检测器 - 自动识别消息格式
 * 灵感来自 Tauri 的协议识别机制
 */
class ProtocolDetector {
    
    /**
     * 检测接收到的消息类型
     * 
     * @param jsonString 从 WebView 接收的 JSON 字符串
     * @return 检测结果
     */
    fun detectProtocol(jsonString: String): ProtocolDetectionResult {
        return try {
            val json = Json.parseToJsonElement(jsonString).jsonObject
            
            // 检查是否有 handshake 字段（V2 协议特征）
            val typeField = json["type"]?.jsonPrimitive?.content
            
            when {
                // === V2 协议：握手消息 ===
                typeField == "handshake" -> {
                    val version = json["version"]?.jsonPrimitive?.int ?: 0
                    val capabilities: List<String> = json["capabilities"]?.let { elem ->
                        Json.decodeFromJsonElement(elem)
                    } ?: emptyList()
                    
                    if (version >= 2) {
                        ProtocolDetectionResult.Success(
                            ProtocolVersion.V2,
                            capabilities
                        )
                    } else {
                        ProtocolDetectionResult.Failure("不支持的 V2 版本：$version")
                    }
                }
                
                // === V1 协议：标准命令/状态消息 ===
                typeField in listOf("command", "stateUpdate") -> {
                    val version = json["version"]?.jsonPrimitive?.int ?: 1
                    
                    ProtocolDetectionResult.Success(
                        ProtocolVersion.fromVersion(version),
                        emptyList()
                    )
                }
                
                // === Legacy 协议：旧版消息（无 type 字段） ===
                typeField == null -> {
                    // 尝试识别旧版消息格式
                    if (json.containsKey("updateType") || json.containsKey("command")) {
                        ProtocolDetectionResult.Legacy
                    } else {
                        ProtocolDetectionResult.Failure("无法识别的消息格式")
                    }
                }
                
                // === 未知协议 ===
                else -> {
                    ProtocolDetectionResult.Failure("未知的消息类型：$typeField")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "[Bridge] Protocol detection failed")
            ProtocolDetectionResult.Failure("解析错误：${e.message}")
        }
    }
}

// ==================== 增强版桥接管理器 ====================

/**
 * 增强版 AMLL 桥接管理器
 * 支持 Tauri 风格的协议检测和版本协商
 */
class AMLLBridgeManagerV2 {
    
    interface Listener {
        fun onCommand(command: CommandPayloadV2)
        fun onError(error: Throwable)
        fun onProtocolNegotiated(version: ProtocolVersion) // 新增：协议协商成功回调
    }
    
    private var listener: Listener? = null
    private val protocolDetector = ProtocolDetector()
    private var negotiatedVersion: ProtocolVersion = ProtocolVersion.LEGACY
    private val messageQueue = mutableListOf<StateUpdateMessageV2>()
    private var isHandshakeComplete = false
    private var clientCapabilities = emptyList<String>()
    
    /**
     * 设置监听器
     */
    fun setListener(listener: Listener) {
        this.listener = listener
    }
    
    /**
     * 从 WebView 接收消息
     * 自动检测协议版本并处理
     */
    fun receiveFromWebView(jsonString: String) {
        Timber.d("[Bridge] Received message from WebView: $jsonString")
        
        // Step 1: 协议检测
        val detectionResult = protocolDetector.detectProtocol(jsonString)
        
        when (detectionResult) {
            // === V1/V2 协议成功 ===
            is ProtocolDetectionResult.Success -> {
                handleNegotiatedProtocol(detectionResult, jsonString)
            }
            
            // === Legacy 协议 ===
            is ProtocolDetectionResult.Legacy -> {
                Timber.d("[Bridge] Detected Legacy protocol, using compatibility mode")
                handleLegacyMessage(jsonString)
            }
            
            // === 检测失败 ===
            is ProtocolDetectionResult.Failure -> {
                Timber.e("[Bridge] Protocol detection failed: ${detectionResult.reason}")
                listener?.onError(Exception(detectionResult.reason))
            }
        }
    }
    
    /**
     * 处理已协商的协议（V1/V2）
     */
    private fun handleNegotiatedProtocol(
        result: ProtocolDetectionResult.Success,
        jsonString: String
    ) {
        negotiatedVersion = result.version
        clientCapabilities = result.capabilities
        
        Timber.i("[Bridge] Protocol negotiated successfully: ${result.version.description}, capabilities: ${result.capabilities}")
        
        // 如果是握手消息，发送响应
        if (jsonString.contains("\"handshake\"")) {
            handleHandshake(jsonString)
            return
        }
        
        // 解析并处理标准消息
        try {
            val json = Json.parseToJsonElement(jsonString).jsonObject
            val type = json["type"]?.jsonPrimitive?.content
            
            when (type) {
                "command" -> {
                    val command = Json.decodeFromString<CommandMessageV2>(jsonString)
                    listener?.onCommand(command.payload)
                }
                else -> {
                    Timber.w("[Bridge] Received unexpected message type: $type")
                }
            }
        } catch (e: Exception) {
            listener?.onError(e)
        }
    }
    
    /**
     * 处理握手请求
     */
    private fun handleHandshake(jsonString: String) {
        try {
            val request = Json.decodeFromString<HandshakeRequest>(jsonString)
            
            // 构建响应
            val response = HandshakeResponse(
                version = BridgeMessageV2.CURRENT_VERSION,
                accepted = true,
                serverCapabilities = listOf(
                    "music_info",
                    "lyric_update",
                    "progress_sync",
                    "volume_control"
                ),
                message = "协议握手成功，使用版本：${BridgeMessageV2.CURRENT_VERSION}"
            )
            
            // 发送响应
            sendToWebView(Json.encodeToString(response))
            
            isHandshakeComplete = true
            
            // 通知监听器
            listener?.onProtocolNegotiated(ProtocolVersion.V2)
            
            // 发送队列中的消息
            messageQueue.forEach { sendToWebView(Json.encodeToString(it)) }
            messageQueue.clear()
            
            Timber.i("[Bridge] Handshake completed, protocol version: V2")
        } catch (e: Exception) {
            Timber.e(e, "[Bridge] Handshake processing failed")
            listener?.onError(e)
        }
    }
    
    /**
     * 处理旧版消息（兼容模式）
     */
    private fun handleLegacyMessage(jsonString: String) {
        try {
            // 尝试解析为旧版格式
            val element = Json.parseToJsonElement(jsonString)
            val jsonObject = element.jsonObject
            
            // 检查是否是旧版命令
            val command = jsonObject["command"]?.jsonPrimitive?.content
            if (command != null) {
                // 转换为新版 CommandPayloadV2
                val legacyCommand = when (command) {
                    "seekPlayProgress" -> {
                        val progress = jsonObject["progress"]?.jsonPrimitive?.long ?: 0L
                        SeekCommandV2(progress = progress)
                    }
                    "pause" -> PauseCommandV2()
                    "resume" -> ResumeCommandV2()
                    else -> null
                }
                
                legacyCommand?.let { listener?.onCommand(it) }
            }
        } catch (e: Exception) {
            listener?.onError(e)
        }
    }
    
    /**
     * 发送消息到 WebView
     */
    private fun sendToWebView(message: String) {
        // 实际实现在这里调用 JavascriptInterface
        Timber.d("[Bridge] Sending to WebView: $message")
    }
    
    /**
     * 发送状态更新
     * 如果握手未完成，加入队列
     */
    fun sendStateUpdate(payload: StatePayloadV2) {
        val message = StateUpdateMessageV2(payload = payload)
        
        if (isHandshakeComplete || negotiatedVersion == ProtocolVersion.LEGACY) {
            sendToWebView(Json.encodeToString(message))
        } else {
            messageQueue.add(message)
            Timber.d("[Bridge] Handshake incomplete, message added to queue")
        }
    }
    
    /**
     * 主动发起握手（可选）
     * 如果 WebView 没有主动发起，Android 端可以主动发起
     */
    fun initiateHandshake() {
        Timber.d("[Bridge] Initiating handshake request")
        
        val request = HandshakeRequest(
            version = BridgeMessageV2.CURRENT_VERSION,
            capabilities = listOf(
                "android_media_session",
                "notification_control",
                "lockscreen_display"
            )
        )
        
        sendToWebView(Json.encodeToString(request))
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
            SetMusicInfoV2(
                musicId = musicId,
                musicName = musicName,
                albumName = albumName,
                artistName = artistName,
                duration = duration
            )
        )
    }
    
    /**
     * 更新歌词（结构化 JSON 格式）- 推荐使用
     */
    fun updateLyric(lines: List<LyricLine>) {
        sendStateUpdate(LyricContentV2.Structured(lines = lines))
    }
    
    /**
     * 更新歌词（TTML 格式）- 向后兼容
     */
    fun updateLyricTtml(ttmlContent: String) {
        sendStateUpdate(LyricContentV2.Ttml(data = ttmlContent))
    }
    
    fun updateProgress(progress: Long) {
        sendStateUpdate(ProgressUpdateV2(progress = progress))
    }
    
    fun pause() {
        sendStateUpdate(PausedUpdateV2())
    }
    
    fun resume() {
        sendStateUpdate(ResumedUpdateV2())
    }
    
    fun updateVolume(volume: Float) {
        sendStateUpdate(VolumeUpdateV2(volume = volume.coerceIn(0f, 1f)))
    }
    
    /**
     * 获取当前协议版本
     */
    fun getCurrentProtocolVersion(): ProtocolVersion {
        return negotiatedVersion
    }
    
    /**
     * 检查是否支持某个功能
     */
    fun hasCapability(capability: String): Boolean {
        return clientCapabilities.contains(capability)
    }
}

// ==================== 使用示例 ====================

/**
 * 在 AMLLLyricsView 中的使用示例
 */
/*
class AMLLLyricsView @Composable(...) {
    val bridgeManager = remember { AMLLBridgeManagerV2() }
    
    LaunchedEffect(Unit) {
        bridgeManager.setListener(object : AMLLBridgeManagerV2.Listener {
            override fun onCommand(command: CommandPayload) {
                when (command) {
                    is SeekCommand -> mediaPlayer.seekTo(command.progress)
                    is PauseCommand -> mediaPlayer.pause()
                    is ResumeCommand -> mediaPlayer.start()
                }
            }
            
            override fun onError(error: Throwable) {
                Timber.e("[Bridge] Bridge error", error)
            }
            
            override fun onProtocolNegotiated(version: ProtocolVersion) {
                Timber.i("[Bridge] Protocol negotiated successfully: $version")
                
                when (version) {
                    ProtocolVersion.V2 -> {
                        // 使用 V2 特性
                        if (bridgeManager.hasCapability("lockscreen_display")) {
                            enableLockscreenLyrics()
                        }
                    }
                    ProtocolVersion.V1 -> {
                        // 降级到 V1 功能
                    }
                    ProtocolVersion.LEGACY -> {
                        // 使用旧版兼容模式
                    }
                }
            }
        })
    }
    
    // WebView 接口
    addJavascriptInterface(
        object {
            @JavascriptInterface
            fun postMessage(json: String) {
                bridgeManager.receiveFromWebView(json)
            }
        },
        "AndroidBridge"
    )
    
    // 可选：主动发起握手
    LaunchedEffect(Unit) {
        delay(500) // 等待 WebView 加载
        bridgeManager.initiateHandshake()
    }
}
*/
