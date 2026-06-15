package io.github.zeehan2005.scoremuse.global

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

/**
 * 当前播放的音乐信息
 *
 * 这个数据类封装了从系统媒体会话中获取的当前播放音乐的所有信息。
 * 它用于在应用内部传递和展示当前播放的歌曲详情。
 */
@Stable
@Serializable
data class NowPlayingMusic(
    val title: String,              // 歌曲标题
    val artist: String,             // 艺术家/歌手
    val album: String? = null,      // 专辑名称（可选）
    val duration: Long = 0L,        // 歌曲总时长（毫秒）
    val currentPosition: Long = 0L, // 当前播放位置（毫秒）
    val isPlaying: Boolean = false, // 是否正在播放
    val packageName: String? = null, // 播放源应用的包名
    val albumArtUri: String? = null, // 专辑封面图片的 URI
    val timestamp: Long = System.currentTimeMillis()  // 记录时间戳（用于缓存验证）
)

/**
 * 单个词的信息（逐字歌词的基本单位）
 *
 * 这个数据类表示歌词中的一个单词或音节，包含精确的时间信息。
 * 用于实现逐字高亮效果（类似 Apple Music 的歌词显示方式）。
 */
@Stable
@Serializable
data class LyricWord(
    val word: String,           // 歌词文本（可以是一个字、一个词或带空格的短语）
    val startTime: Long,        // 开始时间（毫秒）
    val endTime: Long           // 结束时间（毫秒）
)

/**
 * 歌词行信息
 *
 * 这是歌词显示的核心数据结构，代表一行完整的歌词。
 * 每行歌词可以包含：
 * - 主歌词文本
 * - 翻译（例如中文翻译）
 * - 音译（例如日文歌词的假名注音）
 * - 逐词时间信息（用于逐字高亮）
 * - 特殊标记（背景音、合唱等）
 */
@Stable
@Serializable
data class LyricLine(
    val startTime: Long,            // 行开始时间（毫秒）
    val endTime: Long,              // 行结束时间（毫秒）
    val text: String,               // 主歌词文本
    val translation: String? = null,   // 翻译（可选）
    val transliteration: String? = null,  // 音译（可选）
    val words: List<LyricWord> = emptyList(),  // 逐词信息（用于逐字高亮）
    val isBG: Boolean = false,      // 是否为背景音声（例如和声、伴唱）
    val isDuet: Boolean = false,    // 是否为合唱（双人/多人演唱部分）
    val agent: String? = null       // 原始 agent 信息（用于 TTML 导出，标识演唱者）
)

/**
 * 统一歌词结构
 *
 * 这个数据类用于在应用内部表示统一的歌词格式，方便在不同格式之间转换。
 */
@Stable
@Serializable
data class UnifiedLyrics(
    /** 元数据（歌名、歌手、专辑等）*/
    val metadata: LyricsMetadata,
    /** 所有歌词行 */
    val lines: List<LyricLine>,
    /** 保留原始歌词字符串，用于 WebView 直接解析或 WebSocket 发送*/
    val rawContent: String? = null,
    /** 原始格式（可选，如 "ttml", "lrc", "yrc"）*/
    val format: String? = null,
    /** 兼容字段：保留原始 TTML  */
    val rawTtml: String? = null
)

/**
 * 歌词元数据
 *
 * 包含歌曲的附加信息，这些信息通常来自歌词文件的头部元数据。
 */
@Stable
@Serializable
data class LyricsMetadata(
    /** 歌曲标题 */
    val title: String,
    /** 艺术家 */
    val artist: String,
    /** 专辑名称（可选）*/
    val album: String? = null,
    /** 语言代码（可选）*/
    val language: String? = null,
    /** 歌曲总时长（毫秒）*/
    val duration: Long = 0L,
    /** 来源标识 */
    val source: String = "ScoreMuse",
    /** 歌曲结构段落（主歌、副歌等） */
    val songStructures: List<SongStructure>? = null,
    /** 保留原始 XML 的完整 metadata 元素内容（用于未来扩展和保留未使用的 XML 信息）*/
    val rawXmlMetadata: String? = null,
    /** 标记是否为 fallback 结果（true 表示是从其他格式转换而来，非原始格式）*/
    val isFallback: Boolean = false
)

/**
 * 歌曲结构段落
 *
 * 表示歌曲中某个特定段落的详细信息，包括时间范围。
 * label 字段直接保留原始 songpart 值（"Verse"、"Chorus"、"Tag" 等），
 * 前奏/间奏/尾奏等纯音乐段落统一标记为 "Interlude"。
 */
@Stable
@Serializable
data class SongStructure(
    val label: String,        // 段落标签（例如 "Verse 1", "Chorus", "Interlude"）
    val startTime: Long,      // 段落开始时间（毫秒）
    val endTime: Long         // 段落结束时间（毫秒）
) {
    /** 计算段落持续时间 */
    val duration: Long
        get() = endTime - startTime
}

/**
 * 支持的功能（用于 UI 提示）
 *
 * 这个枚举列出了歌词可能支持的各种高级功能，
 * UI 可以根据这些功能显示相应的图标或提示。
 */
enum class LyricsFeature(val displayName: String) {
    DUET("对唱"),           // 多人合唱
    BACKGROUND("背景"),     // 背景音声
    OVERLAP("重叠"),        // 重叠歌词（同时显示多行）
    TRANSLATION("翻译"),    // 多语言翻译
    TRANSLITERATION("音译"), // 发音标注（假名、拼音等）
    WORDS("逐字"),          // 逐字高亮
    STRUCTURE("结构标记")   // 歌词结构标记（主歌、副歌等段落结构，仅在解析 TTML 格式并检测到有效的 songPart 元数据时启用）
}

/**
 * 歌词搜索结果
 *
 * 当用户搜索歌词时，返回的可能有多首匹配的歌曲。
 * 这个数据类表示其中一首歌曲的搜索结果。
 */
@Stable
@Serializable
data class LyricsSearchResult(
    val provider: String,   // 来源："qq" (QQ 音乐), "netease" (网易云)等
    val songId: String,     // 歌曲 ID（用于后续获取歌词）
    val title: String,      // 歌曲标题
    val artist: String,     // 艺术家
    val album: String? = null,  // 专辑（可选）
    val confidence: Float = 0f,  // 匹配度 0-1（1 表示完全匹配）
    /**
     * 匹配分档字符串（仅用于调试）。
     * UI 中不再展示这些词，如 "PERFECT"、"VERY_HIGH" 等。
     */
    val matchType: String = "",
    /**
     * Indicates this result was obtained via a metadata-based search (title/artist),
     * rather than directly via a known ID.
     */
    val metadataMatch: Boolean = false  // 是否通过元数据（标题/艺术家）匹配获得
)

/**
 * 歌词获取结果
 *
 * 封装了获取歌词的最终结果，包括成功/失败状态和具体的歌词数据。
 */
@Stable
@Serializable
data class LyricsResult(
    val isSuccess: Boolean,       // 是否成功获取
    val lyrics: UnifiedLyrics? = null,  // 歌词数据（如果成功）
    val errorMessage: String? = null,  // 错误信息（如果失败）
    val source: String? = null    // 歌词来源
)

/**
 * 本地缓存歌词条目
 *
 * 为了减少网络请求并提高加载速度，获取到的歌词会被缓存在本地。
 * 这个数据类表示缓存中的一条记录。
 */
@Stable
@Serializable
data class CachedLyricEntry(
    val id: String,           // 唯一标识符
    val title: String,        // 歌曲标题
    val artist: String,       // 艺术家
    val source: String,       // 来源平台
    val xmlContent: String,  // XML 格式的歌词内容
    val updatedAt: Long       // 最后更新时间（毫秒时间戳）
)