package dev.amll.droidmate.global

import android.content.Context
import io.github.zeehan2005.scoremuse.components.PreferenceHelper
import org.json.JSONArray


object AMLLSettings {
    private const val PREFS_NAME = "droidmate_settings"  // SharedPreferences 名称
    private const val KEY_AMLL_FONT_FAMILY = "amll_font_family"  // AMLL 字体族
    private const val KEY_AMLL_FONT_FILE_PATH = "amll_font_file_path"  // 字体文件路径
    private const val KEY_AMLL_FONT_FILE_NAME = "amll_font_file_name"  // 字体文件名
    private const val KEY_AMLL_FONT_FILES = "amll_font_files"  // 已安装的字体列表
    private const val KEY_AMLL_ACTIVE_FONT_ID = "amll_active_font_id"  // 当前激活的字体 ID
    private const val KEY_AMLL_ENABLED_FONT_IDS = "amll_enabled_font_ids"  // 启用的字体 ID 列表
    private const val KEY_PROCESS_METADATA_ENABLED = "process_metadata_enabled"  // 处理元数据开关

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
    private const val KEY_AMLL_ADVANCE_DYNAMIC_LYRIC_TIME = "amll_advance_dynamic_lyric_time"  // 提前歌词行时序
    private const val KEY_AMLL_FONT_WEIGHT = "amll_font_weight"  // 字体字重
    private const val KEY_AMLL_LETTER_SPACING = "amll_letter_spacing"  // 字符间距

    // 歌词背景相关设置
    private const val KEY_AMLL_BACKGROUND_RENDERER = "amll_background_renderer"  // 背景渲染器类型
    private const val KEY_AMLL_CSS_BACKGROUND_PROPERTY = "amll_css_background_property"  // CSS 背景属性
    private const val KEY_AMLL_BACKGROUND_FPS = "amll_background_fps"  // 背景帧率
    private const val KEY_AMLL_BACKGROUND_RENDER_SCALE = "amll_background_render_scale"  // 背景渲染倍率
    private const val KEY_AMLL_BACKGROUND_STATIC_MODE = "amll_background_static_mode"  // 背景静态模式

//    private const val KEY_WEBSOCKET_PROTOCOL_ADDRESS = "websocket_protocol_address"  // WebSocket 地址
//    private const val KEY_WEBSOCKET_PROTOCOL_ENABLED = "websocket_protocol_enabled"  // WebSocket 开关
//    private const val KEY_WEBVIEW_ENABLED = "webview_enabled"  // WebView 开关
//
//    // 默认值常量（非 AMLL 核心设置保留默认值）
//    private const val DEFAULT_WEBSOCKET_PROTOCOL_ADDRESS = "ws://localhost:11444"  // 默认本地地址
//    private const val DEFAULT_WEBSOCKET_PROTOCOL_ENABLED = false  // 默认关闭
//    private const val DEFAULT_WEBVIEW_ENABLED = true  // 默认启用 WebView

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

    private fun getBooleanOrNull(context: Context, key: String): Boolean? {
        val p = prefs(context)
        return if (p.contains(key)) p.getBoolean(key) else null
    }

    private fun getFloatOrNull(context: Context, key: String): Float? {
        val p = prefs(context)
        return if (p.contains(key)) p.getFloat(key) else null
    }

    private fun getIntOrNull(context: Context): Int? {
        val p = prefs(context)
        return if (p.contains(KEY_AMLL_FONT_WEIGHT)) p.getInt(KEY_AMLL_FONT_WEIGHT) else null
    }

    private fun getStringOrNull(context: Context, key: String): String? {
        return prefs(context).getString(key, null)
    }

    fun getAmllFontFamily(context: Context): String? {
        return getStringOrNull(context, KEY_AMLL_FONT_FAMILY)
    }

    fun isMetadataProcessingEnabled(context: Context): Boolean {
        return prefs(context)
            .getBoolean(KEY_PROCESS_METADATA_ENABLED, false)
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
                            displayName = displayName.ifBlank { "Imported Font" },
                            absolutePath = absolutePath,
                            fontFamilyName = fontFamilyName.ifBlank {
                                buildFontFamilyName(displayName, absolutePath)
                            }
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getActiveAmllFontFileId(context: Context): String? {
        val helper = prefs(context)
        val activeId = helper.getString(KEY_AMLL_ACTIVE_FONT_ID, null)
        if (!activeId.isNullOrBlank()) return activeId

        val legacyPath = helper.getString(KEY_AMLL_FONT_FILE_PATH, null)
        return legacyPath?.takeIf { it.isNotBlank() }?.let(::stableFontId)
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

    private fun stableFontId(absolutePath: String): String {
        return "font_" + absolutePath.hashCode().toUInt().toString(16)
    }

    private fun buildFontFamilyName(displayName: String, absolutePath: String): String {
        return displayName
            .substringBeforeLast('.')
            .ifBlank { absolutePath.substringAfterLast('/').substringAfterLast('\\').substringBeforeLast('.') }
    }

    fun isAmllAnimationSpringEnabled(context: Context): Boolean? =
        getBooleanOrNull(context, KEY_AMLL_ANIMATION_ENABLE_SPRING)

    fun isAmllAnimationScaleEnabled(context: Context): Boolean? =
        getBooleanOrNull(context, KEY_AMLL_ANIMATION_ENABLE_SCALE)

    fun isAmllAnimationBlurEnabled(context: Context): Boolean? =
        getBooleanOrNull(context, KEY_AMLL_ANIMATION_ENABLE_BLUR)

    fun isAmllAnimationHidePassedLinesEnabled(context: Context): Boolean? =
        getBooleanOrNull(context, KEY_AMLL_ANIMATION_HIDE_PASSED_LINES)

    fun getAmllAnimationWordFadeWidth(context: Context): Float? =
        getFloatOrNull(context, KEY_AMLL_ANIMATION_WORD_FADE_WIDTH)

    fun getAmllAnimationFps(context: Context): Int? {
        val p = prefs(context)
        return if (p.contains(KEY_AMLL_ANIMATION_FPS)) p.getLong(KEY_AMLL_ANIMATION_FPS).toInt() else null
    }

    // === 弹簧参数（纵向 posY） ===
    fun getAmllSpringPosYMass(context: Context): Float? =
        getFloatOrNull(context, KEY_AMLL_SPRING_POSY_MASS)

    fun getAmllSpringPosYDamping(context: Context): Float? =
        getFloatOrNull(context, KEY_AMLL_SPRING_POSY_DAMPING)

    fun getAmllSpringPosYStiffness(context: Context): Float? =
        getFloatOrNull(context, KEY_AMLL_SPRING_POSY_STIFFNESS)

    // === 弹簧参数（缩放 scale） ===
    fun getAmllSpringScaleMass(context: Context): Float? =
        getFloatOrNull(context, KEY_AMLL_SPRING_SCALE_MASS)

    fun getAmllSpringScaleDamping(context: Context): Float? =
        getFloatOrNull(context, KEY_AMLL_SPRING_SCALE_DAMPING)

    fun getAmllSpringScaleStiffness(context: Context): Float? =
        getFloatOrNull(context, KEY_AMLL_SPRING_SCALE_STIFFNESS)

    // === 歌词样式设置 ===

    /**
     * 获取歌词播放器实现类型
     * @return "dom", "dom-slim", 或 "canvas"
     */
    fun getAmllLyricPlayerImplementation(context: Context): String? =
        getStringOrNull(context, KEY_AMLL_LYRIC_PLAYER_IMPLEMENTATION)

    /**
     * 获取歌词字体大小预设
     * @return "tiny", "extra-small", "small", "medium", "large", "extra-large", "huge"
     */
    fun getAmllLyricSizePreset(context: Context): String? =
        getStringOrNull(context, KEY_AMLL_LYRIC_SIZE_PRESET)

    /**
     * 是否启用翻译歌词显示
     */
    fun isAmllTranslationLineEnabled(context: Context): Boolean? =
        getBooleanOrNull(context, KEY_AMLL_ENABLE_TRANSLATION_LINE)

    /**
     * 是否启用音译歌词显示
     */
    fun isAmllRomanLineEnabled(context: Context): Boolean? =
        getBooleanOrNull(context, KEY_AMLL_ENABLE_ROMAN_LINE)

    /**
     * 是否启用提前歌词行时序（更接近 Apple Music 效果）
     */
    fun isAmllAdvanceDynamicLyricTimeEnabled(context: Context): Boolean? =
        getBooleanOrNull(context, KEY_AMLL_ADVANCE_DYNAMIC_LYRIC_TIME)

    // === 歌词字体高级设置 ===

    /**
     * 获取字体字重（等同于 CSS font-weight）
     * @return 字重值，范围 0-1000，0 表示系统控制
     */
    fun getAmllFontWeight(context: Context): Int? =
        getIntOrNull(context)

    /**
     * 获取字符间距（等同于 CSS letter-spacing）
     * @return 字符间距值，可以是任何 CSS 单位（如 "2px", "0.1em"）
     */
    fun getAmllLetterSpacing(context: Context): String? =
        getStringOrNull(context, KEY_AMLL_LETTER_SPACING)

    // === 歌词背景设置 ===

    /**
     * 获取歌词背景渲染器类型
     * @return "mesh" (网格渐变), "pixi" (PixiJS), 或 "css-bg" (CSS 背景)
     */
    fun getAmllBackgroundRenderer(context: Context): String? =
        getStringOrNull(context, KEY_AMLL_BACKGROUND_RENDERER)

    /**
     * 获取 CSS 背景属性值（当使用 css-bg 渲染器时）
     */
    fun getAmllCssBackgroundProperty(context: Context): String? =
        getStringOrNull(context, KEY_AMLL_CSS_BACKGROUND_PROPERTY)

    /**
     * 获取歌词背景最高帧率
     */
    fun getAmllBackgroundFps(context: Context): Int? {
        val p = prefs(context)
        return if (p.contains(KEY_AMLL_BACKGROUND_FPS)) p.getLong(KEY_AMLL_BACKGROUND_FPS).toInt() else null
    }

    /**
     * 获取歌词背景渲染倍率
     */
    fun getAmllBackgroundRenderScale(context: Context): Float? =
        getFloatOrNull(context, KEY_AMLL_BACKGROUND_RENDER_SCALE)

    /**
     * 是否启用背景静态模式（禁用动态效果以节省性能）
     */
    fun isAmllBackgroundStaticModeEnabled(context: Context): Boolean? =
        getBooleanOrNull(context, KEY_AMLL_BACKGROUND_STATIC_MODE)

}
