package io.github.zeehan2005.scoremuse.service

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import io.github.zeehan2005.scoremuse.global.NowPlayingMusic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale

/**
 * 媒体信息监听服务 - 获取当前播放的歌曲信息
 *
 * 性能优化：
 * - 使用 Coroutine + Dispatchers.IO 在后台线程执行轮询
 * - 专辑图片异步处理（不再缓存，专辑图变化时直接覆盖旧文件）
 * - Flow 发射优化，仅在关键数据变化时更新
 */
class MediaInfoService(private val context: Context) {

    private val _nowPlayingMusic = MutableStateFlow<NowPlayingMusic?>(null)
    val nowPlayingMusic: StateFlow<NowPlayingMusic?> = _nowPlayingMusic

    // 后台协程作用域，使用 IO 调度器
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val mediaSessionManager: MediaSessionManager? = try {
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
    } catch (e: Exception) {
        Timber.e("[MediaInfoService] Failed to get MediaSessionManager $e")
        null
    }

    private val listenerComponentName = ComponentName(context, MediaListenerService::class.java)

    private var currentController: MediaController? = null



    /**
     * 启动监听
     */
    fun startListening() {
        Timber.i("[MediaInfoService] Starting media info listener")
        scheduleNextUpdate()
    }

    /**
     * 停止监听
     */
    fun stopListening() {
        Timber.i("[MediaInfoService] Stopping media info listener")
        serviceScope.cancel()
    }

    /**
     * 更新媒体信息（在 IO 线程执行）
     */
    private suspend fun updateMediaInfo() {
        if (!serviceScope.isActive) return

        try {
            val activeSessions = withContext(Dispatchers.IO) {
                mediaSessionManager?.getActiveSessions(listenerComponentName)
            }

            if (!activeSessions.isNullOrEmpty()) {
                val controller = activeSessions[0]
                currentController = controller
                val metadata = controller.metadata
                val playbackState = controller.playbackState
                val packageName = controller.packageName

                if (metadata != null && playbackState != null) {
                    val title = metadata.getString(android.media.MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown"
                    val artist = metadata.getString(android.media.MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown"
                    val album = metadata.getString(android.media.MediaMetadata.METADATA_KEY_ALBUM)
                    val duration = metadata.getLong(android.media.MediaMetadata.METADATA_KEY_DURATION)
                    val position = playbackState.position
                    val isPlaying = playbackState.state == android.media.session.PlaybackState.STATE_PLAYING

                    // 旧音乐对象（用于对比）
                    val oldMusic = _nowPlayingMusic.value

                    // 当前已展示的专辑图 URI
                    val currentAlbumArtUri = oldMusic?.albumArtUri

                    // 尝试从播放源获取专辑图 Bitmap（不是 URI）
                    val newAlbumArtBitmap = metadata.getBitmap(android.media.MediaMetadata.METADATA_KEY_ALBUM_ART)
                        ?: metadata.getBitmap(android.media.MediaMetadata.METADATA_KEY_ART)

                    // 智能判断是否需要处理专辑图
                    val finalAlbumArtUri = when {
                        // 情况 1: 获取到了新的 Bitmap → 重新写入文件（旧的同名文件会被销毁）
                        newAlbumArtBitmap != null -> {
                            processAlbumArtBitmap(bitmap = newAlbumArtBitmap, title, artist, packageName)
                        }
                        // 情况 2: 当前没有专辑图，尝试从 URI 获取
                        currentAlbumArtUri.isNullOrBlank() -> {
                            val uriString = metadata.getString(android.media.MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
                                ?: metadata.getString(android.media.MediaMetadata.METADATA_KEY_ART_URI)
                            if (!uriString.isNullOrBlank()) {
                                Timber.d("[MediaInfoService] No cached album art, fetching from URI...")
                                processAlbumArtAsync(metadata, title, artist, packageName)
                            } else {
                                null
                            }
                        }
                        // 情况 3: 保持使用当前的专辑图（无变化）
                        else -> {
                            currentAlbumArtUri
                        }
                    }

                    // 构建最终的音乐对象（包含最新播放时间和专辑图）
                    val updatedMusic = NowPlayingMusic(
                        title = title,
                        artist = artist,
                        album = album,
                        duration = duration,
                        currentPosition = position,
                        isPlaying = isPlaying,
                        packageName = packageName,
                        albumArtUri = finalAlbumArtUri,
                        timestamp = System.currentTimeMillis()
                    )


                        _nowPlayingMusic.value = updatedMusic
                }
            } else {
                currentController = null
            }
        } catch (e: SecurityException) {
            Timber.e("[MediaInfoService] Permission denied to access media sessions $e")
            updateMediaInfoViaContentResolver()
        } catch (e: Exception) {
            Timber.e("[MediaInfoService] Error updating media info $e")
        }
    }

    /**
     * 通过 ContentResolver 获取媒体信息（备选方案）
     */
    private fun updateMediaInfoViaContentResolver() {
        try {
            // 注：这是一个简化的实现
            // 实际应用可能需要使用 MediaStore 或其他方式
            Timber.i("[MediaInfoService] Attempting to get media info via ContentResolver")
        } catch (e: Exception) {
            Timber.e("[MediaInfoService] Error getting media info via ContentResolver $e")
        }
    }

    /**
     * 直接从 Bitmap 处理专辑图（不再使用内存缓存，每次写入都会覆盖旧文件）
     */
    private suspend fun processAlbumArtBitmap(
        bitmap: Bitmap,
        title: String,
        artist: String,
        packageName: String?
    ): String? = withContext(Dispatchers.IO) {
        try {
            // 生成缓存 key
            val cacheKey = "${packageName ?: "unknown"}_${title}_$artist"

            // 保存（并销毁旧文件）
            saveAlbumArtBitmapToCache(
                bitmap = bitmap,
                cacheKey = cacheKey
            )
        } catch (e: Exception) {
            Timber.e("[AlbumArtExtractor] Failed to process album art bitmap $e")
            null
        }
    }

    /**
     * 异步处理专辑封面（在 IO 线程执行）
     * 从 URI 获取专辑图（不再使用内存缓存）
     */
    private suspend fun processAlbumArtAsync(
        metadata: android.media.MediaMetadata,
        title: String,
        artist: String,
        packageName: String?
    ): String? = withContext(Dispatchers.IO) {
        try {
            // 生成缓存 key
            val cacheKey = "${packageName ?: "unknown"}_${title}_$artist"

            // 获取专辑图 Bitmap
            val albumArtBitmap = metadata.getBitmap(android.media.MediaMetadata.METADATA_KEY_ALBUM_ART)
                ?: metadata.getBitmap(android.media.MediaMetadata.METADATA_KEY_ART)

            if (albumArtBitmap == null) {
                Timber.i("[AlbumArtExtractor] Failed to get bitmap, trying URI")
                return@withContext metadata.getString(android.media.MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
                    ?: metadata.getString(android.media.MediaMetadata.METADATA_KEY_ART_URI)
            }

            // 保存（并销毁旧文件）
            saveAlbumArtBitmapToCache(
                bitmap = albumArtBitmap,
                cacheKey = cacheKey
            )
        } catch (e: Exception) {
            Timber.e("[AlbumArtExtractor] Failed to process album art $e")
            null
        }
    }

    /**
     * 保存专辑图 Bitmap 到磁盘
     *
     * 行为变更：
     * - 移除了内存缓存（albumArtCache）以及「文件已存在则直接返回」的逻辑
     * - 每次写入新的专辑图前，都会先销毁当前缓存目录中的所有旧专辑图文件，
     *   以确保专辑图变化时能即时反映到前端，不会出现「同一首歌内残留旧图」的情况
     */
    private fun saveAlbumArtBitmapToCache(
        bitmap: Bitmap,
        cacheKey: String
    ): String? {
        return try {
            val cacheDir = File(context.cacheDir, "album_art")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            // 使用哈希值作为文件名，避免特殊字符问题
            val safeKey = cacheKey.hashCode().toUInt().toString(16)
            val file = File(cacheDir, "album_art_${safeKey}.jpg")

            // 销毁旧的专辑图文件（包括同名旧文件和不同 cacheKey 的旧文件），
            // 保证不会因为缓存命中错误而展示错误的专辑图
            deleteAllAlbumArtCache(cacheDir, excluding = file)

            // 缩放图片至最大 512x512，减少内存占用
            val scaledBitmap = resizeBitmap(bitmap)

            try {
                FileOutputStream(file).use { out ->
                    // 压缩质量 75，显著减少文件大小
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, out)
                }
            } finally {
                // 关键修复：仅在创建了新 Bitmap 时才调用 recycle()
                // 如果图片本身小于 512px，resizeBitmap 会返回原始 bitmap，
                // 此时不应 recycle，因为它可能仍被 MediaSession/Metadata 使用。
                if (scaledBitmap !== bitmap) {
                    scaledBitmap.recycle()
                }
            }

            val uri = "file://${file.absolutePath}"
            Timber.d("[AlbumArtExtractor] Album art saved to: $uri")
            uri
        } catch (e: Exception) {
            Timber.e("[AlbumArtExtractor] Failed to save album art to cache $e")
            null
        }
    }

    /**
     * 删除缓存目录中除指定保留文件以外的所有专辑图文件
     */
    private fun deleteAllAlbumArtCache(cacheDir: File, excluding: File) {
        try {
            val files = cacheDir.listFiles() ?: return
            for (f in files) {
                if (f.isFile && f.absolutePath != excluding.absolutePath) {
                    if (!f.delete()) {
                        Timber.w("[AlbumArtExtractor] Failed to delete stale album art: ${f.absolutePath}")
                    } else {
                        Timber.d("[AlbumArtExtractor] Deleted stale album art: ${f.absolutePath}")
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e("[AlbumArtExtractor] Failed to clean album art cache $e")
        }
    }

    /**
     * 缩放 Bitmap 到目标尺寸
     */
    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        if (bitmap.width <= 512 && bitmap.height <= 512) {
            return bitmap
        }

        val ratio = minOf(512.toFloat() / bitmap.width, 512.toFloat() / bitmap.height)
        val newWidth = (bitmap.width * ratio).toInt()
        val newHeight = (bitmap.height * ratio).toInt()

        return bitmap.scale(newWidth, newHeight)
    }

    /**
     * 定时更新媒体信息（使用协程而非 Handler）
     */
    private fun scheduleNextUpdate() {
        serviceScope.launch {
            while (isActive) {
                updateMediaInfo()
                delay(UPDATE_INTERVAL_MS)
            }
        }
    }

    /**
     * 播放控制
     */
    fun play() {
        currentController?.transportControls?.play()
        Timber.i("[PlaybackControl] Play command sent")
    }

    fun pause() {
        currentController?.transportControls?.pause()
        Timber.i("[PlaybackControl] Pause command sent")
    }

    fun skipToNext() {
        currentController?.transportControls?.skipToNext()
        Timber.i("[PlaybackControl] Skip to next command sent")
    }

    fun skipToPrevious() {
        currentController?.transportControls?.skipToPrevious()
        Timber.i("[PlaybackControl] Skip to previous command sent")
    }

    fun seekTo(position: Long) {
        val controller = currentController
        if (controller == null) {
            Timber.e("[PlaybackControl] Seek ignored: no active MediaController, target=$position ms")
            return
        }

        val packageName = controller.packageName
        val playbackState = controller.playbackState?.state
        controller.transportControls.seekTo(position)
        Timber.i("[PlaybackControl] Seek command sent: target=$position ms, package=$packageName, playbackState=$playbackState")
    }

    fun fastForward() {
        currentController?.transportControls?.fastForward()
        Timber.i("[PlaybackControl] Fast forward command sent")
    }

    fun rewind() {
        currentController?.transportControls?.rewind()
        Timber.i("[PlaybackControl] Rewind command sent")
    }

    companion object {
        private const val UPDATE_INTERVAL_MS = 100L  // 每 100ms 更新一次，减少更新频率
    }
}
