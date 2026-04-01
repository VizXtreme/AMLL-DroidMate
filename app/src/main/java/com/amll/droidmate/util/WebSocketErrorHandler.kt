package com.amll.droidmate.util

import timber.log.Timber
import java.io.EOFException
import java.net.ConnectException

/**
 * WebSocket 错误处理器
 * 
 * 提供统一的 WebSocket 错误处理和日志记录功能。
 * 将技术性的错误信息转换为用户友好的提示消息。
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
     * 这个方法会：
     * 1. 记录详细的错误日志（供开发者调试）
     * 2. 根据错误类型生成用户友好的提示消息
     * 3. 通过回调返回用户友好的消息
     * 
     * @param error 发生的错误
     * @param onUserFriendlyMessage 用户友好消息回调（可选），用于显示给用户
     */
    fun handleError(
        error: Throwable,
        onUserFriendlyMessage: ((String) -> Unit)? = null
    ) {
        Timber.e("[WebSocketErrorHandler] WebSocket error: ${error.message}", error)
        
        // 根据错误类型返回不同的用户友好提示
        val userMessage = when (error) {
            is EOFException -> {
                Timber.e("[WebSocketErrorHandler] Server disconnected actively")
                "服务器已断开连接"  // 服务器主动断开
            }
            is ConnectException -> {
                Timber.e("[WebSocketErrorHandler] Cannot connect to server")
                "无法连接到服务器，请检查网络设置"  // 网络连接问题
            }
            else -> {
                Timber.e("[WebSocketErrorHandler] Unknown error type: ${error.javaClass.simpleName}")
                "发生未知错误：${error.message}"  // 其他错误
            }
        }
        
        onUserFriendlyMessage?.invoke(userMessage)
    }
    
    /**
     * 获取错误的用户友好描述
     * 
     * 与 handleError 不同，这个方法只返回消息，不记录日志。
     * 适用于只需要显示错误但不需要额外处理的场景。
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
     * 致命错误是指那些无法自动恢复，需要用户手动处理的错误。
     * 例如网络连接问题通常需要用户检查网络设置。
     * 
     * @param error 错误对象
     * @return 如果是致命错误返回 true
     */
    fun isFatalError(error: Throwable): Boolean {
        return error is ConnectException || error is EOFException
    }
}
