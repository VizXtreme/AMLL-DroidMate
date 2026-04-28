package dev.amll.droidmate.data.parser.global

import kotlin.collections.first
import dev.amll.droidmate.global.LyricLine

/**
 * 歌手/角色标签识别器
 *
 * 这个工具用于识别歌词文本中的演唱者标签，例如：
 * - "A: 歌词内容"
 * - "（B）: 歌词内容"
 * - "(C): 歌词内容"
 *
 * 在多人对唱的歌曲中，这个功能可以识别出每句歌词是由哪位歌手演唱的，
 * 并为不同的歌手分配唯一的 ID（如 v1, v2），方便后续进行角色区分显示。
 *
 * 参考：Unilyric 的 agent_recognizer 逻辑
 */
object AgentRecognizer {
    // 正则表达式：匹配三种格式的歌手标签
    // 1. (name): 英文括号
    // 2. （name）: 中文括号
    // 3. name: 纯名称加冒号
    private val AGENT_REGEX = Regex("^\\s*(?:\\((.+?)\\)|（(.+?)）|([^\\s:()（）]+))\\s*[:：]\\s*")

    /**
     * 识别歌词中的歌手标签
     *
     * 这个方法会遍历所有歌词行，检测并标注每句歌词的演唱者。
     * 支持两种模式：
     * 1. 单行模式："A: 歌词" → 这行的 agent 为 "v1"
     * 2. 区块模式："A:"（单独一行）→ 之后所有行的 agent 都为 "v1"，直到遇到新的标签
     *
     * @param lines 原始歌词行列表
     * @return 标注了 agent 信息的歌词行列表
     */
    fun recognizeAgents(lines: List<LyricLine>): List<LyricLine> {
        var currentAgentId: String? = null  // 当前活跃的歌手 ID（用于区块模式）
        val nameToId = mutableMapOf<String, String>()  // 歌手名到 ID 的映射表
        var nextIdNum = 1  // 下一个可用的 ID 编号

        // 第一遍：检测歌手标签并标注每一行
        val annotated = mutableListOf<LyricLine>()

        for (line in lines) {
            val text = line.text
            val match = AGENT_REGEX.find(text)
            if (match != null) {
                // 提取歌手名字（从三个捕获组中找到第一个非空的）
                val agentName = (1..3)
                    .mapNotNull { idx -> match.groups[idx]?.value?.trim() }
                    .firstOrNull()

                val fullMatch = match.value  // 完整匹配的文本（包括冒号）
                val remaining = text.removePrefix(fullMatch).trimStart()  // 移除标签后的歌词内容

                if (!agentName.isNullOrBlank()) {
                    // 为新歌手分配 ID（v1, v2, v3...）
                    val agentId = nameToId.getOrPut(agentName) {
                        "v${nextIdNum++}"
                    }

                    if (remaining.isEmpty()) {
                        // 区块模式：这行只是歌手标记，更新当前歌手但不生成歌词行
                        currentAgentId = agentId
                        continue
                    }

                    currentAgentId = agentId

                    // 创建新的歌词行，包含歌手信息
                    val newLine = line.copy(
                        text = remaining,
                        agent = agentId,
                        words = adjustWordsForPrefix(line.words, fullMatch, remaining)
                    )
                    annotated += newLine
                    continue
                }
            }

            // 没有找到歌手标签，使用当前的活跃歌手 ID
            val agentForLine = line.agent ?: currentAgentId
            annotated += line.copy(agent = agentForLine)
        }

        // 如果存在多个歌手，标记为对唱模式
        val uniqueAgents = annotated.mapNotNull { it.agent }.distinct()
        if (uniqueAgents.size > 1) {
            return annotated.map { line ->
                if (!line.agent.isNullOrBlank()) {
                    line.copy(isDuet = true)  // 标记为对唱
                } else {
                    line
                }
            }
        }

        return annotated
    }

    private fun adjustWordsForPrefix(
        words: List<dev.amll.droidmate.global.LyricWord>,
        prefix: String,
        remainingText: String
    ): List<dev.amll.droidmate.global.LyricWord> {
        if (words.isEmpty()) return listOf(
            dev.amll.droidmate.global.LyricWord(
                word = remainingText,
                startTime = 0L,
                endTime = 0L
            )
        )

        val first = words.first()
        val startTime = first.startTime
        val endTime = words.last().endTime

        val trimmedFirstWord = if (first.word.startsWith(prefix)) {
            first.word.removePrefix(prefix).trimStart()
        } else {
            remainingText
        }

        return listOf(
            dev.amll.droidmate.global.LyricWord(
                word = if (trimmedFirstWord.isNotEmpty()) trimmedFirstWord else remainingText,
                startTime = startTime,
                endTime = endTime
            )
        )
    }
}