package com.amll.droidmate.util

import timber.log.Timber
import java.io.EOFException
import java.net.ConnectException

/**
 * WebSocket 错误处理器
 * 
 * 提供统一的 WebSocket 错误处理和日志记录功能
 * 
 * 用法：
 * ```kotlin
 * // 在 WebSocket 监听器中使用
 * override fun onError(error: Throwable) {
 *     WebSocketErrorHandler.handleError(error) { userMessage ->
 *         // 显示用户友好的错误提示
 *         showToast(userMessage)
 *     }
 * }
 * ```
 */
object WebSocketErrorHandler {
    
    /**
     * 处理 WebSocket 错误
     * 
     * @param error 发生的错误
     * @param onUserFriendlyMessage 用户友好消息回调（可选）
     */
    fun handleError(
        error: Throwable,
        onUserFriendlyMessage: ((String) -> Unit)? = null
    ) {
        Timber.e(error, "WebSocket 错误")
        
        val userMessage = when (error) {
            is EOFException -> {
                Timber.e("服务器主动断开连接，可能原因：")
                Timber.e("  1. 服务器未运行或已关闭")
                Timber.e("  2. 协议格式不匹配（检查 Initialize 消息格式）")
                Timber.e("  3. 网络问题导致连接中断")
                Timber.e("  4. 防火墙/安全软件阻止连接")
                "服务器已断开连接"
            }
            is ConnectException -> {
                Timber.e("无法连接到服务器")
                Timber.e("请检查：")
                Timber.e("  1. 服务器是否正在运行")
                Timber.e("  2. IP 地址和端口是否正确")
                Timber.e("  3. 设备是否在同一局域网内")
                "无法连接到服务器，请检查网络设置"
            }
            else -> {
                Timber.e("未知错误类型：${error.javaClass.simpleName}")
                "发生未知错误：${error.message}"
            }
        }
        
        onUserFriendlyMessage?.invoke(userMessage)
    }
    
    /**
     * 获取错误的用户友好描述
     * 
     * @param error 错误对象
     * @return 用户友好的错误消息
     */
    fun getUserFriendlyMessage(error: Throwable): String {
        return when (error) {
            is EOFException -> "服务器已断开连接"
            is ConnectException -> "无法连接到服务器，请检查网络设置"
            else -> "发生错误：${error.message ?: "未知错误"}"
        }
    }
    
    /**
     * 判断错误是否为致命错误（需要用户干预）
     * 
     * @param error 错误对象
     * @return 如果是致命错误返回 true
     */
    fun isFatalError(error: Throwable): Boolean {
        return error is ConnectException || error is EOFException
    }
}
