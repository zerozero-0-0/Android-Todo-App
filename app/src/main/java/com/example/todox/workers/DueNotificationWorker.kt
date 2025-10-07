package com.example.todox.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.todox.data.repo.TodoRepository
import com.example.todox.notifications.NotificationHelper
import java.util.concurrent.CancellationException

class DueNotificationWorker(
    appContext: Context,
    params: WorkerParameters,
    private val todoRepository: TodoRepository,
    private val notificationHelper: NotificationHelper,
    private val scheduler: DueReminderScheduler
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val todoId = inputData.getString(KEY_TODO_ID) ?: return Result.failure()
            val todo = todoRepository.get(todoId) ?: run {
                scheduler.cancel(todoId)
                return Result.success()
            }
            if (todo.done) {
                scheduler.cancel(todoId)
                return Result.success()
            }

            notificationHelper.showDueNotification(todo)
            scheduler.cancel(todoId)
            Result.success()
        } catch (c: CancellationException) {
            throw c
        } catch (t: Throwable) {
            Result.retry()
        }
    }

    companion object {
        private const val KEY_TODO_ID = "key_todo_id"
        private const val WORK_PREFIX = "due_notification_"
        private const val TAG_PREFIX = "due_notification_tag_"

        fun createInputData(todoId: String) = workDataOf(KEY_TODO_ID to todoId)

        fun workName(todoId: String) = WORK_PREFIX + todoId

        fun tag(todoId: String) = TAG_PREFIX + todoId

        fun enqueueNow(context: Context, todoId: String) {
            val request = OneTimeWorkRequestBuilder<DueNotificationWorker>()
                .setInputData(createInputData(todoId))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                workName(todoId),
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
