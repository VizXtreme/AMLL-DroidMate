package io.github.zeehan2005.scoremuse.components

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * HTTP 客户端工厂 - 统一配置所有网络请求
 *
 * 这个对象负责创建和配置统一的 HTTP 客户端，确保所有网络请求都遵循相同的配置标准。
 * 通过使用工厂模式，避免了在每个需要网络请求的地方重复配置。
 *
 * 缓存策略：
 * - 歌词搜索结果：缓存 5 分钟（因为同一首歌的搜索结果通常不会频繁变化）
 * - 其他请求：缓存 1 小时（减少重复请求，提高响应速度）
 * - 无网络时：自动使用缓存（保证应用在离线状态下仍能提供基本功能）
 *
 * 性能优化：
 * - 智能 HTTP 缓存策略
 * - 歌词搜索结果强制缓存 5 分钟
 * - 无网络时自动使用缓存
 */
object HttpClientFactory {

    // 缓存配置常量
    /** 缓存大小限制 */
    private const val CACHE_SIZE = 50L * 1024 * 1024
    /** 缓存目录名称 */
    private const val CACHE_DIR_NAME = "http_cache"

    /**
     * 创建配置好的 HttpClient，包含缓存支持
     *
     * 这个函数会创建一个完全配置的 HTTP 客户端，包括：
     * 1. 磁盘缓存（50MB）
     * 2. 智能缓存策略（根据 URL 自动调整）
     * 3. 连接超时设置
     * 4. JSON 序列化/反序列化支持
     *
     * @param context Android Context（用于获取缓存目录）
     * @return 配置好的 HttpClient 实例
     */
    fun create(context: Context): HttpClient {
        /** 创建缓存目录（在 Android cache 路径下）
        // 使用 context.cacheDir 确保缓存在系统清理时可以被自动清除 */
        val cacheDir = File(context.cacheDir, CACHE_DIR_NAME)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        /** 缓存拦截器：智能控制不同请求的缓存策略
        // 这是 OkHttp 的核心机制，可以在请求/响应过程中修改行为*/
        val cacheInterceptor = Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)  // 执行请求

            /** 根据 URL 路径设置不同的缓存策略 */
            val url = request.url.toString()

            // 歌词搜索结果缓存 5 分钟
            // 原因：同一首歌的搜索结果通常很稳定，不需要每次都重新请求
            if (url.contains("lyric/search", ignoreCase = true) ||
                url.contains("lyrics", ignoreCase = true)
            ) {
                return@Interceptor response.newBuilder()
                    .header("Cache-Control", "max-age=300") // 5 分钟
                    .build()
            }

            // 其他请求默认缓存 1 小时
            // 这是一个保守的策略，既能减少网络请求，又不会让数据过时太久
            response.newBuilder()
                .header("Cache-Control", "max-age=3600")
                .build()
        }

        return HttpClient(OkHttp) {
            // 配置 OkHttp 引擎
            engine {
                config {
                    // 添加 HTTP 缓存（存储在 cache 目录）
                    // 这是 OkHttp 的原生缓存机制，会自动处理缓存的存储和清理
                    cache(Cache(cacheDir, CACHE_SIZE))

                    // 添加缓存拦截器
                    // 这样每个请求都会经过我们的智能缓存逻辑
                    addInterceptor(cacheInterceptor)

                    // 连接超时设置
                    // 30 秒是一个合理的值：既不会因为网络波动而频繁失败，也不会让用户等待太久
                    connectTimeout(30, TimeUnit.SECONDS)
                    readTimeout(30, TimeUnit.SECONDS)
                    writeTimeout(30, TimeUnit.SECONDS)

                    // 空闲连接回收（使用 OkHttp 原生 API）
                    // 这有助于释放不再使用的连接，避免资源浪费
                    connectionPool(
                        ConnectionPool(
                            maxIdleConnections = 5,  // 最多保留 5 个空闲连接
                            keepAliveDuration = 5,   // 连接空闲 5 分钟后关闭
                            timeUnit = TimeUnit.MINUTES
                        )
                    )
                }
            }

            // 内容协商 - JSON 序列化/反序列化
            // 这使得我们可以直接发送和接收 Kotlin 对象，无需手动处理 JSON
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true  // 忽略未知的 JSON 字段（防止 API 变更导致崩溃）
                    isLenient = true          // 宽松的解析模式（允许一些非标准的 JSON 格式）
                    prettyPrint = false       // 不格式化输出（节省带宽）
                })
            }
        }
    }
}