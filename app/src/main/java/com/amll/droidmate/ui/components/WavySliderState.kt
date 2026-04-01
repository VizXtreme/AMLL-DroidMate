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
     * Snap threshold for soft snap-to-step behavior (normalized distance).
     */
    internal val snapThreshold: Float = WavySliderDefaults.SNAP_THRESHOLD

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
     * Snaps the given value to the nearest step if within the snap threshold.
     *
     * @param value The value to snap
     * @return The snapped value, or the original value if not within threshold
     */
    internal fun snapToNearestStep(value: Float): Float {
        if (customSteps.isEmpty()) return value

        var nearestStep = value
        var minDistance = snapThreshold

        for (step in customSteps) {
            val distance = kotlin.math.abs(value - step)
            if (distance < minDistance) {
                minDistance = distance
                nearestStep = step
            }
        }

        // Apply soft snap - lerp between current and nearest based on distance
        return if (minDistance < snapThreshold) {
            val snapFactor = 1f - (minDistance / snapThreshold)
            value + (nearestStep - value) * snapFactor
        } else {
            value
        }
    }

    /**
     * Updates the value with drag delta.
     *
     * @param delta The drag delta in pixels
     * @return The new value after applying delta
     */
    internal fun drag(delta: Float): Float {
        if (trackWidth <= 0) return value

        val currentValue = value
        val pixelValue = currentValue * trackWidth
        val newPixelValue = (pixelValue + delta).coerceIn(0f, trackWidth)
        val newValue = newPixelValue / trackWidth

        // Apply soft snap to nearest step
        val snappedValue = snapToNearestStep(newValue)
        val coercedValue = snappedValue.fastCoerceIn(0f, 1f)

        if (coercedValue != currentValue) {
            value = coercedValue
            onValueChange(coercedValue)
        }

        return coercedValue
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
        val snappedValue = snapToNearestStep(newValue)
        val coercedValue = snappedValue.fastCoerceIn(0f, 1f)

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
        // Snap to nearest step on drag end
        if (customSteps.isNotEmpty()) {
            var nearestStep = value
            var minDistance = Float.MAX_VALUE

            for (step in customSteps) {
                val distance = kotlin.math.abs(value - step)
                if (distance < minDistance) {
                    minDistance = distance
                    nearestStep = step
                }
            }

            if (minDistance < snapThreshold * 2) {
                value = nearestStep
                onValueChange(nearestStep)
            }
        }

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
): WavySliderState {
    return androidx.compose.runtime.remember {
        WavySliderState(
            initialValue = initialValue,
            customSteps = customSteps,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
        )
    }.also { state ->
        state.updateCallbacks(onValueChange, onValueChangeFinished)
        state.customSteps.clear()
        state.customSteps.addAll(customSteps.map { it.coerceIn(0f, 1f) }.sorted())
    }
}
