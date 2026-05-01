package io.github.zeehan2005.scoremuse.data.repository

import android.content.Context
import io.github.zeehan2005.scoremuse.global.CachedLyricEntry
import kotlinx.serialization.json.Json
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
     * @return 按更新时间降序排列的列表（最新的在前）
     */
    fun getAll(): List<CachedLyricEntry> {
        return readAll().sortedByDescending { it.updatedAt }
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

        // Ensure at most one entry per song (title+artist).  If there are multiple
        // entries with different sources, remove them so we replace with the new one.
        val duplicates = all.withIndex().filter {
            normalize(it.value.title) == titleKey &&
                normalize(it.value.artist) == artistKey
        }.map { it.index }

        val entryId = if (duplicates.isNotEmpty()) {
            // keep the first duplicate's id
            all[duplicates.first()].id
        } else {
            UUID.randomUUID().toString()
        }

        // remove all existing duplicates first
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

    private companion object {
        private const val PREFS_NAME = "ScoreMuse_lyrics_cache"
        private const val KEY_CACHE_JSON = "lyrics_cache_json"
    }
}
