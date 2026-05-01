package io.github.zeehan2005.scoremuse.data.get.qq

/**
 * JNI bridge to the native QQ Music QRC decoder implemented in Rust.
 */
object QqMusicQrcNative {
    init {
        System.loadLibrary("qq_qrc_decoder")
    }

    external fun decryptQrcHex(hex: String?): String?

}
