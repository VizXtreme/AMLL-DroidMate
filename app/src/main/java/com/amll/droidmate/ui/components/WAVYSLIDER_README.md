# WavySlider 波浪形滑动组件

WavySlider 是一个基于 Material Design 3 风格的创新型滑动组件，它结合了波浪形视觉效果和自定义步进点功能。

## 特性

- ✨ **波浪形轨道**: 独特的波浪视觉效果，支持自定义振幅和波长
- 🎯 **自定义步进点**: 支持任意位置的步进点，不必均匀分布
- 🖱️ **完整交互**: 支持拖动、点击和键盘控制
- ♿ **无障碍支持**: 完整的语义支持和键盘导航
- 🎨 **Material 3**: 遵循 Material Design 3 设计规范
- 🌊 **软吸附效果**: 接近步进点时自动平滑吸附

## 快速开始

### 基础用法

```kotlin
@Composable
fun BasicSliderExample() {
    var value by remember { mutableFloatStateOf(0.5f) }
    
    WavySlider(
        value = value,
        onValueChange = { value = it },
        modifier = Modifier.fillMaxWidth()
    )
}
```

### 自定义步进点位置

```kotlin
@Composable
fun CustomStepsSliderExample() {
    var value by remember { mutableFloatStateOf(0.0f) }
    
    // 自定义步进点位置（归一化坐标 0.0 - 1.0）
    val customSteps = listOf(0.0f, 0.15f, 0.3f, 0.5f, 0.7f, 0.85f, 1.0f)
    
    WavySlider(
        value = value,
        onValueChange = { value = it },
        customSteps = customSteps,
        modifier = Modifier.fillMaxWidth()
    )
}
```

### 自定义波浪参数

```kotlin
@Composable
fun CustomWaveSliderExample() {
    var value by remember { mutableFloatStateOf(0.5f) }
    
    WavySlider(
        value = value,
        onValueChange = { value = it },
        amplitude = 1.0f,      // 最大振幅
        wavelength = 24.dp,     // 较长波长
        modifier = Modifier.fillMaxWidth()
    )
}
```

### 使用回调

```kotlin
@Composable
fun SliderWithCallbackExample() {
    var currentValue by remember { mutableFloatStateOf(0.5f) }
    var finalValue by remember { mutableFloatStateOf(0.5f) }
    
    WavySlider(
        value = currentValue,
        onValueChange = { currentValue = it },
        onValueChangeFinished = { 
            finalValue = currentValue
            // 在这里处理值变化完成后的逻辑
        },
        modifier = Modifier.fillMaxWidth()
    )
}
```

## API 参考

### WavySlider

```kotlin
@ExperimentalMaterial3ExpressiveApi
@Composable
fun WavySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    customSteps: List<Float> = emptyList(),
    onValueChangeFinished: (() -> Unit)? = null,
    colors: WavySliderColors = WavySliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    amplitude: Float = 1f,
    wavelength: Dp = WavySliderDefaults.Wavelength,
    waveSpeed: Dp = WavySliderDefaults.WaveSpeed,
)
```

#### 参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `value` | Float | - | 当前滑块值（0.0 - 1.0） |
| `onValueChange` | (Float) -> Unit | - | 值变化回调 |
| `modifier` | Modifier | Modifier | 修饰符 |
| `enabled` | Boolean | true | 是否启用 |
| `customSteps` | List<Float> | emptyList() | 自定义步进点位置列表 |
| `onValueChangeFinished` | () -> Unit | null | 拖动结束回调 |
| `colors` | WavySliderColors | WavySliderDefaults.colors() | 颜色配置 |
| `interactionSource` | MutableInteractionSource | remember { ... } | 交互源 |
| `amplitude` | Float | 1f | 波浪振幅（0.0 - 1.0） |
| `wavelength` | Dp | WavySliderDefaults.Wavelength | 波长 |
| `waveSpeed` | Dp | WavySliderDefaults.WaveSpeed | 波浪移动速度 |

### WavySliderState

使用状态对象管理滑块：

```kotlin
val state = rememberWavySliderState(
    initialValue = 0.5f,
    customSteps = listOf(0.0f, 0.5f, 1.0f),
    onValueChange = { /* 处理值变化 */ },
    onValueChangeFinished = { /* 拖动结束处理 */ }
)

WavySlider(
    state = state,
    modifier = Modifier.fillMaxWidth()
)
```

### WavySliderDefaults

提供默认配置：

```kotlin
object WavySliderDefaults {
    val ThumbSize: DpSize          // 滑块尺寸
    val TrackHeight: Dp            // 轨道高度
    val Wavelength: Dp             // 默认波长
    const val SnapThreshold: Float // 吸附阈值
    
    @Composable
    fun colors(): WavySliderColors // 创建颜色配置
}
```

### WavySliderColors

自定义颜色：

```kotlin
WavySliderDefaults.colors(
    thumbColor = Color.Blue,
    activeTrackColor = Color.Blue,
    inactiveTrackColor = Color.Gray,
    stepColor = Color.Blue.copy(alpha = 0.5f)
)
```

## 高级用法

### 非均匀分布的步进点

```kotlin
// 创建特殊间距的步进点
val irregularSteps = listOf(
    0.0f,   // 起点
    0.1f,   // 10% 位置
    0.3f,   // 30% 位置
    0.5f,   // 中点
    0.8f,   // 80% 位置
    1.0f    // 终点
)

WavySlider(
    value = sliderValue,
    onValueChange = { sliderValue = it },
    customSteps = irregularSteps
)
```

### 动态步进点

```kotlin
@Composable
fun DynamicStepsSlider() {
    var value by remember { mutableFloatStateOf(0.0f) }
    var stepCount by remember { mutableIntStateOf(5) }
    
    // 根据条件动态生成步进点
    val dynamicSteps = remember(stepCount) {
        (0..stepCount).map { it.toFloat() / stepCount }
    }
    
    Column {
        WavySlider(
            value = value,
            onValueChange = { value = it },
            customSteps = dynamicSteps
        )
        
        // 控制步进点数量
        Slider(
            value = stepCount.toFloat(),
            onValueChange = { stepCount = it.toInt() },
            valueRange = 2f..10f,
            steps = 8
        )
    }
}
```

## 软吸附机制

WavySlider 实现了智能软吸附系统：

1. **接近检测**: 当滑块距离步进点小于阈值时触发
2. **平滑过渡**: 使用线性插值实现平滑吸附效果
3. **拖动结束**: 释放时自动对齐到最近的步进点

```kotlin
// 吸附阈值可通过状态对象调整
val state = rememberWavySliderState(
    initialValue = 0.5f,
    customSteps = listOf(0.0f, 0.5f, 1.0f)
)
state.snapThreshold = 0.08f // 增加吸附范围
```

## 手势支持

- **拖动**: 水平拖动滑块
- **点击**: 点击轨道直接跳转到指定位置
- **键盘**: 方向键微调，Home/End 键跳转到起点/终点

## 无障碍特性

- ✅ 完整的 TalkBack/VoiceOver 支持
- ✅ 键盘导航
- ✅ 语义描述
- ✅ 禁用状态正确传达

## 注意事项

1. **值范围**: 所有值和步进点位置必须在 0.0 到 1.0 范围内
2. **性能**: 避免在 compositions 中频繁创建新的 customSteps 列表
3. **波速**: waveSpeed 默认匹配 wavelength，实现每秒移动一个波长的效果

## 示例代码

更多示例请参考：
- [WavySliderSamples.kt](app/src/main/java/com/amll/droidmate/ui/components/samples/WavySliderSamples.kt)
- [WavySliderDemoActivity.kt](app/src/main/java/com/amll/droidmate/ui/WavySliderDemoActivity.kt)

## 许可证

Copyright 2024 The DroidMate Project

Licensed under the Apache License, Version 2.0.
