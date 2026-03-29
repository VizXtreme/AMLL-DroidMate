package com.amll.droidmate.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.amll.droidmate.ui.AppSettings
import com.amll.droidmate.domain.model.TTMLLyrics
import com.amll.droidmate.websocket.AMLLWebSocketClient
import com.amll.droidmate.websocket.WsProtocolV2Helper
import com.amll.droidmate.data.converter.TTMLConverter
import kotlinx.serialization.json.JsonObject
import timber.log.Timber

/**
 * 独立的 WebSocket 监听器初始化 Composable
 * 即使 WebView 被禁用，也能保持 WebSocket 通信
 */
@Composable
fun InitializeWebSocketListener(
    musicId: String,
    musicName: String,
    albumName: String,
    artistName: String,
    duration: Long,
    currentTime: Long,
    isPlaying: Boolean,
    lyrics: TTMLLyrics?,
    debugSource: String,
    onCommandReceived: ((String, kotlinx.serialization.json.JsonObject?) -> Unit)? = null,
    onConnectedCallback: (() -> Unit)? = null,
    onErrorCallback: ((Throwable) -> Unit)? = null
) {
    val context = LocalContext.current
    val webSocketClient = remember { AMLLWebSocketClient.getInstance() }

    LaunchedEffect(Unit) {
        if (AppSettings.isWebSocketProtocolEnabled(context)) {
            val wsAddress = AppSettings.getWebSocketProtocolAddress(context)
            Timber.d("[$debugSource] 开始初始化 WebSocket 监听器：$wsAddress")

            // 定义连接成功后的额外操作
            val connectedCallback: () -> Unit = {
                // 执行传入的连接后操作
                onConnectedCallback?.invoke()
                
                // 发送歌曲信息和歌词
                if (musicName.isNotEmpty() && musicName != "Unknown" || musicId.isNotEmpty()) {
                    webSocketClient.sendMusicInfo(musicId, musicName, albumName, artistName, duration)
                } else {
                    Timber.d("[$debugSource] 无有效歌曲信息，跳过发送")
                }
                lyrics?.lines?.let { lines ->
                    try {
                        val ttmlContent = TTMLConverter.toTTMLString(lyrics)
                        if (ttmlContent.isNotBlank()) {
                            webSocketClient.sendLyrics(ttmlContent)
                            Timber.d("[$debugSource] 已发送初始歌词")
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "[$debugSource] 发送初始歌词失败")
                    }
                }
            }

            // 使用工厂函数创建完整功能的监听器
            val listener = webSocketClient.createFullFeatureListener(
                debugSource = debugSource,
                musicId = musicId,
                musicName = musicName,
                albumName = albumName,
                artistName = artistName,
                duration = duration,
                currentTime = currentTime,
                isPlaying = isPlaying,
                lyrics = lyrics,
                onConnectedCallback = connectedCallback,
                onCommandReceived = onCommandReceived,
                onErrorCallback = onErrorCallback
            )

            webSocketClient.addListener(listener)
            Timber.d("[$debugSource] WebSocket 监听器已添加")
        }
    }
}
