package com.amll.droidmate.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.amll.droidmate.MainActivity
import com.amll.droidmate.R
import com.amll.droidmate.domain.model.LyricLine

open class LyricNotificationManager(private val context: Context) {

    open fun showOrUpdate(currentLine: LyricLine?, ongoing: Boolean = true) {
        if (!hasNotificationPermission()) return

        createChannelIfNeeded()
        val safeLine = buildNotificationText(currentLine)
        val contentIntent = createOpenAppIntent()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(safeLine)
            .setStyle(NotificationCompat.BigTextStyle().bigText(safeLine))
            .setContentIntent(contentIntent)
            .setDeleteIntent(createDeleteIntent())
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_STATUS)

        if (ongoing) {
            builder.setOngoing(true)
        } else {
            builder.setOngoing(false)
        }

        val notification = builder.build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

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
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

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
            "com.amll.droidmate.ACTION_LYRIC_NOTIFICATION_DISMISSED"

        private const val CHANNEL_ID = "lyric_live_channel"
        private const val NOTIFICATION_ID = 20021
        private const val REQUEST_CODE_OPEN_APP = 9001
        private const val REQUEST_CODE_DELETE = 9002
    }
}
