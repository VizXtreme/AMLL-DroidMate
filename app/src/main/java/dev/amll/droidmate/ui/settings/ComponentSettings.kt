package dev.amll.droidmate.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.amll.droidmate.global.AMLLSettings
import io.github.zeehan2005.scoremuse.global.theme.DynamicThemeManager
import io.github.zeehan2005.scoremuse.ui.BaseComposeActivity
import io.github.zeehan2005.scoremuse.ui.components.SwitchWithIcon
import io.github.zeehan2005.scoremuse.ui.LyricsVisualLayer
import kotlinx.coroutines.delay
import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.LyricWord
import io.github.zeehan2005.scoremuse.global.LyricsMetadata
import io.github.zeehan2005.scoremuse.global.NowPlayingMusic
import io.github.zeehan2005.scoremuse.global.UnifiedLyrics

class ComponentSettings : BaseComposeActivity() {
    @Composable
    override fun RenderContent() {
        ComponentSettingsPage(onBack = { finish() })
    }
}

@Composable
private fun ComponentSettingsPage(onBack: () -> Unit) {
    val context = LocalContext.current

    val dynamicColorScheme by DynamicThemeManager.observeColorScheme()
    val rippleColor = dynamicColorScheme?.primary ?: MaterialTheme.colorScheme.primary
    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = rippleColor,
        checkedTrackColor = rippleColor.copy(alpha = 0.5f),
        checkedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
        uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.24f)
    )

    var blurEnabled by remember {
        mutableStateOf(AMLLSettings.isAmllAnimationBlurEnabled(context) ?: true)
    }
    var springEnabled by remember {
        mutableStateOf(AMLLSettings.isAmllAnimationSpringEnabled(context) ?: true)
    }
    var backgroundRendererEnabled by remember {
        mutableStateOf(AMLLSettings.isAmllBackgroundRendererEnabled(context) ?: true)
    }

    val previewWordDurationMs = 500L
    val previewLongWordDurationMs = 1000L
    val previewDurationMs = 4 * previewWordDurationMs + 2 * previewLongWordDurationMs
    var previewTimeMs by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(100L)
            previewTimeMs = (previewTimeMs + 100L) % previewDurationMs
        }
    }

    val previewAlbumArtUri = remember(context) {
        "android.resource://${context.packageName}/drawable/background_blue_black_light_1591226"
    }
    val previewLyrics = remember {
        UnifiedLyrics(
            metadata = LyricsMetadata(
                title = "Preview",
                artist = "AMLL",
                duration = previewDurationMs
            ),
            lines = listOf(
                LyricLine(
                    startTime = 0L,
                    endTime = 2 * previewWordDurationMs + previewLongWordDurationMs,
                    text = "欢迎使用 DroidMate",
                    words = listOf(
                        LyricWord("欢迎", 0L, previewWordDurationMs),
                        LyricWord("使用 ", previewWordDurationMs, 2 * previewWordDurationMs),
                        LyricWord("DroidMate", 2 * previewWordDurationMs, 2 * previewWordDurationMs + previewLongWordDurationMs)
                    )
                ),
                LyricLine(
                    startTime = 2 * previewWordDurationMs + previewLongWordDurationMs,
                    endTime = 4 * previewWordDurationMs + 2 * previewLongWordDurationMs,
                    text = "Welcome to DroidMate",
                    words = listOf(
                        LyricWord("Welcome ", 2 * previewWordDurationMs + previewLongWordDurationMs, 3 * previewWordDurationMs + previewLongWordDurationMs),
                        LyricWord("to ", 3 * previewWordDurationMs + previewLongWordDurationMs, 4 * previewWordDurationMs + previewLongWordDurationMs),
                        LyricWord("DroidMate", 4 * previewWordDurationMs + previewLongWordDurationMs,4 * previewWordDurationMs + 2 * previewLongWordDurationMs )
                    )
                )
            )
        )
    }
    val previewNowPlaying = remember(previewTimeMs, previewAlbumArtUri) {
        NowPlayingMusic(
            title = "Preview",
            artist = "AMLL",
            duration = previewDurationMs,
            currentPosition = previewTimeMs,
            isPlaying = true,
            albumArtUri = previewAlbumArtUri
        )
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("歌词组件设置") },
                navigationIcon = {
                    FilledIconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                LyricsVisualLayer(
                    nowPlaying = previewNowPlaying,
                    lyrics = previewLyrics,
                    currentTime = previewTimeMs,
                    webViewReloadKey = 0,
                    onLineSeek = {},
                    debugSource = "GraphicSettingsPreview",
                    useAndroidBlurOverride = !backgroundRendererEnabled,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = "歌词动画",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val newState = !blurEnabled
                        blurEnabled = newState
                        AMLLSettings.setAmllAnimationBlurEnabled(context, newState)
                    },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                        Text("歌词模糊", color = MaterialTheme.colorScheme.onSurface)
                    }
                    SwitchWithIcon(
                        checked = blurEnabled,
                        onCheckedChange = { enabled ->
                            blurEnabled = enabled
                            AMLLSettings.setAmllAnimationBlurEnabled(context, enabled)
                        },
                        colors = switchColors
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val newState = !springEnabled
                        springEnabled = newState
                        AMLLSettings.setAmllAnimationSpringEnabled(context, newState)
                    },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                        Text("歌词弹簧", color = MaterialTheme.colorScheme.onSurface)
                    }
                    SwitchWithIcon(
                        checked = springEnabled,
                        onCheckedChange = { enabled ->
                            springEnabled = enabled
                            AMLLSettings.setAmllAnimationSpringEnabled(context, enabled)
                        },
                        colors = switchColors
                    )
                }
            }

            Text(
                text = "背景",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val newState = !backgroundRendererEnabled
                        backgroundRendererEnabled = newState
                        AMLLSettings.setAmllBackgroundRendererEnabled(context, newState)
                    },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                        Text("AMLL 背景渲染器", color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = "关闭后使用 Android 实现模糊",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    SwitchWithIcon(
                        checked = backgroundRendererEnabled,
                        onCheckedChange = { enabled ->
                            backgroundRendererEnabled = enabled
                            AMLLSettings.setAmllBackgroundRendererEnabled(context, enabled)
                        },
                        colors = switchColors
                    )
                }
            }
        }
    }
}