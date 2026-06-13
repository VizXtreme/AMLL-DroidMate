package io.github.zeehan2005.scoremuse.ui.components

import android.os.Trace
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.zeehan2005.scoremuse.global.SongStructure
import io.github.zeehan2005.scoremuse.global.SongStructureType
import io.github.zeehan2005.scoremuse.ui.formatTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.collections.plus

/**
 * 歌曲结构显示条
 *
 * 这个 Composable 组件负责在界面上显示歌曲的结构段落，并提供点击跳转功能。
 * 它会自动适应不同屏幕尺寸，支持滚动和自适应布局。
 *
 * **功能特点**：
 * - 可视化显示歌曲的各个段落（前奏、主歌、副歌、间奏、尾奏等）
 * - 高亮当前播放的段落
 * - 点击段落可跳转到对应位置
 * - 自动滚动保持当前段落在视野内
 * - 自适应布局：空间充足时平均分配，空间不足时横向滚动
 *
 * **布局算法**：
 * 1. 测量每个段落标签的自然宽度
 * 2. 计算可用容器宽度
 * 3. 如果总宽度小于容器宽度，则平均分配以填满容器
 * 4. 如果总宽度大于容器宽度，则启用横向滚动
 * 5. 自动滚动使当前播放段落在视野中心
 *
 * @param structures 歌曲结构列表（包含类型、时间范围、标签等信息）
 * @param currentTime 当前播放时间（毫秒），用于高亮当前段落
 * @param onSeekTo 用户点击段落时的跳转回调（传入目标时间）
 * @param modifier Compose 修饰符（用于调整大小、背景等）
 * @param baseColor 基础颜色，用于生成非活动状态的渐变背景
 */
@Composable
fun SongStructureBar(
    structures: List<SongStructure>,
    currentTime: Long,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {

    // 如果没有歌曲结构信息，直接返回不显示任何内容
    if (structures.isEmpty()) {
        return
    }

    /** LazyRow 的状态管理：用于控制横向滚动 */
    val listState = rememberLazyListState()
    // CoroutineScope：用于在需要时启动协程执行滚动动画
    rememberCoroutineScope()
    /** Density：用于在 DP 和 PX 之间转换 */
    val density = LocalDensity.current

    // ==================== 布局常量定义 ====================
    val horizontalPaddingDp = 16.dp // 左右各 16dp，总共 32dp 的内边距
    val chipSpacingDp = 8.dp // chips（段落标签）之间的间距
    /** 将 DP 转换为像素（布局计算需要使用像素） */
    val horizontalPaddingPx = with(density) { horizontalPaddingDp.roundToPx() }
    val chipSpacingPx = with(density) { chipSpacingDp.roundToPx() }

    // ==================== 容器宽度测量 ====================
    // 存储 LazyRow 容器的实际宽度（像素）
    var containerWidthPx by remember { mutableIntStateOf(0) }
    /** 计算实际可用于 chips 的宽度（减去左右 padding） */
    val availableWidthPx = maxOf(0, containerWidthPx - 2 * horizontalPaddingPx)

    // ==================== Chip 自然宽度管理 ====================
    // 存储每个 chip 的自然宽度（像素），使用不可变 Map 避免状态变更问题
    // Key: 段落索引，Value: 自然宽度（像素）
    var chipNaturalWidthsPx by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }

    /** 添加一个 key 用于追踪 structures 的变化，确保切歌时重置测量状态 */
    val structuresKey = remember(structures) {
        // 生成 structures 的唯一标识符，当内容变化时会改变
        structures.hashCode() xor structures.size
    }

    // ==================== 宽度计算 ====================
    // 计算所有 chips 的总自然宽度
    val totalNaturalWidthPx = chipNaturalWidthsPx.values.sum()
    /** 计算所有间距的总宽度（n 个 chips 有 n-1 个间距） */
    val totalSpacingWidthPx = if (chipNaturalWidthsPx.size > 1) {
        (chipNaturalWidthsPx.size - 1) * chipSpacingPx
    } else {
        0
    }
    /** chips 实际占用的总宽度 = 自然宽度 + 间距 */
    val totalContentWidthPx = totalNaturalWidthPx + totalSpacingWidthPx

    /**
     * 判断是否需要扩展宽度：
     * 1. 总内容宽度小于可用宽度
     * 2. 可用宽度大于 0（容器已测量完成）
     * 3. 所有 chips 都已完成测量
     * 4. structures 不为空
     */
    val shouldExpand = totalContentWidthPx < availableWidthPx &&
                       availableWidthPx > 0 &&
                       chipNaturalWidthsPx.size == structures.size &&
                       structures.isNotEmpty()

    /**
     * 计算每个 chip 应该分配的宽度（如果需要扩展）
     * 依赖 structuresKey 确保 structures 内容变化时重新计算
     */
    val expandedChipWidthsPx by remember(
        structuresKey,
        chipNaturalWidthsPx,
        availableWidthPx,
        shouldExpand
    ) {
        derivedStateOf {
            if (!shouldExpand || structures.isEmpty()) {
                emptyMap()
            } else {
                // 计算密集型操作，在后台线程执行
                runBlocking(Dispatchers.Default) {
                    /** 需要填充的额外宽度 = 可用宽度 - (自然宽度总和 + 间距总和) */
                    val extraWidthPx = availableWidthPx - totalContentWidthPx
                    /** 平均分配给每个 chip */
                    val extraPerChipPx = extraWidthPx / structures.size
                    // 每个 chip 的最终宽度 = 自然宽度 + 额外分配的宽度
                    chipNaturalWidthsPx.mapValues { (_, naturalWidthPx) ->
                        naturalWidthPx + extraPerChipPx
                    }
                }
            }
        }
    }

    /**
     * 添加布局完成标志：确保所有芯片都完成测量后再执行滚动
     * 依赖 structuresKey 确保 structures 内容变化时重新检查
     */
    val allChipsMeasured by remember(
        structuresKey,
        chipNaturalWidthsPx,
        expandedChipWidthsPx,
        shouldExpand
    ) {
        derivedStateOf {
            val targetWidths = if (shouldExpand && expandedChipWidthsPx.isNotEmpty()) {
                expandedChipWidthsPx
            } else {
                chipNaturalWidthsPx
            }
            targetWidths.size == structures.size && structures.isNotEmpty()
        }
    }

    /** 计算当前应该显示的 structure 索引 */
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
            /** 在后台线程计算滚动位置，避免阻塞UI线程 */
            val scrollData = withContext(Dispatchers.Default) {
                /** 获取当前可见 item 的信息，直接读取实际像素位置 */
                val visibleItem =
                    listState.layoutInfo.visibleItemsInfo.find { it.index == currentStructureIndex }

                if (visibleItem != null) {
                    /** 直接从 layoutInfo 获取目标 chip 的实际 position 信息 */
                    val chipStartOffsetPx = visibleItem.offset // chip 起始位置的偏移量（相对于当前可视区域左边缘）
                    val chipSizePx = visibleItem.size // chip 的实际大小（宽度）

                    /** 计算 chip 中心点相对于容器左边缘的当前位置 */
                    val chipCenterCurrentPx = chipStartOffsetPx + (chipSizePx / 2)

                    /**
                     * 计算需要的滚动距离：让 chip 中心移动到容器中心
                     * scrollOffset 表示相对于当前滚动位置的额外偏移
                     */
                    val targetCenterPx = containerWidthPx / 2
                    val scrollOffset = chipCenterCurrentPx - targetCenterPx

                    Pair(currentStructureIndex, scrollOffset)
                } else {
                    Pair(currentStructureIndex, 0)
                }
            }

            // 在UI线程执行滚动操作
            if (scrollData.second != 0) {
                // 执行平滑滚动动画
                listState.animateScrollToItem(
                    index = scrollData.first,
                    scrollOffset = scrollData.second
                )
            } else {
                // 如果目标 chip 不在可视区域内，先滚动到大致位置再重新计算
                listState.scrollToItem(scrollData.first)
            }
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
            itemsIndexed(
                structures,
                key = { _, structure -> structure.startTime }) { index, structure ->
                val expandedWidthPx = expandedChipWidthsPx[index]

                SongStructureChip(
                    structure = structure,
                    isCurrent = currentTime in structure.startTime..structure.endTime,
                    onClick = { onSeekTo(structure.startTime) },
                    baseColor = baseColor,
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
                                        chipNaturalWidthsPx = chipNaturalWidthsPx + (index to width)
                                    }
                                }
                            }
                        )
                )
            }
        }
    }
    Trace.endSection()
}

/**
 * 歌曲结构芯片按钮
 *
 * @param structure 歌曲结构段落
 * @param isCurrent 是否为当前播放的段落
 * @param onClick 点击回调
 * @param modifier 修饰符
 * @param baseColor 基础颜色，用于生成渐变背景
 */
@Composable
fun SongStructureChip(
    structure: SongStructure,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {

    /** 使用渐变色方案，匹配 NowPlayingCard 的视觉效果 */
    val chipBaseColor = if (isCurrent) {
        MaterialTheme.colorScheme.primary
    } else {
        baseColor
    }

    val brush = Brush.verticalGradient(
        colors = listOf(
            chipBaseColor.copy(alpha = 0.8f),
            chipBaseColor.copy(alpha = 0.95f)
        )
    )

    val contentColor = if (isCurrent) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    /** 纯音乐类型（只显示音符符号，不显示文字和时间） */
    val isInstrumentalType = structure.type in listOf(
        SongStructureType.INTERLUDE,
        SongStructureType.INTRO_INST,
        SongStructureType.OUTRO_INST
    )

    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .height(56.dp), // 固定高度，让所有芯片保持一致
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent // 背景由内部 Box 的 background 处理
    ) {
        Box(
            modifier = Modifier
                .background(brush)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // 垂直居中
            ) {
                // 纯音乐类型只显示音符符号，其他显示结构标签和时间
                if (isInstrumentalType) {
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

                    // 副文本：时间范围（仅非纯音乐类型显示）
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
    Trace.endSection()
}