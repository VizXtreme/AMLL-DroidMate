package com.amll.droidmate.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.amll.droidmate.ui.theme.DroidMateTheme
import com.amll.droidmate.ui.theme.DynamicThemeManager

/**
 * 纯歌词显示界面（占位符）
 * 
 * 这个 Activity 目前是一个简单的占位符实现，
 * 用于保持 AndroidManifest 中的引用有效。
 * 
 * **未来规划**：
 * 计划实现一个独立的歌词显示界面，支持：
 * - 全屏歌词展示
 * - 简洁模式（无背景、无动画）
 * - 适合在电视或车载系统中使用
 * - 支持远程滚动控制
 * 
 * **当前状态**：
 * ⚠️ 尚未完全实现，仅显示提示文本
 */
class LyricsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 启用边缘到边缘显示
        enableEdgeToEdge()
        // 设置系统栏不遮挡内容
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            val dynamicColorScheme by DynamicThemeManager.observeColorScheme()
            
            DroidMateTheme(
                darkTheme = isDarkTheme,
                dynamicColorScheme = dynamicColorScheme
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.statusBarsPadding()
                ) {
                    Text(text = "Lyrics screen is not implemented yet")
                }
            }
        }
    }
}
