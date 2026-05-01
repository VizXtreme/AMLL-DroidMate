package io.github.zeehan2005.scoremuse.data.parser.global

import timber.log.Timber

/**
 * 统一的时间戳转换工具类
 * 
 * 这个对象提供了一系列函数，用于在不同格式的时间表示之间进行转换。
 * 在歌词文件中，时间戳有多种表示方式，比如 "00:12.345" 表示 0 分 12 秒 345 毫秒，
 * 或者直接是秒数 "12.345"。这个工具类能够智能识别并转换这些格式。
 * 
 * 支持以下格式：
 * - mm:ss.ms (例：00:12.345) - 分：秒。毫秒
 * - mm:ss (例：00:12) - 分：秒
 * - hh:mm:ss.ms (例：00:00:12.345) - 时：分：秒。毫秒
 * - hh:mm:ss (例：00:00:12) - 时：分：秒
 * - 纯秒数 (例：12.345 或 12s) - 直接表示秒数
 * 
 * 参考 Unilyric 的时间戳处理逻辑，确保跨格式兼容性
 */
object TimestampUtils {
    
    /**
     * 将时间字符串转换为毫秒
     * 
     * 这是最常用的功能，将各种格式的时间字符串统一转换为毫秒数，方便后续计算。
     * 例如："00:12.345" -> 12345 毫秒，"12.5" -> 12500 毫秒
     * 
     * @param timeStr 时间字符串，可以是上述任何支持的格式
     * @return 毫秒数，解析失败返回 0L（不会抛出异常）
     */
    fun toMillis(timeStr: String?): Long {
        return try {
            // 处理空值情况
            if (timeStr.isNullOrBlank()) return 0L
            
            // 预处理：去除首尾空格、转小写、去掉可能的 's' 后缀
            val normalized = timeStr.trim().lowercase().removeSuffix("s")
            if (normalized.isEmpty()) return 0L
            
            // 处理纯秒数格式（不包含冒号）
            // 例如："12.345" 或 "12.5s"
            if (!normalized.contains(":")) {
                val seconds = normalized.toDoubleOrNull() ?: return 0L
                return (seconds * 1000.0).toLong()
            }
            
            // 处理包含冒号的格式
            // 根据冒号数量判断是 mm:ss 还是 hh:mm:ss 格式
            val parts = normalized.split(":")
            when (parts.size) {
                2 -> parseMmSsFormat(parts)  // 分：秒格式
                3 -> parseHhMmSsFormat(parts)  // 时：分：秒格式
                else -> 0L  // 不支持的格式
            }
        } catch (e: Exception) {
            // 捕获所有异常，确保不会因为时间戳解析错误导致程序崩溃
            Timber.e("[TimestampUtils] Failed to parse time string: $timeStr", e)
            0L
        }
    }
    
    /**
     * 将毫秒格式化为时间字符串
     * 
     * 这是 toMillis 的逆运算，将毫秒数转换回人类可读的时间格式。
     * 例如：12345 -> "00:12.345"
     * 
     * @param millis 毫秒数
     * @param format 输出格式
     *               - Format.MM_SS_MS: mm:ss.ms (默认)，如 "00:12.345"
     *               - Format.HH_MM_SS_MS: hh:mm:ss.ms (超过 1 小时时自动使用)，如 "01:00:12.345"
     *               - Format.AUTO: 根据时长自动选择（推荐）
     * @return 格式化后的时间字符串
     */
    fun fromMillis(millis: Long, format: Format = Format.AUTO): String {
        // 处理负数情况
        if (millis < 0) return "00:00.000"
        
        // 分解毫秒数为各个时间单位
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val ms = millis % 1000
        
        return when (format) {
            Format.MM_SS_MS -> String.format("%02d:%02d.%03d", minutes, seconds, ms)
            Format.HH_MM_SS_MS -> String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, ms)
            Format.AUTO -> {
                // 智能选择：如果超过 1 小时就使用时分秒格式，否则使用分秒格式
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
     * 
     * 定义了几种常用的时间字符串显示格式
     */
    enum class Format {
        MM_SS_MS,      // mm:ss.ms - 分秒毫秒格式，最常用
        HH_MM_SS_MS,   // hh:mm:ss.ms - 时分秒毫秒格式，用于长歌曲
        AUTO           // 自动选择 - 根据实际时长智能选择合适格式
    }
    
    // ==================== 私有辅助方法 ====================
    // 以下是内部使用的辅助函数，不对外暴露
    
    /**
     * 解析 mm:ss.ms 或 mm:ss 格式
     * 
     * 处理标准的分秒格式，例如 "03:25.123" 或 "03:25"
     * 注意：这里的秒部分可能包含小数点（毫秒），也可能没有
     * 
     * @param parts 分割后的字符串数组，应该是 [分钟，秒] 或 [分钟，秒。毫秒]
     * @return 转换后的毫秒数
     */
    private fun parseMmSsFormat(parts: List<String>): Long {
        val minutes = parts[0].toLongOrNull() ?: return 0L
        val secondToken = parts[1]
        
        // 委托给统一的秒部分解析函数
        return parseSecondToken(secondToken) + minutes * 60 * 1000
    }
    
    /**
     * 解析 hh:mm:ss.ms 或 hh:mm:ss 格式
     * 
     * 处理带小时的完整时间格式，例如 "01:03:25.123" 或 "01:03:25"
     * 
     * @param parts 分割后的字符串数组，应该是 [小时，分钟，秒] 或 [小时，分钟，秒。毫秒]
     * @return 转换后的毫秒数
     */
    private fun parseHhMmSsFormat(parts: List<String>): Long {
        val hours = parts[0].toLongOrNull() ?: return 0L
        val minutes = parts[1].toLongOrNull() ?: return 0L
        val secondToken = parts[2]
        
        // 分别计算各部分的毫秒数然后相加
        return parseSecondToken(secondToken) + minutes * 60 * 1000 + hours * 3600 * 1000
    }
    
    /**
     * 解析秒和毫秒部分
     * 
     * 这是一个通用的秒部分解析函数，能够处理多种精度：
     * - ss (整数秒，例如 "25")
     * - ss.m (1 位小数，例如 "25.1")
     * - ss.mm (2 位小数，例如 "25.12")
     * - ss.mmm (3 位小数，例如 "25.123")
     * 
     * 对于不足 3 位的小数，会自动补零（例如 "25.1" -> 100 毫秒）
     * 对于超过 3 位的小数，会截取前 3 位（例如 "25.1234" -> 123 毫秒）
     * 
     * @param secondToken 秒部分的字符串
     * @return 秒和毫秒的总毫秒数
     */
    private fun parseSecondToken(secondToken: String): Long {
        // 按小数点分割秒和毫秒部分
        val secParts = secondToken.split(".")
        val seconds = secParts[0].toLongOrNull() ?: return 0L
        
        // 处理毫秒部分（小数点后）
        val millis = if (secParts.size > 1) {
            // 补齐到 3 位数字并截取前 3 位
            // 例如："1" -> "100" -> 100ms, "12" -> "120" -> 120ms, "1234" -> "123" -> 123ms
            secParts[1].padEnd(3, '0').take(3).toLongOrNull() ?: 0L
        } else {
            0L
        }
        
        return seconds * 1000 + millis
    }
}
