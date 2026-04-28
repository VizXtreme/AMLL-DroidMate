# ScoreMuse 歌词显示组件

这个目录包含了用于显示 ScoreMuse 自定义 XML 格式歌词的 Compose 组件。

## 组件列表

### 1. ScoreMuseLyricsDisplay
基础的歌词显示组件，支持：
- 逐字高亮显示
- 翻译和音译
- 自动滚动到当前行
- 点击跳转
- 背景音/合唱标记

**基本用法：**
```kotlin
ScoreMuseLyricsDisplay(
    lyrics = lyrics,
    currentTime = currentTime,
    onLineSeek = { time -> mediaPlayer.seekTo(time.toInt()) },
    modifier = Modifier.fillMaxSize()
)
```


### 3. ScoreMuseXmlHelper
工具类，提供 XML 解析和辅助功能：

```kotlin
// 解析 XML
val lyrics = ScoreMuseXmlHelper.parseFromXml(xmlContent)

// 检查特性
val hasWordTiming = ScoreMuseXmlHelper.hasWordByWordTiming(xmlContent)
val hasTranslation = ScoreMuseXmlHelper.hasTranslation(xmlContent)

// 获取元数据
val metadata = ScoreMuseXmlHelper.extractMetadata(xmlContent)
```

## 快速开始

### 步骤 1: 准备 XML 歌词

确保你的歌词符合 ScoreMuse XML 格式（参考 `file-format/example.xml`）。

### 步骤 2: 解析 XML

```kotlin
val xmlContent = """<?xml version="1.0" encoding="UTF-8"?>
<scoremuse xmlns="https://github.com/Zeehan2005/ScoreMuse">
    ...
</scoremuse>"""

val lyrics = ScoreMuseXmlHelper.parseFromXml(xmlContent)
```

### 步骤 3: 显示歌词

```kotlin
@Composable
fun MyLyricsScreen(currentTime: Long) {
    KaraokeStyleLyricsDisplay(
        lyrics = lyrics,
        currentTime = currentTime,
        onLineSeek = { time -> 
            // 处理跳转
        }
    )
}
```

### 步骤 4: 同步播放进度

```kotlin
var currentTime by remember { mutableStateOf(0L) }

LaunchedEffect(mediaPlayer) {
    while (true) {
        currentTime = mediaPlayer.currentPosition.toLong()
        delay(100)
    }
}
```

## 完整示例

查看 `LyricsDisplayExamples.kt` 文件获取更多使用示例，包括：
- 基本用法
- 卡拉OK风格
- 与 MediaPlayer 集成
- 自定义样式
- 从文件加载

## 演示 Activity

运行 `ScoreMuseLyricsDemoActivity` 查看完整的演示效果。

## 支持的 XML 特性

- ✅ 逐字时间戳 (`<w>` 元素)
- ✅ 翻译 (`<translation>` 元素)
- ✅ 音译 (`<roman>` 元素)
- ✅ 演唱者标识 (`by` 属性)
- ✅ 背景音标记 (`isBG`)
- ✅ 合唱标记 (`isDuet`)
- ✅ 歌曲结构段落 (`<para>` 元素)

## 性能提示

1. **避免频繁重组**：使用 `remember` 缓存解析结果
2. **更新频率**：建议每 100-200ms 更新一次 `currentTime`
3. **大文件优化**：组件已使用 `LazyColumn`，自动处理大型歌词文件
4. **内存管理**：在 Activity 销毁时清理资源

## 故障排除

### 歌词不显示
- 检查 XML 格式是否正确
- 确认 `lyrics` 不为 null
- 查看 Logcat 中的解析日志

### 滚动不流畅
- 降低 `currentTime` 更新频率
- 确保没有在主线程进行耗时操作

### 逐字高亮不工作
- 检查 XML 是否包含 `<w>` 元素
- 使用 `ScoreMuseXmlHelper.hasWordByWordTiming()` 验证

## 自定义

可以通过修改 MaterialTheme 来自定义外观：

```kotlin
MaterialTheme(
    colorScheme = darkColorScheme(
        primary = Color(0xFF64B5F6),
        // ...
    )
) 
```

## 未来改进

- [ ] 添加手势缩放支持
- [ ] 支持自定义字体
- [ ] 添加歌词编辑功能
- [ ] 导出为其他格式（LRC、TTML等）
- [ ] 背景粒子效果
- [ ] 更多动画效果

## 相关文档

- ScoreMuse XML 格式规范：`file-format/` 目录
- XML 转换器：`data/converter/XMLConverter.kt`
- 数据模型：`domain/model/Lyrics.kt`
