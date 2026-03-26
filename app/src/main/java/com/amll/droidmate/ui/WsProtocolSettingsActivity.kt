package com.amll.droidmate.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.amll.droidmate.ui.theme.DroidMateTheme
import com.amll.droidmate.ui.theme.DynamicThemeManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class WsProtocolSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            val dynamicColorScheme by DynamicThemeManager.observeColorScheme()

            DroidMateTheme(
                darkTheme = isDarkTheme,
                dynamicColorScheme = dynamicColorScheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WsProtocolSettingsPage(onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WsProtocolSettingsPage(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
    
    // WebSocket 连接状态
    var connectionStatus by remember { mutableStateOf<String>("未连接") }
    var isConnected by remember { mutableStateOf(false) }
    
    // 全局 WebSocket 客户端实例（用于实际连接）
    val webSocketClient = remember { 
        com.amll.droidmate.websocket.AMLLWebSocketClient.getInstance()
    }
    
    // 为设置页面的 WebSocket 客户端设置监听器
    LaunchedEffect(Unit) {
        webSocketClient.addListener(object : com.amll.droidmate.websocket.AMLLWebSocketClient.Listener {
            override fun onConnected() {
                Timber.i("WebSocket 已连接")
                connectionStatus = "已连接"
                isConnected = true
            }
            
            override fun onDisconnected() {
                Timber.w("WebSocket 已断开")
                connectionStatus = "未连接"
                isConnected = false
            }
            
            override fun onMessageReceived(message: String) {
                Timber.d("收到服务器消息：$message")
                // V2 协议下，服务器可能会广播状态更新
                // 可以在这里解析并更新 UI 状态
            }
            
            override fun onError(error: Throwable) {
                Timber.e(error, "WebSocket 错误")
                connectionStatus = "未连接"
                isConnected = false
                // 打印更详细的错误信息
                when (error) {
                    is java.io.EOFException -> {
                        Timber.e("服务器主动断开了连接，可能原因：")
                        Timber.e("  1. 服务器未运行或已关闭")
                        Timber.e("  2. 协议格式不匹配（检查 Initialize 消息格式）")
                        Timber.e("  3. 网络问题导致连接中断")
                        Timber.e("  4. 防火墙/安全软件阻止连接")
                    }
                    is java.net.ConnectException -> {
                        Timber.e("无法连接到服务器：${websocketAddress}")
                        Timber.e("请检查：")
                        Timber.e("  1. 服务器是否正在运行")
                        Timber.e("  2. IP 地址和端口是否正确")
                        Timber.e("  3. 设备是否在同一局域网内")
                    }
                    else -> {
                        Timber.e("未知错误类型：${error.javaClass.simpleName}")
                    }
                }
            }
        })
    }
    
    // 页面加载时自动连接（如果启用了 WebSocket）
    LaunchedEffect(websocketEnabled) {
        if (websocketEnabled && isValidWebSocketAddress(websocketAddress)) {
            webSocketClient.connect(websocketAddress)
        } else {
            webSocketClient.disconnect()
        }
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        TopAppBar(
            title = { Text("WebSocket 协议传递") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    @Suppress("DEPRECATION")
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // WebSocket 总开关卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                        Text(
                            text = "WebSocket 协议传递",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = websocketEnabled,
                            onCheckedChange = { enabled ->
                                websocketEnabled = enabled
                                AppSettings.setWebSocketProtocolEnabled(context, enabled)
                            },
                            colors = switchColors
                        )
                    }
                    
                    // 连接状态指示器
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        // 状态圆点
                        Box(
                            modifier = Modifier.size(8.dp),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
                                drawCircle(
                                    color = if (isConnected) {
                                        androidx.compose.ui.graphics.Color.Green // 绿色 - 已连接
                                    } else {
                                        androidx.compose.ui.graphics.Color.Gray // 灰色 - 未连接
                                    }
                                )
                            }
                        }
                        
                        Text(
                            text = "状态：$connectionStatus",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isConnected) {
                                androidx.compose.ui.graphics.Color.Green
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
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
                                webSocketClient.disconnect()
                                webSocketClient.connect(addressToSave)
                            } else {
                                // 如果未启用，提示将在开启时生效
                                Timber.d("WebSocket 地址已保存：$addressToSave，将在开启时自动连接")
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
                    Switch(
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
