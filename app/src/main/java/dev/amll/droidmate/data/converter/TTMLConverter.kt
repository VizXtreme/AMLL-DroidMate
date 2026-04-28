package com.amll.droidmate.data.converter

import dev.amll.droidmate.global.LyricLine
import dev.amll.droidmate.global.TTMLLyrics
import dev.amll.droidmate.global.TTMLMetadata
import dev.amll.droidmate.global.SongStructure
import com.amll.droidmate.data.parser.TimestampUtils
import timber.log.Timber
import org.json.JSONArray
import org.json.JSONObject

/**
 * TTML 转换器 - 将歌词转换为 TTML 格式
 * 
 * 这个对象提供了一系列方法，用于在内部歌词模型和 TTML (Timed Text Markup Language)
 * 标准格式之间进行转换。TTML 是一种基于 XML 的字幕格式，广泛用于视频和音乐行业。
 * 
 * 主要功能：
 * - 导出：将应用内部的歌词数据转换为标准 TTML 格式
 * - 导入：解析外部 TTML 文件为内部数据结构
 * - XML 转义：防止特殊字符破坏 XML 结构
 * - 元数据保留：保持歌曲信息、结构标记等
 * 
 * 注意：本转换器的所有方法均通过反射调用，因此需要保留 @Suppress("unused")
 * 这些方法在运行时被动态调用，用于歌词格式的导入/导出功能
 */
@Suppress("unused")
object TTMLConverter {

    /**
     * 转义 XML 特殊字符
     * 
     * XML 中有 5 个特殊字符必须进行转义，否则会破坏文档结构：
     * - & (和号) → &amp;  ⭐ 必须最先转义，否则会导致双重转义
     * - < (小于号) → &lt;
     * - > (大于号) → &gt;
     * - " (双引号) → &quot;
     * - ' (单引号) → &apos;
     * 
     * 例如歌词中的 "A&B" 如果不转义，会被 XML 解析器误认为是实体引用。
     * 
     * @param text 原始文本
     * @return 转义后的安全文本
     */
    private fun escapeXml(text: String): String {
        return text
            .replace("&", "&amp;")   // 必须第一个转义，避免双重转义
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    /**
     * 将歌词行列表转换为 TTML 字符串
     * 
     * 这是 TTML 导出的核心方法。它会将内部的歌词数据结构转换为标准的 TTML XML 格式。
     * 
     * 生成的 TTML 包含：
     * - XML 声明和命名空间定义
     * - 元数据区域（标题、艺术家等）
     * - 歌词主体区域（包含时间戳的逐行歌词）
     * - 可选的格式化（缩进和换行，便于阅读）
     * 
     * ⭐ 修复关键：所有字符串值必须进行 XML 转义，防止解析失败
     * 
     * @param lyrics 要转换的歌词对象
     * @param formatted 是否格式化输出（添加缩进和换行）
     * @return TTML 格式的 XML 字符串
     */
    fun toTTMLString(
        lyrics: TTMLLyrics,
        formatted: Boolean = false
    ): String {
        val sb = StringBuilder()
        
        // XML 头
        sb.append("""<?xml version="1.0" encoding="UTF-8"?>""")
        if (formatted) sb.append("\n")
        
        // TTML 核心
        val indent = if (formatted) "  " else ""
        val lineBreak = if (formatted) "\n" else ""
        
        sb.append("""<tt xmlns="http://www.w3.org/ns/ttml"""")
        sb.append(""" xmlns:ttm="http://www.w3.org/ns/ttml#metadata"""")
        sb.append(""" xmlns:itunes="http://music.apple.com/lyric-ttml-internal"""")
        sb.append(""" xmlns:amll="http://www.example.com/ns/amll"""")
        sb.append(""" xml:lang="ja"""")
        sb.append(""" itunes:timing="Word">$lineBreak""")
        
        // Head
        sb.append("${indent}<head>$lineBreak")
        
        // ✅ 如果有原始 XML metadata，直接使用它（保留所有未使用的 XML 信息）
        val rawXmlMetadata = lyrics.metadata.rawXmlMetadata
        if (!rawXmlMetadata.isNullOrBlank()) {
            Timber.d("[TTMLConverter] Using preserved raw XML metadata for serialization")
            sb.append("${indent}${indent}$rawXmlMetadata$lineBreak")
        } else {
            // 否则，重新构建 metadata（向后兼容）
            sb.append("${indent}${indent}<metadata>")
            if (formatted) sb.append("\n")
            
            // Metadata
            with(lyrics.metadata) {
                // ✅ 添加主唱 agent 定义（必须放在其他 meta 之前，让 Rust 解析器正确识别）
                sb.append("""${indent}${indent}${indent}<ttm:agent type="person" xml:id="v1" />""")
                if (formatted) sb.append("\n")
                
                // 检查是否有对唱歌词，如果有则添加 v2 agent 定义
                val hasDuet = lyrics.lines.any { it.isDuet }
                if (hasDuet) {
                    sb.append("""${indent}${indent}${indent}<ttm:agent type="other" xml:id="v2" />""")
                    if (formatted) sb.append("\n")
                }
                
                sb.append("""${indent}${indent}${indent}<amll:meta key="title" value="${escapeXml(title)}" />""")
                if (formatted) sb.append("\n")
                sb.append("""${indent}${indent}${indent}<amll:meta key="artist" value="${escapeXml(artist)}" />""")
                if (formatted) sb.append("\n")
                album?.let {
                    sb.append("""${indent}${indent}${indent}<amll:meta key="album" value="${escapeXml(album)}" />""")
                    if (formatted) sb.append("\n")
                }
                sb.append("""${indent}${indent}${indent}<amll:meta key="language" value="${escapeXml(language)}" />""")
                if (formatted) sb.append("\n")
                sb.append("""${indent}${indent}${indent}<amll:meta key="source" value="${escapeXml(source)}" />""")
                if (formatted) sb.append("\n")
            }
            
            sb.append("""${indent}${indent}</metadata>$lineBreak""")
        }
        
        sb.append("""${indent}</head>$lineBreak""")
        
        // Body
        val duration = TimestampUtils.fromMillis(lyrics.lines.lastOrNull()?.endTime ?: 0L)
        sb.append("""${indent}<body dur="$duration">$lineBreak""")
                
        // ✅ 如果有歌曲结构信息，为每个结构创建独立的 <div> 标签
        val structures = lyrics.metadata.songStructures
        if (!structures.isNullOrEmpty()) {
            // 按歌曲结构分组歌词行
            var lineIndex = 0
            structures.forEachIndexed { structIndex, structure ->
                val startTimeAttr = TimestampUtils.fromMillis(structure.startTime)
                val endTimeAttr = TimestampUtils.fromMillis(structure.endTime)
                        
                // ✅ 优先使用 structure.label（保留"段落 1"等 fallback 标签），其次使用 type.displayName
                val songPartValue = if (structure.label.startsWith("段落")) {
                    structure.label  // ✅ 保留 fallback 的"段落 X"标签
                } else {
                    structure.type.displayName  // 使用标准的类型名称（Verse、Chorus 等）
                }
                sb.append("""${indent}${indent}<div itunes:songPart="${escapeXml(songPartValue)}" begin="$startTimeAttr" end="$endTimeAttr">$lineBreak""")
                        
                // 添加该结构包含的歌词行
                while (lineIndex < lyrics.lines.size) {
                    val line = lyrics.lines[lineIndex]
                    // 如果当前行的开始时间超过结构结束时间，跳出
                    if (line.startTime > structure.endTime && structIndex < structures.size - 1) break
                            
                    appendLyricLine(sb, line, lineIndex, indent, formatted)
                    lineIndex++
                }
                        
                sb.append("""${indent}${indent}</div>$lineBreak""")
            }
                    
            // 处理剩余的歌词行（没有结构信息的部分）
            if (lineIndex < lyrics.lines.size) {
                sb.append("""${indent}${indent}<div>$lineBreak""")
                for (i in lineIndex until lyrics.lines.size) {
                    appendLyricLine(sb, lyrics.lines[i], i, indent, formatted)
                }
                sb.append("""${indent}${indent}</div>$lineBreak""")
            }
        } else {
            // 没有结构信息，使用单个 div 包含所有歌词
            sb.append("""${indent}${indent}<div>$lineBreak""")
            lyrics.lines.forEachIndexed { index, line ->
                appendLyricLine(sb, line, index, indent, formatted)
            }
            sb.append("""${indent}${indent}</div>$lineBreak""")
        }
        
        // 关闭 body 和 tt 标签
        sb.append("""${indent}</body>$lineBreak""")
        sb.append("</tt>")
        
        return sb.toString()
    }
    
    /**
     * 辅助方法：追加歌词行到 StringBuilder
     */
    private fun appendLyricLine(
        sb: StringBuilder,
        line: LyricLine,
        lineIndex: Int,
        indent: String,
        formatted: Boolean
    ) {
        val begin = TimestampUtils.fromMillis(line.startTime)
        val end = TimestampUtils.fromMillis(line.endTime)
        val lineNum = "L${lineIndex + 1}"
                
        // ✅ 优先使用 agent 字段，如果没有则根据 isDuet 推断
        val agentValue = line.agent ?: if (line.isDuet) "v2" else "v1"
        val agentAttr = if (agentValue.isNotEmpty()) " ttm:agent=\"$agentValue\"" else ""
                
        sb.append("""${indent}${indent}${indent}<p begin="$begin" end="$end" itunes:key="$lineNum"$agentAttr>""")
        if (formatted) sb.append("\n")

        val lineContentIndent = "${indent}${indent}${indent}"
        val lineWrapPrefix = if (line.isBG) "<span ttm:role=\"x-bg\">" else ""
        val lineWrapSuffix = if (line.isBG) "</span>" else ""

        if (line.isBG) {
            sb.append("""$lineContentIndent$lineWrapPrefix""")
            if (formatted) sb.append("\n")
        }

        val spanIndent = if (line.isBG) {
            "${indent}${indent}${indent}${indent}"
        } else {
            lineContentIndent
        }
            
        // Main lyrics - 如果有 words 数组则逐词输出，否则整行输出
        if (line.words.isNotEmpty()) {
            // 警示后人：<p>/<span> 内空格是可见歌词语义，不能对词文本做 trim。
            // 这里最多仅清理换行控制字符，避免导出后把 "a b" 变成 "ab"。
            line.words.forEachIndexed inner@{ wordIndex, word ->
                val wordBegin = TimestampUtils.fromMillis(word.startTime)
                val wordEnd = TimestampUtils.fromMillis(word.endTime)

                val spanText = word.word
                    .replace("\r", "")
                    .replace("\n", "")
                
                // ⭐ 修复关键：对歌词内容进行 XML 转义，防止特殊字符导致解析失败
                val escapedText = escapeXml(spanText)

                if (spanText.isEmpty()) {
                    // 保留空白词节点的最小分隔语义，避免词间被完全粘连。
                    if (!formatted && wordIndex < line.words.lastIndex) {
                        sb.append(" ")
                    }
                    return@inner
                }

                sb.append("""$spanIndent<span begin="$wordBegin" end="$wordEnd">${escapeXml(spanText)}</span>""")

                if (formatted) sb.append("\n")
            }
        } else {
            // 整行输出
            sb.append("""$spanIndent<span begin="$begin" end="$end">${escapeXml(line.text)}</span>""")
            if (formatted) sb.append("\n")
        }

        if (line.isBG) {
            // BG 行的翻译与音译应作为 x-bg 的子节点，避免二次解析时被当作主歌词翻译。
            line.translation?.let {
                sb.append("""$spanIndent<span ttm:role="x-translation" xml:lang="zh-CN">${escapeXml(it)}</span>""")
                if (formatted) sb.append("\n")
            }

            line.transliteration?.let {
                sb.append("""$spanIndent<span ttm:role="x-roman" xml:lang="ja-Latn">${escapeXml(it)}</span>""")
                if (formatted) sb.append("\n")
            }

            sb.append("""$lineContentIndent$lineWrapSuffix""")
            if (formatted) sb.append("\n")
        } else {
            // Translation if available
            line.translation?.let {
                sb.append("""$lineContentIndent<span ttm:role="x-translation" xml:lang="zh-CN">${escapeXml(it)}</span>""")
                if (formatted) sb.append("\n")
            }

            // Transliteration if available
            line.transliteration?.let {
                sb.append("""$lineContentIndent<span ttm:role="x-roman" xml:lang="ja-Latn">${escapeXml(it)}</span>""")
                if (formatted) sb.append("\n")
            }
        }
        
        sb.append("""${indent}${indent}</p>""")
        if (formatted) sb.append("\n")
    }

    /**
     * 将歌词行列表转换为完整的 TTML 对象
     */
    fun fromLyricLines(
        lines: List<LyricLine>,
        title: String = "Unknown",
        artist: String = "Unknown",
        album: String? = null
    ): TTMLLyrics {
        return TTMLLyrics(
            metadata = TTMLMetadata(
                title = title,
                artist = artist,
                album = album,
                language = "ja",
                duration = lines.lastOrNull()?.endTime ?: 0L,
                source = "DroidMate"
            ),
            lines = lines.sortedBy { it.startTime }
        )
    }

    /**
     * 格式化时间为 TTML 格式
     * 格式：mm:ss.msms
     * @deprecated 使用 TimestampUtils.fromMillis() 代替
     */
    @Deprecated("Use TimestampUtils.fromMillis()", ReplaceWith("TimestampUtils.fromMillis(millis, TimestampUtils.Format.MM_SS_MS)"))
    fun formatTime(millis: Long): String {
        // 保留旧实现以确保向后兼容
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val ms = millis % 1000
            
        return String.format("%02d:%02d.%03d", minutes, seconds, ms)
    }

    /**
     * 将时间字符串转换为毫秒
     * 支持：mm:ss.mmm 或 mm:ss
     * @deprecated 使用 TimestampUtils.toMillis() 代替
     */
    @Deprecated("Use TimestampUtils.toMillis()", ReplaceWith("TimestampUtils.toMillis(timeStr)"))
    fun timeToMillis(timeStr: String): Long {
        // 保留旧实现以确保向后兼容
        return try {
            val parts = timeStr.split(":")
            if (parts.size != 2) return 0L
    
            val minutes = parts[0].toLongOrNull() ?: return 0L
            val secParts = parts[1].split(".")
            val seconds = secParts[0].toLongOrNull() ?: return 0L
            val millis = if (secParts.size > 1) {
                secParts[1].padEnd(3, '0').take(3).toLongOrNull() ?: 0L
            } else {
                0L
            }
    
            (minutes * 60 + seconds) * 1000 + millis
        } catch (_: Exception) {
            0L
        }
    }

    /**
     * 从多种格式解析歌词到 TTML（使用 Unilyric 规则）
     * 支持：LRC, Enhanced LRC, QRC, KRC, YRC
     * 
     * @param content 歌词内容
     * @param title 歌曲标题（可选）
     * @param artist 艺术家（可选）
     * @param album 专辑（可选）
     * @param processMetadata 是否处理元数据（默认禁用，防止强行处理导致翻译/音译错位）
     * @return TTMLLyrics 对象，如果解析失败则返回 null
     */
    fun fromLyrics(
        content: String,
        title: String = "Unknown",
        artist: String = "Unknown",
        album: String? = null,
        processMetadata: Boolean = false
    ): TTMLLyrics? {
        return try {
            com.amll.droidmate.data.parser.UnifiedLyricsParser.parse(
                content = content,
                title = title,
                artist = artist,
                album = album,
                processMetadata = processMetadata
            )
        } catch (e: Exception) {
            Timber.e("[TTMLConverter] Error parsing lyrics using Unilyric rules", e)
            null
        }
    }
    
    /**
     * 从 LRC 格式转换到 TTML（保留用于向后兼容）
     * @deprecated 使用 fromLyrics() 代替，它支持更多格式
     */
    @Deprecated(
        message = "Use fromLyrics() instead for better format support",
        replaceWith = ReplaceWith("fromLyrics(lrcContent)")
    )
    fun fromLRC(lrcContent: String): TTMLLyrics? {
        return fromLyrics(lrcContent)
    }
}
