package com.example.football

import android.app.Application
import com.example.data.worker.MatchesSyncWorker
import com.example.football.analytics.CrashReporterManager
import com.example.football.analytics.FirebaseCrashReporter
import com.example.football.di.appModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FootballApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Инициализация Firebase
        FirebaseApp.initializeApp(this)

        // Инициализация краш репортера
        val crashReporter = FirebaseCrashReporter()
        CrashReporterManager.init(crashReporter)

        CrashReporterManager.instance.logMessage("App started")
        CrashReporterManager.instance.setKey("app_version", BuildConfig.VERSION_NAME)
        CrashReporterManager.instance.setKey("build_type", BuildConfig.BUILD_TYPE)

        // Обработка глобальных крашей
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            CrashReporterManager.instance.logError(throwable, "Uncaught exception in ${thread.name}")
            defaultHandler?.uncaughtException(thread, throwable)
        }

        startKoin {
            androidContext(this@FootballApp)
            modules(appModule)
        }

        MatchesSyncWorker.schedulePeriodic(this)
    }
}