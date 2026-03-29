package com.amll.droidmate.data.parser

import timber.log.Timber

/**
 * 统一的时间戳转换工具类
 * 
 * 支持以下格式：
 * - mm:ss.ms (例：00:12.345)
 * - mm:ss (例：00:12)
 * - hh:mm:ss.ms (例：00:00:12.345)
 * - hh:mm:ss (例：00:00:12)
 * - 纯秒数 (例：12.345 或 12s)
 * 
 * 参考 Unilyric 的时间戳处理逻辑，确保跨格式兼容性
 */
object TimestampUtils {
    
    /**
     * 将时间字符串转换为毫秒
     * 
     * @param timeStr 时间字符串
     * @return 毫秒数，解析失败返回 0L
     */
    fun toMillis(timeStr: String?): Long {
        return try {
            if (timeStr.isNullOrBlank()) return 0L
            
            val normalized = timeStr.trim().lowercase().removeSuffix("s")
            if (normalized.isEmpty()) return 0L
            
            // 处理纯秒数格式（不包含冒号）
            if (!normalized.contains(":")) {
                val seconds = normalized.toDoubleOrNull() ?: return 0L
                return (seconds * 1000.0).toLong()
            }
            
            // 处理包含冒号的格式
            val parts = normalized.split(":")
            when (parts.size) {
                2 -> parseMmSsFormat(parts)
                3 -> parseHhMmSsFormat(parts)
                else -> 0L
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse time string: $timeStr")
            0L
        }
    }
    
    /**
     * 将毫秒格式化为时间字符串
     * 
     * @param millis 毫秒数
     * @param format 输出格式
     *               - Format.MM_SS_MS: mm:ss.ms (默认)
     *               - Format.HH_MM_SS_MS: hh:mm:ss.ms (超过 1 小时时自动使用)
     *               - Format.AUTO: 根据时长自动选择
     * @return 格式化后的时间字符串
     */
    fun fromMillis(millis: Long, format: Format = Format.AUTO): String {
        if (millis < 0) return "00:00.000"
        
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val ms = millis % 1000
        
        return when (format) {
            Format.MM_SS_MS -> String.format("%02d:%02d.%03d", minutes, seconds, ms)
            Format.HH_MM_SS_MS -> String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, ms)
            Format.AUTO -> {
                if (hours > 0) {
                    String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, ms)
                } else {
                    String.format("%02d:%02d.%03d", minutes, seconds, ms)
                }
            }
        }
    }
    
    /**
     * 输出格式枚举
     */
    enum class Format {
        MM_SS_MS,      // mm:ss.ms
        HH_MM_SS_MS,   // hh:mm:ss.ms
        AUTO           // 自动选择
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 解析 mm:ss.ms 或 mm:ss 格式
     */
    private fun parseMmSsFormat(parts: List<String>): Long {
        val minutes = parts[0].toLongOrNull() ?: return 0L
        val secondToken = parts[1]
        
        return parseSecondToken(secondToken) + minutes * 60 * 1000
    }
    
    /**
     * 解析 hh:mm:ss.ms 或 hh:mm:ss 格式
     */
    private fun parseHhMmSsFormat(parts: List<String>): Long {
        val hours = parts[0].toLongOrNull() ?: return 0L
        val minutes = parts[1].toLongOrNull() ?: return 0L
        val secondToken = parts[2]
        
        return parseSecondToken(secondToken) + minutes * 60 * 1000 + hours * 3600 * 1000
    }
    
    /**
     * 解析秒和毫秒部分
     * 支持：ss、ss.m、ss.mm、ss.mmm
     */
    private fun parseSecondToken(secondToken: String): Long {
        val secParts = secondToken.split(".")
        val seconds = secParts[0].toLongOrNull() ?: return 0L
        
        val millis = if (secParts.size > 1) {
            // 补齐到 3 位数字并截取前 3 位
            secParts[1].padEnd(3, '0').take(3).toLongOrNull() ?: 0L
        } else {
            0L
        }
        
        return seconds * 1000 + millis
    }
}
