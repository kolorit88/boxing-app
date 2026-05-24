package com.example.football.analytics


class MultiCrashReporter(private val reporters: List<CrashReporter>) : CrashReporter {

    override fun log(message: String) {
        reporters.forEach { it.log(message) }
    }

    override fun setKey(key: String, value: String) {
        reporters.forEach { it.setKey(key, value) }
    }

    override fun setUserId(userId: String?) {
        reporters.forEach { it.setUserId(userId) }
    }

    override fun recordNonFatal(throwable: Throwable, message: String?) {
        reporters.forEach { it.recordNonFatal(throwable, message) }
    }
}