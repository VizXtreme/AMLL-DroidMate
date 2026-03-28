package com.amll.droidmate.data.parser

import com.amll.droidmate.domain.model.LyricLine
import com.amll.droidmate.domain.model.LyricWord

// ==================== 常量定义 ====================

/**
 * 歌词行合并时的容差值（毫秒）
 * 用于判断两个时间戳是否表示同一行歌词
 */
const val TOLERANCE_MS: Long = 50

/**
 * 默认歌词行持续时间（毫秒）
 * 当无法确定下一行时间时使用此值
 */
const val DEFAULT_LINE_DURATION_MS = 2000L

/**
 * LRC 格式最后一行的默认持续时间（毫秒）
 */
const val DEFAULT_LAST_LRC_LINE_DURATION_MS = 10_000L

// ==================== 正则表达式 ====================

private val METADATA_TAG_REGEX = Regex("""^\[(?<key>[a-zA-Z]+):(?<value>.*)]$""")

/**
 * 清理和标准化文本 whitespace
 * 
 * @param text 原始文本
 * @return 清理后的文本，如果为空则返回空字符串
 */
fun normalizeTextWhitespace(text: String): String {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return ""

    // QQ 音乐翻译字段有时会以双斜杠开头（"//"），这会导致显示异常。
    // 这里统一去掉行首的双斜杠及后续空白。
    val withoutLeadingSlashes = trimmed.replaceFirst(Regex("^/{2,}\\s*"), "")
    if (withoutLeadingSlashes.isEmpty()) return ""

    return withoutLeadingSlashes.split(Regex("\\s+")).joinToString(" ")
}

/**
 * 处理逐字歌词文本片段
 * 
 * 处理前导空格和尾随空格，确保单词之间的正确间隔
 * 
 * @param rawTextSlice 原始文本片段
 * @param words 已解析的单词列表（用于上下文判断）
 * @return Pair(清理后的文本，是否有尾随空格)，如果文本为空则返回 null
 */
fun processSyllableText(rawTextSlice: String, words: MutableList<LyricWord>): Pair<String, Boolean>? {
    val hasLeadingSpace = rawTextSlice.firstOrNull()?.isWhitespace() == true
    val hasTrailingSpace = rawTextSlice.lastOrNull()?.isWhitespace() == true
    val cleanText = rawTextSlice.trim()

    if (hasLeadingSpace && words.isNotEmpty()) {
        val last = words.last()
        if (!last.word.endsWith(" ")) {
            words[words.lastIndex] = last.copy(word = "${last.word} ")
        }
    }

    if (cleanText.isEmpty()) {
        return null
    }

    return cleanText to hasTrailingSpace
}

/**
 * 统一的文本清理函数
 * 
 * @param text 待清理的文本
 * @param preserveSpaces 是否保留空格（默认为 false，会 trim 并压缩空格）
 * @param removeLeadingSlashes 是否移除开头的双斜杠（针对 QQ 音乐翻译字段）
 * @return 清理后的文本
 */
fun cleanLyricText(
    text: String,
    preserveSpaces: Boolean = false,
    removeLeadingSlashes: Boolean = true
): String {
    if (text.isEmpty()) return ""
    
    return if (preserveSpaces) {
        // 保留空格的清理模式：仅移除回车符
        text.replace("\r", "")
    } else {
        // 标准清理模式：trim 并压缩空格
        var cleaned = normalizeTextWhitespace(text)
        // 仅在明确指定时移除双斜杠
        if (!removeLeadingSlashes) {
            // 如果原始文本有双斜杠且 normalizeTextWhitespace 移除了它们，需要恢复
            if (text.trim().startsWith("//")) {
                cleaned = "//" + cleaned.trimStart()
            }
        } else {
            cleaned = cleaned.replaceFirst(Regex("^/{2,}\\s*"), "")
        }
        cleaned
    }
}

/**
 * 清理背景歌歌词文本
 * 
 * 背景歌词通常用括号包裹，此函数移除外层括号但不改动其它空格
 * 
 * @param text 背景歌词文本
 * @return 清理后的文本
 */
fun cleanBackgroundText(text: String): String {
    // 仅在文本严格被括号包裹时去掉一层括号
    if (text.length >= 2 && text.startsWith("(") && text.endsWith(")")) {
        return text.substring(1, text.length - 1)
    }
    return text
}

/**
 * 估算歌词行的结束时间
 * 
 * 通过查找下一行的起始时间来估算当前行的结束时间
 * 
 * @param allLines 所有歌词行
 * @param currentLineNumber 当前行号
 * @param lineTimestampRegex 时间戳正则表达式
 * @return 下一行的起始时间，如果找不到则返回 null
 */
fun findNextLineStartTime(
    allLines: List<String>,
    currentLineNumber: Int,
    lineTimestampRegex: Regex
): Long? {
    for (i in (currentLineNumber + 1) until allLines.size) {
        val nextLine = allLines[i].trim()
        if (nextLine.isEmpty()) continue
        
        val match = lineTimestampRegex.find(nextLine)
        if (match != null) {
            // 提取时间戳分组并转换为毫秒
            return when (match.groupValues.size) {
                4 -> TimestampUtils.toMillis("${match.groupValues[1]}:${match.groupValues[2]}.${match.groupValues[3]}")
                3 -> TimestampUtils.toMillis("${match.groupValues[1]}:${match.groupValues[2]}")
                else -> 0L
            }
        }
    }
    return null
}

/**
 * 解析并存储元数据行
 * 
 * @param line 待解析的行
 * @param rawMetadata 元数据存储 Map
 * @return 如果是元数据行返回 true，否则返回 false
 */
fun parseAndStoreMetadata(line: String, rawMetadata: MutableMap<String, MutableList<String>>): Boolean {
    val match = METADATA_TAG_REGEX.find(line) ?: return false
    val key = match.groups["key"]?.value?.trim().orEmpty()
    if (key.isEmpty()) return false
    val value = normalizeTextWhitespace(match.groups["value"]?.value.orEmpty())
    rawMetadata.getOrPut(key) { mutableListOf() }.add(value)
    return true
}

/**
 * 合并主歌词、翻译和罗马音歌词
 * 
 * 基于时间戳容差将翻译和罗马音对齐到主歌词行
 * 
 * @param mainLines 主歌词行列表
 * @param translationLines 翻译歌词行列表（可选）
 * @param romanizationLines 罗马音歌词行列表（可选）
 * @return 合并后的歌词行列表
 */
fun mergeLyricLines(
    mainLines: List<LyricLine>,
    translationLines: List<LyricLine>?,
    romanizationLines: List<LyricLine>?
): List<LyricLine> {
    if (translationLines.isNullOrEmpty() && romanizationLines.isNullOrEmpty()) {
        return mainLines
    }

    var transIndex = 0
    var romanIndex = 0

    val trans = translationLines.orEmpty().sortedBy { it.startTime }
    val roman = romanizationLines.orEmpty().sortedBy { it.startTime }

    return mainLines.map { main ->
        while (transIndex < trans.size && trans[transIndex].startTime + TOLERANCE_MS < main.startTime) {
            transIndex += 1
        }
        while (romanIndex < roman.size && roman[romanIndex].startTime + TOLERANCE_MS < main.startTime) {
            romanIndex += 1
        }

        val translation = trans.getOrNull(transIndex)
            ?.takeIf { kotlin.math.abs(it.startTime - main.startTime) <= TOLERANCE_MS }
            ?.text

        val romanization = roman.getOrNull(romanIndex)
            ?.takeIf { kotlin.math.abs(it.startTime - main.startTime) <= TOLERANCE_MS }
            ?.text

        if (translation != null) transIndex += 1
        if (romanization != null) romanIndex += 1

        main.copy(
            translation = translation ?: main.translation,
            transliteration = romanization ?: main.transliteration
        )
    }
}

/**
 * XML 特殊字符转义
 * 
 * @param text 原始文本
 * @return 转义后的文本
 */
fun escapeXML(text: String): String {
    return text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
}
