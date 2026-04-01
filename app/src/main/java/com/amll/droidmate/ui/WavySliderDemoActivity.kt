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

package com.amll.droidmate.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.WavySlider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Demo activity showcasing the [WavySlider] component.
 */
class WavySliderDemoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WavySliderDemoContent()
                }
            }
        }
    }
}

@Composable
private fun WavySliderDemoContent() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Wavy Slider Demo",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Basic slider
        var sliderValue1 by remember { mutableFloatStateOf(0.5f) }
        Text("Basic Slider: ${String.format("%.2f", sliderValue1)}")
        WavySlider(
            value = sliderValue1,
            onValueChange = { sliderValue1 = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Spacer(modifier = Modifier.height(32.dp))

        // Slider with different wave parameters
        var sliderValue2 by remember { mutableFloatStateOf(0.5f) }
        Text("High Amplitude Wave")
        WavySlider(
            value = sliderValue2,
            onValueChange = { sliderValue2 = it },
            amplitude = 1.0f,
            wavelength = 24.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        var sliderValue3 by remember { mutableFloatStateOf(0.5f) }
        Text("Low Amplitude Wave")
        WavySlider(
            value = sliderValue3,
            onValueChange = { sliderValue3 = it },
            amplitude = 0.3f,
            wavelength = 12.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Disabled slider
        Text("Disabled Slider")
        WavySlider(
            value = 0.5f,
            onValueChange = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
