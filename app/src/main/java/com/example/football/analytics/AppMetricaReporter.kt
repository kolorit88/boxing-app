package com.example.football.analytics

import io.appmetrica.analytics.AppMetrica

class AppMetricaReporter : CrashReporter {

    override fun log(message: String) {
        AppMetrica.reportEvent("log", message)
    }

    override fun setKey(key: String, value: String) {
        AppMetrica.reportEvent("custom_key", mapOf(key to value))
    }

    override fun setUserId(userId: String?) {
        userId?.let { AppMetrica.reportEvent("user_id", it) }
    }

    override fun recordNonFatal(throwable: Throwable, message: String?) {
        AppMetrica.reportError(message ?: "Non-fatal error", throwable)
    }
}