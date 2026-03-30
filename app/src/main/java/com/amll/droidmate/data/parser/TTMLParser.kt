package com.amll.droidmate.data.parser

import com.amll.droidmate.domain.model.LyricLine
import com.amll.droidmate.domain.model.LyricWord
import com.amll.droidmate.domain.model.SongStructure
import com.amll.droidmate.domain.model.SongStructureType
import com.amll.droidmate.domain.model.TTMLLyrics
import com.amll.droidmate.domain.model.TTMLMetadata
import timber.log.Timber
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

/**
 * TTML 格式解析器
 * 从 TTML XML 格式中提取歌词行和词级时间戳
 */
object TTMLParser {
    
    private val factory = DocumentBuilderFactory.newInstance()
    
    /**
     * 解析 TTML 内容，返回完整的 TTMLLyrics 对象（包含元数据和歌曲结构）
     */
    fun parse(content: String): TTMLLyrics {
        if (content.isBlank()) return TTMLLyrics(
            metadata = TTMLMetadata(title = "Unknown", artist = "Unknown"),
            lines = emptyList()
        )

        val builder = factory.newDocumentBuilder()

        // attempt a normal parse first; if it fails we may be dealing with
        // malformed metadata tags (common in Apple-supplied TTML) and we'll
        // retry after sanitizing the input.
        fun tryParse(input: String): TTMLLyrics {
            val doc = builder.parse(input.byteInputStream())
            return parseTTMLDocument(doc)
        }

        return try {
            tryParse(content)
        } catch (e: Exception) {
            Timber.w("[TTMLParser] Initial TTML parse failed, trying sanitization", e)
            val sanitized = sanitizeTTMLContent(content)
            return try {
                tryParse(sanitized)
            } catch (e2: Exception) {
                Timber.e("[TTMLParser] Failed to parse ttml content after sanitization", e2)
                TTMLLyrics(
                    metadata = TTMLMetadata(title = "Unknown", artist = "Unknown"),
                    lines = emptyList()
                )
            }
        }
    }
    
    /**
     * 旧版兼容方法，仅返回歌词行（不推荐在新代码中使用）
     */
    @Deprecated("Use parse(content): TTMLLyrics instead")
    fun parseLegacy(content: String): List<LyricLine> {
        return parse(content).lines
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

    private fun parseTTMLDocument(doc: Document): TTMLLyrics {
        val parsedParagraphs = mutableListOf<ParsedParagraph>()
        
        // 解析 TTML 元数据中的歌曲结构信息
        Timber.d("[SongStructure] Starting TTML document parsing")
        val songStructures = parseSongStructuresFromMetadata(doc)
        Timber.d("[SongStructure] TTML metadata parsing complete: ${songStructures.size} structures found")

        try {
            val body = doc.getElementsByTagName("body").item(0) as? Element ?: return TTMLLyrics(
                metadata = TTMLMetadata(title = "Unknown", artist = "Unknown"),
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
            Timber.e("[TTMLParser] Failed to parse TTML document structure", e)
            return TTMLLyrics(
                metadata = TTMLMetadata(title = "Unknown", artist = "Unknown"),
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
            var language = "ja"
            
            if (metadataElement != null) {
                // 尝试从 amll:meta 标签中读取元数据
                val metaElements = metadataElement.getElementsByTagName("amll:meta")
                for (i in 0 until metaElements.length) {
                    val meta = metaElements.item(i) as? Element ?: continue
                    val key = meta.getAttribute("key")
                    val value = meta.getAttribute("value")
                    
                    when (key) {
                        "title" -> title = value
                        "artist" -> artist = value
                        "album" -> album = value
                        "language" -> language = value
                    }
                }
                
                // 如果没有找到，尝试从 itunes 命名空间读取
                if (title == null) {
                    title = metadataElement.getAttribute("itunes:title").takeIf { it.isNotEmpty() }
                }
                if (artist == null) {
                    artist = metadataElement.getAttribute("itunes:artist").takeIf { it.isNotEmpty() }
                }
                if (album == null) {
                    album = metadataElement.getAttribute("itunes:album").takeIf { it.isNotEmpty() }
                }
            }
            
            TTMLMetadata(
                title = title ?: "Unknown",
                artist = artist ?: "Unknown",
                album = album,
                language = language
            )
        } catch (e: Exception) {
            Timber.e("[TTMLParser] Failed to parse metadata", e)
            TTMLMetadata(title = "Unknown", artist = "Unknown")
        }
        
        Timber.d("[SongStructure] Creating TTMLLyrics with ${songStructures.size} structures")
        return TTMLLyrics(
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
                val mainEnd = maxOf(mainStart, mainEndRaw)
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
                val bgEnd = maxOf(bgStart, bgEndRaw)
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
            Timber.e("[TTMLParser] Failed to parse paragraph", e)
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
                                        endTime = end
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
                                        endTime = end
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
                                includeBackground = includeBackground,
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
                Timber.d("[AGENT-DEBUG] alternating: line $i first agent='$agent' -> isDuet=$currentIsRight")
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
        // 仅在文本本身严格被括号包裹时去掉一层括号，不改动其它空格。
        if (text.length >= 2 && text.startsWith("(") && text.endsWith(")")) {
            return text.substring(1, text.length - 1)
        }
        return text
    }
    
    /**
     * 解析 TTML 元数据中的歌曲结构信息
     * 支持两种格式：
     * 1. <head><metadata> 中的 <itunes:songPart> 元素
     * 2. <div> 或 <p> 标签上的 itunes:song-part/songPart 属性
     */
    private fun parseSongStructuresFromMetadata(doc: Document): List<SongStructure> {
        val structures = mutableListOf<SongStructure>()
                    
        try {
            Timber.d("[SongStructure] Starting metadata structure parsing")
            
            // 方式 1: 解析 <head><metadata> 中的 <itunes:songPart> 元素
            val head = doc.getElementsByTagName("head").item(0) as? Element
            if (head != null) {
                Timber.d("[SongStructure] Found <head> element, parsing from head")
                parseSongStructuresFromHeadElement(head, structures)
            } else {
                Timber.d("[SongStructure] No <head> element found in document")
            }
                
            // 方式 2: 解析 <body> 中 <div> 和 <p> 标签上的 itunes:song-part/songPart 属性
            Timber.d("[SongStructure] Parsing structures from body elements")
            parseSongStructuresFromBodyElements(doc, structures)
                
            // 如果没有找到 itunes:song-part，尝试解析 amll:meta
            if (structures.isEmpty()) {
                Timber.d("[SongStructure] No structures found from itunes:song-part, trying amll:meta fallback")
                parseAmllMetaStructures(doc, structures)
            }
            
            Timber.d("[SongStructure] Metadata parsing complete: found ${structures.size} structures")
                    
        } catch (e: Exception) {
            Timber.e("[SongStructure] Error parsing metadata", e)
        }
                
        return structures
    }
        
    /**
     * 从 <head> 元素解析歌曲结构（针对 <itunes:songPart> 元素）
     */
    private fun parseSongStructuresFromHeadElement(head: Element, structures: MutableList<SongStructure>) {
        val metadataElement = head.getElementsByTagName("metadata").item(0) as? Element ?: run {
            Timber.d("[SongStructure] No <metadata> element found in <head>")
            return
        }
            
        // 尝试解析 itunes:songPart 元素（支持驼峰和连字符两种命名）
        var songPartElements = metadataElement.getElementsByTagName("itunes:songPart")
        if (songPartElements.length == 0) {
            songPartElements = metadataElement.getElementsByTagName("itunes:song-part")
        }
        
        Timber.d("[SongStructure] Found ${songPartElements.length} song-part elements in <head> metadata")
                
        if (songPartElements.length > 0) {
            Timber.d("[SongStructure] Found ${songPartElements.length} song-part elements in <head>")
            for (i in 0 until songPartElements.length) {
                val element = songPartElements.item(i) as? Element ?: continue
                
                // 记录元素属性以便调试
                val label = element.getAttribute("itunes:label")
                val startTime = element.getAttribute("itunes:start-time")
                val duration = element.getAttribute("itunes:duration")
                Timber.d("[SongStructure] Parsing songPart[$i]: label='$label', start='$startTime', duration='$duration'")
                
                parseSongStructureFromElement(element, i, structures)
            }
        } else {
            Timber.d("[SongStructure] No itunes:songPart elements found in <head> metadata")
        }
    }
        
    /**
     * 从 <body> 中的 <div> 和 <p> 标签解析歌曲结构（针对 itunes:song-part/songPart 属性）
     */
    private fun parseSongStructuresFromBodyElements(doc: Document, structures: MutableList<SongStructure>) {
        val body = doc.getElementsByTagName("body").item(0) as? Element ?: run {
            Timber.d("[SongStructure] No <body> element found")
            return
        }
            
        // 查找所有带有 itunes:song-part 或 itunes:songPart 属性的 div 和 p 元素
        val divs = body.getElementsByTagName("div")
        val paragraphs = body.getElementsByTagName("p")
        
        Timber.d("[SongStructure] Checking $divs divs and $paragraphs paragraphs for song-part attributes")
            
        var index = 0
        var foundCount = 0
            
        // 处理 div 元素
        for (i in 0 until divs.length) {
            val div = divs.item(i) as? Element ?: continue
            val songPartAttr = readSongPartAttribute(div)
            if (songPartAttr != null) {
                foundCount++
                Timber.d("[SongStructure] Found song-part attribute on div[$i]: $songPartAttr")
                parseSongStructureFromAttribute(div, songPartAttr, index++, structures)
            }
        }
            
        // 处理 p 元素
        for (i in 0 until paragraphs.length) {
            val p = paragraphs.item(i) as? Element ?: continue
            val songPartAttr = readSongPartAttribute(p)
            if (songPartAttr != null) {
                foundCount++
                Timber.d("[SongStructure] Found song-part attribute on p[$i]: $songPartAttr")
                parseSongStructureFromAttribute(p, songPartAttr, index++, structures)
            }
        }
        
        if (foundCount == 0) {
            Timber.d("[SongStructure] No song-part attributes found on body elements")
        } else {
            Timber.d("[SongStructure] Found $foundCount elements with song-part attributes")
        }
    }
        
    /**
     * 读取元素的 itunes:song-part 或 itunes:songPart 属性值
     */
    private fun readSongPartAttribute(element: Element): String? {
        // 尝试连字符式 itunes:song-part
        val kebabCase = element.getAttribute("itunes:song-part").takeIf { it.isNotEmpty() }
        if (kebabCase != null) return kebabCase
            
        // 尝试驼峰式 itunes:songPart
        val camelCase = element.getAttribute("itunes:songPart").takeIf { it.isNotEmpty() }
        if (camelCase != null) return camelCase
            
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
     * 从元素解析歌曲结构（针对 <itunes:songPart> 元素）
     */
    private fun parseSongStructureFromElement(
        element: Element,
        index: Int,
        structures: MutableList<SongStructure>
    ) {
        val label = element.getAttribute("itunes:label")
        val startTimeStr = element.getAttribute("itunes:start-time")
        val durationStr = element.getAttribute("itunes:duration")
                    
        Timber.d("[SongStructure] Parsing structure from element: index=$index, label='$label'")
        
        if (startTimeStr.isNotBlank() && durationStr.isNotBlank()) {
            val startTime = timeStrToMillis(startTimeStr)
            val duration = timeStrToMillis(durationStr)
            val endTime = startTime + duration
                        
            val type = mapStructureType(label)
            val structure = SongStructure(
                label = label.ifBlank { "段落 ${index + 1}" },
                startTime = startTime,
                endTime = endTime,
                type = type
            )
            structures.add(structure)
                        
            Timber.d("[SongStructure] ✅ Parsed structure: $label ($type) ${formatTime(startTime)} - ${formatTime(endTime)}")
        } else {
            Timber.w("[SongStructure] ⚠️ Missing start-time or duration for structure at index=$index")
        }
    }
        
    /**
     * 解析 amll:meta 中的歌曲结构（备用方案）
     */
    private fun parseAmllMetaStructures(doc: Document, structures: MutableList<SongStructure>) {
        val head = doc.getElementsByTagName("head").item(0) as? Element ?: run {
            Timber.d("[SongStructure] No <head> for amll:meta parsing")
            return
        }
        val amllMetaElements = head.getElementsByTagName("amll:meta")
        Timber.d("[SongStructure] Found ${amllMetaElements.length} amll:meta elements for fallback parsing")
        
        if (amllMetaElements.length > 0) {
            Timber.d("[SongStructure] Checking amll:meta elements for song-structure data")
            for (i in 0 until amllMetaElements.length) {
                val element = amllMetaElements.item(i) as? Element ?: continue
                val name = element.getAttribute("name")
                val content = element.getAttribute("content")
                
                Timber.d("[SongStructure] amll:meta[$i]: name='$name', content blank=${content.isBlank()}")
                            
                if (name == "song-structure" && content.isNotBlank()) {
                    try {
                        val jsonStructures = parseJsonStructures(content)
                        structures.addAll(jsonStructures)
                        Timber.d("[SongStructure] ✅ Parsed ${jsonStructures.size} structures from amll:meta")
                    } catch (e: Exception) {
                        Timber.e("[SongStructure] Failed to parse JSON structures", e)
                    }
                } else {
                    Timber.d("[SongStructure] Skipping amll:meta: name='$name', content blank=${content.isBlank()}")
                }
            }
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
            lowerLabel.contains("intro") -> SongStructureType.INTRO
            lowerLabel.contains("interlude") -> SongStructureType.INTERLUDE
            lowerLabel.contains("outro") -> SongStructureType.OUTRO
            lowerLabel.contains("solo") -> SongStructureType.SOLO
            lowerLabel.contains("break") -> SongStructureType.BREAK
            else -> SongStructureType.UNKNOWN
        }
    }
        
    /**
     * 解析 JSON 格式的歌曲结构
     */
    private fun parseJsonStructures(jsonContent: String): List<SongStructure> {
        // 简单的 JSON 解析，格式：[{"label":"Verse","start":0,"end":30000,"type":"verse"},...]
        val structures = mutableListOf<SongStructure>()
        try {
            val json = org.json.JSONArray(jsonContent)
            for (i in 0 until json.length()) {
                val obj = json.getJSONObject(i)
                val label = obj.optString("label", "段落 ${i + 1}")
                val start = obj.optLong("start", 0)
                val end = obj.optLong("end", 0)
                val typeStr = obj.optString("type", "unknown")
                    
                val type = when (typeStr.lowercase()) {
                    "verse" -> SongStructureType.VERSE
                    "chorus" -> SongStructureType.CHORUS
                    "bridge" -> SongStructureType.BRIDGE
                    "pre-chorus" -> SongStructureType.PRE_CHORUS
                    "intro" -> SongStructureType.INTRO
                    "interlude" -> SongStructureType.INTERLUDE
                    "outro" -> SongStructureType.OUTRO
                    "solo" -> SongStructureType.SOLO
                    "break" -> SongStructureType.BREAK
                    else -> SongStructureType.UNKNOWN
                }
                    
                structures.add(
                    SongStructure(
                        label = label,
                        startTime = start,
                        endTime = end,
                        type = type
                    )
                )
            }
        } catch (e: Exception) {
            Timber.e("[SongStructure] Failed to parse JSON", e)
        }
        return structures
    }
        
    /**
     * 格式化时间为 mm:ss 格式（用于日志）
     */
    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
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
            Timber.e("[TTMLParser] Failed to parse time string: $timeStr", e)
            0L
        }
    }

    /**
     * Hacky pre‑parser step to strip dangerous <amll:meta> elements which often
     * contain unescaped characters or malformed attributes. Lyrics extraction
     * does not rely on them, and removing them resolves XML exceptions.
     */
    private fun sanitizeTTMLContent(raw: String): String {
        return raw.replace(
            Regex("<amll:meta\\b[^>]*?(?:\\/>|>.*?<\\/amll:meta>)", RegexOption.DOT_MATCHES_ALL),
            ""
        )
    }
}
