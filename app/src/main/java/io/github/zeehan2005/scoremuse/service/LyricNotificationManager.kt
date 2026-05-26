package io.github.zeehan2005.scoremuse.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.zeehan2005.scoremuse.MainActivity
import dev.amll.droidmate.R
import io.github.zeehan2005.scoremuse.global.LyricLine

/**
 * 歌词通知管理器
 * 
 * 负责管理和显示歌词通知，让用户可以在锁屏或通知栏中看到当前播放的歌词。
 * 
 * 主要功能：
 * - 创建通知渠道（Android O+）
 * - 显示/更新歌词通知
 * - 权限检查（POST_NOTIFICATIONS）
 * - 点击通知打开应用
 * - 滑动删除通知时停止服务
 * 
 * 通知特点：
 * - 使用 BigTextStyle 显示完整歌词
 * - 支持多行显示（主歌词 + 翻译 + 音译）
 * - 静音通知，不打扰用户
 * - 可设置为持续通知（ongoing）
 */
open class LyricNotificationManager(private val context: Context) {

    // 缓存上一次显示的通知内容和状态，用于去重以避免触发系统频率限制
    private var lastNotificationText: String? = null
    private var lastOngoingState: Boolean? = null
    // null 表示状态未知（新实例），true 表示通知已显示，false 表示通知已取消
    private var isNotificationActive: Boolean? = null

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    open fun showOrUpdate(currentLine: LyricLine?, ongoing: Boolean = true) {
        // 检查通知权限（Android 13+ 需要动态申请）
        if (!hasNotificationPermission()) return

        // 创建通知渠道（Android 8.0+）
        createChannelIfNeeded()
        
        // 构建通知文本（可能包含多行：主歌词 + 翻译 + 音译）
        val safeLine = buildNotificationText(currentLine)
        
        // 性能优化：如果内容和 ongoing 状态都没有改变，且通知当前处于激活状态，则跳过本次更新
        // 这可以防止因频繁更新（如 100ms 一次）触发系统的 NotificationManager 频率限制
        if (isNotificationActive == true && safeLine == lastNotificationText && ongoing == lastOngoingState) {
            return
        }

        // 创建点击通知时打开应用的 PendingIntent
        val contentIntent = createOpenAppIntent()

        // 构建通知
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)  // 通知专用图标
            .setContentText(safeLine)  // 通知内容
            .setStyle(NotificationCompat.BigTextStyle().bigText(safeLine))  // 支持多行展开
            .setContentIntent(contentIntent)  // 点击意图
            .setDeleteIntent(createDeleteIntent())  // 删除意图（滑动删除时触发）
            .setOnlyAlertOnce(true)  // 只提醒一次
            .setSilent(true)  // 静音
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // 默认优先级
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)  // 锁屏可见
            .setShowWhen(false)  // 不显示时间
            .setCategory(NotificationCompat.CATEGORY_STATUS)  // 状态类通知

        // 设置是否为持续通知
        if (ongoing) {
            builder.setOngoing(true)  // 持续通知，不能被轻易划掉
        } else {
            builder.setOngoing(false)
        }

        val notification = builder.build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)

        // 更新缓存状态
        lastNotificationText = safeLine
        lastOngoingState = ongoing
        isNotificationActive = true
    }

    /**
     * 构建通知文本
     * 
     * 将歌词行的多个部分（主歌词、翻译、音译）组合成多行文本。
     * 只显示非空的部分，并去除重复内容。
     * 
     * @param currentLine 当前歌词行
     * @return 格式化后的通知文本
     */
    private fun buildNotificationText(currentLine: LyricLine?): String {
        if (currentLine == null) return ""

        val lines = listOf(
            currentLine.text,
            currentLine.translation,
            currentLine.transliteration
        )
            .mapNotNull { it?.trim() }
            .filter { it.isNotEmpty() }
            .distinct()

        return lines.joinToString(separator = "\n")
    }

    open fun cancel() {
        // 如果通知已经不处于激活状态，则跳过取消操作，避免频繁调用 cancel() 触发频率限制
        // 如果状态为 null (未知)，则执行一次取消以确保安全
        if (isNotificationActive == false) return
        
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
        
        // 重置状态
        isNotificationActive = false
        lastNotificationText = null
        lastOngoingState = null
    }

    private fun createDeleteIntent(): PendingIntent {
        val intent = Intent(ACTION_LYRIC_NOTIFICATION_DISMISSED)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, REQUEST_CODE_DELETE, intent, flags)
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createOpenAppIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getActivity(context, REQUEST_CODE_OPEN_APP, intent, flags)
    }

    private fun createChannelIfNeeded() {

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existing = manager.getNotificationChannel(CHANNEL_ID)
        if (existing != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "实时歌词",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "显示当前播放歌词行"
            setShowBadge(false)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            setSound(null, null)
            enableVibration(false)
        }

        manager.createNotificationChannel(channel)
    }

    companion object {
        const val ACTION_LYRIC_NOTIFICATION_DISMISSED =
            "io.github.zeehan2005.scoremuse.service.ACTION_LYRIC_NOTIFICATION_DISMISSED"

        private const val CHANNEL_ID = "lyric_live_channel"
        private const val NOTIFICATION_ID = 20021
        private const val REQUEST_CODE_OPEN_APP = 9001
        private const val REQUEST_CODE_DELETE = 9002
    }
}