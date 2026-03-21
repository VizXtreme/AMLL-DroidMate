package com.amll.droidmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amll.droidmate.ui.theme.AlbumColorExtractor
import com.amll.droidmate.ui.theme.DroidMateTheme
import com.amll.droidmate.ui.theme.DynamicThemeManager
import com.amll.droidmate.ui.screens.MainScreen
import com.amll.droidmate.ui.viewmodel.MainViewModel
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val viewModel: MainViewModel = viewModel()
            val nowPlaying by viewModel.nowPlayingMusic.collectAsState()
            val isDarkTheme = isSystemInDarkTheme()
            
            LaunchedEffect(nowPlaying?.albumArtUri, isDarkTheme) {
                val albumArtUri = nowPlaying?.albumArtUri
                if (!albumArtUri.isNullOrBlank()) {
                    try {
                        val colors = AlbumColorExtractor.extractColorsFromAlbumArt(
                            context = this@MainActivity,
                            albumArtUri = albumArtUri,
                            isDarkTheme = isDarkTheme
                        )
                        DynamicThemeManager.updateColorScheme(colors)
                    } catch (e: Exception) {
                        Timber.e(e, "Error extracting colors from album art")
                        DynamicThemeManager.clearColorScheme()
                    }
                } else {
                    DynamicThemeManager.clearColorScheme()
                }
            }
            
            val dynamicColorScheme by DynamicThemeManager.observeColorScheme()
            
            DroidMateTheme(
                darkTheme = isDarkTheme,
                dynamicColorScheme = dynamicColorScheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}