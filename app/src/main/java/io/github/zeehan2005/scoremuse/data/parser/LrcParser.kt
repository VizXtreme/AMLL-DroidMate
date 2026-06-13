package io.github.zeehan2005.scoremuse.data.parser

import io.github.zeehan2005.scoremuse.data.parser.global.DEFAULT_LAST_LRC_LINE_DURATION_MS
import io.github.zeehan2005.scoremuse.data.parser.global.normalizeTextWhitespace
import io.github.zeehan2005.scoremuse.data.parser.global.parseAndStoreMetadata
import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.LyricWord
import timber.log.Timber

/**
 * 临时 LRC 条目数据类
 * 
 * 用于在解析过程中暂存时间戳和文本，后续会转换为 LyricLine 对象
 */
private data class TempLrcEntry(
    val timestampMs: Long,  // 时间戳（毫秒）
    val text: String        // 歌词文本
)

/**
 * 标准 LRC 格式解析器
 * 
 * LRC 是最常见的歌词格式，每行格式为：[mm:ss.ms] 歌词文本
 * 例如：[00:12.34] 这是一句歌词
 * 
 * 特点：
 * - 支持多时间戳（同一行歌词可以重复出现在不同时间点）
 * - 支持元数据标签（如 [ti:歌名]、[ar:歌手]）
 * - 只精确到行级别，不支持逐字时间戳
 */
object LrcParser {

    /**
     * 正则表达式：匹配 LRC 行格式
     * 例如：[00:12.34][00:15.67] 歌词文本  （支持多个连续时间戳）
     */
    private val lrcLineRegex = Regex("""^((?:\[\d{2,}:\d{2}[.:]\d{2,3}])+)(.*)$""")
    
    /**
     * 正则表达式：从 LRC 行中提取单个时间戳
     * 分组：1=分钟，2=秒，3=毫秒（可以是 2 位或 3 位）
     */
    private val tsExtractRegex = Regex("""\[(\d{2,}):(\d{2})[.:](\d{2,3})]""")

    /**
     * 解析 LRC 格式歌词内容
     * 
     * 解析流程：
     * 1. 逐行读取内容，识别并存储元数据
     * 2. 对非元数据行，提取时间戳和歌词文本
     * 3. 处理全局偏移量（如果有 offset 元数据）
     * 4. 将相同时间戳的行分组，合并为一行歌词
     * 5. 估算每行的结束时间（使用下一行的开始时间）
     * 
     * @param content LRC 格式的完整歌词文本
     * @return 解析后的 LyricLine 列表
     */
    fun parse(content: String): List<LyricLine> {
        /** 临时存储解析出的条目 */
        val entries = mutableListOf<TempLrcEntry>()
        /** 存储元数据（支持同一个 key 有多个值） */
        val metadata = mutableMapOf<String, MutableList<String>>()

        // 逐行解析
        for ((index, raw) in content.lines().withIndex()) {
            val line = raw.trim()
            if (line.isEmpty()) continue  // 跳过空行
            
            // 先尝试解析元数据，如果是元数据行就跳过后续处理
            if (parseAndStoreMetadata(line, metadata)) continue

            /** 尝试匹配 LRC 行格式 */
            val lineCaps = lrcLineRegex.find(line) ?: continue
            val allTs = lineCaps.groupValues[1]  // 所有时间戳部分
            val text = normalizeTextWhitespace(lineCaps.groupValues[2])  // 歌词文本

            // 处理每个时间戳（一行可能有多个时间戳，表示重复演唱）
            for (ts in tsExtractRegex.findAll(allTs)) {
                val minutes = ts.groupValues[1].toLongOrNull() ?: continue
                val seconds = ts.groupValues[2].toLongOrNull() ?: continue
                
                // 验证秒数合法性（不能超过 60）
                if (seconds >= 60) {
                    Timber.e("[LrcParser] Invalid LRC seconds on line ${index + 1}: $line")
                    continue
                }
                
                /** 处理毫秒部分（可能是 2 位或 3 位数字） */
                val fraction = ts.groupValues[3]
                val milliseconds = when (fraction.length) {
                    2 -> (fraction.toLongOrNull() ?: 0L) * 10L  // 2 位补零成 3 位
                    3 -> fraction.toLongOrNull() ?: 0L  // 3 位直接使用
                    else -> 0L  // 其他情况视为 0
                }
                
                // 计算总毫秒数并添加到条目列表
                entries.add(TempLrcEntry((minutes * 60 + seconds) * 1000 + milliseconds, text))
            }
        }

        /**
         * 从元数据中提取全局偏移量（如果有）
         * 偏移量用于整体调整所有歌词的时间，通常用于校正同步问题
         */
        val offsetMs = metadata["offset"]?.firstOrNull()?.toLongOrNull() ?: 0L

        /** 按时间戳排序所有条目 */
        val sorted = entries.sortedBy { it.timestampMs }
        val finalLines = mutableListOf<LyricLine>()
        var i = 0
        
        // 遍历所有条目，将相同时间戳的合并为一行
        while (i < sorted.size) {
            val startMsOriginal = sorted[i].timestampMs
            /** 应用全局偏移量 */
            val startMs = startMsOriginal + offsetMs
            
            /** 找到所有相同时间戳的条目（它们应该显示在同一行） */
            val groupEndIndex = sorted.subList(i, sorted.size).indexOfFirst { it.timestampMs != startMsOriginal }
                .let { if (it == -1) sorted.size else i + it }

            val group = sorted.subList(i, groupEndIndex)
            
            /**
             * 估算结束时间：使用下一行的开始时间
             * 确保持续时间至少为 1ms，防止 JS 侧计算 NaN
             */
            val endMsOriginal = sorted.getOrNull(groupEndIndex)?.timestampMs?.let { maxOf(startMsOriginal + 1, it) }
                ?: (startMsOriginal + DEFAULT_LAST_LRC_LINE_DURATION_MS)  // 最后一行使用默认持续时间
            val endMs = endMsOriginal + offsetMs

            /** 过滤掉空文本，只保留有意义的歌词 */
            val meaningful = group.filter { it.text.isNotEmpty() }
            if (meaningful.isNotEmpty()) {
                val mainText = meaningful[0].text
                
                // 创建 LyricLine 对象
                // 对于 LRC 格式，整行歌词作为一个单词处理
                finalLines.add(
                    LyricLine(
                        startTime = startMs,
                        endTime = endMs,
                        text = mainText,
                        words = listOf(
                            LyricWord(
                                word = mainText,
                                startTime = startMs,
                                endTime = endMs
                            )
                        )
                    )
                )
            }

            // 移动到下一组
            i = groupEndIndex
        }

        return finalLines
    }
}
