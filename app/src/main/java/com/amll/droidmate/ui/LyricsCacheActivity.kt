package com.amll.droidmate.ui

// suppress icon deprecation where used
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import com.amll.droidmate.data.repository.LyricsCacheRepository
import com.amll.droidmate.domain.model.CachedLyricEntry
import com.amll.droidmate.ui.base.BaseComposeActivity
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
    @Composable
    override fun renderContent() {
        // 获取歌词缓存仓库
        val repository = LyricsCacheRepository(applicationContext)
        // 渲染歌词缓存管理页面
        LyricsCachePage(
            repository = repository,
            onBack = { finish() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LyricsCachePage(
    repository: LyricsCacheRepository,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var cacheEntries by remember { mutableStateOf(repository.getAll()) }
    var showClearDialog by remember { mutableStateOf(false) }

    val displayEntries = if (query.isBlank()) {
        cacheEntries
    } else {
        repository.search(query)
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    androidx.compose.material3.Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("管理缓存歌词") },
                navigationIcon = {
                    androidx.compose.material3.FilledIconButton(
                        onClick = onBack,
                        colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
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
                actions = {
                    androidx.compose.material3.FilledIconButton(
                        onClick = { showClearDialog = true },
                        colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "删除所有")
                    }
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("搜索") },
                placeholder = { Text("输入 标题 / 歌手 / 来源") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                items(displayEntries, key = { it.id }) { entry ->
                    CacheEntryItem(
                        entry = entry,
                        onDelete = {
                            repository.deleteById(entry.id)
                            cacheEntries = repository.getAll()
                        }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("清空缓存") },
            text = { Text("确认删除全部缓存歌词吗？") },
            containerColor = MaterialTheme.colorScheme.background,
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.clearAll()
                        cacheEntries = repository.getAll()
                        showClearDialog = false
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
}

@Composable
private fun CacheEntryItem(
    entry: CachedLyricEntry,
    onDelete: () -> Unit
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
                val displaySource = if (entry.source.contains("AMLL TTML DB(") && !entry.source.contains("(基于歌名)")) {
                    entry.source.replaceFirst(
                        Regex("AMLL TTML DB\\([^)]*\\)"),
                        "$0 (基于歌名)"
                    )
                } else {
                    entry.source
                }
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
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除"
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