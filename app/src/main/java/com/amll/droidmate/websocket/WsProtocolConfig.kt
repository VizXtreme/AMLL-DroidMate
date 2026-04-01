package com.amll.droidmate.websocket

/**
 * AMLL WebSocket 协议版本
 * 
 * 定义了两种 WebSocket 通信协议：
 * - V1: 纯二进制协议，效率高但复杂
 * - V2: JSON 混合协议，易于调试和扩展（推荐）
 */
enum class WsProtocolVersion(val version: Int, val description: String) {
    V1(1, "V1 - 二进制协议"),
    V2(2, "V2 - JSON 混合协议（推荐）");
    
    companion object {
        /**
         * 从整数版本号转换为枚举值
         * 
         * @param value 整数版本号（1 或 2）
         * @return 对应的枚举值，如果不支持则返回 V2（默认）
         */
        fun fromInt(value: Int): WsProtocolVersion {
            return entries.find { it.version == value } ?: V2
        }
    }
}

/**
 * WebSocket 协议配置
 * 
 * 封装了 WebSocket 连接的各种配置选项，
 * 允许客户端根据需要自定义通信协议行为。
 */
data class WsProtocolConfig(
    val protocolVersion: WsProtocolVersion = WsProtocolVersion.V2,  // 使用的协议版本（默认 V2）
    val autoDetect: Boolean = true,  // 是否自动检测服务端支持的协议版本
    val sendInitialize: Boolean = true,  // 是否在连接时发送初始化消息
    val enableHeartbeat: Boolean = true,  // 是否启用心跳包（保持连接活跃）
    val heartbeatIntervalSeconds: Int = 30  // 心跳间隔（秒），防止连接超时
)
