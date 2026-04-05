package androidx.compose.material3

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
 * @param interactionSource the [MutableInteractionSource] for observing interactions
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
    interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
    amplitude: Float = 1f,
    wavelength: Dp = WavySliderDefaults.Wavelength,
    waveSpeed: Dp = WavySliderDefaults.WaveSpeed,
    waveAmplitude: Dp = WavySliderDefaults.WaveAmplitude,
    thumbWidth: Dp = WavySliderDefaults.ThumbSize.width,
    thumbHeight: Dp = WavySliderDefaults.ThumbSize.height,
    attractionRadius: Float = 0.03f, // 3% of track length
) {
    val scope = rememberCoroutineScope()
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
    
    var sliderValue by remember { mutableFloatStateOf(state.value) }
    var availableTrackWidth by remember { mutableFloatStateOf(0f) }
    
    // Update internal value and state when external value changes
    LaunchedEffect(value) {
        val coercedValue = value.fastCoerceIn(0f, 1f)
        if (sliderValue != coercedValue) {
            sliderValue = coercedValue
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
                            val newValue = (sliderValue - delta).fastCoerceIn(0f, 1f)
                            sliderValue = newValue
                            state.value = newValue
                            onValueChange(newValue)
                            true
                        }
                        Key.DirectionRight, Key.DirectionDown -> {
                            val newValue = (sliderValue + delta).fastCoerceIn(0f, 1f)
                            sliderValue = newValue
                            state.value = newValue
                            onValueChange(newValue)
                            true
                        }
                        Key.MoveHome -> {
                            sliderValue = 0f
                            state.value = 0f
                            onValueChange(0f)
                            true
                        }
                        Key.MoveEnd -> {
                            sliderValue = 1f
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
    
    // Touch/drag handling
    val dragModifier = if (enabled) {
        Modifier.pointerInput(state, availableTrackWidth) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                var lastX = down.position.x
                
                // Calculate initial value from touch position (absolute positioning)
                // Thumb center moves from (thumbSideGapPx + thumbWidthPx/2) to (trackWidth - thumbSideGapPx - thumbWidthPx/2)
                // Position 0 in value space corresponds to (thumbSideGapPx + thumbWidthPx/2) in pixel space
                val touchOffset = lastX - (thumbSideGapPx + thumbWidthPx / 2)
                val relativeX = touchOffset.coerceIn(0f, availableTrackWidth)
                val initialValue = (relativeX / availableTrackWidth).fastCoerceIn(0f, 1f)
                sliderValue = initialValue
                state.value = initialValue
                onValueChange(initialValue)
                
                horizontalDrag(down.id) { change ->
                    val currentX = change.position.x
                    // Use absolute positioning instead of relative delta
                    val touchOffset = currentX - (thumbSideGapPx + thumbWidthPx / 2)
                    val relativeX = touchOffset.coerceIn(0f, availableTrackWidth)
                    val newValue = (relativeX / availableTrackWidth).fastCoerceIn(0f, 1f)
                    sliderValue = newValue
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
                    sliderValue = newValue
                    state.value = newValue
                    onValueChange(newValue)
                    true
                }
                contentDescription = "Wavy Slider, value $sliderValue"
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
            thumbSideGapPx = thumbSideGapPx,
            onTrackWidthCalculated = { availableWidth ->
                availableTrackWidth = availableWidth
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
    thumbSideGapPx: Float,
    onTrackWidthCalculated: (Float) -> Unit = {},
) {
    val density = LocalDensity.current
    val trackHeightPx = with(density) { WavySliderDefaults.TrackHeight.toPx() }
    
    // MD3 standard (XS size): 6dp gap on each side of thumb
    val thumbSideGapPx = with(density) { WavySliderDefaults.ThumbTrackGapSize.toPx() }
    
    // Calculate canvas height to accommodate both track and thumb
    // Thumb needs enough vertical space to be fully visible
    val canvasHeightDp = maxOf(WavySliderDefaults.TrackHeight * 2, with(density) { thumbHeightPx.toDp() })
    
    // Animate wave movement over time with continuous phase tracking
    val timeOffsetAnimatable = remember { androidx.compose.animation.core.Animatable(0f) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(waveSpeedPx, wavelengthPx) {
        if (waveSpeedPx > 0) {
            // Calculate animation duration for one complete cycle
            val durationMillis = (1000 * wavelengthPx / waveSpeedPx).toInt()
            
            // Continuously animate
            while (true) {
                // Animate to next cycle
                timeOffsetAnimatable.animateTo(
                    targetValue = timeOffsetAnimatable.value + (2 * PI).toFloat(),
                    animationSpec = tween(
                        durationMillis = durationMillis,
                        easing = LinearEasing
                    )
                )
                // Snap back to keep values in reasonable range
                timeOffsetAnimatable.snapTo(timeOffsetAnimatable.value % (2 * PI).toFloat())
            }
        } else {
            // Cancel any ongoing animation when stopped
            timeOffsetAnimatable.stop()
        }
    }
    
    val timeOffset = timeOffsetAnimatable.value
    
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
        // Wave ends so that the round cap is exactly thumbSideGapPx away from thumb
        val wavyTrackHeightPx = trackHeightPx - with(density) { 6.dp.toPx() }
        val wavyEndX = thumbCenterX - thumbWidthPx / 2 - wavyTrackHeightPx / 2 - thumbSideGapPx
        drawWavyTrack(
            color = colors.activeTrackColor(enabled),
            startX = 0f,
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
            size = androidx.compose.ui.geometry.Size(scaledThumbWidth, scaledThumbHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(scaledThumbHeight / 2, scaledThumbHeight / 2)
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
    
    path.moveTo(startX, centerY)
    
    var x = startX
    while (x < endX) {
        val waveOffset = ((x - startX) / wavelengthPx * 2 * PI + timeOffset).toFloat()
        val y = centerY + sin(waveOffset) * actualAmplitude
        
        if (x == startX) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
        x += 2f
    }
    
    // Draw the wave path with round cap for smooth ends
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = trackHeight,
            cap = StrokeCap.Round
        )
    )
    
    // Draw a circle at the end point to create a smooth rounded end
    if (endX > startX) {
        val endWaveOffset = ((endX - startX) / wavelengthPx * 2 * PI + timeOffset).toFloat()
        val endY = centerY + sin(endWaveOffset) * actualAmplitude
        
        drawCircle(
            color = color,
            radius = trackHeight / 2,
            center = Offset(endX, endY)
        )
    }
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
