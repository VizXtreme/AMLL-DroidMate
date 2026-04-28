package com.amll.droidmate.data.parser

import dev.amll.droidmate.global.LyricLine
import dev.amll.droidmate.global.LyricWord
import kotlin.collections.isNotEmpty
import kotlin.collections.isNullOrEmpty
import kotlin.collections.lastIndex
import kotlin.collections.map
import kotlin.collections.orEmpty

// ==================== 常量定义 ====================
// 这些常量在整个解析器模块中共享使用，用于统一标准和行为

/**
 * 歌词行合并时的容差值（毫秒）
 * 
 * 由于不同来源的歌词时间戳可能存在微小差异，这个值用于判断两个时间戳
 * 是否表示同一行歌词。例如，如果两个时间戳相差不到 50 毫秒，就认为它们是同步的。
 */
const val TOLERANCE_MS: Long = 50

/**
 * 默认歌词行持续时间（毫秒）
 * 
 * 当无法通过下一行来确定当前行的结束时间时，使用这个默认值。
 * 2 秒是一个合理的估计值，适用于大多数歌曲。
 */
const val DEFAULT_LINE_DURATION_MS = 2000L

/**
 * LRC 格式最后一行的默认持续时间（毫秒）
 * 
 * LRC 格式的最后一行歌词通常没有明确的结束时间，
 * 这里设置一个较长的持续时间（10 秒），让歌词在歌曲结束时保持显示更久。
 */
const val DEFAULT_LAST_LRC_LINE_DURATION_MS = 10_000L

// ==================== 正则表达式 ====================
// 用于匹配和解析歌词中的元数据标签，例如 [ti:歌名]、[ar:歌手] 等
private val METADATA_TAG_REGEX = Regex("""^\[(?<key>[a-zA-Z]+):(?<value>.*)]$""")

/**
 * 清理和标准化文本 whitespace（空白字符）
 * 
 * 这个函数处理歌词文本中的各种空白问题：
 * 1. 去除首尾空格
 * 2. 将多个连续空格压缩为一个
 * 3. 移除 QQ 音乐翻译字段开头的双斜杠 "//"
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

    // 将任意多个连续空白字符（空格、制表符等）替换为单个空格
    return withoutLeadingSlashes.split(Regex("\\s+")).joinToString(" ")
}

/**
 * 处理逐字歌词文本片段
 * 
 * 在逐字歌词中，每个音节之间可能有空格。这个函数负责：
 * 1. 检测并处理前导空格（可能需要在前一个单词后添加空格）
 * 2. 检测尾随空格（可能需要在当前单词后添加空格）
 * 3. 清理出纯净的文本内容
 * 
 * @param rawTextSlice 原始文本片段（包含空格信息）
 * @param words 已解析的单词列表（用于上下文判断是否需要添加空格）
 * @return Pair(清理后的文本，是否有尾随空格)，如果文本为空则返回 null
 */
fun processSyllableText(rawTextSlice: String, words: MutableList<LyricWord>): Pair<String, Boolean>? {
    // 检查是否有前导和尾随空格
    val hasLeadingSpace = rawTextSlice.firstOrNull()?.isWhitespace() == true
    val hasTrailingSpace = rawTextSlice.lastOrNull()?.isWhitespace() == true
    val cleanText = rawTextSlice.trim()

    // 如果有前导空格且已有单词列表，说明需要在上一个单词后面添加空格
    if (hasLeadingSpace && words.isNotEmpty()) {
        val last = words.last()
        if (!last.word.endsWith(" ")) {
            words[words.lastIndex] = last.copy(word = "${last.word} ")
        }
    }

    // 如果清理后为空，说明这个片段只有空格，不需要处理
    if (cleanText.isEmpty()) {
        return null
    }

    // 返回清理后的文本和尾随空格标记
    return cleanText to hasTrailingSpace
}

/**
 * 统一的文本清理函数
 * 
 * 提供两种清理模式：
 * 1. 标准模式（preserveSpaces=false）：trim 并压缩空格，适用于主歌词
 * 2. 保留空格模式（preserveSpaces=true）：仅移除换行符，适用于需要保留格式的歌词
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
        // 保留空格的清理模式：仅移除换行控制字符
        // 适用于需要保持原有格式的歌词文本
        text
            .replace("\r", "")  // 移除回车符
            .replace("\n", "")  // 移除换行符
    } else {
        // 标准清理模式：trim 并压缩空格
        val cleaned = normalizeTextWhitespace(text)
        if (removeLeadingSlashes) {
            cleaned.replaceFirst(Regex("^/{2,}\\s*"), "")
        } else {
            cleaned
        }
    }
}

/**
 * 清理背景歌歌词文本
 * 
 * 背景歌词（Background vocals）通常用括号包裹，例如 "(啦啦啦)"。
 * 这个函数移除外层括号但不改动其它空格，以便后续处理。
 * 
 * @param text 背景歌词文本
 * @return 清理后的文本（去掉外层括号）
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
 * 通过查找下一行的起始时间来估算当前行的结束时间。
 * 这是歌词解析中的常见需求，因为很多歌词格式只提供了开始时间。
 * 
 * @param allLines 所有歌词行（按顺序）
 * @param currentLineNumber 当前行号
 * @param lineTimestampRegex 时间戳正则表达式（用于识别带时间戳的行）
 * @return 下一行的起始时间，如果找不到则返回 null
 */
fun findNextLineStartTime(
    allLines: List<String>,
    currentLineNumber: Int,
    lineTimestampRegex: Regex
): Long? {
    // 从当前行的下一行开始查找
    for (i in (currentLineNumber + 1) until allLines.size) {
        val nextLine = allLines[i].trim()
        if (nextLine.isEmpty()) continue  // 跳过空行
        
        // 尝试匹配时间戳
        val match = lineTimestampRegex.find(nextLine)
        if (match != null) {
            // 提取时间戳分组并转换为毫秒
            return when (match.groupValues.size) {
                4 -> TimestampUtils.toMillis("${match.groupValues[1]}:${match.groupValues[2]}.${match.groupValues[3]}")  // mm:ss.ms 格式
                3 -> TimestampUtils.toMillis("${match.groupValues[1]}:${match.groupValues[2]}")  // mm:ss 格式
                else -> 0L  // 不支持的格式
            }
        }
    }
    return null  // 没有找到下一行
}

/**
 * 解析并存储元数据行
 * 
 * 歌词文件通常包含元数据，如 [ti:歌名]、[ar:歌手]、[al:专辑] 等。
 * 这个函数负责解析这些元数据并存储到 Map 中。
 * 
 * @param line 待解析的行
 * @param rawMetadata 元数据存储 Map（key 是元数据名称，value 是值的列表）
 * @return 如果是元数据行返回 true，否则返回 false
 */
fun parseAndStoreMetadata(line: String, rawMetadata: MutableMap<String, MutableList<String>>): Boolean {
    // 尝试匹配元数据标签格式
    val match = METADATA_TAG_REGEX.find(line) ?: return false
    val key = match.groups["key"]?.value?.trim().orEmpty()
    if (key.isEmpty()) return false
    val value = normalizeTextWhitespace(match.groups["value"]?.value.orEmpty())
    
    // 将元数据添加到 Map 中（支持同一个 key 有多个值）
    rawMetadata.getOrPut(key) { mutableListOf() }.add(value)
    return true
}

/**
 * 合并主歌词、翻译和罗马音歌词
 * 
 * 在很多情况下，主歌词、翻译歌词和罗马音（拼音/假名）歌词是分开存储的。
 * 这个函数基于时间戳容差将翻译和罗马音对齐到主歌词行。
 * 
 * 工作原理：
 * 1. 分别对翻译和罗马音歌词按时间排序
 * 2. 遍历主歌词，找到时间最接近的翻译和罗马音行
 * 3. 如果时间差在容差范围内（TOLERANCE_MS），就认为是同一行并合并
 * 
 * @param mainLines 主歌词行列表
 * @param translationLines 翻译歌词行列表（可选）
 * @param romanizationLines 罗马音歌词行列表（可选）
 * @return 合并后的歌词行列表（每条都包含主歌词 + 翻译 + 罗马音）
 */
fun mergeLyricLines(
    mainLines: List<LyricLine>,
    translationLines: List<LyricLine>?,
    romanizationLines: List<LyricLine>?
): List<LyricLine> {
    // 如果没有翻译和罗马音，直接返回主歌词
    if (translationLines.isNullOrEmpty() && romanizationLines.isNullOrEmpty()) {
        return mainLines
    }

    // 维护翻译和罗马音的当前索引
    var transIndex = 0
    var romanIndex = 0

    // 按时间排序，方便后续匹配
    val trans = translationLines.orEmpty().sortedBy { it.startTime }
    val roman = romanizationLines.orEmpty().sortedBy { it.startTime }

    // 遍历主歌词，为每一行查找对应的翻译和罗马音
    return mainLines.map { main ->
        // 跳过时间早于当前主歌词的翻译行
        while (transIndex < trans.size && trans[transIndex].startTime + TOLERANCE_MS < main.startTime) {
            transIndex += 1
        }
        // 跳过时间早于当前主歌词的罗马音行
        while (romanIndex < roman.size && roman[romanIndex].startTime + TOLERANCE_MS < main.startTime) {
            romanIndex += 1
        }

        // 检查当前翻译行是否与主歌词时间匹配
        val translation = trans.getOrNull(transIndex)
            ?.takeIf { kotlin.math.abs(it.startTime - main.startTime) <= TOLERANCE_MS }
            ?.text

        // 检查当前罗马音行是否与主歌词时间匹配
        val romanization = roman.getOrNull(romanIndex)
            ?.takeIf { kotlin.math.abs(it.startTime - main.startTime) <= TOLERANCE_MS }
            ?.text

        // 如果匹配成功，移动索引到下一行
        if (translation != null) transIndex += 1
        if (romanization != null) romanIndex += 1

        // 创建新的歌词行，包含主歌词 + 翻译 + 罗马音
        main.copy(
            translation = translation ?: main.translation,
            transliteration = romanization ?: main.transliteration
        )
    }
}

/**
 * XML 特殊字符转义
 * 
 * 在生成 TTML 等 XML 格式的歌词时，需要对特殊字符进行转义，
 * 避免破坏 XML 结构。例如 "&" 要转义为 "&amp;"，"<" 要转义为 "&lt;"。
 * 
 * @param text 原始文本
 * @return 转义后的文本
 */
fun escapeXML(text: String): String {
    return text
        .replace("&", "&amp;")   // 和号必须第一个转义
        .replace("<", "&lt;")    // 小于号
        .replace(">", "&gt;")    // 大于号
        .replace("\"", "&quot;") // 双引号
        .replace("'", "&apos;") // 单引号
}
