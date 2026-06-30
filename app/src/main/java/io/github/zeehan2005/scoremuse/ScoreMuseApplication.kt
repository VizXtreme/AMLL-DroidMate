package io.github.zeehan2005.scoremuse

import android.app.Application
import io.github.zeehan2005.scoremuse.util.WindowEmbeddingInitializer

/**
 * 应用级 Application。
 *
 * 在此注册大屏 Activity Embedding 规则，确保在任何 Activity 启动前规则已生效
 * （深链接、进程重建等场景下也能正确分屏）。
 */
class ScoreMuseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 大屏/横屏设备上注册 Activity 拆分规则（类平板平行视界）
        WindowEmbeddingInitializer.init(this)
    }
}
