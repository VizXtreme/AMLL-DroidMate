package com.amll.droidmate.domain.matcher

import com.amll.droidmate.data.repository.LyricsRepository.MatchType

/**
 * 歌词匹配工具类
 * 提供歌曲信息匹配的静态方法
 */
object LyricsMatcher {
    
    /**
     * 评估搜索查询与候选歌曲的匹配度
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
        
        return MatchEvaluation(
            confidence = (matchType.score / 100f).coerceIn(0f, 1f),
            matchType = matchType.name
        )
    }
    
    /**
     * 比较歌曲信息，返回匹配类型
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
        val titleMatch = compareName(searchTitle, resultTitle)
        val artistMatch = compareArtists(searchArtist, resultArtist)
        val albumMatch = compareName(searchAlbum, resultAlbum)
        val durationMatch = compareDuration(searchDurationMs, resultDurationMs)
        
        // weights can be tweaked later or exposed via configuration
        val titleWeight = 1.0
        val artistWeight = 1.0
        val albumWeight = 0.5   // slightly higher importance for album
        val durationWeight = 0.8 // use duration but not dominant
        val maxSingle = 7.0
        
        val totalScore = ((titleMatch?.score ?: 0) * titleWeight) +
                ((artistMatch?.score ?: 0) * artistWeight) +
                ((albumMatch?.score ?: 0) * albumWeight) +
                ((durationMatch?.score ?: 0) * durationWeight)
        
        val maxScore = (maxSingle * titleWeight) +
                (maxSingle * artistWeight) +
                (maxSingle * albumWeight) +
                (maxSingle * durationWeight)
        
        if (maxScore == 0.0) return MatchType.NONE
        
        val normalizedScore = (totalScore / maxScore * 100).toInt()
        
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
     */
    private fun compareName(searchName: String?, resultName: String?): NameMatchType? {
        if (searchName.isNullOrBlank() || resultName.isNullOrBlank()) return null
        
        val normalizedSearch = normalizeForComparison(searchName.lowercase())
        val normalizedResult = normalizeForComparison(resultName.lowercase())
        
        if (normalizedSearch == normalizedResult) return NameMatchType.PERFECT
        
        // Check for substring matches
        if (normalizedSearch.contains(normalizedResult)) return NameMatchType.VERY_HIGH
        if (normalizedResult.contains(normalizedSearch)) return NameMatchType.VERY_HIGH
        
        // Check for significant overlap
        val searchWords = normalizedSearch.split(" ", "-", "_").filter { it.isNotEmpty() }
        val resultWords = normalizedResult.split(" ", "-", "_").filter { it.isNotEmpty() }
        
        val commonWords = searchWords.intersect(resultWords.toSet())
        if (commonWords.isNotEmpty()) {
            val overlapRatio = commonWords.size.toFloat() / maxOf(searchWords.size, resultWords.size)
            return when {
                overlapRatio >= 0.8 -> NameMatchType.VERY_HIGH
                overlapRatio >= 0.6 -> NameMatchType.HIGH
                overlapRatio >= 0.4 -> NameMatchType.MEDIUM
                else -> NameMatchType.LOW
            }
        }
        
        // Fallback to similarity check
        val similarity = calculateSimilarity(normalizedSearch, normalizedResult)
        return when {
            similarity > 0.90 -> NameMatchType.VERY_HIGH
            similarity > 0.75 -> NameMatchType.HIGH
            similarity > 0.50 -> NameMatchType.MEDIUM
            similarity > 0.30 -> NameMatchType.LOW
            else -> NameMatchType.NO_MATCH
        }
    }
    
    /**
     * 比较艺术家
     */
    private fun compareArtists(searchArtist: String?, resultArtist: String?): ArtistMatchType? {
        if (searchArtist.isNullOrBlank() || resultArtist.isNullOrBlank()) return null
        
        val searchArtists = normalizeArtistList(searchArtist)
        val resultArtists = normalizeArtistList(resultArtist)
        
        if (searchArtists.isEmpty() || resultArtists.isEmpty()) return null
        
        // Perfect match if identical after normalization
        if (searchArtists.sorted() == resultArtists.sorted()) return ArtistMatchType.PERFECT
        
        // Check if any artist matches
        val commonArtists = searchArtists.intersect(resultArtists.toSet())
        if (commonArtists.isNotEmpty()) {
            val overlapRatio = commonArtists.size.toFloat() / maxOf(searchArtists.size, resultArtists.size)
            return when {
                overlapRatio >= 0.8 -> ArtistMatchType.VERY_HIGH
                overlapRatio >= 0.6 -> ArtistMatchType.HIGH
                overlapRatio >= 0.4 -> ArtistMatchType.MEDIUM
                else -> ArtistMatchType.LOW
            }
        }
        
        return ArtistMatchType.NO_MATCH
    }
    
    /**
     * 比较时长（毫秒）
     */
    private fun compareDuration(searchDurationMs: Long?, resultDurationMs: Long?): DurationMatchType? {
        if (searchDurationMs == null || resultDurationMs == null || searchDurationMs <= 0 || resultDurationMs <= 0) {
            return null
        }
        
        val diff = kotlin.math.abs(searchDurationMs - resultDurationMs)
        val maxDiff = 5000L // 5 seconds tolerance
        
        return when {
            diff == 0L -> DurationMatchType.PERFECT
            diff <= 1000L -> DurationMatchType.VERY_HIGH  // 1 second
            diff <= 2000L -> DurationMatchType.HIGH       // 2 seconds
            diff <= maxDiff -> DurationMatchType.MEDIUM
            else -> DurationMatchType.LOW
        }
    }
    
    /**
     * 规范化文本用于比较
     */
    private fun normalizeForComparison(text: String): String {
        return text
            .replace(Regex("\\s+"), " ")  // Normalize whitespace
            .replace(Regex("[^\\w\\s]"), "")  // Remove punctuation
            .trim()
    }
    
    /**
     * 规范化艺术家列表
     */
    private fun normalizeArtistList(artist: String): List<String> {
        return artist.split(Regex("[/、,;]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
    
    /**
     * 计算两个字符串的相似度（基于 Levenshtein 距离）
     */
    private fun calculateSimilarity(s1: String, s2: String): Double {
        if (s1 == s2) return 1.0
        
        val longer = if (s1.length > s2.length) s1 else s2
        val shorter = if (s1.length > s2.length) s2 else s1
        
        if (longer.isEmpty()) return 1.0
        
        val distance = levenshteinDistance(longer, shorter)
        return 1.0 - (distance.toDouble() / longer.length)
    }
    
    /**
     * 计算 Levenshtein 编辑距离
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length
        
        val dp = Array(m + 1) { IntArray(n + 1) }
        
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        
        for (i in 1..m) {
            for (j in 1..n) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
                
                if (i > 1 && j > 1 && s1[i - 1] == s2[j - 2] && s1[i - 2] == s2[j - 1]) {
                    dp[i][j] = minOf(dp[i][j], dp[i - 2][j - 2] + cost) // transposition
                }
            }
        }
        
        return dp[m][n]
    }
    
    /**
     * 匹配评估结果
     */
    data class MatchEvaluation(
        val confidence: Float,
        val matchType: String
    )
    
    /**
     * 名称匹配类型
     */
    internal enum class NameMatchType(val score: Int) {
        NO_MATCH(0), LOW(2), MEDIUM(4), HIGH(5), VERY_HIGH(6), PERFECT(7)
    }
    
    /**
     * 艺术家匹配类型
     */
    internal enum class ArtistMatchType(val score: Int) {
        NO_MATCH(0), LOW(2), MEDIUM(4), HIGH(5), VERY_HIGH(6), PERFECT(7)
    }
    
    /**
     * 时长匹配类型
     */
    internal enum class DurationMatchType(val score: Int) {
        NO_MATCH(0), LOW(2), MEDIUM(4), HIGH(5), VERY_HIGH(6), PERFECT(7)
    }
}
