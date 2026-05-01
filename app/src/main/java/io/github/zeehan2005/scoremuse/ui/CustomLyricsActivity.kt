package io.github.zeehan2005.scoremuse.ui

// icons are deprecated but AutoMirrored is unavailable; suppress warnings where used
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.zeehan2005.scoremuse.global.viewmodel.CustomLyricsCandidate
import io.github.zeehan2005.scoremuse.global.viewmodel.CustomLyricsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope

/**
 * 自定义歌词选择界面
 * 
 * 这个 Activity 允许用户手动选择或导入歌词。
 * 主要功能包括：
 * - 显示从多个平台搜索到的歌词候选列表
 * - 支持用户手动粘贴歌词文本
 * - 显示歌词来源和匹配置信度
 * - 支持歌词特性标签（逐字、翻译、音译等）
 * 
 * **使用场景**：
 * 当自动匹配的歌词不准确时，用户可以通过此界面：
 * 1. 从不同平台（QQ、网易云、酷狗）选择更准确的歌词
 * 2. 手动复制粘贴歌词文本
 * 3. 查看歌词的详细信息（来源、置信度、特性）
 * 
 * **数据传递**：
 * - 输入：歌曲标题、艺术家、播放来源
 * - 输出：选中的歌词文本、来源信息
 */
class CustomLyricsActivity : BaseComposeActivity() {
    @Composable
    override fun RenderContent() {
        // 从 Intent 获取参数
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val artist = intent.getStringExtra(EXTRA_ARTIST).orEmpty()
        val playbackSource = intent.getStringExtra(EXTRA_PLAYBACK_SOURCE)
        
        // 获取 ViewModel（管理歌词搜索和候选列表）
        val viewModel: CustomLyricsViewModel = viewModel()
        // 更新当前播放来源（用于优先级排序）
        LaunchedEffect(playbackSource) {
            viewModel.updateCurrentSource(playbackSource)
        }
        val appliedSource by viewModel.appliedLyricsSource.collectAsState()
        
        // 渲染自定义歌词页面
        CustomLyricsPage(
            title = title,
            artist = artist,
            onBack = { finish() },
            onApply = { lyricsText ->
                // 构建返回结果
                val result = Intent().apply {
                    putExtra(EXTRA_TITLE, title)
                    putExtra(EXTRA_ARTIST, artist)
                    putExtra(EXTRA_LYRICS_TEXT, lyricsText)
                    putExtra(EXTRA_SOURCE, appliedSource ?: "manual")
                }
                setResult(RESULT_OK, result)
                finish()
            }
        )
    }
    
    // Intent 参数常量定义
    companion object {
        const val EXTRA_TITLE = "extra_title"           // 歌曲标题
        const val EXTRA_ARTIST = "extra_artist"         // 艺术家名称
        const val EXTRA_LYRICS_TEXT = "extra_lyrics_text"  // 歌词文本
        const val EXTRA_SOURCE = "extra_lyrics_source"     // 歌词来源
        const val EXTRA_PLAYBACK_SOURCE = "extra_playback_source"  // 播放来源
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomLyricsPage(
    title: String,
    artist: String,
    onBack: () -> Unit,
    onApply: (String) -> Unit
) {
    val vm: CustomLyricsViewModel = viewModel()
    val candidates by vm.candidates.collectAsState()
    // Hide local-cache entries from the displayed list
    val visibleCandidates = remember(candidates) {
        candidates.filter { it.provider.lowercase() != "cache" }
    }
    val isSearching by vm.isSearching.collectAsState()
    val isApplying by vm.isApplying.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()
    val appliedLyricsText by vm.appliedLyricsText.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    var manualText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // File picker launcher for importing lyrics from file
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            // Use coroutineScope to launch coroutine
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val fileContent = inputStream.bufferedReader().use { it.readText() }
                        withContext(Dispatchers.Main) {
                            manualText = fileContent
                            // 自动应用导入的歌词
                            vm.applyManualInput(fileContent, title, artist)
                        }
                    } else {
                        // Error reading file
                    }
                } catch (e: Exception) {
                    // Error handling
                }
            }
        }
    }

    LaunchedEffect(title, artist) {
        vm.searchCandidates(title, artist)
    }

    LaunchedEffect(appliedLyricsText) {
        if (!appliedLyricsText.isNullOrBlank()) {
            onApply(appliedLyricsText!!)
            vm.consumeAppliedLyricsText()
        }
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    androidx.compose.material3.Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("歌词管理") },
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
                actions = {
                    androidx.compose.material3.FilledIconButton(
                        onClick = {
                            context.startActivity(Intent(context, LyricsCacheActivity::class.java))
                        },
                        colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                            contentDescription = "管理缓存歌词"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = title.ifBlank { "未识别歌曲" }, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = artist.ifBlank { "未知歌手" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Text(
                text = "选择歌词来源",
                style = MaterialTheme.typography.titleMedium
            )

            // new explanatory subtitle
            Text(
                text = "按照推荐排序，因此新出现的选项也可能出现在上方。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isSearching && candidates.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LoadingIndicator(modifier = Modifier.size(16.dp))
                    Text(
                        text = "正在查询...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isSearching && candidates.isEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LoadingIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }

                items(visibleCandidates) { candidate ->
                    val onUse = remember(candidate) {
                        { vm.applyCandidate(candidate) }
                    }
                    CandidateItem(
                        candidate = candidate,
                        isApplying = isApplying,
                        onUse = onUse
                    )
                }

                // 按钮项可以放在列表底部，这样在滚动列表时也可见
                item {
                    if (visibleCandidates.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                // 对当前已显示的每个来源请求更多
                                val providers = visibleCandidates.map { it.provider.lowercase() }
                                    .distinct()
                                providers.forEach { prov -> vm.loadMore(prov) }
                            }) {
                                Text("查询更多选项")
                            }
                        }
                    }
                }
            }


            OutlinedTextField(
                value = manualText,
                onValueChange = { manualText = it },
                label = { Text("长按以粘贴") },
                placeholder = { Text("支持多种格式") },
                modifier = Modifier
                    .fillMaxWidth(),
                minLines = 1 // 减少行数以进一步降低高度
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { vm.applyManualInput(manualText, title, artist) },
                    enabled = manualText.isNotBlank() && !isApplying,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isApplying) "处理中..." else "应用")
                }
                Button(
                    onClick = { filePickerLauncher.launch("*/*") },
                    enabled = !isApplying,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("从文件导入")
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun CandidateItem(
    candidate: CustomLyricsCandidate,
    isApplying: Boolean,
    onUse: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = candidate.displayName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "${candidate.title} - ${candidate.artist}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            val detailsText = remember(candidate) {
                buildString {
                    append("匹配度: ${(candidate.confidence * 100).toInt()}%")
                    if (candidate.matchType.isNotBlank()) {
                        append(" (${candidate.matchType})")
                    }
                    if (candidate.features.isNotEmpty()) {
                        append(" | 支持: ")
                        append(candidate.features.joinToString(separator = ", ") {
                            it.displayName
                        })
                    }
                    append(" | ID: ${candidate.songId}")
                }
            }
            Text(
                text = detailsText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Button(
                onClick = onUse,
                enabled = !isApplying,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("应用")
            }
        }
    }
}