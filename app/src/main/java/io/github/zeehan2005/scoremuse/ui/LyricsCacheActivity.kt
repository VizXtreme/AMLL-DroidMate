package io.github.zeehan2005.scoremuse.ui

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.zeehan2005.scoremuse.data.repository.LyricsCacheRepository
import io.github.zeehan2005.scoremuse.global.CachedLyricEntry
import io.github.zeehan2005.scoremuse.ui.components.ManagementCard
import io.github.zeehan2005.scoremuse.ui.components.ManagementConfig
import io.github.zeehan2005.scoremuse.ui.components.ManagementPage
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 歌词缓存管理界面
 *
 * 允许用户查看、搜索、批量删除和导出已缓存的歌词。
 *
 * @see ManagementPage 底层使用通用管理模板
 */
class LyricsCacheActivity : BaseComposeActivity() {
    /** 待导出的歌词内容 */
    private var exportTtmlContent: String? = null

    /** 文件导出 Activity Result（SAF） */
    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/xml"),
    ) { uri ->
        uri?.let {
            try {
                val xmlContent =
                    exportTtmlContent ?: "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tt></tt>"
                contentResolver.openOutputStream(it)?.use { stream ->
                    stream.write(xmlContent.toByteArray(StandardCharsets.UTF_8))
                }
                Toast.makeText(this, "导出成功", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "导出失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Composable
    override fun RenderContent() {
        val repository = remember { LyricsCacheRepository(applicationContext) }
        var cacheEntries by remember { mutableStateOf(repository.getAll()) }

        ManagementPage(
            entries = cacheEntries,
            config = ManagementConfig.build(
                title = "管理缓存歌词",
                itemKey = { it.id },
            ) {
                searchPlaceholder = "搜索 ( 标题 / 歌手 / 来源 )"
                selectable = true

                searchPredicate = { entry, query ->
                    entry.title.lowercase().contains(query) ||
                        entry.artist.lowercase().contains(query) ||
                        entry.source.lowercase().contains(query)
                }

                onClearAll = {
                    repository.clearAll()
                    cacheEntries = repository.getAll()
                }

                onDeleteSelected = { ids ->
                    ids.forEach { repository.deleteById(it) }
                    cacheEntries = repository.getAll()
                }

                renderItem = { entry, isSelectionMode, isSelected, onSelect ->
                    CachedEntryCard(
                        entry = entry,
                        isSelectionMode = isSelectionMode,
                        isSelected = isSelected,
                        onSelect = onSelect,
                    )
                }
            },
            onBack = { finish() },
        )
    }
}

// ====================
// 子组件
// ====================

@Composable
private fun CachedEntryCard(
    entry: CachedLyricEntry,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    ManagementCard(
        showCheckbox = isSelectionMode,
        isChecked = isSelected,
        onCheckedChange = onSelect,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = entry.artist,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "来源: ${entry.source}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
            Text(
                text = "更新时间: ${formatTimestamp(entry.updatedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
    }
}

// ====================
// 工具
// ====================

private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
