package dev.amll.droidmate.data.parser

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.LyricWord
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * WASM 歌词解析器 (Headless 容器)
 *
 * 负责管理一个不可见的 WebView 实例，用于运行前端的 WASM 歌词解析逻辑。
 * 这样做可以复用前端成熟的解析器 (lyricProcessor.ts)，保持解析逻辑的一致性。
 */
class WasmLyricParser(private val context: Context) {
    private var webView: WebView? = null
    private val isPageReady = AtomicBoolean(false)
    private val readyDeferred = CompletableDeferred<Unit>()
    private val mutex = Mutex()
    private var pendingResult: CompletableDeferred<String?>? = null

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Serializable
    private data class WasmLyricWord(
        val word: String = "",
        val startTime: Long = 0,
        val endTime: Long = 0
    )

    @Serializable
    private data class WasmLyricLine(
        val words: List<WasmLyricWord> = emptyList(),
        val translatedLyric: String? = null,
        val romanLyric: String? = null,
        val startTime: Long = 0,
        val endTime: Long = 0,
        val isBG: Boolean = false,
        val isDuet: Boolean = false
    )

    init {
        // WebView 必须在主线程初始化
        if (Looper.myLooper() == Looper.getMainLooper()) {
            setupWebView()
        } else {
            Handler(Looper.getMainLooper()).post {
                setupWebView()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        Timber.d("[WasmLyricParser] Initializing Headless WebView for parsing")

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .build()

        webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            // 禁用不必要的功能以节省资源
            settings.allowFileAccess = false
            settings.allowContentAccess = true

            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ) = assetLoader.shouldInterceptRequest(request.url)

                override fun onPageFinished(view: WebView, url: String) {
                    Timber.d("[WasmLyricParser] WebView page loaded: $url")
                }
            }

            addJavascriptInterface(object {
                @JavascriptInterface
                fun onPageReady() {
                    Timber.i("[WasmLyricParser] JS reported page ready")
                    isPageReady.set(true)
                    readyDeferred.complete(Unit)
                }

                @JavascriptInterface
                fun onLyricsParsedResult(resultJson: String) {
                    Timber.d("[WasmLyricParser] Received parsing result: ${resultJson.length} chars")
                    pendingResult?.complete(resultJson)
                }

                @JavascriptInterface
                fun log(msg: String, level: String) {
                    val logMsg = "[WasmLyricParser] [JS] $msg"
                    when (level.lowercase()) {
                        "error" -> Timber.e(logMsg)
                        "warn" -> Timber.w(logMsg)
                        else -> Timber.d(logMsg)
                    }
                }
            }, "Android")

            // 加载包含 amll.bundle.js 的 index.html
            loadUrl("https://appassets.androidplatform.net/assets/amll/index.html")
        }
    }

    /**
     * 调用 WASM 解析器解析歌词
     *
     * @param raw 歌词原始文本
     * @param format 歌词格式 (lrc, yrc, qrc, krc, ttml 等)
     * @return 解析后的歌词行列表，失败返回 null
     */
    suspend fun parse(raw: String, format: String): List<LyricLine>? = mutex.withLock {
        // 等待页面就绪 (包含 WASM 加载)
        if (!isPageReady.get()) {
            Timber.d("[WasmLyricParser] Waiting for WebView to be ready...")
            try {
                withTimeout(8000) { readyDeferred.await() }
            } catch (e: Exception) {
                Timber.e("[WasmLyricParser] WebView ready timeout" )
                return null
            }
        }

        val deferred = CompletableDeferred<String?>()
        pendingResult = deferred

        try {
            withContext(Dispatchers.Main) {
                // 使用 JSONObject.quote 安全地转义字符串，避免注入
                val escapedRaw = JSONObject.quote(raw)
                val script =
                    "if(window.parseLyrics) { window.parseLyrics($escapedRaw, '$format'); } else { Android.log('parseLyrics not found', 'error'); }"
                webView?.evaluateJavascript(script, null)
            }

            // 等待解析结果 (增加超时)
            val resultJson = withTimeoutOrNull(10000) { deferred.await() }
            if (resultJson == null) {
                Timber.e("[WasmLyricParser] Parsing timed out or failed to return result")
                return null
            }

            // 将 JSON 结果转换为 Kotlin 对象
            return decodeJsonResult(resultJson)
        } catch (e: Exception) {
            Timber.e(e, "[WasmLyricParser] Error during parsing execution")
            return null
        } finally {
            pendingResult = null
        }
    }

    private fun decodeJsonResult(resultJson: String): List<LyricLine>? {
        return try {
            val wasmLines = json.decodeFromString<List<WasmLyricLine>>(resultJson)
            if (wasmLines.isEmpty()) return emptyList()

            wasmLines.map { line ->
                // JS 端的 LyricLine 结构映射到 Kotlin 端
                val text = if (line.words.isNotEmpty()) {
                    line.words.joinToString("") { it.word }
                } else {
                    ""
                }

                LyricLine(
                    startTime = line.startTime,
                    endTime = line.endTime,
                    text = text,
                    translation = line.translatedLyric,
                    transliteration = line.romanLyric,
                    words = line.words.map { LyricWord(it.word, it.startTime, it.endTime) },
                    isBG = line.isBG,
                    isDuet = line.isDuet
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "[WasmLyricParser] Failed to decode JSON result: $resultJson")
            null
        }
    }
}