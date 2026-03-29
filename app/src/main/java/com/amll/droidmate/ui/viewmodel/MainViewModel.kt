package com.amll.droidmate.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amll.droidmate.data.converter.TTMLConverter
import com.amll.droidmate.data.parser.SongStructureParser
import com.amll.droidmate.data.repository.LyricsCacheRepository
import com.amll.droidmate.data.repository.LyricsRepository
import com.amll.droidmate.ui.screens.getAppNameFromPackage
import com.amll.droidmate.di.ServiceLocator
import com.amll.droidmate.domain.model.NowPlayingMusic
import com.amll.droidmate.domain.model.SongStructure
import com.amll.droidmate.domain.model.TTMLLyrics
import com.amll.droidmate.service.LyricNotificationManager
import com.amll.droidmate.service.MediaInfoService
import com.amll.droidmate.service.WebSocketForegroundService
import com.amll.droidmate.ui.AppSettings
import com.amll.droidmate.util.AudioDeviceHelper
import com.amll.droidmate.websocket.AMLLWebSocketClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import java.io.File

/**
 * 主视图模型 - 管理应用的状态和逻辑
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val context: Context = application.applicationContext

    // HTTP Client & 仓库（由 ServiceLocator 提供以便集中管理）
    private val httpClient = ServiceLocator.provideHttpClient(context)
    // make this mutable so tests can inject a fake repository if needed
    @androidx.annotation.VisibleForTesting(otherwise = androidx.annotation.VisibleForTesting.PRIVATE)
    internal var lyricsRepository = ServiceLocator.provideLyricsRepository(context)
    private val lyricsCacheRepository = ServiceLocator.provideLyricsCacheRepository(context)
    
    // 服务
    private val mediaInfoService = MediaInfoService(context)
    
    // WebSocket 客户端（用于同步播放状态到外部服务）
    private val webSocketClient = AMLLWebSocketClient.getInstance()
    
    // 用于跟踪上次发送的歌词，避免重复发送
    private var lastSentLyricsHash: Int = 0
    
    // WebSocket 监听器（用于在连接时提供当前播放状态）
    private val webSocketListener: AMLLWebSocketClient.Listener by lazy {
        object : AMLLWebSocketClient.Listener {
            override fun onConnected() {
                Timber.d("MainViewModel: WebSocket 已连接")
                
                // ✅ 每次连接（包括重连）都重新同步完整的播放状态
                val music = _nowPlayingMusic.value
                if (music != null && AppSettings.isWebSocketProtocolEnabled(context)) {
                    Timber.d("WebSocket 重连成功，重新同步完整播放状态")
                    // isMusicChanged=true 会触发歌曲信息和专辑图的发送
                    syncPlaybackStateToWebSocket(music, isMusicChanged = true)
                }
            }
            
            override fun onDisconnected() {
                Timber.w("MainViewModel: WebSocket 已断开")
            }
            
            override fun onMessageReceived(message: String) {
                Timber.d("MainViewModel: 收到 WebSocket 消息：$message")
                
                // 解析并执行命令
                try {
                    val json = kotlinx.serialization.json.Json.parseToJsonElement(message)
                    val type = json.jsonObject["type"]?.jsonPrimitive?.content
                    
                    if (type == "command") {
                        val valueObj = json.jsonObject["value"]?.jsonObject
                        val command = valueObj?.get("command")?.jsonPrimitive?.content
                        
                        Timber.i("MainViewModel: 收到命令：$command")
                        
                        when (command) {
                            "pause" -> {
                                Timber.i("MainViewModel: 执行暂停命令")
                                pause()
                                Timber.d("MainViewModel: 暂停命令执行完成")
                            }
                            "resume" -> {
                                Timber.i("MainViewModel: 执行播放命令")
                                play()
                                Timber.d("MainViewModel: 播放命令执行完成")
                            }
                            "forwardSong" -> {
                                Timber.i("MainViewModel: 执行下一首命令")
                                skipToNext()
                                Timber.d("MainViewModel: 下一首命令执行完成")
                            }
                            "backwardSong" -> {
                                Timber.i("MainViewModel: 执行上一首命令")
                                skipToPrevious()
                                Timber.d("MainViewModel: 上一首命令执行完成")
                            }
                            "seekPlayProgress" -> {
                                val progress = valueObj?.get("progress")?.jsonPrimitive?.content?.toLongOrNull()
                                if (progress != null) {
                                    Timber.i("MainViewModel: 执行跳转进度命令：$progress ms")
                                    seekTo(progress)
                                    Timber.d("MainViewModel: 跳转进度命令执行完成")
                                } else {
                                    Timber.w("MainViewModel: 跳转进度命令参数无效")
                                }
                            }
                            "setVolume" -> {
                                val volume = valueObj?.get("volume")?.jsonPrimitive?.content?.toDoubleOrNull()
                                if (volume != null) {
                                    Timber.i("MainViewModel: 执行音量设置命令：$volume")
                                    setVolume(volume)
                                    Timber.d("MainViewModel: 音量设置命令执行完成")
                                } else {
                                    Timber.w("MainViewModel: 音量设置命令参数无效")
                                }
                            }
                            "setRepeatMode", "setShuffleMode" -> {
                                Timber.d("MainViewModel: 收到不支持的命令：$command，忽略")
                            }
                            else -> {
                                Timber.d("MainViewModel: 未知命令：$command")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "MainViewModel: 解析命令失败")
                }
            }
            
            override fun onError(error: Throwable) {
                Timber.e(error, "MainViewModel: WebSocket 错误")
            }
            
            override fun getCurrentPlayState(): AMLLWebSocketClient.PlayState? {
                val music = _nowPlayingMusic.value
                if (music == null) {
                    Timber.d("MainViewModel: getCurrentPlayState - 无播放内容")
                    return null
                }
                
                // 检查是否有有效的歌曲信息
                val validMusicId = music.packageName?.takeIf { it.isNotEmpty() && it != "unknown" }
                val validMusicName = music.title.takeIf { it.isNotEmpty() && it != "Unknown" && it != "等待播放" }
                
                if (validMusicId == null || validMusicName == null) {
                    Timber.d("MainViewModel: getCurrentPlayState - 无有效歌曲信息 (musicId=$validMusicId, musicName=$validMusicName)")
                    return null
                }
                
                // 构建 TTML 歌词
                val ttmlContent = _lyrics.value?.let { lyrics ->
                    try {
                        buildTtmlForLyrics(lyrics)
                    } catch (e: Exception) {
                        Timber.e(e, "MainViewModel: 构建 TTML 失败")
                        null
                    }
                }
                
                val state = AMLLWebSocketClient.PlayState(
                    musicId = validMusicId,
                    musicName = validMusicName,
                    albumName = music.album ?: "",
                    artistName = music.artist,
                    duration = music.duration,
                    progress = music.currentPosition,
                    isPlaying = music.isPlaying,
                    ttmlLyric = ttmlContent
                )
                
                Timber.d("MainViewModel: getCurrentPlayState 返回:")
                Timber.d("  - musicId: ${state.musicId}")
                Timber.d("  - musicName: ${state.musicName}")
                Timber.d("  - artistName: ${state.artistName}")
                Timber.d("  - hasLyrics: ${!state.ttmlLyric.isNullOrBlank()}")
                Timber.d("  - isPlaying: ${state.isPlaying}")
                Timber.d("  - progress: ${state.progress}ms")
                if (!state.ttmlLyric.isNullOrBlank()) {
                    Timber.d("  - TTML 预览：${state.ttmlLyric.take(200)}...")
                }
                
                return state
            }
        }
    }

    /**
     * Real notification manager; tests can replace via the internal var below.
     */
    @androidx.annotation.VisibleForTesting(otherwise = androidx.annotation.VisibleForTesting.PRIVATE)
    internal var lyricNotificationManager: LyricNotificationManager =
        LyricNotificationManager(context)

    // UI State
    @androidx.annotation.VisibleForTesting(otherwise = androidx.annotation.VisibleForTesting.PRIVATE)
    internal val _nowPlayingMusic = MutableStateFlow<NowPlayingMusic?>(null)
    val nowPlayingMusic: StateFlow<NowPlayingMusic?> = _nowPlayingMusic
    
    @androidx.annotation.VisibleForTesting(otherwise = androidx.annotation.VisibleForTesting.PRIVATE)
    internal val _lyrics = MutableStateFlow<TTMLLyrics?>(null)
    val lyrics: StateFlow<TTMLLyrics?> = _lyrics
    
    // 歌曲结构信息
    private val _songStructures = MutableStateFlow<List<SongStructure>>(emptyList())
    val songStructures: StateFlow<List<SongStructure>> = _songStructures

    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // tracks whether we've already shown the paused notification. After a
    // pause occurs we'll send one update with ongoing=false, then refrain from
    // sending additional updates until playback resumes.  This ignores whether
    // the user actually swipes it away.
    @androidx.annotation.VisibleForTesting(otherwise = androidx.annotation.VisibleForTesting.PRIVATE)
    internal var pausedNotificationSent: Boolean = false

    private val deleteReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: android.content.Intent?) {
            if (intent?.action == LyricNotificationManager.ACTION_LYRIC_NOTIFICATION_DISMISSED) {
                Timber.i("Lyric notification deleted by user, cancelling")
                lyricNotificationManager.cancel()
            }
        }
    }

    init {
        setupMediaListener()
        observeLyricNotification()
        registerWebSocketListener()
        Timber.plant(Timber.DebugTree())

        // listen for user dismissals. Android 13+ requires an explicit export flag
        // when registering receivers that aren't for system broadcasts.
        context.registerReceiver(
            deleteReceiver,
            android.content.IntentFilter(LyricNotificationManager.ACTION_LYRIC_NOTIFICATION_DISMISSED),
            Context.RECEIVER_NOT_EXPORTED
        )
    }

    /**
     * Called when the lyric notification is explicitly removed by the user (swipe
     * away or clear all).  Extracted to a method so tests can simulate the
     * behaviour without needing to construct an Intent.
     */
    @androidx.annotation.VisibleForTesting(otherwise = androidx.annotation.VisibleForTesting.PRIVATE)
    internal fun onNotificationDeletedByUser() {
        Timber.i("Lyric notification deleted by user (test helper)")
        lyricNotificationManager.cancel()
    }

    private fun observeLyricNotification() {
        viewModelScope.launch {
            combine(_lyrics, _nowPlayingMusic) { lyrics, music ->
                lyrics to music
            }.collect { (lyrics, music) ->
                updateLyricNotification(lyrics, music)
            }
        }
    }

    @androidx.annotation.VisibleForTesting(otherwise = androidx.annotation.VisibleForTesting.PRIVATE)
    internal fun updateLyricNotification(lyrics: TTMLLyrics?, music: NowPlayingMusic?) {
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

    fun refreshLyricNotification() {
        updateLyricNotification(_lyrics.value, _nowPlayingMusic.value)
    }

    internal fun getLyricTimeWithDeviceOffset(music: NowPlayingMusic?): Long {
        if (music == null) return 0L
        val base = music.currentPosition
        val device = AudioDeviceHelper.getCurrentOutputDeviceName(context)
        val source = music.packageName ?: "*"
        val offset = AppSettings.getLyricTimingOffset(context, music.title, music.artist, device, source) ?: 0L
        if (offset == 0L) return base
        Timber.d("Applying lyric offset: ${'$'}offset ms (song='${'$'}{music.title}' artist='${'$'}{music.artist}' device='${'$'}device' source='${'$'}source')")
        return base + offset
    }

    /**
     * 设置媒体监听器
     */
    private fun setupMediaListener() {
        // 如果启用了 WebSocket 协议，则启动前台服务
        if (AppSettings.isWebSocketProtocolEnabled(context)) {
            val serverUrl = AppSettings.getWebSocketProtocolAddress(context)
            if (serverUrl.isNotEmpty()) {
                Timber.i("启动 WebSocket 前台服务")
                WebSocketForegroundService.start(context, serverUrl)
            } else {
                Timber.w("WebSocket 已启用但未配置服务器地址")
                // 回退到普通模式
                mediaInfoService.startListening()
            }
        } else {
            // 未启用 WebSocket，使用普通模式
            mediaInfoService.startListening()
        }
            
        viewModelScope.launch {
            mediaInfoService.nowPlayingMusic.collect { music ->
                // 检查是否为新歌曲（标题或歌手改变）
                val oldMusic = _nowPlayingMusic.value
                val isMusicChanged =
                    oldMusic?.title != music?.title ||
                    oldMusic?.artist != music?.artist
                
                // 检查播放状态是否变化
                val isPlayingChanged = oldMusic?.isPlaying != music?.isPlaying
                
                // 检查进度是否显著变化（用于检测跳转操作）
                val positionChangedSignificantly = 
                    music != null && oldMusic != null &&
                    kotlin.math.abs(music.currentPosition - oldMusic.currentPosition) > 1000 // 超过 1 秒
                
                _nowPlayingMusic.value = music
                Timber.d("Now playing: ${music?.title} - ${music?.artist}")
                
                // 同步到 WebSocket（如果启用）
                if (music != null && AppSettings.isWebSocketProtocolEnabled(context)) {
                    // 任何状态变化都触发同步：歌曲信息、播放状态、或进度显著变化
                    val shouldSync = isMusicChanged || isPlayingChanged || positionChangedSignificantly || music.isPlaying
                    if (shouldSync) {
                        syncPlaybackStateToWebSocket(music, isMusicChanged)
                    }
                }
                
                // 如果歌曲确实改变且有有效的歌曲信息，先尝试使用缓存，只有在缓存不可用时才清空并搜索
                if (isMusicChanged && music != null) {
                    // 兼容老旧酷狗缓存需要刷新空格的问题
                    val cached = lyricsCacheRepository.findBySong(music.title, music.artist)
                    if (cached != null) {
                        val shouldBypassCache = cached.source.contains("kugou", ignoreCase = true) ||
                            cached.source.contains("酷狗")
                        if (!shouldBypassCache) {
                            val parsed = LyricsRepository.parseTTML(cached.ttmlContent)
                            if (parsed != null) {
                                _lyrics.value = parsed
                                updateSongStructures(parsed)
                                _errorMessage.value = null
                                Timber.i("Loaded lyrics from cache (startup): ${cached.title} - ${cached.artist} (${cached.source})")
                                
                                // 同步到 WebSocket（如果启用）
                                if (AppSettings.isWebSocketProtocolEnabled(context)) {
                                    webSocketClient.sendLyrics(cached.ttmlContent)
                                    Timber.d("已同步启动缓存歌词到 WebSocket")
                                }
                                
                                // 已经拿到缓存，跳过后续搜索
                                return@collect
                            }
                        }
                    }

                    // 没有可用缓存时再清空并执行网络请求，以避免闪烁的遮罩
                    _lyrics.value = null
                    Timber.i("Music changed, auto-fetching lyrics...")
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
        val music = _nowPlayingMusic.value
        if (music == null) {
            _errorMessage.value = "未检测到播放信息"
            return
        }
    
        viewModelScope.launch {
            _errorMessage.value = null
    
            // first try to load from cache without toggling the loading flag; this avoids
            // showing a spinner/mask for cached data which is usually very fast.
            val cached = lyricsCacheRepository.findBySong(music.title, music.artist)
            if (cached != null) {
                val shouldBypassCache = cached.source.contains("kugou", ignoreCase = true) ||
                    cached.source.contains("酷狗")
                if (!shouldBypassCache) {
                    val parsed = LyricsRepository.parseTTML(cached.ttmlContent)
                    if (parsed != null) {
                        _lyrics.value = parsed
                        updateSongStructures(parsed)
                        _errorMessage.value = null
                        Timber.d("Loaded lyrics from cache: ${cached.title} - ${cached.artist} (${cached.source})")
                            
                        // ✅ 重置歌词哈希值，确保新歌词会被发送
                        lastSentLyricsHash = 0
                            
                        // 同步到 WebSocket（如果启用）
                        if (AppSettings.isWebSocketProtocolEnabled(context)) {
                            // 使用完整的同步方法，包含歌曲信息、专辑图和歌词
                            syncPlaybackStateToWebSocket(music, isMusicChanged = false)
                            Timber.d("已同步缓存歌词到 WebSocket")
                        }
                            
                        return@launch
                    }
                } else {
                    Timber.d("Bypassing stale Kugou cache to refresh whitespace-fixed lyrics")
                }
            }
    
            // no usable cache, fall back to network search
            _lyrics.value = null
            _isLoading.value = true
    
            try {
                Timber.i("Fetching lyrics for: ${music.title} - ${music.artist}")
    
                val sourceName = getAppNameFromPackage(context, music.packageName)
                val result = lyricsRepository.fetchLyricsAuto(
                    title = music.title,
                    artist = music.artist,
                    currentSourceName = sourceName
                )
    
                if (result.isSuccess && result.lyrics != null) {
                    _lyrics.value = result.lyrics
                    updateSongStructures(result.lyrics)
                    lyricsCacheRepository.upsert(
                        title = music.title,
                        artist = music.artist,
                        source = result.source ?: "auto",
                        ttmlContent = TTMLConverter.toTTMLString(result.lyrics)
                    )
                    Timber.i("Successfully fetched lyrics from ${result.source}")
                        
                    // ✅ 重置歌词哈希值，确保新歌词会被发送
                    lastSentLyricsHash = 0
                        
                    // 刷新歌曲结构（重搜歌词时）
                    refreshSongStructures()
                        
                    // 同步到 WebSocket（如果启用）
                    if (AppSettings.isWebSocketProtocolEnabled(context)) {
                        // 使用完整的同步方法，包含歌曲信息、专辑图和歌词
                        syncPlaybackStateToWebSocket(music, isMusicChanged = false)
                        Timber.d("已同步网络歌词到 WebSocket")
                    }
                } else {
                    _errorMessage.value = result.errorMessage ?: "获取歌词失败"
                    Timber.e("Failed to fetch lyrics: ${result.errorMessage}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "错误：${e.message}"
                Timber.e(e, "Error fetching lyrics")
            } finally {
                _isLoading.value = false
            }
        }
    }
        
    /**
     * 刷新 WebSocket 连接（强制重连）
     * 用于当用户点击刷新按钮时，重新建立 WebSocket 连接以同步最新状态
     * ✅ 此方法会完全断开旧连接并重新初始化，确保服务器端视为全新连接
     */
    fun refreshWebSocketConnection() {
        if (!AppSettings.isWebSocketProtocolEnabled(context)) {
            Timber.d("WebSocket 协议未启用，跳过刷新")
            return
        }
            
        val serverUrl = AppSettings.getWebSocketProtocolAddress(context)
        if (serverUrl.isBlank()) {
            Timber.w("WebSocket 地址为空，跳过刷新")
            return
        }
            
        Timber.i("🔄 强制刷新 WebSocket 连接：$serverUrl")
        // ✅ 使用 forceReconnect=true 强制断开并重连，确保发送完整的初始化消息
        webSocketClient.connect(serverUrl, forceReconnect = true)
        
        // 同时刷新歌曲结构（刷新按钮）
        refreshSongStructures()
    }
    
    /**
     * 从 LRC 文本生成 TTML
     */
    fun convertLRCToTTML(lrcContent: String, title: String, artist: String) {
        viewModelScope.launch {
            try {
                val ttml = TTMLConverter.fromLyrics(
                    content = lrcContent,
                    title = title,
                    artist = artist,
                    processMetadata = AppSettings.isMetadataProcessingEnabled(context)
                )
                if (ttml != null) {
                    _lyrics.value = ttml
                    updateSongStructures(ttml)
                    Timber.i("Successfully converted LRC to TTML")
                } else {
                    _errorMessage.value = "LRC 转换失败"
                }
            } catch (e: Exception) {
                _errorMessage.value = "转换错误: ${e.message}"
                Timber.e(e, "Error converting LRC to TTML")
            }
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
                val format = com.amll.droidmate.data.parser.LyricsFormat.detect(trimmed)
                
                var parsed: TTMLLyrics?
                var cachedTtmlContent: String
                
                when (format) {
                    // ✅ TTML 格式直接解析，保留完整的歌曲结构信息
                    com.amll.droidmate.data.parser.LyricsFormat.TTML -> {
                        Timber.d("[SongStructure] TTML format detected, parsing directly to preserve song structures")
                        try {
                            parsed = com.amll.droidmate.data.parser.TTMLParser.parse(trimmed)
                            // ✅ 对于 TTML 格式，直接保存原始内容，避免 toTTMLString 丢失歌曲结构
                            cachedTtmlContent = trimmed
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to parse TTML directly")
                            parsed = null
                            cachedTtmlContent = ""
                        }
                    }
                    // ✅ 其他格式使用 UnifiedLyricsParser（通过 TTMLConverter.fromLyrics）
                    else -> {
                        Timber.d("[SongStructure] Non-TTML format ($format), using UnifiedLyricsParser")
                        parsed = TTMLConverter.fromLyrics(
                            content = trimmed,
                            title = if (title.isBlank()) "自选歌词" else title,
                            artist = if (artist.isBlank()) "Unknown" else artist,
                            processMetadata = AppSettings.isMetadataProcessingEnabled(context)
                        )
                        // 非 TTML 格式需要转换后缓存
                        cachedTtmlContent = parsed?.let { TTMLConverter.toTTMLString(it) } ?: ""
                    }
                }
    
                if (parsed != null && cachedTtmlContent.isNotBlank()) {
                    _lyrics.value = parsed
                    updateSongStructures(parsed)
                    _errorMessage.value = null
                    lyricsCacheRepository.upsert(
                        title = if (title.isBlank()) "自选歌词" else title,
                        artist = if (artist.isBlank()) "Unknown" else artist,
                        source = source,
                        ttmlContent = cachedTtmlContent
                    )
                                    
                    // ✅ 重置歌词哈希值，确保新歌词会被发送
                    lastSentLyricsHash = 0
                                    
                    // ✅ 如果启用了 WebSocket，重新发送歌词和当前播放状态
                    val music = _nowPlayingMusic.value
                    if (music != null && AppSettings.isWebSocketProtocolEnabled(context)) {
                        Timber.d("用户选择了新歌词，重新同步到 WebSocket")
                        syncPlaybackStateToWebSocket(music, isMusicChanged = true)
                    }
                } else {
                    _errorMessage.value = "无法识别歌词格式"
                }
            } catch (e: Exception) {
                _errorMessage.value = "应用歌词失败：${e.message}"
                Timber.e(e, "Error applying custom lyrics input")
            }
        }
    }
    
    /**
     * 导出歌词为 TTML 文件
     */
    fun exportLyricsAsTTML(): String? {
        val currentLyrics = _lyrics.value ?: return null
        return TTMLConverter.toTTMLString(currentLyrics, formatted = true)
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
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
        Timber.d("MainViewModel.seekTo(position=$position)")
        mediaInfoService.seekTo(position)
    }
    
    fun fastForward() {
        mediaInfoService.fastForward()
    }
    
    fun rewind() {
        mediaInfoService.rewind()
    }
    
    /**
     * 设置系统音量
     * @param volume 音量值，范围 0.0-1.0
     */
    fun setVolume(volume: Double) {
        // 将 0.0-1.0 的音量转换为系统音量级别（0-15）
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
        val targetVolume = (volume * maxVolume).toInt().coerceIn(0, maxVolume)
        audioManager.setStreamVolume(
            android.media.AudioManager.STREAM_MUSIC,
            targetVolume,
            0  // 不显示 UI
        )
        Timber.i("MainViewModel: 音量已设置：$volume -> $targetVolume/$maxVolume")
    }
    
    override fun onCleared() {
        super.onCleared()
        // unregister broadcast listener added earlier
        context.unregisterReceiver(deleteReceiver)
        lyricNotificationManager.cancel()
        mediaInfoService.stopListening()
        
        // 如果 WebSocket 前台服务正在运行，停止它
        if (WebSocketForegroundService.isServiceRunning()) {
            WebSocketForegroundService.stop(context)
        }
        
        httpClient.close()
    }
    
    /**
     * 同步播放状态到 WebSocket 服务器
     * @param music 当前播放的音乐信息
     * @param isMusicChanged 歌曲信息是否发生变化
     */
    private fun syncPlaybackStateToWebSocket(music: NowPlayingMusic, isMusicChanged: Boolean) {
        try {
            // 检查 WebSocket 是否已连接
            if (!webSocketClient.isConnected()) {
                Timber.d("WebSocket 未连接，跳过同步")
                return
            }
            
            // 如果歌曲信息变化，发送歌曲信息
            if (isMusicChanged) {
                webSocketClient.sendMusicInfo(
                    musicId = music.packageName ?: "unknown",
                    musicName = music.title,
                    albumName = music.album ?: "",
                    artistName = music.artist,
                    duration = music.duration
                )
                Timber.d("已同步歌曲信息到 WebSocket: ${music.title} - ${music.artist}")
                
                // ✅ 只有在歌曲变化时才发送专辑图（避免频繁发送）
                if (!music.albumArtUri.isNullOrBlank()) {
                    sendAlbumArtToWebSocket(music.albumArtUri)
                }
            }
            
            // 发送播放/暂停状态
            val stateMessage = if (music.isPlaying) {
                com.amll.droidmate.websocket.WsProtocolV2Helper.createResumedUpdate()
            } else {
                com.amll.droidmate.websocket.WsProtocolV2Helper.createPausedUpdate()
            }
            webSocketClient.send(stateMessage)
            Timber.d("已同步播放状态到 WebSocket: ${if (music.isPlaying) "播放" else "暂停"}")
            
            // ✅ 发送当前播放进度（实时同步）
            val progressMessage = com.amll.droidmate.websocket.WsProtocolV2Helper.createProgressUpdate(music.currentPosition)
            webSocketClient.send(progressMessage)
            Timber.d("已同步播放进度到 WebSocket: ${music.currentPosition}ms")
            
            // ✅ 如果有歌词，发送歌词（仅在歌词变化时）
            val lyrics = _lyrics.value
            if (lyrics != null) {
                try {
                    val ttmlContent = buildTtmlForLyrics(lyrics)
                    if (!ttmlContent.isNullOrBlank()) {
                        // 计算歌词内容的哈希值，避免重复发送相同内容
                        val currentHash = ttmlContent.hashCode()
                        if (currentHash != lastSentLyricsHash) {
                            webSocketClient.sendLyrics(ttmlContent)
                            Timber.d("已同步歌词到 WebSocket (${ttmlContent.length} chars, hash=$currentHash)")
                            lastSentLyricsHash = currentHash
                        } else {
                            // 歌词未变化，跳过发送（减少网络流量）
                            Timber.v("歌词未变化，跳过发送")
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "构建或发送 TTML 歌词失败：${e.message}")
                    // 不抛出异常，继续其他操作
                }
            }
            
        } catch (e: Exception) {
            Timber.e(e, "同步播放状态到 WebSocket 失败")
        }
    }
    
    /**
     * 将专辑图 URI 转换为 Base64 Data URL 并发送到 WebSocket
     * @param albumArtUri 专辑图 URI（file:// 或 content://）
     */
    private fun sendAlbumArtToWebSocket(albumArtUri: String) {
        viewModelScope.launch {
            try {
                Timber.d("准备发送专辑图：$albumArtUri")
                val dataUrl = convertFileUriToDataUrl(context, albumArtUri)
                if (!dataUrl.isNullOrBlank()) {
                    webSocketClient.sendAlbumArt(dataUrl)
                    Timber.d("已同步专辑图到 WebSocket，大小：${dataUrl.length} chars")
                    Timber.d("专辑图 Data URL 预览：${dataUrl.take(100)}...")
                } else {
                    Timber.w("无法转换专辑图 URI 为 Data URL: $albumArtUri")
                }
            } catch (e: Exception) {
                Timber.e(e, "发送专辑图失败")
            }
        }
    }
    
    /**
     * 将 file:// URI 转换为 base64 data URL，以便 WebView 能够加载本地图片
     */
    private fun convertFileUriToDataUrl(context: Context, uriString: String?): String? {
        if (uriString.isNullOrBlank()) {
            Timber.w("convertFileUriToDataUrl: URI 为空")
            return null
        }
        
        return try {
            Timber.d("转换专辑图 URI: $uriString")
            val inputStream = when {
                uriString.startsWith("file://") -> {
                    val path = uriString.removePrefix("file://")
                    Timber.d("读取文件：$path")
                    File(path).inputStream()
                }
                uriString.startsWith("content://") -> {
                    val uri = android.net.Uri.parse(uriString)
                    Timber.d("打开内容流：$uri")
                    context.contentResolver.openInputStream(uri)
                }
                else -> {
                    Timber.w("Unsupported URI scheme: $uriString")
                    return uriString // 直接返回原始字符串（可能是 data URL）
                }
            }
            
            inputStream?.use { stream ->
                val bytes = stream.readBytes()
                Timber.d("读取到图片数据：${bytes.size} bytes")
                val mimeType = getMimeType(uriString) ?: "image/jpeg"
                val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                val dataUrl = "data:$mimeType;base64,$base64"
                Timber.d("生成 Data URL: mimeType=$mimeType, base64 length=${base64.length}")
                dataUrl
            } ?: run {
                Timber.e("无法打开输入流：$uriString")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert file URI to data URL: $uriString")
            null
        }
    }
    
    /**
     * 根据文件扩展名获取 MIME 类型
     */
    private fun getMimeType(uriString: String): String? {
        return when {
            uriString.endsWith(".png", ignoreCase = true) -> "image/png"
            uriString.endsWith(".jpg", ignoreCase = true) || uriString.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
            uriString.endsWith(".gif", ignoreCase = true) -> "image/gif"
            uriString.endsWith(".webp", ignoreCase = true) -> "image/webp"
            else -> "image/jpeg" // 默认使用 JPEG
        }
    }
    
    /**
     * 注册 WebSocket 监听器
     * 用于在 WebSocket 连接时提供当前播放状态
     */
    private fun registerWebSocketListener() {
        // 移除旧的监听器（如果存在）
        webSocketClient.removeListener(webSocketListener)
        // 注册新的监听器
        webSocketClient.addListener(webSocketListener)
        Timber.d("已注册 MainViewModel WebSocket 监听器")
    }
    
    /**
     * 将歌词构建为 TTML 字符串
     */
    /**
     * 构建 TTML 歌词字符串（复用统一转换器）
     */
    private fun buildTtmlForLyrics(lyrics: TTMLLyrics): String {
        // ✅ 复用 TTMLConverter.toTTMLString() 统一函数，确保包含翻译和音译
        return com.amll.droidmate.data.converter.TTMLConverter.toTTMLString(lyrics)
    }
    
    /**
     * 解析并更新歌曲结构信息
     * 从歌词行自动推断歌曲结构（前奏、间奏、尾奏、主歌、副歌等）
     */
    private fun updateSongStructures(lyrics: TTMLLyrics) {
        viewModelScope.launch {
            try {
                // 获取歌曲总时长用于检测尾奏
                val songDuration = _nowPlayingMusic.value?.duration ?: 0L
                
                // 检查是否有元数据中的结构信息
                val metadataStructures = lyrics.metadata.songStructures
                Timber.d("[SongStructure] 📋 MainViewModel: metadata.songStructures = $metadataStructures")
                Timber.d("[SongStructure] 📋 metadataStructures.isNullOrEmpty() = ${metadataStructures.isNullOrEmpty()}")
                if (!metadataStructures.isNullOrEmpty()) {
                    Timber.d("[SongStructure] ✅ MainViewModel: 使用 TTML 元数据中的结构：${metadataStructures.size} 个")
                    metadataStructures.forEachIndexed { index, structure ->
                        val startMin = structure.startTime / 60000
                        val startSec = (structure.startTime % 60000) / 1000
                        val endMin = structure.endTime / 60000
                        val endSec = (structure.endTime % 60000) / 1000
                        Timber.d("  [$index] ${structure.label} (${structure.type.displayName}): ${String.format("%d:%02d", startMin, startSec)} - ${String.format("%d:%02d", endMin, endSec)}")
                    }
                } else {
                    Timber.w("[SongStructure] ⚠️ MainViewModel: 无元数据结构，将触发 SongStructureParser.parseStructure() fallback")
                    Timber.w("[SongStructure] ⚠️ 最终会显示为 '段落 1'")
                }
                
                val structures = SongStructureParser.parseStructure(lyrics.lines, lyrics.metadata.songStructures, songDuration)
                _songStructures.value = structures
                Timber.d("[SongStructure] 🎯 MainViewModel: 最终 structures.size = ${structures.size}")
                structures.forEachIndexed { index, structure ->
                    val startMin = structure.startTime / 60000
                    val startSec = (structure.startTime % 60000) / 1000
                    val endMin = structure.endTime / 60000
                    val endSec = (structure.endTime % 60000) / 1000
                    Timber.d("  [$index] ${structure.label} (${structure.type.displayName}): ${String.format("%d:%02d", startMin, startSec)} - ${String.format("%d:%02d", endMin, endSec)}")
                }
                
                // 诊断最终结果
                if (structures.size == 1 && structures[0].label == "段落 1") {
                    Timber.w("[SongStructure] ❌ 检测到 fallback 结果：单一段落 '段落 1'")
                    Timber.w("[SongStructure] ❌ 这表明原始 TTML 中没有有效的歌曲结构信息")
                }
            } catch (e: Exception) {
                Timber.e(e, "[SongStructure] 解析歌曲结构失败")
                _songStructures.value = emptyList()
            }
        }
    }
    
    /**
     * 刷新歌曲结构信息
     * 用于重搜歌词或刷新时重新解析结构
     */
    fun refreshSongStructures() {
        val currentLyrics = _lyrics.value ?: return
        updateSongStructures(currentLyrics)
        Timber.i("[SongStructure] 刷新歌曲结构")
    }
}