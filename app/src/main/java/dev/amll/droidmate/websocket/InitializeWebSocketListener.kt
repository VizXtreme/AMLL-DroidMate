package dev.amll.droidmate.websocket

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.amll.droidmate.global.AMLLSettings
import io.github.zeehan2005.scoremuse.global.UnifiedLyrics
import com.amll.droidmate.websocket.AMLLWebSocketClient
import dev.amll.droidmate.data.converter.TTMLConverter
import kotlinx.serialization.json.JsonObject
import timber.log.Timber

/**
 * 独立的 WebSocket 监听器初始化 Composable
 * 
 * 这个函数负责在 Compose 生命周期中初始化和配置 WebSocket 监听器。
 * 它是一个纯函数式的 Composable，可以在任何需要 WebSocket 通信的地方使用。
 * 
 * **设计优势**：
 * 1. 模块化：将 WebSocket 初始化逻辑抽取为独立的 Composable
 * 2. 可复用性：可以在多个界面中重复使用
 * 3. 生命周期管理：使用 LaunchedEffect 自动管理连接生命周期
 * 4. 条件启用：根据用户设置决定是否启用 WebSocket
 * 
 * **工作流程**：
 * 1. 检查用户是否启用了 WebSocket 功能
 * 2. 获取配置的 WebSocket 地址
 * 3. 创建全功能监听器（支持歌曲信息同步和命令处理）
 * 4. 连接成功后发送当前歌曲信息和歌词
 * 5. 监听来自外部的控制命令（暂停、播放、跳转等）
 * 
 * @param musicId 当前播放歌曲的 ID
 * @param musicName 歌曲名称
 * @param albumName 专辑名称
 * @param artistName 艺术家名称
 * @param duration 歌曲总时长（毫秒）
 * @param currentTime 当前播放进度（毫秒）
 * @param isPlaying 是否正在播放
 * @param lyrics 当前的歌词数据（TTML 格式）
 * @param debugSource 调试标签，用于日志输出
 * @param onCommandReceived 收到外部命令时的回调（如暂停、恢复、跳转）
 * @param onConnectedCallback 连接成功后的额外操作（可选）
 * @param onErrorCallback 错误处理回调（可选）
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
    lyrics: UnifiedLyrics?,
    debugSource: String,
    onCommandReceived: ((String, JsonObject?) -> Unit)? = null,
    onConnectedCallback: (() -> Unit)? = null,
    onErrorCallback: ((Throwable) -> Unit)? = null
) {
    // 获取 Android Context（用于读取应用设置）
    val context = LocalContext.current
    // 获取 WebSocket 客户端单例（全局唯一实例）
    val webSocketClient = remember { AMLLWebSocketClient.getInstance() }

    // 在 Compose 启动时执行一次初始化逻辑
    LaunchedEffect(Unit) {
        // 检查用户是否启用了 WebSocket 功能
        if (AMLLSettings.isWebSocketProtocolEnabled(context)) {
            // 获取用户配置的 WebSocket 地址
            val wsAddress = AMLLSettings.getWebSocketProtocolAddress(context)
            Timber.d("[WebSocketInit] 开始初始化 WebSocket 监听器：$wsAddress")

            // 定义连接成功后的额外操作
            val connectedCallback: () -> Unit = {
                // Step 1: 执行传入的连接后操作（如果有）
                onConnectedCallback?.invoke()
                
                // Step 2: 发送歌曲信息到 WebSocket 服务端
                if (musicName.isNotEmpty() && musicName != "Unknown" || musicId.isNotEmpty()) {
                    webSocketClient.sendMusicInfo(musicId, musicName, albumName, artistName, duration)
                } else {
                    Timber.d("[WebSocketInit] 无有效歌曲信息，跳过发送")
                }
                
                // Step 3: 转换并发送歌词数据
                lyrics?.lines?.let { lines ->
                    try {
                        // 将内部歌词模型转换为 TTML XML 格式
                        val ttmlContent = TTMLConverter.toTTMLString(lyrics)
                        if (ttmlContent.isNotBlank()) {
                            webSocketClient.sendLyrics(ttmlContent)
                            Timber.d("[WebSocketInit] 已发送初始歌词")
                        }
                    } catch (e: Exception) {
                        Timber.e("[WebSocketInit] 发送初始歌词失败", e)
                    }
                }
            }

            // 使用工厂函数创建完整功能的监听器
            // 这个监听器会：
            // - 自动同步播放状态
            // - 处理外部控制命令
            // - 错误处理和重连
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

            // 注册监听器到 WebSocket 客户端
            webSocketClient.addListener(listener)
            Timber.d("[WebSocketInit] WebSocket 监听器已添加")
        }
    }
}
