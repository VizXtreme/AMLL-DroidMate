package com.amll.droidmate.websocket

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.util.Base64
import timber.log.Timber

/**
 * AMLL WebSocket V2 协议消息工具类
 * 
 * V2 协议是 JSON 混合协议，相比 V1 二进制协议有以下优势：
 * 1. 易于调试：JSON 格式可以直接阅读
 * 2. 更好的扩展性：添加新字段不影响旧版本
 * 3. 类型安全：使用 Kotlin 序列化自动验证数据结构
 * 
 * 消息结构采用扁平化设计，所有字段都在同一层级，避免嵌套过深。
 * 支持的消息类型：
 * - initialize: 初始化连接
 * - ping/pong: 心跳检测
 * - command: 控制命令（播放/暂停/跳转等）
 * - state: 状态更新（音乐信息、进度、歌词等）
 */
object WsProtocolV2Helper {
    
    // JSON 配置：优化序列化的行为
    private val json = Json {
        ignoreUnknownKeys = true       // 忽略未知字段，保证向后兼容
        encodeDefaults = false         // 不编码默认值，减少数据量
        // ⭐ 修复关键：允许编码特殊字符，确保 TTML 中的 XML 能正确传输
        explicitNulls = false          // 不编码 null 值，进一步减少数据量
    }
    
    // ==================== V2 协议消息类型 ====================
    // 这些是 V2 协议中使用的数据模型
    
    /**
     * 顶层消息结构 - 使用扁平化设计
     * 
     * 所有 V2 消息都遵循这个统一格式：
     * - type: 消息类型标识符
     * - value: 具体的载荷数据（可选）
     */
    @Serializable
    data class MessageV2(
        val type: String,  // "initialize", "ping", "pong", "command", "state"
        val value: ValuePayload? = null
    )
    
    @Serializable
    data class ValuePayload(
        val update: String? = null,      // 状态更新类型："setMusic", "progress", "paused" 等
        val command: String? = null,     // 控制命令："pause", "resume", "forward" 等
        // MusicInfo 字段（扁平化，直接放在 value 层级）
        val musicId: String? = null,     // 音乐 ID
        val musicName: String? = null,   // 歌曲名称
        val albumId: String? = null,     // 专辑 ID
        val albumName: String? = null,   // 专辑名称
        val artists: List<Artist>? = null,  // 艺术家列表
        val duration: Long? = null,      // 歌曲时长（毫秒）
        // 其他字段
        val progress: Long? = null,      // 当前播放进度（毫秒）
        val volume: Double? = null,      // 音量（0.0-1.0）
        // 模式相关字段（用于 modeChanged）
        val repeat: String? = null,      // 循环模式："off", "all", "one"
        val shuffle: Boolean? = null,    // 随机播放：true/false
        // 专辑封面相关字段（用于 setCover）
        val source: String? = null,      // 来源类型："uri" 或 "data"
        val url: String? = null,         // URI 模式的 URL
        val image: ImageData? = null,    // Data 模式的嵌套图片对象
        // 歌词相关字段（用于 setLyric）
        val lines: List<LyricLine>? = null,  // Structured 模式：逐行歌词
        val format: String? = null,          // 歌词格式："ttml" 或 null
        val data: String? = null             // TTML 字符串（当 format="ttml" 时）
    )
    
    /**
     * 专辑图片数据（嵌套结构，用于 AlbumCover::Data）
     * 
     * 当专辑封面以 Base64 数据形式传输时使用这个结构。
     * @param mimeType 图片 MIME 类型（如 "image/jpeg"）
     * @param data Base64 编码的图片数据
     */
    @Serializable
    data class ImageData(
        val mimeType: String? = null,
        val data: String? = null  // Base64 编码的图片数据
    )
    
    @Serializable
    data class LyricLine(
        val startTime: Long,
        val endTime: Long,
        val words: List<LyricWord> = emptyList(),
        val translatedLyric: String = "",
        val romanLyric: String = "",
        val isBG: Boolean = false,
        val isDuet: Boolean = false
    )
    
    @Serializable
    data class LyricWord(
        val startTime: Long,
        val endTime: Long,
        val word: String
    )
    
    /**
     * 专辑图片数据（嵌套结构）
     */
    @Serializable
    data class Artist(
        val id: String,
        val name: String
    )
    
    /**
     * Initialize 握手消息
     */
    fun createInitialize(): String {
        return encode(MessageV2(type = "initialize"))
    }
    
    /**
     * Ping 心跳消息
     */
    fun createPing(): String {
        return encode(MessageV2(type = "ping"))
    }
    
    /**
     * Pong 心跳响应
     */
    fun createPong(): String {
        return encode(MessageV2(type = "pong"))
    }
    
    /**
     * 命令消息（服务器 → 客户端）
     */
    fun createCommand(command: String): String {
        return encode(MessageV2(type = "command", value = ValuePayload(command = command)))
    }
    
    /**
     * 状态更新消息（客户端 → 服务器）
     */
    fun createStateUpdate(update: String, valuePayload: ValuePayload): String {
        return encode(MessageV2(type = "state", value = valuePayload))
    }
    
    // ==================== 辅助方法（已在前文定义） ====================
    
    /**
     * 快速创建播放进度更新消息
     */
    fun createProgressUpdate(progressMs: Long): String {
        return encode(MessageV2(type = "state", value = ValuePayload(update = "progress", progress = progressMs)))
    }
    
    /**
     * 快速创建暂停状态消息
     */
    fun createPausedUpdate(): String {
        return encode(MessageV2(type = "state", value = ValuePayload(update = "paused")))
    }
    
    /**
     * 快速创建恢复播放状态消息
     */
    fun createResumedUpdate(): String {
        return encode(MessageV2(type = "state", value = ValuePayload(update = "resumed")))
    }
    
    /**
     * 快速创建歌曲信息更新消息
     */
    fun createSetMusicUpdate(
        musicId: String,
        musicName: String,
        albumName: String,
        artists: List<Artist>,
        duration: Long,
        albumId: String = ""
    ): String {
        return encode(MessageV2(
            type = "state",
            value = ValuePayload(
                update = "setMusic",
                musicId = musicId,
                musicName = musicName,
                albumId = albumId,
                albumName = albumName,
                artists = artists,
                duration = duration
            )
        ))
    }
    
    /**
     * 快速创建 TTML 歌词更新消息
     * 
     * ⭐ 修复关键：TTML 字符串包含 XML 特殊字符，需要确保 JSON 编码正确
     */
    fun createTTMLLyricUpdate(ttmlContent: String): String {
        // 注意：根据 Rust 服务端 v2.rs 的定义，Ttml 变体的 data 字段应该是原始 TTML 字符串
        // pub enum LyricContent {
        //     Structured { lines: Vec<LyricLine> },
        //     Ttml { data: String },  // 直接是 String，不是 Base64
        // }
        // 因此不需要进行 Base64 编码，直接发送原始 TTML 内容
        // 根据 serde 的 tag 配置，format 和 data 字段会被提升到 value 层级
        return try {
            val message = MessageV2(type = "state", value = ValuePayload(update = "setLyric", format = "ttml", data = ttmlContent))
            val encoded = encode(message)
            Timber.d("[WsProtocolV2] Created TTML lyric update message, size=${encoded.length} chars")
            Timber.d("[WsProtocolV2] JSON preview (first 300): ${encoded.take(300)}")
            encoded
        } catch (e: Exception) {
            Timber.e("[WsProtocolV2] Failed to create TTML lyric update", e)
            throw RuntimeException("创建 TTML 歌词消息失败：${e.message}", e)
        }
    }
    
    /**
     * 快速创建专辑封面更新消息（Base64 Data URL 格式）
     * @param base64DataUrl 专辑图的 Base64 Data URL，格式如：data:image/jpeg;base64,/9j/...
     */
    fun createAlbumArtUpdate(base64DataUrl: String): String {
        // 解析 Data URL 格式：data:[mime_type];base64,[base64_data]
        val dataUrlPattern = "^data:([^;]+);base64,(.*)$".toRegex()
        val matchResult = dataUrlPattern.find(base64DataUrl) ?: run {
            throw IllegalArgumentException("无效的 Base64 Data URL 格式：$base64DataUrl")
        }
        
        val mimeType = matchResult.groupValues[1]
        val base64Data = matchResult.groupValues[2]
        
        // 构建符合 V2 协议的消息格式
        // 根据 serde 的 tag 配置，AlbumCover::Data 会生成 source 和 image 字段
        // {"type":"state","value":{"update":"setCover","source":"data","image":{"mimeType":"...","data":"..."}}}
        return encode(MessageV2(
            type = "state",
            value = ValuePayload(
                update = "setCover",
                source = "data",
                image = ImageData(
                    mimeType = mimeType,
                    data = base64Data
                )
            )
        ))
    }
    

    
    // ==================== 编解码方法 ====================
    
    /**
     * 将消息编码为 JSON 字符串
     */
    fun encode(message: MessageV2): String {
        return try {
            json.encodeToString(MessageV2.serializer(), message)
        } catch (e: Exception) {
            throw RuntimeException("JSON 编码失败：${e.message}", e)
        }
    }
    
    /**
     * 解码 JSON 字符串为消息对象
     */
    fun decode(jsonString: String): MessageV2? {
        return try {
            json.decodeFromString(MessageV2.serializer(), jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
}
