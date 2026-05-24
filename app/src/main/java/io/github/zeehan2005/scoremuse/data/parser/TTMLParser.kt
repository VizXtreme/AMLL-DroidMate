package io.github.zeehan2005.scoremuse.data.parser

import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.LyricWord
import io.github.zeehan2005.scoremuse.global.SongStructure
import io.github.zeehan2005.scoremuse.global.SongStructureType
import io.github.zeehan2005.scoremuse.global.UnifiedLyrics
import io.github.zeehan2005.scoremuse.global.LyricsMetadata
import timber.log.Timber
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * TTML (Timed Text Markup Language) 格式解析器
 * 
 * TTML 是一种基于 XML 的字幕格式标准，广泛用于视频字幕和歌词。
 * 这个解析器负责从 TTML XML 文档中提取：
 * - 歌词行信息（文本、时间戳）
 * - 逐词时间信息（用于逐字高亮）
 * - 翻译和音译
 * - 歌曲结构信息（前奏、主歌、副歌等）
 * - 元数据（标题、艺术家等）
 * 
 * 特殊处理：
 * - 自动修复格式错误的 TTML（Apple 的 TTML 经常有不规范的标签）
 * - 支持内联样式和块级样式
 * - 保留原始结构信息供后续处理
 */
object TTMLParser {
    
    // XML 解析工厂（线程安全，可复用）
    private val factory = DocumentBuilderFactory.newInstance()
    
    /**
     * 解析 TTML 内容，返回完整的 UnifiedLyrics 对象
     * 
     * 这是 TTML 解析的主入口方法。它会：
     * 1. 解析 XML 文档结构
     * 2. 提取元数据（标题、艺术家等）
     * 3. 提取所有歌词行及其时间信息
     * 4. 识别歌曲结构段落（前奏、主歌、副歌等）
     * 
     * 容错机制：
     * - 如果正常解析失败，会尝试清理格式后重试
     * - 完全失败时返回空列表，不会崩溃
     * 
     * @param content TTML XML 字符串
     * @return 包含元数据和歌词行的完整对象
     */
    fun parse(content: String): UnifiedLyrics {
        if (content.isBlank()) return UnifiedLyrics(
            metadata = LyricsMetadata(title = "Unknown", artist = "Unknown"),
            lines = emptyList(),
            rawContent = "",
            format = "ttml"
        )

        val builder = factory.newDocumentBuilder()

        // attempt a normal parse first; if it fails we may be dealing with
        // malformed metadata tags (common in Apple-supplied TTML) and we'll
        // retry after sanitizing the input.
        fun tryParse(input: String): UnifiedLyrics {
            val doc = builder.parse(input.byteInputStream())
            return parseTTMLDocument(doc).copy(rawContent = input, format = "ttml")
        }

        // 首先尝试正常解析
        return try {
            tryParse(content)
        } catch (e: Exception) {
                Timber.w("[TTMLParser] Normal parse failed, attempting sanitized parse $e")
            // 如果失败，尝试清理格式后重试
            try {
                val sanitized = sanitizeTTMLContent(content)
                tryParse(sanitized)
            } catch (e2: Exception) {
                Timber.e("[TTMLParser] Both normal and sanitized parse failed $e2")
                // 完全失败时返回空列表，不会崩溃
                UnifiedLyrics(
                    metadata = LyricsMetadata(title = "Unknown", artist = "Unknown"),
                    lines = emptyList(),
                    rawContent = content,
                    format = "ttml"
                )
            }
        }
    }

    private data class ParsedParagraph(
        val mainLine: LyricLine?,
        val bgLine: LyricLine?,
        val agent: String?
    )

    private data class ParagraphParseBuffer(
        val mainWords: MutableList<LyricWord> = mutableListOf(),
        val bgWords: MutableList<LyricWord> = mutableListOf(),
        var translation: String? = null,
        var transliteration: String? = null,
        var bgTranslation: String? = null,
        var bgTransliteration: String? = null
    )

    private fun parseTTMLDocument(doc: Document): UnifiedLyrics {
        val parsedParagraphs = mutableListOf<ParsedParagraph>()
        
        // 解析 TTML 元数据中的歌曲结构信息
        Timber.d("[SongStructure] Starting TTML document parsing")
        val songStructures = parseSongStructuresFromMetadata(doc)
        Timber.d("[SongStructure] TTML metadata parsing complete: ${songStructures.size} structures found")

        try {
            val body = doc.getElementsByTagName("body").item(0) as? Element ?: return UnifiedLyrics(
                metadata = LyricsMetadata(title = "Unknown", artist = "Unknown"),
                lines = emptyList()
            )
            val paragraphs = body.getElementsByTagName("p")
            for (i in 0 until paragraphs.length) {
                val pElement = paragraphs.item(i) as? Element ?: continue
                val rawAgent = pElement.getAttribute("ttm:agent").ifBlank { pElement.getAttribute("agent") }
                if (i < 5 || i >= paragraphs.length - 2) {
                    Timber.d("[AgentDebug-RAW] Para $i: raw ttm:agent='$rawAgent'")
                }
                parseParagraph(pElement)?.let { parsedParagraphs.add(it) }
            }
        } catch (e: Exception) {
                Timber.e("[TTMLParser] Failed to parse TTML document structure $e")
            return UnifiedLyrics(
                metadata = LyricsMetadata(title = "Unknown", artist = "Unknown"),
                lines = emptyList()
            )
        }

        val normalizedAgents = parsedParagraphs.map { normalizeAgent(it.agent) }
        val uniqueAgents = normalizedAgents.filterNotNull().distinct()
        
        Timber.i("[AgentAnalyzer] Total lines: ${parsedParagraphs.size}, unique agents: ${uniqueAgents.size}, agents: $uniqueAgents")
        
        val duetFlags = when {
            uniqueAgents.size <= 1 -> {
                Timber.d("[AgentAnalyzer] Mode: single or no agent (all isDuet=false)")
                List(parsedParagraphs.size) { false }
            }
            uniqueAgents.size == 2 -> {
                val leftAgent = pickLeftAgentForTwo(uniqueAgents[0], uniqueAgents[1])
                Timber.d("[AgentAnalyzer] Mode: exactly 2 agents. leftAgent=$leftAgent, rightAgent=${uniqueAgents.firstOrNull { it != leftAgent }}")
                normalizedAgents.mapIndexed { idx, agent ->
                    val isDuet = agent != null && agent != leftAgent
                    if (idx < 5) Timber.d("[AgentAnalyzer] Line $idx: agent=$agent -> isDuet=$isDuet")
                    isDuet
                }
            }
            else -> {
                Timber.d("[AgentAnalyzer] Mode: >2 agents, alternating mode")
                val flags = buildAlternatingDuetFlags(normalizedAgents)
                flags.mapIndexed { idx, isDuet ->
                    if (idx < 10) Timber.d("[AgentAnalyzer] Line $idx: agent=${normalizedAgents[idx]} -> isDuet=$isDuet")
                    isDuet
                }
            }
        }

        val lines = mutableListOf<LyricLine>()
        for ((index, parsed) in parsedParagraphs.withIndex()) {
            val isDuet = duetFlags.getOrElse(index) { false }
            parsed.mainLine?.let { lines.add(it.copy(isDuet = isDuet)) }
            parsed.bgLine?.let { lines.add(it.copy(isBG = true, isDuet = isDuet)) }
        }

        Timber.d("[TTMLParser] Parse complete: ${lines.size} total output lines")
        
        // 解析 TTML 元数据信息
        val metadata = try {
            Timber.d("[TTMLParser] Parsing TTML metadata")
            val head = doc.getElementsByTagName("head").item(0) as? Element
            val metadataElement = head?.getElementsByTagName("metadata")?.item(0) as? Element
            
            var title: String? = null
            var artist: String? = null
            var album: String? = null
            val language = "ja"
            var rawXmlMetadata: String? = null
            
            if (metadataElement != null) {
                // ✅ 保存原始 metadata 元素的完整 XML，用于未来扩展和保留未使用的信息
                rawXmlMetadata = elementToXml(metadataElement)
                Timber.d("[TTMLParser] Saved raw XML metadata (${rawXmlMetadata.length} chars) for future extensibility")


                // 如果没有找到，尝试从 itunes 命名空间读取
                title = metadataElement.getAttribute("itunes:title").takeIf { it.isNotEmpty() }
                artist = metadataElement.getAttribute("itunes:artist").takeIf { it.isNotEmpty() }
                album = metadataElement.getAttribute("itunes:album").takeIf { it.isNotEmpty() }
            }
            
            LyricsMetadata(
                title = title ?: "Unknown",
                artist = artist ?: "Unknown",
                album = album,
                language = language,
                rawXmlMetadata = rawXmlMetadata
            )
        } catch (e: Exception) {
                Timber.w("[TTMLParser] Failed to parse metadata $e")
            LyricsMetadata(title = "Unknown", artist = "Unknown")
        }
        
        Timber.d("[SongStructure] Creating UnifiedLyrics with ${songStructures.size} structures")
        return UnifiedLyrics(
            metadata = metadata.copy(songStructures = songStructures.ifEmpty { null }),
            lines = lines
        )
    }

    private fun parseParagraph(pElement: Element): ParsedParagraph? {
        return try {
            val beginStr = pElement.getAttribute("begin")
            val endStr = pElement.getAttribute("end")

            val startTime = timeStrToMillis(beginStr)
            var endTime = timeStrToMillis(endStr)
            if (endTime <= startTime) {
                endTime = startTime + 3000L
            }

            val agent = readAgentAttr(pElement)
            val buffer = ParagraphParseBuffer()

            parseNodeChildren(
                parent = pElement,
                inBackground = false,
                buffer = buffer
            )

            val mainText = if (buffer.mainWords.isNotEmpty()) {
                buffer.mainWords.joinToString(separator = "") { it.word }
            } else {
                normalizeLyricTextPreservingSpaces(collectPlainText(pElement, includeBackground = false))
            }

            val bgText = if (buffer.bgWords.isNotEmpty()) {
                buffer.bgWords.joinToString(separator = "") { it.word }
            } else {
                normalizeLyricTextPreservingSpaces(collectPlainText(pElement, includeBackground = true, backgroundOnly = true))
            }

            val mainLine = if (mainText.isNotEmpty()) {
                val mainStart = buffer.mainWords.firstOrNull()?.startTime ?: startTime
                val mainEndRaw = buffer.mainWords.lastOrNull()?.endTime ?: endTime
                val mainEnd = maxOf(mainStart + 1, mainEndRaw) // 确保持续时间至少为 1ms
                LyricLine(
                    startTime = mainStart,
                    endTime = mainEnd,
                    text = mainText,
                    translation = buffer.translation,
                    transliteration = buffer.transliteration,
                    words = buffer.mainWords.toList(),
                    agent = agent
                )
            } else {
                null
            }

            val bgLine = if (bgText.isNotEmpty()) {
                val bgStart = buffer.bgWords.firstOrNull()?.startTime ?: startTime
                val bgEndRaw = buffer.bgWords.lastOrNull()?.endTime ?: endTime
                val bgEnd = maxOf(bgStart + 1, bgEndRaw) // 确保持续时间至少为 1ms
                Timber.d("[BG-LYRICS-DEBUG] Creating BG line: text='$bgText' translation='${buffer.bgTranslation}' roman='${buffer.bgTransliteration}'")
                LyricLine(
                    startTime = bgStart,
                    endTime = bgEnd,
                    text = bgText,
                    translation = buffer.bgTranslation,
                    transliteration = buffer.bgTransliteration,
                    words = buffer.bgWords.toList(),
                    isBG = true,
                    agent = agent
                )
            } else {
                null
            }

            if (mainLine == null && bgLine == null) {
                null
            } else {
                ParsedParagraph(mainLine = mainLine, bgLine = bgLine, agent = agent)
            }
        } catch (e: Exception) {
                Timber.w("[TTMLParser] Failed to parse paragraph $e")
            null
        }
    }

    private fun parseNodeChildren(
        parent: Node,
        inBackground: Boolean,
        buffer: ParagraphParseBuffer
    ) {
        val childNodes = parent.childNodes ?: return
        for (i in 0 until childNodes.length) {
            val child = childNodes.item(i)
            when (child.nodeType) {
                Node.TEXT_NODE -> {
                    // 警示后人：<p> 内 span 之间的空白是可见语义，不能直接忽略。
                    // 这里只把“无换行的纯空白分隔符”回填到前一个词尾，避免空格丢失。
                    appendDelimiterSpaceIfNeeded(child.nodeValue ?: "", inBackground, buffer)
                }

                Node.ELEMENT_NODE -> {
                    val element = child as? Element ?: continue
                    val tag = element.tagName.substringAfter(':').lowercase()
                    if (tag != "span") {
                        parseNodeChildren(element, inBackground, buffer)
                        continue
                    }

                    val role = readRoleAttr(element)
                    when (role) {
                        "x-translation" -> {
                            val text = normalizeAuxiliaryText(element.textContent ?: "")
                            if (text.isNotEmpty()) {
                                if (inBackground) {
                                    buffer.bgTranslation = text
                                    Timber.d("[BG-LYRICS-DEBUG] Collected BG translation: $text")
                                } else {
                                    // 兼容部分 TTML：x-bg 与 x-translation 平级时，将翻译归属到背景行。
                                    if (buffer.mainWords.isEmpty() && buffer.bgWords.isNotEmpty()) {
                                        buffer.bgTranslation = text
                                        Timber.d("[BG-LYRICS-DEBUG] Collected BG translation (fallback outside x-bg): $text")
                                    } else {
                                        buffer.translation = text
                                    }
                                }
                            }
                        }

                        "x-roman", "x-romanization" -> {
                            val text = normalizeAuxiliaryText(element.textContent ?: "")
                            if (text.isNotEmpty()) {
                                if (inBackground) {
                                    buffer.bgTransliteration = text
                                    Timber.d("[BG-LYRICS-DEBUG] Collected BG transliteration: $text")
                                } else {
                                    // 兼容部分 TTML：x-bg 与 x-roman 平级时，将音译归属到背景行。
                                    if (buffer.mainWords.isEmpty() && buffer.bgWords.isNotEmpty()) {
                                        buffer.bgTransliteration = text
                                        Timber.d("[BG-LYRICS-DEBUG] Collected BG transliteration (fallback outside x-bg): $text")
                                    } else {
                                        buffer.transliteration = text
                                    }
                                }
                            }
                        }

                        "x-bg" -> {
                            parseNodeChildren(element, true, buffer)

                            val bgText = normalizeLyricTextPreservingSpaces(
                                collectPlainText(element, includeBackground = true)
                            )
                            val hasBeginAttr = element.hasAttribute("begin")
                            val hasEndAttr = element.hasAttribute("end")
                            val begin = timeStrToMillis(element.getAttribute("begin"))
                            val end = timeStrToMillis(element.getAttribute("end"))
                            if (
                                bgText.isNotEmpty() &&
                                hasBeginAttr &&
                                hasEndAttr &&
                                end >= begin &&
                                !hasDirectTimedSpanChild(element)
                            ) {
                                buffer.bgWords.add(
                                    LyricWord(
                                        word = cleanBackgroundText(bgText),
                                        startTime = begin,
                                        endTime = maxOf(begin + 1, end) // 确保持续时间至少为 1ms
                                    )
                                )
                            }
                        }

                        else -> {
                            val hasBeginAttr = element.hasAttribute("begin")
                            val hasEndAttr = element.hasAttribute("end")
                            val begin = timeStrToMillis(element.getAttribute("begin"))
                            val end = timeStrToMillis(element.getAttribute("end"))
                            if (hasBeginAttr && hasEndAttr && end >= begin && !hasDirectTimedSpanChild(element)) {
                                val text = normalizeLyricTextPreservingSpaces(element.textContent ?: "")
                                if (text.isNotEmpty()) {
                                    val word = LyricWord(
                                        word = if (inBackground) cleanBackgroundText(text) else text,
                                        startTime = begin,
                                        endTime = maxOf(begin + 1, end) // 确保持续时间至少为 1ms，防止 JS 侧计算 NaN
                                    )
                                    if (inBackground) {
                                        buffer.bgWords.add(word)
                                    } else {
                                        buffer.mainWords.add(word)
                                    }
                                }
                            }
                            parseNodeChildren(element, inBackground, buffer)
                        }
                    }
                }
            }
        }
    }

    private fun hasDirectTimedSpanChild(element: Element): Boolean {
        val children = element.childNodes ?: return false
        for (i in 0 until children.length) {
            val node = children.item(i)
            if (node.nodeType != Node.ELEMENT_NODE) continue
            val child = node as Element
            val tag = child.tagName.substringAfter(':').lowercase()
            if (tag != "span") continue
            val hasBegin = child.hasAttribute("begin")
            val hasEnd = child.hasAttribute("end")
            if (hasBegin && hasEnd) return true
        }
        return false
    }

    private fun collectPlainText(
        node: Node,
        includeBackground: Boolean,
        backgroundOnly: Boolean = false,
        inBackground: Boolean = false
    ): String {
        return when (node.nodeType) {
            Node.TEXT_NODE -> {
                if (backgroundOnly && !inBackground) {
                    ""
                } else {
                    node.nodeValue ?: ""
                }
            }

            Node.ELEMENT_NODE -> {
                val element = node as Element
                val tag = element.tagName.substringAfter(':').lowercase()
                if (tag == "span") {
                    val role = readRoleAttr(element)
                    when (role) {
                        "x-translation", "x-roman", "x-romanization" -> return ""
                        "x-bg" -> {
                            if (!includeBackground) return ""
                            return iteratePlainTextChildren(
                                element,
                                includeBackground = true,
                                backgroundOnly = backgroundOnly,
                                inBackground = true
                            )
                        }
                    }
                }

                iteratePlainTextChildren(
                    element,
                    includeBackground = includeBackground,
                    backgroundOnly = backgroundOnly,
                    inBackground = inBackground
                )
            }

            else -> ""
        }
    }

    private fun iteratePlainTextChildren(
        parent: Node,
        includeBackground: Boolean,
        backgroundOnly: Boolean,
        inBackground: Boolean
    ): String {
        val builder = StringBuilder()
        val children = parent.childNodes ?: return ""
        for (i in 0 until children.length) {
            builder.append(
                collectPlainText(
                    node = children.item(i),
                    includeBackground = includeBackground,
                    backgroundOnly = backgroundOnly,
                    inBackground = inBackground
                )
            )
        }
        return builder.toString()
    }

    private fun readRoleAttr(element: Element): String {
        return (element.getAttribute("ttm:role")
            .ifBlank { element.getAttribute("role") })
            .trim()
            .lowercase()
    }

    private fun readAgentAttr(element: Element): String? {
        val raw = element.getAttribute("ttm:agent")
            .ifBlank { element.getAttribute("agent") }
            .trim()
        return raw.ifBlank { null }
    }

    private fun normalizeAgent(agent: String?): String? {
        val normalized = agent?.trim()?.lowercase()?.removePrefix("#")
        return normalized?.ifBlank { null }
    }

    private fun pickLeftAgentForTwo(agentA: String, agentB: String): String {
        val numberA = extractFirstNumber(agentA)
        val numberB = extractFirstNumber(agentB)

        val result = when {
            numberA != null && numberB != null -> if (numberA <= numberB) agentA else agentB
            numberA != null -> agentA
            numberB != null -> agentB
            else -> if (agentA <= agentB) agentA else agentB
        }
        
        Timber.d("[AGENT-DEBUG] pickLeftAgentForTwo: agentA='$agentA'(num=$numberA) vs agentB='$agentB'(num=$numberB) -> leftAgent='$result'")
        return result
    }

    private fun buildAlternatingDuetFlags(agents: List<String?>): List<Boolean> {
        val flags = MutableList(agents.size) { false }
        var lastAgent: String? = null
        var currentIsRight = false

        for (i in agents.indices) {
            val agent = agents[i]
            if (agent == null) {
                flags[i] = currentIsRight
                continue
            }

            if (lastAgent == null) {
                // 多 agent 模式下，首个声部固定在左侧。
                currentIsRight = false
                lastAgent = agent
                Timber.d("[AGENT-DEBUG] alternating: line $i first agent='$agent' -> isDuet=${false}")
            } else if (agent != lastAgent) {
                currentIsRight = !currentIsRight
                Timber.d("[AGENT-DEBUG] alternating: line $i agent change from '$lastAgent' to '$agent' -> isDuet=$currentIsRight")
                lastAgent = agent
            }

            flags[i] = currentIsRight
        }

        return flags
    }

    private fun extractFirstNumber(value: String): Int? {
        val match = Regex("\\d+").find(value) ?: return null
        return match.value.toIntOrNull()
    }

    private fun appendDelimiterSpaceIfNeeded(
        rawDelimiter: String,
        inBackground: Boolean,
        buffer: ParagraphParseBuffer
    ) {
        if (rawDelimiter.isEmpty()) return
        if (!rawDelimiter.all { it.isWhitespace() }) return

        val containsNewline = rawDelimiter.any { it == '\n' || it == '\r' }
        if (containsNewline) return

        val target = if (inBackground) buffer.bgWords else buffer.mainWords
        if (target.isEmpty()) return

        val last = target.last()
        if (last.word.isNotEmpty() && !last.word.last().isWhitespace()) {
            target[target.lastIndex] = last.copy(word = "${last.word} ")
        }
    }

    private fun normalizeAuxiliaryText(text: String): String {
        if (text.isEmpty()) return ""

        // QQ TTML sometimes uses "//" (or repeated slashes) to mean "no translation".
        // Treat it as empty so the UI doesn't show it as literal text.
        val normalized = text.replace(Regex("[\\t\\r\\n]+"), " ").trim()
        // QQ TTML may use "//" to indicate no translation; keep a single slash.
        if (normalized == "//") return ""
        return normalized
    }

    private fun normalizeLyricTextPreservingSpaces(text: String): String {
        if (text.isEmpty()) return ""

        // 警示后人：<p>/<span> 的空格是歌词可见语义，严禁在这里 trim 或压缩空格。
        // 仅移除换行控制字符，避免格式化缩进影响，同时保留普通空格。
        return text
            .replace("\r", "")
            .replace("\n", "")
    }

    private fun cleanBackgroundText(text: String): String {
        // 背景歌词同样遵循可见空格语义：禁止 trim。
        // 仅去除文本中第一个 "(" 和最后一个 ")"，不改动其它内容。
        val firstParenIndex = text.indexOf('(')
        val lastParenIndex = text.lastIndexOf(')')
        
        if (firstParenIndex != -1 && lastParenIndex != -1 && lastParenIndex > firstParenIndex) {
            // 移除第一个 "(" 和最后一个 ")"
            return text.substring(0, firstParenIndex) +
                   text.substring(firstParenIndex + 1, lastParenIndex) +
                   text.substring(lastParenIndex + 1)
        }
        
        return text
    }
    
    /**
     * 解析 TTML 元数据中的歌曲结构信息
     * 只支持一种标准格式：<body> 中 <div> 标签上的 itunes:song-part/songPart 属性
     */
    private fun parseSongStructuresFromMetadata(doc: Document): List<SongStructure> {
        val structures = mutableListOf<SongStructure>()
                    
        try {
            Timber.d("[SongStructure] 🔍 Starting metadata structure parsing (only from <div> itunes:songPart attributes)")
            
            // 唯一合法的方式：从 <body> 中的 <div> 标签解析 itunes:song-part/songPart 属性
            Timber.d("[SongStructure] 📁 Parsing structures from <div> elements in <body>")
            parseSongStructuresFromBodyElements(doc, structures)
            
            if (structures.isEmpty()) {
                Timber.i("[SongStructure] ⚠️ No songPart attributes found on <div> elements")
            } else {
                Timber.d("[SongStructure] ✅ Found ${structures.size} structures from <div> itunes:songPart attributes")
            }
            
            Timber.d("[SongStructure] 📊 Metadata parsing complete: total ${structures.size} structures")
                    
        } catch (e: Exception) {
                Timber.e("[SongStructure] ❌ Error parsing metadata $e")
        }
                
        return structures
    }
        

        
    /**
     * 从 <body> 中的 <div> 标签解析歌曲结构（唯一合法方式）
     * 优先查找 itunes:songPart 属性，兼容 itunes:song-part 属性
     */
    private fun parseSongStructuresFromBodyElements(doc: Document, structures: MutableList<SongStructure>) {
        val body = doc.getElementsByTagName("body").item(0) as? Element ?: run {
            Timber.d("[SongStructure] ⚠️ No <body> element found")
            return
        }
            
        // 优先查找带有 itunes:songPart 或 itunes:song-part 属性的 div 元素
        val divs = body.getElementsByTagName("div")
        
        Timber.d("[SongStructure] 🔍 Checking $divs divs for songPart attributes")
            
        var index = 0
        var foundCount = 0
            
        // 只处理 div 元素
        for (i in 0 until divs.length) {
            val div = divs.item(i) as? Element ?: continue
            val songPartAttr = readSongPartAttribute(div)
            if (songPartAttr != null) {
                foundCount++
                Timber.d("[SongStructure] 🎵 Found songPart attribute on div[$i]: $songPartAttr")
                parseSongStructureFromAttribute(div, songPartAttr, index++, structures)
            }
        }
        
        if (foundCount == 0) {
            Timber.d("[SongStructure] ⚠️ No songPart attributes found on <div> elements")
        } else {
            Timber.d("[SongStructure] ✅ Found $foundCount <div> elements with songPart attributes")
        }
    }
        
    /**
     * 读取元素的 itunes:songPart 或 itunes:song-part 属性值
     * 优先使用 itunes:songPart（iTunes 官方标准），兼容 itunes:song-part
     */
    private fun readSongPartAttribute(element: Element): String? {
        // 优先尝试驼峰式 itunes:songPart（iTunes 官方标准）
        val camelCase = element.getAttribute("itunes:songPart").takeIf { it.isNotEmpty() }
        if (camelCase != null) return camelCase
            
        // 兼容连字符式 itunes:song-part
        val kebabCase = element.getAttribute("itunes:song-part").takeIf { it.isNotEmpty() }
        if (kebabCase != null) return kebabCase
            
        return null
    }
        
    /**
     * 从元素属性解析歌曲结构
     */
    private fun parseSongStructureFromAttribute(
        element: Element,
        label: String,
        index: Int,
        structures: MutableList<SongStructure>
    ) {
        // 尝试从 begin/end 属性获取时间
        val beginStr = element.getAttribute("begin").takeIf { it.isNotEmpty() }
        val endStr = element.getAttribute("end").takeIf { it.isNotEmpty() }
        
        Timber.d("[SongStructure] Parsing structure from attribute: index=$index, label='$label', hasBegin=${beginStr != null}, hasEnd=${endStr != null}")
            
        if (beginStr != null && endStr != null) {
            val startTime = timeStrToMillis(beginStr)
            val endTime = timeStrToMillis(endStr)
                
            if (endTime > startTime) {
                val type = mapStructureType(label)
                val structure = SongStructure(
                    label = label,
                    startTime = startTime,
                    endTime = endTime,
                    type = type
                )
                structures.add(structure)
                    
                Timber.d("[SongStructure] ✅ Parsed from attribute: $label ($type) ${formatTime(startTime)} - ${formatTime(endTime)}")
            } else {
                Timber.w("[SongStructure] ⚠️ Invalid time range for structure: begin=$startTime, end=$endTime")
            }
        } else {
            Timber.d("[SongStructure] Skipping structure without begin/end attributes")
        }
    }
    
    /**
     * 将结构标签映射到 SongStructureType
     */
    private fun mapStructureType(label: String): SongStructureType {
        val lowerLabel = label.lowercase()
        return when {
            lowerLabel.contains("verse") -> SongStructureType.VERSE
            lowerLabel.contains("chorus") -> SongStructureType.CHORUS
            lowerLabel.contains("bridge") -> SongStructureType.BRIDGE
            lowerLabel.contains("pre-chorus") || lowerLabel.contains("prechorus") -> SongStructureType.PRE_CHORUS
            lowerLabel.contains("intro_para") -> SongStructureType.INTRO_PARA
            lowerLabel.contains("intro_inst") -> SongStructureType.INTRO_INST
            lowerLabel.contains("intro") -> SongStructureType.INTRO_PARA  // 默认将 intro 视为引子
            lowerLabel.contains("outro_para") -> SongStructureType.OUTRO_PARA
            lowerLabel.contains("outro_inst") -> SongStructureType.OUTRO_INST
            lowerLabel.contains("outro") -> SongStructureType.OUTRO_PARA  // 默认将 outro 视为尾声
            lowerLabel.contains("interlude") -> SongStructureType.INTERLUDE
            lowerLabel.contains("solo") -> SongStructureType.SOLO
            lowerLabel.contains("break") -> SongStructureType.BREAK
            else -> SongStructureType.UNKNOWN
        }
    }

        
    /**
     * 格式化时间为 mm:ss 格式（用于日志）
     */
    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.ROOT, "%d:%02d", minutes, seconds)
    }
        
    /**
     * 将 TTML 时间格式转换为毫秒
     * 格式：mm:ss.mmm (例：00:12.345)
     */
    private fun timeStrToMillis(timeStr: String): Long {
        return try {
            if (timeStr.isBlank()) return 0L

            val normalized = timeStr.trim().lowercase().removeSuffix("s")
            if (normalized.isEmpty()) return 0L

            if (!normalized.contains(":")) {
                val seconds = normalized.toDoubleOrNull() ?: return 0L
                return (seconds * 1000.0).toLong()
            }

            val parts = normalized.split(":")
            if (parts.size !in 2..3) return 0L

            val hours: Long
            val minutes: Long
            val secondToken: String
            if (parts.size == 3) {
                hours = parts[0].toLongOrNull() ?: return 0L
                minutes = parts[1].toLongOrNull() ?: return 0L
                secondToken = parts[2]
            } else {
                hours = 0L
                minutes = parts[0].toLongOrNull() ?: return 0L
                secondToken = parts[1]
            }

            val secParts = secondToken.split(".")
            val seconds = secParts[0].toLongOrNull() ?: return 0L
            val millis = if (secParts.size > 1) {
                secParts[1].padEnd(3, '0').take(3).toLongOrNull() ?: 0L
            } else 0L

            (hours * 3600 + minutes * 60 + seconds) * 1000 + millis
        } catch (e: Exception) {
            Timber.e("[TTMLParser] Failed to parse time string: $timeStr $e")
            0L
        }
    }

    /**
     * 将 XML Element 转换为字符串（保留所有属性和子元素）
     */
    private fun elementToXml(element: Element): String {
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        
        // 配置输出格式
        val output = java.io.StringWriter()
        val result = StreamResult(output)
        
        // 设置缩进和编码
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "no")
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8")
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes")
        
        // 执行转换
        val source = DOMSource(element.ownerDocument)
        transformer.transform(source, result)
        
        return output.toString()
    }

    /**
     * 清理 TTML 内容，修复常见的格式问题
     * 
     * 主要处理：
     * 1. 移除 BOM (Byte Order Mark)
     * 2. 修复不完整的命名空间声明
     * 3. 清理非法字符
     * 4. 确保 XML 结构完整
     */
    private fun sanitizeTTMLContent(content: String): String {
        var sanitized = content
        
        // 1. 移除 BOM
        if (sanitized.startsWith("\uFEFF")) {
            sanitized = sanitized.substring(1)
        }
        
        // 2. 修复常见的命名空间问题 - 确保有必要的命名空间声明
        if (!sanitized.contains("xmlns:ttm=")) {
            sanitized = sanitized.replace(
                "<tt ",
                "<tt xmlns:ttm=\"http://www.w3.org/ns/ttml#metadata\" "
            )
        }
        
        if (!sanitized.contains("xmlns:itunes=")) {
            sanitized = sanitized.replace(
                "<tt ",
                "<tt xmlns:itunes=\"http://music.apple.com/namespace/1.0/\" "
            )
        }
        
        // 3. 清理一些常见的非法 XML 字符（但保留合法的歌词字符）
        sanitized = sanitized.replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]"), "")
        
        // 4. 确保根元素闭合
        if (!sanitized.trimEnd().endsWith("</tt>")) {
            Timber.w("[TTMLParser] TTML content may be incomplete or malformed")
        }
        
        return sanitized
    }


}