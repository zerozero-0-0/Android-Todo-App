package com.example.todox.workers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todox.App

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val app = context.applicationContext as? App ?: return
            app.onDeviceBoot()
        }
    }
}
