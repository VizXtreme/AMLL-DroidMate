package io.github.zeehan2005.scoremuse.data.parser

import io.github.zeehan2005.scoremuse.data.parser.global.processSyllableText
import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.LyricWord
import org.json.JSONObject
import timber.log.Timber

/**
 * YRC 格式解析器（网易云音乐逐字歌词格式）
 * 
 * YRC 是网易云音乐的专有逐字歌词格式，特点包括：
 * - JSON 元数据行：{"t": ...} 格式，包含歌手、专辑等信息
 * - 行级时间戳：[起始时间，持续时间]（毫秒）
 * - 逐字时间戳：(起始时间，持续时间，0) 紧跟在每个字后面
 * 
 * 示例：
 * {"t":"歌手","c":[{"tx":"歌手:"},{"tx":"周杰伦"}]}
 * [0,3500]在 (0,500,0) 世 (500,300,0) 界 (800,400,0) 的 (1200,300,0)
 */
object YrcParser {

    /** YRC 行级时间戳正则：[起始 ms，持续 ms] */
    private val yrcLineTimestampRegex = Regex("""^\[(?<start>\d+),(?<duration>\d+)]""")
    
    /** YRC 逐字时间戳正则：(起始 ms，持续 ms，0) */
    private val yrcSyllableTimestampRegex = Regex("""\((?<start>\d+),(?<duration>\d+),(?<zero>0)\)""")

    /**
     * 解析 YRC 格式的歌词内容
     * 
     * 解析流程：
     * 1. 逐行遍历输入内容
     * 2. 识别并解析 JSON 元数据行（歌手、专辑等）
     * 3. 对匹配时间戳的行解析为 LyricLine 对象
     * 4. 提取逐字时间信息
     * 
     * @param content YRC 格式的原始文本
     * @return 解析后的歌词行列表
     */
    fun parse(content: String): List<LyricLine> {
        val lines = mutableListOf<LyricLine>()
        val metadata = mutableMapOf<String, MutableList<String>>()
        
        val contentLines = content.lines()
        Timber.v("[YrcParser] Parsing $contentLines.size lines")

        for ((index, raw) in contentLines.withIndex()) {
            val lineNum = index + 1
            val line = raw.trim()
            if (line.isEmpty()) continue

            if (line.startsWith("{\"t\":")) {
                parseJsonMetadataLine(line, metadata, lineNum)
                continue
            }

            if (yrcLineTimestampRegex.containsMatchIn(line)) {
                try {
                    parseYrcLine(line)?.let {
                        lines.add(it)
                        if (lines.size <= 3) {
                            Timber.d("[YrcParser] Parsed line $lineNum - ${it.text.take(30)}")
                        }
                    }
                } catch (e: Exception) {
                    Timber.w("[YrcParser] Failed to parse YRC line $lineNum $e")
                }
            } else {
                if (lineNum <= 5 || (lineNum > contentLines.size - 3)) {
                    Timber.d("[YrcParser] Skipping line $lineNum (not matching YRC format): ${line.take(50)}")
                }
            }
        }
        
        Timber.d("[YrcParser] Parsed $lines.size lyric lines total")
        return lines
    }

    private fun parseJsonMetadataLine(
        line: String,
        metadata: MutableMap<String, MutableList<String>>,
        lineNum: Int
    ) {
        try {
            val json = JSONObject(line)
            val cArray = json.optJSONArray("c") ?: return
            if (cArray.length() == 0) return

            val keyPart = cArray.optJSONObject(0)?.optString("tx").orEmpty().trim()
            val key = keyPart.trimEnd { it == ':' || it == '：' || it.isWhitespace() }
            if (key.isEmpty()) return

            val values = mutableListOf<String>()
            for (i in 1 until cArray.length()) {
                val value = cArray.optJSONObject(i)?.optString("tx").orEmpty().trim()
                if (value.isNotEmpty() && value != "/") {
                    values.add(value)
                }
            }

            if (values.isNotEmpty()) {
                metadata.getOrPut(key) { mutableListOf() }.add(values.joinToString(", "))
            }
        } catch (e: Exception) {
            Timber.w("[YrcParser] Metadata parse failed on line $lineNum $e")
        }
    }

    private fun parseYrcLine(line: String): LyricLine? {
        val lineTs = yrcLineTimestampRegex.find(line) ?: return null
        val lineStart = lineTs.groups["start"]?.value?.toLongOrNull() ?: return null
        val lineDuration = lineTs.groups["duration"]?.value?.toLongOrNull() ?: return null
        val afterTs = line.substring(lineTs.range.last + 1)

        val words = mutableListOf<LyricWord>()
        val matches = yrcSyllableTimestampRegex.findAll(afterTs).toList()
        if (matches.isEmpty()) return null

        for ((i, tsMatch) in matches.withIndex()) {
            val textStart = tsMatch.range.last + 1
            val textEnd = if (i + 1 < matches.size) matches[i + 1].range.first else afterTs.length
            val rawText = afterTs.substring(textStart, textEnd)
            val processed = processSyllableText(rawText, words) ?: continue
            val (cleanText, endsWithSpace) = processed

            val sylStart = tsMatch.groups["start"]?.value?.toLongOrNull() ?: continue
            val sylDuration = tsMatch.groups["duration"]?.value?.toLongOrNull() ?: continue
            val text = if (endsWithSpace) "$cleanText " else cleanText

            words.add(
                LyricWord(
                    word = text,
                    startTime = sylStart,
                    endTime = sylStart + maxOf(1L, sylDuration) // 确保持续时间至少为 1ms，防止 JS 侧计算 NaN
                )
            )
        }

        if (words.isEmpty()) return null

        return LyricLine(
            startTime = lineStart,
            endTime = lineStart + maxOf(1L, lineDuration), // 确保持续时间至少为 1ms
            text = words.joinToString(separator = "") { it.word }.trimEnd(),
            words = words
        )
    }
}
