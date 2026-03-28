package com.amll.droidmate.data.parser

import com.amll.droidmate.domain.model.LyricLine
import com.amll.droidmate.domain.model.LyricWord
import timber.log.Timber

object QrcParser {

    private val lyricTokenRegex = Regex("""(?<text>.*?)\((?<start>\d+),(?<duration>\d+)\)""")
    private val qrcLineTimestampRegex = Regex("""^\[(\d+),(\d+)]""")

    fun parse(content: String): List<LyricLine> {
        val rawContent = extractQrcFromXmlIfNeeded(content)

        Timber.d("QRC raw content length=${rawContent.length}, lineCount=${rawContent.lines().size}, containsNewline=${rawContent.contains('\n')}")
        rawContent.lines().take(10).forEachIndexed { index, line ->
            Timber.d("QRC raw line $index: ${line.take(200)}")
        }

        val finalLines = mutableListOf<LyricLine>()
        val metadata = mutableMapOf<String, MutableList<String>>()

        for (raw in rawContent.lines()) {
            val line = raw.trim()
            if (line.isEmpty()) continue
            // 使用统一的元数据检测函数
            if (MetadataStripper.isMetadataLine(line)) continue

            parseSingleLine(line)?.let { finalLines.add(it) }
        }

        // Debug output to help diagnose why QRC may fall back to line-by-line (no words)
        val totalWords = finalLines.sumOf { it.words.size }
        Timber.d("QRC parser output: ${finalLines.size} lines, $totalWords words (avg=${if (finalLines.isNotEmpty()) totalWords.toDouble() / finalLines.size else 0.0})")

        return finalLines
    }

    private fun parseSingleLine(line: String): LyricLine? {
        // QRC line header: [lineStart,lineDuration] (ms)
        val lineStartMs = qrcLineTimestampRegex.find(line)?.groups?.get(1)?.value?.toLongOrNull()
        val lineDurationMs = qrcLineTimestampRegex.find(line)?.groups?.get(2)?.value?.toLongOrNull()

        val lineContent = qrcLineTimestampRegex.replace(line, "")
        val words = mutableListOf<LyricWord>()

        for (capture in lyricTokenRegex.findAll(lineContent)) {
            val rawText = capture.groups["text"]?.value.orEmpty()
            val processed = processSyllableText(rawText, words) ?: continue
            val (cleanText, endsWithSpace) = processed

            val startMs = capture.groups["start"]?.value?.toLongOrNull() ?: continue
            val durationMs = capture.groups["duration"]?.value?.toLongOrNull() ?: continue

            val text = if (endsWithSpace) "$cleanText " else cleanText
            words.add(
                LyricWord(
                    word = text,
                    startTime = startMs,
                    endTime = startMs + durationMs
                )
            )
        }

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

        if (words.isEmpty()) return null

        val lineStart = lineStartMs ?: words.first().startTime
        val lineEnd = lineStart + (lineDurationMs ?: (words.last().endTime - lineStart))

        return LyricLine(
            startTime = lineStart,
            endTime = lineEnd,
            text = words.joinToString(separator = "") { it.word }.trimEnd(),
            words = words
        )
    }

    private fun extractQrcFromXmlIfNeeded(content: String): String {
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
        val tagStartRegex = Regex("""<Lyric_\d+\b[^>]*\bLyricContent=(['"])""", RegexOption.IGNORE_CASE)
        var searchIndex = 0

        while (true) {
            val match = tagStartRegex.find(content, startIndex = searchIndex) ?: break
            val quoteChar = match.groupValues[1].single()
            val valueStart = match.range.last + 1

            // Find the end of the current tag so we can pick the outermost closing quote inside it.
            val tagEnd = content.indexOf('>', startIndex = valueStart).takeIf { it >= 0 } ?: content.length
            val lastQuoteBeforeTagEnd = content.lastIndexOf(quoteChar, startIndex = tagEnd - 1)
            val valueEnd = if (lastQuoteBeforeTagEnd >= valueStart) {
                lastQuoteBeforeTagEnd
            } else {
                // Fallback: find the first quote after the start
                content.indexOf(quoteChar, startIndex = valueStart).takeIf { it >= 0 } ?: content.length
            }

            val rawValue = content.substring(valueStart, valueEnd)
            extracted.add(unescapeXmlAttribute(rawValue))

            searchIndex = valueEnd + 1
        }

        if (extracted.isNotEmpty()) {
            Timber.d("Extracted ${extracted.size} LyricContent entries from QRC XML (regex)")
            return extracted.joinToString(separator = "\n")
        }

        // Fallback to DOM parsing if regex failed (very rare)
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
                Timber.d("Detected QRC XML but no LyricContent attributes found; falling back to raw content")
                return content
            }

            Timber.d("Extracted ${lyricContents.size} LyricContent entries from QRC XML (DOM)")
            lyricContents.joinToString(separator = "\n")
        } catch (e: Exception) {
            Timber.w(e, "Failed to parse QRC XML content; falling back to raw content")
            content
        }
    }

    private fun unescapeXmlAttribute(value: String): String {
        return value
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replace(Regex("&#(\\d+);")) { match ->
                match.groups[1]?.value?.toIntOrNull()?.toChar()?.toString() ?: match.value
            }
            .replace(Regex("&#x([0-9A-Fa-f]+);")) { match ->
                match.groups[1]?.value?.toIntOrNull(16)?.toChar()?.toString() ?: match.value
            }
    }
}
