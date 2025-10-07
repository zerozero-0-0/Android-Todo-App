package com.example.todox.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todox.workers.DueNotificationWorker

class DueAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getStringExtra(EXTRA_TODO_ID) ?: return
        DueNotificationWorker.enqueueNow(context, todoId)
    }

    companion object {
        const val EXTRA_TODO_ID = "extra_todo_id"
        const val REQUEST_CODE = 1001
    }
}
