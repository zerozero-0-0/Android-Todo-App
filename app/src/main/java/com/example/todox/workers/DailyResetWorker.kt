package com.example.todox.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.todox.data.repo.SettingsRepository
import com.example.todox.data.repo.TodoRepository
import kotlinx.coroutines.flow.first
import java.util.concurrent.CancellationException

class DailyResetWorker(
    appContext: Context,
    params: WorkerParameters,
    private val todoRepository: TodoRepository,
    private val settingsRepository: SettingsRepository,
    private val dailyResetScheduler: DailyResetScheduler
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            todoRepository.resetDailyTodos()
            val scheduledHour = inputData.getInt(
                KEY_RESET_HOUR,
                SettingsRepository.DEFAULT_RESET_HOUR
            )
            val currentHour = runCatching { settingsRepository.resetHour.first() }
                .getOrElse { scheduledHour }
                .coerceIn(0, 23)
            dailyResetScheduler.ensureScheduled(currentHour)
            Result.success()
        } catch (c: CancellationException) {
            throw c
        } catch (t: Throwable) {
            Result.retry()
        }
    }

    companion object {
        const val UNIQUE_WORK_NAME = "daily_reset_worker"
        const val TAG = "daily_reset"
        private const val KEY_RESET_HOUR = "key_reset_hour"

        fun createInputData(resetHour: Int): Data = workDataOf(KEY_RESET_HOUR to resetHour)
    }
}
