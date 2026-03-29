package com.amll.droidmate.domain.matcher

import com.amll.droidmate.data.repository.LyricsRepository.MatchEvaluation
import com.amll.droidmate.data.repository.LyricsRepository.MatchType

/**
 * 歌曲匹配评估器
 * 
 * 提供统一的歌曲信息匹配和相似度评估功能，用于歌词搜索和自动选择
 * 
 * @see LyricsRepository.evaluateMatch
 */
object TrackMatcher {
    
    /**
     * 搜索查询条件
     * 
     * @param title 歌曲标题
     * @param artist 艺术家名称
     * @param album 专辑名称（可选）
     * @param durationMs 歌曲时长（毫秒，可选）
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
     * @param title 歌曲标题
     * @param artist 艺术家名称
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
     * @param query 搜索查询条件
     * @param result 候选歌曲信息
     * @return 匹配评估结果，包含置信度和匹配类型
     */
    fun evaluate(query: SearchQuery, result: ResultTrack): LyricsMatcher.MatchEvaluation {
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
     * @param searchTitle 搜索标题
     * @param searchArtist 搜索艺术家
     * @param resultTitle 结果标题
     * @param resultArtist 结果艺术家
     * @return 匹配类型
     */
    fun quickMatch(
        searchTitle: String,
        searchArtist: String,
        resultTitle: String,
        resultArtist: String
    ): MatchType {
        return LyricsMatcher.compareTrack(
            searchTitle = searchTitle,
            searchArtist = searchArtist,
            resultTitle = resultTitle,
            resultArtist = resultArtist,
            searchAlbum = null,
            resultAlbum = null,
            searchDurationMs = null,
            resultDurationMs = null
        )
    }
}
