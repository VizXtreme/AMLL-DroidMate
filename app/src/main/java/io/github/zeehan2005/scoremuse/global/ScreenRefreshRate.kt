package io.github.zeehan2005.scoremuse.global

import android.content.Context
import android.view.WindowManager
import timber.log.Timber

/**
 * 屏幕刷新率检测工具
 *
 * 获取当前设备的屏幕刷新率，并计算对应的帧间隔（毫秒）。
 * 用于指导轮询间隔和时间更新频率，使其与屏幕刷新率同步。
 */
object ScreenRefreshRate {

    /**
     * 获取当前屏幕刷新率（Hz）
     *
     * 通过 [WindowManager.defaultDisplay.refreshRate] 获取。
     * 如果无法获取，默认返回 60f（最常见值）。
     *
     * @param context Android Context（用于获取 WindowManager 系统服务）
     * @return 屏幕刷新率（Hz），不小于 1f
     */
    fun getRefreshRate(context: Context): Float {
        return try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            if (windowManager != null) {
                val rate = windowManager.defaultDisplay.refreshRate
                Timber.d("[ScreenRefreshRate] Detected refresh rate: ${rate}Hz")
                rate.coerceAtLeast(1f)
            } else {
                Timber.d("[ScreenRefreshRate] WindowManager not available, defaulting to 60Hz")
                60f
            }
        } catch (e: Exception) {
            Timber.e("[ScreenRefreshRate] Failed to detect refresh rate $e")
            60f
        }
    }

    /**
     * 获取帧间隔时间（毫秒），即每帧的时长
     *
     * 计算公式：1000 / refreshRate
     *
     * 60Hz   → 16ms
     * 90Hz   → 11ms
     * 120Hz  → 8ms
     *
     * @param context Android Context
     * @return 帧间隔毫秒数，最小为 1ms
     */
    fun getFrameIntervalMs(context: Context): Long {
        val rate = getRefreshRate(context)
        val interval = (1000f / rate).toLong().coerceAtLeast(1L)
        Timber.d("[ScreenRefreshRate] Frame interval: ${interval}ms (${rate}Hz)")
        return interval
    }
}
