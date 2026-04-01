package com.amll.droidmate.util

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager

/**
 * 音频设备辅助工具
 * 
 * 这个工具类用于检测和识别当前系统使用的音频输出设备。
 * 主要用途是为不同设备应用不同的歌词时间偏移（例如蓝牙耳机通常有延迟）。
 * 
 * 支持的设备类型：
 * - 蓝牙设备（A2DP/SCO）
 * - 有线耳机/头戴式耳机
 * - USB 音频设备
 * - 内置扬声器
 */
object AudioDeviceHelper {
    /**
     * 获取当前音频输出设备的可读名称
     * 
     * 这个方法会检测系统当前使用的音频输出设备，并返回一个人类可读的名称。
     * 主要用于应用基于设备的歌词时间偏移策略。
     * 
     * 检测优先级：
     * 1. 蓝牙设备（最高优先级，因为通常有明显延迟）
     * 2. 有线设备（耳机、USB 等）
     * 3. 内置扬声器（默认）
     * 
     * @param context Android 上下文
     * @return 设备名称，例如 "Bluetooth (Sony WH-1000XM4)"、"Wired"、"Speaker"
     */
    fun getCurrentOutputDeviceName(context: Context): String {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            ?: return "Unknown"  // 无法获取 AudioManager 时返回 Unknown

        // getDevices 仅在 API 23+ 支持，旧版本返回通用标签
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return "Speaker"
        }

        val devices = try {
            audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        } catch (_: Exception) {
            emptyArray<AudioDeviceInfo>()
        }

        // 优先检测蓝牙设备（因为通常有明显的音频延迟）
        val bluetooth = devices.firstOrNull {
            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
        }
        if (bluetooth != null) {
            val name = bluetooth.productName?.toString()?.takeIf { it.isNotBlank() }
            return if (name != null) "Bluetooth ($name)" else "Bluetooth"
        }

        // 检测有线设备（耳机、USB 等）
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

        // 默认使用内置扬声器
        val speaker = devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
        if (speaker != null) {
            return "Speaker"
        }

        // 回退到第一个可用的输出设备
        return devices.firstOrNull()?.productName?.toString()?.takeIf { it.isNotBlank() } ?: "Unknown"
    }
}
