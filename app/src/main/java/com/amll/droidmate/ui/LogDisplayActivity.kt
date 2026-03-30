package com.amll.droidmate.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.amll.droidmate.ui.base.BaseComposeActivity
import com.amll.droidmate.util.LogHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LogDisplayActivity : BaseComposeActivity() {
    @Composable
    override fun renderContent() {
        logDisplayPage(onBack = { finish() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun logDisplayPage(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // 日志列表状态
    var logEntries by remember { mutableStateOf(LogHelper.getAllLogs()) }
    val listState = rememberLazyListState()
    
    // 自动滚动控制
    var autoScrollEnabled by remember { mutableStateOf(true) }
    var isPaused by remember { mutableStateOf(false) }
    
    // 日志记录控制
    var isLoggingPaused by remember { mutableStateOf(false) }
    
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
                    appendLine("=== DroidMate 日志导出 ===")
                    appendLine("导出时间：${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
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
    
    // 实时刷新日志
    LaunchedEffect(Unit) {
        while (true) {
            delay(500) // 每 500ms 刷新一次
            
            // 如果暂停了记录，就不更新日志列表
            if (!isLoggingPaused) {
                logEntries = LogHelper.getAllLogs()
                
                // 如果启用了自动滚动且未暂停，滚动到最后
                if (autoScrollEnabled && !isPaused && logEntries.isNotEmpty()) {
                    scope.launch {
                        listState.scrollToItem(logEntries.lastIndex)
                    }
                }
            }
        }
    }
    
    // 监听列表滚动，如果用户手动滚动则暂停自动滚动
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            isPaused = true
            autoScrollEnabled = false
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // 顶部工具栏
        TopAppBar(
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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回"
                    )
                }
            },
            actions = {
                // 暂停/继续记录按钮
                IconButton(onClick = { 
                    isLoggingPaused = !isLoggingPaused
                    val action = if (isLoggingPaused) "暂停" else "继续"
                    Toast.makeText(context, "已$action 记录日志", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = if (isLoggingPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isLoggingPaused) "继续记录" else "暂停记录",
                        tint = if (isLoggingPaused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // 清除日志按钮
                IconButton(onClick = { 
                    LogHelper.clearLogs()
                    logEntries = emptyList()
                    Toast.makeText(context, "日志已清除", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "清除日志",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                
                // 导出日志按钮
                IconButton(onClick = {
                    val fileName = "droidmate_log_${System.currentTimeMillis()}.txt"
                    saveFileLauncher.launch(fileName)
                }) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "导出日志"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
        
        // 暂停/恢复自动滚动提示
        AnimatedVisibility(visible = isPaused || !autoScrollEnabled) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        // 立即滚动到底部按钮
                        Button(
                            onClick = {
                                scope.launch {
                                    if (logEntries.isNotEmpty()) {
                                        listState.scrollToItem(logEntries.lastIndex)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "底部",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (logEntries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
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
                        logEntryItem(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun logEntryItem(entry: LogHelper.LogEntry) {
    val (bgColor, textColor) = when (entry.level) {
        "V" -> Color(0xFF9E9E9E) to Color(0xFFBDBDBD)
        "D" -> Color(0xFF2196F3).copy(alpha = 0.1f) to Color(0xFF1976D2)
        "I" -> Color(0xFF4CAF50).copy(alpha = 0.1f) to Color(0xFF388E3C)
        "W" -> Color(0xFFFF9800).copy(alpha = 0.1f) to Color(0xFFF57C00)
        "E", "A" -> Color(0xFFF44336).copy(alpha = 0.1f) to Color(0xFFD32F2F)
        else -> Color.Transparent to MaterialTheme.colorScheme.onSurface
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 第一行：时间和日志级别
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
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
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            // 第三行：日志内容
            Text(
                text = entry.message,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
