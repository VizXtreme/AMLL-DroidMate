package io.github.zeehan2005.scoremuse.global.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// MD3 基础色调 - 只需修改这个 primary 颜色，其他颜色会自动生成
private val PrimarySeed = Color(0xFF6366F1)  // 靛蓝色：主色调

/**
 * 根据 MD3 Tonal Palette 规范生成完整的颜色方案
 * 基于单一的 primary seed color 自动派生所有相关颜色
 */
private fun generateColorScheme(primary: Color, isDark: Boolean): androidx.compose.material3.ColorScheme {
    return if (isDark) {
        darkColorScheme(
            // Primary 系列：主色及其变体
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.2f),
            onPrimaryContainer = primary.lighten(0.3f),
            
            // Secondary 系列：色相旋转 15 度，降低饱和度
            secondary = primary.rotateHue(15f).adjustSaturation(0.8f),
            onSecondary = Color.White,
            secondaryContainer = primary.rotateHue(15f).copy(alpha = 0.2f),
            onSecondaryContainer = primary.rotateHue(15f).lighten(0.3f),
            
            // Tertiary 系列：色相旋转 -15 度，增加饱和度
            tertiary = primary.rotateHue(-15f).adjustSaturation(1.2f),
            onTertiary = Color.White,
            tertiaryContainer = primary.rotateHue(-15f).copy(alpha = 0.2f),
            onTertiaryContainer = primary.rotateHue(-15f).lighten(0.3f),
            
            // 背景色系：基于primary seed生成的深色背景
            background = primary.darken(0.8f).adjustSaturation(0.2f),
            onBackground = primary.lighten(0.9f),
            
            // Surface 色系：比背景稍亮，创建层次
            surface = primary.darken(0.9f).adjustSaturation(0.1f),
            onSurface = primary.lighten(0.9f),
            surfaceVariant = primary.copy(alpha = 0.1f),
            onSurfaceVariant = primary.lighten(0.9f),
            
            // Error 系列：标准错误红
            error = Color(0xFFEF4444),
            onError = Color.White
        )
    } else {
        lightColorScheme(
            // Primary 系列：主色及其变体
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.1f),
            onPrimaryContainer = primary.darken(0.2f),
            
            // Secondary 系列：色相旋转 15 度，降低饱和度
            secondary = primary.rotateHue(15f).adjustSaturation(0.8f),
            onSecondary = Color.White,
            secondaryContainer = primary.rotateHue(15f).copy(alpha = 0.1f),
            onSecondaryContainer = primary.rotateHue(15f).darken(0.2f),
            
            // Tertiary 系列：色相旋转 -15 度，增加饱和度
            tertiary = primary.rotateHue(-15f).adjustSaturation(1.2f),
            onTertiary = Color.White,
            tertiaryContainer = primary.rotateHue(-15f).copy(alpha = 0.1f),
            onTertiaryContainer = primary.rotateHue(-15f).darken(0.2f),
            
            // 背景色系：基于primary seed生成的浅色背景
            background = primary.lighten(0.95f).adjustSaturation(0.1f),
            onBackground = primary.darken(0.8f),
            
            // Surface 色系：比背景稍亮，创建层次
            surface = primary.lighten(0.98f),
            onSurface = primary.darken(0.8f),
            surfaceVariant = primary.copy(alpha = 0.08f),
            onSurfaceVariant = primary.darken(0.8f),
            
            // Error 系列：标准错误红
            error = Color(0xFFEF4444),
            onError = Color.White
        )
    }
}

/**
 * ScoreMuse 应用主题
 * 
 * 这个 Composable 函数负责设置整个应用的视觉风格。
 * 支持三种主题模式：
 * 1. 浅色主题：明亮的配色方案
 * 2. 深色主题：暗色配色方案，适合夜间使用
 * 3. 动态主题：根据专辑封面自动调整配色（音乐变色龙效果）
 * 
 * **设计思想**：
 * - 使用 Material Design 3 的颜色系统
 * - 支持动态颜色提取（从专辑封面）
 * - 深浅主题平滑切换
 * - Container 颜色使用透明度实现层次感
 * 
 * @param darkTheme 是否使用深色主题（默认跟随系统设置）
 * @param dynamicColorScheme 可选的动态颜色方案（从专辑封面提取），如果为 null 则使用固定配色
 * @param content Composable 内容
 */
@Composable
fun ScoreMuseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColorScheme: DynamicColorScheme? = null,
    content: @Composable () -> Unit
) {
    // 根据是否有动态颜色方案选择配色策略
    val colorScheme = dynamicColorScheme?.// ✅ 使用动态颜色方案（从专辑封面提取）
        // 这会让 UI 随着播放的歌曲而变化，实现"音乐变色龙"效果
    toMaterialColorScheme(darkTheme)
        ?: // ✅ 使用默认颜色方案（基于单一 primary seed color 生成）
        generateColorScheme(PrimarySeed, darkTheme)

    // 应用 Material Design 3 主题
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}