package com.example.todox.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.todox.data.repo.SettingsRepository
import com.example.todox.data.repo.TodoRepository
import com.example.todox.notifications.NotificationHelper

class AppWorkerFactory(
    private val todoRepository: TodoRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationHelper: NotificationHelper,
    private val dueReminderScheduler: DueReminderScheduler,
    private val dailyResetScheduler: DailyResetScheduler
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = when (workerClassName) {
        DailyResetWorker::class.qualifiedName ->
            DailyResetWorker(appContext, workerParameters, todoRepository, settingsRepository, dailyResetScheduler)
        DueNotificationWorker::class.qualifiedName ->
            DueNotificationWorker(appContext, workerParameters, todoRepository, notificationHelper, dueReminderScheduler)
        else -> null
    }
}
