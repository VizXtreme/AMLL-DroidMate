package androidx.compose.material3

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
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
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.semantics
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
    
    // Create state first
    val state = rememberWavySliderState(
        initialValue = value.fastCoerceIn(0f, 1f),
        customSteps = customSteps,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        attractionRadius = attractionRadius
    )
    
    var sliderValue by remember { mutableFloatStateOf(state.value) }
    
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
        Modifier.pointerInput(state) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                var lastX = down.position.x
                
                // Set initial value based on touch position but don't notify yet
                val initialValue = state.setValueFromPixel(lastX)
                sliderValue = initialValue
                // Don't call onValueChange here - only update internal state
                
                horizontalDrag(down.id) { change ->
                    val currentX = change.position.x
                    val delta = currentX - lastX
                    lastX = currentX
                    val newValue = state.drag(delta)
                    sliderValue = newValue
                    // Only update internal state, don't notify during drag
                    change.consume()
                }
                
                // Only notify when drag is finished
                onValueChange(sliderValue)
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
                minHeight = WavySliderDefaults.TrackHeight
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
            thumbWidthPx = with(density) { thumbWidth.toPx() },
            thumbHeightPx = with(density) { thumbHeight.toPx() }
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
) {
    val density = LocalDensity.current
    val trackHeightPx = with(density) { WavySliderDefaults.TrackHeight.toPx() }
    
    // MD3 standard: 6dp gap on each side of thumb
    val thumbSideGapPx = with(density) { 6.dp.toPx() }
    
    // Calculate canvas height to accommodate both track and thumb
    // Thumb needs enough vertical space to be fully visible
    val canvasHeightDp = maxOf(WavySliderDefaults.TrackHeight * 2, with(density) { thumbHeightPx.toDp() })
    
    // Animate wave movement over time (only if waveSpeed > 0)
    val timeOffset = if (waveSpeedPx > 0) {
        val infiniteTransition = rememberInfiniteTransition(label = "wave")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (1000 * wavelengthPx / waveSpeedPx).toInt(),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "waveTimeOffset"
        ).value
    } else {
        // No animation when waveSpeed is 0 - keep wave static
        0f
    }
    
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(canvasHeightDp)
    ) {
        val trackWidth = size.width
        val centerY = size.height / 2
        
        // Update state with actual track dimensions for gesture handling
        // Available width excludes thumb width and side gaps (6dp each side per MD3 spec)
        state.updateDimensions(trackWidth - thumbWidthPx - thumbSideGapPx * 2, trackHeightPx)
        
        // Calculate thumb center position
        // Thumb center moves from (thumbSideGapPx + thumbWidthPx/2) to (trackWidth - thumbSideGapPx - thumbWidthPx/2)
        val availableWidth = trackWidth - thumbWidthPx - thumbSideGapPx * 2
        val thumbCenterX = thumbSideGapPx + thumbWidthPx / 2 + availableWidth * state.value
        
        // Draw inactive track (starts from thumb center + gap)
        drawRoundRect(
            color = colors.inactiveTrackColor(enabled),
            topLeft = Offset(thumbCenterX + thumbSideGapPx, centerY - trackHeightPx / 2),
            size = Size(trackWidth - thumbCenterX - thumbSideGapPx * 2, trackHeightPx),
            cornerRadius = CornerRadius(trackHeightPx / 2)
        )
        // Draw active track with wave (MD3 standard: 3dp amplitude, 40dp wavelength)
        // Wave ends at thumb center - gap
        drawWavyTrack(
            color = colors.activeTrackColor(enabled),
            startX = 0f,
            endX = thumbCenterX - thumbSideGapPx,
            centerY = centerY,
            trackHeight = trackHeightPx,
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
                trackWidth = trackWidth,
                centerY = centerY,
                trackHeight = trackHeightPx,
                currentValue = state.value,
                customSteps = state.customSteps,
                attractionRadius = state.attractionRadius
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
    attractionRadius: Float = 0.03f
) {
    val markerRadius = trackHeight
    
    for (step in customSteps) {
        val x = step * trackWidth
        val isNearCurrent = abs(step - currentValue) < attractionRadius
        
        drawCircle(
            color = if (isNearCurrent) stepColor else stepColor.copy(alpha = 0.5f),
            radius = if (isNearCurrent) markerRadius * 1.5f else markerRadius,
            center = Offset(x, centerY)
        )
    }
}
