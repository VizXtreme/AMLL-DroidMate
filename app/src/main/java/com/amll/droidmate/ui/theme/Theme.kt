package com.amll.droidmate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 成功和警告颜色定义（用于 UI 状态提示）
val SuccessGreen = Color(0xFF10B981)  // 绿色：表示成功、正常
val WarningAmber = Color(0xFFD97706)  // 琥珀色：表示警告、注意

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
            
            // 背景色系：使用极低的 alpha 创建微妙的色调
            background = Color(0xFF1F2937),
            onBackground = Color(0xFFE2E8F0),
            
            // Surface 色系：比背景稍亮，创建层次
            surface = Color(0xFF111827),
            onSurface = Color(0xFFE2E8F0),
            surfaceVariant = primary.copy(alpha = 0.1f),
            onSurfaceVariant = Color(0xFFE2E8F0),
            
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
            
            // 背景色系：接近白色的极浅色调
            background = Color(0xFFFAFAFA),
            onBackground = Color(0xFF1F1F1F),
            
            // Surface 色系：纯白或接近白色
            surface = Color.White,
            onSurface = Color(0xFF1F1F1F),
            surfaceVariant = primary.copy(alpha = 0.08f),
            onSurfaceVariant = Color(0xFF1F1F1F),
            
            // Error 系列：标准错误红
            error = Color(0xFFEF4444),
            onError = Color.White
        )
    }
}

/**
 * DroidMate 应用主题
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
fun DroidMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColorScheme: DynamicColorScheme? = null,
    content: @Composable () -> Unit
) {
    // 根据是否有动态颜色方案选择配色策略
    val colorScheme = if (dynamicColorScheme != null) {
        // ✅ 使用动态颜色方案（从专辑封面提取）
        // 这会让 UI 随着播放的歌曲而变化，实现"音乐变色龙"效果
        dynamicColorScheme.toMaterialColorScheme(darkTheme)
    } else {
        // ✅ 使用默认颜色方案（基于单一 primary seed color 生成）
        generateColorScheme(PrimarySeed, darkTheme)
    }

    // 应用 Material Design 3 主题
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
