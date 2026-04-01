//! QQ 音乐 QRC 歌词解码库
//!
//! 这是一个小型的 JNI 兼容库，用于解码 QQ 音乐的 QRC 加密歌词。
//! 
//! **用途**：
//! - 被构建为 Android 原生库（.so 文件）
//! - 由 DroidMate Android 应用加载使用
//! - 使用与 `amll-lyric` crate 相同的 QRC 解密算法
//!
//! **解密原理**：
//! QRC 格式使用简单的 XOR 加密，这个库提供了解密功能，
//! 将十六进制编码的加密数据转换为可读的 UTF-8 文本。

use amll_lyric::eqrc::decrypt_qrc_hex;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;

// ==================== JNI 入口点 ====================

/// JNI 函数：解密 QRC 十六进制字符串
/// 
/// 这是从 Java/Kotlin 代码调用的原生方法。
/// 它接收一个十六进制编码的 QRC 加密字符串，返回解密后的 UTF-8 文本。
/// 
/// **参数**：
/// - `env`: JNI 环境对象
/// - `_class`: Java 类（未使用）
/// - `jhex`: Java String，包含十六进制编码的加密 QRC 数据
/// 
/// **返回值**：
/// - 成功：Java String (jstring)，包含解密后的歌词文本
/// - 失败：null 指针
#[no_mangle]
pub extern "system" fn Java_com_amll_droidmate_data_network_QqMusicQrcNative_decryptQrcHex(
    mut env: JNIEnv,
    _class: JClass,
    jhex: JString,
) -> jstring {
    // Step 1: 从 Java String 获取十六进制字符串
    let hex: String = match env.get_string(&jhex) {
        Ok(s) => s.into(),
        Err(_) => return std::ptr::null_mut(),  // 获取失败返回 null
    };

    // Step 2: 调用核心解密函数
    let decoded = decrypt_qrc_hex(&hex);

    // Step 3: 将结果转换回 Java String
    match env.new_string(decoded) {
        Ok(jstr) => jstr.into_raw(),  // 成功返回 Java 字符串指针
        Err(_) => std::ptr::null_mut(),  // 创建失败返回 null
    }
}

/// 解密 QRC 十六进制字符串为普通文本
/// 
/// 这是一个便捷函数，封装了底层的解密逻辑。
/// 
/// **参数**：
/// - `encrypted_text`: 十六进制编码的 QRC 加密数据
/// 
/// **返回值**：
/// - 成功：Ok(解密后的 UTF-8 字符串)
/// - 失败：Err(错误信息)
pub fn decrypt_qrc_hex_to_string(encrypted_text: &str) -> Result<String, String> {
    Ok(decrypt_qrc_hex(encrypted_text))
}
