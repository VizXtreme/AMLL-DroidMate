package io.github.zeehan2005.scoremuse.global.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.InputStream

/**
 * 专辑封面颜色提取工具 (Material Design 3 兼容版)
 * 
 * 这个工具类负责从专辑封面图片中提取种子颜色，并根据 MD3 规范生成完整的颜色方案。
 * 实现了沉浸式的"音乐变色龙"动态主题效果。
 */
object AlbumColorExtractor {

    /**
     * 从专辑封面提取动态颜色主题
     * 
     * @param context Android 上下文
     * @param albumArtUri 专辑封面 URI
     * @param isDarkTheme 是否为深色模式
     * @return 符合 Material 3 规范的 ColorScheme，失败返回 null
     */
    suspend fun extractColorsFromAlbumArt(
        context: Context,
        albumArtUri: String?,
        isDarkTheme: Boolean
    ): ColorScheme? = withContext(Dispatchers.IO) {
        if (albumArtUri.isNullOrBlank()) {
            return@withContext null
        }

        try {
            val bitmap = loadBitmapFromUri(context, albumArtUri) ?: return@withContext null
            val palette = Palette.from(bitmap).generate()
            bitmap.recycle()

            return@withContext createDynamicColorScheme(palette, isDarkTheme)
        } catch (e: Exception) {
            Timber.e("[AlbumColorExtractor] Failed to extract colors: $e")
            return@withContext null
        }
    }

    private fun loadBitmapFromUri(context: Context, uriString: String): Bitmap? {
        try {
            val inputStream: InputStream? = when {
                uriString.startsWith("file://") -> {
                    val path = uriString.removePrefix("file://")
                    File(path).inputStream()
                }
                uriString.startsWith("content://") -> {
                    context.contentResolver.openInputStream(uriString.toUri())
                }
                else -> null
            }

            return inputStream?.use { stream ->
                val options = BitmapFactory.Options().apply { inSampleSize = 4 }
                BitmapFactory.decodeStream(stream, null, options)
            }
        } catch (e: Exception) {
            Timber.w("[AlbumColorExtractor] Bitmap load failed: $e")
            return null
        }
    }

    /**
     * 根据 Palette 提取的种子颜色生成 MD3 颜色方案
     */
    private fun createDynamicColorScheme(
        palette: Palette,
        isDarkTheme: Boolean
    ): ColorScheme {
        // 1. 确定种子颜色 (Seed Colors)
        val primarySeed = palette.vibrantSwatch?.rgb?.let { Color(it) }
            ?: palette.dominantSwatch?.rgb?.let { Color(it) }
            ?: Color(0xFF6366F1)

        val secondarySeed = palette.mutedSwatch?.rgb?.let { Color(it) }
            ?: primarySeed.adjustSaturation(0.5f)

        val tertiarySeed = palette.lightVibrantSwatch?.rgb?.let { Color(it) }
            ?: primarySeed.rotateHue(60f)

        val neutralSeed = palette.darkMutedSwatch?.let { Color(it.rgb) }
            ?: palette.dominantSwatch?.let { Color(it.rgb).adjustSaturation(0.1f) }
            ?: Color(0xFF6366F1).adjustSaturation(0.1f)

        // 2. 根据种子颜色生成对应的 MD3 Tonal Palette 并构建 ColorScheme
        return if (isDarkTheme) {
            darkColorScheme(
                primary = primarySeed.toTone(40),
                onPrimary = primarySeed.toTone(100),
                primaryContainer = primarySeed.toTone(30),
                onPrimaryContainer = primarySeed.toTone(90),
                secondary = secondarySeed.toTone(80),
                onSecondary = secondarySeed.toTone(20),
                secondaryContainer = secondarySeed.toTone(30),
                onSecondaryContainer = secondarySeed.toTone(90),
                tertiary = tertiarySeed.toTone(80),
                onTertiary = tertiarySeed.toTone(20),
                tertiaryContainer = tertiarySeed.toTone(30),
                onTertiaryContainer = tertiarySeed.toTone(90),
                background = neutralSeed.toTone(10),
                onBackground = neutralSeed.toTone(90),
                surface = neutralSeed.toTone(10),
                onSurface = neutralSeed.toTone(90),
                surfaceVariant = neutralSeed.toTone(30),
                onSurfaceVariant = neutralSeed.toTone(80),
                outline = neutralSeed.toTone(60),
                error = Color(0xFFF2B8B5),
                onError = Color(0xFF601410),
                errorContainer = Color(0xFF8C1D18),
                onErrorContainer = Color(0xFFF9DEDC)
            )
        } else { lightColorScheme(
                primary = primarySeed.toTone(40),
                onPrimary = primarySeed.toTone(100),
                primaryContainer = primarySeed.toTone(90),
                onPrimaryContainer = primarySeed.toTone(10),
                secondary = secondarySeed.toTone(40),
                onSecondary = secondarySeed.toTone(100),
                secondaryContainer = secondarySeed.toTone(90),
                onSecondaryContainer = secondarySeed.toTone(10),
                tertiary = tertiarySeed.toTone(40),
                onTertiary = tertiarySeed.toTone(100),
                tertiaryContainer = tertiarySeed.toTone(90),
                onTertiaryContainer = tertiarySeed.toTone(10),
                background = neutralSeed.toTone(99),
                onBackground = neutralSeed.toTone(10),
                surface = neutralSeed.toTone(99),
                onSurface = neutralSeed.toTone(10),
                surfaceVariant = neutralSeed.toTone(90),
                onSurfaceVariant = neutralSeed.toTone(30),
                outline = neutralSeed.toTone(50),
                error = Color(0xFFB3261E),
                onError = Color(0xFFFFFFFF),
                errorContainer = Color(0xFFF9DEDC),
                onErrorContainer = Color(0xFF410E0B)
            )}
    }

    /**
     * 将颜色转换为 MD3 规范中的指定色调 (Tone)
     * 简化实现：映射到 HSL 的亮度通道
     */
    private fun Color.toTone(tone: Int): Color {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(this.toArgb(), hsl)
        hsl[2] = (tone / 100f).coerceIn(0f, 1f)
        return Color(ColorUtils.HSLToColor(hsl))
    }
}

// ==================== 颜色处理辅助扩展函数 ====================

/**
 * 调整饱和度
 */
internal fun Color.adjustSaturation(factor: Float): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    hsl[1] = (hsl[1] * factor).coerceIn(0f, 1f)
    return Color(ColorUtils.HSLToColor(hsl))
}

/**
 * 旋转色相
 */
internal fun Color.rotateHue(degrees: Float = 30f): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    hsl[0] = (hsl[0] + degrees) % 360f
    if (hsl[0] < 0) hsl[0] += 360f
    return Color(ColorUtils.HSLToColor(hsl))
}
