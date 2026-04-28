package io.github.zeehan2005.scoremuse.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.zeehan2005.scoremuse.ui.BaseComposeActivity
import io.github.zeehan2005.scoremuse.components.LogHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 日志显示和管理界面
 *
 * 这个 Activity 提供了一个完整的日志查看器，允许用户：
 * - 实时查看应用运行日志
 * - 按等级筛选日志（DEBUG、INFO、WARN、ERROR）
 * - 搜索特定日志条目
 * - 导出日志到文件
 * - 清空所有日志
 * - 暂停/恢复自动滚动
 * - 控制日志记录开关
 *
 * **功能特点**：
 * - 实时更新：新日志自动追加到列表末尾
 * - 自动滚动：默认滚动到最新日志（可暂停）
 * - 语法高亮：不同等级使用不同颜色
 * - 持久化：日志等级筛选设置会保存
 * - SAF 支持：使用 Storage Access Framework 导出日志
 *
 * **使用场景**：
 * - 调试问题时查看详细日志
 * - 向开发者反馈时导出日志
 * - 监控应用运行状态
 */
class LogDisplayActivity : BaseComposeActivity() {
    @Composable
    override fun RenderContent() {
        // 渲染日志显示页面
        LogDisplayPage(onBack = { finish() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogDisplayPage(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 日志列表状态
    var logEntries by remember { mutableStateOf(LogHelper.getAllLogs()) }
    val listState = rememberLazyListState()

    // 自动滚动控制
    var autoScrollEnabled by remember { mutableStateOf(true) }
    var isPaused by remember { mutableStateOf(false) }

    // 日志记录控制 - 从持久化存储读取
    var isLoggingPaused by remember {
        mutableStateOf(LogHelper.isLoggingPaused())
    }

    // 日志等级筛选 - 从持久化存储读取
    var showFilterDropdown by remember { mutableStateOf(false) }
    var minLogLevel by remember {
        mutableStateOf(LogHelper.getMinLogLevel())
    }

    // 日志统计
    val stats by remember { mutableStateOf(LogHelper.getLogStats()) }

    // 文件保存启动器 - 使用 SAF (Storage Access Framework)
    // 系统会自动打开保存对话框，默认在 Downloads，用户可以选择其他位置
    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                val logText = buildString {
                    appendLine("=== ScoreMuse 日志导出 ===")
                    appendLine(
                        "导出时间：${
                            SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                Locale.getDefault()
                            ).format(Date())
                        }"
                    )
                    appendLine("日志条数：${logEntries.size}")
                    appendLine("========================\n")

                    logEntries.forEach { entry ->
                        appendLine(entry.toLogString())
                    }
                }

                outputStream.write(logText.toByteArray())
                Toast.makeText(context, "日志已保存", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "保存取消", Toast.LENGTH_SHORT).show()
        }
    }

    // 实时刷新日志 - 持续轮询模式
    LaunchedEffect(Unit) {
        while (true) {
            delay(500) // 每 500ms 刷新一次

            // 无论暂停状态如何，都根据选择的等级过滤日志（自动包含更高等级）
            // 这样在暂停时也能看到筛选效果
            logEntries = LogHelper.getFilteredLogsByMinLevel(minLogLevel)

            // 如果未暂停记录且启用了自动滚动，滚动到最后
            if (!isLoggingPaused && autoScrollEnabled && !isPaused && logEntries.isNotEmpty()) {
                scope.launch {
                    listState.scrollToItem(logEntries.lastIndex)
                }
            }
        }
    }

    // 监听筛选等级变化，立即更新日志列表
    LaunchedEffect(minLogLevel) {
        logEntries = LogHelper.getFilteredLogsByMinLevel(minLogLevel)
    }

    // 监听列表滚动，如果用户手动滚动则暂停自动滚动
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            isPaused = true
            autoScrollEnabled = false
        }
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = Modifier.Companion
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("应用日志")
                        Text(
                            text = "共 ${stats.total} 条 | V:${stats.verboseCount} D:${stats.debugCount} I:${stats.infoCount} W:${stats.warnCount} E:${stats.errorCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    FilledIconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.filledIconButtonColors(
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
                actions = {
                    // 日志等级筛选按钮
                    Box {
                        FilledIconButton(
                            onClick = { showFilterDropdown = true },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "筛选日志",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // 筛选下拉菜单
                        DropdownMenu(
                            expanded = showFilterDropdown,
                            onDismissRequest = { showFilterDropdown = false },
                            containerColor = MaterialTheme.colorScheme.background
                        ) {
                            Text(
                                text = "日志等级",
                                modifier = Modifier.Companion.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // 各个等级选项（按等级排序）
                            val levels = listOf(
                                "V" to "详细 (全部)",
                                "D" to "调试",
                                "I" to "信息",
                                "W" to "警告",
                                "E" to "错误"
                            )

                            levels.forEach { (level, label) ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.Companion.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Companion.CenterVertically
                                        ) {
                                            Text(
                                                text = label,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            RadioButton(
                                                selected = minLogLevel == level,
                                                onClick = {
                                                    minLogLevel = level
                                                    // 保存到持久化存储（即使暂停也能筛选）
                                                    LogHelper.setMinLogLevel(level)
                                                    showFilterDropdown = false
                                                },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    },
                                    onClick = {
                                        minLogLevel = level
                                        // 保存到持久化存储（即使暂停也能筛选）
                                        LogHelper.setMinLogLevel(level)
                                        showFilterDropdown = false
                                    },
                                    colors = MenuItemColors(
                                        textColor = MaterialTheme.colorScheme.onSurface,
                                        leadingIconColor = Color.Companion.Unspecified,
                                        trailingIconColor = Color.Companion.Unspecified,
                                        disabledTextColor = Color.Companion.Unspecified,
                                        disabledLeadingIconColor = Color.Companion.Unspecified,
                                        disabledTrailingIconColor = Color.Companion.Unspecified
                                    )
                                )
                            }
                        }
                    }

                    // 暂停/继续记录按钮
                    FilledIconButton(
                        onClick = {
                            isLoggingPaused = !isLoggingPaused
                            // 保存到持久化存储
                            LogHelper.setLoggingPaused(isLoggingPaused)
                            val action = if (isLoggingPaused) "暂停" else "继续"
                            Toast.makeText(context, "已$action 记录日志", Toast.LENGTH_SHORT).show()
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = if (isLoggingPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isLoggingPaused) "继续记录" else "暂停记录",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 清除日志按钮
                    FilledIconButton(
                        onClick = {
                            LogHelper.clearLogs()
                            logEntries = emptyList()
                            Toast.makeText(context, "日志已清除", Toast.LENGTH_SHORT).show()
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "清除日志",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 导出日志按钮
                    FilledIconButton(
                        onClick = {
                            val fileName = "ScoreMuse_log_${System.currentTimeMillis()}.txt"
                            saveFileLauncher.launch(fileName)
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "导出日志"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        // 暂停/恢复自动滚动提示
        AnimatedVisibility(visible = isPaused || !autoScrollEnabled) {
            Card(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    Text(
                        text = if (isPaused) "已暂停自动滚动" else "自动滚动已关闭",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row {
                        // 恢复自动滚动按钮
                        IconButton(onClick = {
                            isPaused = false
                            autoScrollEnabled = true
                            if (logEntries.isNotEmpty()) {
                                scope.launch {
                                    listState.scrollToItem(logEntries.lastIndex)
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (isPaused) "恢复滚动" else "暂停滚动",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // 立即滚动到底部按钮
                        IconButton(onClick = {
                            scope.launch {
                                if (logEntries.isNotEmpty()) {
                                    listState.scrollToItem(logEntries.lastIndex)
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "滚动到底部",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 日志列表
        SelectionContainer {
            LazyColumn(
                state = listState,
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (logEntries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.Companion.fillMaxSize(),
                            contentAlignment = Alignment.Companion.Center
                        ) {
                            Text(
                                text = "暂无日志",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                } else {
                    items(logEntries, key = { it.id }) { entry ->
                        LogEntryItem(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun LogEntryItem(entry: LogHelper.LogEntry) {
    val (bgColor, textColor) = when (entry.level) {
        "V" -> Color(0xFFE1BEE7).copy(alpha = 0.15f) to Color(0xFF8E24AA) // 紫色，更醒目
        "D" -> Color(0xFF2196F3).copy(alpha = 0.1f) to Color(0xFF1976D2)
        "I" -> Color(0xFF4CAF50).copy(alpha = 0.1f) to Color(0xFF388E3C)
        "W" -> Color(0xFFFF9800).copy(alpha = 0.1f) to Color(0xFFF57C00)
        "E", "A" -> Color(0xFFF44336).copy(alpha = 0.1f) to Color(0xFFD32F2F)
        else -> Color.Companion.Transparent to MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.Companion.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 第一行：时间和日志级别
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Text(
                    text = entry.formattedTime(),
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor.copy(alpha = 0.7f)
                )

                // 右上角显示来源标签
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = textColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = entry.tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                        modifier = Modifier.Companion.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1
                    )
                }
            }

            // 第二行：日志级别标签
            Row {
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = textColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "[${entry.level}]",
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor,
                        modifier = Modifier.Companion.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // 第三行：日志内容
            Text(
                text = entry.message,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.Companion.fillMaxWidth()
            )
        }
    }
}