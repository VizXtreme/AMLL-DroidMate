package com.amll.droidmate.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.amll.droidmate.ui.theme.DroidMateTheme
import com.amll.droidmate.ui.theme.DynamicThemeManager
import androidx.compose.runtime.collectAsState

/**
 * 基于 Compose 的 Activity 基类
 * 
 * 提供统一的初始化逻辑和主题设置，所有 Settings Activity 应继承此类
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
        
        // 启用边缘到边缘显示
        enableEdgeToEdge()
        
        // 设置系统栏不遮挡内容
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val context = LocalContext.current
            val isDarkTheme = isSystemInDarkTheme()
            val dynamicColorScheme by DynamicThemeManager.observeColorScheme()
            
            DroidMateTheme(
                darkTheme = isDarkTheme,
                dynamicColorScheme = dynamicColorScheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    renderContent()
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
    abstract fun renderContent()
}
