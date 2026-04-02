package androidx.compose.material3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.util.fastCoerceIn

/**
 * State object for [WavySlider] component.
 *
 * Manages the slider value, custom step positions, and handles snap-to-step logic.
 *
 * @param initialValue The initial value of the slider
 * @param customSteps List of normalized step positions (0.0 to 1.0)
 * @param onValueChange Callback invoked when the slider value changes
 * @param onValueChangeFinished Callback invoked when the user finishes dragging
 */
@Stable
class WavySliderState(
    initialValue: Float,
    customSteps: List<Float> = emptyList(),
    val onValueChange: (Float) -> Unit = {},
    val onValueChangeFinished: (() -> Unit)? = null,
    attractionRadius: Float = 0.03f,
) {
    private var _value: Float by mutableFloatStateOf(initialValue.coerceIn(0f, 1f))
    
    /**
     * Current slider value. Must be in the range [0f, 1f].
     */
    var value: Float
        get() = _value
        internal set(value) {
            _value = value
        }

    /**
     * Attraction radius for snap-to-step behavior.
     * When the finger is within this distance of a step, the thumb will snap to it.
     */
    internal val attractionRadius: Float = attractionRadius

    /**
     * List of normalized custom step positions (0.0 to 1.0).
     */
    val customSteps: SnapshotStateList<Float> = mutableStateListOf<Float>().apply {
        addAll(customSteps.map { it.coerceIn(0f, 1f) }.sorted())
    }
    
    /**
     * Updates the callbacks.
     */
    internal fun updateCallbacks(
        newOnValueChange: (Float) -> Unit,
        newOnValueChangeFinished: (() -> Unit)?
    ) {
        // Callbacks are updated via rememberWavySliderState
    }
    
    /**
     * Internal width of the track in pixels.
     */
    internal var trackWidth: Float by mutableFloatStateOf(0f)

    /**
     * Internal height of the track in pixels.
     */
    internal var trackHeight: Float by mutableFloatStateOf(0f)

    /**
     * Internal thumb width in pixels.
     */
    internal var thumbWidth: Float by mutableFloatStateOf(0f)

    /**
     * Internal thumb height in pixels.
     */
    internal var thumbHeight: Float by mutableFloatStateOf(0f)

    /**
     * Total width of the slider including thumb and track.
     */
    internal var totalWidth: Float by mutableFloatStateOf(0f)

    /**
     * Total height of the slider.
     */
    internal var totalHeight: Float by mutableFloatStateOf(0f)

    /**
     * Whether the layout direction is RTL (Right-to-Left).
     */
    internal var isRtl: Boolean by mutableStateOf(false)

    /**
     * Returns the sorted list of tick fractions including endpoints (0.0 and 1.0).
     */
    internal val tickFractions: List<Float>
        get() {
            val steps = customSteps.filter { it in 0f..1f }
            return buildList {
                add(0.0f)
                addAll(steps)
                add(1.0f)
            }.distinct().sorted()
        }

    /**
     * Finds the nearest step within the attraction radius.
     *
     * @param value The current position to check
     * @return Pair of (nearestStep, distance) if within attraction radius, null otherwise
     */
    internal fun findNearestStepInAttractionRange(value: Float): Pair<Float, Float>? {
        if (customSteps.isEmpty()) return null

        var nearestStep: Float = value
        var minDistance = attractionRadius

        for (step in customSteps) {
            val distance = kotlin.math.abs(value - step)
            if (distance < minDistance) {
                minDistance = distance
                nearestStep = step
            }
        }

        return if (minDistance < attractionRadius) {
            Pair(nearestStep, minDistance)
        } else {
            null
        }
    }

    /**
     * Current thumb scale factor for visual feedback when near a step.
     */
    internal var thumbScale: Float by mutableFloatStateOf(1f)
        private set

    /**
     * Updates the value with drag delta.
     * Binary snap logic: either fully snapped to step OR fully following finger.
     *
     * @param delta The drag delta in pixels
     * @return The new value after applying delta
     */
    internal fun drag(delta: Float): Float {
        if (trackWidth <= 0) return value

        val currentValue = value
        // Convert current value to pixel position
        val currentPixelValue = currentValue * trackWidth
        // Apply delta and clamp to valid track range
        val newPixelValue = (currentPixelValue + delta).coerceIn(0f, trackWidth)
        // Convert back to normalized value (0.0 to 1.0)
        val newValue = newPixelValue / trackWidth

        // Update thumb scale: enlarge when near a step during drag
        val stepInfo = findNearestStepInAttractionRange(newValue)
        thumbScale = if (stepInfo != null) {
            1.3f // Fixed scale when in attraction range
        } else {
            1f
        }

        if (newValue != currentValue) {
            value = newValue
            onValueChange(newValue)
        }

        return newValue
    }

    /**
     * Sets the value from a pixel position.
     *
     * @param pixelX The pixel position along the track
     * @return The new value
     */
    internal fun setValueFromPixel(pixelX: Float): Float {
        if (trackWidth <= 0) return value

        val newValue = (pixelX / trackWidth).fastCoerceIn(0f, 1f)
        
        // Check if initial touch is within attraction range of any step
        val stepInfo = findNearestStepInAttractionRange(newValue)
        val finalValue = stepInfo?.first ?: newValue
        val coercedValue = finalValue.fastCoerceIn(0f, 1f)

        if (coercedValue != value) {
            value = coercedValue
            onValueChange(coercedValue)
        }

        return coercedValue
    }

    /**
     * Called when drag gesture ends.
     */
    internal fun onDragStopped() {
        // Hard snap to nearest step on drag end for precise positioning
        if (customSteps.isNotEmpty()) {
            val stepInfo = findNearestStepInAttractionRange(value)
            
            // Only snap if within attraction range
            if (stepInfo != null) {
                value = stepInfo.first
                onValueChange(stepInfo.first)
            }
        }
        
        // Always reset thumb scale to normal after release
        thumbScale = 1f

        onValueChangeFinished?.invoke()
    }

    /**
     * Updates the dimensions of the slider components.
     */
    internal fun updateDimensions(newTrackWidth: Float, newTrackHeight: Float) {
        trackWidth = newTrackWidth
        trackHeight = newTrackHeight
    }

    /**
     * Updates the thumb dimensions.
     */
    internal fun updateThumbDimensions(newThumbWidth: Float, newThumbHeight: Float) {
        thumbWidth = newThumbWidth
        thumbHeight = newThumbHeight
    }

    /**
     * Updates the RTL layout direction.
     */
    internal fun updateLayoutDirection(isRtl: Boolean) {
        this.isRtl = isRtl
    }

    companion object {
        /**
         * The default [snapThreshold] used by [WavySliderState].
         */
        const val DefaultSnapThreshold = 0.05f
    }
}

/**
 * Creates a [WavySliderState] that is remembered across compositions.
 */
@Composable
fun rememberWavySliderState(
    initialValue: Float = 0f,
    customSteps: List<Float> = emptyList(),
    onValueChange: (Float) -> Unit = {},
    onValueChangeFinished: (() -> Unit)? = null,
    attractionRadius: Float = 0.03f,
): WavySliderState {
    return androidx.compose.runtime.remember {
        WavySliderState(
            initialValue = initialValue,
            customSteps = customSteps,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            attractionRadius = attractionRadius,
        )
    }.also { state ->
        state.updateCallbacks(onValueChange, onValueChangeFinished)
        state.customSteps.clear()
        state.customSteps.addAll(customSteps.map { it.coerceIn(0f, 1f) }.sorted())
    }
}
