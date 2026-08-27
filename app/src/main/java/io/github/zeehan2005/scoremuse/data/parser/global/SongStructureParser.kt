package io.github.zeehan2005.scoremuse.data.parser.global

import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.SongStructure
import org.w3c.dom.Document
import org.w3c.dom.Node
import timber.log.Timber

/**
 * 歌曲结构解析器
 *
 * 工作原理：
 * 1. 优先使用 TTML <div> 元素中的 itunes:songPart 属性（最准确）
 * 2. 否则，从歌词行自动推断结构（间奏等）
 */
object SongStructureParser {

    // 间奏检测阈值（4 秒）
    private const val INTERLUDE_THRESHOLD_MS = 4000L

    /**
     * 解析歌曲结构。
     *
     * - TTML 源有真实 <div itunes:songPart>（metadataStructure 非空）：
     *     保留段落标签 + 合并间奏 + 补全尾奏。不 fallback。
     * - 其他所有情况（TTML 无 songPart、非 TTML 源等）：
     *     fallback 从歌词行推断段落结构。
     *
     * 间奏始终独立检测，避免在各分支中重复计算。
     */
    fun parseStructure(
        lyricsLines: List<LyricLine>,
        metadataStructure: List<SongStructure>? = null,
        songDuration: Long = 0L
    ): List<SongStructure> {
        val effectiveDuration = if (songDuration > 0) {
            songDuration
        } else {
            val inferred = lyricsLines.maxOfOrNull { it.endTime } ?: 0L
            if (inferred > 0) inferred + 5_000L else 0L
        }

        // 始终检测间奏，与 metadata 是否存在无关
        val interludes = detectInterludes(lyricsLines, effectiveDuration)
        Timber.d("[SongStructure] Detected ${interludes.size} interludes")

        if (metadataStructure.isNullOrEmpty()) {
            Timber.d("[SongStructure] No TTML paragraph markers, fallback inference")
            return inferFallbackSections(lyricsLines, interludes)
        }

        // 有真实 TTML 段落标记：保留标签 + 合并间奏 + 补全尾奏
        val merged = mergeWithInterludes(metadataStructure, interludes)
        return ensureOutro(merged, effectiveDuration)
    }

    /**
     * 从歌词行推断段落结构（"Paragraph 1"、"段落 2"…），用于没有 TTML songPart 的场景。
     * 间奏已由 [detectInterludes] 预先计算好，直接在此填入段落标签。
     */
    private fun inferFallbackSections(
        lines: List<LyricLine>,
        interludes: List<SongStructure>
    ): List<SongStructure> {
        if (lines.isEmpty()) {
            Timber.w("[SongStructure] Lyric lines are empty, returning empty structure")
            return emptyList()
        }

        val result = mutableListOf<SongStructure>()

        if (interludes.isEmpty()) {
            result.add(
                SongStructure(
                    label = "Paragraph 1",
                    startTime = lines.first().startTime,
                    endTime = lines.last().endTime
                )
            )
            return result
        }

        var paragraphIndex = 1
        var lastEndTime: Long = 0

        for (interlude in interludes) {
            if (interlude.startTime > lastEndTime) {
                val paragraphLines = lines.filter {
                    it.startTime >= lastEndTime && it.endTime <= interlude.startTime
                }
                if (paragraphLines.isNotEmpty()) {
                    result.add(
                        SongStructure(
                            label = "Paragraph $paragraphIndex",
                            startTime = paragraphLines.first().startTime,
                            endTime = paragraphLines.last().endTime
                        )
                    )
                    paragraphIndex++
                }
            }
            result.add(interlude)
            lastEndTime = interlude.endTime
        }

        val remainingLines = lines.filter { it.startTime >= lastEndTime }
        if (remainingLines.isNotEmpty()) {
            result.add(
                SongStructure(
                    label = "Paragraph $paragraphIndex",
                    startTime = remainingLines.first().startTime,
                    endTime = remainingLines.last().endTime
                )
            )
        }

        return result
    }

    /**
     * 在保留已有结构（如 songPart）的基础上，合并间奏。
     * interludes 由上层统一调用 [detectInterludes] 生成，避免重复计算。
     */
    private fun mergeWithInterludes(
        baseStructures: List<SongStructure>,
        interludes: List<SongStructure>
    ): List<SongStructure> {
        if (interludes.isEmpty()) return baseStructures

        // 过滤掉与 baseStructures 中已有结构时间重叠的间奏。
        // 这避免了当 parseStructure() 被多次调用时（如 TTMLParser 合并一次，
        // updateSongStructures 再用合并后的结果作为 metadataStructures 传入），
        // 同一间奏被重复添加。
        val newInterludes = interludes.filter { interlude ->
            baseStructures.none { existing ->
                interlude.startTime < existing.endTime && interlude.endTime > existing.startTime
            }
        }
        if (newInterludes.isEmpty()) return baseStructures

        return (baseStructures + newInterludes).sortedBy { it.startTime }
    }

    /**
     * 确保合并后的结构末尾有尾奏（如果未覆盖到歌曲末尾）。
     * 以合并后最后一个结构的结束时间为尾奏起点，而非原始 metadata。
     */
    private fun ensureOutro(
        baseStructures: List<SongStructure>,
        songDuration: Long
    ): List<SongStructure> {
        if (songDuration <= 0 || baseStructures.isEmpty()) return baseStructures

        val lastStructure = baseStructures.maxByOrNull { it.endTime } ?: return baseStructures
        val outroStart = lastStructure.endTime
        val outroDuration = songDuration - outroStart

        if (outroDuration < INTERLUDE_THRESHOLD_MS) return baseStructures
        if (lastStructure.label == "Interlude") return baseStructures

        Timber.d("[SongStructure] Appending outro Interlude: last.endTime=${outroStart}ms, songDuration=${songDuration}ms, gap=${outroDuration}ms")
        return baseStructures + SongStructure(
            label = "Interlude",
            startTime = outroStart,
            endTime = songDuration
        )
    }

    /**
     * 检测歌词行之间的间奏/前奏/尾奏。
     * 前奏和尾奏统一标记为 "Interlude"。
     */
    private fun detectInterludes(lyricLines: List<LyricLine>, songDuration: Long = 0L): List<SongStructure> {
        val interludes = mutableListOf<SongStructure>()

        if (lyricLines.isEmpty()) return interludes

        // 检测前奏
        val firstLine = lyricLines.first()
        if (firstLine.startTime >= INTERLUDE_THRESHOLD_MS) {
            interludes.add(
                SongStructure(
                    label = "Interlude",
                    startTime = 0L,
                    endTime = firstLine.startTime
                )
            )
        }

        // 检测歌词行之间的间奏
        for (i in 0 until lyricLines.size - 1) {
            val currentLine = lyricLines[i]
            val nextLine = lyricLines[i + 1]
            val gap = nextLine.startTime - currentLine.endTime

            if (gap >= INTERLUDE_THRESHOLD_MS) {
                interludes.add(
                    SongStructure(
                        label = "Interlude",
                        startTime = currentLine.endTime,
                        endTime = nextLine.startTime
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
                        label = "Interlude",
                        startTime = lastLine.endTime,
                        endTime = songDuration
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
            // 只查找 <tt> 根元素下的直接 <body> 子元素，避免从 rawXmlMetadata
            // 嵌入的文档中获取重复的 div（旧缓存可能在 <metadata> 中嵌入了整份 TTML）。
            val root = doc.getDocumentElement()
            val body = (0 until root.childNodes.length)
                .map { root.childNodes.item(it) }
                .filter { it.nodeType == Node.ELEMENT_NODE }
                .map { it as org.w3c.dom.Element }
                .firstOrNull { it.tagName == "body" }
                ?: return emptyList()
            val divs = body.getElementsByTagName("div")
            Timber.d("[SongStructure] Searching div elements inside body: found ${divs.length}")

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
                        endTime = endTime
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
