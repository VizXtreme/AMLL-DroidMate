package dev.amll.droidmate.global

import android.content.Context
import android.content.SharedPreferences
import io.github.zeehan2005.scoremuse.components.PreferenceHelper
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.edit



object AMLLSettings {
    private const val PREFS_NAME = "droidmate_settings"  // SharedPreferences 名称
    private const val KEY_AMLL_FONT_FAMILY = "amll_font_family"  // AMLL 字体族
    private const val KEY_AMLL_FONT_FILE_PATH = "amll_font_file_path"  // 字体文件路径
    private const val KEY_AMLL_FONT_FILE_NAME = "amll_font_file_name"  // 字体文件名
    private const val KEY_AMLL_FONT_FILES = "amll_font_files"  // 已安装的字体列表
    private const val KEY_AMLL_ACTIVE_FONT_ID = "amll_active_font_id"  // 当前激活的字体 ID
    private const val KEY_AMLL_ENABLED_FONT_IDS = "amll_enabled_font_ids"  // 启用的字体 ID 列表
    private const val KEY_PROCESS_METADATA_ENABLED = "process_metadata_enabled"  // 处理元数据开关
    private const val KEY_AGENT_RECOGNIZER_ENABLED = "agent_recognizer_enabled"  // Agent 识别器开关
    // 动画相关设置（控制歌词运动和动画效果）
    private const val KEY_AMLL_ANIMATION_ENABLE_SPRING = "amll_animation_enable_spring"  // 启用弹簧动画
    private const val KEY_AMLL_ANIMATION_ENABLE_SCALE = "amll_animation_enable_scale"  // 启用缩放效果
    private const val KEY_AMLL_ANIMATION_ENABLE_BLUR = "amll_animation_enable_blur"  // 启用模糊效果
    private const val KEY_AMLL_ANIMATION_HIDE_PASSED_LINES = "amll_animation_hide_passed_lines"  // 隐藏已唱过的歌词行
    private const val KEY_AMLL_ANIMATION_WORD_FADE_WIDTH = "amll_animation_word_fade_width"  // 逐字渐变宽度
    private const val KEY_AMLL_ANIMATION_FPS = "amll_animation_fps"  // 动画帧率
    // 物理弹簧参数（分轴设置）
    private const val KEY_AMLL_SPRING_POSY_MASS = "amll_spring_posy_mass"
    private const val KEY_AMLL_SPRING_POSY_DAMPING = "amll_spring_posy_damping"
    private const val KEY_AMLL_SPRING_POSY_STIFFNESS = "amll_spring_posy_stiffness"

    private const val KEY_AMLL_SPRING_SCALE_MASS = "amll_spring_scale_mass"
    private const val KEY_AMLL_SPRING_SCALE_DAMPING = "amll_spring_scale_damping"
    private const val KEY_AMLL_SPRING_SCALE_STIFFNESS = "amll_spring_scale_stiffness"

    // 歌词样式相关设置
    private const val KEY_AMLL_LYRIC_PLAYER_IMPLEMENTATION = "amll_lyric_player_implementation"  // 歌词播放器实现
    private const val KEY_AMLL_LYRIC_SIZE_PRESET = "amll_lyric_size_preset"  // 歌词字体大小预设
    private const val KEY_AMLL_ENABLE_TRANSLATION_LINE = "amll_enable_translation_line"  // 显示翻译歌词
    private const val KEY_AMLL_ENABLE_ROMAN_LINE = "amll_enable_roman_line"  // 显示音译歌词
    private const val KEY_AMLL_ENABLE_SWAP_TRANS_ROMAN_LINE = "amll_enable_swap_trans_roman_line"  // 交换音译和翻译
    private const val KEY_AMLL_ADVANCE_DYNAMIC_LYRIC_TIME = "amll_advance_dynamic_lyric_time"  // 提前歌词行时序
    private const val KEY_AMLL_FONT_WEIGHT = "amll_font_weight"  // 字体字重
    private const val KEY_AMLL_LETTER_SPACING = "amll_letter_spacing"  // 字符间距

    // 歌词背景相关设置
    private const val KEY_AMLL_BACKGROUND_RENDERER = "amll_background_renderer"  // 背景渲染器类型
    private const val KEY_AMLL_CSS_BACKGROUND_PROPERTY = "amll_css_background_property"  // CSS 背景属性
    private const val KEY_AMLL_BACKGROUND_FPS = "amll_background_fps"  // 背景帧率
    private const val KEY_AMLL_BACKGROUND_RENDER_SCALE = "amll_background_render_scale"  // 背景渲染倍率
    private const val KEY_AMLL_BACKGROUND_STATIC_MODE = "amll_background_static_mode"  // 背景静态模式

    private const val KEY_WEBSOCKET_PROTOCOL_ADDRESS = "websocket_protocol_address"  // WebSocket 地址
    private const val KEY_WEBSOCKET_PROTOCOL_ENABLED = "websocket_protocol_enabled"  // WebSocket 开关
    private const val KEY_WEBVIEW_ENABLED = "webview_enabled"  // WebView 开关

    // 默认值常量（当用户未设置时使用）
    private const val DEFAULT_AMLL_ANIMATION_ENABLE_SPRING = false  // 默认禁用弹簧动画（性能优化）
    private const val DEFAULT_AMLL_ANIMATION_ENABLE_SCALE = false   // 默认禁用缩放（性能优化）
    private const val DEFAULT_AMLL_ANIMATION_ENABLE_BLUR = false    // 默认禁用模糊（性能优化）
    private const val DEFAULT_AMLL_ANIMATION_HIDE_PASSED_LINES = false  // 默认不隐藏已唱过的行
    private const val DEFAULT_AMLL_ANIMATION_WORD_FADE_WIDTH = 0.5f     // 默认渐变宽度 0.5
    private const val DEFAULT_AMLL_ANIMATION_FPS = 30  // 默认 30 FPS（性能优化，降低动画帧率）
    // 默认弹簧参数（与 core 默认保持一致）
    private const val DEFAULT_AMLL_SPRING_POSY_MASS = 0.9f
    private const val DEFAULT_AMLL_SPRING_POSY_DAMPING = 15.0f
    private const val DEFAULT_AMLL_SPRING_POSY_STIFFNESS = 90.0f

    private const val DEFAULT_AMLL_SPRING_SCALE_MASS = 2.0f
    private const val DEFAULT_AMLL_SPRING_SCALE_DAMPING = 25.0f
    private const val DEFAULT_AMLL_SPRING_SCALE_STIFFNESS = 100.0f

    // 歌词样式默认值
    private const val DEFAULT_AMLL_LYRIC_PLAYER_IMPLEMENTATION = "dom"  // 默认 DOM 实现
    private const val DEFAULT_AMLL_LYRIC_SIZE_PRESET = "medium"  // 默认中等字体大小
    private const val DEFAULT_AMLL_ENABLE_TRANSLATION_LINE = true  // 默认显示翻译歌词
    private const val DEFAULT_AMLL_ENABLE_ROMAN_LINE = true  // 默认显示音译歌词
    private const val DEFAULT_AMLL_ENABLE_SWAP_TRANS_ROMAN_LINE = false  // 默认不交换
    private const val DEFAULT_AMLL_ADVANCE_DYNAMIC_LYRIC_TIME = false  // 默认不提前
    private const val DEFAULT_AMLL_FONT_WEIGHT = 0  // 默认系统控制字重
    private const val DEFAULT_AMLL_LETTER_SPACING = ""  // 默认字符间距

    // 歌词背景默认值
    private const val DEFAULT_AMLL_BACKGROUND_RENDERER = "css-bg"  // 默认使用 CSS 背景（性能优化，最省资源）
    private const val DEFAULT_AMLL_CSS_BACKGROUND_PROPERTY = "#111111"  // 默认黑色背景
    private const val DEFAULT_AMLL_BACKGROUND_FPS = 24  // 默认 24 FPS（性能优化）
    private const val DEFAULT_AMLL_BACKGROUND_RENDER_SCALE = 0.75f  // 默认 0.75 倍渲染（性能优化，降低分辨率）
    private const val DEFAULT_AMLL_BACKGROUND_STATIC_MODE = true  // 默认启用静态模式（性能优化，禁用动态效果）

    private const val DEFAULT_WEBSOCKET_PROTOCOL_ADDRESS = "ws://localhost:11444"  // 默认本地地址
    private const val DEFAULT_WEBSOCKET_PROTOCOL_ENABLED = false  // 默认关闭
    private const val DEFAULT_WEBVIEW_ENABLED = true  // 默认启用 WebView
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
    // 辅助函数：获取 SharedPreferences 实例（避免重复代码）
    private fun prefs(context: Context) =
        PreferenceHelper(context, PREFS_NAME)
    fun getDefaultAmllFontFamily(): String = DEFAULT_AMLL_FONT_FAMILY
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
    fun isMetadataProcessingEnabled(context: Context): Boolean {
        return prefs(context)
            .getBoolean(KEY_PROCESS_METADATA_ENABLED, false)
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

    // === 弹簧参数（纵向 posY） ===
    fun getAmllSpringPosYMass(context: Context): Float {
        return prefs(context).getString(KEY_AMLL_SPRING_POSY_MASS, DEFAULT_AMLL_SPRING_POSY_MASS.toString())
            ?.toFloatOrNull() ?: DEFAULT_AMLL_SPRING_POSY_MASS
    }

    fun setAmllSpringPosYMass(context: Context, value: Float) {
        prefs(context).putString(KEY_AMLL_SPRING_POSY_MASS, value.toString())
    }

    fun getAmllSpringPosYDamping(context: Context): Float {
        return prefs(context).getString(KEY_AMLL_SPRING_POSY_DAMPING, DEFAULT_AMLL_SPRING_POSY_DAMPING.toString())
            ?.toFloatOrNull() ?: DEFAULT_AMLL_SPRING_POSY_DAMPING
    }

    fun setAmllSpringPosYDamping(context: Context, value: Float) {
        prefs(context).putString(KEY_AMLL_SPRING_POSY_DAMPING, value.toString())
    }

    fun getAmllSpringPosYStiffness(context: Context): Float {
        return prefs(context).getString(KEY_AMLL_SPRING_POSY_STIFFNESS, DEFAULT_AMLL_SPRING_POSY_STIFFNESS.toString())
            ?.toFloatOrNull() ?: DEFAULT_AMLL_SPRING_POSY_STIFFNESS
    }

    fun setAmllSpringPosYStiffness(context: Context, value: Float) {
        prefs(context).putString(KEY_AMLL_SPRING_POSY_STIFFNESS, value.toString())
    }

    // === 弹簧参数（缩放 scale） ===
    fun getAmllSpringScaleMass(context: Context): Float {
        return prefs(context).getString(KEY_AMLL_SPRING_SCALE_MASS, DEFAULT_AMLL_SPRING_SCALE_MASS.toString())
            ?.toFloatOrNull() ?: DEFAULT_AMLL_SPRING_SCALE_MASS
    }

    fun setAmllSpringScaleMass(context: Context, value: Float) {
        prefs(context).putString(KEY_AMLL_SPRING_SCALE_MASS, value.toString())
    }

    fun getAmllSpringScaleDamping(context: Context): Float {
        return prefs(context).getString(KEY_AMLL_SPRING_SCALE_DAMPING, DEFAULT_AMLL_SPRING_SCALE_DAMPING.toString())
            ?.toFloatOrNull() ?: DEFAULT_AMLL_SPRING_SCALE_DAMPING
    }

    fun setAmllSpringScaleDamping(context: Context, value: Float) {
        prefs(context).putString(KEY_AMLL_SPRING_SCALE_DAMPING, value.toString())
    }

    fun getAmllSpringScaleStiffness(context: Context): Float {
        return prefs(context).getString(KEY_AMLL_SPRING_SCALE_STIFFNESS, DEFAULT_AMLL_SPRING_SCALE_STIFFNESS.toString())
            ?.toFloatOrNull() ?: DEFAULT_AMLL_SPRING_SCALE_STIFFNESS
    }

    fun setAmllSpringScaleStiffness(context: Context, value: Float) {
        prefs(context).putString(KEY_AMLL_SPRING_SCALE_STIFFNESS, value.toString())
    }

    // === 歌词样式设置 ===

    /**
     * 获取歌词播放器实现类型
     * @return "dom", "dom-slim", 或 "canvas"
     */
    fun getAmllLyricPlayerImplementation(context: Context): String {
        return prefs(context).getString(KEY_AMLL_LYRIC_PLAYER_IMPLEMENTATION, DEFAULT_AMLL_LYRIC_PLAYER_IMPLEMENTATION)
            ?: DEFAULT_AMLL_LYRIC_PLAYER_IMPLEMENTATION
    }

    /**
     * 设置歌词播放器实现类型
     */
    fun setAmllLyricPlayerImplementation(context: Context, implementation: String) {
        prefs(context).putString(KEY_AMLL_LYRIC_PLAYER_IMPLEMENTATION, implementation)
    }

    /**
     * 获取歌词字体大小预设
     * @return "tiny", "extra-small", "small", "medium", "large", "extra-large", "huge"
     */
    fun getAmllLyricSizePreset(context: Context): String {
        return prefs(context).getString(KEY_AMLL_LYRIC_SIZE_PRESET, DEFAULT_AMLL_LYRIC_SIZE_PRESET)
            ?: DEFAULT_AMLL_LYRIC_SIZE_PRESET
    }

    /**
     * 设置歌词字体大小预设
     */
    fun setAmllLyricSizePreset(context: Context, preset: String) {
        prefs(context).putString(KEY_AMLL_LYRIC_SIZE_PRESET, preset)
    }

    /**
     * 是否启用翻译歌词显示
     */
    fun isAmllTranslationLineEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_AMLL_ENABLE_TRANSLATION_LINE, DEFAULT_AMLL_ENABLE_TRANSLATION_LINE)
    }

    /**
     * 设置是否启用翻译歌词显示
     */
    fun setAmllTranslationLineEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_AMLL_ENABLE_TRANSLATION_LINE, enabled)
    }

    /**
     * 是否启用音译歌词显示
     */
    fun isAmllRomanLineEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_AMLL_ENABLE_ROMAN_LINE, DEFAULT_AMLL_ENABLE_ROMAN_LINE)
    }

    /**
     * 设置是否启用音译歌词显示
     */
    fun setAmllRomanLineEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_AMLL_ENABLE_ROMAN_LINE, enabled)
    }

    /**
     * 是否启用音译和翻译歌词交换位置
     */
    fun isAmllSwapTransRomanLineEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_AMLL_ENABLE_SWAP_TRANS_ROMAN_LINE, DEFAULT_AMLL_ENABLE_SWAP_TRANS_ROMAN_LINE)
    }

    /**
     * 设置是否启用音译和翻译歌词交换位置
     */
    fun setAmllSwapTransRomanLineEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_AMLL_ENABLE_SWAP_TRANS_ROMAN_LINE, enabled)
    }

    /**
     * 是否启用提前歌词行时序（更接近 Apple Music 效果）
     */
    fun isAmllAdvanceDynamicLyricTimeEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_AMLL_ADVANCE_DYNAMIC_LYRIC_TIME, DEFAULT_AMLL_ADVANCE_DYNAMIC_LYRIC_TIME)
    }

    /**
     * 设置是否启用提前歌词行时序
     */
    fun setAmllAdvanceDynamicLyricTimeEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_AMLL_ADVANCE_DYNAMIC_LYRIC_TIME, enabled)
    }

    // === 歌词字体高级设置 ===

    /**
     * 获取字体字重（等同于 CSS font-weight）
     * @return 字重值，范围 0-1000，0 表示系统控制
     */
    fun getAmllFontWeight(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_AMLL_FONT_WEIGHT, DEFAULT_AMLL_FONT_WEIGHT)
    }

    /**
     * 设置字体字重
     * @param weight 字重值，范围 0-1000
     */
    fun setAmllFontWeight(context: Context, weight: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_AMLL_FONT_WEIGHT, weight.coerceIn(0, 1000)).apply()
    }

    /**
     * 获取字符间距（等同于 CSS letter-spacing）
     * @return 字符间距值，可以是任何 CSS 单位（如 "2px", "0.1em"）
     */
    fun getAmllLetterSpacing(context: Context): String {
        return prefs(context).getString(KEY_AMLL_LETTER_SPACING, DEFAULT_AMLL_LETTER_SPACING)
            ?: DEFAULT_AMLL_LETTER_SPACING
    }

    /**
     * 设置字符间距
     * @param spacing 字符间距值，支持 CSS 单位
     */
    fun setAmllLetterSpacing(context: Context, spacing: String) {
        prefs(context).putString(KEY_AMLL_LETTER_SPACING, spacing)
    }

    // === 歌词背景设置 ===

    /**
     * 获取歌词背景渲染器类型
     * @return "mesh" (网格渐变), "pixi" (PixiJS), 或 "css-bg" (CSS 背景)
     */
    fun getAmllBackgroundRenderer(context: Context): String {
        return prefs(context).getString(KEY_AMLL_BACKGROUND_RENDERER, DEFAULT_AMLL_BACKGROUND_RENDERER)
            ?: DEFAULT_AMLL_BACKGROUND_RENDERER
    }

    /**
     * 设置歌词背景渲染器类型
     */
    fun setAmllBackgroundRenderer(context: Context, renderer: String) {
        prefs(context).putString(KEY_AMLL_BACKGROUND_RENDERER, renderer)
    }

    /**
     * 获取 CSS 背景属性值（当使用 css-bg 渲染器时）
     */
    fun getAmllCssBackgroundProperty(context: Context): String {
        return prefs(context).getString(KEY_AMLL_CSS_BACKGROUND_PROPERTY, DEFAULT_AMLL_CSS_BACKGROUND_PROPERTY)
            ?: DEFAULT_AMLL_CSS_BACKGROUND_PROPERTY
    }

    /**
     * 设置 CSS 背景属性值
     */
    fun setAmllCssBackgroundProperty(context: Context, property: String) {
        prefs(context).putString(KEY_AMLL_CSS_BACKGROUND_PROPERTY, property)
    }

    /**
     * 获取歌词背景最高帧率
     */
    fun getAmllBackgroundFps(context: Context): Int {
        return prefs(context).getLong(KEY_AMLL_BACKGROUND_FPS, DEFAULT_AMLL_BACKGROUND_FPS.toLong()).toInt()
    }

    /**
     * 设置歌词背景最高帧率
     */
    fun setAmllBackgroundFps(context: Context, value: Int) {
        prefs(context).putLong(KEY_AMLL_BACKGROUND_FPS, value.toLong())
    }

    /**
     * 获取歌词背景渲染倍率
     */
    fun getAmllBackgroundRenderScale(context: Context): Float {
        return prefs(context).getString(KEY_AMLL_BACKGROUND_RENDER_SCALE, DEFAULT_AMLL_BACKGROUND_RENDER_SCALE.toString())
            ?.toFloatOrNull() ?: DEFAULT_AMLL_BACKGROUND_RENDER_SCALE
    }

    /**
     * 设置歌词背景渲染倍率
     */
    fun setAmllBackgroundRenderScale(context: Context, value: Float) {
        prefs(context).putString(KEY_AMLL_BACKGROUND_RENDER_SCALE, value.toString())
    }

    /**
     * 是否启用背景静态模式（禁用动态效果以节省性能）
     */
    fun isAmllBackgroundStaticModeEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_AMLL_BACKGROUND_STATIC_MODE, DEFAULT_AMLL_BACKGROUND_STATIC_MODE)
    }

    /**
     * 设置是否启用背景静态模式
     */
    fun setAmllBackgroundStaticModeEnabled(context: Context, enabled: Boolean) {
        prefs(context).putBoolean(KEY_AMLL_BACKGROUND_STATIC_MODE, enabled)
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
}