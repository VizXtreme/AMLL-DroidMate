package io.github.zeehan2005.scoremuse.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import io.github.zeehan2005.scoremuse.global.theme.DynamicThemeManager
import io.github.zeehan2005.scoremuse.global.theme.ScoreMuseTheme

/**
 * 基于 Compose 的 Activity 基类
 *
 * 这个抽象类为所有使用 Jetpack Compose 的 Activity 提供了统一的初始化逻辑。
 * 主要功能包括：
 * - 边缘到边缘显示（现代化全面屏支持）
 * - 系统栏自动适配（状态栏、导航栏不遮挡内容）
 * - 动态主题切换（亮色/暗色模式 + 动态配色）
 * - 统一的内容渲染接口
 *
 * 用法：
 * ```kotlin
 * class MySettingsActivity : BaseComposeActivity() {
 *     @Composable
 *     override fun renderContent() {
 *         MySettingsPage(onBack = { finish() })
 *     }
 * }
 * ```
 */
abstract class BaseComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 启用边缘到边缘显示（现代 Android 的全面屏设计）
        enableEdgeToEdge()

        // 设置系统栏（状态栏、导航栏）不遮挡内容
        // false 表示系统栏会悬浮在应用内容之上，而不是挤压布局空间
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 设置 Compose 内容视图
        setContent {
            LocalContext.current
            val isDarkTheme = isSystemInDarkTheme()  // 跟随系统深色模式
            val dynamicColorScheme by DynamicThemeManager.observeColorScheme()  // 监听动态配色变化

            ScoreMuseTheme(
                darkTheme = isDarkTheme,
                dynamicColorScheme = dynamicColorScheme
            ) {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RenderContent()  // 调用子类实现来渲染具体内容
                }
            }
        }
    }

    /**
     * 渲染 Activity 内容
     *
     * 子类必须实现此函数以提供具体的 UI 内容
     */
    @Composable
    abstract fun RenderContent()
}