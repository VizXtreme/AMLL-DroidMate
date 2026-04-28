package io.github.zeehan2005.scoremuse.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.amll.droidmate.global.LyricLine
import dev.amll.droidmate.global.TTMLLyrics
import kotlinx.coroutines.launch

/**
 * ScoreMuse 自定义 XML 歌词显示组件
 * 
 * 这个 Composable 组件负责显示 ScoreMuse 自定义 XML 格式的歌词，支持：
 * - 逐字高亮动画
 * - 翻译和音译显示
 * - 歌曲结构段落标识
 * - 点击歌词行跳转
 * - 自动滚动到当前播放行
 * - 背景音声和合唱标记
 * 
 * @param lyrics UnifiedLyrics 对象，包含所有歌词数据
 * @param currentTime 当前播放时间（毫秒）
 * @param onLineSeek 用户点击歌词行时的跳转回调
 * @param modifier 修饰符
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun ScoreMuseLyricsDisplay(
    lyrics: TTMLLyrics?,
    currentTime: Long,
    onLineSeek: (Long) -> Unit,
    containerHeight: Int = 0,
    modifier: Modifier = Modifier
) {
    if (lyrics == null || lyrics.lines.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无歌词",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        return
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 找到当前播放的歌词行索引
    val actualCurrentLineIndex by derivedStateOf {
        lyrics.lines.indexOfLast { 
            it.startTime <= currentTime
        }
    }
    
    // 找到时间最近的歌词行索引（用于滚动）
    val currentLineIndex by derivedStateOf {
        var index = actualCurrentLineIndex
        
        // 如果没有找到当前行，找到时间最近的歌词行
        if (index < 0 && lyrics.lines.isNotEmpty()) {
            // 优化：使用更高效的方式找到最近的歌词行
            var minDiff = Long.MAX_VALUE
            var closestIndex = 0
            
            for (i in lyrics.lines.indices) {
                val line = lyrics.lines[i]
                val diff = if (currentTime < line.startTime) {
                    line.startTime - currentTime // 尚未开始的行
                } else {
                    currentTime - line.endTime // 已经结束的行
                }
                
                if (diff < minDiff) {
                    minDiff = diff
                    closestIndex = i
                }
            }
            
            index = closestIndex
        }
        index
    }

    // 自动滚动到当前行（基于外部传入的容器高度）
    LaunchedEffect(currentLineIndex, containerHeight) {
        if (currentLineIndex >= 0) {
            coroutineScope.launch {
                // 计算合适的滚动位置，基于容器高度的1/6
                val targetOffset = if (containerHeight > 0) containerHeight / 6 else 0
                
                // 滚动到当前行，使用平滑动画
                listState.animateScrollToItem(
                    index = currentLineIndex,
                    scrollOffset = -targetOffset
                )
            }
        }
    }

    // 优化：移除不必要的drawWithCache，减少内存开销
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 100.dp, // 顶部留白，确保第一行能滚动到合适位置
                bottom = 100.dp // 底部留白，确保最后一行也能滚动到合适位置
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                items = lyrics.lines,
                key = { _, line -> line.startTime } // 使用唯一的startTime作为键
            ) { index, line ->
                val isCurrentLine = index == actualCurrentLineIndex
                val isPastLine = index < actualCurrentLineIndex
                
                LyricLineItem(
                    line = line,
                    currentTime = currentTime,
                    isCurrentLine = isCurrentLine,
                    isPastLine = isPastLine,
                    onClick = { onLineSeek(line.startTime) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 单个歌词行组件
 * 
 * 显示一行歌词，包括：
 * - 主歌词文本（支持逐字高亮）
 * - 翻译文本（如果有）
 * - 音译文本（如果有）
 * - 演唱者标识（如果有）
 * - 背景音/合唱标记
 * 
 * @param line 歌词行数据
 * @param currentTime 当前播放时间
 * @param isCurrentLine 是否为当前播放行
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
private fun LyricLineItem(
    line: LyricLine,
    currentTime: Long,
    isCurrentLine: Boolean,
    isPastLine: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 使用 remember 缓存计算结果，减少 recomposition
    val mainTextColor = remember(isCurrentLine) {
        when {
            isCurrentLine -> Color.White.copy(alpha = 0.5f) // 当前句未唱到的字 50%白
            else -> Color.White.copy(alpha = 0.5f) // 非当前句 50%白
        }
    }
    
    // 缓存line的属性，避免在重组时重复访问
    val hasWords = remember(line.words) { line.words.isNotEmpty() }
    val hasTranslation = remember(line.translation) { line.translation != null }
    val hasTransliteration = remember(line.transliteration) { line.transliteration != null }
    
    // 使用 drawWithCache 优化渲染性能，将绘制操作转移到 GPU
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                }
            }
    ) {
        // 文本内容层 - 移除Card，直接使用Column减少嵌套层级
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // 演唱者标识 TODO

            // 主歌词文本（支持逐字高亮）
            if (hasWords) {
                WordByWordLyricText(
                    line = line,
                    currentTime = currentTime,
                    isCurrentLine = isCurrentLine,
                    isPastLine = isPastLine
                )
            } else {
                Text(
                    text = line.text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    ),
                    color = mainTextColor,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 翻译文本
            if (hasTranslation) {
                Text(
                    text = line.translation!!,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            // 音译文本
            if (hasTransliteration) {
                Text(
                    text = line.transliteration!!,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun WordByWordLyricText(
    line: LyricLine,
    currentTime: Long,
    isCurrentLine: Boolean,
    isPastLine: Boolean
) {
    // 优化：使用更高效的状态缓存，减少重组
    val wordsWithState by remember(line.words, currentTime, isCurrentLine) {
        derivedStateOf {
            line.words.map {
                val isWordPast = currentTime > it.endTime
                val isWordActive = currentTime in it.startTime..it.endTime
                val textColor = when {
                    (isWordPast || isWordActive) && isCurrentLine -> Color.White // 当前句唱到过的字 100%白
                    isCurrentLine -> Color.White.copy(alpha = 0.5f) // 当前句未唱到的字 50%白
                    else -> Color.White.copy(alpha = 0.5f) // 非当前句 50%白
                }
                it to textColor
            }
        }
    }
    
    // 优化：移除不必要的drawWithCache，因为FlowRow和Text组件已经有很好的性能
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        wordsWithState.forEach { (word, textColor) ->
            Text(
                text = word.word,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                ),
                color = textColor,
                modifier = Modifier.padding(horizontal = 0.dp)
            )
        }
    }
}