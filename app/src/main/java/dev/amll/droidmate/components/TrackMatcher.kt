package dev.amll.droidmate.components

import io.github.zeehan2005.scoremuse.data.repository.LyricsRepository.MatchType

/**
 * 歌曲匹配评估器
 *
 * 这个对象提供了一个更高级、更易用的接口来评估歌曲匹配度。
 * 它封装了 [LyricsMatcher] 的复杂逻辑，使用数据类来组织参数，
 * 使调用代码更加清晰和类型安全。
 *
 * **主要功能**：
 * 1. [evaluate] - 完整的匹配评估（支持所有字段）
 * 2. [quickMatch] - 快速匹配（仅比较歌名和艺术家）
 *
 * **使用场景**：
 * - 歌词搜索结果排序
 * - 自动选择最佳匹配
 * - 识别重复歌曲
 *
 * @see LyricsMatcher.evaluateMatch 底层实现
 */
object TrackMatcher {

    /**
     * 搜索查询条件
     *
     * 这个数据类封装了搜索一首歌所需的所有信息。
     * 使用数据类的好处是：
     * - 类型安全：编译器会检查参数
     * - 可读性强：参数有明确的名称
     * - 易于传递：一个对象包含所有信息
     *
     * @param title 歌曲标题（必需）
     * @param artist 艺术家名称（必需）
     * @param album 专辑名称（可选，用于提高匹配准确度）
     * @param durationMs 歌曲时长（毫秒，可选，用于辅助验证）
     */
    data class SearchQuery(
        val title: String,
        val artist: String,
        val album: String? = null,
        val durationMs: Long? = null
    )

    /**
     * 候选歌曲信息
     *
     * 这个数据类表示从某个平台搜索到的一首候选歌曲。
     * 它与 [SearchQuery] 结构相同，但语义上代表"结果"而非"查询"。
     *
     * @param title 歌曲标题（必需）
     * @param artist 艺术家名称（必需）
     * @param album 专辑名称（可选）
     * @param durationMs 歌曲时长（毫秒，可选）
     */
    data class ResultTrack(
        val title: String,
        val artist: String,
        val album: String? = null,
        val durationMs: Long? = null
    )

    /**
     * 评估搜索查询与候选歌曲的匹配度
     *
     * 这是主要的匹配方法，会综合考虑歌名、艺术家、专辑和时长四个维度。
     * 算法会返回一个详细的评估报告，包括置信度和匹配类型。
     *
     * **使用示例**：
     * ```kotlin
     * val query = SearchQuery(
     *     title = "告白气球",
     *     artist = "周杰伦",
     *     album = "周杰伦的床边故事",
     *     durationMs = 215000L
     * )
     *
     * val result = ResultTrack(
     *     title = "告白气球",
     *     artist = "周杰伦",
     *     album = "周杰伦的床边故事",
     *     durationMs = 216000L
     * )
     *
     * val evaluation = TrackMatcher.evaluate(query, result)
     * // evaluation.confidence = 0.99 (99% 置信度)
     * // evaluation.matchType = PERFECT
     * ```
     *
     * @param query 搜索查询条件
     * @param result 候选歌曲信息
     * @return 匹配评估结果，包含置信度（0.0-1.0）和匹配类型
     */
    fun evaluate(query: SearchQuery, result: ResultTrack): LyricsMatcher.MatchEvaluation {
        // 调用 LyricsMatcher 的核心方法，将数据类解构为独立参数
        return LyricsMatcher.evaluateMatch(
            searchTitle = query.title,
            searchArtist = query.artist,
            resultTitle = result.title,
            resultArtist = result.artist,
            searchAlbum = query.album,
            resultAlbum = result.album,
            searchDurationMs = query.durationMs,
            resultDurationMs = result.durationMs
        )
    }

    /**
     * 快速评估标题和艺术家的匹配度
     *
     * 这是一个便捷方法，适用于只需要快速比较歌名和艺术家的场景。
     * 它会忽略专辑和时长信息，只基于两个核心字段进行判断。
     *
     * **适用场景**：
     * - 初步筛选：在显示详细结果前快速过滤
     * - 简单匹配：用户只提供了基本信息
     * - 性能优化：避免不必要的计算
     *
     * **注意**：由于缺少专辑和时长的验证，匹配准确度可能不如 [evaluate] 方法。
     *
     * @param searchTitle 搜索时的歌名
     * @param searchArtist 搜索时的艺术家
     * @param resultTitle 候选结果的歌名
     * @param resultArtist 候选结果的艺术家
     * @return 匹配类型枚举值
     */
    fun quickMatch(
        searchTitle: String,
        searchArtist: String,
        resultTitle: String,
        resultArtist: String
    ): MatchType {
        // 调用 LyricsMatcher 的比较方法，忽略专辑和时长
        return LyricsMatcher.compareTrack(
            searchTitle = searchTitle,
            searchArtist = searchArtist,
            resultTitle = resultTitle,
            resultArtist = resultArtist,
            searchAlbum = null,      // 不比较专辑
            resultAlbum = null,
            searchDurationMs = null, // 不比较时长
            resultDurationMs = null
        )
    }
}