/**
 * 主界面屏幕 - 应用的核心 UI
 * 
 * 这是用户看到的主要界面，包含以下功能模块：
 * - 当前播放信息卡片（歌名、艺术家、专辑）
 * - 歌词显示区域（AMLL 渲染）
 * - 设置入口
 * - 权限管理（通知、媒体访问）
 * - 动画效果（平滑过渡和交互反馈）
 * 
 * 主要特性：
 * - Compose 声明式 UI
 * - 响应式布局（适配不同屏幕尺寸）
 * - 动态主题（根据专辑封面变色）
 * - 手势支持（滑动、点击）
 * - 权限请求处理
 */
@file:Suppress("UNUSED_VARIABLE")

package com.amll.droidmate.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.WavySlider
import androidx.compose.material3.WavySliderDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.amll.droidmate.R
import com.amll.droidmate.domain.model.NowPlayingMusic
import com.amll.droidmate.domain.model.SongStructure
import com.amll.droidmate.domain.model.TTMLLyrics
import com.amll.droidmate.ui.AppSettings
import com.amll.droidmate.ui.CardClickAction
import com.amll.droidmate.ui.CustomLyricsActivity
import com.amll.droidmate.ui.theme.AlbumColorExtractor
import com.amll.droidmate.ui.theme.SuccessGreen
import com.amll.droidmate.ui.viewmodel.MainViewModel
import com.amll.droidmate.update.GitHubUpdateChecker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

private const val MAIN_SCREEN_LOG_TAG = "MainScreen"

fun getAppNameFromPackage(context: Context, packageName: String?): String? {
    if (packageName.isNullOrBlank()) return null
    return try {
        val pm = context.packageManager
        val appInfo = pm.getApplicationInfo(packageName, 0)
        pm.getApplicationLabel(appInfo).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}

// helpers moved here so they are visible to MainScreen early in the file
private fun isNotificationAccessGranted(context: Context): Boolean =
    Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")?.contains(context.packageName) == true

// simple contrast chooser: white text on dark backgrounds, black on light
private fun Color.contrastAgainst(): Color = if (luminance() > 0.5f) Color.Black else Color.White

@Composable
private fun AdaptiveStatusBarStyle(useDarkIcons: Boolean) {
    val view = LocalView.current
    SideEffect {
        val activity = view.context.findActivity() ?: return@SideEffect
        WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = useDarkIcons
    }
}

// used by AdaptiveStatusBarStyle
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel()
    val nowPlaying by viewModel.nowPlayingMusic.collectAsState()
    val isDarkTheme = isSystemInDarkTheme()
    // rippleColor tracks album-art-based primary color; falls back to theme primary if extraction fails
    val initialPrimary = MaterialTheme.colorScheme.primary
    val rippleColor = remember { mutableStateOf(initialPrimary) }
    // derived background color used by both now playing card and dropdown menu
    // always use the darker alpha so light mode matches dark mode
    val cardBg = rippleColor.value.copy(alpha = 0.3f)
    val lyrics by viewModel.lyrics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val songStructures by viewModel.songStructures.collectAsState()
    val currentTime = nowPlaying?.currentPosition ?: 0L
    // Apply user-configured lyric timing offsets when updating the lyric view
    val lyricTime = viewModel.getLyricTimeWithDeviceOffset(nowPlaying)
    var notificationAccessGranted by remember { mutableStateOf(isNotificationAccessGranted(context)) }

    // update ripple color whenever album art changes or theme toggles
    // use initialPrimary rather than recomputing MaterialTheme inside the coroutine
    LaunchedEffect(nowPlaying?.albumArtUri, isDarkTheme) {
        val uri = nowPlaying?.albumArtUri
        if (!uri.isNullOrBlank()) {
            try {
                val colors = AlbumColorExtractor.extractColorsFromAlbumArt(context, uri, isDarkTheme)
                rippleColor.value = colors?.primary ?: initialPrimary
            } catch (_: Exception) {
                rippleColor.value = initialPrimary
            }
            
            // ✅ 当专辑图刷新时，也通过 WebSocket 发送到外部服务
            if (AppSettings.isWebSocketProtocolEnabled(context)) {
                try {
                    viewModel.sendAlbumArtToWebSocket(uri)
                    Timber.d("[MainScreen] Sent album art to WebSocket on refresh: $uri")
                } catch (e: Exception) {
                    Timber.e("[MainScreen] Failed to send album art to WebSocket: ${e.message}")
                }
            }
        } else {
            rippleColor.value = initialPrimary
        }
    }
    var isLyricsFullscreen by remember { mutableStateOf(false) }
    
    val fullscreenOverlayAlpha by animateFloatAsState(
        targetValue = if (isLyricsFullscreen) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "fullscreenOverlayAlpha"
    )
    
    var webViewReloadKey by remember { mutableIntStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    var showOpenAppDialog by remember { mutableStateOf(false) }
    var showAutoUpdateDialog by remember { mutableStateOf(false) }
    var autoUpdateDialogTitle by remember { mutableStateOf("") }
    var autoUpdateDialogMessage by remember { mutableStateOf("") }
    var autoUpdateDialogUrl by remember { mutableStateOf<String?>(null) }
    var spinnerVisible by remember { mutableStateOf(false) }
    
    // 在 Composable 上下文中创建协程作用域，供回调函数使用
    val scope = rememberCoroutineScope()

    AdaptiveStatusBarStyle(useDarkIcons = !isLyricsFullscreen && MaterialTheme.colorScheme.background.luminance() > 0.5f)

    val customLyricsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val lyricsText = data?.getStringExtra(CustomLyricsActivity.EXTRA_LYRICS_TEXT).orEmpty()
            if (lyricsText.isNotBlank()) {
                viewModel.applyCustomLyricsInput(
                    content = lyricsText,
                    title = data?.getStringExtra(CustomLyricsActivity.EXTRA_TITLE) ?: nowPlaying?.title ?: "自选歌词",
                    artist = data?.getStringExtra(CustomLyricsActivity.EXTRA_ARTIST) ?: nowPlaying?.artist ?: "Unknown",
                    source = data?.getStringExtra(CustomLyricsActivity.EXTRA_SOURCE) ?: "manual"
                )
            }
        }
    }
    
    // WebSocket 连接状态 - 使用统一的状态监听器
    val webSocketClient = remember { com.amll.droidmate.websocket.AMLLWebSocketClient.getInstance() }
    val isWebSocketConnected by produceState(initialValue = webSocketClient.isConnected) {
        value = webSocketClient.isConnected
        
        // 使用工厂函数创建简单的状态监听器
        val listener = webSocketClient.createStateListener(
            onStateChanged = { connected ->
                value = connected
            }
        )
        
        webSocketClient.addListener(listener)
    }
    var websocketUrl by remember { mutableStateOf(AppSettings.getWebSocketProtocolAddress(context)) }
    val isWebViewEnabled = AppSettings.isWebViewEnabled(context)
    
    // 监听上次 WebView 启用状态，用于检测变化并自动刷新
    var lastWebViewEnabled by remember { mutableStateOf(isWebViewEnabled) }
    
    // 当 WebView 启用状态改变时自动刷新
    LaunchedEffect(isWebViewEnabled) {
        if (lastWebViewEnabled != isWebViewEnabled) {
            Timber.d("[UI] WebView 启用状态改变：$lastWebViewEnabled -> $isWebViewEnabled")
            lastWebViewEnabled = isWebViewEnabled
            // 状态改变时自动刷新歌词和 WebView
            viewModel.fetchLyrics()
            webViewReloadKey++
            Timber.d("[UI] 自动刷新完成，webViewReloadKey=$webViewReloadKey")
        }
    }
    
    // 首次进入 App 时自动连接 WebSocket
    LaunchedEffect(Unit) {
        if (AppSettings.isWebSocketProtocolEnabled(context)) {
            val wsAddress = AppSettings.getWebSocketProtocolAddress(context)
            Timber.d("[MainScreen] 首次启动，尝试连接 WebSocket: $wsAddress")
            webSocketClient.connect(wsAddress)
        }
    }

    var showMatchBubble by remember { mutableStateOf(false) }
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(5000L)
            if (isLoading) showMatchBubble = true
        } else {
            showMatchBubble = false
        }
    }

    // 智能退出逻辑：非加载期且无歌词时，延迟退回
    LaunchedEffect(lyrics, isLoading) {
        if (!isLoading && lyrics == null && isLyricsFullscreen) {
            delay(1500)
            if (!isLoading && lyrics == null && isLyricsFullscreen) {
                isLyricsFullscreen = false
            }
        }
    }

    BackHandler(enabled = isLyricsFullscreen) { isLyricsFullscreen = false }

    LaunchedEffect(Unit) {
        while (true) {
            notificationAccessGranted = isNotificationAccessGranted(context)
            delay(1000)
        }
    }

    LaunchedEffect(Unit) {
        if (AppSettings.isAutoUpdateCheckEnabled(context)) {
            val now = System.currentTimeMillis()
            if (now - AppSettings.getLastUpdateLaterAt(context) >= 24 * 60 * 60 * 1000) {
                val updateChannel = AppSettings.getUpdateChannel(context)
                val result = GitHubUpdateChecker.check(context, updateChannel)
                if (result.hasUpdate) {
                    autoUpdateDialogTitle = "发现新版本: ${result.resolvedReleaseTag ?: "未知版本"}"
                    autoUpdateDialogMessage = "当前版本: ${result.currentVersionName}\n\n${result.resolvedReleaseNotes ?: "暂无更新说明"}"
                    autoUpdateDialogUrl = result.resolvedReleaseUrl
                    showAutoUpdateDialog = true
                }
            }
        }
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    androidx.compose.material3.Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (!isLyricsFullscreen) {
                LargeTopAppBar(
                    title = { Text(text = stringResource(R.string.app_name)) },
                    actions = {
                        // AppBar action with anchored M3 DropdownMenu
                        Box {
                            val menuInteractionSource = remember { MutableInteractionSource() }
                                        androidx.compose.material3.FilledIconButton(
                                            onClick = { showMenu = true },
                                            modifier = Modifier.indication(menuInteractionSource, ripple(color = rippleColor.value)),
                                            colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = MaterialTheme.colorScheme.onSurface
                                            )
                                        ) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "菜单")
                                }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                // Use Material3 container tokens so the menu follows MD3 look
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 4.dp,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                                    DropdownMenuItem(
                                        text = { Text("自选歌词") },
                                        trailingIcon = { Icon(Icons.AutoMirrored.Filled.TextSnippet, contentDescription = null) },
                                        onClick = {
                                            val intent = Intent(context, CustomLyricsActivity::class.java).apply {
                                                putExtra(CustomLyricsActivity.EXTRA_TITLE, nowPlaying?.title ?: "")
                                                putExtra(CustomLyricsActivity.EXTRA_ARTIST, nowPlaying?.artist ?: "")
                                                putExtra(
                                                    CustomLyricsActivity.EXTRA_PLAYBACK_SOURCE,
                                                    getAppNameFromPackage(context, nowPlaying?.packageName) ?: ""
                                                )
                                            }
                                            customLyricsLauncher.launch(intent)
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("刷新") },
                                        trailingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                                        onClick = {
                                            scope.launch {
                                                viewModel.fetchLyrics()
                                                viewModel.refreshWebSocketConnection()
                                                webViewReloadKey++
                                                val currentUri = nowPlaying?.albumArtUri
                                                if (!currentUri.isNullOrBlank()) {
                                                    try {
                                                        val colors = AlbumColorExtractor.extractColorsFromAlbumArt(context, currentUri, isDarkTheme)
                                                        rippleColor.value = colors?.primary ?: initialPrimary
                                                        if (AppSettings.isWebSocketProtocolEnabled(context)) {
                                                            viewModel.sendAlbumArtToWebSocket(currentUri)
                                                            Timber.d("[MainScreen] Sent album art to WebSocket on manual refresh: $currentUri")
                                                        }
                                                    } catch (_: Exception) {
                                                        rippleColor.value = initialPrimary
                                                    }
                                                } else {
                                                    rippleColor.value = initialPrimary
                                                }
                                                viewModel.refreshSongStructures()
                                                showMenu = false
                                                Timber.d("[UI] 刷新按钮被点击：webViewReloadKey=$webViewReloadKey")
                                            }
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("设置") },
                                        trailingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                        onClick = { context.startActivity(Intent(context, com.amll.droidmate.ui.SettingsActivity::class.java)); showMenu = false }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background
                    ),
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.statusBarsPadding()
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // no-op: menu is anchored in the AppBar actions

            if (!notificationAccessGranted && !isLyricsFullscreen) {
                PermissionStatusCard(
                    notificationAccessGranted = notificationAccessGranted,
                    onOpenNotificationAccessSettings = { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // WebSocket 状态指示器
            if (AppSettings.isWebSocketProtocolEnabled(context) && !isLyricsFullscreen) {
                val statusCardBg = if (isWebSocketConnected) {
                    SuccessGreen.copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
                
                val connectedDotColor = SuccessGreen
                val disconnectedDotColor = MaterialTheme.colorScheme.onSurfaceVariant
                val connectedTextColor = SuccessGreen
                val disconnectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = statusCardBg)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 状态圆点
                            Box(
                                modifier = Modifier.size(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val dotColor = if (isWebSocketConnected) connectedDotColor else disconnectedDotColor
                                androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
                                    drawCircle(color = dotColor)
                                }
                            }
                            
                            Text(
                                text = if (isWebSocketConnected) {
                                    "WebSocket 已连接"
                                } else {
                                    "WebSocket 未连接"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isWebSocketConnected) connectedTextColor else disconnectedTextColor
                            )
                        }
                        
                        IconButton(onClick = { 
                            context.startActivity(Intent(context, com.amll.droidmate.ui.WsProtocolSettingsActivity::class.java))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "WebSocket 设置",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (showOpenAppDialog) {
                val sourceAppName = getAppNameFromPackage(context, nowPlaying?.packageName) ?: "播放源应用"
                AlertDialog(
                    onDismissRequest = { showOpenAppDialog = false },
                    title = { Text("打开 $sourceAppName？") },
                    text = { Text("您可进入设置调整点击卡片的默认行为。") },
                    containerColor = MaterialTheme.colorScheme.background,
                    confirmButton = {
                        TextButton(onClick = {
                            openSourceApp(context, nowPlaying?.packageName)
                            showOpenAppDialog = false
                        }) { Text("打开") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showOpenAppDialog = false }) { Text("忽略") }
                    }
                )
            }

            if (showAutoUpdateDialog) {
                AlertDialog(
                    onDismissRequest = { showAutoUpdateDialog = false },
                    containerColor = MaterialTheme.colorScheme.background,
                    title = { Text(autoUpdateDialogTitle) },
                    text = { Text(autoUpdateDialogMessage) },
                    confirmButton = {
                        TextButton(onClick = {
                            autoUpdateDialogUrl?.let { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it))) }
                            showAutoUpdateDialog = false
                        }) { Text("去更新") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            AppSettings.setLastUpdateLaterAt(context, System.currentTimeMillis())
                            showAutoUpdateDialog = false
                        }) { Text("稍后") }
                    }
                )
            }

            val currentLyrics = lyrics
            val shouldShowSpinner = isLoading
            LaunchedEffect(shouldShowSpinner) {
                if (shouldShowSpinner) {
                    // wait a short time before actually showing the spinner; if loading
                    // finishes quickly the mask never appears
                    delay(300)
                    if (shouldShowSpinner) spinnerVisible = true
                } else {
                    spinnerVisible = false
                }
            }

            if (!isLyricsFullscreen) {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LyricsVisualLayer(
                            nowPlaying = nowPlaying,
                            lyrics = currentLyrics,
                            currentTime = lyricTime,
                            webViewReloadKey = webViewReloadKey,
                            onLineSeek = { viewModel.seekTo(it) },
                            // 改进：无歌词显示文案时禁止进入全屏
                            onFullscreenTap = { if (currentLyrics != null && isWebViewEnabled) isLyricsFullscreen = true },
                            amllDebugSource = "embedded",
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        // WebView 已关闭提示 - 浮在歌词组件上方
                        if (!isWebViewEnabled) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    modifier = Modifier.padding(32.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        
                                        Text(
                                            text = "歌词组件已关闭",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center
                                        )
                                        
                                        IconButton(onClick = { 
                                            context.startActivity(Intent(context, com.amll.droidmate.ui.WsProtocolSettingsActivity::class.java))
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = "去设置",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // 占位提示：恢复消失的文案
                        if (currentLyrics == null && !spinnerVisible && isWebViewEnabled) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "选择歌词来显示 AMLL",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        if (showMatchBubble) {
                            Row(
                                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)).padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "正在匹配更优歌词", color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp)
                                Button(
                                    onClick = {
                                        val intent = Intent(context, CustomLyricsActivity::class.java).apply {
                                            putExtra(CustomLyricsActivity.EXTRA_TITLE, nowPlaying?.title ?: "")
                                            putExtra(CustomLyricsActivity.EXTRA_ARTIST, nowPlaying?.artist ?: "")
                                            putExtra(
                                                CustomLyricsActivity.EXTRA_PLAYBACK_SOURCE,
                                                getAppNameFromPackage(context, nowPlaying?.packageName) ?: ""
                                            )
                                        }
                                        customLyricsLauncher.launch(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) { Text("自选歌词", fontSize = 14.sp) }
                            }
                        }

                        if (spinnerVisible) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    }
                }
            } else {
                Spacer(Modifier.fillMaxWidth().weight(1f))
            }

            // 歌曲结构显示条（仅在有歌词且非全屏时显示）
            AnimatedVisibility(visible = !isLyricsFullscreen && songStructures.isNotEmpty()) {
                SongStructureBar(
                    structures = songStructures,
                    currentTime = currentTime,
                    onSeekTo = { time ->
                        viewModel.seekTo(time)
                        Timber.d("[SongStructure] 点击跳转至：${time}ms")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnimatedVisibility(visible = !isLyricsFullscreen) {
                NowPlayingCard(
                    nowPlaying = nowPlaying,
                    context = context,
                    songStructures = songStructures,  // 传递歌曲结构
                    onPlayPauseClick = { if (nowPlaying?.isPlaying == true) viewModel.pause() else viewModel.play() },
                    onSkipPreviousClick = { 
                        val currentPos = nowPlaying?.currentPosition ?: 0L
                        if (AppSettings.isSkipPreviousRewindsEnabled(context) && currentPos > 3000) viewModel.seekTo(0) else viewModel.skipToPrevious()
                    },
                    onSkipNextClick = { viewModel.skipToNext() },
                    onRewind = { viewModel.rewind() },
                    onFastForward = { viewModel.fastForward() },
                    onSeek = { viewModel.seekTo(it) },
                    onCardClick = { 
                        when (AppSettings.getCardClickAction(context)) {
                            CardClickAction.DIRECT_OPEN -> openSourceApp(context, nowPlaying?.packageName)
                            CardClickAction.ASK -> showOpenAppDialog = true
                            else -> {}
                        }
                    },
                    cardBg = cardBg,
                    sliderColor = rippleColor.value,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }

        // 全屏显示：恢复原有精细逻辑
        AnimatedVisibility(
            visible = isLyricsFullscreen && (lyrics != null || spinnerVisible),
            enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.96f),
            exit = fadeOut(tween(250)) + scaleOut(targetScale = 0.96f),
            modifier = Modifier.fillMaxSize()
        ) {
            var controlsVisible by remember { mutableStateOf(true) }
            var hideControlsJob by remember { mutableStateOf<Job?>(null) }
            val innerScope = rememberCoroutineScope()
            val controlsAlpha by animateFloatAsState(if (controlsVisible) 1f else 0f, label = "controlsAlpha")

            fun resetHideTimer() {
                hideControlsJob?.cancel()
                controlsVisible = true
                hideControlsJob = innerScope.launch { delay(3000L); controlsVisible = false }
            }

            val localView = LocalView.current
            SideEffect {
                val activity = localView.context.findActivity() ?: return@SideEffect
                val window = activity.window
                val insetsController = WindowCompat.getInsetsController(window, localView)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    window.attributes = window.attributes.apply {
                        layoutInDisplayCutoutMode = if (controlsVisible) android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT else android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                    }
                }
                if (controlsVisible) insetsController.show(androidx.core.view.WindowInsetsCompat.Type.systemBars()) 
                else {
                    insetsController.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
                    insetsController.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }

            androidx.compose.runtime.DisposableEffect(Unit) {
                onDispose {
                    val activity = localView.context.findActivity() ?: return@onDispose
                    val window = activity.window
                    val insetsController = WindowCompat.getInsetsController(window, localView)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        window.attributes = window.attributes.apply { layoutInDisplayCutoutMode = android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT }
                    }
                    insetsController.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
                }
            }

            LaunchedEffect(Unit) { resetHideTimer() }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
                    .background(Color.Black.copy(alpha = 0.35f * fullscreenOverlayAlpha))
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Final)
                                if (event.changes.any { it.pressed || it.previousPressed }) { resetHideTimer() }
                            }
                        }
                    }
            ) {
                Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(0.dp), colors = CardDefaults.cardColors(containerColor = Color.Black)) {
                    LyricsVisualLayer(
                        nowPlaying = nowPlaying,
                        lyrics = lyrics,
                        currentTime = lyricTime,
                        webViewReloadKey = webViewReloadKey,
                        onLineSeek = { viewModel.seekTo(it); resetHideTimer() },
                        amllDebugSource = "fullscreen",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (spinnerVisible) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

                IconButton(
                    onClick = { isLyricsFullscreen = false },
                    modifier = Modifier.align(Alignment.TopStart).padding(top = 40.dp, start = 8.dp).alpha(controlsAlpha)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "退出全屏", tint = Color.White.copy(alpha = 0.9f))
                }

                nowPlaying?.let { currentPlaying ->
                    Row(
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 64.dp, start = 32.dp, end = 32.dp).height(100.dp).alpha(controlsAlpha),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround // 平衡按钮间距
                    ) {
                        val leftInteractionSource = remember { MutableInteractionSource() }
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(50)).indication(leftInteractionSource, ripple())
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            if ((currentPlaying.currentPosition) > 3000L && AppSettings.isSkipPreviousRewindsEnabled(context)) viewModel.seekTo(0L) else viewModel.skipToPrevious()
                                            resetHideTimer()
                                        },
                                        onPress = { offset ->
                                            val press = PressInteraction.Press(offset)
                                            leftInteractionSource.tryEmit(press)
                                            val job = innerScope.launch { delay(500); while(true) { viewModel.rewind(); delay(200) } }
                                            try { awaitPointerEventScope { waitForUpOrCancellation(); job.cancel(); leftInteractionSource.tryEmit(PressInteraction.Release(press)) } }
                                            catch (e: Exception) { job.cancel(); leftInteractionSource.tryEmit(PressInteraction.Cancel(press)) }
                                            resetHideTimer()
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.FastRewind, null, Modifier.size(40.dp), Color.White.copy(0.9f)) }

                        Box(
                            modifier = Modifier.weight(1.5f).fillMaxHeight().clip(RoundedCornerShape(50))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = ripple(color = Color.White)) {
                                    if (currentPlaying.isPlaying) viewModel.pause() else viewModel.play()
                                    resetHideTimer()
                                },
                            contentAlignment = Alignment.Center
                        ) { Icon(if (currentPlaying.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, Modifier.size(64.dp), Color.White.copy(0.9f)) }

                        val rightInteractionSource = remember { MutableInteractionSource() }
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(50)).indication(rightInteractionSource, ripple())
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { viewModel.skipToNext(); resetHideTimer() },
                                        onPress = { offset ->
                                            val press = PressInteraction.Press(offset)
                                            rightInteractionSource.tryEmit(press)
                                            val job = innerScope.launch { delay(500); while(true) { viewModel.fastForward(); delay(200) } }
                                            try { awaitPointerEventScope { waitForUpOrCancellation(); job.cancel(); rightInteractionSource.tryEmit(PressInteraction.Release(press)) } }
                                            catch (e: Exception) { job.cancel(); rightInteractionSource.tryEmit(PressInteraction.Cancel(press)) }
                                            resetHideTimer()
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.FastForward, null, Modifier.size(40.dp), Color.White.copy(0.9f)) }
                    }
                }
            }
        }
    }
}


@Composable
private fun LyricsVisualLayer(
    nowPlaying: NowPlayingMusic?,
    lyrics: TTMLLyrics?,
    currentTime: Long,
    webViewReloadKey: Int,
    onLineSeek: (Long) -> Unit,
    amllDebugSource: String,
    onFullscreenTap: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (nowPlaying?.albumArtUri != null) {
            AsyncImage(model = nowPlaying.albumArtUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().blur(28.dp).alpha(0.55f))
        }
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(0.28f), Color.Black.copy(0.55f), Color.Black.copy(0.68f)))))
        // only create the WebView if we actually have lyrics; avoids unnecessary page loads
        if (lyrics != null) {
            key(webViewReloadKey) {
                AMLLLyricsView(
                    lyrics = lyrics,
                    currentTime = currentTime,
                    musicId = nowPlaying?.packageName ?: "",
                    musicName = nowPlaying?.title ?: "Unknown",
                    albumName = nowPlaying?.album ?: "",
                    artistName = nowPlaying?.artist ?: "Unknown",
                    duration = nowPlaying?.duration ?: 0L,
                    albumArtUri = nowPlaying?.albumArtUri,
                    renderMode = AMLLRenderMode.DOM,
                    debugSource = amllDebugSource,
                    onLineSeek = onLineSeek,
                    isPlaying = nowPlaying?.isPlaying ?: false,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        if (onFullscreenTap != null) {
            Box(Modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onFullscreenTap() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingCard(
    nowPlaying: NowPlayingMusic?,
    context: Context,
    songStructures: List<SongStructure> = emptyList(),  // 添加歌曲结构参数
    onPlayPauseClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onRewind: () -> Unit,
    onFastForward: () -> Unit,
    onSeek: (Long) -> Unit,
    onCardClick: () -> Unit,
    // new parameter: background color for card (should match dropdown)
    cardBg: Color = MaterialTheme.colorScheme.primaryContainer,
    // slider/thumb color (pass rippleColor from caller)
    sliderColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    // card background is provided by caller (computed in MainScreen)
    Card(
        modifier = modifier.clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBg,
            // content stays onSurface for legibility as before
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        if (nowPlaying != null) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(model = nowPlaying.albumArtUri, contentDescription = null, modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                        val appName = nowPlaying.packageName?.let { getAppNameFromPackage(context, it) }
                        Text(appName ?: "播放源应用", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), lineHeight = 12.sp)
                        Text(
                            nowPlaying.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(nowPlaying.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                // add a bit more breathing room between the track info and slider
                Spacer(Modifier.height(16.dp))
                // While the user is actively dragging the slider, we should not overwrite the
                // thumb position from playback updates (which would make the thumb jump).
                // At the same time we want to send seek updates to the playback source in real time.

                var isSeeking by remember { mutableStateOf(false) }
                var draggedSliderValue by remember { mutableStateOf(nowPlaying.currentPosition.toFloat()) }
                var sliderValue by remember { mutableStateOf(nowPlaying.currentPosition.toFloat()) }
                
                // Only update sliderValue from playback position when not seeking
                LaunchedEffect(nowPlaying?.currentPosition, isSeeking) {
                    if (!isSeeking) {
                        sliderValue = nowPlaying?.currentPosition?.toFloat() ?: 0f
                    }
                }

                Column {
                    // progress slider with wavy style
                    WavySlider(
                        value = sliderValue / nowPlaying.duration.toFloat().coerceAtLeast(1f),
                        onValueChange = { normalizedValue ->
                            draggedSliderValue = normalizedValue * nowPlaying.duration.toFloat().coerceAtLeast(1f)
                            isSeeking = true
                        },
                        onValueChangeFinished = {
                            onSeek(draggedSliderValue.toLong())
                            isSeeking = false
                        },
                        customSteps = songStructures
                            .map { it.startTime }  // 获取所有起始时间
                            .filter { it > 0 && it < nowPlaying.duration }
                            .map { (it.toFloat() / nowPlaying.duration.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f) }  // 归一化到 0-1
                            .distinct(),  // 去重
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = WavySliderDefaults.colors(
                            thumbColor = sliderColor,
                            activeTrackColor = sliderColor,
                            inactiveTrackColor = sliderColor.copy(alpha = 0.3f),
                            stepColor = sliderColor.copy(alpha = 0.7f)  // 设置标记点颜色
                        ),
                        amplitude = 1f,  // 使用默认振幅 1.0（最大波浪效果）
                        wavelength = WavySliderDefaults.Wavelength,  // 16.dp，默认波长
                        waveSpeed = if (nowPlaying.isPlaying) WavySliderDefaults.WaveSpeed else 0.dp, // 暂停时停止波浪动画
                        thumbHeight = 24.dp,
                        attractionRadius = 0.01f
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(formatTime(sliderValue.toLong()), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text(formatTime(nowPlaying.duration), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().height(64.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    val leftInteractionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(50)).indication(leftInteractionSource, ripple())
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { onSkipPreviousClick() },
                                    onPress = { offset ->
                                        val press = PressInteraction.Press(offset)
                                        leftInteractionSource.tryEmit(press)
                                        val job = scope.launch { delay(500); while(true) { onRewind(); delay(200) } }
                                        try { awaitPointerEventScope { waitForUpOrCancellation(); job.cancel(); leftInteractionSource.tryEmit(PressInteraction.Release(press)) } }
                                        catch (e: Exception) { job.cancel(); leftInteractionSource.tryEmit(PressInteraction.Cancel(press)) }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.FastRewind, contentDescription = null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onSurface) }
                    Box(modifier = Modifier.weight(1.5f).fillMaxHeight().clip(RoundedCornerShape(50)).clickable { onPlayPauseClick() }, contentAlignment = Alignment.Center) {
                        Icon(
                            if (nowPlaying.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    val rightInteractionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(50)).indication(rightInteractionSource, ripple())
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { onSkipNextClick() },
                                    onPress = { offset ->
                                        val press = PressInteraction.Press(offset)
                                        rightInteractionSource.tryEmit(press)
                                        val job = scope.launch { delay(500); while(true) { onFastForward(); delay(200) } }
                                        try { awaitPointerEventScope { waitForUpOrCancellation(); job.cancel(); rightInteractionSource.tryEmit(PressInteraction.Release(press)) } }
                                        catch (e: Exception) { job.cancel(); rightInteractionSource.tryEmit(PressInteraction.Cancel(press)) }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onSurface) }
                }
            }
        }
    }
}

@Composable
fun PermissionStatusCard(notificationAccessGranted: Boolean, onOpenNotificationAccessSettings: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "需要通知访问权限才能正常使用此应用。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                "滥用通知使用权危及安全，因此系统可能会弹窗阻止。AMLL DroidMate 是开源软件，您可以查看本应用的执行逻辑，因此在应用来源可靠的情况下无需感到担忧。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onOpenNotificationAccessSettings,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { 
                    Text("去授权", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }
}

private fun openSourceApp(context: Context, packageName: String?): Boolean {
    if (packageName.isNullOrBlank()) return false
    val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
    return try { context.startActivity(launchIntent); true } catch (e: Exception) { false }
}

fun formatTime(millis: Long): String = String.format(Locale.US, "%d:%02d", millis / 60000, (millis % 60000) / 1000)