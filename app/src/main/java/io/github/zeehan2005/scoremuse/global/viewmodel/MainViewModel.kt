package io.github.zeehan2005.scoremuse.global.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.amll.droidmate.data.converter.TTMLConverter
import dev.amll.droidmate.global.AMLLSettings
import io.github.zeehan2005.scoremuse.data.parser.global.SongStructureParser
import io.github.zeehan2005.scoremuse.data.repository.LyricsRepository
import io.github.zeehan2005.scoremuse.components.ServiceLocator
import io.github.zeehan2005.scoremuse.global.NowPlayingMusic
import io.github.zeehan2005.scoremuse.global.SongStructure
import io.github.zeehan2005.scoremuse.global.UnifiedLyrics
import io.github.zeehan2005.scoremuse.service.LyricNotificationManager
import io.github.zeehan2005.scoremuse.service.MediaInfoService
import io.github.zeehan2005.scoremuse.global.AppSettings
import io.github.zeehan2005.scoremuse.ui.getAppNameFromPackage
import io.github.zeehan2005.scoremuse.components.AudioDeviceHelper
import io.github.zeehan2005.scoremuse.data.parser.global.LyricsFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Locale

/**
 * 主视图模型 - 应用的核心状态管理器
 * 
 * 这是整个应用的大脑，负责协调和管理所有核心功能：
 * 1. 媒体播放监听：获取当前播放的歌曲信息
 * 2. 歌词管理：搜索、解析、缓存歌词
 * 4. 动态主题：根据专辑封面调整 UI 配色
 * 5. 通知管理：显示歌词通知
 * 6. 设备适配：为不同音频设备应用时间偏移
 * 
 * **架构设计**：
 * - 使用 Flow 实现响应式数据流
 * - 协程处理异步操作
 * - ServiceLocator 集中管理依赖
 * - 单一事实来源（Single Source of Truth）
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext

    // make this mutable so tests can inject a fake repository if needed

    internal var lyricsRepository = ServiceLocator.provideLyricsRepository(context)
    private val lyricsCacheRepository = ServiceLocator.provideLyricsCacheRepository(context)
    
    // ==================== 服务 ====================
    // 媒体信息监听服务（监听系统媒体播放状态）
    private val mediaInfoService = MediaInfoService(context)

    
    // 用于跟踪上次发送的歌词，避免重复发送相同内容
    private var lastSentLyricsHash: Int = 0

    /**
     * Real notification manager; tests can replace via the internal var below.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var lyricNotificationManager: LyricNotificationManager =
        LyricNotificationManager(context)

    // UI State
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val nowPlayingMusicMutable = MutableStateFlow<NowPlayingMusic?>(null)
    val nowPlayingMusic: StateFlow<NowPlayingMusic?> = nowPlayingMusicMutable
    
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val lyricsMutable = MutableStateFlow<UnifiedLyrics?>(null)
    val lyrics: StateFlow<UnifiedLyrics?> = lyricsMutable
    
    // 歌曲结构信息
    private val _songStructures = MutableStateFlow<List<SongStructure>>(emptyList())
    val songStructures: StateFlow<List<SongStructure>> = _songStructures

    private val _isSongStructureBarEnabled = MutableStateFlow(AppSettings.isSongStructureBarEnabled(context))
    val isSongStructureBarEnabled: StateFlow<Boolean> = _isSongStructureBarEnabled
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)

    // 跟踪当前 fetchLyrics 协程任务，以便在 isLoading 变 false 时主动停止
    // fetchLyricsAuto 调用以及任何尚未完成的子任务（例如进行中的网络请求）。
    @Volatile
    private var fetchLyricsJob: Job? = null

    // tracks whether we've already shown the paused notification. After a
    // pause occurs we'll send one update with ongoing=false, then refrain from
    // sending additional updates until playback resumes.  This ignores whether
    // the user actually swipes it away.
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var pausedNotificationSent: Boolean = false

    private val deleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LyricNotificationManager.ACTION_LYRIC_NOTIFICATION_DISMISSED) {
                Timber.i("[NotificationListener] Lyric notification dismissed by user")
                lyricNotificationManager.cancel()
            }
        }
    }

    private val preferenceListener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "song_structure_bar_enabled") {
            _isSongStructureBarEnabled.value = AppSettings.isSongStructureBarEnabled(context)
        }
    }

    init {
        setupMediaListener()
        observeLyricNotification()
        Timber.plant(Timber.DebugTree())

        // 监听设置变化
        val prefs = context.getSharedPreferences("ScoreMuse_settings", Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(preferenceListener)

        // 监听歌词状态变化
        viewModelScope.launch {
            lyricsMutable.collect { lyrics ->
                // 输出歌词状态变化日志
                Timber.d("[MainViewModel] Lyrics state changed:")
                Timber.d("[MainViewModel]   Lyrics: ${lyrics?.let { "${it.lines.size} lines" } ?: "null"}")
                if (lyrics != null) {
                    Timber.d("[MainViewModel]   Title: ${lyrics.metadata.title}")
                    Timber.d("[MainViewModel]   Artist: ${lyrics.metadata.artist}")
                    Timber.d("[MainViewModel]   Has song structures: ${lyrics.metadata.songStructures?.isNotEmpty() ?: false}")
                }
            }
        }

        // listen for user dismissals. Android 13+ requires an explicit export flag
        // when registering receivers that aren't for system broadcasts.
        ContextCompat.registerReceiver(
            context,
            deleteReceiver,
            IntentFilter(LyricNotificationManager.ACTION_LYRIC_NOTIFICATION_DISMISSED),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    private fun observeLyricNotification() {
        viewModelScope.launch {
            combine(lyricsMutable, nowPlayingMusicMutable) { lyrics, music ->
                lyrics to music
            }.collect { (lyrics, music) ->
                updateLyricNotification(lyrics, music)
            }
        }
    }

    @SuppressLint("MissingPermission")
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun updateLyricNotification(lyrics: UnifiedLyrics?, music: NowPlayingMusic?) {
        if (!AppSettings.isLyricNotificationEnabled(context)) {
            lyricNotificationManager.cancel()
            return
        }

        // if we have no music or no lyrics, just clear
        if (music == null || lyrics == null) {
            lyricNotificationManager.cancel()
            return
        }

        // compute current line first, since we'll need it in both branches
        val time = getLyricTimeWithDeviceOffset(music)
        val currentLine = lyrics.lines.firstOrNull { time in it.startTime..it.endTime }
            ?: lyrics.lines.lastOrNull { it.startTime <= time }

        if (!music.isPlaying) {
            // paused state: send only one update with ongoing=false
            if (!pausedNotificationSent) {
                lyricNotificationManager.showOrUpdate(currentLine, ongoing = false)
                pausedNotificationSent = true
            }
            return
        }

        // playback resumed – clear flag and send ongoing notifications again
        pausedNotificationSent = false
        lyricNotificationManager.showOrUpdate(currentLine, ongoing = true)
    }

    internal fun getLyricTimeWithDeviceOffset(music: NowPlayingMusic?): Long {
        if (music == null) return 0L
        val base = music.currentPosition
        val device = AudioDeviceHelper.getCurrentOutputDeviceName(context)
        val source = music.packageName ?: "*"
        val offset = AppSettings.getLyricTimingOffset(context, music.title, music.artist, device, source) ?: 0L
        if (offset == 0L) return base
        Timber.d("[LyricsMatcher] Applying lyric offset: $offset ms (song='${music.title}' artist='${music.artist}' device='$device' source='$source')")
        return base + offset
    }

    /**
     * 设置媒体监听器
     */
    private fun setupMediaListener() {
        mediaInfoService.startListening()

        viewModelScope.launch {
            mediaInfoService.nowPlayingMusic.collect { music ->
                // 检查是否为新歌曲（标题或歌手改变）
                val oldMusic = nowPlayingMusicMutable.value
                val isMusicChanged =
                    oldMusic?.title != music?.title ||
                    oldMusic?.artist != music?.artist

                // 检查播放状态是否变化

                // 检查进度是否显著变化（用于检测跳转操作）

                nowPlayingMusicMutable.value = music


                // 如果歌曲确实改变且有有效的歌曲信息，先尝试使用缓存，只有在缓存不可用时才清空并搜索
                if (isMusicChanged && music != null) {
                    // 兼容老旧酷狗缓存需要刷新空格的问题
                    val cached = lyricsCacheRepository.findBySong(music.title, music.artist)
                    if (cached != null) {
                        val shouldBypassCache = cached.source.contains("kugou", ignoreCase = true) ||
                            cached.source.contains("酷狗")
                        if (!shouldBypassCache) {
                            val parsed = LyricsRepository.parseTTML(cached.xmlContent)
                            if (parsed != null) {
                                lyricsMutable.value = parsed
                                updateSongStructures(parsed)
                                _errorMessage.value = null
                                Timber.i("[CacheManager] Loaded lyrics from cache (startup): ${cached.title} - ${cached.artist} (${cached.source})")



                                // 已经拿到缓存，跳过后续搜索
                                return@collect
                            }
                        }
                    }

                    // 没有可用缓存时再清空并执行网络请求，以避免闪烁的遮罩
                    lyricsMutable.value = null
                    _songStructures.value = emptyList() // 清空歌曲结构
                    Timber.i("[LyricsMatcher] Music changed, auto-fetching lyrics...")
                    fetchLyrics()
                }
            }
        }
        mediaInfoService.startListening()
    }
    
    /**
     * 智能获取歌词 - 自动搜索多个来源并选择最佳结果
     * 基于 Unilyric 的多源搜索策略:
     * 1. 搜索网易云、QQ 音乐、酷狗音乐
     * 2. 优先尝试 AMLL TTML DB (高质量逐字歌词)
     * 3. 回退到各平台的普通歌词
     */
    fun fetchLyrics() {
        val music = nowPlayingMusicMutable.value
        if (music == null) {
            _errorMessage.value = "未检测到播放信息"
            return
        }

        // 如果已有正在运行的 fetchLyrics 任务（例如上一个 fetch 还未结束），
        // 主动取消它，以避免多个搜索任务并行消耗资源。取消后 launch 不会执行。
        fetchLyricsJob?.cancel()
        // coroutineContext 是 CoroutineScope 扩展属性，可以在 launch lambda 内直接访问。
        // 在这里提前抓取当前协程的 Job，保存到 fetchLyricsJob 供后续取消使用。
        val newJob = viewModelScope.launch {
            _errorMessage.value = null
            // 将当前协程的 Job 保存为 fetchLyricsJob，保证 cancelFetchLyrics() 可以取消它
            val currentJob = coroutineContext[Job]
            if (currentJob != null) {
                fetchLyricsJob = currentJob
            }

            // first try to load from cache without toggling the loading flag; this avoids
            // showing a spinner/mask for cached data which is usually very fast.
            val cached = lyricsCacheRepository.findBySong(music.title, music.artist)
            if (cached != null) {
                val shouldBypassCache = cached.source.contains("kugou", ignoreCase = true) ||
                    cached.source.contains("酷狗")
                if (!shouldBypassCache) {
                    val parsed = LyricsRepository.parseTTML(cached.xmlContent)
                    if (parsed != null) {
                        lyricsMutable.value = parsed
                        updateSongStructures(parsed)
                        _errorMessage.value = null
                        Timber.d("[CacheManager] Loaded lyrics from cache: ${cached.title} - ${cached.artist} (${cached.source})")

                        // ✅ 重置歌词哈希值，确保新歌词会被发送
                        lastSentLyricsHash = 0

                        return@launch
                    }
                } else {
                    Timber.d("[CacheManager] Bypassing stale Kugou cache to refresh whitespace-fixed lyrics")
                }
            }

            // no usable cache, fall back to network search
            lyricsMutable.value = null
            _songStructures.value = emptyList() // 清空歌曲结构
            _isLoading.value = true

            try {
                // 在发起网络请求前检查取消状态，避免取消后仍进行昂贵的网络调用
                kotlinx.coroutines.currentCoroutineContext().ensureActive()

                Timber.i("[LyricsMatcher] Fetching lyrics for: ${music.title} - ${music.artist}")

                val sourceName = getAppNameFromPackage(context, music.packageName)
                val result = lyricsRepository.fetchLyricsAuto(
                    title = music.title,
                    artist = music.artist,
                    currentSourceName = sourceName
                )

                // fetchLyricsAuto 返回后再次检查取消状态
                kotlinx.coroutines.currentCoroutineContext().ensureActive()

                if (result.isSuccess && result.lyrics != null) {
                    // ⭐ 修复关键：只有在设置启用元数据处理时才处理，否则使用原始歌词
                    val shouldProcessMetadata = AMLLSettings.isMetadataProcessingEnabled(context)
                    val finalLyrics = if (shouldProcessMetadata) {
                        Timber.d("[LyricsMatcher] Metadata processing enabled, using processed lyrics")
                        result.lyrics
                    } else {
                        Timber.d("[LyricsMatcher] Metadata processing disabled, using raw lyrics")
                        result.lyrics
                    }

                    lyricsMutable.value = finalLyrics
                    updateSongStructures(finalLyrics)
                    // ⭐ 修复关键：始终缓存原始歌词内容（不经过元数据处理）
                    val rawXmlContent = TTMLConverter.toTTMLString(result.lyrics)
                    lyricsCacheRepository.upsert(
                        title = music.title,
                        artist = music.artist,
                        source = result.source ?: "auto",
                        xmlContent = rawXmlContent
                    )
                    Timber.i("[LyricsMatcher] Successfully fetched lyrics from ${result.source}")

                    // ✅ 重置歌词哈希值，确保新歌词会被发送
                    lastSentLyricsHash = 0

                    // 刷新歌曲结构（重搜歌词时）
                    refreshSongStructures()


                } else {
                    _errorMessage.value = result.errorMessage ?: "获取歌词失败"
                    Timber.e("[LyricsMatcher] Failed to fetch lyrics: ${result.errorMessage}")
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                // 主动取消：记个日志后安静退出，不覆盖错误信息
                Timber.i("[LyricsMatcher] fetchLyrics cancelled (isLoading -> false)")
                throw e
            } catch (e: Exception) {
                _errorMessage.value = "错误：${e.message}"
                Timber.e(e, "[LyricsMatcher] Error fetching lyrics")
            } finally {
                _isLoading.value = false
                if (fetchLyricsJob == coroutineContext[Job]) {
                    fetchLyricsJob = null
                }
            }
        }
        // launch 会在调度前返回，但为了避免在 init 阶段出现微小调度空档
        // 导致 fetchLyricsJob 仍是上一个 job，这里同时用 launch 的返回 Job 覆盖一次。
        fetchLyricsJob = newJob
    }

    /**
     * 取消正在运行的 fetchLyrics 任务。
     *
     * 主要由 MainScreen 在 isLoading 变为 false 时调用，以确保
     * LyricsRepository.fetchLyricsAuto() 以及它内部启动的任何子任务（例如
     * 并行的网络搜索、AMLL 探测等）能够被立即停止，避免在 UI 不再展示
     * 加载状态后仍继续在后台运行。
     *
     * 调用此方法后：
     * - 当前协程会抛出 CancellationException，fetchLyricsAuto() 的 `try` 块会
     *   提前退出，finally 仍会执行，_isLoading 被设为 false。
     * - 没有正在运行的任务时，该方法是空操作。
     */
    fun cancelFetchLyrics() {
        val job = fetchLyricsJob
        if (job != null && job.isActive) {
            Timber.i("[LyricsMatcher] cancelFetchLyrics() called -> cancelling running fetchLyrics job")
            job.cancel()
        }
        fetchLyricsJob = null
    }

    private fun wasmFormatFor(format: LyricsFormat): String? = when (format) {
        LyricsFormat.LRC -> "lrc"
        LyricsFormat.ENHANCED_LRC -> "enhanced_lrc"
        LyricsFormat.QRC -> "qrc"
        LyricsFormat.KRC -> "krc"
        LyricsFormat.YRC -> "yrc"
        LyricsFormat.TTML -> "ttml"
        LyricsFormat.SCOREMUSE_XML, LyricsFormat.PLAIN_TEXT -> null
    }

    private suspend fun parseWithWasmPreferred(
        raw: String,
        format: LyricsFormat,
        title: String,
        artist: String
    ): UnifiedLyrics? {
        val wasmFormat = wasmFormatFor(format) ?: return null
        return try {
            val parser = ServiceLocator.provideWasmLyricParser(context)
            val lines = parser.parse(raw, wasmFormat)
            if (lines.isNullOrEmpty()) {
                null
            } else {
                TTMLConverter.fromLyricLines(lines, title = title, artist = artist)
            }
        } catch (e: Exception) {
            Timber.w(e, "[CustomLyrics] WASM parse failed (format=$format), falling back to Kotlin parser")
            null
        }
    }



    /**
     * 应用自选歌词输入，支持 TTML / LRC / 纯文本
     */
    fun applyCustomLyricsInput(content: String, title: String, artist: String, source: String = "manual") {
        viewModelScope.launch {
            try {
                val trimmed = content.trim()
                if (trimmed.isBlank()) {
                    _errorMessage.value = "歌词内容为空"
                    return@launch
                }

                // ✅ 检测歌词格式
                val format = LyricsFormat.detect(trimmed)

                var parsed: UnifiedLyrics?
                var cachedXmlContent: String

                when (format) {
                    // ✅ ScoreMuse XML 格式直接解析，保留完整的歌曲结构信息
                    LyricsFormat.SCOREMUSE_XML, LyricsFormat.TTML -> {
                        Timber.d("[SongStructure] TTML/XML format detected, parsing directly to preserve metadata")
                        try {
                            parsed = TTMLConverter.fromLyrics(trimmed)
                            // ✅ 对于 XML/TTML 格式，直接保存原始内容，避免结构信息丢失
                            cachedXmlContent = trimmed
                        } catch (e: Exception) {
                            Timber.e(e, "[TTMLConverter] Failed to parse TTML/XML directly")
                            parsed = null
                            cachedXmlContent = ""
                        }
                    }
                    // ✅ 其他格式优先使用 WASM 解析器，失败再回退 Kotlin 解析
                    else -> {
                        val wasmParsed = withContext(Dispatchers.IO) {
                            parseWithWasmPreferred(
                                raw = trimmed,
                                format = format,
                                title = title.ifBlank { "自选歌词" },
                                artist = artist.ifBlank { "Unknown" }
                            )
                        }
                        if (wasmParsed != null) {
                            Timber.d("[CustomLyrics] Parsed with WASM lyricProcessor ($format)")
                            parsed = wasmParsed
                            cachedXmlContent = TTMLConverter.toTTMLString(wasmParsed)
                        } else {
                            Timber.d("[CustomLyrics] WASM parse unavailable, using Kotlin parser ($format)")
                            parsed = TTMLConverter.fromLyrics(
                                content = trimmed,
                                title = title.ifBlank { "自选歌词" },
                                artist = artist.ifBlank { "Unknown" }
                            )
                            cachedXmlContent = parsed?.let { TTMLConverter.toTTMLString(it) } ?: ""
                        }
                    }
                }

                if (parsed != null && cachedXmlContent.isNotBlank()) {
                    lyricsMutable.value = parsed
                    updateSongStructures(parsed)
                    _errorMessage.value = null
                    lyricsCacheRepository.upsert(
                        title = title.ifBlank { "自选歌词" },
                        artist = artist.ifBlank { "Unknown" },
                        source = source,
                        xmlContent = cachedXmlContent
                    )

                    // ✅ 重置歌词哈希值，确保新歌词会被发送
                    lastSentLyricsHash = 0


                } else {
                    _errorMessage.value = "无法识别歌词格式"
                }
            } catch (e: Exception) {
                _errorMessage.value = "应用歌词失败：${e.message}"
                Timber.e(e, "[CustomLyrics] Error applying custom lyrics input")
            }
        }
    }

    /**
     * 播放控制
     */
    fun play() {
        mediaInfoService.play()
    }
    
    fun pause() {
        mediaInfoService.pause()
    }
    
    fun skipToNext() {
        mediaInfoService.skipToNext()
    }
    
    fun skipToPrevious() {
        mediaInfoService.skipToPrevious()
    }
    
    fun seekTo(position: Long) {
        Timber.d("[PlaybackControl] seekTo(position=$position)")
        mediaInfoService.seekTo(position)
    }
    
    fun fastForward() {
        mediaInfoService.fastForward()
    }
    
    fun rewind() {
        mediaInfoService.rewind()
    }

    override fun onCleared() {
        // unregister broadcast listener added earlier
        context.unregisterReceiver(deleteReceiver)
        
        val prefs = context.getSharedPreferences("ScoreMuse_settings", Context.MODE_PRIVATE)
        prefs.unregisterOnSharedPreferenceChangeListener(preferenceListener)

        lyricNotificationManager.cancel()
        mediaInfoService.stopListening()
        
        // 注意：不在这里调用 httpClient.close()，因为该客户端是由 ServiceLocator 提供的全局单例，
        // 应该在应用整个生命周期内保持开启。
    }


    /**
     * 解析并更新歌曲结构信息
     * 从歌词行自动推断歌曲结构（前奏、间奏、尾奏、主歌、副歌等）
     */
    private fun updateSongStructures(lyrics: UnifiedLyrics) {
        viewModelScope.launch {
            try {
                // 获取歌曲总时长用于检测尾奏
                val songDuration = nowPlayingMusicMutable.value?.duration ?: 0L

                // 检查是否有元数据中的结构信息
                val metadataStructures = lyrics.metadata.songStructures
                val source = if (!metadataStructures.isNullOrEmpty()) "metadata" else "inferred"

                val structures = SongStructureParser.parseStructure(lyrics.lines, metadataStructures, songDuration)
                _songStructures.value = structures

                val formatTime = { millis: Long ->
                    val totalSeconds = millis / 1000
                    val minutes = totalSeconds / 60
                    val seconds = totalSeconds % 60
                    String.format(Locale.ROOT, "%d:%02d", minutes, seconds)
                }

                val summary = if (structures.isEmpty()) {
                    "none"
                } else {
                    structures.mapIndexed { index, structure ->
                        "[$index] ${structure.label} (${structure.type.displayName}): ${formatTime(structure.startTime)} - ${formatTime(structure.endTime)}"
                    }.joinToString("; ")
                }
                Timber.d("[SongStructure] Parsed ${structures.size} structures (source=$source): $summary")

                // 诊断最终结果
                if (structures.size == 1 && structures[0].label == "段落 1") {
                    Timber.i("[SongStructure] ⚠️ Fallback result: single paragraph structure")
                }
            } catch (e: Exception) {
                Timber.e("[SongStructure] ❌ Failed to parse song structure: ${e.message}")
                _songStructures.value = emptyList()
            }
        }
    }
    
    /**
     * 刷新歌曲结构信息
     * 用于重搜歌词或刷新时重新解析结构
     */
    fun refreshSongStructures() {
        val currentLyrics = lyricsMutable.value ?: return
        updateSongStructures(currentLyrics)
        Timber.i("[SongStructure] Refreshing song structures")
    }
}
