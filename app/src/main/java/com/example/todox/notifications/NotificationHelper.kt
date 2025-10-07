package com.example.todox.notifications

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
import com.example.todox.R
import com.example.todox.data.model.Todo
import com.example.todox.ui.MainActivity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class NotificationHelper(
    private val context: Context
) {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_description)
                enableVibration(true)
                setShowBadge(true)
            }
            manager?.createNotificationChannel(channel)
        }
    }

    fun showDueNotification(todo: Todo) {
        if (!notificationsAllowed()) return

        val pendingIntent = PendingIntent.getActivity(
            context,
            todo.id.hashCode(),
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(MainActivity.EXTRA_TODO_ID, todo.id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dueText = todo.dueAt?.let { due ->
            val dateTime = Instant.ofEpochMilli(due).atZone(ZoneId.systemDefault())
            formatter.format(dateTime)
        }

        val contentText = buildString {
            dueText?.let { append(it) }
            if (!todo.note.isNullOrBlank()) {
                if (isNotEmpty()) append(" • ")
                append(todo.note)
            }
            if (todo.tags.isNotEmpty()) {
                if (isNotEmpty()) append(" • ")
                append(todo.tags.joinToString(", "))
            }
        }.ifBlank { null }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(todo.title)
            .setContentText(contentText ?: context.getString(R.string.notification_default_body))
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(todo.id.hashCode(), notification)
    }

    private fun notificationsAllowed(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) return false
        }
        return notificationManager.areNotificationsEnabled()
    }

    companion object {
        const val CHANNEL_ID = "todo_reminders"
        const val CHANNEL_NAME = "Todo Reminders"
    }
}
