package com.amll.droidmate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.amll.droidmate.domain.model.SongStructure
import com.amll.droidmate.domain.model.SongStructureType
import kotlinx.coroutines.launch

/**
 * 歌曲结构显示条
 * 
 * @param structures 歌曲结构列表
 * @param currentTime 当前播放时间（毫秒）
 * @param onSeekTo 跳转到指定时间的回调
 * @param modifier 修饰符
 */
@Composable
fun SongStructureBar(
    structures: List<SongStructure>,
    currentTime: Long,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (structures.isEmpty()) {
        return
    }
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    
    // 常量：定义布局参数
    val horizontalPaddingDp = 16.dp // 左右各 16dp，总共 32dp
    val chipSpacingDp = 8.dp // chips 之间的间距
    val horizontalPaddingPx = with(density) { horizontalPaddingDp.roundToPx() }
    val chipSpacingPx = with(density) { chipSpacingDp.roundToPx() }
    
    // 存储 LazyRow 容器的宽度（像素）
    var containerWidthPx by remember { mutableIntStateOf(0) }
    // 计算实际可用于 chips 的宽度（减去 padding）
    val availableWidthPx = maxOf(0, containerWidthPx - 2 * horizontalPaddingPx)
    
    // 存储每个 chip 的自然宽度（像素），使用不可变 Map 避免状态变更问题
    // 使用 structures 作为 key，当 structures 变化时自动重置测量状态
    var chipNaturalWidthsPx by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    
    // 添加一个 key 用于追踪 structures 的变化，确保切歌时重置测量状态
    val structuresKey = remember(structures) {
        // 生成 structures 的唯一标识符，当内容变化时会改变
        structures.hashCode() xor structures.size
    }
    
    // 计算所有 chips 的总自然宽度
    val totalNaturalWidthPx = chipNaturalWidthsPx.values.sum()
    // 计算所有间距的总宽度（n 个 chips 有 n-1 个间距）
    val totalSpacingWidthPx = if (chipNaturalWidthsPx.size > 1) {
        (chipNaturalWidthsPx.size - 1) * chipSpacingPx
    } else {
        0
    }
    // chips 实际占用的总宽度 = 自然宽度 + 间距
    val totalContentWidthPx = totalNaturalWidthPx + totalSpacingWidthPx
    
    // 判断是否需要扩展宽度
    val shouldExpand = totalContentWidthPx < availableWidthPx && 
                       availableWidthPx > 0 && 
                       chipNaturalWidthsPx.size == structures.size &&
                       structures.isNotEmpty()
    
    // 计算每个 chip 应该分配的宽度（如果需要扩展）
    // 依赖 structuresKey 确保 structures 内容变化时重新计算
    val expandedChipWidthsPx by remember(structuresKey, chipNaturalWidthsPx, availableWidthPx, shouldExpand) {
        derivedStateOf<Map<Int, Int>> {
            if (!shouldExpand || structures.isEmpty()) {
                emptyMap()
            } else {
                // 需要填充的额外宽度 = 可用宽度 - (自然宽度总和 + 间距总和)
                val extraWidthPx = availableWidthPx - totalContentWidthPx
                // 平均分配给每个 chip
                val extraPerChipPx = extraWidthPx / structures.size
                // 每个 chip 的最终宽度 = 自然宽度 + 额外分配的宽度
                chipNaturalWidthsPx.mapValues { (_, naturalWidthPx) ->
                    naturalWidthPx + extraPerChipPx
                }
            }
        }
    }
    
    // 添加布局完成标志：确保所有芯片都完成测量后再执行滚动
    // 依赖 structuresKey 确保 structures 内容变化时重新检查
    val allChipsMeasured by remember(structuresKey, chipNaturalWidthsPx, expandedChipWidthsPx, shouldExpand) {
        derivedStateOf {
            val targetWidths = if (shouldExpand && expandedChipWidthsPx.isNotEmpty()) {
                expandedChipWidthsPx
            } else {
                chipNaturalWidthsPx
            }
            targetWidths.size == structures.size && structures.isNotEmpty()
        }
    }
    
    // 计算当前应该显示的 structure 索引
    val currentStructureIndex by remember(structures, currentTime) {
        derivedStateOf {
            structures.indexOfFirst { currentTime in it.startTime..it.endTime }
        }
    }
    
    // 当 structures 变化时，重置 chip 宽度测量状态
    LaunchedEffect(structuresKey) {
        // 清空旧的测量数据，让新歌词/chips 重新测量
        chipNaturalWidthsPx = emptyMap()
    }
    
    // 当 currentStructureIndex 变化时，自动滚动到该位置并居中
    LaunchedEffect(currentStructureIndex, containerWidthPx, allChipsMeasured) {
        if (currentStructureIndex >= 0 && containerWidthPx > 0 && allChipsMeasured) {
            // 优先使用扩展宽度，否则使用自然宽度
            val targetChipWidthPx = expandedChipWidthsPx[currentStructureIndex] 
                ?: chipNaturalWidthsPx[currentStructureIndex] 
                ?: 0
            
            // 计算从列表起始位置到目标 chip 起始位置的累计距离（不含目标 chip 自身宽度）
            val distanceToTargetStart = calculateDistanceToTargetStart(
                targetIndex = currentStructureIndex,
                chipWidths = if (expandedChipWidthsPx.isNotEmpty()) expandedChipWidthsPx else chipNaturalWidthsPx,
                spacingPx = chipSpacingPx,
                startPaddingPx = horizontalPaddingPx
            )
            
            // 计算目标 chip 中心点位置
            val targetCenterPx = distanceToTargetStart + (targetChipWidthPx / 2)
            
            // 计算需要的滚动偏移量以实现居中：
            // scrollOffset = 目标 item 应该停留的位置 - 目标 item 的起始位置
            // 要让目标 chip 居中，需要让它停在：容器中心点 - 目标 chip 半宽
            val targetPositionPx = (containerWidthPx / 2) - (targetChipWidthPx / 2)
            val scrollOffset = targetPositionPx - distanceToTargetStart
            
            // 执行平滑滚动动画
            listState.animateScrollToItem(
                index = currentStructureIndex,
                scrollOffset = scrollOffset
            )
        }
    }
    
    Box(modifier = modifier.onGloballyPositioned { coordinates ->
        containerWidthPx = coordinates.size.width
    }) {
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            itemsIndexed(structures, key = { _, structure -> structure.startTime }) { index, structure ->
                val expandedWidthPx = expandedChipWidthsPx[index]
                
                SongStructureChip(
                    structure = structure,
                    isCurrent = currentTime in structure.startTime..structure.endTime,
                    onClick = { onSeekTo(structure.startTime) },
                    modifier = Modifier
                        .then(
                            if (expandedWidthPx != null) {
                                // 如果已经计算出扩展宽度（像素），直接使用
                                Modifier.width(with(density) { expandedWidthPx.toDp() })
                            } else {
                                // 否则先测量自然宽度
                                Modifier.onGloballyPositioned { coordinates ->
                                    val width = coordinates.size.width
                                    if (chipNaturalWidthsPx[index] != width) {
                                        chipNaturalWidthsPx += (index to width)
                                    }
                                }
                            }
                        )
                )
            }
        }
    }
}

/**
 * 计算从列表起始位置到目标 chip 起始位置的累计距离（不含目标 chip 自身宽度）
 * 
 * @param targetIndex 目标 chip 的索引
 * @param chipWidths 每个 chip 的宽度（像素）映射表
 * @param spacingPx chip 之间的间距（像素）
 * @param startPaddingPx 列表起始的内边距（像素）
 * @return 从列表开始到目标 chip 起始位置的距离（像素）
 */
private fun calculateDistanceToTargetStart(
    targetIndex: Int,
    chipWidths: Map<Int, Int>,
    spacingPx: Int,
    startPaddingPx: Int
): Int {
    var distance = startPaddingPx
    for (i in 0 until targetIndex) {
        distance += chipWidths[i] ?: 0
        distance += spacingPx
    }
    return distance
}

/**
 * 歌曲结构芯片按钮
 * 
 * @param structure 歌曲结构段落
 * @param isCurrent 是否为当前播放的段落
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
fun SongStructureChip(
    structure: SongStructure,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 使用 Material Theme 的标准配色方案
    val backgroundColor = if (isCurrent) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = if (isCurrent) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    // 间奏/前奏/尾奏只显示音符符号，不显示文字和时间
    val isInterludeType = structure.type in listOf(
        SongStructureType.INTERLUDE,
        SongStructureType.INTRO,
        SongStructureType.OUTRO
    )
    
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .height(56.dp), // 固定高度，让所有芯片保持一致
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // 垂直居中
        ) {
            // 间奏/前奏/尾奏只显示音符符号，其他显示结构标签
            if (isInterludeType) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = structure.type.displayName,
                    tint = contentColor, // 使用内容颜色，保持单色
                    modifier = Modifier.size(28.dp) // 增大音符图标
                )
            } else {
                // 主文本：结构标签
                Text(
                    text = structure.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                
                // 副文本：时间范围（仅非间奏/前奏/尾奏显示）
                Text(
                    text = "${formatTime(structure.startTime)} - ${formatTime(structure.endTime)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 2.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
