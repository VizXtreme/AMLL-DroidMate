package io.github.zeehan2005.scoremuse.components

import android.content.Context
import dev.amll.droidmate.data.repository.LyricsCacheRepository
import dev.amll.droidmate.data.repository.LyricsRepository

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
    /**
     * 提供 HTTP 客户端实例
     *
     * 所有网络请求都通过这个工厂方法创建，确保统一的配置（缓存、超时等）
     *
     * @param context Android 上下文
     * @return 配置好的 HttpClient 实例
     */
    fun provideHttpClient(context: Context) = HttpClientFactory.create(context)

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
}