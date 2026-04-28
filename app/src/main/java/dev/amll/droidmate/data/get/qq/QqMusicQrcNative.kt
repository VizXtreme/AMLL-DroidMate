package com.amll.droidmate.data.network

/**
 * JNI bridge to the native QQ Music QRC decoder implemented in Rust.
 */
object QqMusicQrcNative {
    init {
        System.loadLibrary("qq_qrc_decoder")
    }

    external fun decryptQrcHex(hex: String?): String?

    external fun freeRustString(ptr: Long)
}
