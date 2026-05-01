package io.github.zeehan2005.scoremuse.data.parser

import io.github.zeehan2005.scoremuse.data.parser.global.processSyllableText
import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.LyricWord
import timber.log.Timber

/**
 * QQ 音乐 QRC 格式解析器（逐字歌词格式）
 * 
 * QRC 是 QQ 音乐的专有歌词格式，支持逐字精确时间戳。
 * 
 * 格式特点：
 * - 行级时间戳：[起始时间，持续时间]（单位：毫秒）
 * - 逐字时间戳：文本 (起始时间，持续时间)
 * - 可能包含 XML 包装（需要从 XML 属性中提取）
 * 
 * 示例：
 * [0,5000] 你 (0,500) 好 (500,500) 世 (1000,500) 界 (1500,500)
 * 表示：整行从 0ms 开始持续 5000ms，其中"你"从 0ms 开始持续 500ms，依此类推
 */
object QrcParser {

    // 正则表达式：匹配 QRC 逐字令牌
    // 分组：text=歌词文本，start=开始时间，duration=持续时间
    // 例如："你好 (0,500)" -> text="你好", start=0, duration=500
    private val lyricTokenRegex = Regex("""(?<text>.*?)\((?<start>\d+),(?<duration>\d+)\)""")
    
    // 正则表达式：匹配 QRC 行级时间戳
    // 格式：[起始时间，持续时间]
    private val qrcLineTimestampRegex = Regex("""^\[(\d+),(\d+)]""")

    /**
     * 解析 QRC 格式内容
     * 
     * 解析流程：
     * 1. 如果内容是 XML 格式，先提取 LyricContent 属性值
     * 2. 逐行解析，识别行级时间戳和逐字时间戳
     * 3. 构建 LyricLine 对象列表
     * 
     * @param content QRC 格式的完整歌词文本（可能是 XML 包装的）
     * @return 解析后的 LyricLine 列表
     */
    fun parse(content: String): List<LyricLine> {
        // 如果是 XML 格式，需要先提取歌词内容
        val rawContent = extractQrcFromXmlIfNeeded(content)

        // 调试日志：输出原始内容的基本信息
        Timber.d("[QrcParser] Raw content length=${rawContent.length}, lineCount=${rawContent.lines().size}, containsNewline=${rawContent.contains('\n')}")
        rawContent.lines().take(10).forEachIndexed { index, line ->
            Timber.d("[QrcParser] Raw line $index: ${line.take(200)}")
        }

        val finalLines = mutableListOf<LyricLine>()
        mutableMapOf<String, MutableList<String>>()

        // 逐行解析
        for (raw in rawContent.lines()) {
            val line = raw.trim()
            if (line.isEmpty()) continue
            // 使用统一的元数据检测函数
            // TEMPORARILY DISABLED: MetadataStripper.isMetadataLine(line)
            // if (MetadataStripper.isMetadataLine(line)) continue

            // 解析单行并添加到结果列表
            parseSingleLine(line)?.let { finalLines.add(it) }
        }

        // 调试输出：统计解析结果
        val totalWords = finalLines.sumOf { it.words.size }
        Timber.d("[QrcParser] Output: ${finalLines.size} lines, $totalWords words (avg=${if (finalLines.isNotEmpty()) totalWords.toDouble() / finalLines.size else 0.0})")

        return finalLines
    }

    /**
     * 解析单个 QRC 行
     * 
     * QRC 行格式：[起始时间，持续时间] 文本 (起始时间，持续时间) 文本 (起始时间，持续时间)...
     * 
     * 处理逻辑：
     * 1. 提取行级时间戳（如果有）
     * 2. 逐个提取逐字令牌（文本 + 时间戳）
     * 3. 如果没有逐字时间戳但有行级时间戳，使用 fallback 模式
     * 4. 构建 LyricLine 对象
     * 
     * @param line 单行 QRC 歌词文本
     * @return 解析后的 LyricLine，如果无法解析则返回 null
     */
    private fun parseSingleLine(line: String): LyricLine? {
        // QRC 行头部：[lineStart,lineDuration] (单位：毫秒)
        val lineStartMs = qrcLineTimestampRegex.find(line)?.groups?.get(1)?.value?.toLongOrNull()
        val lineDurationMs = qrcLineTimestampRegex.find(line)?.groups?.get(2)?.value?.toLongOrNull()
    
        // 移除行级时间戳，剩下的就是歌词内容
        val lineContent = qrcLineTimestampRegex.replace(line, "")
        val words = mutableListOf<LyricWord>()
    
        // 逐个匹配逐字令牌
        for (capture in lyricTokenRegex.findAll(lineContent)) {
            val rawText = capture.groups["text"]?.value.orEmpty()
            val processed = processSyllableText(rawText, words) ?: continue
            val (cleanText, endsWithSpace) = processed
    
            val startMs = capture.groups["start"]?.value?.toLongOrNull() ?: continue
            val durationMs = capture.groups["duration"]?.value?.toLongOrNull() ?: continue
    
            // 根据是否有尾随空格决定是否在单词后添加空格
            val text = if (endsWithSpace) "$cleanText " else cleanText
            words.add(
                LyricWord(
                    word = text,
                    startTime = startMs,
                    endTime = startMs + durationMs
                )
            )
        }

        // Fallback 模式：处理只有行级时间戳而没有逐字时间戳的情况
        // Some QRC lines include only a line-level timestamp ([start,duration]) without per-word timings.
        // In that case, we still want to emit a lyric line rather than drop it.
        // However, some files may include placeholder tokens like "(240410,1651)" where there is no actual lyric text.
        // Do not treat these timestamp tokens as lyric text.
        if (words.isEmpty() && lineStartMs != null) {
            val fallbackText = lineContent.trim()
            // Normalize to catch hidden/zero-width characters or other noise that still renders as
            // a timestamp-like token (e.g. "\u200B(240410,1651)").
            val normalized = fallbackText.replace(Regex("[^\\d(),]"), "")
            val isTimestampToken = Regex("^\\(\\d+,\\d+\\)(?:\\(\\d+,\\d+\\))*$").matches(normalized)
            
            // 如果不是纯时间戳占位符，就作为整行歌词处理
            if (fallbackText.isNotEmpty() && !isTimestampToken) {
                val lineEnd = lineStartMs + (lineDurationMs ?: 0)
                words.add(
                    LyricWord(
                        word = fallbackText,
                        startTime = lineStartMs,
                        endTime = lineEnd
                    )
                )
            }
        }

        // 如果没有解析出任何单词，返回 null（丢弃这一行）
        if (words.isEmpty()) return null

        // 计算行的开始和结束时间
        // 优先使用行级时间戳，如果没有则使用第一个和最后一个单词的时间
        val lineStart = lineStartMs ?: words.first().startTime
        val lineEnd = lineStart + (lineDurationMs ?: (words.last().endTime - lineStart))

        // 创建 LyricLine 对象
        return LyricLine(
            startTime = lineStart,
            endTime = lineEnd,
            text = words.joinToString(separator = "") { it.word }.trimEnd(),  // 拼接所有单词
            words = words
        )
    }

    /**
     * 从 XML 包装中提取 QRC 歌词内容
     * 
     * QQ 音乐的 QRC 输出有时会被包装在 XML 标签中，例如：
     * <Lyric_1 LyricContent="歌词内容..." />
     * 
     * 由于 XML 解析器会规范化空白字符（换行符变成空格），
     * 我们使用正则表达式直接提取属性值，保留原始换行符。
     * 
     * @param content 可能包含 XML 包装的 QRC 内容
     * @return 提取后的纯 QRC 歌词文本
     */
    private fun extractQrcFromXmlIfNeeded(content: String): String {
        // 如果不包含 XML 特征标记，直接返回原始内容
        if (!content.contains("<QrcInfos", ignoreCase = true) &&
            !content.contains("<LyricInfo", ignoreCase = true) &&
            !Regex("""<Lyric_\d+\b""", RegexOption.IGNORE_CASE).containsMatchIn(content)
        ) {
            return content
        }

        // QQ QRC output uses LyricContent="..." attributes, and XML parsers normalize whitespace
        // (newlines become spaces), which breaks line splitting. We prefer regex extraction to
        // preserve original newlines.
        //
        // Note: QRC payloads often wrap LyricContent in double quotes, but the lyric text may
        // contain apostrophes or even internal quotation marks. We want to use the outermost
        // quotes for the attribute value so that inner quotes don't truncate the match.
        val extracted = mutableListOf<String>()
        // 匹配 <Lyric_N LyricContent='...' /> 或 <Lyric_N LyricContent="..." />
        val tagStartRegex = Regex("""<Lyric_\d+\b[^>]*\bLyricContent=(['"])""", RegexOption.IGNORE_CASE)
        var searchIndex = 0

        // 循环查找所有 LyricContent 属性
        while (true) {
            val match = tagStartRegex.find(content, startIndex = searchIndex) ?: break
            val quoteChar = match.groupValues[1].single()  // 获取引号类型（单引号或双引号）
            val valueStart = match.range.last + 1  // 属性值开始位置

            // Find the end of the current tag so we can pick the outermost closing quote inside it.
            val tagEnd = content.indexOf('>', startIndex = valueStart).takeIf { it >= 0 } ?: content.length
            val lastQuoteBeforeTagEnd = content.lastIndexOf(quoteChar, startIndex = tagEnd - 1)
            val valueEnd = if (lastQuoteBeforeTagEnd >= valueStart) {
                lastQuoteBeforeTagEnd
            } else {
                // Fallback: find the first quote after the start
                content.indexOf(quoteChar, startIndex = valueStart).takeIf { it >= 0 } ?: content.length
            }

            // 提取属性值并进行 XML 反转义
            val rawValue = content.substring(valueStart, valueEnd)
            extracted.add(unescapeXmlAttribute(rawValue))

            searchIndex = valueEnd + 1
        }

        // 如果成功提取了内容，用换行符拼接并返回
        if (extracted.isNotEmpty()) {
            Timber.d("[QrcParser] Extracted $extracted.size LyricContent entries from QRC XML (regex): ${extracted.joinToString("\\n").take(500)}")
            return extracted.joinToString(separator = "\n")
        }

        // Fallback to DOM parsing if regex failed (very rare)
        // 如果正则表达式失败（极罕见），使用 DOM 解析器
        return try {
            val builder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc = builder.parse(content.byteInputStream())
            val lyricContents = mutableListOf<String>()

            val allElements = doc.getElementsByTagName("*")
            for (i in 0 until allElements.length) {
                val node = allElements.item(i)
                if (node is org.w3c.dom.Element) {
                    val lyricContent = node.getAttribute("LyricContent")
                    if (!lyricContent.isNullOrBlank()) {
                        lyricContents.add(lyricContent)
                    }
                }
            }

            if (lyricContents.isEmpty()) {
                Timber.d("[QrcParser] Detected QRC XML but no LyricContent attributes found; falling back to raw content")
                return content
            }
            
            Timber.d("[QrcParser] Extracted $lyricContents.size LyricContent entries from QRC XML (DOM)")
            lyricContents.joinToString(separator = "\n")
        } catch (e: Exception) {
            Timber.w("[QrcParser] Failed to parse QRC XML content; falling back to raw content", e)
            content
        }
    }

    /**
     * XML 属性反转移函数
     * 
     * 将 XML 实体引用转换回原始字符：
     * - &quot; -> "
     * - &apos; -> '
     * - &lt; -> <
     * - &gt; -> >
     * - &amp; -> &
     * - &#数字; -> Unicode 字符（十进制）
     * - &#x 十六进制; -> Unicode 字符（十六进制）
     * 
     * @param value 包含 XML 实体的字符串
     * @return 反转义后的字符串
     */
    private fun unescapeXmlAttribute(value: String): String {
        return value
            .replace("&quot;", "\"")      // 双引号
            .replace("&apos;", "'")       // 单引号
            .replace("&lt;", "<")         // 小于号
            .replace("&gt;", ">")         // 大于号
            .replace("&amp;", "&")        // 和号（必须最后处理）
            .replace(Regex("&#(\\d+);")) { match ->
                // 十进制 Unicode 字符引用
                match.groups[1]?.value?.toIntOrNull()?.toChar()?.toString() ?: match.value
            }
            .replace(Regex("&#x([0-9A-Fa-f]+);")) { match ->
                // 十六进制 Unicode 字符引用
                match.groups[1]?.value?.toIntOrNull(16)?.toChar()?.toString() ?: match.value
            }
    }
}
