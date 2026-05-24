package com.example.data.worker


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.worker.MatchesSyncWorker

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Устройство перезагружено, перезапускаем WorkManager")
            MatchesSyncWorker.schedulePeriodic(context)
        }
    }
}