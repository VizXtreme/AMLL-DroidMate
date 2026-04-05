/*
 * Copyright 2024 The DroidMate Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.material3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * Contains the default values used for [WavySlider].
 */
@Stable
object WavySliderDefaults {

    /**
     * Default thumb size for the wavy slider.
     * MD3 standard (XS size): width=4dp, height=44dp
     */
    val ThumbSize: DpSize = DpSize(4.dp, 44.dp)

    /**
     * Default track height for the wavy slider.
     * MD3 standard (XS size): 16dp
     */
    val TrackHeight: Dp = 16.dp

    /**
     * Default wavelength for the wavy effect.
     * MD3 standard: 40dp
     */
    val Wavelength: Dp = 40.dp

    /**
     * Default wave amplitude.
     * MD3 standard: 3dp (scaled for proper wave rendering)
     */
    val WaveAmplitude: Dp = 3.dp

    /**
     * Default wave speed (1 wavelength per second).
     */
    val WaveSpeed: Dp = Wavelength

    /**
     * Default snap threshold for soft snap-to-step behavior.
     */
    const val SNAP_THRESHOLD = 0.05f

    /**
     * The gap between the thumb and the track.
     * MD3 standard (XS size): 6dp on each side
     */
    internal val ThumbTrackGapSize: Dp = 6.dp

    /**
     * The corner size for the track's inside corners.
     * MD3 standard (XS size): 8dp
     */
    internal val TrackInsideCornerSize: Dp = 8.dp

    /**
     * Creates a [WavySliderColors] with default colors.
     */
    @Composable
    fun colors(): WavySliderColors = defaultWavySliderColors()

    /**
     * Creates a [WavySliderColors] with custom colors.
     */
    @Composable
    fun colors(
        thumbColor: Color = Color.Unspecified,
        activeTrackColor: Color = Color.Unspecified,
        inactiveTrackColor: Color = Color.Unspecified,
        stepColor: Color = Color.Unspecified,
        disabledThumbColor: Color = Color.Unspecified,
        disabledActiveTrackColor: Color = Color.Unspecified,
        disabledInactiveTrackColor: Color = Color.Unspecified,
        disabledStepColor: Color = Color.Unspecified,
    ): WavySliderColors {
        return WavySliderColors(
            thumbColor = if (thumbColor != Color.Unspecified) thumbColor else MaterialTheme.colorScheme.primary,
            activeTrackColor = if (activeTrackColor != Color.Unspecified) activeTrackColor else MaterialTheme.colorScheme.primary,
            inactiveTrackColor = if (inactiveTrackColor != Color.Unspecified) inactiveTrackColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            stepColor = if (stepColor != Color.Unspecified) stepColor else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledThumbColor = if (disabledThumbColor != Color.Unspecified) disabledThumbColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledActiveTrackColor = if (disabledActiveTrackColor != Color.Unspecified) disabledActiveTrackColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledInactiveTrackColor = if (disabledInactiveTrackColor != Color.Unspecified) disabledInactiveTrackColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledStepColor = if (disabledStepColor != Color.Unspecified) disabledStepColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f),
        )
    }
}

/**
 * Alignment line used to communicate the track's corner size from the track to the parent layout.
 * This is essential for precise thumb positioning.
 */
internal val TrackCornerSizeAlignmentLine = VerticalAlignmentLine { min, max -> min }

/**
 * Represents the colors used by [WavySlider] in different states.
 */
@Stable
class WavySliderColors(
    val thumbColor: Color,
    val activeTrackColor: Color,
    val inactiveTrackColor: Color,
    val stepColor: Color,
    val disabledThumbColor: Color,
    val disabledActiveTrackColor: Color,
    val disabledInactiveTrackColor: Color,
    val disabledStepColor: Color,
) {
    internal fun thumbColor(enabled: Boolean): Color {
        return if (enabled) thumbColor else disabledThumbColor
    }

    internal fun activeTrackColor(enabled: Boolean): Color {
        return if (enabled) activeTrackColor else disabledActiveTrackColor
    }

    internal fun inactiveTrackColor(enabled: Boolean): Color {
        return if (enabled) inactiveTrackColor else disabledInactiveTrackColor
    }

    internal fun stepColor(enabled: Boolean): Color {
        return if (enabled) stepColor else disabledStepColor
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as WavySliderColors

        if (thumbColor != other.thumbColor) return false
        if (activeTrackColor != other.activeTrackColor) return false
        if (inactiveTrackColor != other.inactiveTrackColor) return false
        if (stepColor != other.stepColor) return false
        if (disabledThumbColor != other.disabledThumbColor) return false
        if (disabledActiveTrackColor != other.disabledActiveTrackColor) return false
        if (disabledInactiveTrackColor != other.disabledInactiveTrackColor) return false
        if (disabledStepColor != other.disabledStepColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thumbColor.hashCode()
        result = 31 * result + activeTrackColor.hashCode()
        result = 31 * result + inactiveTrackColor.hashCode()
        result = 31 * result + stepColor.hashCode()
        result = 31 * result + disabledThumbColor.hashCode()
        result = 31 * result + disabledActiveTrackColor.hashCode()
        result = 31 * result + disabledInactiveTrackColor.hashCode()
        result = 31 * result + disabledStepColor.hashCode()
        return result
    }
}

@Composable
internal fun defaultWavySliderColors(): WavySliderColors = WavySliderColors(
    thumbColor = MaterialTheme.colorScheme.primary,
    activeTrackColor = MaterialTheme.colorScheme.primary,
    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    stepColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    disabledThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    disabledActiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    disabledInactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    disabledStepColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f),
)
