package io.github.zeehan2005.scoremuse.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import timber.log.Timber

/**
 * 媒体监听服务
 * 
 * 这是一个系统级的通知监听服务。当用户在系统设置中授权通知访问权限后，
 * 该服务可以监听来自音乐应用的通知，从而获取当前播放的歌曲信息。
 * 
 * 主要用途：
 * - 检测哪个音乐应用正在播放
 * - 捕获媒体通知的变化
 * - 配合 MediaInfoService 实现完整的播放状态监测
 */
class MediaListenerService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        Timber.i("[MediaListenerService] Service connected")  // 服务连接到系统通知管理器
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return
        // 当有新的媒体通知时记录日志（用于调试）
        Timber.i("[MediaListenerService] Notification posted from package: ${sbn.packageName}")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Timber.i("[MediaListenerService] Service disconnected")  // 服务断开连接
    }
}
