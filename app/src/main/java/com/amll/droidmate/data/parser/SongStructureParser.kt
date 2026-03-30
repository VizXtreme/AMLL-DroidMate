package com.amll.droidmate.data.parser

import com.amll.droidmate.domain.model.LyricLine
import com.amll.droidmate.domain.model.SongStructure
import com.amll.droidmate.domain.model.SongStructureType
import com.amll.droidmate.domain.model.TTMLLyrics
import timber.log.Timber

/**
 * 歌曲结构解析器
 * 从 TTML 歌词中解析歌曲结构信息，或在只有单一段落时自动推断结构
 */
object SongStructureParser {
    
    /**
     * 间奏检测阈值（4 秒）
     * 当歌词行之间的时间间隔大于等于此值时，认为是间奏/前奏/尾奏
     */
    private const val INTERLUDE_THRESHOLD_MS = 4000L
    
    /**
     * 从 TTML 歌词解析歌曲结构
     * 
     * @param lyricsLines 歌词行列表
     * @param metadataStructure 从 TTML 元数据中解析的结构信息（如果有）
     * @param songDuration 歌曲总时长（毫秒），用于检测尾奏
     * @return 歌曲结构列表
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
     * 从 TTMLLyrics 对象解析歌曲结构
     * 优先使用元数据中的结构信息
     */
    fun parseStructure(lyrics: TTMLLyrics): List<SongStructure> {
        return parseStructure(lyrics.lines, lyrics.metadata.songStructures)
    }
    
    /**
     * 从歌词行自动推断歌曲结构
     * 
     * Fallback 逻辑：
     * 1. 检测歌词行之间的时间间隔
     * 2. 间隔 >= 4 秒的标记为间奏/前奏/尾奏
     * 3. 将间隔前后的歌词标记为独立段落
     */
    private fun inferStructureFromLyrics(lines: List<LyricLine>, songDuration: Long = 0L): List<SongStructure> {
        if (lines.isEmpty()) {
            Timber.w("[SongStructure] Fallback: 歌词行为空，返回空结构")
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
                    type = SongStructureType.VERSE
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
                                type = SongStructureType.VERSE
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
                        type = SongStructureType.VERSE
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
     */
    private fun detectInterludes(lyricLines: List<LyricLine>, songDuration: Long = 0L): List<SongStructure> {
        val interludes = mutableListOf<SongStructure>()
        
        if (lyricLines.isEmpty()) {
            return interludes
        }
        
        // 检测前奏：从歌曲开始到第一句歌词
        val firstLine = lyricLines.first()
        if (firstLine.startTime >= INTERLUDE_THRESHOLD_MS) {
            val intro = SongStructure(
                label = SongStructureType.INTRO.displayName,
                startTime = 0L,
                endTime = firstLine.startTime,
                type = SongStructureType.INTRO
            )
            interludes.add(intro)
            Timber.d("[SongStructure] 检测到前奏：${formatTime(0)} - ${formatTime(firstLine.startTime)} (时长：${firstLine.startTime}ms)")
        }
        
        // 检测歌词之间的间奏
        for (i in 0 until lyricLines.size - 1) {
            val currentLine = lyricLines[i]
            val nextLine = lyricLines[i + 1]
            val gap = nextLine.startTime - currentLine.endTime
            
            // 所有间隔都需要 >= 4 秒才显示
            if (gap >= INTERLUDE_THRESHOLD_MS) {
                val type = when {
                    i == 0 -> SongStructureType.INTRO  // 不会到这里，保留以防逻辑变化
                    i == lyricLines.size - 2 -> SongStructureType.OUTRO
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
                val outro = SongStructure(
                    label = SongStructureType.OUTRO.displayName,
                    startTime = lastLine.endTime,
                    endTime = songDuration,
                    type = SongStructureType.OUTRO
                )
                interludes.add(outro)
                Timber.d("[SongStructure] 检测到尾奏：${formatTime(lastLine.endTime)} - ${formatTime(songDuration)} (时长：${outroDuration}ms)")
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
        return String.format("%d:%02d", minutes, seconds)
    }
}
