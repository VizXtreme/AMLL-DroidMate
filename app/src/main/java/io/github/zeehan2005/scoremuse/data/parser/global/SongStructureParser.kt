package io.github.zeehan2005.scoremuse.data.parser.global

import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.SongStructure
import io.github.zeehan2005.scoremuse.global.SongStructureType
import timber.log.Timber
import java.util.Locale

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
        Timber.d("[SongStructure] 🔍 SongStructureParser.parseStructure 被调用")
        Timber.d("[SongStructure] 📋 metadataStructure 参数：$metadataStructure")
        Timber.d("[SongStructure] 📋 metadataStructure.isNullOrEmpty() = ${metadataStructure.isNullOrEmpty()}")
        Timber.d("[SongStructure] 📋 lyricsLines.size = ${lyricsLines.size}")
        
        // 如果提供了元数据结构，优先使用
        if (!metadataStructure.isNullOrEmpty()) {
            Timber.d("[SongStructure] ✅ 使用元数据结构信息：${metadataStructure.size} 个段落")
            metadataStructure.forEachIndexed { index, structure ->
                Timber.d("[SongStructure]  [$index] ${structure.label} (${structure.type.displayName}): ${formatTime(structure.startTime)} - ${formatTime(structure.endTime)} (${structure.duration}ms)")
            }
            return metadataStructure
        }
        
        // 否则，从歌词行自动推断结构
        Timber.i("[SongStructure] ⚠️ Fallback 触发：无元数据结构信息")
        Timber.d("[SongStructure] 歌词行数：${lyricsLines.size}，歌曲时长：${songDuration}ms")
        
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
        
        Timber.d("[SongStructure] Fallback: 检测到 ${interludes.size} 个间奏/前奏/尾奏段落")
        
        if (interludes.isEmpty()) {
            // 没有检测到间奏，将所有歌词作为一个段落
            Timber.i("[SongStructure] ⚠️ Fallback: 无间奏检测，将整首歌标记为单一段落 '段落 1'")
            Timber.d("[SongStructure] 第一行时间：${formatTime(lines.first().startTime)}, 最后一行时间：${formatTime(lines.last().endTime)}")
            
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
        
        Timber.d("[SongStructure] 推断完成：${structures.size} 个段落")
        structures.forEachIndexed { index, structure ->
            Timber.d("[SongStructure]  [$index] ${structure.label} (${structure.type}): ${formatTime(structure.startTime)} - ${formatTime(structure.endTime)} (${structure.duration}ms)")
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
            Timber.d("[SongStructure] 检测到前奏 (intro_inst): ${formatTime(0)} - ${formatTime(firstLine.startTime)} (时长：${firstLine.startTime}ms)")
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
                
                Timber.d("[SongStructure] 检测到${type.displayName}: ${formatTime(currentLine.endTime)} - ${formatTime(nextLine.startTime)} (间隔：${gap}ms)")
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
                Timber.d("[SongStructure] 检测到尾奏 (outro_inst): ${formatTime(lastLine.endTime)} - ${formatTime(songDuration)} (时长：${outroDuration}ms)")
            }
        }
        
        return interludes
    }
    
    /**
     * 格式化时间为 mm:ss 格式
     */
    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.ROOT, "%d:%02d", minutes, seconds)
    }
}