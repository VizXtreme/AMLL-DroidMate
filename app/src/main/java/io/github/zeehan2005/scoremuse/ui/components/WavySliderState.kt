package io.github.zeehan2005.scoremuse.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlin.math.abs

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
    /**
     * Attraction radius for snap-to-step behavior.
     * When the finger is within this distance of a step, the thumb will snap to it.
     */
    internal val attractionRadius: Float = 0.03f,
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
            val distance = abs(value - step)
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

    companion object
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
    return remember {
        WavySliderState(
            initialValue = initialValue,
            customSteps = customSteps,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            attractionRadius = attractionRadius,
        )
    }.also { state ->
        state.customSteps.clear()
        state.customSteps.addAll(customSteps.map { it.coerceIn(0f, 1f) }.sorted())
    }
}
