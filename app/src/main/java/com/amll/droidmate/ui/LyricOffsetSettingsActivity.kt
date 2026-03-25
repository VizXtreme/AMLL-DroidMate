package com.amll.droidmate.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.amll.droidmate.service.MediaInfoService
import com.amll.droidmate.ui.theme.DroidMateTheme
import com.amll.droidmate.ui.theme.DynamicThemeManager
import com.amll.droidmate.util.AudioDeviceHelper

class LyricOffsetSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            val dynamicColorScheme by DynamicThemeManager.observeColorScheme()

            DroidMateTheme(
                darkTheme = isDarkTheme,
                dynamicColorScheme = dynamicColorScheme
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LyricOffsetSettingsPage(onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LyricOffsetSettingsPage(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val mediaInfoService = remember { MediaInfoService(context) }
    val nowPlaying by mediaInfoService.nowPlayingMusic.collectAsState()
    val currentDeviceName = AudioDeviceHelper.getCurrentOutputDeviceName(context)

    var offsets by remember { mutableStateOf(AppSettings.getLyricTimingOffsets(context)) }

    var songOffsetText by remember { mutableStateOf("0") }
    var songOffsetError by remember { mutableStateOf<String?>(null) }
    var deviceOffsetText by remember { mutableStateOf("0") }
    var deviceOffsetError by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        mediaInfoService.startListening()
        onDispose { mediaInfoService.stopListening() }
    }

    // Update input values when song/device changes
    // Note: MediaInfoService frequently emits updates (position/timestamp), so only react to title/artist changes
    LaunchedEffect(nowPlaying?.title, nowPlaying?.artist, currentDeviceName) {
        val currentSource = nowPlaying?.packageName ?: "*"
        songOffsetText = AppSettings.getLyricTimingOffset(context, nowPlaying?.title, nowPlaying?.artist, "*", currentSource)
            ?.toString() ?: "0"
        deviceOffsetText = AppSettings.getLyricTimingOffset(context, "*", "*", currentDeviceName)
            ?.toString() ?: "0"
    }

    fun saveSongOffset() {
        val ms = songOffsetText.toLongOrNull()
        if (ms == null) {
            songOffsetError = "请输入有效的毫秒值"
            return
        }
        songOffsetError = null
        nowPlaying?.let { current ->
            val currentSource = current.packageName ?: "*"
            AppSettings.setLyricTimingOffset(context, current.title, current.artist, "*", ms, currentSource)
            offsets = AppSettings.getLyricTimingOffsets(context)
        }
    }

    fun saveDeviceOffset() {
        val ms = deviceOffsetText.toLongOrNull()
        if (ms == null) {
            deviceOffsetError = "请输入有效的毫秒值"
            return
        }
        deviceOffsetError = null
        AppSettings.setLyricTimingOffset(context, "*", "*", currentDeviceName, ms)
        offsets = AppSettings.getLyricTimingOffsets(context)
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        TopAppBar(
            title = { Text("歌词时间轴偏移") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            actions = {
                IconButton(onClick = {
                    context.startActivity(Intent(context, LyricOffsetManagementActivity::class.java))
                }) {
                    Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = "管理")
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "所有匹配的偏移规则将会叠加。",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("基于当前歌曲", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            "当前歌曲：${nowPlaying?.title ?: "未知"} — ${nowPlaying?.artist ?: "未知"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "来源：${nowPlaying?.packageName ?: "*"}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        CapsuleOffsetControl(
                            offsetValue = songOffsetText,
                            onOffsetChange = { songOffsetText = it },
                            onDecrease = {
                                val value = (songOffsetText.toLongOrNull() ?: 0L) - 100L
                                songOffsetText = value.toString()
                            },
                            onIncrease = {
                                val value = (songOffsetText.toLongOrNull() ?: 0L) + 100L
                                songOffsetText = value.toString()
                            },
                            onSave = { saveSongOffset() }
                        )

                        songOffsetError?.takeIf { it.isNotBlank() }?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("基于输出设备", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            "当前设备：$currentDeviceName",
                            style = MaterialTheme.typography.bodySmall
                        )

                        CapsuleOffsetControl(
                            offsetValue = deviceOffsetText,
                            onOffsetChange = { deviceOffsetText = it },
                            onDecrease = {
                                val value = (deviceOffsetText.toLongOrNull() ?: 0L) - 100L
                                deviceOffsetText = value.toString()
                            },
                            onIncrease = {
                                val value = (deviceOffsetText.toLongOrNull() ?: 0L) + 100L
                                deviceOffsetText = value.toString()
                            },
                            onSave = { saveDeviceOffset() }
                        )

                        deviceOffsetError?.takeIf { it.isNotBlank() }?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun CapsuleOffsetControl(
    offsetValue: String,
    onOffsetChange: (String) -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left arrow button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = onDecrease,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "减少 100ms",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Center input field
            OutlinedTextField(
                value = offsetValue,
                onValueChange = onOffsetChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 4.dp),
                placeholder = { Text("0") },
                shape = RoundedCornerShape(25),
                textStyle = androidx.compose.ui.text.TextStyle(
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            )

            // Right arrow button (flipped left arrow)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = onIncrease,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        modifier = Modifier.graphicsLayer {
                            scaleX = -1f // Flip horizontally
                        },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "增加 100ms",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }

    // Save button below the capsule
    Button(
        onClick = onSave,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text("保存")
    }
}


