package dev.amll.droidmate.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
//import android.telecom.Call.Details.can
import android.util.Base64
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
//import android.webkit.WebResourceRequest
//import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
//import androidx.webkit.WebViewAssetLoader
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import dev.amll.droidmate.global.AMLLSettings
import io.github.zeehan2005.scoremuse.global.UnifiedLyrics
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import android.graphics.Color as AndroidColor
import androidx.core.net.toUri
import kotlin.collections.joinToString


/**
 * AMLL 视图实例计数器：用于输出日志
 */
private val AMLL_VIEW_INSTANCE_COUNTER = AtomicInteger(0)

/**
 * @param modifier Compose 修饰符（用于调整大小、背景等样式）
 * @param lyrics 歌词数据（TTML 格式，包含完整的歌曲结构和时间信息）
 * @param currentTime 当前播放进度（毫秒），用于同步歌词高亮
 * @param albumArtUri 专辑封面图片 URI（可以是 file://、content://或 data URL）
 * @param debugSource 调试来源标签（用于日志输出，区分不同的实例）
 * @param onLyricsClick 歌词点击事件回调（用户点击歌词区域时触发）
 * @param onLineSeek 歌词行跳转回调（用户点击某行歌词时跳转到指定时间）
 * @param onLyricsParsed JS 解析完成回调（当 WebView 使用 WASM 解析出歌词行时调用）
 * @param isPlaying 是否正在播放（用于同步播放/暂停状态）
 * @param isInteractive 是否允许交互（非全屏模式下通常禁用交互，仅响应点击全屏）
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AMLLLyricsView(
    modifier: Modifier = Modifier,
    lyrics: UnifiedLyrics?,
    currentTime: Long,
    albumArtUri: String? = null,
    debugSource: String = "unknown",
    onLyricsClick: (() -> Unit)? = null,
    onLineSeek: ((Long) -> Unit)? = null,
    onLyricsParsed: ((String) -> Unit)? = null,
    isPlaying: Boolean = true,
    isInteractive: Boolean = true,
) {


    // ==================== 内部状态变量定义 ====================

/**
 *  视图实例 ID（用于调试日志，区分多个 AMLLLyricsView 实例）
 */
    val instanceId = remember { AMLL_VIEW_INSTANCE_COUNTER.incrementAndGet() }
    
    /**
     * 使用 rememberUpdatedState 确保回调函数始终是最新的
     * 这样可以避免因为闭包捕获旧值而导致的 stale closure 问题
     */
    val onLyricsClickState = rememberUpdatedState(onLyricsClick)
    val onLineSeekState = rememberUpdatedState(onLineSeek)
/** val onLyricsParsedState = rememberUpdatedState(onLyricsParsed) */
    val isPlayingState = rememberUpdatedState(isPlaying)
    val isInteractiveState = rememberUpdatedState(isInteractive)

    /**
     *  页面就绪状态（WebView 加载完成后设为 true）
     */
    var isPageReady by remember { mutableStateOf(false) }
    val onPageReady = remember { { isPageReady = true } }
    
//    // 上一次配置值的缓存（用于去重，避免重复调用 JavaScript）
//    // Cache last applied render-mode (dom/dom-lite) and lyric player implementation
//    var lastLyricPlayerImplValue by remember { mutableStateOf<String?>(null) }
//    var lastLyricSizePreset by remember { mutableStateOf<String?>(null) }
//    var lastEnableAdvanceDynamicTime by remember { mutableStateOf<Boolean?>(null) }
    
     /**
      * 上一次的歌词数据引用（用于检测歌词是否变化）
      */
    var lastLyrics by remember { mutableStateOf<UnifiedLyrics?>(null) }

    /**
     * 上一次设置的专辑封面 URI（用于去重）
     */
    var lastAlbumArtUri by remember { mutableStateOf<String?>(null) }

    var lastMotionConfigValue by remember { mutableStateOf<String?>(null) }
    
//    // 字体配置相关状态
//    var lastFontConfigSignature by remember { mutableStateOf<String?>(null) }
//    var lastBackgroundConfigValue by remember { mutableStateOf<String?>(null) }

    // ==================== 时间���新节流 ====================
    /**
     * 记录上一次更新时间的时间戳（用于节流，避免每帧都更新）
     */
    var lastTimeUpdateTimestamp by remember { mutableLongStateOf(0L) }

    /**
     * 时间更新间隔（毫秒）- 减少频繁的 JS 调用
     * 优化：增加到 100ms，因为人眼难以感知 10fps 以下的差异
     * */
    val timeUpdateIntervalMs = 100L
    
    // ==================== 播放状态节流 ====================
    // 避免每次 recompose 都调用 JS
    var lastIsPlayingValue by remember { mutableStateOf<Boolean?>(null) }
    
//    // ==================== 歌词开关状态缓存（用于去重） ====================
//    var lastTranslationLineEnabled by remember { mutableStateOf<Boolean?>(null) }
//    var lastRomanLineEnabled by remember { mutableStateOf<Boolean?>(null) }



    // ==================== WebView 组件定义 ====================
    // 使用 AndroidView 将原生 WebView 嵌入到 Compose 界面中
    AndroidView(
        modifier = modifier,  // 应用传入的修饰符
        factory = { context ->
            // WebView 工厂函数：创建并配置 WebView 实例
            Timber.i("[AMLLLyrics] [$debugSource#$instanceId] Creating AMLL WebView, onLineSeek=${onLineSeekState.value != null}")
            
            // 启用 WebView 调试功能（可在 Chrome DevTools 中调试）
            WebView.setWebContentsDebuggingEnabled(true)
            
//            // 配置 WebViewAssetLoader 以安全地加载本地资源
//            // 将 assets 目录映射到 https://appassets.androidplatform.net/assets/
//            // 将外部字体文件映射到 https://appassets.androidplatform.net/fonts/
//            val assetLoader = WebViewAssetLoader.Builder()
//                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
//                .addPathHandler("/fonts/") { path ->
//                    // 根据路径中的 ID 查找对应的字体文件
//                    val fontId = path.substringBefore('/')
//                    val fontFile = AMLLSettings.getAmllFontFiles(context).find { it.id == fontId }
//                    if (fontFile != null) {
//                        val file = File(fontFile.absolutePath)
//                        if (file.exists()) {
//                            try {
//                                // 统一以 font/ttf 类型返回，现代浏览器通常能自动识别具体格式
//                                WebResourceResponse("font/ttf", null, file.inputStream())
//                            } catch (e: Exception) {
//                                Timber.e("[AMLLLyrics] Failed to load font through AssetLoader: $path $e")
//                                null
//                            }
//                        } else null
//                    } else null
//                }
//                .build()

            WebView(context).apply {
                // 设置 WebView 的 LayoutParams 为 MATCH_PARENT（填满父容器）
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // ==================== WebViewClient 配置 ====================
                // 监听 WebView 页面加载事件
                webViewClient = object : WebViewClient() {
//                    /**
//                     * 拦截请求并交给 AssetLoader 处理
//                     */
//                    override fun shouldInterceptRequest(
//                        view: WebView,
//                        request: WebResourceRequest
//                    ): WebResourceResponse? {
//                        return assetLoader.shouldInterceptRequest(request.url)
//                    }

                    /**
                     * 页面开始加载时回调
                     * - 重置所有就绪状态
                     * - 清空上一次配置的缓存
                     */
                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                        if (isPageReady) {
                            isPageReady = false
                        }
                        if (lastLyrics != null) {
                            lastLyrics = null
                        }
                        Timber.d("[AMLLLyrics] [$debugSource#$instanceId] WebView page started: $url")
                    }

                    /**
                     * 页面加载完成时回调
                     * - 标记页面就绪
                     * - 重新应用歌词和配置
                     * - 注入 WebSocket 桥接代码
                     */
                    override fun onPageFinished(view: WebView, url: String) {
                        // 确保页面加载后背景仍然透明
                        view.setBackgroundColor(AndroidColor.TRANSPARENT)
                        Timber.d("[AMLLLyrics] [$debugSource#$instanceId] WebView page finished: $url")
                    }
                }
                // ==================== WebChromeClient 配置 ====================
                webChromeClient = object : WebChromeClient() {
                    /**
                     * 处理 JavaScript 控制台日志，将其转发到 Timber
                     */
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                        val logMessage = "[AMLLLyrics] [WebView] [$debugSource#$instanceId] JS Console(@${consoleMessage.sourceId()}:${consoleMessage.lineNumber()}): ${consoleMessage.message()}"
                        // 根据日志级别分别处理
                        when (consoleMessage.messageLevel()) {
                            ConsoleMessage.MessageLevel.DEBUG -> Timber.d(logMessage)
                            ConsoleMessage.MessageLevel.LOG -> Timber.i(logMessage)
                            ConsoleMessage.MessageLevel.WARNING -> Timber.w(logMessage)
                            ConsoleMessage.MessageLevel.ERROR -> Timber.e(logMessage)
                            else -> Timber.d(logMessage)
                        }
                        return super.onConsoleMessage(consoleMessage)
                    }
                }
                // ==================== WebView 安全配置 ====================
                // 使用 WebViewAssetLoader 以现代且安全的方式加载本地资源
                settings.apply {
                    javaScriptEnabled = true       // 启用 JavaScript
                    domStorageEnabled = true       // 启用 DOM 存储（localStorage 等）
                    allowFileAccess = false        // 禁用直接文件访问（更安全，使用 AssetLoader 代持）
                    allowContentAccess = true      // 允许访问内容提供者
                    
                    // 禁用缓存确保每次加������新的文件
                    cacheMode = WebSettings.LOAD_NO_CACHE
                }

                // 透明 WebView 配置，允许宿主 Compose 层的专辑图背景透出
                // 先设置背景透明
                setBackgroundColor(AndroidColor.TRANSPARENT)
                // 使用 NONE 让 View 自行决定渲染方式，通常会使用硬件加速
                // 同时避免软件渲染导致的帧率问题
                setLayerType(View.LAYER_TYPE_NONE, null)
                
                // 强制清除所有缓存数据，确保加载最新的 HTML 和 JS
                clearAllCache()

                /** keep a reference to the WebView so we can send immediate commands back to
                 * the javascript bridge when the user initiates a seek via clicking a lyric.
                 * 保存 WebView 引用，以便用户点击歌词时能立即发送命令到 JavaScript
                 */
                val webViewRef = this

                // ==================== JavaScript 接口注册 ====================
                // 注册 AMLLInterface 对象为 window.Android，供前端调用
                addJavascriptInterface(
                    AMLLInterface(
                        debugSource,
                        instanceId,
                        onLineSeekState.value,
                        onSeekRequested = { seekTime ->
                            // schedule a UI-thread action so that the webview can immediately
                            // acknowledge the seek and prevent the "lyrics running around" effect.
                            // 在 UI 线程上执行跳转，防止歌词乱跑
                            webViewRef.post {
                                // tell the JS player we are seeking so it can suspend auto-scroll
                                // 告诉 JS 播放器正在跳转，暂停自动滚动
                                webViewRef.evaluateJavascript(
                                    "window.callPlayer && window.callPlayer('setIsSeeking', true);",
                                    null
                                )

                                // update the webview time to the target position right away. this
                                // reduces the window where the old time would cause the view to
                                // scroll back to the previous line before the new position arrives
                                // 立即更新 WebView 时间到新位置，减少旧时间导致的回滚
                                webViewRef.evaluateJavascript(
                                    "window.updateTime && window.updateTime($seekTime);",
                                    null
                                )
                            }
                        },
                        isPlayingProvider = { isPlayingState.value },
                        onPageReady = {
                            webViewRef.post { onPageReady() }
                        },
//                        onLyricsParsed = { json ->
//                            onLyricsParsedState.value?.invoke(json)
//                        }
                    ),
                    "Android"  // 在前端通过 window.Android 访问
                )
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] JavascriptInterface added as Android")

                // ==================== 点击事件监听 ====================
                /**
                 * 使用 GestureDetector 辅助检测点击，避免 WebView 内部消费导致 setOnClickListener 失效
                 */
                val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDown(e: MotionEvent): Boolean = true
                    override fun onSingleTapUp(e: MotionEvent): Boolean = true
                })

                setOnTouchListener { v, event ->
                    /**将触摸事件传递给 GestureDetector*/
                    val isTapped = gestureDetector.onTouchEvent(event) && event.action == MotionEvent.ACTION_UP
                    if (isTapped) {
                        v.performClick()
                    }
                    
                    // 如果处于非交互模式，消费所有事件以阻止 WebView 响应
                    // 只有在交互模式下才返回 false，允许事件透传给 WebView 内部
                    !isInteractiveState.value
                }

                // 处理点击事件
                setOnClickListener {
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] WebView onClick listener fired")
                    onLyricsClickState.value?.invoke()
                }

                // ==================== 加载本地 HTML 资源 ====================
                // 使用 WebViewAssetLoader 提供的安全虚拟域名加载本地 HTML 资源
                // 这解决了 file:// 协议下的跨域限制问题（如 ES Module 加载）
                loadUrl("https://appassets.androidplatform.net/assets/amll/index.html")

                // 在消息队列中发布延迟任务，获取 WebView 的实际尺寸
                post {
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] WebView size after layout: width=$width, height=$height, measuredWidth=$measuredWidth, measuredHeight=$measuredHeight")
                }

                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] WebView initialized with URL: https://appassets.androidplatform.net/assets/amll/index.html")
            }
        },
        // ==================== WebView 更新逻辑 ====================
        // update 回调：当 Compose 状态变化时触发，用于同步最新状态到 WebView
        update = { view ->
            // 如果页面还未就绪，跳过本次更新
            if (!isPageReady) {
                return@AndroidView
            }

            // ==================== 播放状态同步 ====================
            /** 优化：使用节流机制，只有真正变化时才更新
             * 避免因 recompose 频繁调用 JS 导致 UI 线程阻塞 */
            val currentIsPlaying = isPlayingState.value
            if (lastIsPlayingValue != currentIsPlaying) {
                lastIsPlayingValue = currentIsPlaying
                // 只在状态真正变化时触发 JS 调用
                view.evaluateJavascript(
                    "window.setPaused && window.setPaused(${!currentIsPlaying});",
                    null
                )
            }


            // ==================== 更新时间同步（节流优化） ====================
            /** 仅在时间间隔超过阈值时才更新，避免每帧都调用 JS */
            val now = System.currentTimeMillis()
            if (now - lastTimeUpdateTimestamp >= timeUpdateIntervalMs) {
                view.evaluateJavascript("window.updateTime && window.updateTime($currentTime);", null)
                lastTimeUpdateTimestamp = now
            }



//            // ==================== 动画 FPS 设置 ====================
//            /** 使用用户自定义的 FPS 值，不再根据渲染模式强制限制 */
//            val userFps = AMLLSettings.getAmllAnimationFps(view.context)

            // ==================== 歌词背景配置 ====================
//            val backgroundRendererEnabled = AMLLSettings.isAmllBackgroundRendererEnabled(view.context) ?: true
//            val backgroundRenderer = if (backgroundRendererEnabled) {
//                AMLLSettings.getAmllBackgroundRenderer(view.context)
//            } else {
//                null
//            }
//            val cssBackgroundProperty = if (backgroundRendererEnabled) {
//                AMLLSettings.getAmllCssBackgroundProperty(view.context)
//            } else {
//                "transparent"
//            }
//            val backgroundFps = if (backgroundRendererEnabled) {
//                AMLLSettings.getAmllBackgroundFps(view.context) ?: userFps
//            } else {
//                null
//            }
//            val backgroundRenderScale = if (backgroundRendererEnabled) {
//                AMLLSettings.getAmllBackgroundRenderScale(view.context)
//            } else {
//                null
//            }
//            val enableBackgroundStaticMode = if (backgroundRendererEnabled) {
//                AMLLSettings.isAmllBackgroundStaticModeEnabled(view.context)
//            } else {
//                null
//            }

//            // 根据渲染器类型构建背景配置
//            val backgroundConfigObj = JSONObject().apply {
//                put("renderer", backgroundRenderer ?: "mesh")
////                if (backgroundRenderer == "css-bg") {
////                    cssBackgroundProperty?.let { put("cssProperty", it) }
////                } else {
////                    backgroundFps?.let { put("fps", it.coerceIn(15, 240)) }
////                    backgroundRenderScale?.let { put("renderScale", it.toDouble()) }
////                    enableBackgroundStaticMode?.let { put("staticMode", it) }
////
////                }
//            }
//            val backgroundConfig = backgroundConfigObj.toString()
//
//            if (lastBackgroundConfigValue != backgroundConfig) {
//                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: configureLyricBackground(config=$backgroundConfig)")
//                view.evaluateJavascript("window.configureLyricBackground && window.configureLyricBackground($backgroundConfig);", null)
//                lastBackgroundConfigValue = backgroundConfig
//            }

            // ==================== 歌词动画运动配置 ====================
            // 构建歌词动画的运动配置文件（弹簧、缩放、模糊等效果）
            val motionConfigObj = JSONObject().apply {
                AMLLSettings.isAmllAnimationSpringEnabled(view.context)?.let { put("enableSpring", it) }
                AMLLSettings.isAmllAnimationScaleEnabled(view.context)?.let { put("enableScale", it) }
                AMLLSettings.isAmllAnimationBlurEnabled(view.context)?.let { put("enableBlur", it) }
//                AMLLSettings.isAmllAnimationHidePassedLinesEnabled(view.context)?.let { put("hidePassedLines", it) }
//                AMLLSettings.getAmllAnimationWordFadeWidth(view.context)?.let { put("wordFadeWidth", it.toDouble()) }
//                userFps?.let { put("fps", it.coerceIn(15, 240)) }

//                val springPosY = JSONObject()
//                AMLLSettings.getAmllSpringPosYMass(view.context)?.let { springPosY.put("mass", it.toDouble()) }
//                AMLLSettings.getAmllSpringPosYDamping(view.context)?.let { springPosY.put("damping", it.toDouble()) }
//                AMLLSettings.getAmllSpringPosYStiffness(view.context)?.let { springPosY.put("stiffness", it.toDouble()) }
//                if (springPosY.length() > 0) put("springPosY", springPosY)
//
//                val springScale = JSONObject()
//                AMLLSettings.getAmllSpringScaleMass(view.context)?.let { springScale.put("mass", it.toDouble()) }
//                AMLLSettings.getAmllSpringScaleDamping(view.context)?.let { springScale.put("damping", it.toDouble()) }
//                AMLLSettings.getAmllSpringScaleStiffness(view.context)?.let { springScale.put("stiffness", it.toDouble()) }
//                if (springScale.length() > 0) put("springScale", springScale)
            }
            val motionConfig = motionConfigObj.toString()

            if (lastMotionConfigValue != motionConfig) {
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: configureLyricMotion(profile=$motionConfig)")
                view.evaluateJavascript("window.configureLyricMotion && window.configureLyricMotion($motionConfig);", null)
                lastMotionConfigValue = motionConfig
            }

            // ==================== 歌词样式配置 ====================
//            // 歌词播放器实现（DOM / DOM Lite / Canvas）
//            val lyricPlayerImpl = AMLLSettings.getAmllLyricPlayerImplementation(view.context)
//            if (lyricPlayerImpl != null) {
//                val renderModeValue = when (lyricPlayerImpl) {
//                    "dom" -> "dom"
//                    "dom-slim" -> "dom-lite"
//                    "canvas" -> "canvas"
//                    else -> "dom"
//                }
//                if (lastLyricPlayerImplValue != renderModeValue) {
//                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: setLyricPlayerImplementation($renderModeValue)")
//                    view.evaluateJavascript("window.setLyricPlayerImplementation && window.setLyricPlayerImplementation('$renderModeValue');", null)
//                    lastLyricPlayerImplValue = renderModeValue
//                }
//            }

//            // 歌词字体大小预设
//            val lyricSizePreset = AMLLSettings.getAmllLyricSizePreset(view.context)
//            if (lyricSizePreset != null && lastLyricSizePreset != lyricSizePreset) {
//                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: setLyricSizePreset($lyricSizePreset)")
//                view.evaluateJavascript("window.setLyricSizePreset && window.setLyricSizePreset('$lyricSizePreset');", null)
//                lastLyricSizePreset = lyricSizePreset
//            }

//            // 翻译歌词开关（去重优化）
//            val enableTranslationLine = AMLLSettings.isAmllTranslationLineEnabled(view.context)
//            if (enableTranslationLine != null && lastTranslationLineEnabled != enableTranslationLine) {
//                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: setEnableTranslationLine($enableTranslationLine)")
//                view.evaluateJavascript("window.setEnableTranslationLine && window.setEnableTranslationLine($enableTranslationLine);", null)
//                lastTranslationLineEnabled = enableTranslationLine
//            }

//            // 音译歌词开关（去重优化）
//            val enableRomanLine = AMLLSettings.isAmllRomanLineEnabled(view.context)
//            if (enableRomanLine != null && lastRomanLineEnabled != enableRomanLine) {
//                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: setEnableRomanLine($enableRomanLine)")
//                view.evaluateJavascript("window.setEnableRomanLine && window.setEnableRomanLine($enableRomanLine);", null)
//                lastRomanLineEnabled = enableRomanLine
//            }

//            // 提前歌词行时序
//            val enableAdvanceDynamicTime = AMLLSettings.isAmllAdvanceDynamicLyricTimeEnabled(view.context)
//            if (enableAdvanceDynamicTime != null && lastEnableAdvanceDynamicTime != enableAdvanceDynamicTime) {
//                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: setAdvanceLyricDynamicLyricTime($enableAdvanceDynamicTime)")
//                view.evaluateJavascript("window.setAdvanceLyricDynamicLyricTime && window.setAdvanceLyricDynamicLyricTime($enableAdvanceDynamicTime);", null)
//                lastEnableAdvanceDynamicTime = enableAdvanceDynamicTime
//            }
            
//            // 字体字重 - 通过 CSS 应用
//            val fontWeight = AMLLSettings.getAmllFontWeight(view.context)
//            if (fontWeight != null && fontWeight > 0) {
//              Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: applyFontWeight($fontWeight)")
//              view.evaluateJavascript(
//                "document.documentElement.style.setProperty('--amll-font-weight', '$fontWeight');",
//                null
//              )
//            }
            
//            // 字符间距 - 通过 CSS 应用
//            val letterSpacing = AMLLSettings.getAmllLetterSpacing(view.context)
//            if (!letterSpacing.isNullOrBlank()) {
//              val escapedLetterSpacing = escapeJsString(letterSpacing)
//              Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: applyLetterSpacing('$escapedLetterSpacing')")
//              view.evaluateJavascript(
//                "document.documentElement.style.setProperty('--amll-letter-spacing', '$escapedLetterSpacing');",
//                null
//              )
//            }

            // ==================== 歌词数据更新 ====================
            // 只在 lyrics 对象引用改变时才重新构建 JSON（避免每秒都构建）
            if (lyrics !== lastLyrics) {
                Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Lyrics changed: ${lyrics?.lines?.size ?: 0} lines")
                if (lyrics != null && lyrics.lines.isNotEmpty()) {
                    /** 构建歌词 JSON 数据结构*/
                    val lyricsJson = buildLyricsJson(lyrics)
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: updateLyrics(lines=${lyrics.lines.size})")
                    // 添加详细日志，显示前几行歌词内容
                    lyrics.lines.take(3).forEachIndexed { idx, line ->
                        Timber.d("[AMLLLyrics]   Line $idx: text='${line.text}', words=${line.words.size}, isBG=${line.isBG}")
                    }
                    view.evaluateJavascript("window.updateLyrics && window.updateLyrics($lyricsJson);", null)


                } else {
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] No lyrics provided, injecting empty lyrics")
                    /** 如果 lyrics 为空或 null，注入测试歌词以便调试 */
                    val testLyricsJson = ""
                    view.evaluateJavascript("window.updateLyrics && window.updateLyrics($testLyricsJson);", null)
                }
                lastLyrics = lyrics
            }

            // ==================== 专辑封面更新 ====================
            // 专辑图更新：添加数据验证和去重
            if (lastAlbumArtUri != albumArtUri) {
                /** 验证专辑图 URI 是否有效 */
                val isValidAlbumArt = !albumArtUri.isNullOrBlank() && 
                                      albumArtUri.length > 20 // 有效的 data URL 应该有一定长度
                
                if (isValidAlbumArt) {
                    /** 将 file:// URI 转换为 base64 data URL，因为 WebView 的 Fetch API 不支持 file:// 协议 */
                    val albumArtDataUrl = convertFileUriToDataUrl(view.context, albumArtUri)
                    
                    // 再次检查转换后的数据是否有效
                    if (!albumArtDataUrl.isNullOrBlank() && albumArtDataUrl.length > 100) {
                        val escapedAlbumUri = escapeJsString(albumArtDataUrl)
                        Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Bridge call: updateAlbumArt(uri=present, ${albumArtDataUrl.length} chars)")
                        view.evaluateJavascript("window.updateAlbumArt && window.updateAlbumArt(\"$escapedAlbumUri\");", null)
                        lastAlbumArtUri = albumArtUri
                    } else {
                        Timber.w("[AMLLLyrics] [$debugSource#$instanceId] Album art conversion failed or invalid data URL")
                    }
                } else {
                    // 如果专辑图为空或无效，不发送到前端，避免污染 BackgroundRender 状态
                    Timber.d("[AMLLLyrics] [$debugSource#$instanceId] Album art is empty/invalid, skipping update to avoid dirty state")
                    // 但仍然更新 lastAlbumArtUri，避免重复尝试发送无效数据
                    lastAlbumArtUri = albumArtUri
                }
            }

//            // ==================== 字体配置应用 ====================
//            /** 获取用户配置的字体家族名称 */
//            val configuredFontFamily = AMLLSettings.getAmllFontFamily(view.context)
//            /** 获取已安装的字体文件列表 */
//            val fontFiles = AMLLSettings.getAmllFontFiles(view.context)
//                .filter { it.absolutePath.isNotBlank() }
//                .mapNotNull { item ->
//                    val file = File(item.absolutePath)
//                    if (!file.exists()) return@mapNotNull null
//                    FontWebEntry(
//                        id = item.id,
//                        sortKey = item.fontFamilyName,
//                        familyName = buildRuntimeFontFamilyName(item.fontFamilyName),
//                        // 使用 WebViewAssetLoader 的虚拟域名来加载本地字体文件，
//                        // 避免从 https://appassets.androidplatform.net 访问 file:// 协议导致的跨域错误。
//                        uri = "https://appassets.androidplatform.net/fonts/${item.id}"
//                    )
//                }
//
//            // 获取启用的字体 ID 列表
//            val enabledIds = AMLLSettings.getEnabledAmllFontFileIds(view.context)
//            // 解析用户偏好的字体顺序
//            val preferredOrder = parsePreferredFontOrder(configuredFontFamily)
//            // 根据偏好排序启用的字体
//            val enabledFamilies = fontFiles
//                .filter { enabledIds.contains(it.id) }
//                .sortedWith(
//                    compareBy<FontWebEntry> { fontSortPriority(it.sortKey, preferredOrder) }
//                        .thenBy { it.sortKey.lowercase() }
//                        .thenBy { it.id }
//                )
//                .map { it.familyName }
//                .distinct()
//
//            // 构建最终使用的字体家族栈
//            val effectiveFamily = if (enabledFamilies.isNotEmpty()) {
//                val enabledStack = enabledFamilies.joinToString(", ") { "\"$it\"" }
//                if (configuredFontFamily != null) "$enabledStack, $configuredFontFamily" else enabledStack
//            } else {
//                configuredFontFamily
//            }
//
//            // 构建字体配置签名（用于检测变化）
//            val fontSignature = buildString {
//                append(effectiveFamily)
//                append("|")
//                append(fontFiles.joinToString(";") { "${it.id}:${it.familyName}:${it.uri}" })
//                append("|")
//                append(enabledFamilies.joinToString(","))
//            }
//
//            // 如果字体配置发生变化，应用新的字体设置
//            if (lastFontConfigSignature != fontSignature) {
//                val script = buildApplyFontScript(effectiveFamily, fontFiles)
//                Timber.d(
//                    "[AMLLLyrics] [$debugSource#$instanceId] Bridge call: applyFontSettings(enabled=${enabledFamilies.size}, files=${fontFiles.size})"
//                )
//                view.evaluateJavascript(script, null)
//                lastFontConfigSignature = fontSignature
//            }
        },
        // ==================== WebView 销毁回调 ====================
        // 当组件被销毁时，销毁 WebView 以避免内存泄漏
        onRelease = { view ->
            // 当组件被销毁时，销毁 WebView 以避免内存泄漏
            Timber.i("[AMLLLyrics] [$debugSource] Destroying AMLL WebView")
            view.stopLoading()      // 停止加载
            view.clearHistory()     // 清除历史记录
            view.clearCache(true)   // 清除缓存
            view.removeJavascriptInterface("Android")  // 移除 JS 接口
            view.destroy()          // 销毁 WebView
        }
    )
}

//private data class FontWebEntry(
//    val id: String,
//    val sortKey: String,
//    val familyName: String,
//    val uri: String
//)
//
//private fun buildRuntimeFontFamilyName(baseFamilyName: String): String {
//    // 直接使用原始字体名称，以便与 CSS 中的 font-family 匹配
//    return baseFamilyName
//}
//
//private fun parsePreferredFontOrder(configuredFontFamily: String?): List<String> {
//    if (configuredFontFamily == null) return emptyList()
//    return configuredFontFamily
//        .split(',')
//        .map { normalizeFontToken(it) }
//        .filter { it.isNotBlank() }
//}
//
//private fun fontSortPriority(sortKey: String, preferredOrder: List<String>): Int {
//    if (preferredOrder.isEmpty()) return Int.MAX_VALUE
//    val normalizedSortKey = normalizeFontToken(sortKey)
//    for (index in preferredOrder.indices) {
//        val preferred = preferredOrder[index]
//        if (preferred.isBlank()) continue
//        if (normalizedSortKey.contains(preferred) || preferred.contains(normalizedSortKey)) {
//            return index
//        }
//    }
//    return Int.MAX_VALUE
//}
//
//private fun normalizeFontToken(value: String): String {
//    return value
//        .lowercase()
//        .replace(Regex("[^a-z0-9]"), "")
//}
//
//private fun buildApplyFontScript(effectiveFamily: String?, files: List<FontWebEntry>): String {
//    // 将字体家族名称转换为 JSON 安全的字符串
//    val familyJson = if (effectiveFamily != null) "\"${escapeJsStringForJson(effectiveFamily)}\"" else "null"
//
//    // 构建文件数组的 JSON 表示
//    val filesArrayJson = if (files.isEmpty()) {
//        "[]"
//    } else {
//        val filesEntries = files.joinToString(",") { entry ->
//            "{id:\"${escapeJsStringForJson(entry.id)}\",familyName:\"${escapeJsStringForJson(entry.familyName)}\",uri:\"${escapeJsStringForJson(entry.uri)}\"}"
//        }
//        "[$filesEntries]"
//    }
//
//    return buildString {
//        append("(function(){")
//        append("var effectiveFamily=$familyJson;")
//        append("var files=$filesArrayJson;")
//        append("var styleId='amll-dynamic-font-face-style';")
//        append("var styleNode=document.getElementById(styleId);")
//        append("if(!styleNode){styleNode=document.createElement('style');styleNode.id=styleId;document.head.appendChild(styleNode);}")
//        append("var css='';")
//        append("for(var i=0;i<files.length;i+=1){var item=files[i];if(!item||!item.familyName||!item.uri)continue;if(item.uri.indexOf('data:image/svg+xml')===0)continue;css+='@font-face{font-family:\"'+item.familyName+'\";src:url(\"'+item.uri+'\");font-display:swap;}';}")
//        append("styleNode.textContent=css;")
//        append("if(effectiveFamily){")
//        append("document.documentElement.style.setProperty('--amll-user-font-family',effectiveFamily);")
//        append("document.documentElement.style.setProperty('--amll-lp-font-family','var(--amll-user-font-family)');")
//        append("}")
//        append("var players=document.querySelectorAll('.amll-lyric-player');")
//        append("for(var j=0;j<players.length;j+=1){players[j].style.fontFamily='var(--amll-lp-font-family)';}")
//        append("})();")
//    }
//}


private fun escapeJsString(value: String): String {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("'", "\\'")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}

/**
 * 清洗背景歌词文本：移除第一个 "(" 和最后一个 ")"
 * 
 * 这个函数用于在生成 JSON 时处理背景歌词，移除括号但保留其他内容。
 * TTML 原始数据中会保留完整的括号格式。
 * 
 * @param text 原始文本
 * @return 清洗后的文本（移除了首尾括号）
 */
private fun cleanBackgroundText(text: String): String {
    /**
     * 背景歌词同样遵循可见空格语义：禁止 trim。
     * 仅去除文本中第一个 "(" 和最后一个 ")"，不改动其它内容。
     */
    val firstParenIndex = text.indexOf('(')
    val lastParenIndex = text.lastIndexOf(')')
    
    if (firstParenIndex != -1 && lastParenIndex != -1 && lastParenIndex > firstParenIndex) {
        // 移除第一个 "(" 和最后一个 ")"
        return text.substring(0, firstParenIndex) +
               text.substring(firstParenIndex + 1, lastParenIndex) +
               text.substring(lastParenIndex + 1)
    }
    
    return text
}

private fun buildLyricsJson(lyrics: UnifiedLyrics): String {
    val bgLines = lyrics.lines.filter { it.isBG }
    val bgWithTranslation = bgLines.count { !it.translation.isNullOrBlank() }
    val bgWithRoman = bgLines.count { !it.transliteration.isNullOrBlank() }
    val sampleBg = bgLines.firstOrNull()
    Timber.d("[BG-LYRICS-DEBUG] buildLyricsJson summary: total=${lyrics.lines.size}, bg=${bgLines.size}, bgWithTrans=$bgWithTranslation, bgWithRoman=$bgWithRoman, sampleBg='${sampleBg?.text ?: ""}', sampleTrans='${sampleBg?.translation ?: ""}'")

    /** 调试日志：限制在 10 行以内，超出的降级为 v 级别 */
    var debugCount = 0
    
    val linesJson = lyrics.lines.joinToString(",") { line ->
        /** 背景歌词清洗：移除第一个 "(" 和最后一个 ")" */
        val cleanedText = if (line.isBG) {
            cleanBackgroundText(line.text)
        } else {
            line.text
        }
        
        val text = cleanedText.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
        val translation = line.translation?.replace("\\", "\\\\")?.replace("\"", "\\\"") ?: ""
        val transliteration = line.transliteration?.replace("\\", "\\\\")?.replace("\"", "\\\"") ?: ""
        
        /** 构建 words 数组 */
        val wordsJson = if (line.words.isNotEmpty()) {
            line.words.joinToString(",") { word ->
                /** 背景歌词的单词也需要清洗 */
                val wordText = if (line.isBG) {
                    cleanBackgroundText(word.word).replace("\\", "\\\\").replace("\"", "\\\"")
                } else {
                    word.word.replace("\\", "\\\\").replace("\"", "\\\"")
                }
                /** 增加防御性检查：确保 endTime > startTime，防止 JS 计算 progress 时出现 NaN (0/0) 或 Infinity (1/0)
                // 这修复了 "Invalid keyframe value for property maskPosition: NaNpx 0" 的报错 */
                val wordEndTime = if (word.endTime <= word.startTime) word.startTime + 1 else word.endTime
                """{"word":"$wordText","startTime":${word.startTime},"endTime":$wordEndTime}"""
            }
        } else {
            /** 如果没有逐词信息，则使用整行文本作为单词 */
            val wordText = text.replace("\"", "\\\"")
            val wordEndTime = if (line.endTime <= line.startTime) line.startTime + 1 else line.endTime
            """{"word":"$wordText","startTime":${line.startTime},"endTime":$wordEndTime}"""
        }
        
        // 调试日志：只记录前 5 行
        if (line.words.isNotEmpty()) {
            if (debugCount < 5) {
                Timber.d("[AMLLLyrics] Building JSON for line: '${line.text}' with ${line.words.size} words")
                debugCount++
            }
        }
        
        // 调试背景歌词的数据传递
        if (line.isBG) {
            Timber.d("[BG-LYRICS-DEBUG] JSON for BG line: text='$text' translation='$translation' roman='$transliteration' isBG=true")
        }
        
        /** 增加防御性检查：确保整行的 endTime > startTime */
        val lineEndTime = if (line.endTime <= line.startTime) line.startTime + 1 else line.endTime

        """{
            "startTime":${line.startTime},
            "endTime":$lineEndTime,
            "text":"$text",
            "translatedLyric":"$translation",
            "romanLyric":"$transliteration",
            "words":[$wordsJson],
            "isBG":${line.isBG},
            "isDuet":${line.isDuet}
        }"""
    }

    val title = lyrics.metadata.title.replace("\\", "\\\\").replace("\"", "\\\"")
    val artist = lyrics.metadata.artist.replace("\\", "\\\\").replace("\"", "\\\"")
    
    /** 如果有原始歌词内容，也包含在 JSON 中供前端解析 */
    val rawPart = if (lyrics.rawContent != null) {
        val escapedRaw = lyrics.rawContent.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
        """, "raw": "$escapedRaw", "format": "${lyrics.format ?: "lrc"}" """
    } else ""

    return """{"metadata":{"title":"$title","artist":"$artist"},"lines":[$linesJson] $rawPart}"""
}

/**
 * AMLL JavaScript 接口类
 * 
 * 这个类通过 JavascriptInterface 暴露给 WebView 中的 JavaScript 调用，
 * 实现了前端页面与 Android 原生代码之间的双向通信���
 *
 * **主要功能**：
 * 1. 日志转发：将前端日志转发到 Timber
 * 2. 歌词点击处理：响应用户点击歌词行的操作
 * 3. 播放状态查询：提供当前播放状态给前端
 * 4. WebSocket 消息桥接：将前端消息转发到 WebSocket 服务器
 * 
 * @param debugSource 调试来源标签
 * @param instanceId 实例 ID（用于区分多个视图）
 * @param onLineSeek 歌词行跳转回调
 * @param onSeekRequested 跳转请求回调
 * @param isPlayingProvider 播放状态提供者函数
 */
class AMLLInterface(
    private val debugSource: String,
    private val instanceId: Int,
    private val onLineSeek: ((Long) -> Unit)? = null,
    private val onSeekRequested: ((Long) -> Unit)? = null,
    private val isPlayingProvider: () -> Boolean = { true },
    private val onPageReady: (() -> Unit)? = null,
//    private val onLyricsParsed: ((String) -> Unit)? = null
) {
    /**
     * 页面初始化完成通知
     */
    @JavascriptInterface
    fun onPageReady() {
        Timber.i("[AMLLLyrics] [$debugSource#$instanceId] JS reported page ready")
        onPageReady?.invoke()
    }

//    /**
//     * JS 解析完成回调（由 WASM 解析器解析后触发）
//     */
//    @JavascriptInterface
//    fun onLyricsParsed(json: String) {
//        Timber.i("[AMLLLyrics] [$debugSource#$instanceId] JS reported lyrics parsed: ${json.length} chars")
//        onLyricsParsed?.invoke(json)
//    }

    /**
     * 日志输出接口（供 JavaScript 调用）
     * 
     * @JavascriptInterface 注解使得这个方法可以被 WebView 中的 JavaScript 直接调用。
     * 
     * @param message 日志消息内容
     * @param level 日志级别（debug/info/warn/error），默认为 "debug"
     */
    @JavascriptInterface
    fun log(message: String, level: String = "debug") {
        val levelUpper = level.uppercase()
        // 根据日志级别分别转发到 Timber
        when (levelUpper) {
            "DEBUG" -> Timber.d("[AMLLLyrics] [WebView] JS: $message")
            "INFO" -> Timber.i("[AMLLLyrics] [WebView] JS: $message")
            "WARN" -> Timber.w("[AMLLLyrics] [WebView] JS: $message")
            "ERROR" -> Timber.e("[AMLLLyrics] [WebView] JS: $message")
            else -> Timber.d("[AMLLLyrics] [WebView] JS: $message")
        }
    }

    /**
     * 歌词行点击处理接口（供 JavaScript 调用）
     * 
     * 当用户点击歌词中的某一行时，JavaScript 会调用这个方法。
     * 
     * @param lineIndex 被点击的歌词行索引
     * @param startTime 该歌词行的开始时间（毫秒）
     */
    @JavascriptInterface
    fun onLineClick(lineIndex: Int, startTime: Long) {
        Timber.i("[AMLLLyrics] [$debugSource#$instanceId] User clicked lyric line: index=$lineIndex, startTime=$startTime, callbackPresent=${onLineSeek != null}")
        // 触发跳转请求
        onSeekRequested?.invoke(startTime)
        // 同时调用外部回调
        onLineSeek?.invoke(startTime)
    }

    /**
     * 查询播放状态接口（供 JavaScript 调用）
     * 
     * JavaScript 可以通过 window.Android.isPlaying() 查询当前是否正在播放。
     * 
     * @return 当前播放状态（true=播放中，false=已暂停）
     */
    @JavascriptInterface
    fun isPlaying(): Boolean {
        return isPlayingProvider()
    }

}

/**
 * 将 file:// URI 转换为 base64 data URL，以便 WebView 能够加载本地图片
 * 
 * **为什么需要转换？**
 * - WebView 的 Fetch API 不支持 file:// 协议
 * - data URL 可以直接在 HTML 中使用，无需额外请求
 * - Base64 编码确保二进制数据可以安全传输
 * 
 * **支持的 URI 类型**：
 * - file:// 开头的本地文件路径
 * - content:// 开头的内容提供者 URI
 * - 其他类型直接返回（可能是 data URL）
 * 
 * @param context Android Context
 * @param uriString 要转换的 URI 字符串
 * @return 转换���的 data URL（格式：data:image/jpeg;base64,...），失败返回 null
 */
private fun convertFileUriToDataUrl(context: Context, uriString: String?): String? {
    // URI 为空时直接返回 null
    if (uriString.isNullOrBlank()) {
        return null
    }
    
    return try {
        /** 根据 URI 类型选择不同的输入流获取方式 */
        val inputStream = when {
            uriString.startsWith("file://") -> {
                /** file:// URI：直接从文件系统读取 */
                val path = uriString.removePrefix("file://")
                File(path).inputStream()
            }
            uriString.startsWith("content://") || uriString.startsWith("android.resource://") -> {
                /** content:// 或 android.resource:// URI：通过 ContentResolver 读取 */
                val uri = uriString.toUri()
                context.contentResolver.openInputStream(uri)
            }
            else -> {
                // 其他类型（可能是 data URL）直接返回原始字符串
                Timber.w("[AMLLLyrics] [WebView] Unsupported URI scheme: $uriString")
                return uriString // 直接返回原始字符串（可能是 data URL）
            }
        }
        // 使用 use 自动关闭输入流，避免资源泄漏
        inputStream?.use { stream ->
            /** 读取所有字节 */
            val bytes = stream.readBytes()
            /** 获取 MIME 类型（默认为 image/jpeg） */
            val mimeType = getMimeType(uriString) ?: "image/jpeg"
            /** Base64 编码（NO_WRAP 选项不添加换行符）*/
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            // 构建 data URL：data:image/jpeg;base64,<base64 数据>
            "data:$mimeType;base64,$base64"
        }
    } catch (e: Exception) {
        Timber.e("[AMLLLyrics] [WebView] Failed to convert file URI to data URL: $uriString $e")
        null
    }
}

/**
 * 根据文件扩展名获取 MIME 类型
 * 
 * **用途**：
 * - 用于 data URL 的 MIME 类型标识
 * - 帮助浏览器正确识别和渲染图片格式
 * 
 * @param uriString 文件 URI 或路径
 * @return MIME 类型字符串，未知类型返回 null
 */
private fun getMimeType(uriString: String): String? {
    return when {
        uriString.endsWith(".png", ignoreCase = true) -> "image/png"
        uriString.endsWith(".jpg", ignoreCase = true) || uriString.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
        uriString.endsWith(".gif", ignoreCase = true) -> "image/gif"
        uriString.endsWith(".webp", ignoreCase = true) -> "image/webp"
        else -> "image/jpeg" // 默认为 JPEG
    }
}

/**
 * 清除 WebView 的所有缓存数据
 * 
 * **清除的内容**：
 * 1. 内存缓存（HTTP 缓存、图片缓存等）
 * 2. DOM 存储（localStorage、sessionStorage）
 * 
 * **为什么需要清除？**
 * - 确保每次加载最新的 HTML 和 JS 文件
 * - 避免旧版本代码导致的兼容性问题
 * - 清理可能的脏数据
 */
private fun WebView.clearAllCache() {
    try {
        // 清除内存缓存（包括 HTTP 缓存、图片缓存等）
        clearCache(true)
        
        // 清除 DOM 存储（localStorage、sessionStorage 等）
        // 先禁用再启用，强制重置 DOM 存储
        settings.domStorageEnabled = false
        settings.domStorageEnabled = true
        
        Timber.d("[AMLLLyrics] WebView cache cleared")
    } catch (e: Exception) {
        Timber.d("[AMLLLyrics] Failed to clear WebView cache: ${e.message}")
    }
}