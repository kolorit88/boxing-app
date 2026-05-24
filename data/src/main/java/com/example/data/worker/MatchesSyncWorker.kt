package com.example.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.repository.MockMatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MatchesSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    // Создаем репозиторий напрямую, без Koin
    private val repository = MockMatchRepository()

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Имитация синхронизации данных
                val result = repository.getUpcomingMatches(days = 7)

                if (result.isSuccess) {
                    saveLastSyncTime()
                    Result.success()
                } else {
                    Result.retry()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }

    private fun saveLastSyncTime() {
        applicationContext.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
            .edit()
            .putLong("last_sync_time", System.currentTimeMillis())
            .apply()
    }

    companion object {

        // Запланировать периодическую синхронизацию
        fun schedulePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)  // Требуется интернет
                .setRequiresBatteryNotLow(true)                 // Не при низком заряде
                .setRequiresStorageNotLow(true)                 // Требуется свободное место
                .build()

            val syncWork = PeriodicWorkRequestBuilder<MatchesSyncWorker>(
                repeatInterval = 6,
                repeatIntervalTimeUnit = TimeUnit.HOURS  // Каждые 6 часов
            )
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.HOURS)  // Первый запуск через 1 час
                .addTag("matches_sync_tag")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "matches_sync_work",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                syncWork
            )
        }

        // Запустить одноразовую синхронизацию сейчас
        fun startOneTimeSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWork = androidx.work.OneTimeWorkRequestBuilder<MatchesSyncWorker>()
                .setConstraints(constraints)
                .addTag("matches_sync_tag")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "matches_sync_one_time",
                androidx.work.ExistingWorkPolicy.REPLACE,
                syncWork
            )
        }

        // Отменить синхронизацию
        fun cancelSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork("matches_sync_work")
        }

        // Получить время последней синхронизации
        fun getLastSyncTime(context: Context): Long {
            return context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
                .getLong("last_sync_time", 0)
        }
    }
}