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

package androidx.compose.material3.samples

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.WavySlider

/**
 * Basic WavySlider sample without custom steps.
 *
 * @sample androidx.compose.material3.samples.WavySliderSample
 */
@Composable
fun WavySliderSample() {
    var sliderPosition by remember { mutableFloatStateOf(0.5f) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Basic Wavy Slider: $sliderPosition")

        WavySlider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * WavySlider with custom step positions sample.
 *
 * This sample demonstrates how to create a slider with non-uniformly distributed steps.
 *
 * @sample androidx.compose.material3.samples.WavySliderWithCustomStepsSample
 */
@Composable
fun WavySliderWithCustomStepsSample() {
    var sliderPosition by remember { mutableFloatStateOf(0.0f) }

    // Custom step positions (normalized 0.0 to 1.0)
    // These steps are NOT evenly distributed
    val customSteps = listOf(0.0f, 0.15f, 0.3f, 0.5f, 0.7f, 0.85f, 1.0f)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Wavy Slider with Custom Steps: ${String.format("%.2f", sliderPosition)}")
        Text("Steps at: ${customSteps.joinToString(", ")}")

        Spacer(modifier = Modifier.height(16.dp))

        WavySlider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            customSteps = customSteps,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * WavySlider with different wave parameters sample.
 *
 * This sample shows how to customize the wave amplitude and wavelength.
 *
 * @sample androidx.compose.material3.samples.WavySliderCustomWaveSample
 */
@Composable
fun WavySliderCustomWaveSample() {
    var sliderPosition by remember { mutableFloatStateOf(0.5f) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Wavy Slider - High Amplitude: $sliderPosition")

        WavySlider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            amplitude = 1.0f, // Maximum wave amplitude
            wavelength = 24.dp, // Longer wavelength
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Wavy Slider - Low Amplitude: $sliderPosition")

        WavySlider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            amplitude = 0.3f, // Subtle wave effect
            wavelength = 12.dp, // Shorter wavelength
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * WavySlider with disabled state sample.
 *
 * @sample androidx.compose.material3.samples.WavySliderDisabledSample
 */
@Composable
fun WavySliderDisabledSample() {
    var sliderPosition by remember { mutableFloatStateOf(0.5f) }
    val customSteps = listOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Enabled Slider")

        WavySlider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            customSteps = customSteps,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Disabled Slider")

        WavySlider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            customSteps = customSteps,
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * WavySlider with soft snap behavior sample.
 *
 * Demonstrates the soft snap-to-step feature where the slider gently snaps
 * to nearby steps when dragged close to them.
 *
 * @sample androidx.compose.material3.samples.WavySliderSoftSnapSample
 */
@Composable
fun WavySliderSoftSnapSample() {
    var sliderPosition by remember { mutableFloatStateOf(0.0f) }

    // Create steps at specific positions
    val customSteps = listOf(0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Soft Snap Slider: ${String.format("%.2f", sliderPosition)}")
        Text("Drag near a step to see soft snap behavior")

        Spacer(modifier = Modifier.height(16.dp))

        WavySlider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            customSteps = customSteps,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * WavySlider with callback sample.
 *
 * Shows how to use onValueChangeFinished callback.
 *
 * @sample androidx.compose.material3.samples.WavySliderWithCallbackSample
 */
@Composable
fun WavySliderWithCallbackSample() {
    var sliderPosition by remember { mutableFloatStateOf(0.5f) }
    var finalPosition by remember { mutableFloatStateOf(0.5f) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Current: ${String.format("%.2f", sliderPosition)}")
        Text("Final: ${String.format("%.2f", finalPosition)}")

        Spacer(modifier = Modifier.height(16.dp))

        WavySlider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = { finalPosition = sliderPosition },
            modifier = Modifier.fillMaxWidth()
        )
    }
}


