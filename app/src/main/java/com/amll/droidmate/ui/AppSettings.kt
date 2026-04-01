package com.amll.droidmate.ui

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import com.amll.droidmate.util.PreferenceHelper

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
 * - PREVIEW：预览版（适合喜欢新功能的测试用户）
 */
enum class UpdateChannel(val value: String) {
    STABLE("stable"),     // 稳定版
    PREVIEW("preview");   // 预览版

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
    private const val PREFS_NAME = "droidmate_settings"  // SharedPreferences 名称
    private const val KEY_CARD_CLICK_ACTION = "card_click_action"  // 卡片点击行为
    private const val KEY_LYRIC_NOTIFICATION_ENABLED = "lyric_notification_enabled"  // 歌词通知开关
    private const val KEY_AMLL_FONT_FAMILY = "amll_font_family"  // AMLL 字体族
    private const val KEY_AMLL_FONT_FILE_PATH = "amll_font_file_path"  // 字体文件路径
    private const val KEY_AMLL_FONT_FILE_NAME = "amll_font_file_name"  // 字体文件名
    private const val KEY_AMLL_FONT_FILES = "amll_font_files"  // 已安装的字体列表
    private const val KEY_AMLL_ACTIVE_FONT_ID = "amll_active_font_id"  // 当前激活的字体 ID
    private const val KEY_AMLL_ENABLED_FONT_IDS = "amll_enabled_font_ids"  // 启用的字体 ID 列表
    private const val KEY_AUTO_UPDATE_CHECK_ENABLED = "auto_update_check_enabled"  // 自动检查更新
    private const val KEY_UPDATE_CHANNEL = "update_channel"  // 更新渠道（稳定版/预览版）
    private const val KEY_LAST_UPDATE_CHECK_AT = "last_update_check_at"  // 上次检查更新的时间戳
    private const val KEY_SKIP_PREVIOUS_REWINDS = "skip_previous_rewinds"  // 跳过上一首回退
    private const val KEY_PROCESS_METADATA_ENABLED = "process_metadata_enabled"  // 处理元数据开关
    private const val KEY_AGENT_RECOGNIZER_ENABLED = "agent_recognizer_enabled"  // Agent 识别器开关

    // 动画相关设置（控制歌词运动和动画效果）
    private const val KEY_AMLL_ANIMATION_ENABLE_SPRING = "amll_animation_enable_spring"  // 启用弹簧动画
    private const val KEY_AMLL_ANIMATION_ENABLE_SCALE = "amll_animation_enable_scale"  // 启用缩放效果
    private const val KEY_AMLL_ANIMATION_ENABLE_BLUR = "amll_animation_enable_blur"  // 启用模糊效果
    private const val KEY_AMLL_ANIMATION_HIDE_PASSED_LINES = "amll_animation_hide_passed_lines"  // 隐藏已唱过的歌词行
    private const val KEY_AMLL_ANIMATION_WORD_FADE_WIDTH = "amll_animation_word_fade_width"  // 逐字渐变宽度
    private const val KEY_AMLL_ANIMATION_FPS = "amll_animation_fps"  // 动画帧率
    private const val KEY_WEBSOCKET_PROTOCOL_ADDRESS = "websocket_protocol_address"  // WebSocket 地址
    private const val KEY_WEBSOCKET_PROTOCOL_ENABLED = "websocket_protocol_enabled"  // WebSocket 开关
    private const val KEY_WEBVIEW_ENABLED = "webview_enabled"  // WebView 开关

    // 默认值常量（当用户未设置时使用）
    private const val DEFAULT_AMLL_ANIMATION_ENABLE_SPRING = true  // 默认启用弹簧动画
    private const val DEFAULT_AMLL_ANIMATION_ENABLE_SCALE = true   // 默认启用缩放
    private const val DEFAULT_AMLL_ANIMATION_ENABLE_BLUR = true    // 默认启用模糊
    private const val DEFAULT_AMLL_ANIMATION_HIDE_PASSED_LINES = false  // 默认不隐藏已唱过的行
    private const val DEFAULT_AMLL_ANIMATION_WORD_FADE_WIDTH = 0.5f     // 默认渐变宽度 0.5
    private const val DEFAULT_AMLL_ANIMATION_FPS = 60  // 默认 60 FPS
    private const val DEFAULT_WEBSOCKET_PROTOCOL_ADDRESS = "ws://localhost:11444"  // 默认本地地址
    private const val DEFAULT_WEBSOCKET_PROTOCOL_ENABLED = false  // 默认关闭
    private const val DEFAULT_WEBVIEW_ENABLED = true  // 默认启用 WebView

    // 辅助函数：获取 SharedPreferences 实例（避免重复代码）
    private fun prefs(context: Context) =
        PreferenceHelper(context, PREFS_NAME)

    // 系统字体默认值（跨平台兼容字体栈）
    private const val DEFAULT_AMLL_FONT_FAMILY = "-apple-system, BlinkMacSystemFont, \"SF Pro Display\", Inter, \"PingFang SC\", system-ui, sans-serif"

    /**
     * AMLL 自定义字体文件
     * 
     * 用于存储用户安装的第三方字体信息。
     * 每个字体包含唯一 ID、显示名称、完整路径和字体族名称。
     * 
     * **用途说明**：
     * - id: 唯一标识符（UUID），用于在配置中引用
     * - displayName: 用户可见的字体名称（如 "思源黑体"）
     * - absolutePath: 字体文件的绝对路径（用于加载字体）
     * - fontFamilyName: 字体族名称（CSS font-family 使用）
     * 
     * @param id 唯一标识符（UUID）
     * @param displayName 用户可见的字体名称
     * @param absolutePath 字体文件的绝对路径
     * @param fontFamilyName 字体族名称
     * @param fontFamilyName CSS font-family 使用的名称
     */
    data class AmllFontFile(
        val id: String,
        val displayName: String,
        val absolutePath: String,
        val fontFamilyName: String
    )

    fun getDefaultAmllFontFamily(): String = DEFAULT_AMLL_FONT_FAMILY

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

    fun getAmllFontFamily(context: Context): String {
        return prefs(context).getString(KEY_AMLL_FONT_FAMILY, DEFAULT_AMLL_FONT_FAMILY)
            ?: DEFAULT_AMLL_FONT_FAMILY
    }

    fun setAmllFontFamily(context: Context, fontFamily: String) {
        prefs(context).putString(KEY_AMLL_FONT_FAMILY, fontFamily)
    }

    fun getAmllFontFilePath(context: Context): String? {
        return getActiveAmllFontFile(context)?.absolutePath
    }

    fun getAmllFontFileName(context: Context): String? {
        return getActiveAmllFontFile(context)?.displayName
    }

    fun setAmllFontFile(context: Context, absolutePath: String, displayName: String) {
        val updatedList = upsertAmllFontFile(
            context = context,
            absolutePath = absolutePath,
            displayName = displayName
        )
        val added = updatedList.firstOrNull { it.absolutePath == absolutePath }
        if (added != null) {
            setActiveAmllFontFileId(context, added.id)
        }
    }

    fun clearAmllFontFile(context: Context) {
        prefs(context).edit {
            remove(KEY_AMLL_ACTIVE_FONT_ID)
            remove(KEY_AMLL_ENABLED_FONT_IDS)
            remove(KEY_AMLL_FONT_FILE_PATH)
            remove(KEY_AMLL_FONT_FILE_NAME)
        }
    }

    fun getAmllFontFiles(context: Context): List<AmllFontFile> {
        val helper = prefs(context)
        val raw = helper.getString(KEY_AMLL_FONT_FILES, null)
        if (raw.isNullOrBlank()) {
            val legacyPath = helper.getString(KEY_AMLL_FONT_FILE_PATH, null)
            val legacyName = helper.getString(KEY_AMLL_FONT_FILE_NAME, null)
            if (!legacyPath.isNullOrBlank()) {
                val fallbackName = legacyName ?: "Imported Font"
                return listOf(
                    AmllFontFile(
                        id = stableFontId(legacyPath),
                        displayName = fallbackName,
                        absolutePath = legacyPath,
                        fontFamilyName = buildFontFamilyName(fallbackName, legacyPath)
                    )
                )
            }
            return emptyList()
        }

        return try {
            val json = JSONArray(raw)
            buildList {
                for (i in 0 until json.length()) {
                    val item = json.optJSONObject(i) ?: continue
                    val id = item.optString("id")
                    val displayName = item.optString("displayName")
                    val absolutePath = item.optString("absolutePath")
                    val fontFamilyName = item.optString("fontFamilyName")
                    if (id.isBlank() || absolutePath.isBlank()) continue
                    add(
                        AmllFontFile(
                            id = id,
                            displayName = if (displayName.isBlank()) "Imported Font" else displayName,
                            absolutePath = absolutePath,
                            fontFamilyName = if (fontFamilyName.isBlank()) {
                                buildFontFamilyName(displayName, absolutePath)
                            } else {
                                fontFamilyName
                            }
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun setAmllFontFiles(context: Context, files: List<AmllFontFile>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = JSONArray().apply {
            files.forEach { file ->
                put(
                    JSONObject().apply {
                        put("id", file.id)
                        put("displayName", file.displayName)
                        put("absolutePath", file.absolutePath)
                        put("fontFamilyName", file.fontFamilyName)
                    }
                )
            }
        }
        prefs.edit()
            .putString(KEY_AMLL_FONT_FILES, json.toString())
            .apply()
    }

    fun upsertAmllFontFile(context: Context, absolutePath: String, displayName: String): List<AmllFontFile> {
        val existing = getAmllFontFiles(context).toMutableList()
        val existingIndex = existing.indexOfFirst { it.absolutePath == absolutePath }
        val next = AmllFontFile(
            id = stableFontId(absolutePath),
            displayName = displayName,
            absolutePath = absolutePath,
            fontFamilyName = buildFontFamilyName(displayName, absolutePath)
        )

        if (existingIndex >= 0) {
            existing[existingIndex] = next
        } else {
            existing.add(next)
        }

        setAmllFontFiles(context, existing)
        return existing
    }

    fun removeAmllFontFile(context: Context, fileId: String): List<AmllFontFile> {
        val remaining = getAmllFontFiles(context).filterNot { it.id == fileId }
        setAmllFontFiles(context, remaining)

        val activeId = getActiveAmllFontFileId(context)
        if (activeId == fileId) {
            setActiveAmllFontFileId(context, null)
        }

        val enabled = getEnabledAmllFontFileIds(context).filterNot { it == fileId }
        setEnabledAmllFontFileIds(context, enabled)
        return remaining
    }

    fun getActiveAmllFontFileId(context: Context): String? {
        val helper = prefs(context)
        val activeId = helper.getString(KEY_AMLL_ACTIVE_FONT_ID, null)
        if (!activeId.isNullOrBlank()) return activeId

        val legacyPath = helper.getString(KEY_AMLL_FONT_FILE_PATH, null)
        return legacyPath?.takeIf { it.isNotBlank() }?.let(::stableFontId)
    }

    fun setActiveAmllFontFileId(context: Context, fileId: String?) {
        val helper = prefs(context)
        if (fileId.isNullOrBlank()) {
            helper.remove(KEY_AMLL_ACTIVE_FONT_ID)
        } else {
            helper.putString(KEY_AMLL_ACTIVE_FONT_ID, fileId)
        }
    }

    fun getEnabledAmllFontFileIds(context: Context): List<String> {
        val helper = prefs(context)
        val raw = helper.getString(KEY_AMLL_ENABLED_FONT_IDS, null)
        if (raw.isNullOrBlank()) {
            val legacyActive = getActiveAmllFontFileId(context)
            return if (legacyActive.isNullOrBlank()) emptyList() else listOf(legacyActive)
        }

        return try {
            val json = JSONArray(raw)
            buildList {
                for (i in 0 until json.length()) {
                    val id = json.optString(i)
                    if (id.isNotBlank()) add(id)
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun setEnabledAmllFontFileIds(context: Context, fileIds: List<String>) {
        val normalized = fileIds.filter { it.isNotBlank() }.distinct()
        val json = JSONArray().apply {
            normalized.forEach { put(it) }
        }
        prefs(context).putString(KEY_AMLL_ENABLED_FONT_IDS, json.toString())
    }

    fun getActiveAmllFontFile(context: Context): AmllFontFile? {
        val fonts = getAmllFontFiles(context)
        if (fonts.isEmpty()) return null

        val activeId = getActiveAmllFontFileId(context)
        return fonts.firstOrNull { it.id == activeId }
    }

    fun resetAmllFontSettings(context: Context) {
        setAmllFontFamily(context, DEFAULT_AMLL_FONT_FAMILY)
        clearAmllFontFile(context)
    }

    private fun stableFontId(absolutePath: String): String {
        return "font_" + absolutePath.hashCode().toUInt().toString(16)
    }

    private fun buildFontFamilyName(displayName: String, absolutePath: String): String {
        return displayName
            .substringBeforeLast('.')
            .ifBlank { absolutePath.substringAfterLast('/').substringAfterLast('\\').substringBeforeLast('.') }
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

    fun getLastUpdateCheckAt(context: Context): Long {
        return prefs(context).getLong(KEY_LAST_UPDATE_CHECK_AT, 0L)
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

    fun setMetadataProcessingEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_PROCESS_METADATA_ENABLED, enabled)
    }

    fun isAgentRecognizerEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_AGENT_RECOGNIZER_ENABLED, false)
    }

    fun setAgentRecognizerEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_AGENT_RECOGNIZER_ENABLED, enabled)
    }

    fun isAmllAnimationSpringEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_AMLL_ANIMATION_ENABLE_SPRING, DEFAULT_AMLL_ANIMATION_ENABLE_SPRING)
    }

    fun setAmllAnimationSpringEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_AMLL_ANIMATION_ENABLE_SPRING, enabled)
    }

    fun isAmllAnimationScaleEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_AMLL_ANIMATION_ENABLE_SCALE, DEFAULT_AMLL_ANIMATION_ENABLE_SCALE)
    }

    fun setAmllAnimationScaleEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_AMLL_ANIMATION_ENABLE_SCALE, enabled)
    }

    fun isAmllAnimationBlurEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_AMLL_ANIMATION_ENABLE_BLUR, DEFAULT_AMLL_ANIMATION_ENABLE_BLUR)
    }

    fun setAmllAnimationBlurEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_AMLL_ANIMATION_ENABLE_BLUR, enabled)
    }

    fun isAmllAnimationHidePassedLinesEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_AMLL_ANIMATION_HIDE_PASSED_LINES, DEFAULT_AMLL_ANIMATION_HIDE_PASSED_LINES)
    }

    fun setAmllAnimationHidePassedLinesEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_AMLL_ANIMATION_HIDE_PASSED_LINES, enabled)
    }

    fun getAmllAnimationWordFadeWidth(context: Context): Float {
        return prefs(context).getString(KEY_AMLL_ANIMATION_WORD_FADE_WIDTH, DEFAULT_AMLL_ANIMATION_WORD_FADE_WIDTH.toString())
            ?.toFloatOrNull() ?: DEFAULT_AMLL_ANIMATION_WORD_FADE_WIDTH
    }

    fun setAmllAnimationWordFadeWidth(context: Context, value: Float) {
        prefs(context).putString(KEY_AMLL_ANIMATION_WORD_FADE_WIDTH, value.toString())
    }

    fun getAmllAnimationFps(context: Context): Int {
        return prefs(context).getLong(KEY_AMLL_ANIMATION_FPS, DEFAULT_AMLL_ANIMATION_FPS.toLong()).toInt()
    }

    fun setAmllAnimationFps(context: Context, value: Int) {
        prefs(context).putLong(KEY_AMLL_ANIMATION_FPS, value.toLong())
    }

    // === WebSocket 传递设置 ===
    fun getWebSocketProtocolAddress(context: Context): String {
        return prefs(context).getString(KEY_WEBSOCKET_PROTOCOL_ADDRESS, DEFAULT_WEBSOCKET_PROTOCOL_ADDRESS)
            ?: DEFAULT_WEBSOCKET_PROTOCOL_ADDRESS
    }

    fun setWebSocketProtocolAddress(context: Context, address: String) {
        prefs(context).putString(KEY_WEBSOCKET_PROTOCOL_ADDRESS, address)
    }

    fun isWebSocketProtocolEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_WEBSOCKET_PROTOCOL_ENABLED, DEFAULT_WEBSOCKET_PROTOCOL_ENABLED)
    }

    fun setWebSocketProtocolEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_WEBSOCKET_PROTOCOL_ENABLED, enabled)
    }

    // === WebView 全局开关 ===
    fun isWebViewEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_WEBVIEW_ENABLED, DEFAULT_WEBVIEW_ENABLED)
    }

    fun setWebViewEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_WEBVIEW_ENABLED, enabled)
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
