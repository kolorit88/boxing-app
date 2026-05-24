package com.example.football.service


import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.data.worker.MatchesSyncWorker
import kotlinx.coroutines.*

class MatchesSyncService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MatchesSyncService", "Сервис запущен, запускаем синхронизацию")

        serviceScope.launch {
            MatchesSyncWorker.startOneTimeSync(applicationContext)
            delay(3000)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d("MatchesSyncService", "Сервис остановлен")
    }
}