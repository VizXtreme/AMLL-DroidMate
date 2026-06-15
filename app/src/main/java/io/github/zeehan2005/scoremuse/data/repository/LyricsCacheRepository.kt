package io.github.zeehan2005.scoremuse.data.repository

import android.content.Context
import io.github.zeehan2005.scoremuse.global.CachedLyricEntry
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.Map.entry
import java.util.UUID

/**
 * 歌词缓存仓库
 * 
 * 负责管理本地歌词缓存，包括：
 * - 存储获取到的歌词（避免重复网络请求）
 * - 搜索缓存的歌词
 * - 根据歌曲信息查询缓存
 * 
 * 使用 SharedPreferences 持久化存储，
 * 支持添加、查询、更新、删除操作。
 */
class LyricsCacheRepository(context: Context) {
    private val prefs = io.github.zeehan2005.scoremuse.components.PreferenceHelper(context, PREFS_NAME)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * 获取所有缓存的歌词条目
     *
     * 同一首歌（normalize 后的 title+artist）只保留最新一条，
     * 防止历史遗留的重复条目出现在 UI 中。
     *
     * 额外清理旧版 [LyricsRepository.getLyrics] 遗留的"内层缓存"条目。
     * 该函数曾以搜索候选结果的 title/artist + 原始提供者名（如 "KUGOU"）写入缓存，
     * 而调用方又以系统媒体信息的 title/artist + 格式化来源名（如 "酷狗音乐"）再次写入。
     * 当两者 title/artist 不一致时，同一首歌产生了两个缓存条目。
     * 此处删除被规范化条目替代了的旧原始提供者名条目。
     *
     * @return 按更新时间降序排列的列表（最新的在前）
     */
    fun getAll(): List<CachedLyricEntry> {
        val all = readAll()
        // 读取时去重：同  title+artist 只保留 updatedAt 最新的
        val seen = mutableSetOf<String>()
        val deduped = all.sortedByDescending { it.updatedAt }.filter { entry ->
            val key = "${normalize(entry.title)}|${normalize(entry.artist)}"
            seen.add(key)
        }
        return cleanupStaleRawProviderEntries(all, deduped)
    }

    /**
     * 清理旧版 getLyrics() 遗留的原始提供者名缓存条目。
     *
     * 在此修复之前，getLyrics() 会以 provider.uppercase()（如 "KUGOU"、"QQ"）作为 source
     * 写入缓存，而调用方随后又以 formatAutoSource 的格式化名称（如 "酷狗音乐"）写入。
     * 若搜索候选结果中的 title/artist 与系统媒体信息不同，则产生两条缓存——
     * 一条 source 为原始提供者名，另一条为格式化来源名。
     *
     * 此方法识别这些被替代的旧条目并将其从持久化存储中删除。
     */
    private fun cleanupStaleRawProviderEntries(
        all: List<CachedLyricEntry>,
        visible: List<CachedLyricEntry>
    ): List<CachedLyricEntry> {
        val rawProviders = setOf("AMLL", "NETEASE", "NCM", "QQ", "QQMUSIC", "KUGOU")
        val toRemove = mutableSetOf<String>()

        // 遍历所有可见条目，找出那些 source 是原始提供者名的
        for (entry in visible) {
            if (entry.source.uppercase() in rawProviders) {
                // 检查是否有另一条"规范化"条目（非原始提供者名 source）
                // 且属于同一首歌（artist 相同、title 通过后缀剥离后匹配）
                val hasCanonical = visible.any { other ->
                    other.id != entry.id &&
                        normalize(other.artist) == normalize(entry.artist) &&
                        titleMatchesAfterStrippingSuffix(normalize(entry.title), normalize(other.title))
                }
                if (hasCanonical) {
                    toRemove.add(entry.id)
                }
            }
        }

        if (toRemove.isNotEmpty()) {
            Timber.i("[LyricsCache] Removing ${toRemove.size} stale cache entries with raw provider source: $toRemove")
            val cleaned = all.filter { it.id !in toRemove }
            writeAll(cleaned)
            return visible.filter { it.id !in toRemove }
        }

        return visible
    }

    /**
     * 判断两个标题在剥离常见后缀后是否相同。
     *
     * 不同歌词来源可能在搜索结果中附加 "(Live)"、"(Remix)" 等后缀，
     * 导致同一首歌在缓存中出现不同标题。此方法剥离这些后缀后比较。
     */
    private fun titleMatchesAfterStrippingSuffix(a: String, b: String): Boolean {
        if (a == b) return true
        val stripped = a.replace(SUFFIX_PATTERN, "").trimEnd()
        val strippedOther = b.replace(SUFFIX_PATTERN, "").trimEnd()
        return stripped == strippedOther
    }

    private companion object {
        private const val PREFS_NAME = "ScoreMuse_lyrics_cache"
        private const val KEY_CACHE_JSON = "lyrics_cache_json"

        /** 匹配尾部括号后缀，如 (live)、(remix)、(feat.xxx) 等 */
        private val SUFFIX_PATTERN = Regex("""\s*\([^)]*\)\s*$""", RegexOption.IGNORE_CASE)
    }

    /**
     * 搜索缓存的歌词
     * 
     * 支持模糊匹配歌名、艺术家或来源
     * 
     * @param query 搜索关键词
     * @return 匹配的歌词列表，如果查询为空则返回全部
     */
    fun search(query: String): List<CachedLyricEntry> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return getAll()

        return getAll().filter {
            it.title.lowercase().contains(q) ||
                it.artist.lowercase().contains(q) ||
                it.source.lowercase().contains(q)
        }
    }

    /**
     * 根据歌曲信息查询缓存
     * 
     * 会归一化处理歌名和艺术家（忽略大小写和空格差异），
     * 返回最新更新的匹配条目。
     * 
     * @param title 歌曲标题
     * @param artist 艺术家名称
     * @return 匹配的缓存条目，如果没有则返回 null
     */
    fun findBySong(title: String, artist: String): CachedLyricEntry? {
        val titleKey = normalize(title)
        val artistKey = normalize(artist)
        return readAll()
            .filter { normalize(it.title) == titleKey && normalize(it.artist) == artistKey }
            .maxByOrNull { it.updatedAt }
    }

    /**
     * 插入或更新歌词缓存
     * 
     * 这个函数确保每首歌（相同标题 + 艺术家）只保留一个缓存条目。
     * 如果发现多个来源的重复条目，会全部替换为新的条目。
     * 
     * @param title 歌曲标题
     * @param artist 艺术家
     * @param source 来源平台（qq、netease 等）
     * @param xmlContent TTML 格式的歌词内容
     */
    fun upsert(
        title: String,
        artist: String,
        source: String,
        xmlContent: String
    ) {
        if (xmlContent.isBlank()) return

        val all = readAll().toMutableList()
        val titleKey = normalize(title)
        val artistKey = normalize(artist)

        /** Ensure at most one entry per song (title+artist).
         * If there are multiple entries with different sources, remove them so we replace with the new one. */
        val duplicates = all.withIndex().filter {
            normalize(it.value.title) == titleKey &&
                normalize(it.value.artist) == artistKey
        }.map { it.index }

        val entryId = if (duplicates.isNotEmpty()) {
            /** keep the first duplicate's id */
            all[duplicates.first()].id
        } else {
            UUID.randomUUID().toString()
        }

        /** remove all existing duplicates first */
        duplicates.sortedDescending().forEach { all.removeAt(it) }

        val newEntry = CachedLyricEntry(
            id = entryId,
            title = title,
            artist = artist,
            source = source,
            xmlContent = xmlContent,
            updatedAt = System.currentTimeMillis()
        )

        all.add(newEntry)

        writeAll(all)
    }

    fun deleteById(id: String) {
        val all = readAll().toMutableList()
        all.removeAll { it.id == id }
        writeAll(all)
    }

    fun clearAll() {
        prefs.remove(KEY_CACHE_JSON)
    }

    private fun readAll(): List<CachedLyricEntry> {
        val raw = prefs.getString(KEY_CACHE_JSON, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<CachedLyricEntry>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun writeAll(entries: List<CachedLyricEntry>) {
        prefs.putString(KEY_CACHE_JSON, json.encodeToString(entries))
    }

    private fun normalize(value: String): String {
        return value.trim().lowercase()
    }
}
