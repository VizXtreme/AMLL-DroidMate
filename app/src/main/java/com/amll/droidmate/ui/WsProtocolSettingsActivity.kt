package com.amll.droidmate.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.amll.droidmate.ui.base.BaseComposeActivity
import com.amll.droidmate.ui.theme.DynamicThemeManager
import com.amll.droidmate.ui.theme.SuccessGreen
import com.amll.droidmate.ui.theme.WarningAmber
import timber.log.Timber

/**
 * WebSocket 协议设置界面
 * 
 * 这个 Activity 允许用户配置 WebSocket 连接参数，用于将播放状态同步到外部服务。
 * 主要功能包括：
 * - WebSocket 地址配置（默认：ws://localhost:11444）
 * - WebSocket 启用/禁用开关
 * - 电池优化豁免授权（保证后台运行）
 * - 连接状态检测（实时显示是否已连接）
 * - 发送测试消息（验证连接是否正常）
 * 
 * **使用场景**：
 * 配合 AMLL（Apple Music-like Lyrics）服务使用，
 * 将当前播放的歌曲信息、歌词、播放进度实时同步到 Web 端，
 * 在电脑浏览器或其他设备上显示同步歌词。
 * 
 * **注意事项**：
 * - 需要授予电池优化豁免，否则后台可能被杀死
 * - 地址格式必须是 ws:// 或 wss:// 开头
 * - 本地服务通常使用 ws://localhost:端口号
 */
class WsProtocolSettingsActivity : BaseComposeActivity() {
    @Composable
    override fun renderContent() {
        // 渲染 WebSocket 协议设置页面
        WsProtocolSettingsPage(onBack = { finish() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WsProtocolSettingsPage(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 检查是否忽略电池优化
    fun isIgnoringBatteryOptimizations(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true // Android 6.0 以下不需要考虑电池优化
        }
    }
    
    var batteryOptimizationIgnored by remember { 
        mutableStateOf(isIgnoringBatteryOptimizations()) 
    }
    
    // 在未授权时，每 2 秒检测一次状态
    LaunchedEffect(batteryOptimizationIgnored) {
        if (!batteryOptimizationIgnored) {
            // 未授权时，每 2 秒检查一次
            while (true) {
                kotlinx.coroutines.delay(2000)
                val currentState = isIgnoringBatteryOptimizations()
                if (currentState != batteryOptimizationIgnored) {
                    batteryOptimizationIgnored = currentState
                    break // 状态改变后停止定时检查
                }
            }
        }
    }

    val dynamicColorScheme by DynamicThemeManager.observeColorScheme()
    val rippleColor = dynamicColorScheme?.primary ?: MaterialTheme.colorScheme.primary

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = rippleColor,
        checkedTrackColor = rippleColor.copy(alpha = 0.5f),
        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
        uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.24f)
    )

    var websocketAddress by remember { 
        mutableStateOf(AppSettings.getWebSocketProtocolAddress(context)) 
    }
    var websocketEnabled by remember { 
        mutableStateOf(AppSettings.isWebSocketProtocolEnabled(context)) 
    }
    var webViewEnabled by remember {
        mutableStateOf(AppSettings.isWebViewEnabled(context))
    }
    
    // WebSocket 连接状态 - 使用统一的状态监听器
    val webSocketClient = remember { 
        com.amll.droidmate.websocket.AMLLWebSocketClient.getInstance()
    }
    
    // 使用 produceState 实时监听连接状态变化
    val isConnected by produceState(initialValue = webSocketClient.isConnected) {
        value = webSocketClient.isConnected
        
        // 使用工厂函数创建简单的状态监听器
        val listener = webSocketClient.createStateListener(
            onStateChanged = { connected ->
                value = connected
            },
            onErrorCallback = { error ->
                Timber.e("[WebSocketSettings] WebSocket error: ${error.message}", error)
                // 打印更详细的错误信息
                when (error) {
                    is java.io.EOFException -> {
                        Timber.e("[WebSocketSettings] 服务器主动断开了连接")
                    }
                    is java.net.ConnectException -> {
                        Timber.e("[WebSocketSettings] 无法连接到服务器")
                    }
                    else -> {
                        Timber.e("[WebSocketSettings] 错误类型：${error.javaClass.simpleName}")
                    }
                }
            }
        )
        
        webSocketClient.addListener(listener)
    }
    
    val connectionStatus = if (isConnected) "已连接" else "未连接"

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    androidx.compose.material3.Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("WebSocket 传递") },
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
            // WebSocket 总开关卡片
            val statusCardBg = if (websocketEnabled && isConnected) {
                SuccessGreen.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = statusCardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "WebSocket 传递",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (websocketEnabled && isConnected) SuccessGreen else MaterialTheme.colorScheme.onSurface
                            )
                            
                            // 连接状态指示器（作为附属说明）
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                // 状态圆点
                                Box(
                                    modifier = Modifier.size(6.dp),
                                    contentAlignment = androidx.compose.ui.Alignment.Center
                                ) {
                                    val dotColor = if (isConnected) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
                                        drawCircle(color = dotColor)
                                    }
                                }
                                
                                Text(
                                    text = "状态：$connectionStatus",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isConnected) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        SwitchWithIcon(
                            checked = websocketEnabled,
                            onCheckedChange = { enabled ->
                                websocketEnabled = enabled
                                AppSettings.setWebSocketProtocolEnabled(context, enabled)

                                // 切换开关时立即生效连接
                                if (enabled && isValidWebSocketAddress(websocketAddress)) {
                                    Timber.d("[WebSocketSettings] 启用 WebSocket，尝试连接：$websocketAddress")
                                    webSocketClient.connect(websocketAddress)
                                } else {
                                    Timber.d("[WebSocketSettings] 禁用 WebSocket，断开连接")
                                    webSocketClient.disconnect()
                                }
                            },
                            colors = if (websocketEnabled && isConnected) {
                                SwitchDefaults.colors(
                                    checkedThumbColor = SuccessGreen,
                                    checkedTrackColor = SuccessGreen.copy(alpha = 0.5f),
                                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.24f)
                                )
                            } else {
                                switchColors
                            }
                        )
                    }
                }
            }

            // 后台运行说明卡片 - 根据电池优化状态显示不同样式
            val batteryCardBg = if (batteryOptimizationIgnored) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                WarningAmber.copy(alpha = 0.2f)
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = batteryCardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (batteryOptimizationIgnored) Icons.Outlined.Info else Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = if (batteryOptimizationIgnored) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                WarningAmber
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "需要调整电池优化设置、白名单等以保持后台存活，具体设置随设备 OEM 而异。\n此外，根据 Android 要求，需要通知权限以保持\"前台活动\"持续。",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (batteryOptimizationIgnored) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                WarningAmber
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                                
                    Button(
                        onClick = {
                            // 跳转到电池优化设置页面
                            val intent = Intent().apply {
                                action = android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (batteryOptimizationIgnored) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            WarningAmber
                        }.let { color ->
                            androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = color,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    ) {
                        Text(if (batteryOptimizationIgnored) "打开电池优化设置" else "打开电池优化设置")
                    }
                }
            }
            
            // 使用 SideEffect 在每次重组时检查状态，确保从设置页面返回时立即更新
            androidx.compose.runtime.SideEffect {
                // 只在已授权时进行即时检测（用户可能手动关闭权限）
                if (batteryOptimizationIgnored) {
                    val currentState = isIgnoringBatteryOptimizations()
                    if (batteryOptimizationIgnored != currentState) {
                        batteryOptimizationIgnored = currentState
                    }
                }
            }

            // WebSocket 地址设置卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "WebSocket 配置",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = websocketAddress,
                        onValueChange = { newValue ->
                            websocketAddress = newValue
                        },
                        label = { Text("接收端 WebSocket URL") },
                        placeholder = { Text("ws://localhost:11444") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = !isValidWebSocketAddress(websocketAddress) && websocketAddress.isNotBlank()
                    )
                    
                    Button(
                        onClick = {
                            // URL 留空时保存为默认值，否则保存输入值
                            val addressToSave = if (websocketAddress.isBlank()) {
                                "ws://localhost:11444"
                            } else {
                                websocketAddress
                            }
                            
                            AppSettings.setWebSocketProtocolAddress(context, addressToSave)
                            // 如果当前已启用，立即重启连接
                            if (websocketEnabled) {
                                Timber.d("[WebSocketSettings] 保存设置并强制重连 WebSocket: $addressToSave")
                                webSocketClient.connect(addressToSave, forceReconnect = true)
                            } else {
                                // 如果未启用，提示将在开启时生效
                                Timber.d("[WebSocketSettings] WebSocket 地址已保存：$addressToSave，将在开启时自动连接")
                            }
                        },
                        enabled = isValidWebSocketAddress(websocketAddress) || websocketAddress.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("保存")
                    }
                    

                    if (!isValidWebSocketAddress(websocketAddress) && websocketAddress.isNotBlank()) {
                        Text(
                            text = "请输入有效的 WebSocket 地址（格式：ws://host:port 或 wss://host:port）",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // WebView 全局开关卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                        Text(
                            text = "DroidMate 主界面歌词组件",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "关闭后不再使用歌词组件，完全依赖外部。可能更省电。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    SwitchWithIcon(
                        checked = webViewEnabled,
                        onCheckedChange = { enabled ->
                            webViewEnabled = enabled
                            AppSettings.setWebViewEnabled(context, enabled)
                        },
                        colors = switchColors
                    )
                }
            }
            
            


        }
    }
}

/**
 * 从 WebSocket 地址提取主机信息用于显示
 */
private fun extractHost(address: String): String {
    return try {
        val uri = java.net.URI(address)
        "${uri.host}:${if (uri.port == -1) "默认端口" else uri.port}"
    } catch (e: Exception) {
        address
    }
}

/**
 * 验证 WebSocket 地址格式
 */
private fun isValidWebSocketAddress(address: String): Boolean {
    return try {
        address.startsWith("ws://") || address.startsWith("wss://")
    } catch (e: Exception) {
        false
    }
}
