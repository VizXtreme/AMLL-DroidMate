package io.github.zeehan2005.scoremuse.ui

// suppress icon deprecation where used
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.activity.compose.BackHandler
import androidx.compose.ui.unit.dp
import io.github.zeehan2005.scoremuse.data.repository.LyricsCacheRepository
import io.github.zeehan2005.scoremuse.global.CachedLyricEntry
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 歌词缓存管理界面
 * 
 * 这个 Activity 允许用户查看和管理已缓存的歌词。
 * 主要功能包括：
 * - 查看所有已缓存的歌词条目
 * - 搜索特定歌曲的缓存
 * - 删除单条缓存记录
 * - 批量清空所有缓存
 * - 查看缓存占用空间
 * 
 * **缓存策略**：
 * - 自动缓存：每次成功加载歌词后自动保存
 * - 智能匹配：通过歌曲 ID、标题、艺术家多重匹配
 * - 持久化存储：缓存在 SQLite 数据库中
 * - 过期清理：长期未使用的缓存可被清理
 * 
 * **使用场景**：
 * - 离线环境下使用已缓存的歌词
 * - 减少重复网络请求，节省流量
 * - 加快歌词加载速度
 */
class LyricsCacheActivity : BaseComposeActivity() {
    /** 存储要导出的歌词内容 */
    private var exportTtmlContent: String? = null
    
    /** 使用新的 Activity Result API */
    val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/xml")
    ) { uri ->
        uri?.let {
            try {
                val xmlContent = exportTtmlContent ?: "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tt></tt>"
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(xmlContent.toByteArray(StandardCharsets.UTF_8))
                }
                Toast.makeText(
                    this,
                    "导出成功",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "导出失败: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    @Composable
    override fun RenderContent() {
        /** 获取歌词缓存仓库 */
        val repository = LyricsCacheRepository(applicationContext)
        // 渲染歌词缓存管理页面
        LyricsCachePage(
            repository = repository,
            onBack = { finish() },
            context = this // 使用 Activity 上下文
        )
    }
    

}

@Composable
private fun LyricsCachePage(
    repository: LyricsCacheRepository,
    onBack: () -> Unit,
    context: android.content.Context
) {
    var query by remember { mutableStateOf("") }
    var cacheEntries by remember { mutableStateOf(repository.getAll()) }
    var showClearDialog by remember { mutableStateOf(false) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedEntries by remember { mutableStateOf<Set<String>>(emptySet()) }

    val displayEntries = if (query.isBlank()) {
        cacheEntries
    } else {
        repository.search(query)
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    val handleSelectAll = {
        selectedEntries = if (selectedEntries.size == displayEntries.size) {
            emptySet()
        } else {
            displayEntries.map { it.id }.toSet()
        }
    }

    val handleDeleteSelected = {
        selectedEntries.forEach { repository.deleteById(it) }
        selectedEntries = emptySet()
        isSelectionMode = false
    }

//    run {
//        if (selectedEntries.isNotEmpty()) {
//            val selectedItems = displayEntries.filter { selectedEntries.contains(it.id) }
//
//            // 使用文件导出 API 导出第一个选中的条目
//            // 对于多个条目，我们可以逐个导出或打包成 zip
//            val entry = selectedItems.first()
//            val fileName = "${entry.title}_${entry.artist}_${entry.source}.xml"
//
//            // 存储要导出的歌词内容
////            onExport(entry.xmlContent)
//
//            // 启动文件选择器（使用新的 API）
//            (context as? android.app.Activity)?.let { activity ->
//                if (activity is LyricsCacheActivity) {
//                    activity.exportLauncher.launch(fileName)
//                }
//            }
//        }
//    }

    // 处理系统返回键
    BackHandler(isSelectionMode) {
        isSelectionMode = false
        selectedEntries = emptySet()
    }

    androidx.compose.material3.Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("管理缓存歌词") },
                navigationIcon = {
                    androidx.compose.material3.FilledIconButton(
                        onClick = if (isSelectionMode) {
                            { 
                                isSelectionMode = false
                                selectedEntries = emptySet()
                            }
                        } else {
                            onBack
                        },
                        colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isSelectionMode) "取消" else "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    if (isSelectionMode) {
                        // 选择模式下的操作按钮
                        androidx.compose.material3.FilledIconButton(
                            onClick = handleSelectAll,
                            colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = if (selectedEntries.size == displayEntries.size) Icons.Default.Deselect else Icons.Default.SelectAll,
                                contentDescription = if (selectedEntries.size == displayEntries.size) "取消全选" else "全选"
                            )
                        }
                        androidx.compose.material3.FilledIconButton(
                            onClick = handleDeleteSelected,
                            colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "删除")
                        }
//                        androidx.compose.material3.FilledIconButton(
//                            onClick = handleExportSelected,
//                            colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
//                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                                contentColor = MaterialTheme.colorScheme.onSurface
//                            )
//                        ) {
//                            Icon(Icons.Default.FileDownload, contentDescription = "导出")
//                        }
                    } else {
                        // 正常模式下的操作按钮
                        androidx.compose.material3.FilledIconButton(
                            onClick = { isSelectionMode = true },
                            colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(Icons.Default.ChecklistRtl, contentDescription = "选择")
                        }

                    }
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.statusBarsPadding()
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("搜索 ( 标题 / 歌手 / 来源 )") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = CircleShape,
                    singleLine = true
                )

                Text(
                    text = "共 ${displayEntries.size} 条",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayEntries, key = { entry -> entry.id }) { entry ->
                        CacheEntryItem(
                            entry = entry,
                            isSelectionMode = isSelectionMode,
                            isSelected = selectedEntries.contains(entry.id),
                            onSelect = {
                                selectedEntries = if (selectedEntries.contains(entry.id)) {
                                    selectedEntries.minus(entry.id)
                                } else {
                                    selectedEntries.plus(entry.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    )

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("清空缓存") },
            text = { Text("确认删除全部缓存歌词吗？") },
            containerColor = MaterialTheme.colorScheme.background,
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.clearAll()
                    }
                ) {
                    Text("删除全部")
                }
            },
            dismissButton = {
                TextButton(onClick = { }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun CacheEntryItem(
    entry: CachedLyricEntry,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = entry.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val displaySource = entry.source
                Text(
                    text = "来源: $displaySource",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "更新时间: ${formatTimestamp(entry.updatedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                if (isSelectionMode) {
                    // 选择模式下显示复选框
                    androidx.compose.material3.Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onSelect() }
                    )
                }
            }
        }
    }
}


private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}