package com.amll.droidmate.websocket

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * AMLL WebSocket V1 二进制协议工具类
 * 
 * V1 协议使用二进制格式，通过 u16 Magic Number 区分消息类型
 */
object WsProtocolV1Helper {
    
    // ==================== Magic Numbers ====================
    
    const val MAGIC_PING = 0x0000
    const val MAGIC_PONG = 0x0001
    const val MAGIC_SET_MUSIC_INFO = 0x0002
    const val MAGIC_SET_ALBUM_COVER_URI = 0x0003
    const val MAGIC_SET_ALBUM_COVER_DATA = 0x0004
    const val MAGIC_ON_PLAY_PROGRESS = 0x0005
    const val MAGIC_ON_VOLUME_CHANGED = 0x0006
    const val MAGIC_ON_PAUSED = 0x0007
    const val MAGIC_ON_RESUMED = 0x0008
    const val MAGIC_ON_AUDIO_DATA = 0x0009
    const val MAGIC_SET_LYRIC = 0x000A
    const val MAGIC_SET_LYRIC_FROM_TTML = 0x000B
    const val MAGIC_PAUSE = 0x000C
    const val MAGIC_RESUME = 0x000D
    const val MAGIC_FORWARD_SONG = 0x000E
    const val MAGIC_BACKWARD_SONG = 0x000F
    const val MAGIC_SET_VOLUME = 0x0010
    const val MAGIC_SEEK_PLAY_PROGRESS = 0x0011
    
    // ==================== 编码方法 ====================
    
    /**
     * 创建 Ping 消息
     */
    fun encodePing(): ByteArray {
        return ByteBuffer.allocate(2).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putShort(MAGIC_PING.toShort())
        }.array()
    }
    
    /**
     * 创建 Pong 消息
     */
    fun encodePong(): ByteArray {
        return ByteBuffer.allocate(2).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putShort(MAGIC_PONG.toShort())
        }.array()
    }
    
    /**
     * 创建播放进度消息
     * @param progressMs 播放进度（毫秒）
     */
    fun encodePlayProgress(progressMs: Long): ByteArray {
        return ByteBuffer.allocate(10).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putShort(MAGIC_ON_PLAY_PROGRESS.toShort())
            putLong(progressMs)
        }.array()
    }
    
    /**
     * 创建暂停消息
     */
    fun encodePaused(): ByteArray {
        return ByteBuffer.allocate(2).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putShort(MAGIC_ON_PAUSED.toShort())
        }.array()
    }
    
    /**
     * 创建恢复播放消息
     */
    fun encodeResumed(): ByteArray {
        return ByteBuffer.allocate(2).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putShort(MAGIC_ON_RESUMED.toShort())
        }.array()
    }
    
    /**
     * 创建音量变化消息
     * @param volume 音量值 (0.0-1.0)
     */
    fun encodeVolumeChanged(volume: Double): ByteArray {
        return ByteBuffer.allocate(10).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putShort(MAGIC_ON_VOLUME_CHANGED.toShort())
            putDouble(volume)
        }.array()
    }
    
    /**
     * 创建暂停命令
     */
    fun encodePause(): ByteArray {
        return ByteBuffer.allocate(2).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putShort(MAGIC_PAUSE.toShort())
        }.array()
    }
    
    /**
     * 创建恢复命令
     */
    fun encodeResume(): ByteArray {
        return ByteBuffer.allocate(2).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putShort(MAGIC_RESUME.toShort())
        }.array()
    }
    
    /**
     * 创建下一首命令
     */
    fun encodeForwardSong(): ByteArray {
        return ByteBuffer.allocate(2).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putShort(MAGIC_FORWARD_SONG.toShort())
        }.array()
    }
    
    /**
     * 创建上一首命令
     */
    fun encodeBackwardSong(): ByteArray {
        return ByteBuffer.allocate(2).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putShort(MAGIC_BACKWARD_SONG.toShort())
        }.array()
    }
    
    /**
     * 创建设置音量命令
     * @param volume 音量值 (0.0-1.0)
     */
    fun encodeSetVolume(volume: Double): ByteArray {
        return ByteBuffer.allocate(10).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putShort(MAGIC_SET_VOLUME.toShort())
            putDouble(volume)
        }.array()
    }
    
    /**
     * 创建跳转进度命令
     * @param progressMs 进度位置（毫秒）
     */
    fun encodeSeekPlayProgress(progressMs: Long): ByteArray {
        return ByteBuffer.allocate(10).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putShort(MAGIC_SEEK_PLAY_PROGRESS.toShort())
            putLong(progressMs)
        }.array()
    }
    
    /**
     * 写入 NullString（以 \0 结尾的字符串）
     */
    private fun ByteArrayOutputStream.writeNullString(str: String) {
        write(str.toByteArray(Charsets.UTF_8))
        write(0) // null terminator
    }
    
    /**
     * 从字节数组读取 NullString
     */
    fun readNullString(data: ByteArray, offset: Int): Pair<String, Int> {
        var end = offset
        while (end < data.size && data[end] != 0.toByte()) {
            end++
        }
        val str = String(data, offset, end - offset, Charsets.UTF_8)
        return Pair(str, end + 1) // skip null terminator
    }
    
    // ==================== 解码方法 ====================
    
    /**
     * 解析消息类型
     * @return Magic Number
     */
    fun parseMessageType(data: ByteArray): Int {
        require(data.size >= 2) { "消息数据太短" }
        return ByteBuffer.wrap(data, 0, 2).apply {
            order(ByteOrder.LITTLE_ENDIAN)
        }.short.toInt() and 0xFFFF
    }
    
    /**
     * 解析播放进度消息
     * @return 播放进度（毫秒）
     */
    fun parsePlayProgress(data: ByteArray): Long {
        require(data.size >= 10) { "进度消息数据长度不足" }
        return ByteBuffer.wrap(data, 2, 8).apply {
            order(ByteOrder.LITTLE_ENDIAN)
        }.long
    }
    
    /**
     * 解析音量消息
     * @return 音量值
     */
    fun parseVolume(data: ByteArray): Double {
        require(data.size >= 10) { "音量消息数据长度不足" }
        return ByteBuffer.wrap(data, 2, 8).apply {
            order(ByteOrder.LITTLE_ENDIAN)
        }.double
    }
}
