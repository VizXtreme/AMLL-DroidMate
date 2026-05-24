package io.github.zeehan2005.scoremuse.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sin


/**
 * [Material Design wavy slider](https://m3.material.io/components/sliders/overview)
 *
 * A wavy-styled slider that allows users to make selections from a range of values with custom
 * step positions.
 *
 * @param value current value of the slider, must be in the range [0f, 1f]
 * @param onValueChange callback in which value should be updated
 * @param modifier the [Modifier] to be applied to this slider
 * @param enabled controls the enabled state of this slider
 * @param customSteps list of normalized step positions (0.0 to 1.0)
 * @param onValueChangeFinished called when value change has ended
 * @param colors [WavySliderColors] for different states
 * @param amplitude the wave's amplitude (0.0 to 1.0)
 * @param wavelength the length of a wave
 * @param waveSpeed the speed in which the wave will move (DP per second)
 * @param thumbWidth custom width for the thumb, defaults to MD3 standard
 * @param thumbHeight custom height for the thumb, defaults to MD3 standard
 */
@Composable
fun WavySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    customSteps: List<Float> = emptyList(),
    onValueChangeFinished: (() -> Unit)? = null,
    colors: WavySliderColors = WavySliderDefaults.colors(),
    amplitude: Float = 1f,
    wavelength: Dp = WavySliderDefaults.Wavelength,
    waveSpeed: Dp = WavySliderDefaults.WaveSpeed,
    waveAmplitude: Dp = WavySliderDefaults.WaveAmplitude,
    thumbWidth: Dp = WavySliderDefaults.ThumbSize.width,
    thumbHeight: Dp = WavySliderDefaults.ThumbSize.height,
    attractionRadius: Float = 0.03f, // 3% of track length
) {
    val density = LocalDensity.current
    val wavelengthPx = with(density) { wavelength.toPx() }
    val waveSpeedPx = with(density) { waveSpeed.toPx() }
    val thumbWidthPx = with(density) { thumbWidth.toPx() }
    val thumbHeightPx = with(density) { thumbHeight.toPx() }
    val thumbSideGapPx = with(density) { WavySliderDefaults.ThumbTrackGapSize.toPx() }
    
    // Create state first
    val state = rememberWavySliderState(
        initialValue = value.fastCoerceIn(0f, 1f),
        customSteps = customSteps,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        attractionRadius = attractionRadius
    )
    
    // Update internal value and state when external value changes
    LaunchedEffect(value) {
        val coercedValue = value.fastCoerceIn(0f, 1f)
        if (state.value != coercedValue) {
            state.value = coercedValue
        }
    }
    
    // Keyboard handling
    val keyboardModifier = if (enabled) {
        Modifier.onKeyEvent { keyEvent ->
            when (keyEvent.type) {
                KeyEventType.KeyDown -> {
                    val delta = 0.01f
                    when (keyEvent.key) {
                        Key.DirectionLeft, Key.DirectionUp -> {
                            val newValue = (state.value - delta).fastCoerceIn(0f, 1f)
                            state.value = newValue
                            onValueChange(newValue)
                            true
                        }
                        Key.DirectionRight, Key.DirectionDown -> {
                            val newValue = (state.value + delta).fastCoerceIn(0f, 1f)
                            state.value = newValue
                            onValueChange(newValue)
                            true
                        }
                        Key.MoveHome -> {
                            state.value = 0f
                            onValueChange(0f)
                            true
                        }
                        Key.MoveEnd -> {
                            state.value = 1f
                            onValueChange(1f)
                            true
                        }
                        else -> false
                    }
                }
                KeyEventType.KeyUp -> {
                    when (keyEvent.key) {
                        Key.DirectionLeft, Key.DirectionRight,
                        Key.DirectionUp, Key.DirectionDown,
                        Key.MoveHome, Key.MoveEnd -> {
                            onValueChangeFinished?.invoke()
                            true
                        }
                        else -> false
                    }
                }
                else -> false
            }
        }
    } else {
        Modifier
    }
    
    val hoverIconModifier = if (enabled) {
        Modifier.pointerHoverIcon(PointerIcon.Hand)
    } else {
        Modifier
    }
    
    val availableTrackWidth = remember { mutableFloatStateOf(0f) }
    
    // Touch/drag handling
    val dragModifier = if (enabled) {
        Modifier.pointerInput(state, availableTrackWidth.floatValue) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val lastX = down.position.x
                
                // Calculate initial value from touch position (absolute positioning)
                // Thumb center moves from (thumbSideGapPx + thumbWidthPx/2) to (trackWidth - thumbSideGapPx - thumbWidthPx/2)
                // Position 0 in value space corresponds to (thumbSideGapPx + thumbWidthPx/2) in pixel space
                val touchOffset = lastX - (thumbSideGapPx + thumbWidthPx / 2)
                val relativeX = touchOffset.coerceIn(0f, availableTrackWidth.floatValue)
                val initialValue = (relativeX / availableTrackWidth.floatValue).fastCoerceIn(0f, 1f)
                state.value = initialValue
                onValueChange(initialValue)
                
                horizontalDrag(down.id) { change ->
                    val currentX = change.position.x
                    // Use absolute positioning instead of relative delta
                    val touchOffset = currentX - (thumbSideGapPx + thumbWidthPx / 2)
                    val relativeX = touchOffset.coerceIn(0f, availableTrackWidth.floatValue)
                    val newValue = (relativeX / availableTrackWidth.floatValue).fastCoerceIn(0f, 1f)
                    state.value = newValue
                    onValueChange(newValue)
                    change.consume()
                }
                
                // Notify when drag is finished
                state.onDragStopped()
                onValueChangeFinished?.invoke()
            }
        }
    } else {
        Modifier
    }
    
    Box(
        modifier = modifier
            .requiredSizeIn(
                minWidth = 160.dp,
                minHeight = thumbHeight
            )
            .then(keyboardModifier)
            .then(hoverIconModifier)
            .then(dragModifier)
            .semantics(mergeDescendants = true) {
                if (!enabled) {
                    disabled()
                }
                setProgress {
                    val newValue = it.fastCoerceIn(0f, 1f)
                    state.value = newValue
                    onValueChange(newValue)
                    true
                }
                contentDescription = "Wavy Slider, value ${state.value}"
            }
    ) {
        WavySliderDrawing(
            state = state,
            colors = colors,
            enabled = enabled,
            amplitude = amplitude,
            wavelengthPx = wavelengthPx,
            waveSpeedPx = waveSpeedPx,
            waveAmplitudePx = with(density) { waveAmplitude.toPx() },
            thumbWidthPx = thumbWidthPx,
            thumbHeightPx = thumbHeightPx,
            onTrackWidthCalculated = { availableWidth ->
                availableTrackWidth.floatValue = availableWidth
            }
        )
    }
}

@Composable
private fun WavySliderDrawing(
    state: WavySliderState,
    colors: WavySliderColors,
    enabled: Boolean,
    amplitude: Float,
    wavelengthPx: Float,
    waveSpeedPx: Float,
    waveAmplitudePx: Float,
    thumbWidthPx: Float,
    thumbHeightPx: Float,
    onTrackWidthCalculated: (Float) -> Unit = {},
) {
    val density = LocalDensity.current
    val trackHeightPx = with(density) { WavySliderDefaults.TrackHeight.toPx() }
    
    // MD3 standard (XS size): 6dp gap on each side of thumb
    val thumbSideGapPx = with(density) { WavySliderDefaults.ThumbTrackGapSize.toPx() }
    
    // Calculate canvas height to accommodate both track and thumb
    // Thumb needs enough vertical space to be fully visible
    val canvasHeightDp = maxOf(WavySliderDefaults.TrackHeight * 2, with(density) { thumbHeightPx.toDp() })
    
    // 保存当前的时间偏移，用于暂停时保持波浪相位
    val savedTimeOffset = remember { mutableFloatStateOf(0f) }
    val animationTimeOffset = remember { mutableFloatStateOf(0f) }
    
    // 使用 LaunchedEffect 管理动画
    LaunchedEffect(waveSpeedPx) {
        if (waveSpeedPx > 0) {
            // 播放时，启动动画
            var currentOffset: Float
            val duration = (1000 * wavelengthPx / waveSpeedPx).toInt()
            
            while (true) {
                val startTime = System.currentTimeMillis()
                while (System.currentTimeMillis() - startTime < duration) {
                    val progress = (System.currentTimeMillis() - startTime).toFloat() / duration
                    currentOffset = savedTimeOffset.floatValue + progress * (2 * PI).toFloat()
                    animationTimeOffset.floatValue = currentOffset
                    // 优化：使用合理的延迟值，平衡性能和流畅度
                    delay(20) // 约50fps，在各种设备上都能流畅运行
                }
                savedTimeOffset.floatValue = 0f // 重置相位，避免无限增长
            }
        }
    }
    
    // 优化：减少不必要的计算，只在需要时更新时间偏移
    val timeOffset by remember(waveSpeedPx, animationTimeOffset.floatValue) {
        derivedStateOf {
            if (waveSpeedPx > 0) {
                // 播放时，使用动画值
                animationTimeOffset.floatValue
            } else {
                // 暂停时，保存当前动画值并使用
                savedTimeOffset.floatValue = animationTimeOffset.floatValue % (2 * PI).toFloat()
                savedTimeOffset.floatValue
            }
        }
    }
    
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(canvasHeightDp)
    ) {
        val trackWidth = size.width
        val centerY = size.height / 2
        
        // Update state with actual track dimensions for gesture handling
        // Track width is the full canvas width (thumb moves within this space)
        state.updateDimensions(trackWidth, trackHeightPx)
        state.updateThumbDimensions(thumbWidthPx, thumbHeightPx)
        
        // Calculate available track width for thumb movement and notify parent
        val availableWidth = trackWidth - thumbWidthPx - thumbSideGapPx * 2
        onTrackWidthCalculated(availableWidth)
        
        // Calculate thumb center position
        // Thumb center moves from (thumbSideGapPx + thumbWidthPx/2) to (trackWidth - thumbSideGapPx - thumbWidthPx/2)
        val thumbCenterX = thumbSideGapPx + thumbWidthPx / 2 + availableWidth * state.value
        
        // Draw inactive track (starts from thumb center + gap)
        drawRoundRect(
            color = colors.inactiveTrackColor(enabled),
            topLeft = Offset(thumbCenterX + thumbSideGapPx, centerY - trackHeightPx / 2),
            size = Size(trackWidth - thumbCenterX - thumbSideGapPx * 2, trackHeightPx),
            cornerRadius = CornerRadius(trackHeightPx / 2)
        )
        // Draw active track with wave (wave width = inactive track width - 6dp)
        // Wave starts and ends so that the round cap is exactly thumbSideGapPx away from the edges/thumb
        val wavyTrackHeightPx = trackHeightPx - with(density) { 6.dp.toPx() }
        val wavyStartX = thumbSideGapPx + wavyTrackHeightPx / 2
        val wavyEndX = thumbCenterX - thumbWidthPx / 2 - wavyTrackHeightPx / 2 - thumbSideGapPx
        
        drawWavyTrack(
            color = colors.activeTrackColor(enabled),
            startX = wavyStartX,
            endX = wavyEndX,
            centerY = centerY,
            trackHeight = wavyTrackHeightPx,
            amplitude = amplitude,
            wavelengthPx = wavelengthPx,
            timeOffset = timeOffset,
            waveAmplitudePx = waveAmplitudePx
        )
        
        // Draw thumb with dynamic scale for visual feedback when near a step
        val scaledThumbWidth = thumbWidthPx * state.thumbScale
        val scaledThumbHeight = thumbHeightPx * state.thumbScale
        
        drawRoundRect(
            color = colors.thumbColor(enabled),
            topLeft = Offset(thumbCenterX - scaledThumbWidth / 2, centerY - scaledThumbHeight / 2),
            size = Size(scaledThumbWidth, scaledThumbHeight),
            cornerRadius = CornerRadius(scaledThumbHeight / 2, scaledThumbHeight / 2)
        )
        if (state.customSteps.isNotEmpty()) {
            drawStepMarkers(
                stepColor = colors.stepColor(enabled),
                trackWidth = availableWidth,
                centerY = centerY,
                trackHeight = trackHeightPx,
                currentValue = state.value,
                customSteps = state.customSteps,
                attractionRadius = state.attractionRadius,
                offsetX = thumbSideGapPx + thumbWidthPx / 2
            )
        }
    }
}

private fun DrawScope.drawWavyTrack(
    color: Color,
    startX: Float,
    endX: Float,
    centerY: Float,
    trackHeight: Float,
    amplitude: Float,
    wavelengthPx: Float,
    timeOffset: Float,
    waveAmplitudePx: Float
) {
    if (endX <= startX) return
    
    val path = Path()
    // Use MD3 standard amplitude (3dp) scaled by amplitude parameter
    val actualAmplitude = waveAmplitudePx * amplitude.coerceIn(0f, 1f)
    
    // Calculate the step size based on wavelength for better performance
    val stepSize = max(2f, wavelengthPx / 20f) // Reduce number of points
    
    // Start at the first point of the wave
    val firstWaveOffset = (startX / wavelengthPx * 2 * PI + timeOffset).toFloat()
    val firstY = centerY + sin(firstWaveOffset) * actualAmplitude
    path.moveTo(startX, firstY)
    
    var x = startX + stepSize
    while (x < endX) {
        val waveOffset = (x / wavelengthPx * 2 * PI + timeOffset).toFloat()
        val y = centerY + sin(waveOffset) * actualAmplitude
        
        path.lineTo(x, y)
        x += stepSize
    }
    
    // Ensure the path reaches the end point
    val finalWaveOffset = (endX / wavelengthPx * 2 * PI + timeOffset).toFloat()
    val finalY = centerY + sin(finalWaveOffset) * actualAmplitude
    path.lineTo(endX, finalY)
    
    // Draw the wave path with round cap for smooth ends
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = trackHeight,
            cap = StrokeCap.Round
        )
    )
}

private fun DrawScope.drawStepMarkers(
    stepColor: Color,
    trackWidth: Float,
    centerY: Float,
    trackHeight: Float,
    currentValue: Float,
    customSteps: List<Float>,
    attractionRadius: Float = 0.03f,
    offsetX: Float = 0f
) {
    val markerRadius = trackHeight / 2
    
    // Calculate thumb center position for proper step marker positioning
    // Steps should be positioned based on normalized value (0.0 to 1.0)
    for (step in customSteps) {
        // Step position is normalized, multiply by track width and add offset to get pixel position
        val x = step * trackWidth + offsetX
        val isNearCurrent = abs(step - currentValue) < attractionRadius
        
        drawCircle(
            color = if (isNearCurrent) stepColor else stepColor.copy(alpha = 0.5f),
            radius = if (isNearCurrent) markerRadius * 1.5f else markerRadius,
            center = Offset(x, centerY)
        )
    }
}