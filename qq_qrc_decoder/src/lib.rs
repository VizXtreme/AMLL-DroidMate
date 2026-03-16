//! A small JNI-compatible library for decoding QQ Music QRC lyrics.
//!
//! This crate is meant to be built as an Android native library and loaded
//! by the DroidMate Android app. It uses the same QRC decryption logic
//! used by the `amll-lyric` crate.

use amll_lyric::eqrc::decrypt_qrc_hex;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;

// JNI entry point ----------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_com_amll_droidmate_data_network_QqMusicQrcNative_decryptQrcHex(
    mut env: JNIEnv,
    _class: JClass,
    jhex: JString,
) -> jstring {
    let hex: String = match env.get_string(&jhex) {
        Ok(s) => s.into(),
        Err(_) => return std::ptr::null_mut(),
    };

    let decoded = decrypt_qrc_hex(&hex);

    match env.new_string(decoded) {
        Ok(jstr) => jstr.into_raw(),
        Err(_) => std::ptr::null_mut(),
    }
}

/// Decode a QRC payload (hex-encoded) into UTF-8 text.
pub fn decrypt_qrc_hex_to_string(encrypted_text: &str) -> Result<String, String> {
    Ok(decrypt_qrc_hex(encrypted_text))
}
