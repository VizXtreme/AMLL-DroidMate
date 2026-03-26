# Kotlin 版 AMLL 桥接协议实现总结

## 📦 已创建的文件

### 1. **核心协议层** 
📄 [`AMLLBridgeProtocol.kt`](../app/src/main/java/com/amll/droidmate/bridge/AMLLBridgeProtocol.kt)

**内容**：
- ✅ `BridgeMessage` - 顶层消息结构
- ✅ `StatePayload` - Android → WebView 的状态更新
- ✅ `CommandPayload` - WebView → Android 的控制命令
- ✅ `AMLLBridgeManager` - 桥接管理器（核心）
- ✅ kotlinx.serialization 配置

**关键类**：
```kotlin
// 状态更新（Android → WebView）
SetMusicInfo(musicId, musicName, albumName, artistName, duration)
SetLyric(format = "ttml", data)
ProgressUpdate(progress)
PausedUpdate()
ResumedUpdate()
VolumeUpdate(volume)

// 控制命令（WebView → Android）
SeekCommand(progress)
PauseCommand()
ResumeCommand()
SetVolumeCommand(volume)
LyricLineClickCommand(lineIndex, time)
```

### 2. **使用指南**
📄 [`BRIDGE_PROTOCOL_GUIDE.md`](../docs/BRIDGE_PROTOCOL_GUIDE.md)

**内容**：
- ✅ 快速开始教程
- ✅ 完整的 API 文档
- ✅ 最佳实践建议
- ✅ 性能优化技巧
- ✅ 迁移计划指南

### 3. **集成示例**
📄 [`AMLLLyricsViewWithBridgeExample.kt`](../app/src/main/java/com/amll/droidmate/ui/screens/AMLLLyricsViewWithBridgeExample.kt)

**内容**：
- ✅ 完整的使用示例
- ✅ 如何初始化桥接管理器
- ✅ 如何处理命令和状态更新
- ✅ 向后兼容的示例代码

## 🎯 核心特性

### 1. **结构化消息传递**

```kotlin
// ❌ 旧方式：散弹式调用
webView.evaluateJavascript("window.updateTime(123)", null)
webView.evaluateJavascript("window.updateLyrics($json)", null)

// ✅ 新方式：结构化消息
bridgeManager.updateProgress(123)
bridgeManager.updateLyric(ttmlContent)
```

### 2. **双向通信架构**

```
┌──────────────────┐              ┌─────────────┐
│  AMLLBridgeManager│◄── Command ──│   WebView   │
│   (Android Kotlin)│              │ (JavaScript)│
└────────┬─────────┘              └──────▲──────┘
         │ State Update                  │
         └───────────────────────────────┘
```

### 3. **类型安全保证**

```kotlin
// 编译时检查消息类型
val message = StateUpdateMessage(
    payload = ProgressUpdate(progress = 12345L) // ✓ Long 类型
)

// 自动序列化
val json = bridgeJson.encodeToString(message)
// 输出：{"type":"stateUpdate","payload":{"updateType":"progress","progress":12345}}
```

### 4. **消息队列机制**

```kotlin
// WebView 未就绪时
bridgeManager.updateMusicInfo(...) // 加入队列

// WebView 加载完成后
bridgeManager.onWebViewReady() // 自动发送所有 queued 消息
```

## 🚀 如何使用

### Step 1: 添加依赖

在 `build.gradle.kts` 中：
```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}
```

### Step 2: 创建桥接管理器

```kotlin
class MyActivity : ComponentActivity() {
    private val bridgeManager = AMLLBridgeManager()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 设置监听器接收 WebView 命令
        bridgeManager.setListener(object : AMLLBridgeManager.Listener {
            override fun onCommand(command: CommandPayload) {
                when (command) {
                    is SeekCommand -> mediaPlayer.seekTo(command.progress)
                    is PauseCommand -> mediaPlayer.pause()
                    is ResumeCommand -> mediaPlayer.start()
                }
            }
            
            override fun onError(error: Throwable) {
                Timber.e(error, "Bridge error")
            }
        })
    }
}
```

### Step 3: 集成到 WebView

```kotlin
@Composable
fun MyLyricsView(currentTime: Long) {
    val bridgeManager = remember { AMLLBridgeManager() }
    
    AndroidView(factory = { context ->
        WebView(context).apply {
            addJavascriptInterface(
                object {
                    @JavascriptInterface
                    fun postMessage(json: String) {
                        bridgeManager.receiveFromWebView(json)
                    }
                },
                "AndroidBridge"
            )
        }
    }, update = {
        bridgeManager.onWebViewReady()
        bridgeManager.updateProgress(currentTime)
    })
}
```

## 💡 设计亮点

### 1. **受 Tauri 启发的协议设计**

| 概念 | Tauri (Rust) | DroidMate (Kotlin) |
|------|-------------|-------------------|
| 消息结构 | `enum Body` | `sealed class BridgeMessage` |
| 序列化 | serde_json / binrw | kotlinx.serialization |
| 事件系统 | `app.emit()` | `Listener` 接口 |
| 协议版本 | V1/V2 自动协商 | `version` 字段 |

### 2. **密封类的妙用**

```kotlin
// 编译时穷尽检查
when (command) {
    is SeekCommand -> ...      // ✓ 必须处理
    is PauseCommand -> ...     // ✓ 必须处理
    // 如果漏掉某个分支，编译器会报错
}
```

### 3. **Flow 集成**

```kotlin
// 与 Kotlin Flow 完美结合
LaunchedEffect(Unit) {
    playbackStateFlow
        .distinctUntilChanged()
        .collect { state ->
            bridgeManager.updateProgress(state.position)
        }
}
```

### 4. **错误处理**

```kotlin
try {
    bridgeManager.receiveFromWebView(invalidJson)
} catch (e: SerializationException) {
    // 优雅降级
    fallbackToLegacyInterface()
}
```

## 📊 对比现有实现

### 当前方式（分散式）

```kotlin
//  scattered calls
webView.evaluateJavascript("window.updateTime($time)", null)
webView.evaluateJavascript("window.updateLyrics($json)", null)
webView.evaluateJavascript("window.setAlbumArt(\"$uri\")", null)

// 问题：
// - 难以追踪所有调用点
// - 没有类型检查
// - 错误处理分散
// - 不利于测试
```

### 新方式（集中式）

```kotlin
// 统一管理
bridgeManager.updateProgress(time)
bridgeManager.updateLyric(ttmlContent)
bridgeManager.sendStateUpdate(SetAlbumCover(uri))

// 优势：
// ✓ 所有调用集中在一处
// ✓ 类型安全的消息构造
// ✓ 统一的错误处理
// ✓ 易于 Mock 和测试
```

## 🔮 未来扩展方向

### 1. **真正的 WebSocket 支持**

```kotlin
// 基于同样的协议，可以轻松切换到 WebSocket
class WebSocketBridgeManager : AMLLBridgeManager() {
    private var websocket: WebSocket? = null
    
    fun connect(url: String) {
        websocket = OkHttpClient().newWebSocket(...)
    }
    
    override fun sendMessage(message: StateUpdateMessage) {
        websocket?.send(bridgeJson.encodeToString(message))
    }
}
```

### 2. **二进制协议支持**

```kotlin
// 类似 Tauri V1 的二进制格式
@Serializable
data class BinaryMessage(
    val magic: UShort, // 消息类型
    val length: Int,
    val payload: ByteArray
)

// 用于高性能场景（如音频 PCM 数据传输）
```

### 3. **插件化架构**

```kotlin
interface BridgePlugin {
    fun onMessageReceived(message: BridgeMessage)
    fun sendMessage(type: String, payload: Any)
}

// 可以开发各种插件
class LyricAnimationPlugin : BridgePlugin { ... }
class AudioVisualizerPlugin : BridgePlugin { ... }
```

## ⚠️ 注意事项

### 1. **线程安全**

```kotlin
// ❌ 错误：在后台线程调用 WebView
launch(Dispatchers.IO) {
    bridgeManager.sendMessage(...) // 可能崩溃
}

// ✅ 正确：在主线程调用
launch(Dispatchers.Main) {
    bridgeManager.sendMessage(...)
}
```

### 2. **内存管理**

```kotlin
// 及时清理 Listener
DisposableEffect(bridgeManager) {
    onDispose {
        bridgeManager.setListener(null)
    }
}
```

### 3. **版本兼容**

```kotlin
// 保留旧接口至少一个版本
@Deprecated("Use postMessage instead")
fun updateTime(time: Long) {
    updateProgress(time)
}
```

## 📖 学习资源

- [Tauri WebSocket 协议文档](../applemusic-like-lyrics/packages/ws-protocol/README.md)
- [Kotlin Serialization 官方文档](https://github.com/Kotlin/kotlinx.serialization)
- [本项目的桥接协议指南](../docs/BRIDGE_PROTOCOL_GUIDE.md)

## 🎉 总结

这个实现展示了如何：

✅ **学习先进架构** - 借鉴 Tauri 的设计思想  
✅ **保持 Kotlin 风格** - 使用协程、Flow、密封类等特性  
✅ **向后兼容** - 渐进式迁移，不影响现有功能  
✅ **面向未来** - 为可能的架构升级做准备  

虽然不直接把 Tauri 的代码搬过来，但我们学习了它的**核心思想**并用 Kotlin 的方式重新实现！🚀
