package io.github.zeehan2005.scoremuse.data.ranking

import io.github.zeehan2005.scoremuse.global.LyricsFeature
import io.github.zeehan2005.scoremuse.global.LyricsSearchResult

/**
 * 共享的歌词候选排序器。
 *
 * 统一以下两个调用方的排序规则，避免出现"自动选的词"和"手动选的词"体验割裂：
 *  - [io.github.zeehan2005.scoremuse.data.repository.LyricsRepository.fetchLyricsAuto]
 *    （同步等待所有 features ready 后排序取 top）
 *  - [io.github.zeehan2005.scoremuse.global.viewmodel.CustomLyricsViewModel.searchCandidates]
 *    （流式排序，features 异步到达时实时重排）
 *
 * 排序规则（从高到低）：
 *  1. 本地缓存（provider == "cache"）最高优先级
 *  2. 置信度降序
 *  3. 特性数降序（仅当双方 features 均已探测）
 *  4. AMLL DB 结果优先于其他来源
 *  5. AMLL 内部：ID 匹配 > 元数据匹配
 *  6. 当前播放源偏好（如播放源含 "qq" 则 QQ/Kugou 候选靠前）
 *  7. AMLL 内部 platform 前缀与当前播放源匹配
 *  8. provider 固定优先级；若双方都是 QQ/Kugou 且当前源是腾讯系，使用 TME 互斥规则
 *  9. 平局保留原顺序（依赖稳定排序）
 *
 * 所有函数都是纯函数（无副作用，不持有状态），便于在 VM / Repository / 测试中复用。
 */
object LyricsCandidateRanker {

    private val providerPriority = mapOf(
        "cache" to -1,   // 本地缓存（速度最快，无需网络）
        "amll" to 0,     // AMLL TTML DB
        "kugou" to 1,    // 酷狗
        "netease" to 2,  // 网易云
        "ncm" to 2,      // 网易云别名
        "qq" to 3,       // QQ 音乐
        "qqmusic" to 3   // QQ 音乐别名
    )

    /**
     * 纯比较函数。返回负值表示 a 应该排在 b 前面。
     *
     * @param aFeatures a 的特性集合；传 `null` 表示尚未探测（不参与 features 排序）
     * @param bFeatures b 的特性集合；同上
     * @param currentSourceName 当前播放源名称（如应用名），可选
     */
    fun compare(
        a: LyricsSearchResult, aFeatures: Set<LyricsFeature>?,
        b: LyricsSearchResult, bFeatures: Set<LyricsFeature>?,
        currentSourceName: String?
    ): Int {
        /** 1. cache 优先 */
        val aCache = a.provider.equals("cache", true)
        val bCache = b.provider.equals("cache", true)
        if (aCache != bCache) return if (aCache) -1 else 1

        /** 2. 置信度降序 */
        val confDiff = a.confidence - b.confidence
        if (confDiff != 0f) return -confDiff.compareTo(0f)

        // 3. features 数降序（仅当双方都已探测）
        if (aFeatures != null && bFeatures != null) {
            val featDiff = bFeatures.size - aFeatures.size
            if (featDiff != 0) return featDiff
        }

        /** 4. AMLL 优先于其他来源 */
        val aAml = a.provider.equals("amll", true)
        val bAml = b.provider.equals("amll", true)
        if (aAml != bAml) return if (aAml) -1 else 1

        // 5. AMLL 内部：ID 匹配 > 元数据匹配
        if (aAml) {
            if (a.metadataMatch != b.metadataMatch) {
                return if (!a.metadataMatch) -1 else 1
            }
        }

        // 6/7. 当前播放源偏好 + AMLL 前缀匹配
        if (!currentSourceName.isNullOrBlank()) {
            val lower = currentSourceName.lowercase()
            val preferred = when {
                lower.contains("qq") && !lower.contains("酷狗") -> setOf("qq", "qqmusic")
                lower.contains("酷狗") && !lower.contains("qq") -> setOf("kugou")
                lower.contains("qq") && lower.contains("酷狗") -> setOf("qq", "qqmusic", "kugou")
                lower.contains("网易") -> setOf("netease", "ncm")
                else -> emptySet()
            }
            if (preferred.isNotEmpty()) {
                val aIn = preferred.contains(a.provider.lowercase())
                val bIn = preferred.contains(b.provider.lowercase())
                if (aIn != bIn) return if (aIn) -1 else 1
            }

            fun amllMatch(r: LyricsSearchResult): Boolean {
                if (!r.provider.equals("amll", true)) return false
                val parts = r.songId.split(":", limit = 2)
                if (parts.size < 2) return false
                val prefix = parts[0].lowercase()
                return when {
                    lower.contains("网易") -> prefix == "netease" || prefix == "ncm"
                    lower.contains("qq") -> prefix == "qq" || prefix == "qqmusic"
                    lower.contains("酷狗") -> prefix == "kugou"
                    else -> false
                }
            }
            if (amllMatch(a) != amllMatch(b)) {
                return if (amllMatch(a)) -1 else 1
            }
        }

        /** 8. provider 固定优先级 / TME 互斥规则 */
        val lowerA = a.provider.lowercase()
        val lowerB = b.provider.lowercase()
        val bothTme = lowerA in setOf("qq", "kugou") && lowerB in setOf("qq", "kugou")
        val tmeSource = !currentSourceName.isNullOrBlank() &&
            (currentSourceName.contains("qq", ignoreCase = true) || currentSourceName.contains("酷狗"))
        if (bothTme && tmeSource) {
            val lowerSource = currentSourceName.lowercase()
            val preferKugou = lowerSource.contains("酷狗") && !lowerSource.contains("qq")
            val preferQQ = lowerSource.contains("qq") && !lowerSource.contains("酷狗")
            if (preferKugou) {
                if (lowerA == "kugou" && lowerB == "qq") return -1
                if (lowerA == "qq" && lowerB == "kugou") return 1
            } else if (preferQQ) {
                if (lowerA == "qq" && lowerB == "kugou") return -1
                if (lowerA == "kugou" && lowerB == "qq") return 1
            }
            /** 都是 TME 但都不偏向某一边时落入 providerPriority */
            val pa = providerPriority[lowerA] ?: Int.MAX_VALUE
            val pb = providerPriority[lowerB] ?: Int.MAX_VALUE
            if (pa != pb) return pa - pb
        } else {
            val pa = providerPriority[lowerA] ?: Int.MAX_VALUE
            val pb = providerPriority[lowerB] ?: Int.MAX_VALUE
            if (pa != pb) return pa - pb
        }

        // 9. 平局保留原顺序
        return 0
    }

    /**
     * 同步排序一个候选列表。
     *
     * 用于 `fetchLyricsAuto` 等"先收集完整 features 再排序"的同步场景。
     *
     * @param candidates 待排序候选
     * @param featuresByKey 每个候选对应的 features 集合；key 格式为 `provider:songId`。
     *                      未在 map 中找到的候选按"features 未知"处理（不参与 features 排序）。
     * @param currentSourceName 当前播放源名称（可选）
     * @return 排序后的新列表（不修改原列表）
     */
    fun rank(
        candidates: List<LyricsSearchResult>,
        featuresByKey: Map<String, Set<LyricsFeature>>,
        currentSourceName: String?
    ): List<LyricsSearchResult> {
        if (candidates.size <= 1) return candidates
        val decorated = candidates.map { it to featuresByKey[keyOf(it)] }
        return decorated.sortedWith(
            Comparator { x, y ->
                compare(x.first, x.second, y.first, y.second, currentSourceName)
            }
        ).map { it.first }
    }

    /**
     * 用于在 [featuresByKey] 中查找某个候选对应的 features。
     * 公开给调用方以保证 key 格式一致。
     */
    fun keyOf(result: LyricsSearchResult): String =
        "${result.provider.lowercase()}:${result.songId}"
}
