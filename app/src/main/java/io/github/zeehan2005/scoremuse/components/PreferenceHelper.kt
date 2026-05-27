package io.github.zeehan2005.scoremuse.components

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import androidx.core.content.edit

/**
 * SharedPreferences 简单封装类
 * 
 * 这个类是对 Android SharedPreferences 的轻量级封装，目的是减少模板代码，
 * 提供更简洁的 API 来访问和修改持久化设置。
 * 
 * 使用示例：
 * ```kotlin
 * val prefs = PreferenceHelper(context, "my_prefs")
 * prefs.putString("key", value)
 * val existing = prefs.getString("key", "")
 * prefs.remove("key")
 * ```
 * 
 * 性能优化：
 * - 批量写入 + 防抖动：减少 I/O 操作次数
 * - 异步写入：避免阻塞主线程
 * - 内存缓存：pending writes 先缓存到内存
 */
class PreferenceHelper(context: Context, name: String) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    
    // Dispatcher for IO operations with limited parallelism
    private val dispatcher = Dispatchers.IO.limitedParallelism(1)
    
    // Pending writes cache for batching
    private val pendingWrites = ConcurrentHashMap<String, Any>()
    private var saveJob: Job? = null

    fun contains(key: String): Boolean =
        prefs.contains(key)

    fun getString(key: String, default: String? = null): String? =
        prefs.getString(key, default)

    /**
     * Synchronous write (kept for backward compatibility)
     * For better performance, use putStringAsync when possible
     */
    fun putString(key: String, value: String?) {
        prefs.edit {putString(key, value)}
    }
    
    /**
     * 异步字符串写入（带批量和防抖优化）
     * 
     * 这是推荐的写入方式。多个调用会在 100ms 内被批量合并成一次 I/O 操作，
     * 大大减少了磁盘写入次数，提高了性能。
     * 
     * 工作原理：
     * 1. 将值存入内存缓存（pendingWrites）
     * 2. 取消之前的定时保存任务
     * 3. 重新调度 100ms 后执行保存
     * 4. 如果 100ms 内有新写入，重复步骤 2-3
     * 
     * @param key 设置的键
     * @param value 要存储的值
     */
    fun putStringAsync(key: String, value: String?) {
        if (value == null) {
            pendingWrites.remove(key)
            prefs.edit { remove(key) }
            return
        }
        pendingWrites[key] = value
        // 取消之前的定时保存
        saveJob?.cancel()
        // 调度新的保存任务，100ms 防抖延迟
        saveJob = CoroutineScope(dispatcher).launch {
            delay(100)
            flushPendingWrites()
        }
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        prefs.getBoolean(key, default)

    fun getInt(key: String, default: Int = 0): Int =
        prefs.getInt(key, default)

    fun getFloat(key: String, default: Float = 0f): Float =
        prefs.getFloat(key, default)

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit {putBoolean(key, value)}
    }
    
    /**
     * 异步布尔值写入（带批量优化）
     * 
     * 与 putStringAsync 类似，但用于布尔类型
     */
    fun putBooleanAsync(key: String, value: Boolean) {
        pendingWrites[key] = value
        scheduleFlush()
    }

    fun getLong(key: String, default: Long = 0L): Long =
        prefs.getLong(key, default)

    fun putLong(key: String, value: Long) {
        prefs.edit {putLong(key, value)}
    }

    fun remove(key: String) {
        prefs.edit {remove(key)}
    }

    /**
     * Flush any pending writes immediately
     */
    fun flushPendingWrites() {
        if (pendingWrites.isEmpty()) return
        
        try {
            prefs.edit {
                pendingWrites.forEach { (key, value) ->
                    when (value) {
                        is String -> putString(key, value)
                        is Boolean -> putBoolean(key, value)
                        is Long -> putLong(key, value)
                        is Int -> putInt(key, value)
                        is Float -> putFloat(key, value)
                    }
                }
            } // apply() is async, doesn't block
            
            pendingWrites.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Schedule a flush operation with debouncing
     */
    private fun scheduleFlush() {
        saveJob?.cancel()
        saveJob = CoroutineScope(dispatcher).launch {
            delay(100)
            flushPendingWrites()
        }
    }

}
