package io.github.zeehan2005.scoremuse.global

import android.content.Context
import io.github.zeehan2005.scoremuse.components.PreferenceHelper
import org.json.JSONArray
import org.json.JSONObject

/**
 * 卡片点击行为枚举
 *
 * 定义用户点击音乐卡片时的响应方式。
 *
 * **选项说明**：
 * - DIRECT_OPEN：直接打开歌词界面（快速操作）
 * - ASK：弹出对话框询问用户（默认，防止误触）
 * - NONE：不执行任何操作（禁用点击）
 */
enum class CardClickAction(val value: String) {
    DIRECT_OPEN("direct_open"),  // 直接打开
    ASK("ask"),                   // 询问用户
    NONE("none");                 // 无操作

    companion object {
        /**
         * 从字符串值转换为枚举
         * @param value 存储的字符串值
         * @return 对应的枚举项，无效时返回 ASK（默认）
         */
        fun fromValue(value: String?): CardClickAction {
            return entries.firstOrNull { it.value == value } ?: ASK
        }
    }
}

/**
 * 更新渠道枚举
 *
 * 定义应用接收更新的版本通道。
 *
 * **通道说明**：
 * - STABLE：稳定版（推荐普通用户使用）
 * - BETA：测试版（格式：X.X.X Beta Y）
 * - ALPHA：开发版（适合喜欢新功能的测试用户）
 */
enum class UpdateChannel(val value: String) {
    STABLE("stable"),     // 稳定版
    BETA("beta"),         // 测试版
    ALPHA("alpha");   // 开发版

    companion object {
        /**
         * 从字符串值转换为枚举
         * @param value 存储的字符串值
         * @return 对应的枚举项，无效时返回 STABLE（默认）
         */
        fun fromValue(value: String?): UpdateChannel {
            return entries.firstOrNull { it.value == value } ?: STABLE
        }
    }
}

/**
 * 应用全局设置管理
 *
 * 这个对象封装了所有用户可配置的应用设置，包括：
 * - 卡片点击行为
 * - 歌词通知开关
 * - 字体配置
 * - 自动更新策略
 * - WebSocket 协议配置
 * - 动画效果设置
 *
 * 所有设置都通过 SharedPreferences 持久化存储，
 * 支持在运行时动态读取和修改。
 */
object AppSettings {
    // 键名常量定义（避免硬编码字符串）
    private const val PREFS_NAME = "ScoreMuse_settings"  // SharedPreferences 名称
    private const val KEY_CARD_CLICK_ACTION = "card_click_action"  // 卡片点击行为
    private const val KEY_LYRIC_NOTIFICATION_ENABLED = "lyric_notification_enabled"  // 歌词通知开关
    private const val KEY_AUTO_UPDATE_CHECK_ENABLED = "auto_update_check_enabled"  // 自动检查更新
    private const val KEY_UPDATE_CHANNEL = "update_channel"  // 更新渠道（稳定版/测试版/开发版）
    private const val KEY_LAST_UPDATE_CHECK_AT = "last_update_check_at"  // 上次检查更新的时间戳
    private const val KEY_SKIP_PREVIOUS_REWINDS = "skip_previous_rewinds"  // 跳过上一首回退
    private const val KEY_PROCESS_METADATA_ENABLED = "process_metadata_enabled"  // 处理元数据开关
    private const val KEY_AGENT_RECOGNIZER_ENABLED = "agent_recognizer_enabled"  // Agent 识别器开关


    // 辅助函数：获取 SharedPreferences 实例（避免重复代码）
    private fun prefs(context: Context) =
        PreferenceHelper(context, PREFS_NAME)

    fun getCardClickAction(context: Context): CardClickAction {
        val value = prefs(context).getString(KEY_CARD_CLICK_ACTION, CardClickAction.ASK.value)
        return CardClickAction.fromValue(value)
    }

    fun setCardClickAction(context: Context, action: CardClickAction) {
        prefs(context).putString(KEY_CARD_CLICK_ACTION, action.value)
    }

    fun isLyricNotificationEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_LYRIC_NOTIFICATION_ENABLED, false)
    }

    fun setLyricNotificationEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_LYRIC_NOTIFICATION_ENABLED, enabled)
    }



    fun isAutoUpdateCheckEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_AUTO_UPDATE_CHECK_ENABLED, true)
    }

    fun setAutoUpdateCheckEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_AUTO_UPDATE_CHECK_ENABLED, enabled)
    }

    fun getUpdateChannel(context: Context): UpdateChannel {
        val value = prefs(context).getString(KEY_UPDATE_CHANNEL, UpdateChannel.STABLE.value)
        return UpdateChannel.fromValue(value)
    }

    fun setUpdateChannel(context: Context, channel: UpdateChannel) {
        prefs(context).putString(KEY_UPDATE_CHANNEL, channel.value)
    }

    fun setLastUpdateCheckAt(context: Context, timestampMillis: Long) {
        prefs(context).putLong(KEY_LAST_UPDATE_CHECK_AT, timestampMillis)
    }

    // time when user tapped “later” in update dialog; used to suppress automatic
    // checks for the next 24 hours.
    private const val KEY_LAST_UPDATE_LATER_AT = "last_update_later_at"

    fun getLastUpdateLaterAt(context: Context): Long {
        return prefs(context).getLong(KEY_LAST_UPDATE_LATER_AT, 0L)
    }

    fun setLastUpdateLaterAt(context: Context, timestampMillis: Long) {
        prefs(context).putLong(KEY_LAST_UPDATE_LATER_AT, timestampMillis)
    }

    fun isSkipPreviousRewindsEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_SKIP_PREVIOUS_REWINDS, false)
    }

    fun setSkipPreviousRewindsEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_SKIP_PREVIOUS_REWINDS, enabled)
    }

    fun isMetadataProcessingEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_PROCESS_METADATA_ENABLED, false)
    }


    // === 歌词时间轴偏移设置（基于歌曲 + 输出设备 + 音乐源） ===
    private const val KEY_LYRIC_TIMING_OFFSETS = "lyric_timing_offsets"
    private const val WILDCARD = "*"

    data class LyricTimingOffset(
        val title: String,
        val artist: String,
        val device: String,
        val source: String,
        val offsetMs: Long
    )

    fun getLyricTimingOffsets(context: Context): List<LyricTimingOffset> {
        val raw = prefs(context).getString(KEY_LYRIC_TIMING_OFFSETS, null)
        if (raw.isNullOrBlank()) return emptyList()

        return try {
            val json = JSONArray(raw)
            buildList {
                for (i in 0 until json.length()) {
                    val obj = json.optJSONObject(i) ?: continue
                    val title = obj.optString("title").trim().ifBlank { WILDCARD }
                    val artist = obj.optString("artist").trim().ifBlank { WILDCARD }
                    val device = obj.optString("device").trim().ifBlank { WILDCARD }
                    val source = obj.optString("source").trim().ifBlank { WILDCARD }
                    val offsetMs = obj.optLong("offsetMs", 0L)
                    add(LyricTimingOffset(title, artist, device, source, offsetMs))
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getLyricTimingOffset(
        context: Context,
        title: String?,
        artist: String?,
        device: String,
        source: String = WILDCARD
    ): Long? {
        // Normalize for lookup
        val normalizedTitle = title?.trim().takeIf { !it.isNullOrBlank() } ?: WILDCARD
        val normalizedArtist = artist?.trim().takeIf { !it.isNullOrBlank() } ?: WILDCARD
        val normalizedDevice = device.trim().ifBlank { WILDCARD }
        val normalizedSource = source.trim().ifBlank { WILDCARD }

        val entries = getLyricTimingOffsets(context)

        // Sum all matching entries (支持叠加)
        return entries
            .filter { entry ->
                (entry.title == WILDCARD || entry.title.equals(normalizedTitle, ignoreCase = true)) &&
                    (entry.artist == WILDCARD || entry.artist.equals(normalizedArtist, ignoreCase = true)) &&
                    (entry.device == WILDCARD || entry.device.equals(normalizedDevice, ignoreCase = true)) &&
                    (entry.source == WILDCARD || entry.source.equals(normalizedSource, ignoreCase = true))
            }
            .sumOf { it.offsetMs }
            .takeIf { it != 0L }
    }

    fun setLyricTimingOffset(
        context: Context,
        title: String,
        artist: String,
        device: String,
        offsetMs: Long,
        source: String = WILDCARD
    ) {
        val existing = getLyricTimingOffsets(context).toMutableList()
        val normalizedTitle = title.trim().ifBlank { WILDCARD }
        val normalizedArtist = artist.trim().ifBlank { WILDCARD }
        val normalizedDevice = device.trim().ifBlank { WILDCARD }
        val normalizedSource = source.trim().ifBlank { WILDCARD }
        val existingIndex = existing.indexOfFirst {
            it.title.equals(normalizedTitle, ignoreCase = true) &&
                it.artist.equals(normalizedArtist, ignoreCase = true) &&
                it.device.equals(normalizedDevice, ignoreCase = true) &&
                it.source.equals(normalizedSource, ignoreCase = true)
        }
        val entry = LyricTimingOffset(normalizedTitle, normalizedArtist, normalizedDevice, normalizedSource, offsetMs)
        if (existingIndex >= 0) {
            existing[existingIndex] = entry
        } else {
            existing.add(entry)
        }
        saveLyricTimingOffsets(context, existing)
    }

    fun removeLyricTimingOffset(
        context: Context,
        title: String,
        artist: String,
        device: String,
        source: String = WILDCARD
    ) {
        val existing = getLyricTimingOffsets(context).toMutableList()
        val normalizedTitle = title.trim().ifBlank { WILDCARD }
        val normalizedArtist = artist.trim().ifBlank { WILDCARD }
        val normalizedDevice = device.trim().ifBlank { WILDCARD }
        val normalizedSource = source.trim().ifBlank { WILDCARD }
        val remaining = existing.filterNot {
            it.title.equals(normalizedTitle, ignoreCase = true) &&
                it.artist.equals(normalizedArtist, ignoreCase = true) &&
                it.device.equals(normalizedDevice, ignoreCase = true) &&
                it.source.equals(normalizedSource, ignoreCase = true)
        }
        saveLyricTimingOffsets(context, remaining)
    }

    fun clearLyricTimingOffsets(context: Context) {
        saveLyricTimingOffsets(context, emptyList())
    }

    private fun saveLyricTimingOffsets(context: Context, entries: List<LyricTimingOffset>) {
        val json = JSONArray().apply {
            entries.forEach { entry ->
                put(
                    JSONObject().apply {
                        put("title", entry.title)
                        put("artist", entry.artist)
                        put("device", entry.device)
                        put("source", entry.source)
                        put("offsetMs", entry.offsetMs)
                    }
                )
            }
        }
        prefs(context).putString(KEY_LYRIC_TIMING_OFFSETS, json.toString())
    }
}