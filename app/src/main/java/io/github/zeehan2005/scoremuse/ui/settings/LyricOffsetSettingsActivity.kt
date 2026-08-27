package io.github.zeehan2005.scoremuse.ui.settings

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.zeehan2005.scoremuse.service.MediaInfoService
import io.github.zeehan2005.scoremuse.global.AppSettings
import io.github.zeehan2005.scoremuse.ui.BaseComposeActivity
import io.github.zeehan2005.scoremuse.components.AudioDeviceHelper

/**
 * 歌词时间偏移设置界面
 *
 * 这个 Activity 允许用户为当前播放的歌曲和音频设备配置时间偏移。
 * 用户可以：
 * - 查看当前歌曲和设备的偏移值
 * - 手动调整歌曲级别的偏移
 * - 手动调整设备级别的偏移
 * - 保存偏移到数据库
 * - 管理所有已保存的偏移记录
 *
 * **使用场景**：
 * 当用户使用蓝牙耳机或外部音箱时，由于硬件延迟，
 * 歌词可能会比音乐慢。通过这个界面，用户可以：
 * 1. 微调当前歌曲的偏移（如 +50ms）
 * 2. 为当前设备设置全局偏移（所有歌曲都应用）
 * 3. 系统会自动叠加两种偏移值
 *
 * **偏移计算**：
 * 总偏移 = 歌曲偏移 + 设备偏移
 * 例如：某首歌慢 50ms + 蓝牙设备慢 100ms = 总共 +150ms
 */
class LyricOffsetSettingsActivity : BaseComposeActivity() {
    @Composable
    override fun RenderContent() {
        // 渲染歌词偏移设置页面
        LyricOffsetSettingsPage(onBack = { finish() })
    }
}

@Composable
private fun LyricOffsetSettingsPage(onBack: () -> Unit) {
    val context = LocalContext.current
    rememberCoroutineScope()

    val mediaInfoService = remember { MediaInfoService(context) }
    val nowPlaying by mediaInfoService.nowPlayingMusic.collectAsState()
    val currentDeviceName = AudioDeviceHelper.getCurrentOutputDeviceName(context)

    /** 转换设备名称用于显示 */
    val displayDeviceName = when {
        currentDeviceName.startsWith("Bluetooth") -> currentDeviceName.replaceFirst("Bluetooth", "Bluetooth")
        currentDeviceName == "Wired" -> "Wired Audio"
        currentDeviceName == "Speaker" -> "Speaker"
        else -> currentDeviceName
    }

    /** 获取应用名称 */
    val appName = remember(nowPlaying?.packageName) {
        nowPlaying?.packageName?.let { pkg ->
            try {
                context.packageManager.getApplicationLabel(context.packageManager.getApplicationInfo(pkg, 0)).toString()
            } catch (e: Exception) { null }
        }
    }

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
        songOffsetText = AppSettings.getLyricTimingOffset(
            context,
            nowPlaying?.title,
            nowPlaying?.artist,
            "*",
            currentSource
        )
            ?.toString() ?: "0"
        deviceOffsetText = AppSettings.getLyricTimingOffset(context, "*", "*", currentDeviceName)
            ?.toString() ?: "0"
    }

    fun saveSongOffset() {
        val ms = songOffsetText.toLongOrNull()
        if (ms == null) {
            songOffsetError = "Please enter a valid millisecond value"
            return
        }
        songOffsetError = null
        nowPlaying?.let { current ->
            val currentSource = current.packageName ?: "*"
            AppSettings.setLyricTimingOffset(context, current.title, current.artist, "*", ms, currentSource)
        }
    }

    fun saveDeviceOffset() {
        val ms = deviceOffsetText.toLongOrNull()
        if (ms == null) {
            deviceOffsetError = "Please enter a valid millisecond value"
            return
        }
        deviceOffsetError = null
        AppSettings.setLyricTimingOffset(context, "*", "*", currentDeviceName, ms)
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Lyric Timeline Offset") },
                navigationIcon = {
                    FilledIconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    FilledIconButton(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    context,
                                    LyricOffsetManagementActivity::class.java
                                )
                            )
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.FormatListBulleted,
                            contentDescription = "Manage"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "All matching offset rules will be stacked.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Based on current song",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Current Song: ${nowPlaying?.title ?: "Unknown"} — ${nowPlaying?.artist ?: "Unknown"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Source：${appName ?: nowPlaying?.packageName ?: "*"}",
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
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Based on output device",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Current Device: $displayDeviceName",
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                        contentDescription = "Decrease 100ms",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                shape = androidx.compose.foundation.shape.RoundedCornerShape(25),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center
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
                        contentDescription = "Increase 100ms",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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
        Text("Save")
    }
}