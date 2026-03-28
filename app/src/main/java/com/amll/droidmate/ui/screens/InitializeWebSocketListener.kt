package com.amll.droidmate.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.amll.droidmate.ui.AppSettings
import com.amll.droidmate.domain.model.TTMLLyrics
import com.amll.droidmate.websocket.AMLLWebSocketClient
import com.amll.droidmate.websocket.WsProtocolV2Helper
import com.amll.droidmate.data.parser.escapeXML
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

    fun buildTtmlString(lyrics: TTMLLyrics?): String {
        if (lyrics == null) return ""
        
        val sb = StringBuilder()
        
        // XML 头
        sb.append("""<?xml version="1.0" encoding="UTF-8"?>""")
        
        // TTML 根元素
        sb.append("""<tt xmlns="http://www.w3.org/ns/ttml" xmlns:ttm="http://www.w3.org/ns/ttml#metadata" xml:lang="ja">""")
        
        // Head
        sb.append("<head>")
        sb.append("<metadata>")
        sb.append("""<amll:meta key="title" value="${lyrics.metadata.title}" xmlns:amll="http://www.example.com/ns/amll"/>""")
        sb.append("""<amll:meta key="artist" value="${lyrics.metadata.artist}" xmlns:amll="http://www.example.com/ns/amll"/>""")
        sb.append("</metadata>")
        sb.append("</head>")
        
        // Body
        val duration = lyrics.lines.lastOrNull()?.endTime ?: 0L
        sb.append("""<body dur="${formatTime(duration)}">""")
        sb.append("<div>")
        
        // Lyrics lines
        lyrics.lines.forEach { line ->
            val begin = formatTime(line.startTime)
            val end = formatTime(line.endTime)
            sb.append("""<p begin="$begin" end="$end">""")
            
            // 如果有逐词数据
            if (line.words.isNotEmpty()) {
                line.words.forEach { word ->
                    val wordBegin = formatTime(word.startTime)
                    val wordEnd = formatTime(word.endTime)
                    sb.append("""<span begin="$wordBegin" end="$wordEnd">${escapeXML(word.word)}</span>""")
                }
            } else {
                // 整行输出
                sb.append("""<span begin="$begin" end="$end">${escapeXML(line.text)}</span>""")
            }
            
            sb.append("</p>")
        }
        
        sb.append("</div>")
        sb.append("</body>")
        sb.append("</tt>")
        
        return sb.toString()
    }
    
    fun formatTime(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val remainingMillis = millis % 1000
        return String.format("%02d:%02d.%03d", minutes, remainingSeconds, remainingMillis)
    }
    


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
                    val ttmlContent = buildTtmlString(lyrics)
                    if (ttmlContent.isNotBlank()) {
                        webSocketClient.sendLyrics(ttmlContent)
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
