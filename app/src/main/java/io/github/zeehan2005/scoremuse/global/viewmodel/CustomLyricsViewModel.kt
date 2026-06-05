package io.github.zeehan2005.scoremuse.global.viewmodel




import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
//import io.github.zeehan2005.scoremuse.data.converter.XMLConverter
import io.github.zeehan2005.scoremuse.data.repository.LyricsRepository
import io.github.zeehan2005.scoremuse.data.repository.LyricsCacheRepository
import io.github.zeehan2005.scoremuse.components.ServiceLocator
import io.github.zeehan2005.scoremuse.data.ranking.LyricsCandidateRanker
import io.github.zeehan2005.scoremuse.global.LyricsSearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import androidx.compose.runtime.Stable
import dev.amll.droidmate.data.converter.TTMLConverter
import io.github.zeehan2005.scoremuse.data.parser.global.LyricsFormat
import io.github.zeehan2005.scoremuse.global.LyricsFeature
import java.util.concurrent.atomic.AtomicLong

/**
 * 自定义歌词候选数据结构
 * 
 * 表示从不同平台搜索到的歌词候选结果，
 * 用于在自定义歌词界面中展示给用户选择。
 * 
 * @param provider 歌词来源平台（如 "qq"、"netease"、"kugou"）
 * @param songId 平台歌曲 ID
 * @param title 歌曲标题
 * @param artist 艺术家名称
 * @param confidence 匹配置信度（0.0-1.0，越高越匹配）
 * @param matchType 匹配类型描述
 * @param displayName 显示名称（格式化后的完整标题）
 * @param metadataMatch 是否通过元数据（歌名/歌手）搜索得到的结果
 * @param features 特性集合（对唱/背景/重叠/翻译/音译/逐字/结构标记）
 * @param seq 单调递增序列号（用于区分到达顺序，打破平局）
 */
@Stable
data class CustomLyricsCandidate(
    val provider: String,
    val songId: String,
    val title: String,
    val artist: String,
    val confidence: Float,
    val matchType: String,
    val displayName: String,
    /**
     * 是否通过元数据（歌名/歌手）搜索得到的结果。
     */
    val metadataMatch: Boolean = false,
    /**
     * 特性集合，e.g. 对唱/背景/重叠/翻译/音译/逐字/结构标记
     * UI 需要在候选列表中显示这些功能。
     */
    val features: Set<LyricsFeature> = emptySet(),
    /**
     * Monotonic sequence assigned when candidate is first published.
     * Higher value means the candidate arrived later; used to break ties.
     */
    val seq: Long = 0L
)

/**
 * 自定义歌词视图模型
 * 
 * 这个类负责管理自定义歌词功能的 UI 状态和业务逻辑，包括：
 * - 搜索和显示歌词候选列表
 * - 用户选择歌词来源
 * - 缓存管理
 * - 多平台歌词源优先级排序
 * - 分页加载歌词结果
 * 
 * **支持的歌词源（按优先级）**：
 * 1. cache（本地缓存）- 最高优先级，快速响应
 * 3. kugou（酷狗音乐）- 中文歌词丰富
 * 4. netease/ncm（网易云音乐）- 独立音乐人多
 * 5. qq/qqmusic（QQ 音乐）- 版权库大
 * 
 * **设计思想**：
 * - 使用 Flow 实现响应式数据流
 * - 协程处理异步搜索和分页
 * - Mutex 保证并发安全
 * - 智能排序和去重
 */
class CustomLyricsViewModel @JvmOverloads constructor(
    application: Application,
    private val lyricsRepository: LyricsRepository = ServiceLocator.provideLyricsRepository(application.applicationContext),
    private val lyricsCacheRepository: LyricsCacheRepository = ServiceLocator.provideLyricsCacheRepository(application.applicationContext)
) : AndroidViewModel(application) {

    // ==================== 状态管理 ====================
    // 当前歌曲唯一标识（title + artist）
    // 用于区分不同歌曲的歌词搜索结果
    private var currentSongKey: String? = null
    // remember most recent search terms for pagination
    private var lastSearchTitle: String = ""
    private var lastSearchArtist: String = ""
    // 分页偏移量（每个平台独立记录）
    private val offsets = mutableMapOf<String, Int>()

    // ==================== 平台优先级配置 ====================
    // provider 固定优先级表已迁移到 LyricsCandidateRanker.providerPriority，
    // 排序规则统一在 data/ranking/LyricsCandidateRanker.kt 维护。

    // 当前正在播放的来源名称（用于打破平局）
    // 当多个歌词候选的置信度和特性完全相同时，优先显示当前播放源的歌词
    private var currentSourceName: String? = null

    /**
     * 更新当前播放源的名字（如播放器应用名）
     * 
     * 当多个歌词候选的置信度和特性完全相同时，
     * 会使用这个信息来决定优先显示哪个来源的歌词。
     * 
     * @param name 播放源名称（例如 "QQ 音乐"、"网易云音乐"）
     */
    fun updateCurrentSource(name: String?) {
        currentSourceName = name
    }

    // 候选比较逻辑（按以下优先级逐条判断）：
    // 1. 本地缓存最高
    // 2. 置信度降序 + 特性数降序
    // 3. AMLL DB 优先
    // 4. currentSourceName 相关性
    // 5. 固定 provider 优先级表
    // 6. seq 平局 tiebreak（VM 内部"到达顺序"概念）
    //
    // 实际规则统一在 LyricsCandidateRanker 中维护；本函数只做"VM 字段到 ranker"适配。
    internal fun compareCandidates(a: CustomLyricsCandidate, b: CustomLyricsCandidate): Int {
        val cmp = LyricsCandidateRanker.compare(
            a.toLyricsSearchResult(), a.features,
            b.toLyricsSearchResult(), b.features,
            currentSourceName
        )
        if (cmp != 0) return cmp
        // ranker 已统一平局语义；这里保留 VM 内部的"到达顺序"作为最终 tiebreak
        return a.seq.compareTo(b.seq)
    }

    // 把 VM 自己的 CustomLyricsCandidate 适配成 ranker 期望的 LyricsSearchResult。
    // 仅做字段拷贝，不做任何业务判断。
    private fun CustomLyricsCandidate.toLyricsSearchResult(): LyricsSearchResult =
        LyricsSearchResult(
            provider = provider,
            songId = songId,
            title = title,
            artist = artist,
            confidence = confidence,
            matchType = matchType,
            metadataMatch = metadataMatch
        )

    // comparator 委托给 compareCandidates
    internal val candidateComparator = Comparator<CustomLyricsCandidate> { a, b ->
        compareCandidates(a, b)
    }

    // 别名，保留旧调用点（之前 publishCandidate 使用 combinedComparator）
    internal val combinedComparator = candidateComparator

    private val _candidates = MutableStateFlow<List<CustomLyricsCandidate>>(emptyList())
    val candidates: StateFlow<List<CustomLyricsCandidate>> = _candidates

    // sequence generator for arrival order
    private val seqGenerator = AtomicLong()

    // helper used by searchCandidates and loadMore
    private fun publishCandidate(candidate: CustomLyricsCandidate) {
        val withSeq = candidate.copy(seq = seqGenerator.incrementAndGet())
        val currentList = _candidates.value.toMutableList()
        currentList.add(withSeq)
        // 只在列表大小发生变化时排序，减少排序频率
        if (currentList.size > 1) {
            currentList.sortWith(combinedComparator)
        }
        _candidates.value = currentList
    }

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _isApplying = MutableStateFlow(false)
    val isApplying: StateFlow<Boolean> = _isApplying

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _appliedLyricsText = MutableStateFlow<String?>(null)
    val appliedLyricsText: StateFlow<String?> = _appliedLyricsText

    // tag the origin of the applied lyrics so the caller can distinguish manual vs candidate
    private val _appliedLyricsSource = MutableStateFlow<String?>(null)
    val appliedLyricsSource: StateFlow<String?> = _appliedLyricsSource

    fun searchCandidates(title: String, artist: String) {
        if (title.isBlank() && artist.isBlank()) return

        // 更新当前歌曲唯一标识
        val songKey = "$title-$artist"
        currentSongKey = songKey
        // remember search terms for pagination and reset offsets
        lastSearchTitle = title
        lastSearchArtist = artist
        offsets.clear()

        viewModelScope.launch {
            _isSearching.value = true
            _errorMessage.value = null
            _candidates.value = emptyList()

            // NOTE: publishCandidate is now defined outside so it can also be
        // invoked from loadMore().

            try {
                // 缓存优先加入
                withContext(Dispatchers.IO) {
                    lyricsCacheRepository.findBySong(title, artist)
                }?.let { cached ->
                    if (currentSongKey == songKey) {
                        publishCandidate(
                            CustomLyricsCandidate(
                                provider = "cache",
                                songId = cached.id,
                                title = cached.title,
                                artist = cached.artist,
                                confidence = 1.0f,
                                matchType = "",
                                displayName = "本地缓存"
                            )
                        )
                    }
                }

                // 增量搜索：每个来源的第一条结果到达后都会回调一次
                lyricsRepository.searchLyricsIncremental(title, artist) { result ->
                    if (currentSongKey != songKey) return@searchLyricsIncremental
                    val candidate = result.toCandidate()
                    viewModelScope.launch {
                        publishCandidate(candidate)
                        // fetch features in background and re-sort when ready
                        val feats = runCatching {
                            withContext(Dispatchers.IO) {
                                lyricsRepository.getLyricsFeatures(
                                    candidate.provider,
                                    candidate.songId,
                                    candidate.title,
                                    candidate.artist
                                )
                            }
                        }.getOrDefault(emptySet())
                        if (currentSongKey == songKey) {
                            _candidates.value = _candidates.value
                                .map {
                                    if (it.provider.equals(candidate.provider, true) && it.songId == candidate.songId) {
                                        it.copy(features = feats)
                                    } else it
                                }
                            // 特性更新时不重新排序，因为特性数量对排序影响较小，减少排序频率
                        }
                    }
                }

            } catch (e: Exception) {
                Timber.e(e, "[LyricsViewModel] Failed to search candidates")
                _errorMessage.value = "搜索候选歌词失败: ${e.message}"
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun applyCandidate(candidate: CustomLyricsCandidate) {
        // 更新当前歌曲唯一标识
        val songKey = "${candidate.title}-${candidate.artist}"
        currentSongKey = songKey

        viewModelScope.launch {
            _isApplying.value = true
            _errorMessage.value = null
            try {
                if (candidate.provider == "cache") {
                    // 直接读取缓存内容
                    val cached = lyricsCacheRepository.findBySong(candidate.title, candidate.artist)
                    if (cached != null && cached.xmlContent.isNotBlank()) {
                        // 只在歌曲未切换时应用歌词
                        if (currentSongKey == songKey) {
                            _appliedLyricsSource.value = cached.source
                            _appliedLyricsText.value = cached.xmlContent
                        }
                    } else {
                        _errorMessage.value = "缓存歌词不存在或内容为空"
                    }
                } else {
                    // 传递候选歌词的 title 和 artist 以确保正确的元数据
                    val result = withContext(Dispatchers.IO) {
                        lyricsRepository.getLyrics(
                            candidate.provider,
                            candidate.songId,
                            candidate.title,
                            candidate.artist
                        )
                    }
                    if (result.isSuccess && result.lyrics != null) {
                        // 转换为TTML格式以保留words数组(逐词同步数据)
                        // 只在歌曲未切换时应用歌词
                        if (currentSongKey == songKey) {
                            // format the source string with provider-specific rules
                            _appliedLyricsSource.value = autoSourceForCandidate(
                                provider = candidate.provider,
                                title = candidate.title,
                                artist = candidate.artist,
                                id = candidate.songId
                            )
                            // 优先使用原始歌词内容，以便利用前端的 WASM 解析器获得更精确的效果
                            _appliedLyricsText.value = result.lyrics.rawContent ?: TTMLConverter.toTTMLString(result.lyrics)
                        }
                    } else {
                        _errorMessage.value = result.errorMessage ?: "应用候选歌词失败"
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "[LyricsViewModel] Failed to apply candidate")
                _errorMessage.value = "应用候选歌词失败: ${e.message}"
            } finally {
                _isApplying.value = false
            }
        }
    }

    companion object {

        /**
         * Given raw lyrics input return an appropriate "source" label that will
         * later be stored in the view model / message intent.  Files are tagged
         * with their detected format extension (ttml/lrc/etc); free‑form plain
         * text is still labelled as "manual" for backwards compatibility.
         */
        fun sourceFromInput(input: String): String {
            val format = LyricsFormat.detect(input)
            return if (format == LyricsFormat.PLAIN_TEXT) {
                "manual"
            } else {
                format.extension
            }
        }

        /**
         * Constructs the source string for lyrics obtained from an external
         * provider (candidate or cache) – always marks it as auto‑recognized.
         */
        fun autoSourceForCandidate(
            provider: String,
            title: String,
            artist: String,
            id: String?
        ): String {
            // every provider uses the same template: 服务商：歌曲名 - 歌手名(id)
            val providerName = providerDisplayName(provider)
            return "$providerName：$title - $artist(${id ?: ""})"
        }

        /**
         * Human-friendly name for a lyrics provider.
         *
         * If an ID is supplied (e.g. songId) it will be appended in parentheses
         * for providers where that makes sense.
         */
        private fun providerDisplayName(provider: String): String {
            // The UI list only shows a friendly name; IDs should not be
            // appended directly after the provider name.  Previously we added
            // the songId here which caused the title to read
            // That was confusing and the ID is still
            // surfaced elsewhere if needed so drop it from the display name.
            val base = when (provider.lowercase()) {
                "netease", "ncm" -> "网易云音乐"
                "qq", "qqmusic" -> "QQ音乐"
                "kugou" -> "酷狗音乐"
                "amll" -> "AMLL TTML DB"
                else -> provider.uppercase()
            }
            return base
        }

    }

    /**
     * Load an additional batch of candidates for a given provider.  The
     * view model maintains offset state so repeated calls page through
     * the results in groups of three.
     */
    fun loadMore(provider: String) {
        val title = lastSearchTitle
        val artist = lastSearchArtist
        if (title.isBlank() && artist.isBlank()) return
        viewModelScope.launch {
            // after repository change each search returns max 3 candidates; offsets
            // can still be used to track how many we have shown locally but the
            // data source itself does not support pagination.  we therefore ignore
            // the stored offset when querying and instead update it afterwards.
            val newResults = withContext(Dispatchers.IO) {
                when (provider.lowercase()) {
                    "qq", "qqmusic" -> lyricsRepository.searchQQMusic(title, artist)
                    "netease", "ncm" -> lyricsRepository.searchNetease(title, artist)
                    "kugou" -> lyricsRepository.searchKugou(title, artist)
                    else -> emptyList()
                }
            }
            val start = offsets.getOrDefault(provider, 0)
            offsets[provider] = start + newResults.size
            for (r in newResults) {
                publishCandidate(r.toCandidate())
            }
        }
    }

    fun applyManualInput(input: String, title: String, artist: String) {
        viewModelScope.launch {
            _isApplying.value = true
            _errorMessage.value = null
            try {
                val format = LyricsFormat.detect(input)
                    
                // 如果是已识别的格式（非纯文本），直接传递原文本以便前端 WASM 解析
                if (format != LyricsFormat.PLAIN_TEXT) {
                    _appliedLyricsSource.value = sourceFromInput(input)
                    _appliedLyricsText.value = input.trim()
                } else {
                    // 对于纯文本，仍然进行一次转换以确保基本结构
                    val parsed = TTMLConverter.fromLyrics(
                        content = input,
                        title = title.ifBlank { "自选歌词" },
                        artist = artist.ifBlank { "Unknown" }
                    )
                    if (parsed != null) {
                        _appliedLyricsSource.value = "manual"
                        _appliedLyricsText.value = TTMLConverter.toTTMLString(parsed)
                    } else {
                        _errorMessage.value = "无法识别歌词格式"
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "[LyricsViewModel] Failed to parse manual lyrics")
                _errorMessage.value = "解析歌词失败：${e.message}"
            } finally {
                _isApplying.value = false
            }
        }
    }

    fun consumeAppliedLyricsText() {
        _appliedLyricsText.value = null
        _appliedLyricsSource.value = null
    }

    private fun LyricsSearchResult.toCandidate(): CustomLyricsCandidate {
        // matchType may contain verbose labels such as PERFECT/VERY_HIGH
        // those are not useful in the UI, so clear them.
        val displayName = if (provider.equals("amll", true) && metadataMatch) {
            "AMLL TTML DB (基于歌名)"
        } else {
            providerDisplayName(provider)
        }
        return CustomLyricsCandidate(
            provider = provider,
            songId = songId,
            title = title,
            artist = artist,
            confidence = confidence,
            matchType = "",
            displayName = displayName,
            metadataMatch = metadataMatch
        )
    }


    override fun onCleared() {
        // Do not close the shared httpClient here as it's a global singleton from ServiceLocator
    }
}