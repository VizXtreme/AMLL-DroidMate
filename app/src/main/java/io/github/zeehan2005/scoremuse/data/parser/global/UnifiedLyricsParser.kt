package io.github.zeehan2005.scoremuse.data.parser.global

import androidx.compose.ui.input.key.Key.Companion.Music
import io.github.zeehan2005.scoremuse.data.parser.EnhancedLrcParser
import io.github.zeehan2005.scoremuse.data.parser.KrcParser
import io.github.zeehan2005.scoremuse.data.parser.LrcParser
import io.github.zeehan2005.scoremuse.data.parser.QrcParser
import io.github.zeehan2005.scoremuse.data.parser.TTMLParser
import io.github.zeehan2005.scoremuse.data.parser.YrcParser
import io.github.zeehan2005.scoremuse.data.parser.global.LyricsFormat.*
import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.UnifiedLyrics
import io.github.zeehan2005.scoremuse.global.LyricsMetadata
import timber.log.Timber

/**
 * 统一歌词解析器
 * 
 * 这是歌词解析的总入口，负责根据输入内容自动检测格式并调用相应的解析器。
 * 支持多种歌词格式的智能识别和解析：
 * - LRC：标准滚动歌词格式
 * - Enhanced LRC：增强型 LRC（包含翻译、音译）
 * - QRC：QQ 音乐逐字歌词格式
 * - KRC：酷狗音乐逐字歌词格式
 * - YRC：网易云音乐逐字歌词格式
 * - TTML：Apple Music 标准字幕格式
 * 
 * 工作流程：
 * 1. 归一化输入内容（移除 BOM、空白字符）
 * 2. 使用正则表达式检测歌词格式
 * 3. 调用对应的专业解析器
 * 4. 整合结果为统一的 UnifiedLyrics 对象
 * 
 * 参考：https://github.com/apoint123/Unilyric/tree/main/lyrics_helper_rs
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

    
    /**
     * 解析歌词内容为 UnifiedLyrics 对象
     * 
     * 这是歌词解析的主入口方法。它会自动：
     * 1. 验证输入内容是否有效
     * 2. 归一化处理（移除 BOM 和首尾空白）
     * 3. 检测歌词格式
     * 4. 调用相应的专业解析器
     * 5. 整合为包含元数据的完整 UnifiedLyrics 对象
     * 
     * @param content 歌词原始内容
     * @param title 歌曲标题（用于元数据）
     * @param artist 艺术家名称（用于元数据）
     * @param album 专辑名称（用于元数据，可选）
     * @param processMetadata 是否处理元数据（默认 true）
     * @return 解析后的 UnifiedLyrics 对象，失败返回 null
     */
    fun parse(
        content: String,
        title: String = "Unknown",
        artist: String = "Unknown",
        album: String? = null,
        processMetadata: Boolean = true
    ): UnifiedLyrics? {
        if (content.isBlank()) {
            Timber.w("[UnifiedLyricsParser] Empty lyrics content")
            return null
        }

        /** Some lyric payloads (especially QQ Music) may include a leading BOM (U+FEFF), which can
          * interfere with format detection regexes. Normalize the input by trimming whitespace and
          * stripping a leading BOM before further processing. */
        val normalizedContent = content.trim().trimStart('\uFEFF')
        if (normalizedContent != content) {
            Timber.d("[UnifiedLyricsParser] Normalized lyrics content by stripping leading BOM/whitespace")
        }

        Timber.d("[UnifiedLyricsParser] Lyrics content preview (first 300 chars): ${normalizedContent.take(300)}")
        
        return try {
            /** 检测格式（使用归一化内容来避免 BOM 等前缀影响检测） */
            val format = LyricsFormat.detect(normalizedContent)
            Timber.d("[UnifiedLyricsParser] Detected lyrics format: $format")
            
            /** 使用相应的解析器解析 */
            val lines = when (format) {
//                LyricsFormat.SCOREMUSE_XML -> {
//                    // ScoreMuse XML 格式使用 XMLConverter 解析
//                    Timber.d("[UnifiedLyricsParser] Parsing ScoreMuse XML format")
//
//                    val xmlLyrics = XMLConverter.fromXMLString(normalizedContent)
//                    if (xmlLyrics != null) {
//                        // 诊断解析结果
//                        val songStructures = xmlLyrics.metadata.songStructures
//                        if (!songStructures.isNullOrEmpty()) {
//                            Timber.d("[SongStructure] Parsed ${songStructures.size} structures from ScoreMuse XML metadata")
//                            songStructures.forEachIndexed { index, structure ->
//                                Timber.d("[SongStructure] [$index] ${structure.label} (${structure.type.displayName}): ${structure.startTime}ms - ${structure.endTime}ms")
//                            }
//                        } else {
//                            Timber.i("[SongStructure] No song structures found in ScoreMuse XML metadata")
//                        }
//
//                        Timber.d("[UnifiedLyricsParser] ScoreMuse XML parsed summary: ${summarizeBgLines(xmlLyrics.lines)}")
//
//                        // ScoreMuse XML 格式的歌曲结构已经在 XMLConverter 中解析完成，直接返回完整的 UnifiedLyrics 对象
//                        // 不需要再走下面的统一处理流程
//                        return if (processMetadata) {
//                            // 如果开启元数据处理，检查是否有歌曲结构
//                            val structures = xmlLyrics.metadata.songStructures
//                            if (!structures.isNullOrEmpty()) {
//                                Timber.d("[SongStructure] Parsed ${structures.size} structures from ScoreMuse XML metadata")
//                            } else {
//                                Timber.i("[SongStructure] No structures found with processMetadata=true, fallback will be triggered")
//                            }
//                            // 直接使用 XMLConverter 返回的完整对象（包含元数据和歌曲结构）
//                            xmlLyrics
//                        } else {
//                            // 如果关闭元数据处理，返回不带结构的对象
//                            xmlLyrics.copy(
//                                metadata = xmlLyrics.metadata.copy(songStructures = null)
//                            )
//                        }
//                    } else {
//                        Timber.e("[UnifiedLyricsParser] Failed to parse ScoreMuse XML")
//                        return null
//                    }
//                }
                QRC -> {
                    val parsed = QrcParser.parse(normalizedContent)
                    val firstLineWords = parsed.firstOrNull()?.words?.size ?: 0
                    Timber.d("[UnifiedLyricsParser] QRC parsed ${parsed.size} lines, first line word count=$firstLineWords")
                    parsed
                }
                KRC -> {
                    val parsed = KrcParser.parse(normalizedContent)
                    Timber.d("[UnifiedLyricsParser] KRC parsed ${parsed.size} lines, first line words: ${parsed.firstOrNull()?.words?.size ?: 0}")
                    parsed
                }
                YRC -> {
                    val parsed = YrcParser.parse(normalizedContent)
                    Timber.d("[UnifiedLyricsParser] YRC parsed ${parsed.size} lines")
                    parsed.ifEmpty {
                        Timber.i("[UnifiedLyricsParser] YRC parsing returned no lines, falling back to LRC parser")
                        val lrcFallback = LrcParser.parse(normalizedContent)
                        Timber.d("[UnifiedLyricsParser] LRC fallback parsed ${lrcFallback.size} lines")
                        lrcFallback
                    }
                }
                ENHANCED_LRC -> {
                    val parsed = EnhancedLrcParser.parse(normalizedContent)
                    Timber.d("[UnifiedLyricsParser] Enhanced LRC parsed ${parsed.size} lines")
                    parsed
                }
                LRC -> {
                    val parsed = LrcParser.parse(normalizedContent)
                    Timber.d("[UnifiedLyricsParser] LRC parsed ${parsed.size} lines")
                    parsed
                }
                SCOREMUSE_XML, TTML -> {
                    /** TTML 和 ScoreMuse XML 格式使用专用解析器 */
                    Timber.d("[UnifiedLyricsParser] Parsing $format format")
                    
                    /** 诊断输入内容是否包含歌曲结构标签 */
                    val hasItunesSongPart = normalizedContent.contains("itunes:songPart") || normalizedContent.contains("itunes:song-part")
                    val hasItunesLabel = normalizedContent.contains("itunes:label")
                    val hasBodyDiv = normalizedContent.contains("<body") && normalizedContent.contains("<div")
                    
                    Timber.d("[SongStructure] $format input diagnosis: hasItunesSongPart=$hasItunesSongPart, hasItunesLabel=$hasItunesLabel,BodyDiv=$hasBodyDiv")
                    Timber.d("[UnifiedLyricsParser] $format input contains x-bg=${normalizedContent.contains("ttm:role=\"x-bg\"")}, x-translation=${normalizedContent.contains("ttm:role=\"x-translation\"")}, length=${normalizedContent.length}")
                    
                    val unifiedLyrics = TTMLParser.parse(normalizedContent)
                    
                    /** 诊断解析结果 */
                    val songStructures = unifiedLyrics.metadata.songStructures
                    if (!songStructures.isNullOrEmpty()) {
                        Timber.d("[SongStructure] Parsed ${songStructures.size} structures from $format metadata")
                        songStructures.forEachIndexed { index, structure ->
                            Timber.d("[SongStructure] [$index] ${structure.label} (${structure.type.displayName}): ${structure.startTime}ms - ${structure.endTime}ms")
                        }
                    } else {
                        Timber.i("[SongStructure] No song structures found in $format metadata")
                        Timber.d("[SongStructure] Input preview: ${normalizedContent.take(500)}...")
                    }
                    
                    Timber.d("[UnifiedLyricsParser] $format parsed summary: ${summarizeBgLines(unifiedLyrics.lines)}")
                    
                    /** TTML 格式的歌曲结构已经在 TTMLParser 中解析完成，直接返回完整的 UnifiedLyrics 对象
                     *  不需要再走下面的统一处理流程 */
                    return if (processMetadata) {
                        /** 如果开启元数据处理，检查是否有歌曲结构 */
                        val structures = unifiedLyrics.metadata.songStructures
                        if (!structures.isNullOrEmpty()) {
                            Timber.d("[SongStructure] Parsed ${structures.size} structures from $format metadata")
                        } else {
                            Timber.i("[SongStructure] No structures found with processMetadata=true, fallback will be triggered")
                        }
                        /** 直接使用 TTMLParser 返回的完整对象（包含元数据和歌曲结构） */
                        unifiedLyrics
                    } else {
                        /** 如果关闭元数据处理，返回不带结构的对象 */
                        unifiedLyrics.copy(
                            metadata = unifiedLyrics.metadata.copy(songStructures = null)
                        )
                    }
                }
                PLAIN_TEXT -> {
                    /** 纯文本格式转换为简单行 */
                    val parsed = parsePlainText(normalizedContent)
                    Timber.d("[UnifiedLyricsParser] Plain text parsed ${parsed.size} lines")
                    parsed
                }
            }
            
            if (lines.isEmpty()) {
                Timber.e("[UnifiedLyricsParser] No lyrics lines parsed")
                return null
            }
            
            // 【已临时禁用】元数据过滤功能
            // TODO: 暂时不过滤元数据行，保留所有歌词行（包括"词：...", "作曲：..."等）
            // val cleanedLines = if (processMetadata) {
            //     MetadataStripper.stripMetadataLines(lines)
            // } else {
            //     lines
            // }
            val cleanedLines = lines  /** 临时禁用：直接使用原始行，不过滤元数据 */

            /** 【已临时禁用】演唱者识别功能
             *  TODO: 暂时不过滤元数据，所以也不进行 Agent 识别 */
            val annotatedLines = cleanedLines  /** 临时禁用：不进行演唱者识别 */

            /** 构建 UnifiedLyrics 对象 */
            val sortedLines = annotatedLines.sortedBy { it.startTime }
            val duration = sortedLines.lastOrNull()?.endTime ?: 0L
            Timber.d("[UnifiedLyricsParser] Final sorted summary: total=${sortedLines.size}, ${summarizeBgLines(sortedLines)}")
            
            /** 解析歌曲结构
             *  修复：之前没有传 songDuration，导致 detectInterludes 中的尾奏检测永远不生效。
             *  这里传入推断出的 duration，让尾奏能被正确识别。 */
            val songStructures = SongStructureParser.parseStructure(sortedLines, songDuration = duration)

            UnifiedLyrics(
                metadata = LyricsMetadata(
                    title = title,
                    artist = artist,
                    album = album,
                    language = detectLanguage(content),
                    duration = duration,
                    source = "ScoreMuse (${format.displayName})",
                    songStructures = songStructures
                ),
                lines = sortedLines,
                rawContent = normalizedContent,
                format = format.name.lowercase()
            )
        } catch (e: Exception) {
                Timber.e("[UnifiedLyricsParser] Failed to parse lyrics $e")
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
            it.code in 0x3040..0x309F ||  /** 平假名 */
            it.code in 0x30A0..0x30FF      /** 片假名 */
        }
        val hasKorean = content.any { it.code in 0xAC00..0xD7AF }
        
        return when {
            hasJapanese -> "ja"
            hasKorean -> "ko"
            hasChinese -> "zh"
            else -> "en"
        }
    }

}