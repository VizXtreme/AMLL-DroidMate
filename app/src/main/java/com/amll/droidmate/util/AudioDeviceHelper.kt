package com.amll.droidmate.util

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager

/**
 * Utilities for resolving the current audio output device.
 */
object AudioDeviceHelper {
    /**
     * Returns a human-readable name for the currently selected output device.
     *
     * This is used for applying per-device lyric timing offsets.
     */
    fun getCurrentOutputDeviceName(context: Context): String {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            ?: return "Unknown"

        // getDevices is only supported on API 23+, fall back to a generic label otherwise.
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return "Speaker"
        }

        val devices = try {
            audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        } catch (_: Exception) {
            emptyArray<AudioDeviceInfo>()
        }

        // Prioritize Bluetooth devices by type. Use product name when available.
        val bluetooth = devices.firstOrNull {
            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
        }
        if (bluetooth != null) {
            val name = bluetooth.productName?.toString()?.takeIf { it.isNotBlank() }
            return if (name != null) "Bluetooth ($name)" else "Bluetooth"
        }

        // If a wired headset / headphones are plugged in, treat as wired.
        val wired = devices.firstOrNull {
            it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                it.type == AudioDeviceInfo.TYPE_USB_DEVICE ||
                it.type == AudioDeviceInfo.TYPE_USB_HEADSET
        }
        if (wired != null) {
            val name = wired.productName?.toString()?.takeIf { it.isNotBlank() }
            return if (name != null) "Wired ($name)" else "Wired"
        }

        // Default to speaker if available.
        val speaker = devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
        if (speaker != null) {
            return "Speaker"
        }

        // Fall back to first available output device.
        return devices.firstOrNull()?.productName?.toString()?.takeIf { it.isNotBlank() } ?: "Unknown"
    }
}
