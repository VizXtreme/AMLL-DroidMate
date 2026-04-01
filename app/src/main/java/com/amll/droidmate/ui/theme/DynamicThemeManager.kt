package com.amll.droidmate.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import timber.log.Timber

/**
 * 全局动态主题管理器
 * 
 * 这个对象负责管理从专辑封面提取的动态颜色方案，实现"音乐变色龙"效果。
 * 当用户播放不同的歌曲时，应用界面会自动采用与专辑封面相匹配的配色方案，
 * 提供沉浸式的视觉体验。
 * 
 * 工作原理：
 * 1. MainActivity 监听播放状态变化
 * 2. 当新歌播放时，提取专辑封面的主色调
 * 3. 调用 updateColorScheme 更新全局颜色方案
 * 4. 所有 Composable 组件通过 observeColorScheme 自动响应变化
 * 5. UI 整体切换到新的配色（包括背景、文字、按钮等）
 */
object DynamicThemeManager {
    // 内部状态：存储当前的动态颜色方案
    private val _dynamicColorScheme = mutableStateOf<DynamicColorScheme?>(null)
    
    /**
     * 获取当前的动态颜色方案
     * 
     * @return 当前的 DynamicColorScheme，如果没有则返回 null
     */
    val dynamicColorScheme: DynamicColorScheme?
        get() = _dynamicColorScheme.value
    
    /**
     * 更新全局动态颜色方案
     * 
     * 这个方法会被 MainActivity 调用，当检测到新的专辑封面时，
     * 提取的颜色方案会通过这里广播到整个应用的 UI。
     * 
     * @param scheme 新的颜色方案，传入 null 表示清除动态配色，恢复默认主题
     */
    fun updateColorScheme(scheme: DynamicColorScheme?) {
        if (_dynamicColorScheme.value != scheme) {
            _dynamicColorScheme.value = scheme
            if (scheme != null) {
                Timber.i("[DynamicThemeManager] Dynamic color scheme updated globally")  // 记录日志
            } else {
                Timber.i("[DynamicThemeManager] Dynamic color scheme cleared, using default theme")  // 恢复默认
            }
        }
    }
    
    /**
     * 清除动态颜色方案，恢复默认主题
     * 
     * 当没有播放音乐或用户手动关闭动态配色时调用
     */
    fun clearColorScheme() {
        updateColorScheme(null)
    }
    
    /**
     * 在 Composable 中观察动态颜色方案的变化
     * 
     * 这是 Compose 的响应式 API，当颜色方案改变时，
     * 使用该函数的 Composable 会自动重组并应用新颜色。
     * 
     * @return 包含当前颜色方案的 State 对象
     */
    @Composable
    fun observeColorScheme(): State<DynamicColorScheme?> {
        return _dynamicColorScheme
    }
}
