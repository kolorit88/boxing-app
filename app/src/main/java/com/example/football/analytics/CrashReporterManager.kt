package com.example.football.analytics

import com.google.firebase.crashlytics.FirebaseCrashlytics

interface CrashReporter {
    fun log(message: String)
    fun setKey(key: String, value: String)
    fun setUserId(userId: String?)
    fun recordNonFatal(throwable: Throwable, message: String? = null)

    fun logMessage(message: String) {
        log(message)
    }

    fun logError(throwable: Throwable, message: String? = null) {
        recordNonFatal(throwable, message)
    }
}

class FirebaseCrashReporter : CrashReporter {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun setKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setUserId(userId: String?) {
        crashlytics.setUserId(userId.orEmpty())
    }

    override fun recordNonFatal(throwable: Throwable, message: String?) {
        message?.let { crashlytics.log(it) }
        crashlytics.recordException(throwable)
    }
}

object CrashReporterManager {
    lateinit var instance: CrashReporter
        private set

    fun init(crashReporter: CrashReporter) {
        instance = crashReporter
    }
}