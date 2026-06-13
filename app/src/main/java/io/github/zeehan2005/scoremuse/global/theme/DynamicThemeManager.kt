package io.github.zeehan2005.scoremuse.global.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import timber.log.Timber

/**
 * 全局动态主题管理器
 * 
 * 这个对象负责管理从专辑封面提取的动态颜色方案，实现"音乐变色龙"效果。
 */
object DynamicThemeManager {
    /** 内部状态：存储当前的动态颜色方案 (Material 3 ColorScheme) */
    private val _dynamicColorScheme = mutableStateOf<ColorScheme?>(null)

    /**
     * 更新全局动态颜色方案
     * 
     * @param scheme 新的颜色方案 (ColorScheme)，传入 null 表示清除动态配色
     */
    fun updateColorScheme(scheme: ColorScheme?) {
        if (_dynamicColorScheme.value != scheme) {
            _dynamicColorScheme.value = scheme
            if (scheme != null) {
                Timber.i("[DynamicThemeManager] Dynamic ColorScheme updated globally")
            } else {
                Timber.i("[DynamicThemeManager] Dynamic ColorScheme cleared")
            }
        }
    }
    
    /**
     * 清除动态颜色方案，恢复默认主题
     */
    fun clearColorScheme() {
        updateColorScheme(null)
    }
    
    /**
     * 在 Composable 中观察动态颜色方案的变化
     * 
     * @return 包含当前 ColorScheme 的 State 对象
     */
    @Composable
    fun observeColorScheme(): State<ColorScheme?> {
        return _dynamicColorScheme
    }
}
