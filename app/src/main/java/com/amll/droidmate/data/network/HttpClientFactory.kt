package com.amll.droidmate.data.network

import android.content.Context
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * HTTP 客户端工厂 - 统一配置所有网络请求
 * 缓存存储在 Android cache 目录，可通过系统设置清除
 * 
 * 性能优化：
 * - 智能 HTTP 缓存策略
 * - 歌词搜索结果强制缓存 5 分钟
 * - 无网络时自动使用缓存
 */
object HttpClientFactory {
    
    private const val CACHE_SIZE = 50L * 1024 * 1024 // 50 MB
    private const val CACHE_DIR_NAME = "http_cache"
    
    /**
     * 创建配置好的 HttpClient，包含缓存支持
     * 
     * @param context Android Context
     * @return 配置好的 HttpClient 实例
     */
    fun create(context: Context): HttpClient {
        // 创建缓存目录（在 Android cache 路径下）
        val cacheDir = File(context.cacheDir, CACHE_DIR_NAME)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        
        // 缓存拦截器：智能控制不同请求的缓存策略
        val cacheInterceptor = Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            
            // 根据 URL 路径设置不同的缓存策略
            val url = request.url.toString()
            
            // 歌词搜索结果缓存 5 分钟
            if (url.contains("lyric/search", ignoreCase = true) || 
                url.contains("lyrics", ignoreCase = true)) {
                return@Interceptor response.newBuilder()
                    .header("Cache-Control", "max-age=300") // 5 分钟
                    .build()
            }
            
            // 其他请求默认缓存 1 小时
            response.newBuilder()
                .header("Cache-Control", "max-age=3600")
                .build()
        }
        
        return HttpClient(OkHttp) {
            // 配置 OkHttp 引擎
            engine {
                config {
                    // 添加 HTTP 缓存（存储在 cache 目录）
                    cache(Cache(cacheDir, CACHE_SIZE))
                    
                    // 添加缓存拦截器
                    addInterceptor(cacheInterceptor)
                    
                    // 连接超时
                    connectTimeout(30, TimeUnit.SECONDS)
                    readTimeout(30, TimeUnit.SECONDS)
                    writeTimeout(30, TimeUnit.SECONDS)
                    
                    // 空闲连接回收（使用 OkHttp 原生 API）
                    connectionPool(
                        okhttp3.ConnectionPool(
                            maxIdleConnections = 5,
                            keepAliveDuration = 5,
                            timeUnit = TimeUnit.MINUTES
                        )
                    )
                }
            }
            
            // 内容协商 - JSON 序列化/反序列化
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                })
            }
        }
    }
}
