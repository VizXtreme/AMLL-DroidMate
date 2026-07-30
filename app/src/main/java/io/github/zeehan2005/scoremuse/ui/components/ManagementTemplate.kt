package io.github.zeehan2005.scoremuse.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler

// ========================
// 通用管理模板 — 配置
// ========================

/**
 * 通用管理页面 [ManagementPage] 的全部可定制配置。
 *
 * @param T 列表条目类型
 * @property title 页面标题（显示在 TopAppBar 上）
 * @property itemKey 从条目中提取唯一标识的 lambda（用于 LazyColumn key 和选择集）
 * @property searchPredicate 搜索过滤逻辑；(条目, 用户输入的小写文本) → 是否匹配
 * @property searchPlaceholder 搜索框占位文本
 * @property emptyText 列表为空时显示的文本
 * @property countText 条目数量显示模板（默认 "共 {count} 条"）
 * @property headerContent 搜索栏上方可选的内容（如提示文字），会在条目计数之前渲染
 * @property clearDialogTitle 清空确认对话框标题
 * @property clearDialogMessage 清空确认对话框消息
 * @property selectable 是否支持选择模式（默认 false）
 * @property selectionBackBehaviour 选择模式下按返回键的行为
 * @property topBarActions 非选择模式下 TopAppBar 右侧额外操作按钮的 compose
 * @property selectionActions 选择模式下 TopAppBar 右侧操作按钮的 compose；不提供时使用默认的全选+删除
 * @property fabData 可选的浮动操作按钮配置（图标 + 点击回调）
 * @property onClearAll 清空全部的回调；提供后会在 TopAppBar 操作区显示"删除全部"图标 + 弹出确认对话框
 * @property onDeleteSelected 删除选中条目的回调（传入选中 ID 集合）；提供后会在选择模式下显示删除按钮
 * @property renderItem 渲染单条列表项的 composable，接收 (item, isSelectionMode, isSelected, onSelect)
 */
class ManagementConfig<T> @PublishedApi internal constructor(
    val title: String,
    val itemKey: (T) -> String,
    val searchPredicate: (T, String) -> Boolean,
    val searchPlaceholder: String,
    val emptyText: String,
    val countText: (count: Int) -> String,
    val headerContent: @Composable (() -> Unit)?,
    val clearDialogTitle: String,
    val clearDialogMessage: String,
    val selectable: Boolean,
    val selectionBackBehaviour: SelectionBackBehaviour,
    val topBarActions: @Composable ((showClearDialog: () -> Unit) -> Unit)?,
    val selectionActions: @Composable (
        isAllSelected: Boolean,
        onToggleSelectAll: () -> Unit,
        onDeleteSelected: () -> Unit,
    ) -> Unit?,
    val fabData: FabData?,
    val onClearAll: (() -> Unit)?,
    val onDeleteSelected: ((Set<String>) -> Unit)?,
    val renderItem: @Composable (
        item: T,
        isSelectionMode: Boolean,
        isSelected: Boolean,
        onSelect: () -> Unit,
    ) -> Unit,
) {
    companion object {
        /**
         * 使用 DSL 构建 [ManagementConfig]。
         *
         * @param title 页面标题（必填）
         * @param itemKey 从条目中提取唯一标识的 lambda（必填）
         * @param block DSL 配置块
         */
        fun <T> build(
            title: String,
            itemKey: (T) -> String,
            block: Builder<T>.() -> Unit,
        ): ManagementConfig<T> = Builder<T>().apply {
            this.title = title
            this.itemKey = itemKey
            block()
        }.build()
    }
}

/** 浮动操作按钮配置 */
data class FabData(
    val icon: ImageVector,
    val onClick: () -> Unit,
    val contentDescription: String = "添加",
)

// ================
// DSL Builder
// ================

/** 选择模式下返回键关闭选择的方式 */
enum class SelectionBackBehaviour {
    /** 仅取消选择，不触发返回 */
    CLEAR,
    /** 直接调用 onBack */
    FINISH,
}

class Builder<T> {
    lateinit var title: String
    lateinit var itemKey: (T) -> String

    var searchPredicate: ((T, String) -> Boolean)? = null
    var searchPlaceholder: String = "搜索"
    var emptyText: String = "当前没有数据。"
    var countText: (count: Int) -> String = { "共 $it 条" }
    var headerContent: (@Composable () -> Unit)? = null
    var clearDialogTitle: String = "清空数据"
    var clearDialogMessage: String = "确认删除全部数据吗？"
    var selectable: Boolean = false
    var selectionBackBehaviour: SelectionBackBehaviour = SelectionBackBehaviour.CLEAR
    var topBarActions: (@Composable ((showClearDialog: () -> Unit) -> Unit))? = null
    var selectionActions: (@Composable (
        isAllSelected: Boolean,
        onToggleSelectAll: () -> Unit,
        onDeleteSelected: () -> Unit,
    ) -> Unit)? = null
    var fabData: FabData? = null
    var onClearAll: (() -> Unit)? = null
    var onDeleteSelected: ((Set<String>) -> Unit)? = null
    lateinit var renderItem: @Composable (
        item: T,
        isSelectionMode: Boolean,
        isSelected: Boolean,
        onSelect: () -> Unit,
    ) -> Unit

    fun build(): ManagementConfig<T> {
        val effectiveSearch: (T, String) -> Boolean = searchPredicate ?: { _, _ -> true }
        val effectiveSelectionActions: @Composable (
            Boolean, () -> Unit, () -> Unit,
        ) -> Unit = selectionActions ?: { isAll, toggleSelect, deleteSelected ->
            FilledIconButton(
                onClick = toggleSelect,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Icon(
                    imageVector = if (isAll) Icons.Default.Deselect else Icons.Default.SelectAll,
                    contentDescription = if (isAll) "取消全选" else "全选",
                )
            }
            if (onDeleteSelected != null) {
                FilledIconButton(
                    onClick = deleteSelected,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "删除")
                }
            }
        }

        return ManagementConfig(
            title = title,
            itemKey = itemKey,
            searchPredicate = effectiveSearch,
            searchPlaceholder = searchPlaceholder,
            emptyText = emptyText,
            countText = countText,
            headerContent = headerContent,
            clearDialogTitle = clearDialogTitle,
            clearDialogMessage = clearDialogMessage,
            selectable = selectable,
            selectionBackBehaviour = selectionBackBehaviour,
            topBarActions = topBarActions,
            selectionActions = effectiveSelectionActions,
            fabData = fabData,
            onClearAll = onClearAll,
            onDeleteSelected = onDeleteSelected,
            renderItem = renderItem,
        )
    }
}

// ======================
// 通用管理模板 — 页面
// ======================

/**
 * 通用管理页面。
 *
 * 封装了折叠 TopAppBar、搜索栏、条目计数、LazyColumn、选择模式（多选+批量删除）、
 * 清空确认对话框、FAB 等常见管理页面的基础设施。
 *
 * 业务页面只需通过 [ManagementConfig] 注入数据和渲染逻辑：
 *
 * ```
 * @Composable
 * fun MyPage(entries: List<MyEntry>, onBack: () -> Unit) {
 *     ManagementPage(
 *         entries = entries,
 *         config = ManagementConfig.build(title = "管理XX", itemKey = { it.id }) {
 *             searchPredicate = { entry, query -> entry.name.contains(query) }
 *             selectable = true
 *             onClearAll = { /* ... */ }
 *             onDeleteSelected = { ids -> /* ... */ }
 *             renderItem = { entry, selMode, sel, onSelect ->
 *                 MyCard(entry, selMode, sel, onSelect)
 *             }
 *         },
 *         onBack = onBack,
 *     )
 * }
 * ```
 *
 * @param entries 条目列表（由外部维护，页面只做过滤和展示）
 * @param config 页面配置
 * @param onBack 返回回调
 * @param modifier 额外修饰符
 */
@Composable
fun <T> ManagementPage(
    entries: List<T>,
    config: ManagementConfig<T>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // ===== 内部状态 =====
    var query by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    // ===== 过滤 =====
    val displayEntries = remember(entries, query) {
        if (query.isBlank()) entries
        else entries.filter { config.searchPredicate(it, query.trim().lowercase()) }
    }

    // ===== 选择辅助 =====
    val isAllSelected = selectedIds.size == displayEntries.size && displayEntries.isNotEmpty()
    val handleToggleSelectAll = {
        selectedIds = if (isAllSelected) emptySet()
        else displayEntries.map { config.itemKey(it) }.toSet()
    }
    val handleDeleteSelected = {
        config.onDeleteSelected?.invoke(selectedIds)
        selectedIds = emptySet()
        isSelectionMode = false
    }

    BackHandler(isSelectionMode) {
        when (config.selectionBackBehaviour) {
            SelectionBackBehaviour.CLEAR -> {
                isSelectionMode = false
                selectedIds = emptySet()
            }
            SelectionBackBehaviour.FINISH -> onBack()
        }
    }

    // ===== 滚动 =====
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(config.title) },
                navigationIcon = {
                    FilledIconButton(
                        onClick = if (isSelectionMode) {
                            {
                                isSelectionMode = false
                                selectedIds = emptySet()
                            }
                        } else {
                            onBack
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isSelectionMode) "取消" else "返回",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
                actions = {
                    if (isSelectionMode) {
                        config.selectionActions(isAllSelected, handleToggleSelectAll, handleDeleteSelected)
                    } else {
                        // 选择模式入口（放在自定义操作之前）
                        if (config.selectable) {
                            FilledIconButton(
                                onClick = { isSelectionMode = true },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                            ) {
                                Icon(Icons.Default.ChecklistRtl, contentDescription = "选择")
                            }
                        }
                        config.topBarActions?.invoke({ showClearDialog = true })
                    }
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.statusBarsPadding(),
            )
        },
        floatingActionButton = {
            config.fabData?.let { fab ->
                FloatingActionButton(
                    onClick = fab.onClick,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                ) {
                    Icon(fab.icon, contentDescription = fab.contentDescription)
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // 头部内容（搜索栏上方）
            config.headerContent?.invoke()

            // 搜索栏
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(config.searchPlaceholder) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "搜索")
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = CircleShape,
                singleLine = true,
            )

            // 计数
            Text(
                text = config.countText(displayEntries.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )

            // 空状态
            if (displayEntries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = config.emptyText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    )
                }
            }

            // 列表
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(displayEntries, key = { config.itemKey(it) }) { entry ->
                    val id = config.itemKey(entry)
                    config.renderItem(
                        entry,
                        isSelectionMode && config.selectable,
                        selectedIds.contains(id),
                        {
                            if (config.selectable) {
                                selectedIds = if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
                            }
                        },
                    )
                }
            }
        }
    }

    // ===== 清空对话框 =====
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(config.clearDialogTitle) },
            text = { Text(config.clearDialogMessage) },
            containerColor = MaterialTheme.colorScheme.background,
            confirmButton = {
                TextButton(onClick = {
                    config.onClearAll?.invoke()
                    showClearDialog = false
                }) {
                    Text("删除全部")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("取消")
                }
            },
        )
    }
}

// ==============================
// 便捷低层级组件（可选使用）
// ==============================

/**
 * 标准列表卡片容器。
 * 按项目现有样式统一 Card + surfaceVariant 背景 + 内边距排版。
 */
@Composable
fun ManagementCard(
    modifier: Modifier = Modifier,
    showCheckbox: Boolean = false,
    isChecked: Boolean = false,
    onCheckedChange: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(modifier = Modifier.weight(1f)) { content() }
            if (showCheckbox && onCheckedChange != null) {
                Checkbox(checked = isChecked, onCheckedChange = { onCheckedChange() })
            }
        }
    }
}
