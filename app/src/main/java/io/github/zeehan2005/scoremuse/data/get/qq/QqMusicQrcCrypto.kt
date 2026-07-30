package io.github.zeehan2005.scoremuse.data.get.qq

import android.util.Base64
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.util.Locale
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

/**
 * QQ 音乐 QRC 歌词解密器
 *
 * 核心 3DES 解密使用 QqMusicTripledes（精确移植自 Python qqmusic_api/algorithms/tripledes.py，
 * 参考 L-1124/QQMusicApi）,该实现已通过硬编码测试数据验证。
 *
 * 加密流程（反向即为解密）：
 * 原始文本 → Zlib 压缩 → 3DES 加密（非标准实现）→ Hex 编码
 *
 * 解密流程：Hex → 3DES → Zlib → UTF-8/GB18030
 */
object QqMusicQrcCrypto {
    private const val DES_BLOCK_SIZE = 8

    /**
     * 解密 QQ 音乐的 QRC 歌词
     *
     * 主要流程：
     * 1. Hex → 3DES → Zlib → UTF-8/GB18030
     */
    fun decryptQrcHex(encryptedText: String): String {
        Timber.d("[QqMusicQrcCrypto] Starting Hex+3DES+Zlib decryption, input length: ${encryptedText.length}")

        Timber.i("[QqMusicQrcCrypto] Decrypting via QqMusicTripledes")

        val encryptedBytes = decodeHex(encryptedText)
        Timber.d("[QqMusicQrcCrypto] After Hex decode: ${encryptedBytes.size} bytes")

        require(encryptedBytes.size % DES_BLOCK_SIZE == 0) {
            "Encrypted data length must be a multiple of $DES_BLOCK_SIZE"
        }

        // 使用 QqMusicTripledes（精确移植自 Python, 已验证与 L-1124/QQMusicApi 输出一致）
        val decrypted = decrypt3DesEde(encryptedBytes)

        Timber.d("[QqMusicQrcCrypto] After 3DES decrypt: ${decrypted.size} bytes, first 32 bytes: ${decrypted.take(32).joinToString(",") { "%02X".format(it) }}")

        // 特殊情况：解密后可能是 Base64 编码或纯文本
        if (looksLikeMostlyPrintable(decrypted)) {
            val candidate = String(decrypted, Charsets.UTF_8).trim()
            Timber.d("[QqMusicQrcCrypto] Decrypted output looks like text (len=${candidate.length})")
            val base64Regex = Regex("^[A-Za-z0-9+/=\\s]+$")
            if (candidate.length % 4 == 0 && base64Regex.matches(candidate)) {
                try {
                    val decoded = Base64.decode(candidate, Base64.DEFAULT)
                    val decodedText = String(decoded, Charsets.UTF_8)
                    Timber.d("[QqMusicQrcCrypto] Interpreted decrypted output as Base64")
                    return decodedText
                } catch (e: Exception) {
                    Timber.w("[QqMusicQrcCrypto] Base64 decode failed $e")
                }
            }
        }

        val decompressed = if (decrypted.isNotEmpty() && decrypted[0] != 0x78.toByte()) {
            Timber.i("[QqMusicQrcCrypto] First byte is 0x${"%02X".format(decrypted[0])}, attempting to locate zlib header")
            attemptDecompressFromPossibleZlibOffset(decrypted)
        } else {
            decompress(decrypted)
        }
        Timber.d("[QqMusicQrcCrypto] After Zlib decompress: ${decompressed.size} bytes")

        val payload = if (
            decompressed.size >= 3 &&
            decompressed[0] == 0xEF.toByte() &&
            decompressed[1] == 0xBB.toByte() &&
            decompressed[2] == 0xBF.toByte()
        ) {
            Timber.d("[QqMusicQrcCrypto] UTF-8 BOM detected, removing first 3 bytes")
            decompressed.copyOfRange(3, decompressed.size)
        } else {
            decompressed
        }

        val utf8Result = payload.toString(Charsets.UTF_8)

        if (utf8Result.contains('�')) {
            Timber.i("[QqMusicQrcCrypto] UTF-8 produced replacement chars; retrying GB18030")
            try {
                val gb18030 = payload.toString(Charset.forName("GB18030"))
                Timber.d("[QqMusicQrcCrypto] Final result (GB18030): ${gb18030.length} chars")
                return gb18030
            } catch (e: Exception) {
                Timber.i("[QqMusicQrcCrypto] GB18030 failed; falling back to UTF-8 $e")
            }
        }

        Timber.d("[QqMusicQrcCrypto] Final result: ${utf8Result.length} chars")
        return utf8Result
    }

    // ========== 辅助方法 ==========

    private fun decodeHex(value: String): ByteArray {
        val clean = value.trim()
        require(clean.length % 2 == 0) { "Invalid hex string length" }
        val out = ByteArray(clean.length / 2)
        var i = 0
        while (i < clean.length) {
            val hi = clean[i].digitToIntOrNull(16) ?: error("Invalid hex string")
            val lo = clean[i + 1].digitToIntOrNull(16) ?: error("Invalid hex string")
            out[i / 2] = ((hi shl 4) or lo).toByte()
            i += 2
        }
        return out
    }

    private fun decompress(data: ByteArray): ByteArray {
        return try {
            InflaterInputStream(ByteArrayInputStream(data)).use { it.readBytes() }
        } catch (e: Exception) {
            Timber.i("[QqMusicQrcCrypto] Zlib failed, retrying raw deflate $e")
            val inflater = Inflater(true)
            try {
                InflaterInputStream(ByteArrayInputStream(data), inflater).use { it.readBytes() }
            } finally {
                inflater.end()
            }
        }
    }

    private fun looksLikeMostlyPrintable(bytes: ByteArray): Boolean {
        if (bytes.isEmpty()) return false
        val printable = bytes.count { b ->
            val c = b.toInt() and 0xFF
            (c in 0x20..0x7E) || c == 0x0A || c == 0x0D || c == 0x09
        }
        return printable.toDouble() / bytes.size >= 0.75
    }

    private fun attemptDecompressFromPossibleZlibOffset(data: ByteArray): ByteArray {
        val candidateOffsets = data.withIndex().filter { it.value == 0x78.toByte() }.map { it.index }
        Timber.d("[QqMusicQrcCrypto] Found ${candidateOffsets.size} potential 0x78 zlib start bytes")

        for (offset in candidateOffsets) {
            if (offset + 1 >= data.size) continue
            val second = data[offset + 1].toInt() and 0xFF
            val combined = (0x78 shl 8) or second
            val valid = combined % 31 == 0
            Timber.d("[QqMusicQrcCrypto] Candidate @ $offset: header=0x78 0x${"%02X".format(second)} valid=$valid")
            if (!valid) continue

            try {
                return decompress(data.copyOfRange(offset, data.size))
            } catch (e: Exception) {
                Timber.w("[QqMusicQrcCrypto] Decompress failed at offset $offset $e")
            }
        }

        Timber.i("[QqMusicQrcCrypto] No valid zlib header found; raw deflate from start")
        val inflater = Inflater(true)
        return try {
            InflaterInputStream(ByteArrayInputStream(data), inflater).use { it.readBytes() }
        } finally {
            inflater.end()
        }
    }

    fun looksLikeHex(text: String): Boolean {
        val normalized = text.trim().lowercase(Locale.ROOT)
        return normalized.isNotEmpty() &&
            normalized.length % 2 == 0 &&
            normalized.all { it in '0'..'9' || it in 'a'..'f' }
    }
}
