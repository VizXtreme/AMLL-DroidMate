package io.github.zeehan2005.scoremuse.components

import android.content.Context
import io.ktor.client.HttpClient
import io.github.zeehan2005.scoremuse.data.repository.LyricsCacheRepository
import io.github.zeehan2005.scoremuse.data.repository.LyricsRepository
import dev.amll.droidmate.data.parser.WasmLyricParser

/**
 * 简单的手动服务定位器
 *
 * 为了避免在整个代码库中散乱地创建 HttpClientFactory 和 Repository 实例，
 * 这里提供了一个统一的入口来管理这些依赖。
 *
 * 设计考虑：
 * 1. 简化依赖获取：不需要在每个类中重复构造
 * 2. 支持测试：可以传入替代实现进行单元测试
 * 3. 轻量级方案：不使用 Dagger/Hilt 等重型框架
 *
 * 用法：
 * ```kotlin
 * val httpClient = ServiceLocator.provideHttpClient(context)
 * val lyricsRepo = ServiceLocator.provideLyricsRepository(context)
 * ```
 */
object ServiceLocator {
    private var httpClient: HttpClient? = null
    private var wasmLyricParser: WasmLyricParser? = null

    /**
     * 提供单例 HTTP 客户端实例
     *
     * 性能优化：全局复用同一个客户端，避免重复创建资源（如连接池、缓存等），
     * 同时解决了因未关闭客户端导致的资源泄漏问题。
     */
    @Synchronized
    fun provideHttpClient(context: Context): HttpClient {
        if (httpClient == null) {
            httpClient = HttpClientFactory.create(context.applicationContext)
        }
        return httpClient!!
    }

    /**
     * 关闭全局 HTTP 客户端，释放资源
     */
    fun closeHttpClient() {
        httpClient?.close()
        httpClient = null
    }

    /**
     * 提供歌词仓库实例
     *
     * 封装了 HTTP 客户端和缓存仓库的依赖关系，
     * 调用者不需要关心这些内部依赖。
     *
     * @param context Android 上下文
     * @return 配置好的 LyricsRepository 实例
     */
    fun provideLyricsRepository(context: Context): LyricsRepository =
        LyricsRepository(provideHttpClient(context), provideLyricsCacheRepository(context), context)

    /**
     * 提供歌词缓存仓库实例
     *
     * 负责管理本地歌词缓存，使用 SharedPreferences 持久化存储
     *
     * @param context Android 上下文
     * @return 配置好的 LyricsCacheRepository 实例
     */
    fun provideLyricsCacheRepository(context: Context): LyricsCacheRepository =
        LyricsCacheRepository(context)

    /**
     * 提供 WASM 歌词解析器实例 (单例)
     *
     * @param context Android 上下文
     * @return WasmLyricParser 实例
     */
    @Synchronized
    fun provideWasmLyricParser(context: Context): WasmLyricParser {
        if (wasmLyricParser == null) {
            wasmLyricParser = WasmLyricParser(context.applicationContext)
        }
        return wasmLyricParser!!
    }
}
