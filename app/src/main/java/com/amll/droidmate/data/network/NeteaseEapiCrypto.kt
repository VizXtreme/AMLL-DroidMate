package com.amll.droidmate.data.network

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * 网易云音乐 EAPI 加密工具
 * 
 * 网易云音乐的 API 使用特殊的加密方式来保护请求参数。
 * 这个工具类实现了 EAPI 接口的签名和加密逻辑。
 * 
 * 加密流程：
 * 1. 构建特殊格式的明文字符串
 * 2. 计算 MD5 摘要
 * 3. 使用 AES-ECB 模式加密
 * 4. 转换为大写十六进制字符串
 * 
 * 注意：这些方法通过反射调用，因此保留 @Suppress("unused")
 */
object NeteaseEapiCrypto {

    // EAPI 接口的固定密钥
    private const val EAPI_KEY = "e82ckenh8dichen8"

    /**
     * 准备 EAPI 接口所需的加密参数
     * 
     * 这个方法会将 URL 路径和 JSON 参数封装并加密，生成 API 请求所需的 params 字段。
     * 
     * 封装格式：
     * "nobody{urlPath}use{paramsJson}md5forencrypt" -> MD5 -> digest
     * 最终 payload: "{urlPath}-36cd479b6b5-{paramsJson}-36cd479b6b5-{digest}"
     * 
     * @param urlPath API 路径（例如 "/api/song/lyric"）
     * @param paramsJson JSON 格式的请求参数
     * @return 加密后的大写十六进制字符串
     */
    fun prepareEapiParams(urlPath: String, paramsJson: String): String {
        val message = "nobody${urlPath}use${paramsJson}md5forencrypt"
        val digest = md5Hex(message.toByteArray())
        val payload = "${urlPath}-36cd479b6b5-${paramsJson}-36cd479b6b5-${digest}"
        return aesEcbEncryptToUpperHex(payload.toByteArray(), EAPI_KEY.toByteArray())
    }

    /**
     * AES-ECB 加密并转换为大写十六进制
     * 
     * 使用固定的密钥对数据进行 AES-ECB 模式加密。
     * ECB 模式不需要 IV（初始化向量），适合这种简单的参数加密场景。
     * 
     * @param data 待加密的数据
     * @param key 加密密钥
     * @return 加密后的大写十六进制字符串
     */
    private fun aesEcbEncryptToUpperHex(data: ByteArray, key: ByteArray): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val keySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        return toHexUpper(cipher.doFinal(data))
    }

    /**
     * 计算 MD5 哈希值（小写十六进制）
     * 
     * @param data 输入数据
     * @return 32 位小写十六进制 MD5 字符串
     */
    private fun md5Hex(data: ByteArray): String {
        val md = MessageDigest.getInstance("MD5")
        return md.digest(data).joinToString("") { "%02x".format(it) }
    }

    /**
     * 字节数组转大写十六进制字符串
     * 
     * @param data 字节数组
     * @return 大写十六进制字符串（每个字节占 2 个字符）
     */
    private fun toHexUpper(data: ByteArray): String {
        val out = StringBuilder(data.size * 2)
        for (b in data) {
            out.append("%02X".format(b))
        }
        return out.toString()
    }
}
