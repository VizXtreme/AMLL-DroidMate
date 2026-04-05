# DroidMate 日志使用规范

## 📋 日志等级说明

本项目使用 Timber 作为日志库，所有日志必须按照以下规范使用对应的等级：

### 🔴 `Timber.wtf()` - 致命错误 (What a Terrible Failure)
**定义：** 会导致整个 Activity 崩溃的严重错误

**使用场景：**
- 应用无法继续运行的致命错误
- 关键资源加载失败导致无法显示界面
- 数据损坏或不一致导致无法继续操作
- 系统级别的关键故障

**示例：**
```kotlin
// 关键数据库文件损坏，无法启动
Timber.wtf("[Database] Database corrupted, cannot start app", e)

// 必需的配置文件丢失
Timber.wtf("[Config] Critical configuration file missing")
```

---

### 🟠 `Timber.e()` - 错误 (Error)
**定义：** 非预期的错误，会导致功能出错

**使用场景：**
- 网络请求失败
- 文件读写失败
- 数据解析错误
- API 调用异常
- 权限被拒绝导致功能无法使用

**示例：**
```kotlin
// 网络请求失败
Timber.e("[Network] Failed to fetch lyrics for song: $songId", e)

// 文件保存失败
Timber.e("[Storage] Cannot save log file to storage", e)

// JSON 解析错误
Timber.e("[WebSocket] Invalid WebSocket message format", e)
```

---

### 🟡 `Timber.w()` - 警告 (Warning)
**定义：** 非预期的错误，但功能几乎不影响或较少影响

**使用场景：**
- 非关键功能失败
- 性能问题的预警
- 已捕获但有潜在风险的异常

**示例：**
```kotlin
// 专辑封面提取失败，使用默认图片
Timber.w("[AlbumArtExtractor] Failed to extract album art, using default", e)

// 可恢复的网络错误（自动重试）
Timber.w("[Network] Request timeout, retrying (attempt 2/3)")

// 非关键功能失败（歌词显示正常，但动画效果不可用）
Timber.w("[Animation] Advanced blur effect not supported on this device")

// 数据格式警告（使用默认值继续）
Timber.w("[DataParser] Unknown lyric format version, assuming v1")

// 性能预警（操作成功但较慢）
Timber.w("[CacheManager] Cache cleanup took longer than expected: 2.5s")

```


---

### 🟢 `Timber.i()` - 信息 (Info)
**定义：** 一个阶段的完成，或是程序进入哪个分支结构（fallback 到什么方法），有可能出现的合理的无法获取

**⚠️ 重要说明：**
- **阶段开始 vs 完成**：一个阶段的**开始**一般使用 `Debug` 级别，**完成**使用 `Info` 级别
- **非预期完成**：如果出现非预期的完成情况（如失败、异常），应使用相应的 `Warn`/`Error`/`Wtf` 级别
- **边缘但可能的情况**：一些发生概率较低但合理的边缘情况（如资源未找到、降级处理），使用 `Info` 级别

**使用场景：**
- 重要流程的完成
- 状态转换（如：连接建立、断开）
- 用户关键操作的记录
- 有可能出现的合理异常情况（如：资源不存在）
- 功能模块初始化完成
- 条件分支的选择
- Fallback

**示例：**
```kotlin
// WebSocket 连接成功
Timber.i("[WebSocket] Connected to server: $url")

// 歌词匹配完成
Timber.i("[LyricsMatcher] Lyrics matched for song: $title")

// 有可能出现的合理 404（资源不存在）
Timber.i("[CustomLyrics] Custom lyric not found, using default")

// 进入某个业务分支
Timber.i("[CacheManager] Using cached data instead of network request")

// 服务启动完成
Timber.i("[NotificationListener] MediaListenerService started successfully")


// 缓存未命中，从网络获取（首次访问的正常情况）
Timber.i("[CacheManager] Cache miss for lyric data, fetching from network")

// 旧版本兼容处理（正常的系统适配）
Timber.i("[Compatibility] Using legacy API for Android version ${Build.VERSION.SDK_INT}")

// Fallback 到备用方案（合理的降级处理）
Timber.i("[SongStructure] Fallback: 无元数据结构信息，从歌词自动推断")
Timber.i("[LyricsMatcher] Fallback: 精确匹配失败，使用模糊匹配")
Timber.i("[CustomLyrics] Fallback: 自定义歌词未找到，使用默认歌词")

// 其他合理的异常情况
Timber.i("[Network] 网络请求超时，使用缓存数据")
Timber.i("[Storage] 外部存储不可用，使用内部存储")
Timber.i("[MediaSession] 当前无播放会话，等待用户操作")
Timber.i("[WebSocket] 连接已断开，将在后台重试")
Timber.i("[Permission] 通知权限未授予，使用基础功能")

// ✅ 边缘但可能的情况（发生概率低但是合理的）
Timber.i("[CustomLyrics] 自定义歌词文件不存在，使用默认歌词")
Timber.i("[CacheManager] 缓存已过期，从网络重新获取")
Timber.i("[ThemeEngine] 深色模式资源未找到，回退到浅色主题")

// ✅ 正确示例：阶段开始用 d，完成用 i
Timber.d("[DownloadManager] Starting download: $url")  // 阶段开始
// ... 下载过程 ...
Timber.i("[DownloadManager] Download completed: ${file.size} bytes")  // 阶段完成

// ❌ 错误示例：非预期的完成应该使用 w/e
// Timber.i("[DownloadManager] Download failed with error: $error")  // ❌ 应该用 e
// Timber.e("[DownloadManager] Download started")  // ❌ 开始不应该用 e
```

---

### 🔵 `Timber.d()` - 调试 (Debug)
**定义：** 一个阶段内部的处理逻辑

**使用场景：**
- 详细的处理步骤
- 变量值的变化
- 中间状态的记录
- 循环和迭代的过程
- 条件判断的详细路径
- 数据转换的细节

**⚠️ 重要限制：**
- **长内容行数限制**：单条 debug 日志的内容不得超过 10 行
- **超出降级规则**：如必须超出 10 行，超出的部分应降级为 `Timber.v()`（Verbose）
- **目的**：避免 logcat 被大量 debug 日志刷屏，保持日志可读性
- **禁止逐行发送**：除非是分步执行的操作，否则不应一行发送一次日志，应当使用换行符 `\n` 合并多条信息
- **等级限制**：展示长内容的日志等级不得高于 Debug（即只能使用 `d` 或 `v`，不能使用 `i`、`w`、`e`）

**示例：**
```kotlin
// 数据处理过程
Timber.d("[DataParser] Parsing lyric line: $line")
Timber.d("[DataParser] Extracted timestamp: $timestamp")

// 状态变化
Timber.d("[PlaybackControl] Current playback position: $position ms")

// 分支选择原因
Timber.d("[CacheManager] Skipping cache check because forceRefresh=true")

// 方法入口（复杂逻辑）
Timber.d("[UIUpdate] Entering syncLyricsWithProgress() with progress: $progress")

// ✅ 正确示例：使用换行符合并信息，避免逐行发送
val debugInfo = buildString {
    appendLine("Processing ${items.size} items:")
    items.take(10).forEachIndexed { index, item ->
        appendLine("  [$index] ${item.id}: ${item.name}")
    }
    if (items.size > 10) {
        appendLine("  ... and ${items.size - 10} more")
    }
}
Timber.d("[DataProcessor] $debugInfo")

// ✅ 正确示例：长内容控制在 10 行以内
Timber.d("[DataParser] Parsing lyrics (${lines.size} lines):")
lines.take(10).forEachIndexed { index, line ->
    Timber.d("[DataParser]   [$index] ${line.timestamp}: ${line.content}")
}
if (lines.size > 10) {
    Timber.v("[DataParser] ... and ${lines.size - 10} more lines (truncated)")
}

// ❌ 错误示例：逐行发送多条日志（会造成 logcat 刷屏）
// Timber.d("[Processor] Item 1: $item1")
// Timber.d("[Processor] Item 2: $item2")
// Timber.d("[Processor] Item 3: $item3")
// ... 不应该这样连续发送多条独立的日志

// ❌ 错误示例：使用 i/w/e 等级输出长内容
// Timber.i("[DataParser] Processing ${lines.size} lines:\n" +  // ❌ 不应该使用 i 级别
//     lines.joinToString("\n") { "${it.timestamp}: ${it.content}" })
// Timber.w("[Debug] Long debug content with multiple lines...")  // ❌ 不应该使用 w 级别

// ❌ 错误示例：超出 10 行仍使用 d 级别
// 不应该一次性输出 100 行歌词详情（会造成 logcat 刷屏）
```

---

### ⚪ `Timber.v()` - 详细 (Verbose)
**定义：** 持续性的检查但是发现没有变动，或超出debug行数限制的长内容

**使用场景：**
- 高频次的轮询检查
- 状态未改变的重复验证
- 超出debug行数限制的长内容

**示例：**
```kotlin
// 轮询检查（状态未变）
Timber.v("[Polling] Polling complete, no state change detected")

// 重复的数据验证
Timber.v("[Validation] Validation passed (same as previous)")

// 心跳检测
Timber.v("[WebSocket] Heartbeat check: connection stable")

```

---

## 📝 日志格式规范

### 基本格式
```kotlin
Timber.x("简短的描述性消息")
Timber.x("包含异常信息的描述", e)
Timber.x("带参数的消息：$param1, $param2")
```

### 参数顺序要求 ⚠️ **重要**
- **异常参数必须放在最后**：当记录异常时，`e` 参数必须作为最后一个参数
- **消息在前，异常在后**：始终先写日志消息，再写异常对象

#### 正确示例 ✅
```kotlin
Timber.e("[Network] Request failed: ${e.message}", e)
Timber.w("[Storage] Save failed, using fallback", e)
Timber.wtf("[Database] Critical error occurred", e)
```

#### 错误示例 ❌
```kotlin
Timber.e(e, "[Network] Request failed") // ❌ e 不应该在前面
Timber.w(e, "[Storage] Save failed")    // ❌ e 不应该在前面
```

### 标签（Tag）使用
- **自动提取**：LogHelper 会自动从调用堆栈提取类名作为 tag

### 模块标记要求 ⚠️ **重要**

**所有日志内容必须在每一行开头添加模块标记**，格式为 `[MODULE_NAME]`，便于快速识别日志来源的功能模块。

#### 常见模块标记示例：
- `[AGENT]` - 角色识别相关功能
- `[SongStructure]` - 歌曲结构分析功能
- `[WebSocket]` - WebSocket 通信相关
- `[LyricsMatcher]` - 歌词匹配功能
- `[NotificationListener]` - 通知监听服务
- `[AlbumArtExtractor]` - 专辑封面提取
- `[CustomLyrics]` - 自定义歌词功能
- `[PlaybackControl]` - 播放控制相关
- `[CacheManager]` - 缓存管理相关
- `[UIUpdate]` - UI 更新相关

#### 正确示例 ✅
```kotlin
// 角色识别功能
Timber.d("[AGENT] Starting agent recognition for line: $lyricLine")
Timber.i("[AGENT] Agent identified: $agentName")

// 歌曲结构分析
Timber.d("[SongStructure] Analyzing structure at position: $position")
Timber.w("[SongStructure] No structure found, using default")

// WebSocket 通信
Timber.i("[WebSocket] Connected to server: $address")
Timber.e("[WebSocket] Failed to parse message: $message", e)

// 歌词匹配
Timber.d("[LyricsMatcher] Matching lyrics with threshold: $threshold")
Timber.i("[LyricsMatcher] Match completed, confidence: $confidence")
```

#### 错误示例 ❌
```kotlin
// 缺少模块标记
Timber.d("Starting agent recognition") // ❌ 没有 [AGENT] 标记
Timber.e("Failed to parse message", e) // ❌ 没有 [WebSocket] 标记

// 标记不清晰
Timber.d("[Info] Processing data") // ❌ [Info] 不是有效的模块标记
Timber.d("[Debug] Variable value: $value") // ❌ [Debug] 不是有效的模块标记
```

### 完整格式示例
```
时间戳 [级别] 类名：[模块标记] 消息内容
2026-03-30 10:49:43.690 [D] AMLLLyricsViewKt: [AGENT] Starting recognition
```

### 消息内容要求
1. ✅ **必须包含模块标记**：所有日志必须在开头添加 `[MODULE]` 标记
2. ✅ **清晰明确**：能够清楚说明发生了什么
3. ✅ **包含上下文**：重要的参数值应该包含在消息中
4. ✅ **简洁精炼**：避免过长的消息
5. ❌ **避免敏感信息**：不要记录密码、token 等敏感数据
6. ❌ **避免过度日志**：不要在循环中大量记录不必要的日志
7. ❌ **禁止缺少标记**：不允许出现没有模块标记的日志
8. ❌ **禁止讲解性内容**：日志只记录事实和关键数据，不要包含"可能原因"、"请检查"等讲解性、教育性的内容

---

## 🎯 最佳实践

### 1. 错误处理中的日志
```kotlin
try {
    // 可能抛出异常的代码
    riskyOperation()
} catch (e: SpecificException) {
    // 记录具体原因
    Timber.e("[WebSocket] Connection failed with reason: ${e.message}", e)
    // 进行恢复处理
    handleFailure()
} catch (e: Exception) {
    // 未知异常需要更多信息
    Timber.e("[DataParser] Unexpected error in critical section", e)
    throw e // 或者适当的错误处理
}
```

### 2. 条件分支的日志
```kotlin
if (condition) {
    Timber.d("[CacheManager] Cache hit, using cached data")
    fastPath()
} else {
    Timber.d("[CacheManager] Cache miss, fetching from network")
    fallbackPath()
}
```

### 3. 生命周期的日志
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Timber.i("[MainActivity] Activity created with savedInstanceState: ${savedInstanceState != null}")
    // ...
}

override fun onDestroy() {
    Timber.i("[MainActivity] Activity destroying")
    super.onDestroy()
}
```

### 4. 异步操作的日志
```kotlin
lifecycleScope.launch {
    Timber.d("Starting async operation")
    try {
        val result = withContext(Dispatchers.IO) {
            Timber.d("Executing on IO thread")
            performNetworkRequest()
        }
        Timber.i("Async operation completed successfully")
        handleResult(result)
    } catch (e: Exception) {
        Timber.e("Async operation failed", e)
        handleError(e)
    }
}
```

---

## 🚫 常见错误用法

### ❌ 错误示例
```kotlin

// 未使用 Timber
println("Error occurred") // ❌ 不应该使用 println

// 过于简单的消息（缺少模块标记）
Timber.e("Error occurred") // ❌ 应该：Timber.e("[Network] Request failed: ${e.message}", e)

// 缺少关键信息（缺少模块标记和参数）
Timber.d("Processing data") // ❌ 应该：Timber.d("[DataParser] Processing ${items.size} items")

// 在生产环境使用 wtf
Timber.wtf("Just a normal error") // ❌ wtf 只用于致命错误

// 过度使用 v 级别
list.forEach { item ->
    Timber.v("Item: $item") // ❌ 如果列表很长会造成性能问题
}

// 记录敏感信息
Timber.d("User token: $userToken") // ❌ 绝对禁止！

// 缺少模块标记
Timber.d("[Info] Processing complete") // ❌ [Info] 不是有效的模块标记

// 包含讲解性内容
Timber.w("[SongStructure] ⚠️ Fallback 触发：无元数据结构信息")
Timber.w("[SongStructure] ⚠️ 可能原因:")  // ❌ 不应该解释原因
Timber.w("[SongStructure]   1. TTML 文件本身不包含 itunes:songPart 元数据")  // ❌ 不应该列举原因
Timber.w("[SongStructure]   2. UnifiedLyricsParser 在 processMetadata=false 模式下运行")  // ❌ 不应该列举原因

// 包含教育性内容
Timber.e("[WebSocket] 无法连接到服务器")
Timber.e("[WebSocket] 请检查：")  // ❌ 不应该指导用户检查
Timber.e("[WebSocket]   1. 服务器是否正在运行")  // ❌ 不应该列举检查项
Timber.e("[WebSocket]   2. IP 地址和端口是否正确")  // ❌ 不应该列举检查项
```

### ✅ 正确示例
```kotlin
// 包含具体错误信息和模块标记
Timber.e("[DataParser] Failed to parse JSON for user profile: ${e.message}", e)

// 包含关键参数和模块标记
Timber.d("[LyricsMatcher] Processing ${items.size} items with filter: $filterType")

// 合理使用 wtf（带模块标记）
Timber.wtf("[Database] Integrity check failed, app cannot continue", e)

// 脱敏处理（带模块标记）
Timber.d("[AuthService] User authenticated: ${user.id.hashCode()}")

// 完整的日志格式示例
Timber.i("[WebSocket] Connected to server: ws://localhost:8080")
Timber.d("[SongStructure] Analyzing structure at position: 120000ms")
Timber.w("[AlbumArtExtractor] Extraction failed, using default image")
```

---

## 📊 日志等级选择流程图

```
开始
  ↓
是否会导致应用崩溃？
  ↓
是 → Timber.wtf()
否
  ↓
是否会导致功能出错？
  ↓
是 → Timber.e()
否
  ↓
是否是未逾期但不严重的问题？
  ↓
是 → Timber.w()
否
  ↓
是否是阶段完成/出现分支/合理边缘情况？
  ↓
是 → Timber.i()
否
  ↓
是否是阶段内操作？
  ↓
是 → Timber.d()
否
  ↓
是否是无变化的持续检查或行数限制外内容？
  ↓
是 → Timber.v()
```

---

## 🔧 工具支持

### LogHelper 提供的功能
- ✅ 自动捕获所有 Timber 日志
- ✅ 自动提取调用者类名作为 tag
- ✅ 实时查看日志（LogDisplayActivity）
- ✅ 导出日志到文件
- ✅ 按级别统计日志数量

### 查看日志
1. 打开应用设置
2. 点击"开发者工具" → "查看日志"
3. 可以根据颜色快速识别日志级别：
   - 🔴 红色：Error/Assert
   - 🟠 橙色：Warn
   - 🟢 绿色：Info
   - 🔵 蓝色：Debug
   - ⚪ 灰色：Verbose

---

## 📖 参考资源

- [Timber 官方文档](https://github.com/JakeWharton/timber)
- [Android 日志最佳实践](https://developer.android.com/topic/performance/vitals/launch-time#logging)

---

**最后更新：** 2026-03-30  
**维护者：** DroidMate 开发团队
