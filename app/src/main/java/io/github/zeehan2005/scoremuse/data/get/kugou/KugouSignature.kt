package io.github.zeehan2005.scoremuse.data.get.kugou

import java.security.MessageDigest
import timber.log.Timber

/**
 * 酷狗音乐签名工具
 * 
 * 这个工具类用于生成酷狗音乐 API 请求所需的签名。
 * 酷狗音乐使用 MD5 签名算法来验证请求的合法性，防止未授权访问。
 * 
 * 签名规则：
 * MD5(salt + sortedParams + body + salt)
 * 其中：
 * - salt: 固定的盐值字符串
 * - sortedParams: 按 key 排序后的参数拼接
 * - body: 请求体（如果有）
 * 
 * 参考：https://github.com/apoint123/unilyric/tree/main/lyrics_helper_rs/src/providers/kugou
 * 
 * 注意：本工具类的所有方法均通过反射调用，因此需要保留 @Suppress("unused")
 * 这些方法在运行时被动态调用，用于生成酷狗音乐的 API 签名
 */
@Suppress("unused")
object KugouSignature {
    
    /** 酷狗 Android 客户端固定盐值 */
    private const val KUGOU_ANDROID_SALT = "OIlwieks28dk2k092lksi2UIkp"
    private const val APP_ID = "1005"       // 应用 ID
    private const val CLIENT_VER = "12569"  // 客户端版本号
    
    /**
     * 生成 Android 签名
     * 
     * 这是酷狗音乐 API 认证的核心方法。所有发往酷狗的请求都需要携带此签名，
     * 否则会被服务器拒绝。
     * 
     * 签名步骤：
     * 1. 将所有参数按 key 字母顺序排序
     * 2. 拼接成 "key1=value1key2=value2..." 格式
     * 3. 在前后加上盐值：salt + params + body + salt
     * 4. 计算 MD5 哈希值
     * 5. 转换为 16 进制小写字符串
     * 
     * @param params URL 查询参数 Map
     * @param body 请求体（POST 请求时使用，默认为空）
     * @return 32 位小写 MD5 签名字符串
     */
    fun generateSignature(params: Map<String, String>, body: String = ""): String {
        return try {
            /**
             * 构建参数字符串（需要按 key 排序）
             * TreeMap 会自动按键排序
             */
            val sortedParams = params.toSortedMap()
            val paramsString = sortedParams.entries.joinToString("") { (k, v) -> "$k=$v" }
            
            /** 构建待签名字符串：salt + params + body + salt */
            val stringToSign = KUGOU_ANDROID_SALT + paramsString + body + KUGOU_ANDROID_SALT
            
            /** 计算 MD5 */
            val md5 = MessageDigest.getInstance("MD5")
            val digest = md5.digest(stringToSign.toByteArray())
            
            // 转换为 16 进制字符串（小写）
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Timber.e("[KugouSignature] Failed to generate Kugou signature $e")
            ""  // 签名失败返回空字符串
        }
    }
    
    /**
     * 生成设备 ID（Device Mid）
     * 
     * 设备 ID 是酷狗用来标识客户端设备的唯一标识符。
     * 这里使用 MD5("-") 作为通用设备 ID，表示不特定于某个设备。
     * 
     * @return 32 位小写 MD5 字符串
     */
    fun generateDeviceMid(): String {
        return try {
            val md5 = MessageDigest.getInstance("MD5")
            val digest = md5.digest("-".toByteArray())
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Timber.e("[KugouSignature] Failed to generate device mid $e")
            "00000000000000000000000000000000"
        }
    }
    
    /**
     * 获取应用 ID
     */
    fun getAppId(): String = APP_ID
    
    /**
     * 获取客户端版本
     */
    fun getClientVer(): String = CLIENT_VER
}
