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
                source.trim(),
            )
            showDialog = false
            offsets = AppSettings.getLyricTimingOffsets(context)
        }
    }

    ManagementPage(
        entries = offsets,
        config = ManagementConfig.build(
            title = "管理时间轴偏移",
            itemKey = { "${it.title}|${it.artist}|${it.device}|${it.source}" },
        ) {
            searchPlaceholder = "搜索 ( 歌曲 / 歌手 / 设备 / 来源 )"
            emptyText = "当前没有已保存的偏移设置。"

            headerContent = {
                Text(
                    "所有匹配的偏移规则将会叠加。",
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
                    Icon(Icons.Default.DeleteSweep, contentDescription = "删除所有")
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
            title = { Text(if (editTarget != null) "编辑偏移" else "添加偏移") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "填入歌曲、歌手、输出设备、来源（支持 * 通配符）。所有匹配规则将叠加。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                    Text(
                        "对于输出设备，请按照引号规则填写。\n扬声器: \"Speaker\"\n有线音频: \"Wired\"\n蓝牙设备: \"Bluetooth (设备名称)\"\n英文括号，括号前有空格。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("歌曲名称") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = artist,
                        onValueChange = { artist = it },
                        label = { Text("歌手") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = device,
                        onValueChange = { device = it },
                        label = { Text("输出设备") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = source,
                        onValueChange = { source = it },
                        label = { Text("来源包名 (如: com.netease.cloudmusic、com.tencent.qqmusic)") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = offsetMsText,
                        onValueChange = { offsetMsText = it },
                        label = { Text("偏移 (毫秒)") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    errorMessage?.takeIf { it.isNotBlank() }?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { saveEntry() }) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("取消") }
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
                Text("设备: ${entry.device}", style = MaterialTheme.typography.bodySmall)
                Text("来源: ${entry.source}", style = MaterialTheme.typography.bodySmall)
                Text("偏移: ${entry.offsetMs} ms", style = MaterialTheme.typography.bodySmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "编辑")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "删除")
                }
            }
        }
    }
}
