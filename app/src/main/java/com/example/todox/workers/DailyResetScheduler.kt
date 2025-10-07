package com.example.todox.workers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class DailyResetScheduler(
    private val context: Context
) {

    private val workManager = WorkManager.getInstance(context)

    fun ensureScheduled(resetHour: Int) {
        val delay = computeDelay(resetHour)
        val request = OneTimeWorkRequestBuilder<DailyResetWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(DailyResetWorker.createInputData(resetHour))
            .addTag(DailyResetWorker.TAG)
            .build()

        workManager.enqueueUniqueWork(
            DailyResetWorker.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    private fun computeDelay(resetHour: Int): Long {
        val now = ZonedDateTime.now()
        var next = now.withHour(resetHour).withMinute(0).withSecond(0).withNano(0)
        if (!next.isAfter(now)) {
            next = next.plusDays(1)
        }
        return Duration.between(now, next).toMillis().coerceAtLeast(0L)
    }

    companion object
}
