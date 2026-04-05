package com.amll.droidmate.ui.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.InputStream

/**
 * 专辑封面颜色提取工具
 * 
 * 这个工具类负责从专辑封面图片中提取主要的颜色方案，
 * 用于实现动态主题功能（"音乐变色龙"效果）。
 * 
 * **提取流程**：
 * 1. 从 URI 加载专辑封面图片
 * 2. 使用 Android Palette API 分析主要颜色
 * 3. 提取多种颜色变体（主色、强调色、背景色等）
 * 4. 根据深色/浅色模式调整颜色亮度
 * 5. 返回完整的颜色方案供 UI 使用
 * 
 * **性能优化**：
 * - 在 IO 线程执行，避免阻塞主线程
 * - 及时回收 Bitmap 释放内存
 * - 异常处理保证应用稳定性
 * 
 * **颜色提取策略**：
 * - DominantSwatch：主导颜色（占比最大）
 * - MutedSwatch：柔和颜色（低饱和度）
 * - VibrantSwatch：鲜艳颜色（高饱和度）
 * - LightMutedSwatch：浅柔和色
 * - DarkVibrantSwatch：深鲜艳色
 */
object AlbumColorExtractor {

    /**
     * 从专辑封面提取动态颜色主题
     * 
     * 这是动态主题的核心方法。它会：
     * 1. 验证 URI 是否有效
     * 2. 在后台线程加载图片（避免卡顿）
     * 3. 使用 Palette API 提取主要颜色
     * 4. 生成适配深色/浅色模式的颜色方案
     * 
     * **返回值说明**：
     * - 成功：返回 DynamicColorScheme 对象，包含完整的颜色方案
     * - 失败：返回 null，使用默认主题
     * 
     * @param context Android 上下文
     * @param albumArtUri 专辑封面 URI（支持 file:// 和 content:// 协议）
     * @param isDarkTheme 是否为深色模式（影响颜色亮度调整）
     * @return 提取的颜色方案，失败返回 null
     */
    suspend fun extractColorsFromAlbumArt(
        context: Context,
        albumArtUri: String?,
        isDarkTheme: Boolean
    ): DynamicColorScheme? = withContext(Dispatchers.IO) {
        // URI 无效时直接返回 null
        if (albumArtUri.isNullOrBlank()) {
            Timber.e("[AlbumColorExtractor] Album art URI is null or blank")
            return@withContext null  // URI 无效，直接返回
        }

        try {
            // Step 1: 加载图片并生成调色板
            val bitmap = loadBitmapFromUri(context, albumArtUri) ?: return@withContext null
            // Palette 会分析图片的主要颜色，提取多个色板
            val palette = Palette.from(bitmap).generate()
            
            // Step 2: 及时释放内存，避免 OOM
            bitmap.recycle()

            // Step 3: 根据调色板和深色模式创建颜色方案
            return@withContext createDynamicColorScheme(palette, isDarkTheme)
        } catch (e: Exception) {
            Timber.e("[AlbumColorExtractor] Failed to extract colors from album art: $albumArtUri", e)
            return@withContext null  // 提取失败，返回 null
        }
    }

    /**
     * 从 URI 加载 Bitmap 图片
     * 
     * 支持多种 URI 格式：
     * - file://：本地文件路径，直接读取文件系统
     * - content://：ContentProvider 提供的流，需要权限
     * 
     * **注意事项**：
     * - 不支持 http:// 或 https:// 网络图片
     * - content:// URI 可能需要读取权限
     * - 异常处理保证稳定性
     * 
     * @param context Android 上下文
     * @param uriString 图片 URI 字符串
     * @return 加载的 Bitmap，失败返回 null
     */
    private fun loadBitmapFromUri(context: Context, uriString: String): Bitmap? {
        try {
            val inputStream: InputStream? = when {
                uriString.startsWith("file://") -> {
                    val path = uriString.removePrefix("file://")
                    File(path).inputStream()
                }
                uriString.startsWith("content://") -> {
                    val uri = Uri.parse(uriString)
                    context.contentResolver.openInputStream(uri)
                }
                else -> {
                    Timber.w("[AlbumColorExtractor] Unsupported URI scheme: $uriString")
                    return null
                }
            }

            return inputStream?.use { stream ->
                // 缩小图片以提高性能
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 4 // 缩小到1/4大小
                }
                BitmapFactory.decodeStream(stream, null, options)
            }
        } catch (e: Exception) {
            Timber.w("[AlbumColorExtractor] Failed to load bitmap from URI: $uriString", e)
            return null
        }
    }

    /**
     * 根据Palette生成动态颜色方案
     */
    private fun createDynamicColorScheme(
        palette: Palette,
        isDarkTheme: Boolean
    ): DynamicColorScheme {
        if (isDarkTheme) {
            return createDarkColorScheme(palette)
        } else {
            return createLightColorScheme(palette)
        }
    }

    /**
     * 创建深色模式的颜色方案
     */
    private fun createDarkColorScheme(palette: Palette): DynamicColorScheme {
        // 主色：使用鲜艳的颜色
        val primarySwatch = palette.vibrantSwatch 
            ?: palette.lightVibrantSwatch
            ?: palette.dominantSwatch
        val primary = primarySwatch?.let { Color(it.rgb).adjustForDarkMode() }
            ?: Color(0xFF6366F1)

        // 次要色：使用柔和的颜色
        val secondarySwatch = palette.mutedSwatch 
            ?: palette.lightMutedSwatch
            ?: palette.dominantSwatch
        val secondary = secondarySwatch?.let { Color(it.rgb).adjustForDarkMode() }
            ?: Color(0xFF8B5CF6)

        // 背景色：深色
        val background = palette.darkMutedSwatch?.let {
            Color(it.rgb).darken(0.6f).adjustSaturation(0.4f)
        } ?: Color(0xFF1F2937)

        // Surface色：略浅于背景
        val surface = background.lighten(0.08f)

        // 确保对比度
        val onPrimary = if (primary.luminance() > 0.5f) Color.Black else Color.White
        val onSecondary = if (secondary.luminance() > 0.5f) Color.Black else Color.White
        val onBackground = Color(0xFFE5E5E5)
        val onSurface = Color(0xFFE5E5E5)

        return DynamicColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            secondary = secondary,
            onSecondary = onSecondary,
            tertiary = primary.rotatehue(30f),
            onTertiary = onPrimary,
            background = background,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = surface.lighten(0.05f),
            onSurfaceVariant = onSurface.copy(alpha = 0.8f),
            error = Color(0xFFEF4444),
            onError = Color.White
        )
    }

    /**
     * 创建浅色模式的颜色方案
     */
    private fun createLightColorScheme(palette: Palette): DynamicColorScheme {
        // 主色：使用鲜艳的颜色
        val primarySwatch = palette.vibrantSwatch 
            ?: palette.darkVibrantSwatch
            ?: palette.dominantSwatch
        val primary = primarySwatch?.let { Color(it.rgb).adjustForLightMode() }
            ?: Color(0xFF6366F1)

        // 次要色：使用柔和的颜色
        val secondarySwatch = palette.mutedSwatch 
            ?: palette.darkMutedSwatch
            ?: palette.dominantSwatch
        val secondary = secondarySwatch?.let { Color(it.rgb).adjustForLightMode() }
            ?: Color(0xFF8B5CF6)

        // 背景色：基于 MD3 Tonal Palette 规范
        // 使用 primary 颜色的极浅变体，创建柔和的背景色调
        val background = primary.let {
            // MD3 浅色模式背景通常使用 primary 的 98-99% 亮度版本
            it.copy(alpha = 0.05f) // 降低不透明度以创建微妙的色调
                .lighten(0.95f)    // 大幅提亮至接近白色
                .adjustSaturation(0.05f) // 降低饱和度，保持中性
        }
        // 确保背景色足够明亮 (MD3 建议背景亮度 > 0.95)
        val backgroundFinal = if (background.luminance() < 0.95f) {
            background.withMinLuminance(0.98f)
        } else {
            background
        }

        // Surface 色：基于 MD3 Elevation 规范
        // Surface 应该比背景稍亮，创建微妙的层次感
        val surface = backgroundFinal.lighten(0.02f).coerceAtMost(Color.White)

        // 确保对比度
        val onPrimary = if (primary.luminance() > 0.5f) Color.Black else Color.White
        val onSecondary = if (secondary.luminance() > 0.5f) Color.Black else Color.White
        val onBackground = Color(0xFF1F1F1F)
        val onSurface = Color(0xFF1F1F1F)

        return DynamicColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            secondary = secondary,
            onSecondary = onSecondary,
            tertiary = primary.rotatehue(30f),
            onTertiary = onPrimary,
            background = backgroundFinal,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = primary.copy(alpha = 0.08f), // MD3 建议使用更低的不透明度
            onSurfaceVariant = onSurface.copy(alpha = 0.8f),
            error = Color(0xFFEF4444),
            onError = Color.White
        )
    }

    // 颜色调整辅助函数

    /**
     * 调整颜色使其适合深色模式（提高亮度）
     */
    private fun Color.adjustForDarkMode(): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(this.toArgb(), hsv)
        
        // 如果太暗，提高亮度
        if (hsv[2] < 0.6f) {
            hsv[2] = 0.6f
        }
        // 如果太鲜艳，降低饱和度
        if (hsv[1] > 0.8f) {
            hsv[1] = 0.7f
        }
        
        var adjusted = Color(android.graphics.Color.HSVToColor(hsv))

        // 一些封面色彩非常暗，即使亮度已调高也可能仍不够明亮，进一步提亮以避免 primary 太深
        if (adjusted.luminance() < 0.65f) {
            adjusted = adjusted.withMinLuminance(0.65f)
        }

        return adjusted
    }

    /**
     * 调整颜色使其适合浅色模式（降低亮度）
     */
    private fun Color.adjustForLightMode(): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(this.toArgb(), hsv)
        
        // 如果太亮，降低亮度
        if (hsv[2] > 0.7f) {
            hsv[2] = hsv[2].coerceAtMost(0.65f)
        }
        // 增加饱和度使颜色更鲜明
        if (hsv[1] < 0.5f) {
            hsv[1] = 0.6f
        }
        
        var adjusted = Color(android.graphics.Color.HSVToColor(hsv))
        if (adjusted.luminance() < 0.65f) {
            adjusted = adjusted.withMinLuminance(0.65f)
        }
        return adjusted
    }

    /**
     * 保证颜色至少达到指定亮度（直接设置亮度值）
     */
    private fun Color.withMinLuminance(minLuminance: Float): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(this.toArgb(), hsv)
        hsv[2] = hsv[2].coerceAtLeast(minLuminance.coerceIn(0f, 1f))
        return Color(android.graphics.Color.HSVToColor(hsv))
    }

    /**
     * 限制颜色的最大值
     */
    private fun Color.coerceAtMost(other: Color): Color {
        return if (this.luminance() > other.luminance()) other else this
    }
}

// ==================== 颜色处理扩展函数（包级可见） ====================

/**
 * 使颜色变亮
 */
internal fun Color.lighten(factor: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)
    hsv[2] = (hsv[2] + (1f - hsv[2]) * factor).coerceAtMost(1f)
    return Color(android.graphics.Color.HSVToColor(hsv))
}

/**
 * 使颜色变暗
 */
internal fun Color.darken(factor: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)
    hsv[2] = (hsv[2] * (1f - factor)).coerceAtLeast(0f)
    return Color(android.graphics.Color.HSVToColor(hsv))
}

/**
 * 调整饱和度
 */
internal fun Color.adjustSaturation(factor: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)
    hsv[1] = (hsv[1] * factor).coerceIn(0f, 1f)
    return Color(android.graphics.Color.HSVToColor(hsv))
}

/**
 * 旋转色相
 */
internal fun Color.rotatehue(degrees: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)
    hsv[0] = (hsv[0] + degrees) % 360f
    if (hsv[0] < 0) hsv[0] += 360f
    return Color(android.graphics.Color.HSVToColor(hsv))
}

/**
 * 旋转色相（别名，兼容 MD3 规范命名）
 */
internal fun Color.rotateHue(degrees: Float): Color = rotatehue(degrees)

/**
 * 动态颜色方案数据类
 */
data class DynamicColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val error: Color,
    val onError: Color
)

/**
 * 将动态颜色方案转换为 Material Design 3 ColorScheme
 * 
 * @param isDarkTheme 是否为深色模式
 * @return 完整的 Material ColorScheme
 */
fun DynamicColorScheme.toMaterialColorScheme(isDarkTheme: Boolean) = if (isDarkTheme) {
    darkColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primary.copy(alpha = 0.2f),
        onPrimaryContainer = onPrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondary.copy(alpha = 0.2f),
        onSecondaryContainer = onSecondary,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiary.copy(alpha = 0.2f),
        onTertiaryContainer = onTertiary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        error = error,
        onError = onError
    )
} else {
    lightColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primary.copy(alpha = 0.1f),
        onPrimaryContainer = primary,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondary.copy(alpha = 0.1f),
        onSecondaryContainer = secondary,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiary.copy(alpha = 0.1f),
        onTertiaryContainer = tertiary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        error = error,
        onError = onError
    )
}
