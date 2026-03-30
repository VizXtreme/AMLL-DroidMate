package com.amll.droidmate.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Simple wrapper around [SharedPreferences] to reduce boilerplate when
 * accessing named preferences.  Usage examples:
 *
 * ```kotlin
 * val prefs = PreferenceHelper(context, "my_prefs")
 * prefs.putString("key", value)
 * val existing = prefs.getString("key", "")
 * prefs.remove("key")
 * ```
 * 
 * Performance optimizations:
 * - Batch writes with debounce to reduce I/O operations
 * - Async write operations to avoid blocking main thread
 * - Memory cache for pending writes
 */
class PreferenceHelper(context: Context, name: String) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    
    // Dispatcher for IO operations with limited parallelism
    private val dispatcher = Dispatchers.IO.limitedParallelism(1)
    
    // Pending writes cache for batching
    private val pendingWrites = ConcurrentHashMap<String, Any?>()
    private var saveJob: Job? = null

    fun getString(key: String, default: String? = null): String? =
        prefs.getString(key, default)

    /**
     * Synchronous write (kept for backward compatibility)
     * For better performance, use putStringAsync when possible
     */
    fun putString(key: String, value: String?) {
        prefs.edit().putString(key, value).apply()
    }
    
    /**
     * Asynchronous write with batching and debouncing
     * Multiple calls within 100ms will be batched into a single I/O operation
     */
    fun putStringAsync(key: String, value: String?) {
        pendingWrites[key] = value
        
        // Cancel previous scheduled save
        saveJob?.cancel()
        
        // Schedule new save with 100ms debounce
        saveJob = CoroutineScope(dispatcher).launch {
            delay(100)
            flushPendingWrites()
        }
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        prefs.getBoolean(key, default)

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
    
    /**
     * Asynchronous boolean write with batching
     */
    fun putBooleanAsync(key: String, value: Boolean) {
        pendingWrites[key] = value
        scheduleFlush()
    }

    fun getLong(key: String, default: Long = 0L): Long =
        prefs.getLong(key, default)

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }
    
    /**
     * Asynchronous long write with batching
     */
    fun putLongAsync(key: String, value: Long) {
        pendingWrites[key] = value
        scheduleFlush()
    }

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }
    
    /**
     * Asynchronous remove with batching
     */
    fun removeAsync(key: String) {
        pendingWrites[key] = null // Mark as null to indicate removal
        scheduleFlush()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
    
    /**
     * Flush any pending writes immediately
     */
    fun flushPendingWrites() {
        if (pendingWrites.isEmpty()) return
        
        try {
            prefs.edit().apply { 
                pendingWrites.forEach { (key, value) ->
                    when (value) {
                        null -> remove(key)
                        is String -> putString(key, value)
                        is Boolean -> putBoolean(key, value)
                        is Long -> putLong(key, value)
                        is Int -> putInt(key, value)
                        is Float -> putFloat(key, value)
                    }
                }
            }.apply() // apply() is async, doesn't block
            
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

    /**
     * Perform multiple editor operations in a single transaction.
     */
    fun edit(block: SharedPreferences.Editor.() -> Unit) {
        prefs.edit().apply(block).apply()
    }
}
