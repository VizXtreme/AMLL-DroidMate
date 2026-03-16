package com.amll.droidmate.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Storage
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("歌词时间轴偏移") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, LyricOffsetManagementActivity::class.java))
                    }) {
                        Icon(Icons.Default.Storage, contentDescription = "管理")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding(),
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
                        Text("基于当前歌曲", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "当前歌曲：${nowPlaying?.title ?: "未知"} — ${nowPlaying?.artist ?: "未知"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "来源：${nowPlaying?.packageName ?: "*"}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                val value = (songOffsetText.toLongOrNull() ?: 0L) - 100L
                                songOffsetText = value.toString()
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "减 100ms")
                            }

                            OutlinedTextField(
                                value = songOffsetText,
                                onValueChange = { songOffsetText = it },
                                singleLine = true,
                                modifier = Modifier.width(120.dp),
                                label = { Text("ms") }
                            )

                            IconButton(onClick = {
                                val value = (songOffsetText.toLongOrNull() ?: 0L) + 100L
                                songOffsetText = value.toString()
                            }) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "加 100ms")
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(onClick = { saveSongOffset() }) {
                                Text("保存")
                            }
                        }

                        songOffsetError?.takeIf { it.isNotBlank() }?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("基于输出设备", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "当前设备：$currentDeviceName",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                val value = (deviceOffsetText.toLongOrNull() ?: 0L) - 100L
                                deviceOffsetText = value.toString()
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "减 100ms")
                            }

                            OutlinedTextField(
                                value = deviceOffsetText,
                                onValueChange = { deviceOffsetText = it },
                                singleLine = true,
                                modifier = Modifier.width(120.dp),
                                label = { Text("ms") }
                            )

                            IconButton(onClick = {
                                val value = (deviceOffsetText.toLongOrNull() ?: 0L) + 100L
                                deviceOffsetText = value.toString()
                            }) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "加 100ms")
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(onClick = { saveDeviceOffset() }) {
                                Text("保存")
                            }
                        }

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
