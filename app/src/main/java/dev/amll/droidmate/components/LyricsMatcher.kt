package dev.amll.droidmate.components

import kotlin.math.abs
import dev.amll.droidmate.data.repository.LyricsRepository.MatchType

/**
 * 歌词匹配工具类
 *
 * 这个对象提供了智能的歌曲信息匹配算法，用于评估搜索结果与搜索查询的相似度。
 * 它综合考虑多个因素来判断两首歌曲是否是同一首：
 * - 歌名相似度（使用文本归一化和 Levenshtein 距离算法）
 * - 艺术家匹配度（支持多艺术家、别名、分隔符处理）
 * - 专辑名称匹配
 * - 时长差异（容忍一定范围内的误差）
 *
 * **应用场景**：
 * 1. 歌词搜索：从多个平台搜索时，识别重复的歌曲
 * 2. 自动选择：当有多个歌词候选时，选择匹配度最高的
 * 3. 结果排序：按匹配度对搜索结果进行排序
 *
 * **匹配算法特点**：
 * - 加权评分：不同字段有不同权重（歌名 > 艺术家 > 时长 > 专辑）
 * - 模糊匹配：不要求完全一致，允许拼写差异
 * - 多维度评估：综合判断而非单一标准
 */
object LyricsMatcher {

    /**
     * 评估搜索查询与候选歌曲的匹配度
     *
     * 这是歌词匹配的主入口方法。它会调用 [compareTrack] 进行详细比较，
     * 然后将分数转换为标准化的置信度值（0.0-1.0）。
     *
     * @param searchTitle 搜索时的歌名
     * @param searchArtist 搜索时的艺术家
     * @param resultTitle 候选结果的歌名
     * @param resultArtist 候选结果的艺术家
     * @param searchAlbum 搜索时的专辑名（可选）
     * @param resultAlbum 候选结果的专辑名（可选）
     * @param searchDurationMs 搜索时的时长（毫秒，可选）
     * @param resultDurationMs 候选结果的时长（毫秒，可选）
     * @return 匹配评估结果，包含置信度和匹配类型
     */
    fun evaluateMatch(
        searchTitle: String,
        searchArtist: String,
        resultTitle: String,
        resultArtist: String,
        searchAlbum: String? = null,
        resultAlbum: String? = null,
        searchDurationMs: Long? = null,
        resultDurationMs: Long? = null
    ): MatchEvaluation {
        // 调用核心比较方法，获取匹配类型和原始分数
        val matchType = compareTrack(
            searchTitle = searchTitle,
            searchArtist = searchArtist,
            resultTitle = resultTitle,
            resultArtist = resultArtist,
            searchAlbum = searchAlbum,
            resultAlbum = resultAlbum,
            searchDurationMs = searchDurationMs,
            resultDurationMs = resultDurationMs
        )

        // 将分数转换为 0.0-1.0 的置信度，并返回评估结果
        return MatchEvaluation(
            confidence = (matchType.score / 100f).coerceIn(0f, 1f),
            matchType = matchType.name
        )
    }

    /**
     * 比较歌曲信息，返回匹配类型
     *
     * 这是核心的匹配逻辑。它会分别比较歌名、艺术家、专辑和时长，
     * 然后根据加权算法计算总体匹配度。
     *
     * **权重说明**（可根据需要调整）：
     * - 歌名：1.0（最重要，因为歌名通常最准确）
     * - 艺术家：1.0（同样重要，但可能有多个版本）
     * - 专辑：0.5（较次要，因为同一首歌可能出现在多个专辑）
     * - 时长：0.8（重要但不是决定性，因为不同版本时长可能不同）
     *
     * **匹配等级**：
     * - PERFECT (≥99): 几乎完美匹配
     * - VERY_HIGH (≥95): 非常高匹配度
     * - PRETTY_HIGH (≥90): 相当高的匹配度
     * - HIGH (≥70): 高匹配度
     * - MEDIUM (≥30): 中等匹配度
     * - LOW (≥10): 低匹配度
     * - VERY_LOW (<10): 极低匹配度
     *
     * @return 匹配类型枚举值
     */
    fun compareTrack(
        searchTitle: String,
        searchArtist: String,
        resultTitle: String,
        resultArtist: String,
        searchAlbum: String? = null,
        resultAlbum: String? = null,
        searchDurationMs: Long? = null,
        resultDurationMs: Long? = null
    ): MatchType {
        // Step 1: 分别比较各个字段
        val titleMatch = compareName(searchTitle, resultTitle)  // 歌名匹配
        val artistMatch = compareArtists(searchArtist, resultArtist)  // 艺术家匹配
        val albumMatch = compareName(searchAlbum, resultAlbum)  // 专辑匹配
        val durationMatch = compareDuration(searchDurationMs, resultDurationMs)  // 时长匹配

        // Step 2: 设置权重（可配置）
        val titleWeight = 1.0      // 歌名权重
        val artistWeight = 1.0     // 艺术家权重
        val albumWeight = 0.5      // 专辑权重（较低）
        val durationWeight = 0.8   // 时长权重（中等）
        val maxSingle = 7.0        // 单项最高分

        // Step 3: 计算加权总分
        val totalScore = ((titleMatch?.score ?: 0) * titleWeight) +
                ((artistMatch?.score ?: 0) * artistWeight) +
                ((albumMatch?.score ?: 0) * albumWeight) +
                ((durationMatch?.score ?: 0) * durationWeight)

        // Step 4: 计算理论最高分
        val maxScore = (maxSingle * titleWeight) +
                (maxSingle * artistWeight) +
                (maxSingle * albumWeight) +
                (maxSingle * durationWeight)

        // 如果所有字段都为空，返回无匹配
        if (maxScore == 0.0) return MatchType.NONE

        // Step 5: 归一化为百分制分数
        val normalizedScore = (totalScore / maxScore * 100).toInt()

        // Step 6: 根据分数确定匹配等级
        return when {
            normalizedScore >= 99 -> MatchType.PERFECT
            normalizedScore >= 95 -> MatchType.VERY_HIGH
            normalizedScore >= 90 -> MatchType.PRETTY_HIGH
            normalizedScore >= 70 -> MatchType.HIGH
            normalizedScore >= 30 -> MatchType.MEDIUM
            normalizedScore >= 10 -> MatchType.LOW
            else -> MatchType.VERY_LOW
        }
    }

    /**
     * 比较名称（标题/专辑）
     *
     * 使用多级匹配策略来判断两个名称的相似度：
     * 1. 完全匹配：归一化后完全相同
     * 2. 子串匹配：一个包含另一个（如 "告白气球" vs "告白气球 (Live 版)"）
     * 3. 单词重叠：基于分词的重叠率（如 "A B C" vs "A B D"）
     * 4. 编辑距离：使用 Levenshtein 距离计算相似度
     *
     * **归一化处理**：
     * - 转小写：避免大小写差异
     * - 移除标点：去除特殊符号
     * - 规范空白：多个空格合并为一个
     *
     * @return 名称匹配类型，null 表示无法比较（输入为空）
     */
    private fun compareName(searchName: String?, resultName: String?): NameMatchType? {
        // Step 1: 空值检查
        if (searchName.isNullOrBlank() || resultName.isNullOrBlank()) return null

        // Step 2: 归一化处理（转小写、去标点、规范空白）
        val normalizedSearch = normalizeForComparison(searchName.lowercase())
        val normalizedResult = normalizeForComparison(resultName.lowercase())

        // Step 3: 完全匹配检查
        if (normalizedSearch == normalizedResult) return NameMatchType.PERFECT

        // Step 4: 子串匹配检查
        if (normalizedSearch.contains(normalizedResult)) return NameMatchType.VERY_HIGH
        if (normalizedResult.contains(normalizedSearch)) return NameMatchType.VERY_HIGH

        // Step 5: 分词并检查单词重叠率
        // 按空格、连字符、下划线分词
        val searchWords = normalizedSearch.split(" ", "-", "_").filter { it.isNotEmpty() }
        val resultWords = normalizedResult.split(" ", "-", "_").filter { it.isNotEmpty() }

        // 计算共同单词数量和重叠率
        val commonWords = searchWords.intersect(resultWords.toSet())
        if (commonWords.isNotEmpty()) {
            val overlapRatio = commonWords.size.toFloat() / maxOf(searchWords.size, resultWords.size)
            return when {
                overlapRatio >= 0.8 -> NameMatchType.VERY_HIGH  // 80% 以上单词相同
                overlapRatio >= 0.6 -> NameMatchType.HIGH       // 60% 以上单词相同
                overlapRatio >= 0.4 -> NameMatchType.MEDIUM     // 40% 以上单词相同
                else -> NameMatchType.LOW                       // 低于 40%
            }
        }

        // Step 6: 使用 Levenshtein 距离计算字符串相似度
        val similarity = calculateSimilarity(normalizedSearch, normalizedResult)
        return when {
            similarity > 0.90 -> NameMatchType.VERY_HIGH  // 90% 相似
            similarity > 0.75 -> NameMatchType.HIGH       // 75% 相似
            similarity > 0.50 -> NameMatchType.MEDIUM     // 50% 相似
            similarity > 0.30 -> NameMatchType.LOW        // 30% 相似
            else -> NameMatchType.NO_MATCH                // 低于 30%
        }
    }

    /**
     * 比较艺术家
     *
     * 支持多种艺术家分隔符的处理：
     * - 斜杠："周杰伦/林俊杰"
     * - 顿号："周杰伦、林俊杰"
     * - 逗号："Jay Chou, JJ Lin"
     * - 分号："周杰伦;林俊杰"
     *
     * 匹配逻辑：
     * 1. 归一化艺术家列表
     * 2. 检查是否完全相同（排序后比较）
     * 3. 检查是否有部分艺术家匹配
     * 4. 计算重叠率确定匹配等级
     *
     * @return 艺术家匹配类型，null 表示无法比较
     */
    private fun compareArtists(searchArtist: String?, resultArtist: String?): ArtistMatchType? {
        // Step 1: 空值检查
        if (searchArtist.isNullOrBlank() || resultArtist.isNullOrBlank()) return null

        // Step 2: 解析艺术家列表（处理多种分隔符）
        val searchArtists = normalizeArtistList(searchArtist)
        val resultArtists = normalizeArtistList(resultArtist)

        if (searchArtists.isEmpty() || resultArtists.isEmpty()) return null

        // Step 3: 完全匹配检查（排序后比较，忽略顺序差异）
        if (searchArtists.sorted() == resultArtists.sorted()) return ArtistMatchType.PERFECT

        // Step 4: 部分匹配检查
        val commonArtists = searchArtists.intersect(resultArtists.toSet())
        if (commonArtists.isNotEmpty()) {
            // 计算重叠率
            val overlapRatio = commonArtists.size.toFloat() / maxOf(searchArtists.size, resultArtists.size)
            return when {
                overlapRatio >= 0.8 -> ArtistMatchType.VERY_HIGH  // 80% 以上艺术家相同
                overlapRatio >= 0.6 -> ArtistMatchType.HIGH       // 60% 以上艺术家相同
                overlapRatio >= 0.4 -> ArtistMatchType.MEDIUM     // 40% 以上艺术家相同
                else -> ArtistMatchType.LOW                       // 低于 40%
            }
        }

        return ArtistMatchType.NO_MATCH
    }

    /**
     * 比较时长（毫秒）
     *
     * 使用固定容差范围来判断时长是否匹配：
     * - 完全相同：PERFECT
     * - 相差 ≤1 秒：VERY_HIGH
     * - 相差 ≤2 秒：HIGH
     * - 相差 ≤5 秒：MEDIUM（5 秒是最大容忍度）
     * - 相差 >5 秒：LOW
     *
     * **为什么时长不是决定性因素？**
     * - 不同版本的歌曲时长可能不同（Radio Edit、Extended Mix 等）
     * - 不同平台的时长数据可能有误差
     * - Live 版本通常比录音室版本长
     *
     * @return 时长匹配类型，null 表示无法比较（输入为 null 或 ≤0）
     */
    private fun compareDuration(searchDurationMs: Long?, resultDurationMs: Long?): DurationMatchType? {
        // 空值或无效值检查
        if (searchDurationMs == null || resultDurationMs == null || searchDurationMs <= 0 || resultDurationMs <= 0) {
            return null
        }

        // 计算时长差的值的绝对值
        val diff = abs(searchDurationMs - resultDurationMs)
        val maxDiff = 5000L // 5 秒最大容忍度

        // 根据差值确定匹配等级
        return when {
            diff == 0L -> DurationMatchType.PERFECT      // 完全相同
            diff <= 1000L -> DurationMatchType.VERY_HIGH // 相差 ≤1 秒
            diff <= 2000L -> DurationMatchType.HIGH      // 相差 ≤2 秒
            diff <= maxDiff -> DurationMatchType.MEDIUM  // 相差 ≤5 秒
            else -> DurationMatchType.LOW                // 相差 >5 秒
        }
    }

    /**
     * 规范化文本用于比较
     *
     * 这个函数会执行以下操作来标准化文本：
     * 1. 将多个连续空白字符合并为一个空格
     * 2. 移除所有标点符号（逗号、句号、括号等）
     * 3. 去除首尾空白
     *
     * 例如：
     * - "Hello   World!" → "hello world"
     * - "告白气球 (Live 版)" → "告白气球 live 版"
     * - "A, B & C" → "a b  c"
     *
     * @param text 原始文本
     * @return 规范化后的文本
     */
    private fun normalizeForComparison(text: String): String {
        return text
            .replace(Regex("\\s+"), " ")  // 规范空白：多个空格合并为一个
            .replace(Regex("[^\\w\\s]"), "")  // 移除标点符号和非单词字符
            .trim()  // 去除首尾空白
    }

    /**
     * 规范化艺术家列表
     *
     * 将艺术家字符串拆分为独立的艺术家名称列表。
     * 支持多种分隔符，适应不同地区和平台的习惯：
     * - 斜杠 [/]: 常见于华语音乐 "周杰伦/林俊杰"
     * - 顿号 [、]: 中文传统分隔符 "周杰伦、林俊杰"
     * - 逗号 [,]: 英文常用 "Jay Chou, JJ Lin"
     * - 分号 [;]: 某些平台使用 "周杰伦;林俊杰"
     *
     * 示例：
     * - "周杰伦/林俊杰" → ["周杰伦", "林俊杰"]
     * - "A, B & C" → ["A", "B & C"]（& 不会被拆分）
     *
     * @param artist 艺术家字符串
     * @return 规范化后的艺术家列表
     */
    private fun normalizeArtistList(artist: String): List<String> {
        // 使用正则表达式拆分多种分隔符，然后去除空白并过滤空字符串
        return artist.split(Regex("[/、,;]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    /**
     * 计算两个字符串的相似度（基于 Levenshtein 距离）
     *
     * Levenshtein 距离是一种衡量两个字符串差异的算法，定义为：
     * 将一个字符串转换成另一个字符串所需的最少单字符编辑操作次数。
     *
     * 允许的编辑操作包括：
     * - 插入一个字符
     * - 删除一个字符
     * - 替换一个字符
     *
     * 相似度计算公式：1.0 - (编辑距离 / 较长字符串长度)
     *
     * 示例：
     * - "kitten" → "sitting" = 距离 3（k→s, e→i, +g）
     * - "abc" → "abc" = 距离 0（完全相同）
     *
     * @return 相似度（0.0-1.0，1.0 表示完全相同）
     */
    private fun calculateSimilarity(s1: String, s2: String): Double {
        // 完全相同的情况直接返回 1.0
        if (s1 == s2) return 1.0

        // 找出较长和较短的字符串
        val longer = if (s1.length > s2.length) s1 else s2
        val shorter = if (s1.length > s2.length) s2 else s1

        // 空字符串特殊情况
        if (longer.isEmpty()) return 1.0

        // 计算编辑距离并转换为相似度
        val distance = levenshteinDistance(longer, shorter)
        return 1.0 - (distance.toDouble() / longer.length)
    }

    /**
     * 计算 Levenshtein 编辑距离（动态规划算法）
     *
     * 使用二维数组 dp[i][j] 表示 s1 的前 i 个字符和 s2 的前 j 个字符之间的编辑距离。
     *
     * **状态转移方程**：
     * ```
     * dp[i][j] = min(
     *     dp[i-1][j] + 1,      // 删除 s1[i]
     *     dp[i][j-1] + 1,      // 插入 s2[j]
     *     dp[i-1][j-1] + cost  // 替换（如果字符相同则 cost=0）
     * )
     * ```
     *
     * **额外优化**：支持相邻字符交换操作（Damerau-Levenshtein 距离）
     * 例如："ab" → "ba" 只需 1 次交换，而不是 2 次替换
     *
     * @return 编辑距离（数值越小表示越相似）
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length

        // 创建 (m+1) x (n+1) 的动态规划表
        val dp = Array(m + 1) { IntArray(n + 1) }

        // 初始化边界条件
        // dp[i][0] = i：将 s1 的前 i 个字符变为空串需要 i 次删除
        for (i in 0..m) dp[i][0] = i
        // dp[0][j] = j：将空串变为 s2 的前 j 个字符需要 j 次插入
        for (j in 0..n) dp[0][j] = j

        // 填充动态规划表
        for (i in 1..m) {
            for (j in 1..n) {
                // 计算当前字符的替换代价
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1

                // 取三种操作的最小值
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // 删除操作
                    dp[i][j - 1] + 1,      // 插入操作
                    dp[i - 1][j - 1] + cost // 替换操作
                )

                // 检查是否可以进行交换操作（Damerau-Levenshtein）
                // 条件：s1[i-1]==s2[j-2] 且 s1[i-2]==s2[j-1]
                if (i > 1 && j > 1 && s1[i - 1] == s2[j - 2] && s1[i - 2] == s2[j - 1]) {
                    dp[i][j] = minOf(dp[i][j], dp[i - 2][j - 2] + cost) // 交换操作
                }
            }
        }

        // 返回最终编辑距离
        return dp[m][n]
    }

    /**
     * 匹配评估结果
     *
     * @property confidence 置信度（0.0-1.0），1.0 表示完全匹配
     * @property matchType 匹配类型的字符串表示
     */
    data class MatchEvaluation(
        val confidence: Float,
        val matchType: String
    )

    /**
     * 名称匹配类型枚举
     *
     * @property score 分数（0-7），用于加权计算
     */
    internal enum class NameMatchType(val score: Int) {
        NO_MATCH(0),    // 无匹配
        LOW(2),         // 低匹配度
        MEDIUM(4),      // 中等匹配度
        HIGH(5),        // 高匹配度
        VERY_HIGH(6),   // 非常高匹配度
        PERFECT(7)      // 完美匹配
    }

    /**
     * 艺术家匹配类型枚举
     *
     * @property score 分数（0-7），用于加权计算
     */
    internal enum class ArtistMatchType(val score: Int) {
        NO_MATCH(0),    // 无匹配
        LOW(2),         // 低匹配度
        MEDIUM(4),      // 中等匹配度
        HIGH(5),        // 高匹配度
        VERY_HIGH(6),   // 非常高匹配度
        PERFECT(7)      // 完美匹配
    }

    /**
     * 时长匹配类型枚举
     *
     * @property score 分数（0-7），用于加权计算
     */
    internal enum class DurationMatchType(val score: Int) {
        NO_MATCH(0),    // 无匹配
        LOW(2),         // 低匹配度
        MEDIUM(4),      // 中等匹配度
        HIGH(5),        // 高匹配度
        VERY_HIGH(6),   // 非常高匹配度
        PERFECT(7)      // 完美匹配
    }
}