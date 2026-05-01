package io.github.zeehan2005.scoremuse.global.viewmodel

import android.Manifest
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.amll.droidmate.data.converter.TTMLConverter
import com.amll.droidmate.websocket.AMLLWebSocketClient
import com.amll.droidmate.websocket.WsProtocolV2Helper
import dev.amll.droidmate.global.AMLLSettings
import dev.amll.droidmate.websocket.WebSocketForegroundService
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import kotlin.math.abs
import kotlin.text.get
import java.io.File
import android.net.Uri
import android.util.Base64

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
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val context: Context = application.applicationContext

    // ==================== HTTP Client & 仓库 ====================
    // HTTP 客户端（由 ServiceLocator 提供以便集中管理）
    private val httpClient = ServiceLocator.provideHttpClient(context)
    // make this mutable so tests can inject a fake repository if needed
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var lyricsRepository = ServiceLocator.provideLyricsRepository(context)
    private val lyricsCacheRepository = ServiceLocator.provideLyricsCacheRepository(context)
    
    // ==================== 服务 ====================
    // 媒体信息监听服务（监听系统媒体播放状态）
    private val mediaInfoService = MediaInfoService(context)
    
    // ==================== WebSocket 客户端 ====================
    // WebSocket 客户端（用于同步播放状态到外部服务）
    private val webSocketClient = AMLLWebSocketClient.getInstance()
    
    // 用于跟踪上次发送的歌词，避免重复发送相同内容
    private var lastSentLyricsHash: Int = 0
    
    // ==================== WebSocket 监听器 ====================
    // WebSocket 监听器（用于在连接时提供当前播放状态）
    // 使用 lazy 延迟初始化，避免在构造函数外访问 this
    private val webSocketListener: AMLLWebSocketClient.Listener by lazy {
        object : AMLLWebSocketClient.Listener {
            override fun onConnected() {
                Timber.d("[WebSocket] WebSocket connected")
                
                // ✅ 每次连接（包括重连）都重新同步完整的播放状态
                val music = mediaInfoService.nowPlayingMusic.value
                if (music != null && AMLLSettings.isWebSocketProtocolEnabled(context)) {
                    Timber.d("[WebSocket] Reconnected, syncing full playback state")
                    // isMusicChanged=true 会触发歌曲信息和专辑图的发送
                    syncPlaybackStateToWebSocket(music, isMusicChanged = true)
                }
            }
            
            override fun onDisconnected() {
                Timber.w("[WebSocket] WebSocket disconnected")
            }
            
            override fun onMessageReceived(message: String) {
                Timber.d("[WebSocket] Received message: $message")
                
                // 解析并执行命令
                try {
                    val json = Json.parseToJsonElement(message)
                    val type = json.jsonObject["type"]?.jsonPrimitive?.content
                    
                    if (type == "command") {
                        val valueObj = json.jsonObject["value"]?.jsonObject
                        val command = valueObj?.get("command")?.jsonPrimitive?.content
                        
                        Timber.i("[PlaybackControl] Received command: $command")
                        
                        when (command) {
                            "pause" -> {
                                Timber.i("[PlaybackControl] Executing pause command")
                                pause()
                                Timber.d("[PlaybackControl] Pause command completed")
                            }
                            "resume" -> {
                                Timber.i("[PlaybackControl] Executing resume command")
                                play()
                                Timber.d("[PlaybackControl] Resume command completed")
                            }
                            "forwardSong" -> {
                                Timber.i("[PlaybackControl] Executing skip to next command")
                                skipToNext()
                                Timber.d("[PlaybackControl] Skip to next command completed")
                            }
                            "backwardSong" -> {
                                Timber.i("[PlaybackControl] Executing skip to previous command")
                                skipToPrevious()
                                Timber.d("[PlaybackControl] Skip to previous command completed")
                            }
                            "seekPlayProgress" -> {
                                val progress = valueObj?.get("progress")?.jsonPrimitive?.content?.toLongOrNull()
                                if (progress != null) {
                                    Timber.i("[PlaybackControl] Executing seek command: $progress ms")
                                    seekTo(progress)
                                    Timber.d("[PlaybackControl] Seek command completed")
                                } else {
                                    Timber.w("[PlaybackControl] Invalid seek command parameter")
                                }
                            }
                            "setVolume" -> {
                                val volume = valueObj?.get("volume")?.jsonPrimitive?.content?.toDoubleOrNull()
                                if (volume != null) {
                                    Timber.i("[PlaybackControl] Executing set volume command: $volume")
                                    setVolume(volume)
                                    Timber.d("[PlaybackControl] Set volume command completed")
                                } else {
                                    Timber.w("[PlaybackControl] Invalid set volume command parameter")
                                }
                            }
                            "setRepeatMode", "setShuffleMode" -> {
                                Timber.d("[PlaybackControl] Received unsupported command: $command, ignoring")
                            }
                            else -> {
                                Timber.d("[PlaybackControl] Unknown command: $command")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Timber.e("[WebSocket] Failed to parse command: ${e.message}", e)
                }
            }
            
            override fun onError(error: Throwable) {
                Timber.e("[WebSocket] WebSocket error: ${error.message}", error)
            }
            
            override fun getCurrentPlayState(): AMLLWebSocketClient.PlayState? {
                val music = mediaInfoService.nowPlayingMusic.value
                if (music == null) {
                    Timber.d("[WebSocket] getCurrentPlayState - no playback")
                    return null
                }
                
                // 检查是否有有效的歌曲信息
                val validMusicId = music.packageName?.takeIf { it.isNotEmpty() && it != "unknown" }
                val validMusicName = music.title.takeIf { it.isNotEmpty() && it != "Unknown" && it != "等待播放" }
                
                if (validMusicId == null || validMusicName == null) {
                    Timber.d("[WebSocket] getCurrentPlayState - invalid song info (musicId=$validMusicId, musicName=$validMusicName)")
                    return null
                }
                
                // 构建 TTML 歌词
                val ttmlContent = lyrics.value?.let { lyrics ->
                    try {
                        buildTtmlForLyrics(lyrics)
                    } catch (e: Exception) {
                        Timber.e("[LyricsMatcher] Failed to build TTML: ${e.message}", e)
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
                
                Timber.d("[WebSocket] getCurrentPlayState returns:")
                Timber.d("[WebSocket]  - musicId: ${state.musicId}")
                Timber.d("[WebSocket]  - musicName: ${state.musicName}")
                Timber.d("[WebSocket]  - artistName: ${state.artistName}")
                Timber.d("[WebSocket]  - hasLyrics: ${!state.ttmlLyric.isNullOrBlank()}")
                Timber.d("[WebSocket]  - isPlaying: ${state.isPlaying}")
                Timber.d("[WebSocket]  - progress: ${state.progress}ms")
                if (!state.ttmlLyric.isNullOrBlank()) {
                    Timber.d("[WebSocket]  - TTML preview: ${state.ttmlLyric.take(200)}...")
                }
                
                return state
            }
        }
    }

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
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
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

    init {
        setupMediaListener()
        observeLyricNotification()
        Timber.plant(Timber.DebugTree())

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
        context.registerReceiver(
            deleteReceiver,
            IntentFilter(LyricNotificationManager.ACTION_LYRIC_NOTIFICATION_DISMISSED),
            Context.RECEIVER_NOT_EXPORTED
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
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
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
                val isPlayingChanged = oldMusic?.isPlaying != music?.isPlaying

                // 检查进度是否显著变化（用于检测跳转操作）
                val positionChangedSignificantly = music != null && oldMusic != null &&
                        abs(music.currentPosition - oldMusic.currentPosition) > 1000 // 超过 1 秒

                nowPlayingMusicMutable.value = music

                // 同步到 WebSocket（如果启用）
                if (music != null && AMLLSettings.isWebSocketProtocolEnabled(context)) {
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
                            val parsed = LyricsRepository.parseTTML(cached.xmlContent)
                            if (parsed != null) {
                                lyricsMutable.value = parsed
                                updateSongStructures(parsed)
                                _errorMessage.value = null
                                Timber.i("[CacheManager] Loaded lyrics from cache (startup): ${cached.title} - ${cached.artist} (${cached.source})")

                                // 同步到 WebSocket（如果启用）
                                if (AMLLSettings.isWebSocketProtocolEnabled(context)) {
                                    webSocketClient.sendLyrics(cached.xmlContent)
                                    Timber.d("[WebSocket] Synced cached lyrics on startup")
                                }

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
    
        viewModelScope.launch {
            _errorMessage.value = null
    
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
                            
                        // 同步到 WebSocket（如果启用）
                        if (AMLLSettings.isWebSocketProtocolEnabled(context)) {
                            // 使用完整的同步方法，包含歌曲信息、专辑图和歌词
                            syncPlaybackStateToWebSocket(music, isMusicChanged = false)
                            Timber.d("[WebSocket] Synced cached lyrics")
                        }
                            
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
                Timber.i("[LyricsMatcher] Fetching lyrics for: ${music.title} - ${music.artist}")
    
                val sourceName = getAppNameFromPackage(context, music.packageName)
                val result = lyricsRepository.fetchLyricsAuto(
                    title = music.title,
                    artist = music.artist,
                    currentSourceName = sourceName
                )
    
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
                    val rawXmlContent = TTMLConverter.toTTMLString(result.lyrics!!)
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
                        
                    // 同步到 WebSocket（如果启用）
                    if (AMLLSettings.isWebSocketProtocolEnabled(context)) {
                        // 使用完整的同步方法，包含歌曲信息、专辑图和歌词
                        syncPlaybackStateToWebSocket(music, isMusicChanged = false)
                        Timber.d("[WebSocket] Synced network lyrics")
                    }
                } else {
                    _errorMessage.value = result.errorMessage ?: "获取歌词失败"
                    Timber.e("[LyricsMatcher] Failed to fetch lyrics: ${result.errorMessage}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "错误：${e.message}"
                Timber.e(e, "[LyricsMatcher] Error fetching lyrics")
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
        if (!AMLLSettings.isWebSocketProtocolEnabled(context)) {
            Timber.d("[WebSocket] WebSocket protocol not enabled, skipping refresh")
            return
        }
            
        val serverUrl = AMLLSettings.getWebSocketProtocolAddress(context)
        if (serverUrl.isBlank()) {
            Timber.w("[WebSocket] WebSocket address is empty, skipping refresh")
            return
        }
            
        Timber.i("[WebSocket] 🔄 Forcing WebSocket connection refresh: $serverUrl")
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
                    // ⭐ 修复关键：只有在设置启用时才处理元数据
                    processMetadata = AppSettings.isMetadataProcessingEnabled(context)
                )
                if (ttml != null) {
                    lyricsMutable.value = ttml
                    updateSongStructures(ttml)
                    Timber.i("[CustomLyrics] Successfully converted LRC to TTML")
                } else {
                    _errorMessage.value = "LRC 转换失败"
                }
            } catch (e: Exception) {
                _errorMessage.value = "转换错误：${e.message}"
                Timber.e(e, "[CustomLyrics] Error converting LRC to TTML")
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
                val format = LyricsFormat.detect(trimmed)
                
                var parsed: UnifiedLyrics?
                var cachedXmlContent: String
                
                when (format) {
                    // ✅ ScoreMuse XML 格式直接解析，保留完整的歌曲结构信息
                    LyricsFormat.SCOREMUSE_XML -> {
                        Timber.d("[SongStructure] ScoreMuse XML format detected, parsing directly to preserve song structures")
                        try {
                            parsed = TTMLConverter.fromLyrics(trimmed)
                            // ✅ 对于 XML 格式，直接保存原始内容，避免 toXMLString 丢失歌曲结构
                            cachedXmlContent = trimmed
                        } catch (e: Exception) {
                            Timber.e(e, "[TTMLConverter] Failed to parse ScoreMuse XML directly")
                            parsed = null
                            cachedXmlContent = ""
                        }
                    }
                    // ✅ 其他格式使用 UnifiedLyricsParser（通过 TTMLConverter.fromLyrics）
                    else -> {
                        Timber.d("[SongStructure] Non-XML format ($format), using UnifiedLyricsParser")
                        parsed = TTMLConverter.fromLyrics(
                            content = trimmed,
                            title = title.ifBlank { "自选歌词" },
                            artist = artist.ifBlank { "Unknown" }
                        )
                        // 非 XML 格式需要转换后缓存为 ScoreMuse XML
                        cachedXmlContent = parsed?.let { TTMLConverter.toTTMLString(it) } ?: ""
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
                                    
                    // ✅ 如果启用了 WebSocket，重新发送歌词和当前播放状态
                    val music = nowPlayingMusicMutable.value
                    if (music != null && AMLLSettings.isWebSocketProtocolEnabled(context)) {
                        Timber.d("[CustomLyrics] User selected new lyrics, resyncing to WebSocket")
                        syncPlaybackStateToWebSocket(music, isMusicChanged = true)
                    }
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
    
    fun setVolume(volume: Double) {
        Timber.d("[PlaybackControl] setVolume(volume=$volume)")
        mediaInfoService.setVolume(volume)
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
                Timber.d("[WebSocket] Not connected, skipping sync")
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
                Timber.d("[WebSocket] Synced song info: ${music.title} - ${music.artist}")
                
                // ✅ 只有在歌曲变化时才发送专辑图（避免频繁发送）
                if (!music.albumArtUri.isNullOrBlank()) {
                    sendAlbumArtToWebSocket(music.albumArtUri)
                }
            }
            
            // 发送播放/暂停状态
            val stateMessage = if (music.isPlaying) {
                WsProtocolV2Helper.createResumedUpdate()
            } else {
                WsProtocolV2Helper.createPausedUpdate()
            }
            webSocketClient.send(stateMessage)
            Timber.d("[WebSocket] Synced playback state to WebSocket: ${if (music.isPlaying) "playing" else "paused"}")
            
            // ✅ 发送当前播放进度（实时同步）
            val progressMessage = WsProtocolV2Helper.createProgressUpdate(music.currentPosition)
            webSocketClient.send(progressMessage)
            Timber.d("[WebSocket] Synced playback progress to WebSocket: ${music.currentPosition}ms")
            
            // ✅ 如果有歌词，发送歌词（仅在歌词变化时）
            val lyrics = lyricsMutable.value
            if (lyrics != null) {
                try {
                    val ttmlContent = buildTtmlForLyrics(lyrics)
                    if (!ttmlContent.isNullOrBlank()) {
                        // 计算歌词内容的哈希值，避免重复发送相同内容
                        val currentHash = ttmlContent.hashCode()
                        if (currentHash != lastSentLyricsHash) {
                            webSocketClient.sendLyrics(ttmlContent)
                            Timber.d("[WebSocket] Synced lyrics to WebSocket (${ttmlContent.length} chars, hash=$currentHash)")
                            lastSentLyricsHash = currentHash
                        } else {
                            // 歌词未变化，跳过发送（减少网络流量）
                            Timber.v("[WebSocket] Lyrics unchanged, skipping send")
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "[WebSocket] Failed to build or send TTML lyrics: ${e.message}")
                    // 不抛出异常，继续其他操作
                }
            }
            
        } catch (e: Exception) {
            Timber.e(e, "[WebSocket] Failed to sync playback state")
        }
    }
    
    /**
     * 将专辑图 URI 转换为 Base64 Data URL 并发送到 WebSocket
     * @param albumArtUri 专辑图 URI（file:// 或 content://）
     */
    fun sendAlbumArtToWebSocket(albumArtUri: String) {
        viewModelScope.launch {
            try {
                Timber.d("[AlbumArtExtractor] Preparing to send album art: $albumArtUri")
                val dataUrl = convertFileUriToDataUrl(context, albumArtUri)
                if (!dataUrl.isNullOrBlank()) {
                    webSocketClient.sendAlbumArt(dataUrl)
                    Timber.d("[WebSocket] Synced album art to WebSocket, size: ${dataUrl.length} chars")
                    Timber.d("[WebSocket]  - Album art Data URL preview: ${dataUrl.take(100)}...")
                } else {
                    Timber.w("[AlbumArtExtractor] Failed to convert album art URI to Data URL: $albumArtUri")
                }
            } catch (e: Exception) {
                Timber.e("[AlbumArtExtractor] Failed to send album art: ${e.message}", e)
            }
        }
    }
    
    /**
     * 将 file:// URI 转换为 base64 data URL，以便 WebView 能够加载本地图片
     */
    private fun convertFileUriToDataUrl(context: Context, uriString: String?): String? {
        if (uriString.isNullOrBlank()) {
            Timber.w("[AlbumArtExtractor] URI is empty")
            return null
        }
        
        return try {
            Timber.d("[AlbumArtExtractor] Converting album art URI: $uriString")
            val inputStream = when {
                uriString.startsWith("file://") -> {
                    val path = uriString.removePrefix("file://")
                    Timber.d("[AlbumArtExtractor] Reading file: $path")
                    File(path).inputStream()
                }
                uriString.startsWith("content://") -> {
                    val uri = Uri.parse(uriString)
                    Timber.d("[AlbumArtExtractor] Opening content stream: $uri")
                    context.contentResolver.openInputStream(uri)
                }
                else -> {
                    Timber.w("[Network] Unsupported URI scheme: $uriString")
                    return uriString // 直接返回原始字符串（可能是 data URL）
                }
            }
            
            inputStream?.use { stream ->
                val bytes = stream.readBytes()
                Timber.d("[AlbumArtExtractor] Read image data: ${bytes.size} bytes")
                val mimeType = getMimeType(uriString) ?: "image/jpeg"
                val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                val dataUrl = "data:$mimeType;base64,$base64"
                Timber.d("[AlbumArtExtractor] Generated Data URL: mimeType=$mimeType, base64 length=${base64.length}")
                dataUrl
            } ?: run {
                Timber.e("[AlbumArtExtractor] Cannot open input stream: $uriString")
                null
            }
        } catch (e: Exception) {
            Timber.e("[AlbumArtExtractor] Failed to convert file URI to data URL: $uriString: ${e.message}", e)
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
        Timber.d("[WebSocket] 已注册 MainViewModel WebSocket 监听器")
    }
    
    /**
     * 将歌词构建为 TTML 字符串
     */
    private fun buildTtmlForLyrics(lyrics: UnifiedLyrics): String? {
        return try {
            TTMLConverter.toTTMLString(lyrics)
        } catch (e: Exception) {
            Timber.e(e, "[TTMLConverter] Failed to build TTML")
            null
        }
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
                Timber.d("[SongStructure] Metadata structures count: ${metadataStructures?.size ?: 0}")
                Timber.d("[SongStructure] Current music: ${nowPlayingMusicMutable.value?.title} - ${nowPlayingMusicMutable.value?.artist}")
                Timber.d("[SongStructure] Lyrics source: ${lyrics.metadata.source}")
                if (!metadataStructures.isNullOrEmpty()) {
                    Timber.d("[SongStructure] ✅ Using ${metadataStructures.size} structures from TTML metadata")
                    metadataStructures.forEachIndexed { index, structure ->
                        val startMin = structure.startTime / 60000
                        val startSec = (structure.startTime % 60000) / 1000
                        val endMin = structure.endTime / 60000
                        val endSec = (structure.endTime % 60000) / 1000
                        Timber.d("[SongStructure] [$index] ${structure.label} (${structure.type.displayName}): ${String.format("%d:%02d", startMin, startSec)} - ${String.format("%d:%02d", endMin, endSec)}")
                    }
                } else {
                    Timber.i("[SongStructure] ⚠️ No metadata structures found, will use SongStructureParser fallback")
                    Timber.d("[SongStructure] Lyrics has ${lyrics.lines.size} lines")
                }
                
                val structures = SongStructureParser.parseStructure(lyrics.lines, lyrics.metadata.songStructures, songDuration)
                _songStructures.value = structures
                Timber.d("[SongStructure] 📊 Final parsed ${structures.size} structures")
                structures.forEachIndexed { index, structure ->
                    val startMin = structure.startTime / 60000
                    val startSec = (structure.startTime % 60000) / 1000
                    val endMin = structure.endTime / 60000
                    val endSec = (structure.endTime % 60000) / 1000
                    Timber.d("[SongStructure] 🎵 [$index] ${structure.label} (${structure.type.displayName}): ${String.format("%d:%02d", startMin, startSec)} - ${String.format("%d:%02d", endMin, endSec)}")
                }
                
                // 诊断最终结果
                if (structures.size == 1 && structures[0].label == "段落 1") {
                    Timber.i("[SongStructure] ⚠️ Fallback result: single paragraph structure")
                }
            } catch (e: Exception) {
                Timber.e("[SongStructure] ❌ Failed to parse song structure: ${e.message}", e)
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