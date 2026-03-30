package com.amll.droidmate.util

import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * 日志管理工具类
 * 
 * 提供全局日志捕获、查看和导出功能
 * 
 * ## 日志等级使用说明：
 * - [Timber.wtf] 致命错误，会导致 Activity 崩溃
 * - [Timber.e] 非预期错误，会造成功能出错
 * - [Timber.w] 非预期错误，但功能几乎不影响
 * - [Timber.i] 阶段完成或进入预期内的分支（包括预期的 404）
 * - [Timber.d] 阶段内部的处理逻辑
 * - [Timber.v] 持续性检查但发现没有变动
 * 
 * @see <a href="../../../../LOGGING_GUIDELINES.md">完整日志规范文档</a>
 */
object LogHelper {
    
    private const val MAX_LOG_ENTRIES = 1000 // 最多保留 1000 条日志
    
    private val logEntries = ConcurrentLinkedDeque<LogEntry>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private var nextId = 0L // 自增 ID 生成器
    
    /**
     * 日志条目数据类
     */
    data class LogEntry(
        val id: Long,
        val timestamp: Long,
        val level: String,
        val tag: String,
        val message: String
    ) {
        fun formattedTime(): String = dateFormat.format(Date(timestamp))
        
        fun toLogString(): String = "${formattedTime()} [$level] $tag: $message"
    }
    
    /**
     * Timber.Tree 实现，用于捕获应用日志
     * 
     * 自动从调用堆栈提取类名作为日志标签，便于定位日志来源
     */
    class LogHelperTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            val level = getLogLevelName(priority)
            // 如果 Timber 提供了 tag 就使用，否则自动从堆栈中获取类名
            val logTag = tag ?: createStackElementTag()
            
            synchronized(LogHelper) {
                nextId++
            }
            
            val entry = LogEntry(
                id = nextId,
                timestamp = System.currentTimeMillis(),
                level = level,
                tag = logTag,
                message = buildString {
                    append(message)
                    if (t != null) {
                        append("\n")
                        append(Log.getStackTraceString(t))
                    }
                }
            )
            
            addLogEntry(entry)
        }
        
        /**
         * 从调用堆栈中提取类名作为 tag
         */
        private fun createStackElementTag(): String {
            val stackTrace = Throwable().stackTrace
            // 找到第一个非 Timber、非 LogHelper 的调用者
            for (i in stackTrace.indices) {
                val element = stackTrace[i]
                val className = element.className
                
                // 跳过 Timber 和 LogHelper 相关的类
                if (className.contains("timber.log") || 
                    className.contains("LogHelper")) {
                    continue
                }
                
                // 提取类名的最后一部分（去掉包名）
                val simpleClassName = className.substringAfterLast('.')
                return simpleClassName
            }
            return "DroidMate" // 默认值
        }
        
        private fun getLogLevelName(priority: Int): String {
            return when (priority) {
                Log.VERBOSE -> "V"
                Log.DEBUG -> "D"
                Log.INFO -> "I"
                Log.WARN -> "W"
                Log.ERROR -> "E"
                Log.ASSERT -> "A"
                else -> "?"
            }
        }
    }
    
    /**
     * 添加日志条目
     */
    private fun addLogEntry(entry: LogEntry) {
        logEntries.addLast(entry)
        
        // 保持日志数量不超过限制
        while (logEntries.size > MAX_LOG_ENTRIES) {
            logEntries.removeFirst()
        }
    }
    
    /**
     * 获取所有日志条目
     */
    fun getAllLogs(): List<LogEntry> {
        return logEntries.toList()
    }
    
    /**
     * 获取最近 N 条日志
     */
    fun getRecentLogs(count: Int): List<LogEntry> {
        return logEntries.toList().takeLast(count)
    }
    
    /**
     * 清除所有日志
     */
    fun clearLogs() {
        logEntries.clear()
    }
    
    /**
     * 导出日志到文件
     */
    fun exportLogsToFile(file: File): Boolean {
        return try {
            FileWriter(file).use { writer ->
                writer.appendLine("=== DroidMate 日志导出 ===")
                writer.appendLine("导出时间：${dateFormat.format(Date())}")
                writer.appendLine("日志条数：${logEntries.size}")
                writer.appendLine("========================\n")
                
                logEntries.forEach { entry ->
                    writer.appendLine(entry.toLogString())
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 获取日志统计信息
     */
    fun getLogStats(): LogStats {
        val entries = logEntries.toList()
        return LogStats(
            total = entries.size,
            verboseCount = entries.count { it.level == "V" },
            debugCount = entries.count { it.level == "D" },
            infoCount = entries.count { it.level == "I" },
            warnCount = entries.count { it.level == "W" },
            errorCount = entries.count { it.level == "E" || it.level == "A" }
        )
    }
    
    /**
     * 日志统计信息
     */
    data class LogStats(
        val total: Int,
        val verboseCount: Int,
        val debugCount: Int,
        val infoCount: Int,
        val warnCount: Int,
        val errorCount: Int
    )
}
