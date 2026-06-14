package io.github.zeehan2005.scoremuse.data.parser.global

import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.SongStructure
import io.github.zeehan2005.scoremuse.global.SongStructureType
import org.w3c.dom.Document
import org.w3c.dom.Node
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
     * 解析歌曲结构。
     *
     * 步骤：
     * 1. 始终检测间奏（前奏/间奏/尾奏）—— 独立函数，与 metadata 无关
     * 2. 如果有 metadata（来自 TTML div 的 songPart）：
     *    a. 合并 metadata 段落标签与检测到的间奏（去重）
     *    b. 在合并结果后补全尾奏（以 merge 后最后一个结构的结束时间为准）
     * 3. 如果没有 metadata（非 TTML 源）：
     *    仅返回检测到的间奏，不生成"段落 X"回退标签
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

        // 步骤1: 始终检测间奏，与 metadata 是否存在无关
        val interludes = detectInterludes(lyricsLines, effectiveDuration)
        Timber.d("[SongStructure] Detected ${interludes.size} interludes")

        if (metadataStructure.isNullOrEmpty()) {
            Timber.v("[SongStructure] No metadata structures, returning ${interludes.size} interludes only")
            return interludes
        }

        // 步骤2: 有 metadata — 合并段落标签 + 间奏，然后补齐尾奏
        val merged = mergeWithInterludes(metadataStructure, interludes)
        return ensureOutro(merged, effectiveDuration)
    }

    /**
     * 在保留已有结构（如 songPart）的基础上，合并前奏/间奏/尾奏。
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
        if (lastStructure.type == SongStructureType.OUTRO_INST ||
            lastStructure.type == SongStructureType.OUTRO_PARA
        ) return baseStructures

        Timber.d("[SongStructure] Appending OUTRO_INST: last.endTime=${outroStart}ms, songDuration=${songDuration}ms, gap=${outroDuration}ms")
        return baseStructures + SongStructure(
            label = SongStructureType.OUTRO_INST.displayName,
            startTime = outroStart,
            endTime = songDuration,
            type = SongStructureType.OUTRO_INST
        )
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
