package com.example.todox.workers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.example.todox.data.model.Todo
import com.example.todox.notifications.DueAlarmReceiver
import java.time.Duration
import java.util.concurrent.TimeUnit

class DueReminderScheduler(
    private val context: Context
) {

    private val workManager = WorkManager.getInstance(context)
    private val alarmManager: AlarmManager? = context.getSystemService()

    fun schedule(todo: Todo) {
        if (todo.dueAt == null || todo.done) {
            cancel(todo.id)
            return
        }
        val now = System.currentTimeMillis()
        val delay = (todo.dueAt - now).coerceAtLeast(0L)
        if (delay <= 0L) {
            DueNotificationWorker.enqueueNow(context, todo.id)
            return
        }

        val request = OneTimeWorkRequestBuilder<DueNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(DueNotificationWorker.createInputData(todo.id))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag(DueNotificationWorker.tag(todo.id))
            .build()

        workManager.enqueueUniqueWork(
            DueNotificationWorker.workName(todo.id),
            ExistingWorkPolicy.REPLACE,
            request
        )

        maybeScheduleExactAlarm(todo.id, todo.dueAt, delay)
    }

    fun cancel(todoId: String) {
        workManager.cancelUniqueWork(DueNotificationWorker.workName(todoId))
        cancelAlarm(todoId)
    }

    fun reschedule(todos: List<Todo>) {
        todos.forEach { schedule(it) }
    }

    private fun maybeScheduleExactAlarm(todoId: String, triggerAtMillis: Long, delay: Long) {
        val alarm = alarmManager ?: return
        val threshold = Duration.ofMinutes(15).toMillis()
        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarm.canScheduleExactAlarms()
        } else {
            true
        }
        if (!canScheduleExact) return
        if (delay > threshold) return

        val intent = Intent(context, DueAlarmReceiver::class.java).apply {
            putExtra(DueAlarmReceiver.EXTRA_TODO_ID, todoId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    private fun cancelAlarm(todoId: String) {
        val alarm = alarmManager ?: return
        val intent = Intent(context, DueAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarm.cancel(it) }
    }
}
