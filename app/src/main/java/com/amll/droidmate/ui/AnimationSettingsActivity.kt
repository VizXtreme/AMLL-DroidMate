package com.amll.droidmate.ui

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amll.droidmate.ui.base.BaseComposeActivity
import com.amll.droidmate.ui.theme.DynamicThemeManager
import java.io.File
import java.io.IOException

/**
 * 有相当一部分设置未完工！这些设置已被注释。
 */




/**
 * 歌词播放器实现类型枚举
 */
enum class LyricPlayerImplementation(val value: String, val displayName: String) {
    DOM("dom", "DOM"),
    CANVAS("canvas", "Canvas")
}

/**
 * 歌词字体大小预设枚举
 */
enum class LyricSizePreset(val value: String, val displayName: String) {
    TINY("tiny", "超小"),
    EXTRA_SMALL("extra-small", "极小"),
    SMALL("small", "小"),
    MEDIUM("medium", "中"),
    LARGE("large", "大"),
    EXTRA_LARGE("extra-large", "极大"),
    HUGE("huge", "超大")
}

/**
 * 背景渲染器类型枚举
 */
enum class BackgroundRenderer(val value: String, val displayName: String) {
    MESH("mesh", "网格渐变渲染器"),
    PIXIJ("pixi", "PixiJS 渲染器"),
    CSS_BG("css-bg", "CSS 背景")
}

/**
 * 动画效果设置界面
 * 
 * 这个 Activity 允许用户自定义 AMLL 歌词渲染的动画效果。
 * 用户可以调整以下参数：
 * - 弹簧动画（Spring）：启用/禁用弹性效果
 * - 缩放动画（Scale）：启用/禁用歌词缩放
 * - 模糊效果（Blur）：启用/禁用运动模糊
 * - 隐藏已唱行：是否隐藏已播放的歌词行
 * - 逐字渐变宽度：控制逐字歌词的淡入淡出范围
 * - 动画帧率（FPS）：限制最高帧率以节省电量
 * 
 * **性能提示**：
 * - 低端设备建议关闭部分特效（如模糊、弹簧）
 * - 省电模式下可以降低 FPS 到 30-45
 * - 高刷新率设备可以设置为 90-120 FPS 获得更流畅体验
 * - 最高支持 240 FPS，但会增加耗电量
 */
class AnimationSettingsActivity : BaseComposeActivity() {
    @Composable
    override fun renderContent() {
        // 渲染动画设置页面
        AnimationSettingsPage(onBack = { finish() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimationSettingsPage(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val dynamicColorScheme by DynamicThemeManager.observeColorScheme()
    val rippleColor = dynamicColorScheme?.primary ?: MaterialTheme.colorScheme.primary

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = rippleColor,
        checkedTrackColor = rippleColor.copy(alpha = 0.5f),
        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
        uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.24f)
    )

    // === 字体设置 ===
    val loadExistingFonts = {
        val all = AppSettings.getAmllFontFiles(context)
        val corrected = all.filter { File(it.absolutePath).exists() }.map { file ->
            val actualName = readFontFamilyName(File(file.absolutePath))
            if (!actualName.isNullOrBlank() && actualName != file.displayName) {
                AppSettings.upsertAmllFontFile(context, file.absolutePath, actualName)
                file.copy(displayName = actualName)
            } else {
                file
            }
        }
        if (corrected.size != all.size) {
            AppSettings.setAmllFontFiles(context, corrected)
        }
        corrected
    }

    var amllFontFamily by remember { mutableStateOf(AppSettings.getAmllFontFamily(context)) }
    var importedFonts by remember { mutableStateOf(loadExistingFonts()) }
    var enabledFontIds by remember { mutableStateOf(AppSettings.getEnabledAmllFontFileIds(context).toSet()) }
    var fontStatusMessage by remember { mutableStateOf<String?>(null) }
    
    // === 歌词字体高级设置 ===
    var fontWeight by remember { mutableStateOf(AppSettings.getAmllFontWeight(context)) }
    var letterSpacing by remember { mutableStateOf(AppSettings.getAmllLetterSpacing(context)) }
    var previewText by remember { mutableStateOf("字体预览 Font Preview") }

    val importFontLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult

        try {
            var importedCount = 0
            val newEnabled = enabledFontIds.toMutableSet()

            uris.forEach { uri ->
                val result = importFontToInternalStorage(context, uri)
                val updated = AppSettings.upsertAmllFontFile(
                    context = context,
                    absolutePath = result.absolutePath,
                    displayName = result.displayName
                )
                updated.firstOrNull { it.absolutePath == result.absolutePath }?.id?.let { newEnabled.add(it) }
                importedCount += 1
            }

            importedFonts = loadExistingFonts()
            enabledFontIds = newEnabled
            AppSettings.setEnabledAmllFontFileIds(context, newEnabled.toList())
            fontStatusMessage = "已导入 $importedCount 个字体文件"
        } catch (e: Exception) {
            fontStatusMessage = "导入失败：${e.message ?: "未知错误"}"
        }
    }

    // === 动画效果设置（原有） ===
    var enableSpring by remember { mutableStateOf(AppSettings.isAmllAnimationSpringEnabled(context)) }
    var enableScale by remember { mutableStateOf(AppSettings.isAmllAnimationScaleEnabled(context)) }
    var enableBlur by remember { mutableStateOf(AppSettings.isAmllAnimationBlurEnabled(context)) }
    var hidePassedLines by remember { mutableStateOf(AppSettings.isAmllAnimationHidePassedLinesEnabled(context)) }
    var wordFadeWidth by remember { mutableStateOf(AppSettings.getAmllAnimationWordFadeWidth(context)) }
    var fps by remember { mutableStateOf(AppSettings.getAmllAnimationFps(context)) }










    // === 歌词样式设置（新增） ===
    var lyricPlayerImpl by remember { mutableStateOf(AppSettings.getAmllLyricPlayerImplementation(context)) }
    var lyricSizePreset by remember { mutableStateOf(AppSettings.getAmllLyricSizePreset(context)) }
    var enableTranslationLine by remember { mutableStateOf(AppSettings.isAmllTranslationLineEnabled(context)) }
    var enableRomanLine by remember { mutableStateOf(AppSettings.isAmllRomanLineEnabled(context)) }
    var enableSwapTransRoman by remember { mutableStateOf(AppSettings.isAmllSwapTransRomanLineEnabled(context)) }
    var enableAdvanceDynamicTime by remember { mutableStateOf(AppSettings.isAmllAdvanceDynamicLyricTimeEnabled(context)) }
    var playerImplExpanded by remember { mutableStateOf(false) }
    var sizePresetExpanded by remember { mutableStateOf(false) }

    // === 歌词背景设置（新增） ===
    var backgroundRenderer by remember { mutableStateOf(AppSettings.getAmllBackgroundRenderer(context)) }
    var cssBackgroundProperty by remember { mutableStateOf(AppSettings.getAmllCssBackgroundProperty(context)) }
    var backgroundFps by remember { mutableStateOf(AppSettings.getAmllBackgroundFps(context)) }
    var backgroundRenderScale by remember { mutableStateOf(AppSettings.getAmllBackgroundRenderScale(context)) }
    var enableBackgroundStaticMode by remember { mutableStateOf(AppSettings.isAmllBackgroundStaticModeEnabled(context)) }
    var rendererExpanded by remember { mutableStateOf(false) }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    androidx.compose.material3.Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("AMLL 歌词组件设置") },
                navigationIcon = {
                    androidx.compose.material3.FilledIconButton(
                        onClick = onBack,
                        colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "未完工的选项暂被隐去。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // ========== Card 1: 字体管理 ==========
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "字体管理",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    OutlinedTextField(
                        value = amllFontFamily,
                        onValueChange = { amllFontFamily = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("font-family") },
                        placeholder = { Text("默认：${AppSettings.getDefaultAmllFontFamily()}") }
                    )
                    Text(
                        text = "以逗号分隔的字体名称组合，等同于 CSS 的 font-family 属性，留空为默认",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    // 字体字重设置
                    var fontWeightInput by remember { mutableStateOf(fontWeight.toString()) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.6f)) {
                            Text("字体字重", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "等同于 CSS 的 font-weight 属性，0 为系统控制，推荐值 600（默认：0）",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        OutlinedTextField(
                            value = fontWeightInput,
                            onValueChange = { value ->
                                fontWeightInput = value
                                value.toIntOrNull()?.let {
                                    fontWeight = it.coerceIn(0, 1000)
                                    AppSettings.setAmllFontWeight(context, fontWeight)
                                }
                            },
                            placeholder = { Text("0-1000") },
                            modifier = Modifier.fillMaxWidth(0.35f)
                        )
                    }

                    // 字符间距设置
                    OutlinedTextField(
                        value = letterSpacing,
                        onValueChange = { letterSpacing = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("字符间距") },
                        placeholder = { Text("默认：留空（2px, 0.1em 等 CSS 单位）") }
                    )
                    Text(
                        text = "等同于 CSS 的 letter-spacing 属性，留空为默认",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    // 字体预览
                    OutlinedTextField(
                        value = previewText,
                        onValueChange = { previewText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("字体预览文本") },
                        placeholder = { Text("输入要预览的文字") }
                    )
                    
                    // 实时预览区域
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = previewText,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = if (amllFontFamily.isNotBlank()) {
                                    val fonts = amllFontFamily.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                    if (fonts.isNotEmpty()) {
                                        android.graphics.Typeface.create(fonts.first(), android.graphics.Typeface.NORMAL)?.let {
                                            FontFamily(it)
                                        }
                                    } else null
                                } else null,
                                fontWeight = if (fontWeight > 0) FontWeight(fontWeight) else null,
                                letterSpacing = (letterSpacing.toFloatOrNull()?.coerceIn(-10f, 10f) ?: 0f).sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = previewText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                fontFamily = if (amllFontFamily.isNotBlank()) {
                                    val fonts = amllFontFamily.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                    if (fonts.isNotEmpty()) {
                                        android.graphics.Typeface.create(fonts.first(), android.graphics.Typeface.NORMAL)?.let {
                                            FontFamily(it)
                                        }
                                    } else null
                                } else null,
                                fontWeight = if (fontWeight > 0) FontWeight(fontWeight) else null,
                                letterSpacing = (letterSpacing.toFloatOrNull()?.coerceIn(-10f, 10f) ?: 0f).sp
                            )
                        }
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                AppSettings.setAmllFontFamily(context, amllFontFamily)
                                fontStatusMessage = "font-family 设置已保存"
                            }
                        ) {
                            Text("保存 font-family")
                        }

                        OutlinedButton(
                            onClick = {
                                importFontLauncher.launch(arrayOf("font/*"))
                            }
                        ) {
                            Text("导入字体文件")
                        }

                        OutlinedButton(
                            onClick = {
                                AppSettings.resetAmllFontSettings(context)
                                amllFontFamily = AppSettings.getDefaultAmllFontFamily()
                                enabledFontIds = emptySet()
                                fontStatusMessage = "已还原为默认 font-family 设置"
                            }
                        ) {
                            Text("还原 font-family")
                        }
                    }

                    val sortedFonts = importedFonts.sortedBy { it.fontFamilyName.lowercase() }
                    if (sortedFonts.isEmpty()) {
                        Text(
                            text = "未导入字体文件",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    } else {
                        Text(
                            text = "已导入的字体",
                            style = MaterialTheme.typography.titleMedium
                        )
                        sortedFonts.forEach { font ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.65f)
                                        .heightIn(min = 48.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = font.displayName,
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    TextButton(
                                        onClick = {
                                            File(font.absolutePath).takeIf { it.exists() }?.delete()
                                            importedFonts = AppSettings.removeAmllFontFile(context, font.id)
                                            val next = enabledFontIds.toMutableSet().apply { remove(font.id) }
                                            enabledFontIds = next
                                            AppSettings.setEnabledAmllFontFileIds(context, next.toList())
                                            fontStatusMessage = "已删除字体：${font.displayName}"
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "删除",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }

                                    SwitchWithIcon(
                                        checked = enabledFontIds.contains(font.id),
                                        onCheckedChange = { enabled ->
                                            val next = enabledFontIds.toMutableSet()
                                            if (enabled) {
                                                next.add(font.id)
                                            } else {
                                                next.remove(font.id)
                                            }
                                            enabledFontIds = next
                                            AppSettings.setEnabledAmllFontFileIds(context, next.toList())
                                            fontStatusMessage = if (enabled) {
                                                "已启用字体：${font.displayName}"
                                            } else {
                                                "已停用字体：${font.displayName}"
                                            }
                                        },
                                        colors = switchColors
                                    )
                                }
                            }
                        }
                    }

                    if (!fontStatusMessage.isNullOrBlank()) {
                        Text(
                            text = fontStatusMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // ========== Card 2: 动画效果 ==========
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "动画效果",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newState = !enableSpring
                                enableSpring = newState
                                AppSettings.setAmllAnimationSpringEnabled(context, newState)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("启用弹性动画", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "允许歌词行按弹性曲线移动，提升滚动过渡效果。（默认：开启）",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        SwitchWithIcon(
                            checked = enableSpring,
                            onCheckedChange = { enabled ->
                                enableSpring = enabled
                                AppSettings.setAmllAnimationSpringEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newState = !enableScale
                                enableScale = newState
                                AppSettings.setAmllAnimationScaleEnabled(context, newState)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("启用缩放动画", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "开启后歌词当前行会有轻微缩放效果。（默认：开启）",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        SwitchWithIcon(
                            checked = enableScale,
                            onCheckedChange = { enabled ->
                                enableScale = enabled
                                AppSettings.setAmllAnimationScaleEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newState = !enableBlur
                                enableBlur = newState
                                AppSettings.setAmllAnimationBlurEnabled(context, newState)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("启用模糊过渡", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "开启后歌词行过渡时会出现模糊渐变效果。（默认：开启）",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        SwitchWithIcon(
                            checked = enableBlur,
                            onCheckedChange = { enabled ->
                                enableBlur = enabled
                                AppSettings.setAmllAnimationBlurEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newState = !hidePassedLines
                                hidePassedLines = newState
                                AppSettings.setAmllAnimationHidePassedLinesEnabled(context, newState)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("隐藏已过歌词", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "已播放的歌词行将自动淡出并隐藏。（默认：关闭）",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        SwitchWithIcon(
                            checked = hidePassedLines,
                            onCheckedChange = { enabled ->
                                hidePassedLines = enabled
                                AppSettings.setAmllAnimationHidePassedLinesEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }

                    Column {
                        var fpsInput by remember { mutableStateOf(fps.toString()) }
                        Text(
                            text = "动画帧率：${fps} FPS",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = fpsInput,
                            onValueChange = { value ->
                                fpsInput = value
                                value.toIntOrNull()?.let {
                                    fps = it.coerceIn(1, 1000)
                                    AppSettings.setAmllAnimationFps(context, fps)
                                }
                            },
                            placeholder = { Text("15-240") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "调节动画帧率以节省电量，建议范围：15-240（默认：60）",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Column {
                        var wordFadeWidthInput by remember { mutableStateOf(wordFadeWidth.toString()) }
                        Text(
                            text = "文字渐变宽度：${"%.2f".format(wordFadeWidth)}",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = wordFadeWidthInput,
                            onValueChange = { value ->
                                wordFadeWidthInput = value
                                value.toFloatOrNull()?.let {
                                    wordFadeWidth = it.coerceIn(0.0f, 2.0f)
                                    AppSettings.setAmllAnimationWordFadeWidth(context, wordFadeWidth)
                                }
                            },
                            placeholder = { Text("0.0-2.0") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "调节逐字歌词的淡入淡出范围，建议范围：0.0-2.0（默认：0.5）",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }


                }
            }

            // ========== Card 2: 歌词样式 ==========
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "歌词样式",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // 字体设置入口（在动画设置页面内直接管理）
                    Text(
                        text = "字体相关设置请参见上方「字体管理」部分",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    // 歌词播放器实现
                    ExposedDropdownMenuBox(
                        expanded = playerImplExpanded,
                        onExpandedChange = { playerImplExpanded = !playerImplExpanded }
                    ) {
                        OutlinedTextField(
                            value = LyricPlayerImplementation.entries.find { it.value == lyricPlayerImpl }?.displayName ?: "DOM",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("歌词播放器实现（默认：DOM）") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = playerImplExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = playerImplExpanded,
                            onDismissRequest = { playerImplExpanded = false }
                        ) {
                            LyricPlayerImplementation.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.displayName) },
                                    onClick = {
                                        lyricPlayerImpl = option.value
                                        AppSettings.setAmllLyricPlayerImplementation(context, option.value)
                                        playerImplExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Text(
                        text = "目前有两个歌词播放实现：\n• DOM：使用 DOM 元素实现，效果最全但性能开销大\n• Canvas：使用 Canvas 实现，性能优异但部分细节效果不足",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    // 歌词字体大小
                    ExposedDropdownMenuBox(
                        expanded = sizePresetExpanded,
                        onExpandedChange = { sizePresetExpanded = !sizePresetExpanded }
                    ) {
                        OutlinedTextField(
                            value = LyricSizePreset.entries.find { it.value == lyricSizePreset }?.displayName ?: "中",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("歌词字体大小（默认：中）") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sizePresetExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = sizePresetExpanded,
                            onDismissRequest = { sizePresetExpanded = false }
                        ) {
                            LyricSizePreset.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.displayName) },
                                    onClick = {
                                        lyricSizePreset = option.value
                                        AppSettings.setAmllLyricSizePreset(context, option.value)
                                        sizePresetExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 启用翻译歌词
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newState = !enableTranslationLine
                                enableTranslationLine = newState
                                AppSettings.setAmllTranslationLineEnabled(context, newState)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("显示翻译歌词", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "在歌词下方显示翻译文本。（默认：开启）",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        SwitchWithIcon(
                            checked = enableTranslationLine,
                            onCheckedChange = { enabled ->
                                enableTranslationLine = enabled
                                AppSettings.setAmllTranslationLineEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }

                    // 启用音译歌词
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newState = !enableRomanLine
                                enableRomanLine = newState
                                AppSettings.setAmllRomanLineEnabled(context, newState)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("显示音译歌词", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "在歌词下方显示音译文本（如拼音）。（默认：开启）",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        SwitchWithIcon(
                            checked = enableRomanLine,
                            onCheckedChange = { enabled ->
                                enableRomanLine = enabled
                                AppSettings.setAmllRomanLineEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }

                    // 交换音译和翻译位置
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newState = !enableSwapTransRoman
                                enableSwapTransRoman = newState
                                AppSettings.setAmllSwapTransRomanLineEnabled(context, newState)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("交换音译和翻译位置", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "交换音译和翻译歌词的显示顺序。（默认：关闭）",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        SwitchWithIcon(
                            checked = enableSwapTransRoman,
                            onCheckedChange = { enabled ->
                                enableSwapTransRoman = enabled
                                AppSettings.setAmllSwapTransRomanLineEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }

                    // 提前歌词行时序
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newState = !enableAdvanceDynamicTime
                                enableAdvanceDynamicTime = newState
                                AppSettings.setAmllAdvanceDynamicLyricTimeEnabled(context, newState)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("提前歌词行时序", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "让歌词行初始时间提前，更接近 Apple Music 效果。可能导致歌词行末尾未播完就切换。（默认：关闭）",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        SwitchWithIcon(
                            checked = enableAdvanceDynamicTime,
                            onCheckedChange = { enabled ->
                                enableAdvanceDynamicTime = enabled
                                AppSettings.setAmllAdvanceDynamicLyricTimeEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }
                }
            }

            // ========== Card 3: 歌词背景 ==========
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "歌词背景",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // 背景渲染器选择
                    ExposedDropdownMenuBox(
                        expanded = rendererExpanded,
                        onExpandedChange = { rendererExpanded = !rendererExpanded }
                    ) {
                        OutlinedTextField(
                            value = BackgroundRenderer.entries.find { it.value == backgroundRenderer }?.displayName ?: "网格渐变渲染器",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("背景渲染器（默认：网格渐变渲染器）") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rendererExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = rendererExpanded,
                            onDismissRequest = { rendererExpanded = false }
                        ) {
                            BackgroundRenderer.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.displayName) },
                                    onClick = {
                                        backgroundRenderer = option.value
                                        AppSettings.setAmllBackgroundRenderer(context, option.value)
                                        rendererExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 条件渲染：CSS 背景模式
                    if (backgroundRenderer == "css-bg") {
                        OutlinedTextField(
                            value = cssBackgroundProperty,
                            onValueChange = { value ->
                                cssBackgroundProperty = value
                                AppSettings.setAmllCssBackgroundProperty(context, value)
                            },
                            label = { Text("CSS 背景属性值") },
                            placeholder = { Text("#111111") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "等同于 CSS 的 background 属性值，默认为 #111111。（默认：#111111）",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    } else {
                        // 非 CSS 背景模式的设置项
                        Column {
                            var backgroundFpsInput by remember { mutableStateOf(backgroundFps.toString()) }
                            Text(
                                text = "背景最高帧率：${backgroundFps} FPS",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            OutlinedTextField(
                                value = backgroundFpsInput,
                                onValueChange = { value ->
                                    backgroundFpsInput = value
                                    value.toIntOrNull()?.let {
                                        backgroundFps = it.coerceIn(1, 1000)
                                        AppSettings.setAmllBackgroundFps(context, backgroundFps)
                                    }
                                },
                                placeholder = { Text("1-1000") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "对性能影响较高，默认值为 60。（默认：60）",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        Column {
                            var backgroundRenderScaleInput by remember { mutableStateOf(backgroundRenderScale.toString()) }
                            Text(
                                text = "背景渲染倍率：${"%.2f".format(backgroundRenderScale)}",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            OutlinedTextField(
                                value = backgroundRenderScaleInput,
                                onValueChange = { value ->
                                    backgroundRenderScaleInput = value
                                    value.toFloatOrNull()?.let {
                                        backgroundRenderScale = it.coerceIn(0.01f, 10.0f)
                                        AppSettings.setAmllBackgroundRenderScale(context, backgroundRenderScale)
                                    }
                                },
                                placeholder = { Text("0.01-10.0") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "对性能影响较高，默认值为 1（每像素点渲染）。（默认：1.0）",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val newState = !enableBackgroundStaticMode
                                    enableBackgroundStaticMode = newState
                                    AppSettings.setAmllBackgroundStaticModeEnabled(context, newState)
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                                Text("背景静态模式", color = MaterialTheme.colorScheme.onSurface)
                                Text(
                                    text = "让背景保持静止（切换歌曲除外），优化性能。启用后背景跳动效果失效。（默认：关闭）",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            SwitchWithIcon(
                                checked = enableBackgroundStaticMode,
                                onCheckedChange = { enabled ->
                                    enableBackgroundStaticMode = enabled
                                    AppSettings.setAmllBackgroundStaticModeEnabled(context, enabled)
                                },
                                colors = switchColors
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Attempts to parse the "name" table of a TrueType/OpenType font file to
 * extract the font family name (nameID = 1). This is the human-visible font
 * name such as "微软雅黑". Returns null if parsing fails.
 */
private fun readFontFamilyName(file: File): String? {
    try {
        java.io.RandomAccessFile(file, "r").use { raf ->
            // skip sfnt version
            raf.readInt()
            val numTables = raf.readUnsignedShort()
            raf.skipBytes(6) // searchRange, entrySelector, rangeShift
            for (i in 0 until numTables) {
                val tag = raf.readInt()
                val _checkSum = raf.readInt()
                val offset = raf.readInt()
                val _length = raf.readInt()
                // 'name' table tag
                if (tag == 0x6E616D65) {
                    raf.seek(offset.toLong())
                    val _format = raf.readUnsignedShort()
                    val count = raf.readUnsignedShort()
                    val stringOffset = raf.readUnsignedShort()
                    for (j in 0 until count) {
                        val platformID = raf.readUnsignedShort()
                        val _encodingID = raf.readUnsignedShort()
                        val _languageID = raf.readUnsignedShort()
                        val nameID = raf.readUnsignedShort()
                        val lengthEntry = raf.readUnsignedShort()
                        val offsetEntry = raf.readUnsignedShort()
                        if (nameID == 1) { // Font Family name
                            val pos = offset.toLong() + stringOffset + offsetEntry
                            raf.seek(pos)
                            val bytes = ByteArray(lengthEntry)
                            raf.readFully(bytes)
                            val encoding = when (platformID) {
                                0, 3 -> Charsets.UTF_16BE
                                else -> Charsets.ISO_8859_1
                            }
                            return try {
                                String(bytes, encoding)
                            } catch (_: Exception) {
                                null
                            }
                        }
                    }
                    break
                }
            }
        }
    } catch (_: Exception) {
    }
    return null
}

@Throws(IOException::class)
private fun importFontToInternalStorage(context: android.content.Context, sourceUri: Uri): AppSettings.AmllFontFile {
    val resolver = context.contentResolver
    val rawName = queryDisplayName(context, sourceUri) ?: "custom_font_${System.currentTimeMillis()}.ttf"
    val safeName = rawName.replace(Regex("[^A-Za-z0-9._-]"), "_")

    val fontDir = File(context.filesDir, "amll_fonts")
    if (!fontDir.exists()) {
        fontDir.mkdirs()
    }

    val outFile = File(fontDir, "${System.currentTimeMillis()}_$safeName")
    resolver.openInputStream(sourceUri).use { input ->
        if (input == null) throw IOException("无法打开字体文件")
        outFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    // try to read internal family name, fallback to original name
    val internalName = readFontFamilyName(outFile)
    val displayName = internalName ?: rawName

    return AppSettings.AmllFontFile(
        id = "font_" + outFile.absolutePath.hashCode().toUInt().toString(16),
        displayName = displayName,
        absolutePath = outFile.absolutePath,
        fontFamilyName = internalName ?: rawName.substringBeforeLast('.').ifBlank { "Imported Font" }
    )
}

private fun queryDisplayName(context: android.content.Context, uri: Uri): String? {
    val resolver = context.contentResolver
    resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            return cursor.getString(nameIndex)
        }
    }
    return null
}