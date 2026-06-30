package io.github.zeehan2005.scoremuse.util

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import androidx.window.embedding.RuleController
import androidx.window.embedding.SplitAttributes
import androidx.window.embedding.SplitPairFilter
import androidx.window.embedding.SplitPairRule
import androidx.window.embedding.SplitRule
import dev.amll.droidmate.ui.settings.ComponentSettings
import io.github.zeehan2005.scoremuse.MainActivity
import io.github.zeehan2005.scoremuse.ui.CustomLyricsActivity
import io.github.zeehan2005.scoremuse.ui.LyricsCacheActivity
import io.github.zeehan2005.scoremuse.ui.settings.LyricOffsetManagementActivity
import io.github.zeehan2005.scoremuse.ui.settings.LyricOffsetSettingsActivity
import io.github.zeehan2005.scoremuse.ui.settings.LogDisplayActivity
import io.github.zeehan2005.scoremuse.ui.settings.SettingsActivity
import timber.log.Timber

/**
 * 大屏 Activity Embedding 初始化器。
 *
 * 在大屏/横屏设备上，从主界面启动设置、歌词管理等 Activity 时，
 * 自动将其与 [MainActivity] 并排显示，实现类似平板“平行视界”的体验。
 */
object WindowEmbeddingInitializer {

    /**
     * 注册所有需要并排显示的 Activity 拆分规则。
     *
     * 建议在 [android.app.Application.onCreate] 或 [android.app.Activity.onCreate]
     * 中尽早调用一次。重复注册幂等（RuleController 会自动去重相同规则）。
     */
    fun init(context: Context) {
        try {
            val controller = RuleController.getInstance(context)
            val rules = buildSplitRules(context)
            rules.forEach { controller.addRule(it) }
        } catch (e: Throwable) {
            Timber.e(e, "[WindowEmbedding] Failed to register split rules")
        }
    }

    private fun buildSplitRules(context: Context): List<SplitPairRule> {
        val pairs = listOf(
            // 主界面 -> 设置
            MainActivity::class.java to SettingsActivity::class.java,
            // 主界面 -> 自定义歌词
            MainActivity::class.java to CustomLyricsActivity::class.java,
            // 主界面 -> 歌词缓存管理
            MainActivity::class.java to LyricsCacheActivity::class.java,
            // 设置 -> 组件设置
            SettingsActivity::class.java to ComponentSettings::class.java,
            // 设置 -> 歌词偏移设置
            SettingsActivity::class.java to LyricOffsetSettingsActivity::class.java,
            // 设置 -> 歌词偏移管理
            SettingsActivity::class.java to LyricOffsetManagementActivity::class.java,
            // 设置 -> 日志显示
            SettingsActivity::class.java to LogDisplayActivity::class.java,
            // 自定义歌词 -> 歌词缓存管理
            CustomLyricsActivity::class.java to LyricsCacheActivity::class.java
        )

        return pairs.map { (primary, secondary) ->
            createSplitPairRule(context, primary, secondary)
        }
    }

    private fun <P : Activity, S : Activity> createSplitPairRule(
        context: Context,
        primary: Class<P>,
        secondary: Class<S>
    ): SplitPairRule {
        val filter = SplitPairFilter(
            ComponentName(context.packageName, primary.name),
            ComponentName(context.packageName, secondary.name),
            null
        )

        val splitAttributes = SplitAttributes.Builder()
            .setSplitType(SplitAttributes.SplitType.ratio(0.5f))
            .setLayoutDirection(SplitAttributes.LayoutDirection.LEFT_TO_RIGHT)
            .build()

        return SplitPairRule.Builder(setOf(filter))
            .setDefaultSplitAttributes(splitAttributes)
            .setMinWidthDp(600)
            .setMinSmallestWidthDp(0)
            .setFinishPrimaryWithSecondary(SplitRule.FinishBehavior.NEVER)
            .setFinishSecondaryWithPrimary(SplitRule.FinishBehavior.ALWAYS)
            .setClearTop(false)
            .build()
    }
}
