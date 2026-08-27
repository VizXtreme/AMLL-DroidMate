package io.github.zeehan2005.scoremuse.ui.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import io.github.zeehan2005.scoremuse.service.LyricNotificationManager
import io.github.zeehan2005.scoremuse.global.AppSettings
import io.github.zeehan2005.scoremuse.ui.settings.ApiTestActivity
import io.github.zeehan2005.scoremuse.global.CardClickAction
import io.github.zeehan2005.scoremuse.global.UpdateChannel
import io.github.zeehan2005.scoremuse.ui.components.SwitchWithIcon
import dev.amll.droidmate.ui.settings.ComponentSettings
import io.github.zeehan2005.scoremuse.global.theme.DynamicThemeManager
import io.github.zeehan2005.scoremuse.global.theme.ScoreMuseTheme
import io.github.zeehan2005.scoremuse.components.GitHubUpdateChecker
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.jvm.java

/**
 * 应用主设置界面
 *
 * 这个 Activity 提供了应用的所有主要设置选项，包括：
 * - 歌词通知设置（启用/禁用、打开系统通知设置）
 * - 卡片点击行为配置（直接打开/询问/禁用）
 * - 自动更新检查（启用/禁用、更新渠道）
 * - 跳过上一首回退功能
 * - 元数据处理开关
 * - Agent 识别器开关
 * - 权限管理（通知监听权限）
 *
 * **设计理念**：
 * - 分组展示：相关设置放在同一卡片中
 * - 即时生效：修改后立即应用到 AppSettings
 * - 状态同步：UI 状态与持久化存储保持一致
 * - 权限引导：未授权时显示说明对话框
 */
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 启用边缘到边缘显示
        enableEdgeToEdge()
        // 设置系统栏不遮挡内容
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
            val dynamicColorScheme by DynamicThemeManager.observeColorScheme()
            
            ScoreMuseTheme(
                darkTheme = isDarkTheme,
                dynamicColorScheme = dynamicColorScheme
            ) {
                SettingsPage(
                    onBack = { finish() },
                    onOpenNotificationSettings = {
                        startActivity(Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsPage(
    onBack: () -> Unit,
    onOpenNotificationSettings: () -> Unit
) {
    val context = LocalContext.current

    /** use the global dynamic color scheme as the source of truth for accent */
    val dynamicColorScheme by DynamicThemeManager.observeColorScheme()
    val rippleColor = dynamicColorScheme?.primary ?: MaterialTheme.colorScheme.primary

    /** keep switch thumb/track consistent with the dynamic accent color */
    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = rippleColor,
        checkedTrackColor = rippleColor.copy(alpha = 0.5f),
        checkedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
        uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.24f)
    )

    /**
     * we no longer need MainViewModel or album art extraction here; that work
     * is already performed in MainActivity and pushed to DynamicThemeManager
     */

    var selectedAction by remember { mutableStateOf(AppSettings.getCardClickAction(context)) }
    var lyricNotificationEnabled by remember {
        mutableStateOf(
            AppSettings.isLyricNotificationEnabled(
                context
            )
        )
    }
    var autoCheckEnabled by remember { mutableStateOf(AppSettings.isAutoUpdateCheckEnabled(context)) }
    var updateChannel by remember { mutableStateOf(AppSettings.getUpdateChannel(context)) }
    var skipPreviousRewinds by remember {
        mutableStateOf(
            AppSettings.isSkipPreviousRewindsEnabled(
                context
            )
        )
    }
    var songStructureBarEnabled by remember {
        mutableStateOf(
            AppSettings.isSongStructureBarEnabled(
                context
            )
        )
    }
    var updateDialogTitle by remember { mutableStateOf("") }
    var updateDialogMessage by remember { mutableStateOf("") }
    var updateDialogUrl by remember { mutableStateOf<String?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var checkingUpdate by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        lyricNotificationEnabled = granted
        AppSettings.setLyricNotificationEnabled(context, granted)
        if (!granted) {
            LyricNotificationManager(context).cancel()
        }
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    FilledIconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
            SettingsHeader("Ongoing Notification Lyrics")

            SettingsToggleItem(
                title = "Ongoing Notification Lyrics",
                description = "Notification permission is required. Can be shown on the lock screen after acquiring lock screen permission.",
                checked = lyricNotificationEnabled,
                onCheckedChange = { enabled ->
                    if (!enabled) {
                        lyricNotificationEnabled = false
                        AppSettings.setLyricNotificationEnabled(context, false)
                        LyricNotificationManager(context).cancel()
                    } else {
                        if (needsNotificationPermission() && !hasNotificationPermission(context)) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            lyricNotificationEnabled = true
                            AppSettings.setLyricNotificationEnabled(context, true)
                        }
                    }
                },
                switchColors = switchColors
            )

            SettingsHeader("Song Structure Bar")

            SettingsToggleItem(
                title = "Song Structure Bar",
                checked = songStructureBarEnabled,
                onCheckedChange = { enabled ->
                    songStructureBarEnabled = enabled
                    AppSettings.setSongStructureBarEnabled(context, enabled)
                },
                switchColors = switchColors
            )

            SettingsHeader("Lyric Component Settings")

            SettingsNavigateItem(
                title = "Lyric Component Settings",
                onClick = {
                    context.startActivity(Intent(context, ComponentSettings::class.java))
                }
            )

            SettingsHeader("\"Now Playing\" Card Click Action")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CardClickActionOption(
                        title = "Directly open source app",
                        selected = selectedAction == CardClickAction.DIRECT_OPEN,
                        onClick = {
                            selectedAction = CardClickAction.DIRECT_OPEN
                            AppSettings.setCardClickAction(context, CardClickAction.DIRECT_OPEN)
                        }
                    )
                    CardClickActionOption(
                        title = "Ask",
                        selected = selectedAction == CardClickAction.ASK,
                        onClick = {
                            selectedAction = CardClickAction.ASK
                            AppSettings.setCardClickAction(context, CardClickAction.ASK)
                        }
                    )
                    CardClickActionOption(
                        title = "Do nothing",
                        selected = selectedAction == CardClickAction.NONE,
                        onClick = {
                            selectedAction = CardClickAction.NONE
                            AppSettings.setCardClickAction(context, CardClickAction.NONE)
                        }
                    )
                }
            }

            SettingsHeader("Accessibility")

            SettingsToggleItem(
                title = "Click Previous to go to 0:00",
                description = "Clicking the previous button will first go back to the beginning of the song, rather than directly jumping to the previous one.",
                checked = skipPreviousRewinds,
                onCheckedChange = { enabled ->
                    skipPreviousRewinds = enabled
                    AppSettings.setSkipPreviousRewindsEnabled(context, enabled)
                },
                switchColors = switchColors
            )



//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable {
//                        val newState = !processMetadataEnabled
//                        processMetadataEnabled = newState
//                        AppSettings.setMetadataProcessingEnabled(context, newState)
//                    },
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(12.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column(modifier = Modifier.fillMaxWidth(0.75f)) {
//                        Text("处理元数据（实验性）（当前版本请勿开启）", color = MaterialTheme.colorScheme.onSurface)
//                        Text(
//                            text = "尝试自动移除歌词中的\"词:\", \"曲:\", \"编曲:\"等行，可能会误删。\n由于当前的处理实现会破坏结构，请勿开启。",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//                        )
//                    }
//                    SwitchWithIcon(
//                        checked = processMetadataEnabled,
//                        onCheckedChange = { enabled ->
//                            processMetadataEnabled = enabled
//                            AppSettings.setMetadataProcessingEnabled(context, enabled)
//                        },
//                        colors = switchColors
//                    )
//                }
//            )
//
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable {
//                        val newState = !agentRecognizerEnabled
//                        agentRecognizerEnabled = newState
//                        AppSettings.setAgentRecognizerEnabled(context, newState)
//                    },
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(12.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column(modifier = Modifier.fillMaxWidth(0.75f)) {
//                        Text("角色识别（实验性）（当前版本请勿开启）", color = MaterialTheme.colorScheme.onSurface)
//                        Text(
//                            text = "尝试识别当前句由哪位歌手演唱并设置对唱歌词，可能存在错误。\n由于当前的处理实现会破坏结构，请勿开启。",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//                        )
//                    }
//                    SwitchWithIcon(
//                        checked = agentRecognizerEnabled,
//                        onCheckedChange = { enabled ->
//                            agentRecognizerEnabled = enabled
//                            AppSettings.setAgentRecognizerEnabled(context, enabled)
//                        },
//                        colors = switchColors
//                    )
//                }
//            }


            SettingsHeader("Lyric Timeline Offset")
            SettingsLabel("Song Offset + Device Offset")
            SettingsNavigateItem(
                title = "Lyric Timeline Offset Settings",
                onClick = {
                    context.startActivity(Intent(context, LyricOffsetSettingsActivity::class.java))
                }
            )




            SettingsHeader("Version Updates")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Current version: ${getCurrentVersionName(context)}",
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newState = !autoCheckEnabled
                                autoCheckEnabled = newState
                                AppSettings.setAutoUpdateCheckEnabled(context, newState)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                            Text("Automatically check for updates", color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "Automatically check GitHub Releases",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        SwitchWithIcon(
                            checked = autoCheckEnabled,
                            onCheckedChange = { enabled ->
                                autoCheckEnabled = enabled
                                AppSettings.setAutoUpdateCheckEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }

                    Text(
                        text = "Update Channel",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    CardClickActionOption(
                        title = "Release",
                        selected = updateChannel == UpdateChannel.STABLE,
                        onClick = {
                            updateChannel = UpdateChannel.STABLE
                            AppSettings.setUpdateChannel(context, UpdateChannel.STABLE)
                        }
                    )
                    CardClickActionOption(
                        title = "Beta",
                        selected = updateChannel == UpdateChannel.BETA,
                        onClick = {
                            updateChannel = UpdateChannel.BETA
                            AppSettings.setUpdateChannel(context, UpdateChannel.BETA)
                        }
                    )
                    CardClickActionOption(
                        title = "Dev Version",
                        selected = updateChannel == UpdateChannel.ALPHA,
                        onClick = {
                            updateChannel = UpdateChannel.ALPHA
                            AppSettings.setUpdateChannel(context, UpdateChannel.ALPHA)
                        }
                    )

                    Button(
                        onClick = {
                            if (checkingUpdate) return@Button
                            checkingUpdate = true
                            scope.launch {
                                val result = GitHubUpdateChecker.check(context, updateChannel)
                                AppSettings.setLastUpdateCheckAt(
                                    context,
                                    System.currentTimeMillis()
                                )
                                checkingUpdate = false

                                if (result.hasUpdate) {
                                    updateDialogTitle =
                                        "New version found: ${result.resolvedReleaseTag ?: "未知版本"}"
                                    updateDialogMessage = buildString {
                                        append("Current version: ${result.currentVersionName}\n")
                                        if (result.resolvedPublishedAt != null) {
                                            append("Publish time: ${formatReleaseTime(result.resolvedPublishedAt)}\n\n")
                                        }
                                        if (!result.resolvedReleaseNotes.isNullOrBlank()) {
                                            append(result.resolvedReleaseNotes)
                                        } else {
                                            append("No release notes")
                                        }
                                    }
                                    updateDialogUrl = result.resolvedReleaseUrl
                                } else {
                                    updateDialogTitle = "Check for Updates"
                                    updateDialogMessage = result.reason ?: "Already on the latest version"
                                    updateDialogUrl = null
                                }
                                showUpdateDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (checkingUpdate) "Checking..." else "Check for Updates")
                    }
                }
            }

            SettingsHeader("Notification Access Permission")
            SettingsDescription("Required permission. Used to get playing information.")
            SettingsDescription("Abusing notification access can endanger security, so the system might pop up a block. Since this app is open source, you can review its code execution logic, and there is no need to worry if the app source is trusted.")
            SettingsNavigateItem(
                title = "Go to 'Notification Read, Reply and Control'",
                onClick = onOpenNotificationSettings
            )

            SettingsHeader("Developer Tools")

            SettingsNavigateItem(
                title = "View Logs",
                onClick = {
                    context.startActivity(Intent(context, LogDisplayActivity::class.java))
                }
            )

            SettingsNavigateItem(
                title = "API Testing",
                onClick = {
                    context.startActivity(Intent(context, ApiTestActivity::class.java))
                }
            )

            SettingsHeader("Project and Contribution")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TextButton(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "https://github.com/Zeehan2005/AMLL-DroidMate".toUri()
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Project Repository")
                    }

                    TextButton(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "https://github.com/amll-dev/amll-ttml-db/blob/main/README.md".toUri()
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Help improve lyrics in AMLL TTML DB")
                    }
                }
            }
        }

        if (showUpdateDialog) {
            AlertDialog(
                onDismissRequest = { showUpdateDialog = false },
                containerColor = MaterialTheme.colorScheme.background,
                title = { Text(updateDialogTitle) },
                text = { Text(updateDialogMessage) },
                confirmButton = {
                    if (!updateDialogUrl.isNullOrBlank()) {
                        TextButton(
                            onClick = {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        updateDialogUrl!!.toUri()
                                    )
                                )
                                showUpdateDialog = false
                            }
                        ) {
                            Text("View Details")
                        }
                    } else {
                        TextButton(onClick = { showUpdateDialog = false }) {
                            Text("Got it")
                        }
                    }
                },
                dismissButton = {
                    if (!updateDialogUrl.isNullOrBlank()) {
                        TextButton(onClick = {
                            AppSettings.setLastUpdateLaterAt(context, System.currentTimeMillis())
                            showUpdateDialog = false
                        }) {
                            Text("Later")
                        }
                    }
                }
            )
        }
    }
}

// ── 设置页面通用组件 ─────────────────────────────────────────────────

/**
 * 设置分组节标题
 */
@Composable
private fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

/**
 * 设置项描述文字
 */
@Composable
private fun SettingsDescription(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
}

/**
 * 设置项小标签
 */
@Composable
private fun SettingsLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
}

/**
 * 开关切换设置项卡片。点击卡片或开关都会触发切换。
 *
 * @param title 主标题
 * @param description 可选描述文字（非 null 时显示）
 * @param checked 当前开关状态
 * @param onCheckedChange 切换回调（接收新状态）
 * @param switchColors 开关配色
 */
@Composable
private fun SettingsToggleItem(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    switchColors: SwitchColors,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.fillMaxWidth(if (description != null) 0.75f else 0.78f)) {
                Text(title, color = MaterialTheme.colorScheme.onSurface)
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            SwitchWithIcon(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = switchColors
            )
        }
    }
}

/**
 * 导航型设置项卡片，右侧显示箭头。
 */
@Composable
private fun SettingsNavigateItem(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = MaterialTheme.colorScheme.onSurface)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Enter",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatReleaseTime(instant: Instant): String {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())
        .format(instant)
}

private fun getCurrentVersionName(context: Context): String {
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    return packageInfo.versionName ?: "unknown"
}

private fun needsNotificationPermission(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}

private fun hasNotificationPermission(context: Context): Boolean {
    if (!needsNotificationPermission()) return true
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
private fun CardClickActionOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}