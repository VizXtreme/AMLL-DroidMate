package io.github.zeehan2005.scoremuse.data.parser.global

import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.SongStructure
import io.github.zeehan2005.scoremuse.global.SongStructureType
import org.w3c.dom.Document
import timber.log.Timber

/**
 * 歌曲结构解析器
 *
 * 工作原理：
 * 1. 优先使用 TTML <div> 元素中的 itunes:songPart 属性（最准确）
 * 2. 否则，从歌词行自动推断结构（前奏、间奏、尾奏等）
 */
object SongStructureParser {

    // 间奏检测阈值（4 秒）
    private const val INTERLUDE_THRESHOLD_MS = 4000L

    /**
     * 从歌词行解析歌曲结构（如果提供了 metadataStructure，优先使用并补充间奏/尾奏）。
     *
     * 修复：即使有 metadataStructure（如 itunes:songPart），也会同时从歌词行时间间隔
     * 检测间奏（interlude），因为 songPart 不会记录两个段落之间的纯器乐间隔。
     */
    fun parseStructure(
        lyricsLines: List<LyricLine>,
        metadataStructure: List<SongStructure>? = null,
        songDuration: Long = 0L
    ): List<SongStructure> {
        // 计算有效的 songDuration（多级 fallback）
        val effectiveDuration = if (songDuration > 0) {
            songDuration
        } else {
            val inferred = lyricsLines.maxOfOrNull { it.endTime } ?: 0L
            if (inferred > 0) inferred + 5_000L else 0L
        }

        if (metadataStructure.isNullOrEmpty()) {
            Timber.v("[SongStructure] Fallback triggered: no metadata structures")
            return inferStructureFromLyrics(lyricsLines, effectiveDuration)
        }

        // 有 metadata：保留 songPart + 补全间奏 + 补全尾奏
        val withOutro = ensureOutro(metadataStructure, lyricsLines, effectiveDuration)
        return mergeWithInterludes(withOutro, lyricsLines, effectiveDuration)
    }

    /**
     * 在保留已有结构（如 songPart）的基础上，补充从歌词行推断出的间奏和尾奏。
     */
    private fun mergeWithInterludes(
        baseStructures: List<SongStructure>,
        lyricsLines: List<LyricLine>,
        songDuration: Long
    ): List<SongStructure> {
        if (lyricsLines.isEmpty()) return baseStructures

        val interludes = detectInterludes(lyricsLines, songDuration)
        if (interludes.isEmpty()) return baseStructures

        // 去重：过滤掉与 baseStructures 中已有结构时间重叠的间奏。
        // 这避免了当 parseStructure() 被多次调用时（如 TTMLParser 合并一次，
        // updateSongStructures 再用合并后的结果作为 metadataStructures 传入），
        // 同一间奏被重复添加。
        val newInterludes = interludes.filter { interlude ->
            baseStructures.none { existing ->
                interlude.startTime < existing.endTime && interlude.endTime > existing.startTime
            }
        }
        if (newInterludes.isEmpty()) return baseStructures

        // 合并：baseStructures + 新增间奏/尾奏，按 startTime 排序
        return (baseStructures + newInterludes).sortedBy { it.startTime }
    }

    /**
     * 确保元数据结构后面追加强制尾奏（如果元数据未覆盖到歌曲末尾）。
     */
    private fun ensureOutro(
        metadataStructure: List<SongStructure>,
        lyricsLines: List<LyricLine>,
        songDuration: Long
    ): List<SongStructure> {
        if (songDuration <= 0 || metadataStructure.isEmpty()) {
            return metadataStructure
        }

        val lastStructure = metadataStructure.maxByOrNull { it.endTime } ?: return metadataStructure
        val outroStart = lastStructure.endTime
        val outroDuration = songDuration - outroStart

        if (outroDuration < INTERLUDE_THRESHOLD_MS) return metadataStructure
        if (lastStructure.type == SongStructureType.OUTRO_INST ||
            lastStructure.type == SongStructureType.OUTRO_PARA
        ) return metadataStructure

        Timber.d("[SongStructure] Appending OUTRO_INST: last.endTime=${outroStart}ms, songDuration=${songDuration}ms, gap=${outroDuration}ms")
        return metadataStructure + SongStructure(
            label = SongStructureType.OUTRO_INST.displayName,
            startTime = outroStart,
            endTime = songDuration,
            type = SongStructureType.OUTRO_INST
        )
    }

    /**
     * 从歌词行自动推断歌曲结构。
     */
    private fun inferStructureFromLyrics(lines: List<LyricLine>, songDuration: Long = 0L): List<SongStructure> {
        if (lines.isEmpty()) {
            Timber.w("[SongStructure] 歌词行为空，返回空结构")
            return emptyList()
        }

        val structures = mutableListOf<SongStructure>()
        val interludes = detectInterludes(lines, songDuration)

        if (interludes.isEmpty()) {
            structures.add(
                SongStructure(
                    label = "段落 1",
                    startTime = lines.first().startTime,
                    endTime = lines.last().endTime,
                    type = SongStructureType.UNKNOWN
                )
            )
        } else {
            var paragraphIndex = 1
            var lastEndTime: Long = 0

            for (interlude in interludes) {
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
                                type = SongStructureType.UNKNOWN
                            )
                        )
                        paragraphIndex++
                    }
                }

                structures.add(interlude)
                lastEndTime = interlude.endTime
            }

            val remainingLines = lines.filter { it.startTime >= lastEndTime }
            if (remainingLines.isNotEmpty()) {
                structures.add(
                    SongStructure(
                        label = "段落 $paragraphIndex",
                        startTime = remainingLines.first().startTime,
                        endTime = remainingLines.last().endTime,
                        type = SongStructureType.UNKNOWN
                    )
                )
            }
        }

        return structures
    }

    /**
     * 检测歌词行之间的间奏/前奏/尾奏。
     */
    private fun detectInterludes(lyricLines: List<LyricLine>, songDuration: Long = 0L): List<SongStructure> {
        val interludes = mutableListOf<SongStructure>()

        if (lyricLines.isEmpty()) return interludes

        // 检测前奏
        val firstLine = lyricLines.first()
        if (firstLine.startTime >= INTERLUDE_THRESHOLD_MS) {
            interludes.add(
                SongStructure(
                    label = SongStructureType.INTRO_INST.displayName,
                    startTime = 0L,
                    endTime = firstLine.startTime,
                    type = SongStructureType.INTRO_INST
                )
            )
        }

        // 检测歌词行之间的间奏（统一为 INTERLUDE，尾奏由 songDuration 单独处理）
        for (i in 0 until lyricLines.size - 1) {
            val currentLine = lyricLines[i]
            val nextLine = lyricLines[i + 1]
            val gap = nextLine.startTime - currentLine.endTime

            if (gap >= INTERLUDE_THRESHOLD_MS) {
                interludes.add(
                    SongStructure(
                        label = SongStructureType.INTERLUDE.displayName,
                        startTime = currentLine.endTime,
                        endTime = nextLine.startTime,
                        type = SongStructureType.INTERLUDE
                    )
                )
            }
        }

        // 检测尾奏
        if (songDuration > 0) {
            val lastLine = lyricLines.last()
            val outroDuration = songDuration - lastLine.endTime

            if (outroDuration >= INTERLUDE_THRESHOLD_MS) {
                interludes.add(
                    SongStructure(
                        label = SongStructureType.OUTRO_INST.displayName,
                        startTime = lastLine.endTime,
                        endTime = songDuration,
                        type = SongStructureType.OUTRO_INST
                    )
                )
            }
        }

        return interludes
    }

    /**
     * 时间范围
     */
    data class TimeRange(
        val startTime: Long,
        val endTime: Long
    )

    /**
     * 从 <div> 元素的 itunes:songPart 属性解析 songPart。
     *
     * TTML 示例：
     * ```xml
     * <div begin="00:17.286" end="00:45.219" itunes:songPart="Verse">...</div>
     * ```
     */
    fun parseStructuresFromDivs(doc: Document): List<SongStructure> {
        return try {
            // Android 的 DocumentBuilderFactory 默认是 namespace-unaware，
            // 直接使用 getElementsByTagName 即可匹配 div 元素
            val divs = doc.getElementsByTagName("div")
            Timber.d("[SongStructure] Searching div elements: found ${divs.length}")

            val structures = mutableListOf<SongStructure>()
            for (i in 0 until divs.length) {
                val div = divs.item(i) as? org.w3c.dom.Element ?: continue

                // 兼容驼峰（Apple Music 原始）和连字符（AMLL TTML DB）两种属性名
                val songPart = div.getAttribute("itunes:songPart")
                    .ifBlank { div.getAttribute("itunes:song-part") }
                if (songPart.isBlank()) continue

                val beginStr = div.getAttribute("begin")
                val endStr = div.getAttribute("end")
                if (beginStr.isBlank() || endStr.isBlank()) continue

                val startTime = parseTimeString(beginStr)
                val endTime = parseTimeString(endStr)
                if (endTime <= startTime) continue

                structures.add(
                    SongStructure(
                        label = songPart,
                        startTime = startTime,
                        endTime = endTime,
                        type = mapLabelToStructureType(songPart)
                    )
                )
                Timber.d("[SongStructure] div itunes:songPart parsed: '$songPart' ${startTime}ms-${endTime}ms")
            }
            structures
        } catch (e: Exception) {
            Timber.w("[SongStructure] Failed to parse div itunes:songPart: $e")
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
