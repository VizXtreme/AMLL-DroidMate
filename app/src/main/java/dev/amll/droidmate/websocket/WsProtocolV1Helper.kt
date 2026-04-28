package com.amll.droidmate.websocket

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * AMLL WebSocket V1 二进制协议工具类
 * 
 * V1 协议使用紧凑的二进制格式进行通信，优点是效率高、带宽占用小。
 * 通过 2 字节的 Magic Number（魔术数字）来区分不同的消息类型。
 * 
 * 序列化规则：
 * - 所有数值类型使用小端字节序（Little Endian）：低位字节在前
 * - 字符串使用 NullString 格式：UTF-8 编码 + \0 结尾标记
 * - 线性表（List）使用 Vec<T> 格式：u32 数量 + 元素列表
 * 
 * 消息类型包括：
 * - 心跳消息：Ping/Pong（保持连接活跃）
 * - 音乐信息：设置歌曲、专辑、艺术家信息
 * - 播放控制：播放/暂停/跳转/音量调节
 * - 歌词数据：发送歌词行和逐词信息
 */
object WsProtocolV1Helper {
    
    // ==================== 数据结构定义 ====================
    // 这些是 V1 协议中使用的数据模型
    
    /**
     * 艺术家信息
     * @param id 艺术家唯一标识
     * @param name 艺术家名称
     */
    data class Artist(
        val id: String,
        val name: String
    )
    
    /**
     * 歌词单词（逐字信息）
     * 
     * 用于实现逐字高亮效果，每个单词包含精确的开始和结束时间。
     * @param startTime 开始时间（毫秒）
     * @param endTime 结束时间（毫秒）
     * @param word 歌词文本
     */
    data class LyricWord(
        val startTime: Long,
        val endTime: Long,
        val word: String
    )
    
    /**
     * 歌词行
     * 
     * 代表一行完整的歌词，包含：
     * - 整行的时间范围
     * - 逐词时间信息（用于逐字高亮）
     * - 翻译和音译
     * - 特殊标记（背景音、合唱）
     * 
     * @param startTime 行开始时间（毫秒）
     * @param endTime 行结束时间（毫秒）
     * @param words 逐词列表
     * @param translatedLyric 翻译歌词
     * @param romanLyric 音译歌词
     * @param isBG 是否背景音声
     * @param isDuet 是否合唱
     */
    data class LyricLine(
        val startTime: Long,
        val endTime: Long,
        val words: List<LyricWord>,
        val translatedLyric: String = "",
        val romanLyric: String = "",
        val isBG: Boolean = false,
        val isDuet: Boolean = false
    ) {
        /**
         * 计算属性标记位
         * 
         * 使用位运算将两个布尔值压缩到一个字节中：
         * - bit0 (0b00000001): isBG 标记
         * - bit1 (0b00000010): isDuet 标记
         * 
         * 例如：isBG=true, isDuet=false → flag = 0b00000001 = 1
         * 
         * @return 压缩后的标记字节
         */
        fun getFlag(): Byte {
            var flag: Byte = 0
            if (isBG) flag = flag.toInt().or(0b01).toByte()   // 设置第 0 位
            if (isDuet) flag = flag.toInt().or(0b10).toByte() // 设置第 1 位
            return flag
        }
        
        companion object {
            /**
             * 从 flag 值解析 isBG 和 isDuet
             * 
             * 使用位掩码操作提取特定位的值：
             * - flag and 0b00000001 → 提取第 0 位（isBG）
             * - flag and 0b00000010 → 提取第 1 位（isDuet）
             * 
             * @param flag 标记字节
             * @return Pair(isBG, isDuet)
             */
            fun fromFlag(flag: Byte): Pair<Boolean, Boolean> {
                val isBG = (flag.toInt() and 0b01) != 0
                val isDuet = (flag.toInt() and 0b10) != 0
                return Pair(isBG, isDuet)
            }
        }
    }
    
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
    
    // ==================== 复杂消息编码方法 ====================
    
    /**
     * 创建歌曲信息设置消息 (SetMusicInfo)
     * Magic: 0x0002
     * 
     * 数据结构：
     * - music_id: NullString
     * - music_name: NullString
     * - album_id: NullString
     * - album_name: NullString
     * - artists: Vec<Artist>
     * - duration: u64 (毫秒)
     */
    fun encodeSetMusicInfo(
        musicId: String,
        musicName: String,
        albumId: String,
        albumName: String,
        artists: List<Artist>,
        duration: Long
    ): ByteArray {
        val baos = ByteArrayOutputStream()
        
        // 写入 Magic Number
        baos.write(
            ByteBuffer.allocate(2).apply {
                order(ByteOrder.LITTLE_ENDIAN)
                putShort(MAGIC_SET_MUSIC_INFO.toShort())
            }.array()
        )
        
        // 写入各个字段
        baos.writeNullString(musicId)
        baos.writeNullString(musicName)
        baos.writeNullString(albumId)
        baos.writeNullString(albumName)
        // 写入 artists (Vec<Artist>)
        baos.writeVec(artists) { artist -> this.writeArtist(artist) }
        
        // 写入时长
        baos.write(
            ByteBuffer.allocate(8).apply {
                order(ByteOrder.LITTLE_ENDIAN)
                putLong(duration)
            }.array()
        )
        
        return baos.toByteArray()
    }
    
    /**
     * 创建专辑封面 URI 设置消息 (SetMusicAlbumCoverImageURI)
     * Magic: 0x0003
     * 
     * 数据结构：
     * - img_url: NullString
     */
    fun encodeSetAlbumCoverURI(imgUrl: String): ByteArray {
        val baos = ByteArrayOutputStream()
        
        // 写入 Magic Number
        baos.write(
            ByteBuffer.allocate(2).apply {
                order(ByteOrder.LITTLE_ENDIAN)
                putShort(MAGIC_SET_ALBUM_COVER_URI.toShort())
            }.array()
        )
        
        // 写入图片 URL
        baos.writeNullString(imgUrl)
        
        return baos.toByteArray()
    }
    
    /**
     * 创建 TTML 歌词设置消息 (SetLyricFromTTML)
     * Magic: 0x000B
     * 
     * 数据结构：
     * - data: NullString (TTML 格式的歌词字符串)
     */
    fun encodeSetLyricFromTTML(ttmlContent: String): ByteArray {
        val baos = ByteArrayOutputStream()
        
        // 写入 Magic Number
        baos.write(
            ByteBuffer.allocate(2).apply {
                order(ByteOrder.LITTLE_ENDIAN)
                putShort(MAGIC_SET_LYRIC_FROM_TTML.toShort())
            }.array()
        )
        
        // 写入 TTML 内容
        baos.writeNullString(ttmlContent)
        
        return baos.toByteArray()
    }
    
    /**
     * 创建结构化歌词设置消息 (SetLyric) - 可选功能
     * Magic: 0x000A
     * 
     * 数据结构：
     * - data: Vec<LyricLine>
     */
    fun encodeSetLyric(lines: List<LyricLine>): ByteArray {
        val baos = ByteArrayOutputStream()
        
        // 写入 Magic Number
        baos.write(
            ByteBuffer.allocate(2).apply {
                order(ByteOrder.LITTLE_ENDIAN)
                putShort(MAGIC_SET_LYRIC.toShort())
            }.array()
        )
        
        // 写入歌词行向量
        // 写入歌词行向量
        baos.writeVec(lines) { line -> this.writeLyricLine(line) }
        
        return baos.toByteArray()
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
    
    /**
     * 写入 Vec<T>（先写 u32 数量，再写元素）
     * 用于序列化线性列表数据结构
     */
    private fun <T> ByteArrayOutputStream.writeVec(
        items: List<T>,
        writeElement: ByteArrayOutputStream.(T) -> Unit
    ) {
        // 写入数量 (u32, 小端)
        val size = items.size
        ByteBuffer.allocate(4).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putInt(size)
        }.let { write(it.array()) }
        
        // 写入每个元素
        items.forEach { item -> this.writeElement(item) }
    }
    
    /**
     * 写入 Artist 数据结构
     */
    private fun ByteArrayOutputStream.writeArtist(artist: Artist) {
        writeNullString(artist.id)
        writeNullString(artist.name)
    }
    
    /**
     * 写入 LyricWord 数据结构
     */
    private fun ByteArrayOutputStream.writeLyricWord(word: LyricWord) {
        ByteBuffer.allocate(20).apply {  // 8+8+4(min string len) = 至少 20 字节
            order(ByteOrder.LITTLE_ENDIAN)
            putLong(word.startTime)
            putLong(word.endTime)
        }.let { write(it.array()) }
        writeNullString(word.word)
    }
    
    /**
     * 写入 LyricLine 数据结构
     * 注意：根据 Rust v1.rs 的定义，写入顺序为：
     * start_time, end_time, words(Vec), translated_lyric, roman_lyric, flag
     */
    private fun ByteArrayOutputStream.writeLyricLine(line: LyricLine) {
        // startTime (u64) + endTime (u64)
        ByteBuffer.allocate(16).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putLong(line.startTime)
            putLong(line.endTime)
        }.let { write(it.array()) }
        
        // 写入 words 向量（在 flag 之前！）
        writeVec(line.words) { word -> writeLyricWord(word) }
        
        // 写入翻译歌词和罗马音歌词
        writeNullString(line.translatedLyric)
        writeNullString(line.romanLyric)
        
        // 最后写入 flag
        write(line.getFlag().toInt())
    }
    
    // ==================== 解码方法 ====================
    
    /**
     * 从字节数组读取 Vec<T>
     * @return 读取的元素列表和新的偏移量
     */
    private fun <T> readVec(
        data: ByteArray,
        offset: Int,
        readElement: (ByteArray, Int) -> Pair<T, Int>
    ): Pair<List<T>, Int> {
        require(data.size >= offset + 4) { "Vec 数据长度不足" }
        
        // 读取数量 (u32, 小端)
        val size = ByteBuffer.wrap(data, offset, 4).apply {
            order(ByteOrder.LITTLE_ENDIAN)
        }.int
        
        var currentOffset = offset + 4
        val items = mutableListOf<T>()
        
        // 读取每个元素
        repeat(size) {
            val (item, newOffset) = readElement(data, currentOffset)
            items.add(item)
            currentOffset = newOffset
        }
        
        return Pair(items, currentOffset)
    }
    
    /**
     * 从字节数组读取 Artist
     * @return Artist 对象和新的偏移量
     */
    private fun readArtist(data: ByteArray, offset: Int): Pair<Artist, Int> {
        val (id, offset1) = readNullString(data, offset)
        val (name, offset2) = readNullString(data, offset1)
        return Pair(Artist(id, name), offset2)
    }
    
    /**
     * 从字节数组读取 LyricWord
     * @return LyricWord 对象和新的偏移量
     */
    private fun readLyricWord(data: ByteArray, offset: Int): Pair<LyricWord, Int> {
        require(data.size >= offset + 16) { "LyricWord 数据长度不足" }
        
        val buffer = ByteBuffer.wrap(data, offset, 16).apply {
            order(ByteOrder.LITTLE_ENDIAN)
        }
        
        val startTime = buffer.long
        val endTime = buffer.long
        val (word, newOffset) = readNullString(data, offset + 16)
        
        return Pair(LyricWord(startTime, endTime, word), newOffset)
    }
    
    /**
     * 从字节数组读取 LyricLine
     * 注意：根据 Rust v1.rs 的定义，读取顺序为：
     * start_time, end_time, words(Vec), translated_lyric, roman_lyric, flag
     * @return LyricLine 对象和新的偏移量
     */
    private fun readLyricLine(data: ByteArray, offset: Int): Pair<LyricLine, Int> {
        require(data.size >= offset + 16) { "LyricLine 数据长度不足（至少需要 16 字节用于 startTime+endTime）" }
        
        val buffer = ByteBuffer.wrap(data, offset, 16).apply {
            order(ByteOrder.LITTLE_ENDIAN)
        }
        
        val startTime = buffer.long
        val endTime = buffer.long
        
        var currentOffset = offset + 16
        
        // 读取 words 向量（在 flag 之前！）
        val (words, offsetAfterWords) = readVec(data, currentOffset) { d, o ->
            readLyricWord(d, o)
        }
        currentOffset = offsetAfterWords
        
        // 读取翻译歌词
        val (translatedLyric, offsetAfterTranslated) = readNullString(data, currentOffset)
        currentOffset = offsetAfterTranslated
        
        // 读取罗马音歌词
        val (romanLyric, offsetAfterRoman) = readNullString(data, currentOffset)
        currentOffset = offsetAfterRoman
        
        // 最后读取 flag
        require(data.size > currentOffset) { "flag 数据长度不足" }
        val flag = data[currentOffset]
        val (isBG, isDuet) = LyricLine.fromFlag(flag)
        
        return Pair(
            LyricLine(
                startTime = startTime,
                endTime = endTime,
                words = words,
                translatedLyric = translatedLyric,
                romanLyric = romanLyric,
                isBG = isBG,
                isDuet = isDuet
            ),
            currentOffset + 1 // flag 占用 1 字节
        )
    }
    
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
    
    // ==================== 复杂消息解码方法 ====================
    
    /**
     * 解析歌曲信息消息 (SetMusicInfo)
     * Magic: 0x0002
     * 
     * @return SetMusicInfoData 对象，包含所有歌曲元数据
     */
    data class SetMusicInfoData(
        val musicId: String,
        val musicName: String,
        val albumId: String,
        val albumName: String,
        val artists: List<Artist>,
        val duration: Long
    )
    
    fun parseSetMusicInfo(data: ByteArray): SetMusicInfoData {
        require(data.size >= 2) { "消息数据太短" }
        
        // 验证 Magic Number
        val magic = parseMessageType(data)
        require(magic == MAGIC_SET_MUSIC_INFO) { "Magic Number 不匹配：期望 ${MAGIC_SET_MUSIC_INFO.toString(16)}, 实际 ${magic.toString(16)}" }
        
        var offset = 2
        
        // 读取 music_id
        val (musicId, offset1) = readNullString(data, offset)
        offset = offset1
        
        // 读取 music_name
        val (musicName, offset2) = readNullString(data, offset)
        offset = offset2
        
        // 读取 album_id
        val (albumId, offset3) = readNullString(data, offset)
        offset = offset3
        
        // 读取 album_name
        val (albumName, offset4) = readNullString(data, offset)
        offset = offset4
        
        // 读取 artists (Vec<Artist>)
        val (artists, offset5) = readVec(data, offset) { d, o -> readArtist(d, o) }
        offset = offset5
        
        // 读取 duration
        require(data.size >= offset + 8) { "duration 数据长度不足" }
        val duration = ByteBuffer.wrap(data, offset, 8).apply {
            order(ByteOrder.LITTLE_ENDIAN)
        }.long
        
        return SetMusicInfoData(
            musicId = musicId,
            musicName = musicName,
            albumId = albumId,
            albumName = albumName,
            artists = artists,
            duration = duration
        )
    }
    
    /**
     * 解析专辑封面 URI 消息 (SetMusicAlbumCoverImageURI)
     * Magic: 0x0003
     * 
     * @return imgUrl 字符串
     */
    fun parseSetAlbumCoverURI(data: ByteArray): String {
        require(data.size >= 2) { "消息数据太短" }
        
        // 验证 Magic Number
        val magic = parseMessageType(data)
        require(magic == MAGIC_SET_ALBUM_COVER_URI) { "Magic Number 不匹配：期望 ${MAGIC_SET_ALBUM_COVER_URI.toString(16)}, 实际 ${magic.toString(16)}" }
        
        // 读取 img_url
        val (imgUrl, _) = readNullString(data, 2)
        return imgUrl
    }
    
    /**
     * 解析 TTML 歌词消息 (SetLyricFromTTML)
     * Magic: 0x000B
     * 
     * @return ttmlContent TTML 格式的歌词字符串
     */
    fun parseSetLyricFromTTML(data: ByteArray): String {
        require(data.size >= 2) { "消息数据太短" }
        
        // 验证 Magic Number
        val magic = parseMessageType(data)
        require(magic == MAGIC_SET_LYRIC_FROM_TTML) { "Magic Number 不匹配：期望 ${MAGIC_SET_LYRIC_FROM_TTML.toString(16)}, 实际 ${magic.toString(16)}" }
        
        // 读取 data
        val (ttmlContent, _) = readNullString(data, 2)
        return ttmlContent
    }
    
    /**
     * 解析结构化歌词消息 (SetLyric) - 可选功能
     * Magic: 0x000A
     * 
     * @return lines 歌词行列表
     */
    fun parseSetLyric(data: ByteArray): List<LyricLine> {
        require(data.size >= 2) { "消息数据太短" }
        
        // 验证 Magic Number
        val magic = parseMessageType(data)
        require(magic == MAGIC_SET_LYRIC) { "Magic Number 不匹配：期望 ${MAGIC_SET_LYRIC.toString(16)}, 实际 ${magic.toString(16)}" }
        
        // 读取 data (Vec<LyricLine>)
        val (lines, _) = readVec(data, 2) { d, o -> readLyricLine(d, o) }
        return lines
    }
}
