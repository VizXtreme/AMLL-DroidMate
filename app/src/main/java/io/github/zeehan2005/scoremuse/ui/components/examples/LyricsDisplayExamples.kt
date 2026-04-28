package io.github.zeehan2005.scoremuse.ui.components.examples

/**
 * ScoreMuse 歌词显示组件使用示例
 * 
 * 这个文件展示了如何在你的应用中使用 ScoreMuse 歌词显示组件。
 */

// ==================== 基本用法 ====================


// ==================== 完整集成示例 ====================

// ==================== 从文件加载示例 ====================

/**
 * 示例4：从 Assets 或 Raw 资源加载 XML
 * 
 * 在 Activity 中的使用方式：
 */
/*
class MyLyricsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            var xmlContent by remember { mutableStateOf("") }
            var currentTime by remember { mutableStateOf(0L) }
            
            // 从 assets 加载 XML
            LaunchedEffect(Unit) {
                try {
                    xmlContent = assets.open("lyrics/example.xml")
                        .bufferedReader()
                        .use { it.readText() }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            ScoreMuseTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (xmlContent.isNotEmpty()) {
                        BasicLyricsExample(
                            xmlContent = xmlContent,
                            currentTime = currentTime
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator()
                        }
                    }
                }
            }
        }
    }
}
*/

// ==================== 与 MediaPlayer 集成 ====================

/**
 * 示例5：与 MediaPlayer 集成
 * 
 * 展示如何同步歌词和音频播放。
 */
/*
@Composable
fun MediaPlayerIntegratedLyrics(
    mediaPlayer: MediaPlayer,
    xmlContent: String
) {
    var currentTime by remember { mutableStateOf(0L) }
    var isPlaying by remember { mutableStateOf(false) }
    
    // 监听播放进度
    LaunchedEffect(mediaPlayer, isPlaying) {
        while (isPlaying) {
            currentTime = mediaPlayer.currentPosition.toLong()
            delay(100) // 每100ms更新一次
        }
    }
    
    val lyrics = remember(xmlContent) {
        ScoreMuseXmlHelper.parseFromXml(xmlContent)
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 歌词显示区域
        KaraokeStyleLyricsDisplay(
            lyrics = lyrics,
            currentTime = currentTime,
            onLineSeek = { time ->
                mediaPlayer.seekTo(time.toInt())
            },
            modifier = Modifier.weight(1f)
        )
        
        // 播放控制
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                isPlaying = !isPlaying
                if (isPlaying) mediaPlayer.start()
                else mediaPlayer.pause()
            }) {
                Text(if (isPlaying) "暂停" else "播放")
            }
        }
    }
}
*/

// ==================== 自定义样式 ====================

// ==================== 注意事项 ====================

/*
 * 使用提示：
 * 
 * 1. 性能优化：
 *    - 对于大型歌词文件，建议使用 LazyColumn（已内置）
 *    - 避免在每次重组时重新解析 XML，使用 remember
 *    -  currentTime 更新频率建议为 100-200ms
 * 
 * 2. 内存管理：
 *    - 在 Activity/Fragment 销毁时清理资源
 *    - 大型歌词文件考虑分页加载
 * 
 * 3. 用户体验：
 *    - 提供加载状态指示器
 *    - 支持手势操作（缩放、滑动）
 *    - 允许用户自定义字体大小
 * 
 * 4. 兼容性：
 *    - 确保 XML 格式符合 ScoreMuse 规范
 *    - 处理缺少某些字段的情况（翻译、音译等）
 *    - 提供降级方案（无逐字信息时使用行级显示）
 * 
 * 5. 调试技巧：
 *    - 使用 ScoreMuseXmlHelper 的工具方法检查 XML 特性
 *    - 查看 Logcat 中的解析日志
 *    - 使用 Demo Activity 测试不同的 XML 文件
 */