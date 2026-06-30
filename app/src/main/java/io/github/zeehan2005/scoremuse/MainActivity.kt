package io.github.zeehan2005.scoremuse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.zeehan2005.scoremuse.global.viewmodel.MainViewModel
import io.github.zeehan2005.scoremuse.global.theme.AlbumColorExtractor
import io.github.zeehan2005.scoremuse.global.theme.DynamicThemeManager
import io.github.zeehan2005.scoremuse.global.theme.ScoreMuseTheme
import io.github.zeehan2005.scoremuse.ui.MainScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * 应用主入口 Activity
 * 
 * 这是用户启动应用时看到的第一个界面，负责：
 * 1. 初始化日志系统（LogHelper + Timber）
 * 2. 设置全屏显示和系统栏适配
 * 3. 加载主界面（MainScreen）
 * 4. 动态主题管理（根据专辑封面颜色自动调整配色）
 * 
 * 生命周期流程：
 * - onCreate: 初始化 → 设置布局 → 监听播放状态变化 → 提取专辑颜色 → 应用动态主题
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化 LogHelper（必须在 Timber 之前，因为 LogHelper 依赖 Timber）
        io.github.zeehan2005.scoremuse.components.LogHelper.init(this)
        
        // 初始化 Timber 以捕获应用日志
        // 如果还没有种植任何树（首次启动），则种入 LogHelperTree
        if (Timber.treeCount == 0) {
            Timber.plant(io.github.zeehan2005.scoremuse.components.LogHelper.LogHelperTree())
        }

        // 启用边缘到边缘显示（全面屏设计）
        enableEdgeToEdge()
        // 设置系统栏不遮挡内容
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // 设置 Compose 内容视图
        setContent {
            val viewModel: MainViewModel = viewModel()
            val nowPlaying by viewModel.nowPlayingMusic.collectAsState()
            val isDarkTheme = isSystemInDarkTheme()
            
            /** 观察全局动态颜色方案 */
            val dynamicColorScheme by DynamicThemeManager.observeColorScheme()
            
            LaunchedEffect(nowPlaying?.albumArtUri, isDarkTheme) {
                val albumArtUri = nowPlaying?.albumArtUri
                if (!albumArtUri.isNullOrBlank()) {
                    try {
                        /** 优化：将专辑封面颜色提取移到后台线程，避免阻塞UI线程 */
                        val colors = withContext(Dispatchers.Default) {
                            AlbumColorExtractor.extractColorsFromAlbumArt(
                                context = this@MainActivity,
                                albumArtUri = albumArtUri,
                                isDarkTheme = isDarkTheme
                            )
                        }
                        DynamicThemeManager.updateColorScheme(colors)
                    } catch (e: Exception) {
                        Timber.e("[AlbumArtExtractor] Failed to extract colors from album art $e")
                        DynamicThemeManager.clearColorScheme()
                    }
                } else {
                    DynamicThemeManager.clearColorScheme()
                }
            }

            // 应用主题并加载主界面
            ScoreMuseTheme(
                darkTheme = isDarkTheme,
                dynamicColorScheme = dynamicColorScheme
            ) {
                MainScreen()
            }
        }
    }
}