package com.amll.droidmate.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amll.droidmate.ui.theme.DroidMateTheme
import com.amll.droidmate.ui.theme.DynamicThemeManager

class AnimationSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            val dynamicColorScheme by DynamicThemeManager.observeColorScheme()

            DroidMateTheme(
                darkTheme = isDarkTheme,
                dynamicColorScheme = dynamicColorScheme
            ) {
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimationSettingsPage(onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimationSettingsPage(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val dynamicColorScheme by DynamicThemeManager.observeColorScheme()
    val rippleColor = dynamicColorScheme?.primary ?: MaterialTheme.colorScheme.primary

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = rippleColor,
        checkedTrackColor = rippleColor.copy(alpha = 0.5f),
        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
        uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.24f)
    )

    var enableSpring by remember { mutableStateOf(AppSettings.isAmllAnimationSpringEnabled(context)) }
    var enableScale by remember { mutableStateOf(AppSettings.isAmllAnimationScaleEnabled(context)) }
    var enableBlur by remember { mutableStateOf(AppSettings.isAmllAnimationBlurEnabled(context)) }
    var hidePassedLines by remember { mutableStateOf(AppSettings.isAmllAnimationHidePassedLinesEnabled(context)) }
    var wordFadeWidth by remember { mutableStateOf(AppSettings.getAmllAnimationWordFadeWidth(context)) }
    var fps by remember { mutableStateOf(AppSettings.getAmllAnimationFps(context)) }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        TopAppBar(
            title = { Text("动画设置") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    @Suppress("DEPRECATION")
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("启用弹性动画", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "允许歌词行按弹性曲线移动，提升滚动过渡效果。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Switch(
                            checked = enableSpring,
                            onCheckedChange = { enabled ->
                                enableSpring = enabled
                                AppSettings.setAmllAnimationSpringEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("启用缩放动画", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "开启后歌词当前行会有轻微缩放效果。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Switch(
                            checked = enableScale,
                            onCheckedChange = { enabled ->
                                enableScale = enabled
                                AppSettings.setAmllAnimationScaleEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("启用模糊过渡", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "开启后歌词行过渡时会出现模糊渐变效果。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Switch(
                            checked = enableBlur,
                            onCheckedChange = { enabled ->
                                enableBlur = enabled
                                AppSettings.setAmllAnimationBlurEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("隐藏已过歌词", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "已播放的歌词行将自动淡出并隐藏。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Switch(
                            checked = hidePassedLines,
                            onCheckedChange = { enabled ->
                                hidePassedLines = enabled
                                AppSettings.setAmllAnimationHidePassedLinesEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }

                    Column {
                        Text(
                            text = "帧率: ${fps} FPS",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Slider(
                            value = fps.toFloat(),
                            onValueChange = { value ->
                                fps = value.toInt()
                                AppSettings.setAmllAnimationFps(context, fps)
                            },
                            valueRange = 15f..240f,
                            colors = SliderDefaults.colors(
                                thumbColor = rippleColor,
                                activeTrackColor = rippleColor
                            )
                        )
                    }

                    Column {
                        Text(
                            text = "文字渐变宽度: ${"%.2f".format(wordFadeWidth)}",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Slider(
                            value = wordFadeWidth,
                            onValueChange = { value ->
                                wordFadeWidth = value
                                AppSettings.setAmllAnimationWordFadeWidth(context, value)
                            },
                            valueRange = 0.0f..2.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = rippleColor,
                                activeTrackColor = rippleColor
                            )
                        )
                    }
                }
            }
        }
    }
}
