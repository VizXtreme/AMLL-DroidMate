package com.amll.droidmate.websocket

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.util.Base64

/**
 * AMLL WebSocket V2 协议消息工具类
 */
object WsProtocolV2Helper {
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false  // 不编码默认值（null 字段）
    }
    
    // ==================== V2 协议消息类型 ====================
    
    /**
     * 顶层消息结构 - 使用扁平化设计
     */
    @Serializable
    data class MessageV2(
        val type: String,  // "initialize", "ping", "pong", "command", "state"
        val value: ValuePayload? = null
    )
    
    @Serializable
    data class ValuePayload(
        val update: String? = null,      // "setMusic", "progress", "paused", "resumed", "setLyric", "setCover"
        val command: String? = null,     // "pause", "resume", etc.
        // MusicInfo 字段（扁平化，直接放在 value 层级）
        val musicId: String? = null,
        val musicName: String? = null,
        val albumId: String? = null,
        val albumName: String? = null,
        val artists: List<Artist>? = null,
        val duration: Long? = null,
        // 其他字段
        val progress: Long? = null,
        val volume: Double? = null,
        val format: String? = null,
        val ttml: String? = null,
        val lines: List<LyricLine>? = null,
        val repeat: String? = null,
        val shuffle: Boolean? = null,
        val data: String? = null,  // Base64 编码的数据或 JSON 字符串
        // 专辑封面相关字段（用于 setCover）
        val source: String? = null,  // "uri" 或 "data"
        val url: String? = null,     // URI 模式的 URL
        val image: ImageData? = null  // Data 模式的嵌套图片对象
    )
    
    /**
     * 专辑图片数据（嵌套结构，用于 AlbumCover::Data）
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
            encode(MessageV2(type = "state", value = ValuePayload(update = "setLyric", format = "ttml", data = ttmlContent)))
        } catch (e: Exception) {
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
