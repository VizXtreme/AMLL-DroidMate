package io.github.zeehan2005.scoremuse.data.parser.global

import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.SongStructure
import io.github.zeehan2005.scoremuse.global.SongStructureType
import org.w3c.dom.Document
import timber.log.Timber

/**
 * 歌曲结构解析器
 * 
 * 这个工具用于识别和标记歌曲的各个段落结构，例如：
 * - 前奏 (Intro)
 * - 主歌 (Verse)
 * - 预副歌 (Pre-Chorus)
 * - 副歌 (Chorus)
 * - 间奏 (Interlude)
 * - 桥段 (Bridge)
 * - 尾奏 (Outro)
 * 
 * 工作原理：
 * 1. 优先使用 TTML 元数据中提供的结构信息（最准确）
 * 2. 如果没有元数据，则基于歌词时间间隔自动推断：
 *    - 长时间空白 → 间奏/前奏/尾奏
 *    - 第一段歌词 → 主歌
 *    - 重复出现的相似段落 → 副歌
 */
object SongStructureParser {
    
    // 间奏检测阈值（4 秒）
    // 当歌词行之间的时间间隔大于等于此值时，认为是间奏/前奏/尾奏
    private const val INTERLUDE_THRESHOLD_MS = 4000L
    
    /**
     * 从 TTML 歌词解析歌曲结构
     * 
     * 这是歌曲结构解析的主入口方法。它采用两级策略：
     * 1. 优先使用 TTML 元数据中的结构信息（如果存在）
     * 2. 如果没有元数据，则从歌词行自动推断结构
     * 
     * @param lyricsLines 歌词行列表
     * @param metadataStructure 从 TTML 元数据中解析的结构信息（如果有）
     * @param songDuration 歌曲总时长（毫秒），用于检测尾奏
     * @return 歌曲结构列表（按时间顺序排列）
     */
    fun parseStructure(
        lyricsLines: List<LyricLine>,
        metadataStructure: List<SongStructure>? = null,
        songDuration: Long = 0L
    ): List<SongStructure> {
        // 如果提供了元数据结构，优先使用
        if (!metadataStructure.isNullOrEmpty()) {
            return metadataStructure
        }

        // 否则，从歌词行自动推断结构
        Timber.v("[SongStructure] Fallback triggered: no metadata structures")
        return inferStructureFromLyrics(lyricsLines, songDuration)
    }

    /**
     * 从歌词行自动推断歌曲结构
     * 
     * Fallback 逻辑：
     * 1. 检测歌词行之间的时间间隔
     * 2. 间隔 >= 4 秒的标记为间奏/前奏/尾奏
     * 3. 将间隔前后的歌词标记为独立段落
     * 4. 区分纯音乐 (inst) 和有歌词 (para) 的前奏/尾奏
     */
    private fun inferStructureFromLyrics(lines: List<LyricLine>, songDuration: Long = 0L): List<SongStructure> {
        if (lines.isEmpty()) {
            Timber.w("[SongStructure] 歌词行为空，返回空结构")
            return emptyList()
        }

        val structures = mutableListOf<SongStructure>()
        val interludes = detectInterludes(lines, songDuration)

        if (interludes.isEmpty()) {
            // 没有检测到间奏，将所有歌词作为一个段落
            structures.add(
                SongStructure(
                    label = "段落 1",
                    startTime = lines.first().startTime,
                    endTime = lines.last().endTime,
                    type = SongStructureType.UNKNOWN  // ✅ 使用 UNKNOWN 避免强行覆盖成 Verse
                )
            )
        } else {
            // 有间奏，需要将歌词分割成多个段落
            var paragraphIndex = 1
            var lastEndTime: Long = 0

            for (interlude in interludes) {
                // 添加间奏之前的歌词段落
                if (interlude.startTime > lastEndTime) {
                    val paragraphLines = lines.filter {
                        it.startTime >= lastEndTime && it.endTime <= interlude.startTime
                    }

                    if (paragraphLines.isNotEmpty()) {
                        structures.add(
                            SongStructure(
                                label = "段落 $paragraphIndex",
                                startTime = paragraphLines.first().startTime,
                                endTime = paragraphLines.last().endTime,
                                type = SongStructureType.UNKNOWN  // ✅ 使用 UNKNOWN 避免强行覆盖成 Verse
                            )
                        )
                        paragraphIndex++
                    }
                }

                // 添加间奏段落
                structures.add(interlude)
                lastEndTime = interlude.endTime
            }

            // 添加最后一个间奏之后的歌词段落（如果有）
            val remainingLines = lines.filter { it.startTime >= lastEndTime }
            if (remainingLines.isNotEmpty()) {
                structures.add(
                    SongStructure(
                        label = "段落 $paragraphIndex",
                        startTime = remainingLines.first().startTime,
                        endTime = remainingLines.last().endTime,
                        type = SongStructureType.UNKNOWN  // ✅ 使用 UNKNOWN 避免强行覆盖成 Verse
                    )
                )
            }
        }

        return structures
    }
    
    /**
     * 检测歌词行之间的间奏/前奏/尾奏
     * 
     * 检测规则：
     * - 所有间隔（包括前奏、间奏、尾奏）都需要 >= 4 秒才显示
     * - 区分纯音乐 (inst) 和有歌词 (para) 的前奏/尾奏：
     *   - intro_inst/outro_inst: 前后都没有歌词（纯音乐间隔）
     *   - intro_para/outro_para: 前后有歌词（有歌词的引子/尾声）
     */
    private fun detectInterludes(lyricLines: List<LyricLine>, songDuration: Long = 0L): List<SongStructure> {
        val interludes = mutableListOf<SongStructure>()

        if (lyricLines.isEmpty()) {
            return interludes
        }

        // 检测前奏：从歌曲开始到第一句歌词
        val firstLine = lyricLines.first()
        if (firstLine.startTime >= INTERLUDE_THRESHOLD_MS) {
            // 判断是前奏还是引子：第一段歌词之前有时间间隔
            // 由于这是第一段歌词之前的间隔，后面肯定有歌词，所以是 intro_inst（纯音乐前奏）
            val intro = SongStructure(
                label = SongStructureType.INTRO_INST.displayName,
                startTime = 0L,
                endTime = firstLine.startTime,
                type = SongStructureType.INTRO_INST
            )
            interludes.add(intro)
        }

        // 检测歌词之间的间奏
        for (i in 0 until lyricLines.size - 1) {
            val currentLine = lyricLines[i]
            val nextLine = lyricLines[i + 1]
            val gap = nextLine.startTime - currentLine.endTime

            // 所有间隔都需要 >= 4 秒才显示
            if (gap >= INTERLUDE_THRESHOLD_MS) {
                val type = when (i) {
                    0 -> SongStructureType.INTRO_INST  // 不会到这里，保留以防逻辑变化
                    lyricLines.size - 2 -> {
                        // 倒数第二段和最后一段之间的间隔，需要判断是 outro_inst 还是 outro_para
                        // 因为后面还有最后一段歌词，所以是 outro_inst（纯音乐尾奏）
                        SongStructureType.OUTRO_INST
                    }

                    else -> SongStructureType.INTERLUDE
                }

                interludes.add(
                    SongStructure(
                        label = type.displayName,
                        startTime = currentLine.endTime,
                        endTime = nextLine.startTime,
                        type = type
                    )
                )
            }
        }

        // 检测尾奏：从最后一句歌词结束到歌曲结束
        if (songDuration > 0) {
            val lastLine = lyricLines.last()
            val outroDuration = songDuration - lastLine.endTime

            if (outroDuration >= INTERLUDE_THRESHOLD_MS) {
                // 最后一段歌词之后到歌曲结束，没有后续歌词，所以是 outro_inst（纯音乐尾奏）
                val outro = SongStructure(
                    label = SongStructureType.OUTRO_INST.displayName,
                    startTime = lastLine.endTime,
                    endTime = songDuration,
                    type = SongStructureType.OUTRO_INST
                )
                interludes.add(outro)
            }
        }

        return interludes
    }

    /**
     * 时间范围
     *
     * 表示 TTML 中某个节点 (例如段落 <p> 或歌曲结构 <span>) 的时间范围。
     * 用作歌曲结构解析的原始输入数据。
     */
    data class TimeRange(
        val startTime: Long,  // 开始时间（毫秒）
        val endTime: Long     // 结束时间（毫秒）
    )

    /**
     * 从 TTML 文档解析歌曲结构
     *
     * 这是与 TTML 文档配套的解析入口。它采用两级策略：
     * 1. 优先使用 TTML 元数据中提供的结构信息（最准确）
     *    - itunes:songPart / itunes:songwriters 等
     * 2. 如果没有元数据或元数据中未找到结构，则从歌词行和时间范围自动推断结构
     *
     * @param doc TTML 解析后的 XML 文档
     * @param vocalLines 来自 <body>/<p> 的有效演唱行（已过滤背景行等）
     * @param timedParagraphRanges 来自段落的时间范围列表
     * @return 歌曲结构列表（按时间顺序排列）
     */
    fun parseFromTtmlDocument(
        doc: Document,
        vocalLines: List<LyricLine>,
        timedParagraphRanges: List<TimeRange>
    ): List<SongStructure> {
        // 1. 优先尝试从 TTML 元数据中解析结构
        val metadataStructures = parseStructuresFromMetadata(doc)
        if (metadataStructures.isNotEmpty()) {
            Timber.d("[SongStructure] Found ${metadataStructures.size} structures from TTML metadata")
            return metadataStructures
        }

        // 2. Fallback：结合 timedParagraphRanges 和 vocalLines 推断结构
        if (vocalLines.isEmpty() && timedParagraphRanges.isEmpty()) {
            Timber.w("[SongStructure] No vocal lines or timed ranges available, returning empty structures")
            return emptyList()
        }

        // 如果有时间范围信息，以时间范围为主
        val baseLines = if (vocalLines.isNotEmpty()) {
            vocalLines
        } else {
            // 把时间范围转换成伪 LyricLine 供 detectInterludes 使用
            timedParagraphRanges.map { range ->
                LyricLine(
                    startTime = range.startTime,
                    endTime = range.endTime,
                    text = ""
                )
            }
        }

        val songDuration = timedParagraphRanges.maxOfOrNull { it.endTime } ?: 0L
        Timber.d("[SongStructure] Fallback inference: ${baseLines.size} lines, duration=${songDuration}ms")
        return inferStructureFromLyrics(baseLines, songDuration)
    }

    /**
     * 从 TTML metadata 元素中解析歌曲结构
     *
     * 支持的命名空间与字段：
     * - itunes:songPart (apple-music 提供的歌曲结构信息)
     * - ttm:songPart (W3C TTML metadata 扩展)
     *
     * 注：当前实现专注于通用回退逻辑，元数据中明确的 songPart 留作后续扩展。
     */
    private fun parseStructuresFromMetadata(doc: Document): List<SongStructure> {
        return try {
            val head = doc.getElementsByTagName("head").item(0) as? org.w3c.dom.Element
                ?: return emptyList()
            val metadataElement = head.getElementsByTagName("metadata").item(0) as? org.w3c.dom.Element
                ?: return emptyList()

            val structures = mutableListOf<SongStructure>()

            // 查找 itunes:songPart 元素 (Apple Music TTML 扩展)
            val songParts = metadataElement.getElementsByTagName("itunes:songPart")
            for (i in 0 until songParts.length) {
                val element = songParts.item(i) as? org.w3c.dom.Element ?: continue
                val label = element.textContent?.trim()?.takeIf { it.isNotEmpty() } ?: continue
                val begin = element.getAttribute("begin")
                val end = element.getAttribute("end")
                if (begin.isBlank() || end.isBlank()) continue

                val startTime = parseTimeString(begin)
                val endTime = parseTimeString(end)
                if (endTime <= startTime) continue

                structures.add(
                    SongStructure(
                        label = label,
                        startTime = startTime,
                        endTime = endTime,
                        type = mapLabelToStructureType(label)
                    )
                )
            }

            structures
        } catch (e: Exception) {
            Timber.w("[SongStructure] Failed to parse structures from metadata: $e")
            emptyList()
        }
    }

    /**
     * 将文本标签映射到 SongStructureType
     */
    private fun mapLabelToStructureType(label: String): SongStructureType {
        val normalized = label.trim().lowercase()
        return when {
            normalized.contains("intro") -> SongStructureType.INTRO_INST
            normalized.contains("outro") || normalized.contains("ending") -> SongStructureType.OUTRO_INST
            normalized.contains("chorus") || normalized.contains("hook") -> SongStructureType.CHORUS
            normalized.contains("verse") -> SongStructureType.VERSE
            normalized.contains("bridge") -> SongStructureType.BRIDGE
            normalized.contains("pre-chorus") || normalized.contains("prechorus") -> SongStructureType.PRE_CHORUS
            normalized.contains("interlude") || normalized.contains("instrumental") -> SongStructureType.INTERLUDE
            normalized.contains("solo") -> SongStructureType.SOLO
            normalized.contains("break") -> SongStructureType.BREAK
            else -> SongStructureType.UNKNOWN
        }
    }

    /**
     * 将 TTML 时间格式转换为毫秒
     *
     * 格式：mm:ss.mmm (例：00:12.345) 或 hh:mm:ss.mmm
     *
     * 说明：internal 可见性以便同模块内的 TTMLParser 复用，消除重复代码。
     */
    internal fun parseTimeString(timeStr: String): Long {
        if (timeStr.isBlank()) return 0L
        val normalized = timeStr.trim().lowercase().removeSuffix("s")
        if (normalized.isEmpty()) return 0L

        return try {
            if (!normalized.contains(":")) {
                val seconds = normalized.toDoubleOrNull() ?: return 0L
                (seconds * 1000.0).toLong()
            } else {
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
            }
        } catch (e: Exception) {
            Timber.e("[SongStructure] Failed to parse time string: $timeStr $e")
            0L
        }
    }
}
