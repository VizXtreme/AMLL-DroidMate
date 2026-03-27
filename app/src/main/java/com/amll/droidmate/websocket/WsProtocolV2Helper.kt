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
        val update: String? = null,      // "setMusic", "progress", "paused", "resumed"
        val command: String? = null,     // "pause", "resume", etc.
        // MusicInfo 字段扁平化到这里
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
        val data: String? = null  // Base64 编码的数据或 JSON 字符串
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
        // 注意：歌词数据应该放在 data 字段中，使用 Base64 编码
        val base64Data = Base64.encodeToString(ttmlContent.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        return encode(MessageV2(type = "state", value = ValuePayload(update = "setLyric", format = "ttml", data = base64Data)))
    }
    
    @Serializable
    data class MusicInfo(
        val musicId: String,
        val musicName: String,
        val albumId: String = "",
        val albumName: String = "",
        val artists: List<Artist> = emptyList(),
        val duration: Long
    )
    
    @Serializable
    data class Artist(
        val id: String,
        val name: String
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
    
    // ==================== 编解码方法 ====================
    
    /**
     * 将消息编码为 JSON 字符串
     */
    fun encode(message: MessageV2): String {
        return try {
            json.encodeToString(MessageV2.serializer(), message)
        } catch (e: Exception) {
            throw RuntimeException("编码失败", e)
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
