package com.amll.droidmate.data.network

import android.util.Base64
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.util.zip.InflaterInputStream

/**
 * 酷狗音乐 KRC 歌词解密工具
 * 
 * 酷狗音乐的 KRC 歌词文件是加密的，这个工具负责将其解密为纯文本。
 * 解密过程包含多个步骤：Base64 解码 → 去头 → XOR 解密 → Zlib 解压。
 * 
 * 加密原理：
 * 1. 原始歌词经过 Zlib 压缩
 * 2. 使用 16 字节固定密钥进行 XOR 加密
 * 3. 添加"krc1"文件头（4 字节）
 * 4. 最后 Base64 编码
 * 
 * 参考：https://github.com/apoint123/unilyric/tree/main/lyrics_helper_rs/src/providers/kugou
 */
object KugouDecrypter {
    
    // 固定的 16 字节解密密钥（XOR 密钥循环使用）
    private val KRC_DECRYPT_KEY = byteArrayOf(
        0x40, 0x47, 0x61, 0x77, 0x5E, 0x32, 0x74, 0x47,
        0x51, 0x36, 0x31, 0x2D, 0xCE.toByte(), 0xD2.toByte(), 0x6E, 0x69
    )
    
    /**
     * 解密 KRC 歌词
     * 
     * 这是获取酷狗音乐歌词的核心方法。输入的 Base64 字符串会经过以下处理：
     * 1. Base64 解码：还原为二进制数据
     * 2. 移除前 4 字节：去掉 "krc1" 文件头
     * 3. XOR 解密：使用 16 字节密钥逐字节异或运算
     * 4. Zlib 解压缩：还原文本内容
     * 5. UTF-8 解码：转换为可读字符串
     * 
     * @param encryptedBase64 Base64 编码的加密歌词数据
     * @return 解密后的歌词文本，失败返回 null
     */
    fun decryptKrc(encryptedBase64: String): String? {
        return try {
            // Step 1: Base64 解码
            val encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT)
            Timber.d("[KugouDecrypter] KRC encrypted data length: ${encryptedBytes.size}")
            
            // 检查最小长度（需要至少 4 字节 header）
            if (encryptedBytes.size < 4) {
                Timber.e("[KugouDecrypter] KRC encrypted data too short: ${encryptedBytes.size} bytes")
                return null
            }
            
            // Step 2: 移除前 4 字节头（"krc1" 标识）
            val dataToDecrypt = encryptedBytes.drop(4).toByteArray()
            
            // Step 3: XOR 解密（16 字节密钥循环使用）
            // XOR 的特性：A XOR K XOR K = A，所以加密和解密使用相同的算法
            val decryptedData = ByteArray(dataToDecrypt.size)
            for (i in dataToDecrypt.indices) {
                decryptedData[i] = (dataToDecrypt[i].toInt() xor KRC_DECRYPT_KEY[i % KRC_DECRYPT_KEY.size].toInt()).toByte()
            }
            
            // Step 4: Zlib 解压缩
            val decompressed = decompress(decryptedData)
            
            // Step 5: 转换为 UTF-8 字符串
            String(decompressed, Charsets.UTF_8)
        } catch (e: Exception) {
            Timber.e("[KugouDecrypter] Failed to decrypt KRC lyrics", e)
            null  // 解密失败返回 null
        }
    }
    
    /**
     * Zlib 解压缩
     * 
     * 使用 Java 原生的 InflaterInputStream 进行 zlib 解压。
     * 如果解压失败，返回原始数据（可能是未压缩的）。
     * 
     * @param data 压缩的二进制数据
     * @return 解压后的数据
     */
    private fun decompress(data: ByteArray): ByteArray {
        return try {
            val input = ByteArrayInputStream(data)
            val inflater = InflaterInputStream(input)
            inflater.readBytes()
        } catch (e: Exception) {
            Timber.e("[KugouDecrypter] Failed to decompress", e)
            data  // 如果解压失败，返回原数据
        }
    }
}
