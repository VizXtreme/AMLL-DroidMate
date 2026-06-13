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
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext

    internal var lyricsRepository = ServiceLocator.provideLyricsRepository(context)
    private val lyricsCacheRepository = ServiceLocator.provideLyricsCacheRepository(context)

    /**
     * 媒体信息监听服务（监听系统媒体播放状态）
     * 改为 internal，便于 MainScreen 强制刷新专辑图时直接调用底层 API
     */
    internal val mediaInfoService = MediaInfoService(context)

    private var lastSentLyricsHash: Int = 0

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var lyricNotificationManager: LyricNotificationManager =
        LyricNotificationManager(context)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val nowPlayingMusicMutable = MutableStateFlow<NowPlayingMusic?>(null)
    val nowPlayingMusic: StateFlow<NowPlayingMusic?> = nowPlayingMusicMutable

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val lyricsMutable = MutableStateFlow<UnifiedLyrics?>(null)
    val lyrics: StateFlow<UnifiedLyrics?> = lyricsMutable

    private val _songStructures = MutableStateFlow<List<SongStructure>>(emptyList())
    val songStructures: StateFlow<List<SongStructure>> = _songStructures

    private val _isSongStructureBarEnabled = MutableStateFlow(AppSettings.isSongStructureBarEnabled(context))
    val isSongStructureBarEnabled: StateFlow<Boolean> = _isSongStructureBarEnabled

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)

    @Volatile
    private var fetchLyricsJob: Job? = null

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

        val prefs = context.getSharedPreferences("ScoreMuse_settings", Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(preferenceListener)

        viewModelScope.launch {
            lyricsMutable.collect { lyrics ->
                Timber.d("[MainViewModel] Lyrics state changed:")
                Timber.d("[MainViewModel]   Lyrics: ${lyrics?.let { "${it.lines.size} lines" } ?: "null"}")
                if (lyrics != null) {
                    Timber.d("[MainViewModel]   Title: ${lyrics.metadata.title}")
                    Timber.d("[MainViewModel]   Artist: ${lyrics.metadata.artist}")
                    Timber.d("[MainViewModel]   Has song structures: ${lyrics.metadata.songStructures?.isNotEmpty() ?: false}")
                }
            }
        }

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

        if (music == null || lyrics == null) {
            lyricNotificationManager.cancel()
            return
        }

        val time = getLyricTimeWithDeviceOffset(music)
        val currentLine = lyrics.lines.firstOrNull { time in it.startTime..it.endTime }
            ?: lyrics.lines.lastOrNull { it.startTime <= time }

        if (!music.isPlaying) {
            if (!pausedNotificationSent) {
                lyricNotificationManager.showOrUpdate(currentLine, ongoing = false)
                pausedNotificationSent = true
            }
            return
        }

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

    private fun setupMediaListener() {
        mediaInfoService.startListening()

        viewModelScope.launch {
            mediaInfoService.nowPlayingMusic.collect { music ->
                val oldMusic = nowPlayingMusicMutable.value
                val isMusicChanged =
                    oldMusic?.title != music?.title ||
                    oldMusic?.artist != music?.artist

                nowPlayingMusicMutable.value = music

                if (isMusicChanged && music != null) {
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
                                return@collect
                            }
                        }
                    }

                    lyricsMutable.value = null
                    _songStructures.value = emptyList()
                    Timber.i("[LyricsMatcher] Music changed, auto-fetching lyrics...")
                    fetchLyrics()
                }
            }
        }
        mediaInfoService.startListening()
    }

    fun fetchLyrics() {
        val music = nowPlayingMusicMutable.value
        if (music == null) {
            _errorMessage.value = "未检测到播放信息"
            return
        }

        fetchLyricsJob?.cancel()
        val newJob = viewModelScope.launch {
            _errorMessage.value = null
            val currentJob = coroutineContext[Job]
            if (currentJob != null) {
                fetchLyricsJob = currentJob
            }

            val cached = lyricsCacheRepository.findBySong(music.title, music.artist)
            if (cached != null) {
                val parsed = LyricsRepository.parseTTML(cached.xmlContent)
                if (parsed != null) {
                    lyricsMutable.value = parsed
                    updateSongStructures(parsed)
                    _errorMessage.value = null
                    Timber.d("[CacheManager] Loaded lyrics from cache: ${cached.title} - ${cached.artist} (${cached.source})")
                    lastSentLyricsHash = 0
                    return@launch
                }
            }

            lyricsMutable.value = null
            _songStructures.value = emptyList()
            _isLoading.value = true

            try {
                kotlinx.coroutines.currentCoroutineContext().ensureActive()

                Timber.i("[LyricsMatcher] Fetching lyrics for: ${music.title} - ${music.artist}")

                val sourceName = getAppNameFromPackage(context, music.packageName)
                val result = lyricsRepository.fetchLyricsAuto(
                    title = music.title,
                    artist = music.artist,
                    currentSourceName = sourceName
                )

                kotlinx.coroutines.currentCoroutineContext().ensureActive()

                if (result.isSuccess && result.lyrics != null) {
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
                    val rawXmlContent = TTMLConverter.toTTMLString(result.lyrics)
                    lyricsCacheRepository.upsert(
                        title = music.title,
                        artist = music.artist,
                        source = result.source ?: "auto",
                        xmlContent = rawXmlContent
                    )
                    Timber.i("[LyricsMatcher] Successfully fetched lyrics from ${result.source}")

                    lastSentLyricsHash = 0
                    refreshSongStructures()
                } else {
                    _errorMessage.value = result.errorMessage ?: "获取歌词失败"
                    Timber.e("[LyricsMatcher] Failed to fetch lyrics: ${result.errorMessage}")
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
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
        fetchLyricsJob = newJob
    }

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

    fun applyCustomLyricsInput(content: String, title: String, artist: String, source: String = "manual") {
        viewModelScope.launch {
            try {
                val trimmed = content.trim()
                if (trimmed.isBlank()) {
                    _errorMessage.value = "歌词内容为空"
                    return@launch
                }

                val format = LyricsFormat.detect(trimmed)

                var parsed: UnifiedLyrics?
                var cachedXmlContent: String

                when (format) {
                    LyricsFormat.SCOREMUSE_XML, LyricsFormat.TTML -> {
                        Timber.d("[SongStructure] TTML/XML format detected, parsing directly to preserve metadata")
                        try {
                            parsed = TTMLConverter.fromLyrics(trimmed)
                            cachedXmlContent = trimmed
                        } catch (e: Exception) {
                            Timber.e(e, "[TTMLConverter] Failed to parse TTML/XML directly")
                            parsed = null
                            cachedXmlContent = ""
                        }
                    }
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
                    lastSentLyricsHash = 0
                } else {
                    _errorMessage.value = "无法识别歌词格式"
                }
            } catch (e: Exception) {
                _errorMessage.value = "应用歌词失败：${e.message}"
                Timber.e(e, "[CustomLyrics] Error applying custom lyrics input")
            } finally {
                // 无论成功/失败/早返回，都确保 MainScreen 的 isLoading 指示器会消失
                // 与 MainViewModel.fetchLyrics() 的 finally 块保持一致。
                _isLoading.value = false
            }
        }
    }

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
        context.unregisterReceiver(deleteReceiver)

        val prefs = context.getSharedPreferences("ScoreMuse_settings", Context.MODE_PRIVATE)
        prefs.unregisterOnSharedPreferenceChangeListener(preferenceListener)

        lyricNotificationManager.cancel()
        mediaInfoService.stopListening()
    }

    private fun updateSongStructures(lyrics: UnifiedLyrics) {
        viewModelScope.launch {
            try {
                /**
                 * 多级 fallback 获取 songDuration，确保尾奏能够被正确检测：
                 * 1) 优先使用当前播放音乐的 duration（最准确）
                 * 2) 否则使用 lyrics 元数据中的 duration
                 * 3) 否则从歌词行推断（最后一句歌词的结束时间 + 5秒缓冲）
                 * 4) 最后回退到 0
                 * 修复：当 nowPlayingMusic 还未更新或 duration=0 时（如异步竞态），
                 * 尾奏段落无法被检测，导致尾奏丢失。
                 */
                val inferredEndTime = lyrics.lines.maxOfOrNull { it.endTime } ?: 0L
                /**
                 * 修复：使用 .takeIf { it > 0 } 确保当 duration==0（不仅仅是 null）时也能 fallback。
                 * 这处理了 nowPlayingMusic 还未更新、或 duration=0 的异常情况。
                 */
                val songDuration = nowPlayingMusicMutable.value?.duration?.takeIf { it > 0 }
                    ?: lyrics.metadata.duration.takeIf { it > 0 }
                    ?: inferredEndTime.takeIf { it > 0 }?.plus(5_000L)
                    ?: 0L
                Timber.v("[SongStructure] Using songDuration=${songDuration}ms (nowPlaying=${nowPlayingMusicMutable.value?.duration}, meta=${lyrics.metadata.duration}, inferred=${inferredEndTime})")

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
     */
    fun refreshSongStructures() {
        val currentLyrics = lyricsMutable.value ?: return
        updateSongStructures(currentLyrics)
        Timber.i("[SongStructure] Refreshing song structures")
    }

    /**
     * 强制刷新当前播放歌曲的专辑图。
     */
    fun refreshAlbumArt() {
        val music = nowPlayingMusicMutable.value
        val refreshKey = music?.let { "${it.packageName ?: "unknown"}_${it.title}_${it.artist}" }

        // 1. 通知 MediaInfoService 清空缓存
        mediaInfoService.refreshAlbumArt(refreshKey)

        // 2. 立即把 UI 上的 albumArtUri 置空，触发前端占位图
        if (music != null) {
            nowPlayingMusicMutable.value = music.copy(albumArtUri = null)
        }
        Timber.i("[MainViewModel] refreshAlbumArt invoked (key=$refreshKey)")
    }
}

