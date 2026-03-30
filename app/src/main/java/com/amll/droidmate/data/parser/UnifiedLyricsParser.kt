package com.amll.droidmate.data.parser

import com.amll.droidmate.domain.model.LyricLine
import com.amll.droidmate.domain.model.TTMLLyrics
import com.amll.droidmate.domain.model.TTMLMetadata
import com.amll.droidmate.domain.model.SongStructure
import timber.log.Timber

/**
 * 统一歌词解析器
 * 
 * 根据内容自动检测格式并使用相应的解析器
 * 支持多种格式：LRC, Enhanced LRC, QRC, KRC, YRC
 * 
 * 参考: https://github.com/apoint123/Unilyric/tree/main/lyrics_helper_rs
 */
object UnifiedLyricsParser {

    private fun summarizeBgLines(lines: List<LyricLine>): String {
        val bgLines = lines.filter { it.isBG }
        val withTranslation = bgLines.count { !it.translation.isNullOrBlank() }
        val withRoman = bgLines.count { !it.transliteration.isNullOrBlank() }
        val sample = bgLines.firstOrNull()
        val sampleText = sample?.text ?: ""
        val sampleTranslation = sample?.translation ?: ""
        return "bg=${bgLines.size}, bgWithTrans=$withTranslation, bgWithRoman=$withRoman, sampleBg='${sampleText.take(40)}', sampleTrans='${sampleTranslation.take(40)}'"
    }

    // 移除 callerTrace() 函数，因为它是调试专用且不符合日志规范
    
    /**
     * 解析歌词内容为 TTMLLyrics 对象
     * 
     * @param content 歌词内容
     * @param title 歌曲标题（可选）
     * @param artist 艺术家（可选）
     * @param album 专辑（可选）
     * @return TTMLLyrics 对象，如果解析失败则返回 null
     */
    fun parse(
        content: String,
        title: String = "Unknown",
        artist: String = "Unknown",
        album: String? = null,
        processMetadata: Boolean = true
    ): TTMLLyrics? {
        if (content.isBlank()) {
            Timber.w("[UnifiedLyricsParser] Empty lyrics content")
            return null
        }

        // Some lyric payloads (especially QQ Music) may include a leading BOM (U+FEFF), which can
        // interfere with format detection regexes. Normalize the input by trimming whitespace and
        // stripping a leading BOM before further processing.
        val normalizedContent = content.trim().trimStart('\uFEFF')
        if (normalizedContent != content) {
            Timber.d("[UnifiedLyricsParser] Normalized lyrics content by stripping leading BOM/whitespace")
        }

        Timber.d("[UnifiedLyricsParser] Lyrics content preview (first 300 chars): ${normalizedContent.take(300)}")
        
        return try {
            // 检测格式（使用归一化内容来避免 BOM 等前缀影响检测）
            val format = LyricsFormat.detect(normalizedContent)
            Timber.d("[UnifiedLyricsParser] Detected lyrics format: $format")
            
            // 使用相应的解析器解析
            val lines = when (format) {
                LyricsFormat.QRC -> {
                    val parsed = QrcParser.parse(normalizedContent)
                    val firstLineWords = parsed.firstOrNull()?.words?.size ?: 0
                    Timber.d("[UnifiedLyricsParser] QRC parsed ${parsed.size} lines, first line word count=$firstLineWords")
                    parsed
                }
                LyricsFormat.KRC -> {
                    val parsed = KrcParser.parse(normalizedContent)
                    Timber.d("[UnifiedLyricsParser] KRC parsed ${parsed.size} lines, first line words: ${parsed.firstOrNull()?.words?.size ?: 0}")
                    parsed
                }
                LyricsFormat.YRC -> {
                    val parsed = YrcParser.parse(normalizedContent)
                    Timber.d("[UnifiedLyricsParser] YRC parsed ${parsed.size} lines")
                    if (parsed.isEmpty()) {
                        Timber.i("[UnifiedLyricsParser] YRC parsing returned no lines, falling back to LRC parser")
                        val lrcFallback = LrcParser.parse(normalizedContent)
                        Timber.d("[UnifiedLyricsParser] LRC fallback parsed ${lrcFallback.size} lines")
                        lrcFallback
                    } else {
                        parsed
                    }
                }
                LyricsFormat.ENHANCED_LRC -> {
                    val parsed = EnhancedLrcParser.parse(normalizedContent)
                    Timber.d("[UnifiedLyricsParser] Enhanced LRC parsed ${parsed.size} lines")
                    parsed
                }
                LyricsFormat.LRC -> {
                    val parsed = LrcParser.parse(normalizedContent)
                    Timber.d("[UnifiedLyricsParser] LRC parsed ${parsed.size} lines")
                    parsed
                }
                LyricsFormat.TTML -> {
                    // TTML 格式使用专用解析器
                    Timber.d("[UnifiedLyricsParser] Parsing TTML format")
                    
                    // 诊断输入内容是否包含歌曲结构标签
                    val hasItunesSongPart = normalizedContent.contains("itunes:songPart") || normalizedContent.contains("itunes:song-part")
                    val hasItunesLabel = normalizedContent.contains("itunes:label")
                    val hasAmllMeta = normalizedContent.contains("amll:meta")
                    val hasBodyDiv = normalizedContent.contains("<body") && normalizedContent.contains("<div")
                    
                    Timber.d("[SongStructure] TTML input diagnosis: hasItunesSongPart=$hasItunesSongPart, hasItunesLabel=$hasItunesLabel, hasAmllMeta=$hasAmllMeta, hasBodyDiv=$hasBodyDiv")
                    Timber.d("[UnifiedLyricsParser] TTML input contains x-bg=${normalizedContent.contains("ttm:role=\"x-bg\"")}, x-translation=${normalizedContent.contains("ttm:role=\"x-translation\"")}, length=${normalizedContent.length}")
                    
                    val ttmlLyrics = TTMLParser.parse(normalizedContent)
                    
                    // 诊断解析结果
                    val songStructures = ttmlLyrics.metadata.songStructures
                    if (!songStructures.isNullOrEmpty()) {
                        Timber.d("[SongStructure] Parsed ${songStructures.size} structures from TTML metadata")
                        songStructures.forEachIndexed { index, structure ->
                            Timber.d("[SongStructure] [$index] ${structure.label} (${structure.type.displayName}): ${structure.startTime}ms - ${structure.endTime}ms")
                        }
                    } else {
                        Timber.i("[SongStructure] No song structures found in TTML metadata")
                        Timber.d("[SongStructure] Input preview: ${normalizedContent.take(500)}...")
                    }
                    
                    Timber.d("[UnifiedLyricsParser] TTML parsed summary: ${summarizeBgLines(ttmlLyrics.lines)}")
                    
                    // TTML 格式的歌曲结构已经在 TTMLParser 中解析完成，直接返回完整的 TTMLLyrics 对象
                    // 不需要再走下面的统一处理流程
                    return if (processMetadata) {
                        // 如果开启元数据处理，检查是否有歌曲结构
                        val structures = ttmlLyrics.metadata.songStructures
                        if (!structures.isNullOrEmpty()) {
                            Timber.d("[SongStructure] Parsed ${structures.size} structures from TTML metadata")
                        } else {
                            Timber.i("[SongStructure] No structures found with processMetadata=true, fallback will be triggered")
                        }
                        // 直接使用 TTMLParser 返回的完整对象（包含元数据和歌曲结构）
                        ttmlLyrics
                    } else {
                        // 如果关闭元数据处理，返回不带结构的对象
                        ttmlLyrics.copy(
                            metadata = ttmlLyrics.metadata.copy(songStructures = null)
                        )
                    }
                }
                LyricsFormat.PLAIN_TEXT -> {
                    // 纯文本格式转换为简单行
                    val parsed = parsePlainText(normalizedContent)
                    Timber.d("[UnifiedLyricsParser] Plain text parsed ${parsed.size} lines")
                    parsed
                }
            }
            
            if (lines.isEmpty()) {
                Timber.e("[UnifiedLyricsParser] No lyrics lines parsed")
                return null
            }
            
            // 非 TTML 格式才需要下面的统一处理流程
            // 抛弃可能前/后端的元数据行（例如：词：..., 作曲：...）
            val cleanedLines = if (processMetadata) {
                MetadataStripper.stripMetadataLines(lines)
            } else {
                lines
            }

            // 识别演唱者标记（A: XX），用于填充 agent/isDuet 信息
            val annotatedLines = if (processMetadata) {
                AgentRecognizer.recognizeAgents(cleanedLines)
            } else {
                cleanedLines
            }

            // 构建 TTMLLyrics 对象
            val sortedLines = annotatedLines.sortedBy { it.startTime }
            val duration = sortedLines.lastOrNull()?.endTime ?: 0L
            Timber.d("[UnifiedLyricsParser] Final sorted summary: total=${sortedLines.size}, ${summarizeBgLines(sortedLines)}")
            
            // 如果开启了元数据处理，解析歌曲结构
            val songStructures = if (processMetadata) {
                SongStructureParser.parseStructure(sortedLines)
            } else {
                emptyList()
            }

            TTMLLyrics(
                metadata = TTMLMetadata(
                    title = title,
                    artist = artist,
                    album = album,
                    language = detectLanguage(content),
                    duration = duration,
                    source = "DroidMate (${format.displayName})",
                    songStructures = songStructures
                ),
                lines = sortedLines
            )
        } catch (e: Exception) {
            Timber.e("[UnifiedLyricsParser] Failed to parse lyrics", e)
            null
        }
    }
    
    /**
     * 解析纯文本内容
     */
    private fun parsePlainText(content: String): List<LyricLine> {
        val lines = content.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
        
        if (lines.isEmpty()) return emptyList()
        
        return lines.mapIndexed { index, text ->
            val startTime = index * 2000L
            LyricLine(
                startTime = startTime,
                endTime = startTime + 2000L,
                text = text,
                words = emptyList()
            )
        }
    }
    
    /**
     * 检测歌词语言
     */
    private fun detectLanguage(content: String): String {
        val hasChinese = content.any { it.code in 0x4E00..0x9FFF }
        val hasJapanese = content.any { 
            it.code in 0x3040..0x309F ||  // 平假名
            it.code in 0x30A0..0x30FF      // 片假名
        }
        val hasKorean = content.any { it.code in 0xAC00..0xD7AF }
        
        return when {
            hasJapanese -> "ja"
            hasKorean -> "ko"
            hasChinese -> "zh"
            else -> "en"
        }
    }
    
    /**
     * 解析指定格式的歌词
     * 
     * @param content 歌词内容
     * @param format 指定的格式
     * @return 歌词行列表
     */
    fun parseWithFormat(content: String, format: LyricsFormat): List<LyricLine> {
        return when (format) {
            LyricsFormat.QRC -> QrcParser.parse(content)
            LyricsFormat.KRC -> KrcParser.parse(content)
            LyricsFormat.YRC -> YrcParser.parse(content)
            LyricsFormat.ENHANCED_LRC -> EnhancedLrcParser.parse(content)
            LyricsFormat.LRC -> LrcParser.parse(content)
            LyricsFormat.PLAIN_TEXT -> parsePlainText(content)
            LyricsFormat.TTML -> TTMLParser.parse(content).lines
        }
    }
}
