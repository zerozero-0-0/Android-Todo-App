package com.example.todox

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.Configuration
import com.example.todox.data.db.AppDatabase
import com.example.todox.data.repo.SettingsRepository
import com.example.todox.data.repo.TodoRepository
import com.example.todox.notifications.NotificationHelper
import com.example.todox.workers.AppWorkerFactory
import com.example.todox.workers.DailyResetScheduler
import com.example.todox.workers.DueReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.settingsDataStore by preferencesDataStore(
    name = "settings"
)

class App : Application(), Configuration.Provider {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val dataStore: androidx.datastore.core.DataStore<Preferences> by lazy { settingsDataStore }
    val database: AppDatabase by lazy { AppDatabase.build(this) }
    val todoRepository: TodoRepository by lazy { TodoRepository(database.todoDao()) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(dataStore) }
    val notificationHelper: NotificationHelper by lazy { NotificationHelper(this) }
    val dueReminderScheduler: DueReminderScheduler by lazy { DueReminderScheduler(this) }
    val dailyResetScheduler: DailyResetScheduler by lazy { DailyResetScheduler(this) }

    private val workerFactory: AppWorkerFactory by lazy {
        AppWorkerFactory(
            todoRepository,
            settingsRepository,
            notificationHelper,
            dueReminderScheduler,
            dailyResetScheduler
        )
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper.ensureChannel()
        applicationScope.launch {
            settingsRepository.resetHour.collect { hour ->
                dailyResetScheduler.ensureScheduled(hour)
            }
        }
        applicationScope.launch {
            val todos = todoRepository.dueAfter(System.currentTimeMillis())
            dueReminderScheduler.reschedule(todos)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    fun onDeviceBoot() {
        applicationScope.launch {
            val hour = settingsRepository.resetHour.first()
            dailyResetScheduler.ensureScheduled(hour)
            val todos = todoRepository.dueAfter(System.currentTimeMillis())
            dueReminderScheduler.reschedule(todos)
        }
    }
}
