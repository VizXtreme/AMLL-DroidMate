package com.amll.droidmate.websocket

/**
 * AMLL WebSocket 协议版本
 */
enum class WsProtocolVersion(val version: Int, val description: String) {
    V1(1, "V1 - 二进制协议"),
    V2(2, "V2 - JSON 混合协议（推荐）");
    
    companion object {
        fun fromInt(value: Int): WsProtocolVersion {
            return entries.find { it.version == value } ?: V2
        }
    }
}

/**
 * WebSocket 协议配置
 */
data class WsProtocolConfig(
    val protocolVersion: WsProtocolVersion = WsProtocolVersion.V2,
    val autoDetect: Boolean = true,
    val sendInitialize: Boolean = true,
    val enableHeartbeat: Boolean = true,
    val heartbeatIntervalSeconds: Int = 30
)
