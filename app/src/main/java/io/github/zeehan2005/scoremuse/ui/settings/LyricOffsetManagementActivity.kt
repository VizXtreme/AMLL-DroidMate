package io.github.zeehan2005.scoremuse.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.zeehan2005.scoremuse.global.AppSettings
import io.github.zeehan2005.scoremuse.service.MediaInfoService
import io.github.zeehan2005.scoremuse.ui.BaseComposeActivity
import io.github.zeehan2005.scoremuse.ui.components.FabData
import io.github.zeehan2005.scoremuse.ui.components.ManagementCard
import io.github.zeehan2005.scoremuse.ui.components.ManagementConfig
import io.github.zeehan2005.scoremuse.ui.components.ManagementPage
import kotlinx.coroutines.launch

/**
 * 歌词时间偏移管理界面
 *
 * 允许用户查看、搜索、添加、编辑和删除歌词时间偏移配置。
 *
 * @see ManagementPage 底层使用通用管理模板
 */
class LyricOffsetManagementActivity : BaseComposeActivity() {
    @Composable
    override fun RenderContent() {
        LyricOffsetManagementPage(onBack = { finish() })
    }
}

@Composable
private fun LyricOffsetManagementPage(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val mediaInfoService = remember { MediaInfoService(context) }

    var offsets by remember { mutableStateOf(AppSettings.getLyricTimingOffsets(context)) }
    var showDialog by remember { mutableStateOf(false) }

    // — 表单状态（供添加/编辑对话框用）
    var editTarget: AppSettings.LyricTimingOffset? by remember { mutableStateOf(null) }
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
        editTarget = entry
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
            errorMessage = "Please enter a valid millisecond value"
            return
        }
        if (title.isBlank() || artist.isBlank() || device.isBlank() || source.isBlank()) {
            errorMessage = "Song, artist, output device, and source cannot be empty (use * for all)"
            return
        }
        coroutineScope.launch {
            AppSettings.setLyricTimingOffset(
                context,
                title.trim(),
                artist.trim(),
                device.trim(),
                ms,
                source.trim(),
            )
            showDialog = false
            offsets = AppSettings.getLyricTimingOffsets(context)
        }
    }

    ManagementPage(
        entries = offsets,
        config = ManagementConfig.build(
            title = "Manage Timeline Offset",
            itemKey = { "${it.title}|${it.artist}|${it.device}|${it.source}" },
        ) {
            searchPlaceholder = "Search ( Song / Artist / Device / Source )"
            emptyText = "No saved offset settings currently."

            headerContent = {
                Text(
                    "All matching offset rules will be stacked.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            searchPredicate = { entry, query ->
                entry.title.lowercase().contains(query) ||
                    entry.artist.lowercase().contains(query) ||
                    entry.device.lowercase().contains(query) ||
                    entry.source.lowercase().contains(query)
            }

            fabData = FabData(
                icon = Icons.Default.Add,
                onClick = { openDialog(null) },
            )

            topBarActions = { showClear ->
                FilledIconButton(
                    onClick = showClear,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Delete All")
                }
            }

            onClearAll = {
                coroutineScope.launch {
                    AppSettings.clearLyricTimingOffsets(context)
                    offsets = AppSettings.getLyricTimingOffsets(context)
                }
            }

            renderItem = { entry, _, _, _ ->
                OffsetEntryCard(
                    entry = entry,
                    onEdit = { openDialog(entry) },
                    onDelete = {
                        coroutineScope.launch {
                            AppSettings.removeLyricTimingOffset(
                                context,
                                entry.title,
                                entry.artist,
                                entry.device,
                                entry.source,
                            )
                            offsets = AppSettings.getLyricTimingOffsets(context)
                        }
                    },
                )
            }
        },
        onBack = onBack,
    )

    // ===== 添加 / 编辑对话框（独立于模板，保持特定领域逻辑）=====
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = { Text(if (editTarget != null) "Edit Offset" else "Add Offset") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Enter song, artist, output device, source (supports * wildcard). All matching rules will be stacked.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                    Text(
                        "For output devices, please enter according to the quote rules.\nSpeaker: \"Speaker\"\nWired: \"Wired\"\nBluetooth device: \"Bluetooth (device name)\"\nEnglish brackets, with a space before the bracket.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Song Name") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = artist,
                        onValueChange = { artist = it },
                        label = { Text("Artist") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = device,
                        onValueChange = { device = it },
                        label = { Text("Output Device") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = source,
                        onValueChange = { source = it },
                        label = { Text("Source Package (e.g., com.netease.cloudmusic, com.tencent.qqmusic)") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = offsetMsText,
                        onValueChange = { offsetMsText = it },
                        label = { Text("Offset (ms)") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    errorMessage?.takeIf { it.isNotBlank() }?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { saveEntry() }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
        )
    }
}

// ====================
// 子组件
// ====================

@Composable
private fun OffsetEntryCard(
    entry: AppSettings.LyricTimingOffset,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    ManagementCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "${entry.title} — ${entry.artist}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text("Device: ${entry.device}", style = MaterialTheme.typography.bodySmall)
                Text("Source: ${entry.source}", style = MaterialTheme.typography.bodySmall)
                Text("Offset: ${entry.offsetMs} ms", style = MaterialTheme.typography.bodySmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
