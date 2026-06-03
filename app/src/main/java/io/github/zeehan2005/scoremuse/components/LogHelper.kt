package io.github.zeehan2005.scoremuse.components

import android.content.Context
import android.util.Log
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * 日志管理工具类
 * 
 * 这个对象提供了全局的日志捕获、查看和导出功能。
 * 主要特性包括：
 * - 统一的日志格式和时间戳
 * - 内存缓存最近 3000 条日志
 * - 支持按等级过滤日志
 * - 可暂停/恢复日志记录
 * - 导出日志到文件
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
    
    // 配置常量
    private const val MAX_LOG_SIZE_BYTES = 128 * 1024  // 最多保留 128KB 日志，避免内存溢出
    private const val PREFS_NAME = "ScoreMuse_log_settings"  // SharedPreferences 名称
    private const val KEY_LOGGING_PAUSED = "logging_paused"  // 暂停状态键
    private const val KEY_MIN_LOG_LEVEL = "min_log_level"  // 最小日志等级键
    
    // 内部状态
    private val logEntries = ConcurrentLinkedDeque<LogEntry>()  // 线程安全的日志队列
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ROOT)  // 时间格式化
    private var nextId = 0L  // 自增 ID 生成器，为每条日志分配唯一编号
    private var currentLogSize = 0L  // 当前日志总大小（字节）
    
    // 持久化设置
    private var prefs: PreferenceHelper? = null  // SharedPreferences 封装

    /**
     * 初始化 LogHelper，必须在应用启动时调用
     * 
     * 这个方法会创建 SharedPreferences 实例，
     * 用于持久化用户的日志设置（如暂停状态、最小日志等级）。
     * 
     * @param context Android 上下文
     */
    fun init(context: Context) {
        if (prefs == null) {
            prefs = PreferenceHelper(context, PREFS_NAME)
        }
    }
    
    /**
     * 获取日志记录是否暂停
     * 
     * 当用户打开日志显示界面时，可以暂停日志记录以避免内存占用过高。
     * 
     * @return true 表示已暂停，false 表示正常记录
     */
    fun isLoggingPaused(): Boolean {
        return prefs?.getBoolean(KEY_LOGGING_PAUSED, false) ?: false
    }
    
    /**
     * 设置日志记录暂停状态
     */
    fun setLoggingPaused(paused: Boolean) {
        prefs?.putBooleanAsync(KEY_LOGGING_PAUSED, paused)
    }
    
    /**
     * 获取最小日志等级
     */
    fun getMinLogLevel(): String {
        return prefs?.getString(KEY_MIN_LOG_LEVEL, "V") ?: "V"
    }
    
    /**
     * 设置最小日志等级
     */
    fun setMinLogLevel(level: String) {
        prefs?.putStringAsync(KEY_MIN_LOG_LEVEL, level)
    }
    
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
            
            val entry = synchronized(LogHelper) {
                nextId++
                LogEntry(
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
            }
            
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
            return "ScoreMuse" // 默认值
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
        synchronized(LogHelper) {
            // 计算日志条目的大小（字节）
            val entrySize = entry.toLogString().toByteArray().size.toLong()
            
            // 添加到队列并更新总大小
            logEntries.addLast(entry)
            currentLogSize += entrySize
            
            // 保持日志大小不超过限制
            while (currentLogSize > MAX_LOG_SIZE_BYTES && logEntries.isNotEmpty()) {
                val removedEntry = logEntries.removeFirst()
                currentLogSize -= removedEntry.toLogString().toByteArray().size.toLong()
            }
        }
    }
    
    /**
     * 获取所有日志条目
     */
    fun getAllLogs(): List<LogEntry> {
        return logEntries.toList()
    }

    /**
     * 根据最低日志等级过滤（自动包含更高等级）
     * 等级顺序：V < D < I < W < E < A
     * 
     * @param minLevel 最低显示等级，例如 "W" 会显示 W、E、A
     */
    fun getFilteredLogsByMinLevel(minLevel: String): List<LogEntry> {
        val levelOrder = mapOf(
            "V" to 0,
            "D" to 1,
            "I" to 2,
            "W" to 3,
            "E" to 4,
            "A" to 5
        )
        
        val minLevelValue = levelOrder[minLevel] ?: 0
        
        return logEntries.filter { entry ->
            val entryLevelValue = levelOrder[entry.level] ?: 0
            entryLevelValue >= minLevelValue
        }
    }

    /**
     * 清除所有日志
     */
    fun clearLogs() {
        synchronized(LogHelper) {
            logEntries.clear()
            currentLogSize = 0
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