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

private val DarkColors = darkColorScheme(
    primary = Color(0xFF818CF8),
    secondary = Color(0xFF8B5CF6),
    tertiary = Color(0xFFF97316),
    background = Color(0xFF1F2937),
    surface = Color(0xFF111827),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFE2E8F0),
    error = Color(0xFFEF4444),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF6366F1),
    secondary = Color(0xFF8B5CF6),
    tertiary = Color(0xFFF97316),
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF334155),
    error = Color(0xFFEF4444),
)

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
        if (darkTheme) {
            // 深色模式下的动态颜色方案
            // Container 使用 0.3 透明度，在深色背景上形成柔和的层次
            darkColorScheme(
                primary = dynamicColorScheme.primary,
                onPrimary = dynamicColorScheme.onPrimary,
                primaryContainer = dynamicColorScheme.primary.copy(alpha = 0.3f),
                onPrimaryContainer = dynamicColorScheme.onPrimary,
                secondary = dynamicColorScheme.secondary,
                onSecondary = dynamicColorScheme.onSecondary,
                secondaryContainer = dynamicColorScheme.secondary.copy(alpha = 0.3f),
                onSecondaryContainer = dynamicColorScheme.onSecondary,
                tertiary = dynamicColorScheme.tertiary,
                onTertiary = dynamicColorScheme.onTertiary,
                tertiaryContainer = dynamicColorScheme.tertiary.copy(alpha = 0.3f),
                onTertiaryContainer = dynamicColorScheme.onTertiary,
                background = dynamicColorScheme.background,
                onBackground = dynamicColorScheme.onBackground,
                surface = dynamicColorScheme.surface,
                onSurface = dynamicColorScheme.onSurface,
                surfaceVariant = dynamicColorScheme.surfaceVariant,
                onSurfaceVariant = dynamicColorScheme.onSurfaceVariant,
                error = dynamicColorScheme.error,
                onError = dynamicColorScheme.onError
            )
        } else {
            // 浅色模式下的动态颜色方案
            // Container 使用 0.1 透明度，保持明亮清爽
            lightColorScheme(
                primary = dynamicColorScheme.primary,
                onPrimary = dynamicColorScheme.onPrimary,
                primaryContainer = dynamicColorScheme.primary.copy(alpha = 0.1f),
                onPrimaryContainer = dynamicColorScheme.primary,
                secondary = dynamicColorScheme.secondary,
                onSecondary = dynamicColorScheme.onSecondary,
                secondaryContainer = dynamicColorScheme.secondary.copy(alpha = 0.1f),
                onSecondaryContainer = dynamicColorScheme.secondary,
                tertiary = dynamicColorScheme.tertiary,
                onTertiary = dynamicColorScheme.onTertiary,
                tertiaryContainer = dynamicColorScheme.tertiary.copy(alpha = 0.1f),
                onTertiaryContainer = dynamicColorScheme.tertiary,
                background = dynamicColorScheme.background,
                onBackground = dynamicColorScheme.onBackground,
                surface = dynamicColorScheme.surface,
                onSurface = dynamicColorScheme.onSurface,
                surfaceVariant = dynamicColorScheme.surfaceVariant,
                onSurfaceVariant = dynamicColorScheme.onSurfaceVariant,
                error = dynamicColorScheme.error,
                onError = dynamicColorScheme.onError
            )
        }
    } else {
        // 使用默认颜色方案（固定配色）
        if (darkTheme) DarkColors else LightColors
    }

    // 应用 Material Design 3 主题
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
