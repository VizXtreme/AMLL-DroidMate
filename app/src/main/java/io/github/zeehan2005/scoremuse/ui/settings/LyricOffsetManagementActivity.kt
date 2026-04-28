package io.github.zeehan2005.scoremuse.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.zeehan2005.scoremuse.service.MediaInfoService
import io.github.zeehan2005.scoremuse.global.AppSettings
import io.github.zeehan2005.scoremuse.ui.BaseComposeActivity
import kotlinx.coroutines.launch

/**
 * 歌词时间偏移管理界面
 *
 * 这个 Activity 允许用户查看和管理所有已保存的歌词时间偏移配置。
 * 用户可以：
 * - 查看不同歌曲/设备的偏移设置
 * - 搜索特定的偏移记录
 * - 手动添加新的偏移配置
 * - 删除不需要的偏移记录
 * - 批量清空所有偏移
 *
 * **使用场景**：
 * 当用户使用不同的音频设备（如蓝牙耳机、有线音箱）时，
 * 由于硬件延迟不同，歌词可能会与音乐不同步。
 * 通过这个界面，用户可以为每个设备单独配置偏移值，
 * 应用会自动识别并应用正确的偏移。
 */
class LyricOffsetManagementActivity : BaseComposeActivity() {
    @Composable
    override fun RenderContent() {
        // 渲染歌词偏移管理页面
        LyricOffsetManagementPage(onBack = { finish() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LyricOffsetManagementPage(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val mediaInfoService = remember { MediaInfoService(context) }

    var offsets by remember { mutableStateOf(AppSettings.getLyricTimingOffsets(context)) }
    var query by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val displayOffsets = if (query.isBlank()) {
        offsets
    } else {
        offsets.filter { entry ->
            val lowerQuery = query.trim().lowercase()
            entry.title.lowercase().contains(lowerQuery) ||
                entry.artist.lowercase().contains(lowerQuery) ||
                entry.device.lowercase().contains(lowerQuery) ||
                entry.source.lowercase().contains(lowerQuery)
        }
    }

    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var device by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var offsetMsText by remember { mutableStateOf("0") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        mediaInfoService.startListening()
        onDispose { mediaInfoService.stopListening() }
    }

    fun openDialog(entry: AppSettings.LyricTimingOffset? = null) {
        title = entry?.title?.takeIf { it != "*" } ?: ""
        artist = entry?.artist?.takeIf { it != "*" } ?: ""
        device = entry?.device?.takeIf { it != "*" } ?: ""
        source = entry?.source?.takeIf { it != "*" } ?: ""
        offsetMsText = entry?.offsetMs?.toString() ?: "0"
        errorMessage = null
        showDialog = true
    }

    fun saveEntry() {
        val ms = offsetMsText.toLongOrNull()
        if (ms == null) {
            errorMessage = "请输入有效的毫秒值"
            return
        }
        if (title.isBlank() || artist.isBlank() || device.isBlank() || source.isBlank()) {
            errorMessage = "歌曲、歌手、输出设备、来源均不能为空（可用 *）"
            return
        }
        coroutineScope.launch {
            AppSettings.setLyricTimingOffset(
                context,
                title.trim(),
                artist.trim(),
                device.trim(),
                ms,
                source.trim()
            )
            showDialog = false
        }
    }

            val topAppBarState = rememberTopAppBarState()
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = Modifier.Companion.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("管理时间轴偏移") },
                navigationIcon = {
                    FilledIconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    FilledIconButton(
                        onClick = { },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "删除所有")
                    }
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.Companion.statusBarsPadding()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openDialog(null) },
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加偏移")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                "所有匹配的偏移规则将会叠加。",
                style = MaterialTheme.typography.bodySmall
            )

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("搜索") },
                placeholder = { Text("输入 歌曲 / 歌手 / 设备 / 来源") },
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Text(
                text = "共 ${displayOffsets.size} 条",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.Companion.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            if (displayOffsets.isEmpty()) {
                Text("当前没有已保存的偏移设置。", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(
                    modifier = Modifier.Companion.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        displayOffsets,
                        key = { "${it.title}-${it.artist}-${it.device}-${it.source}" }) { entry ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Row(
                                modifier = Modifier.Companion
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Companion.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(Modifier.Companion.weight(1f)) {
                                    Text(
                                        "${entry.title} — ${entry.artist}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        "设备: ${entry.device}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "来源: ${entry.source}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "偏移: ${entry.offsetMs} ms",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = { openDialog(entry) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "编辑")
                                    }
                                    IconButton(onClick = {
                                        coroutineScope.launch {
                                            AppSettings.removeLyricTimingOffset(
                                                context,
                                                entry.title,
                                                entry.artist,
                                                entry.device,
                                                entry.source
                                            )
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "删除")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("清空偏移设置") },
                text = { Text("确认删除所有时间轴偏移设置吗？") },
                containerColor = MaterialTheme.colorScheme.background,
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                AppSettings.clearLyricTimingOffsets(context)
                                showClearDialog = false
                            }
                        }
                    ) {
                        Text("删除全部")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false; },
                containerColor = MaterialTheme.colorScheme.background,
                title = { Text("编辑偏移") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "填入歌曲、歌手、输出设备、来源（支持 * 通配符）。所有匹配规则将叠加。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("歌曲名称") },
                            modifier = Modifier.Companion.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = artist,
                            onValueChange = { artist = it },
                            label = { Text("歌手") },
                            modifier = Modifier.Companion.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = device,
                            onValueChange = { device = it },
                            label = { Text("输出设备 (如: Bluetooth, Speaker)") },
                            modifier = Modifier.Companion.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = source,
                            onValueChange = { source = it },
                            label = { Text("来源 (如: com.tencent.qqmusic)") },
                            modifier = Modifier.Companion.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = offsetMsText,
                            onValueChange = { offsetMsText = it },
                            label = { Text("偏移 (毫秒)") },
                            modifier = Modifier.Companion.fillMaxWidth()
                        )

                        errorMessage?.takeIf { it.isNotBlank() }?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { saveEntry() }) {
                        Text("保存")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                    }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}