package com.example.football

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.tasks.await

object FirebaseRemoteConfigService {

    private val remoteConfig = Firebase.remoteConfig

    object Keys {
        const val APP_VERSION = "app_version"
        const val FORCE_UPDATE = "force_update"
        const val MAINTENANCE_MODE = "maintenance_mode"
        const val FEATURE_SEARCH_ENABLED = "feature_search_enabled"
        const val FEATURE_ANIMATIONS_ENABLED = "feature_animations_enabled"
        const val DEFAULT_LEAGUE = "default_league"
        const val UPDATE_MESSAGE = "update_message"
    }

    private val defaultConfig = mapOf(
        Keys.APP_VERSION to "1.0.0",
        Keys.FORCE_UPDATE to false,
        Keys.MAINTENANCE_MODE to false,
        Keys.FEATURE_SEARCH_ENABLED to true,
        Keys.FEATURE_ANIMATIONS_ENABLED to true,
        Keys.DEFAULT_LEAGUE to "АПЛ",
        Keys.UPDATE_MESSAGE to "Доступна новая версия!"
    )

    suspend fun init() {
        try {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(defaultConfig)

            fetchAndActivate()
            Log.d("FirebaseConfig", "Remote Config инициализирован")
        } catch (e: Exception) {
            Log.e("FirebaseConfig", "Ошибка инициализации: ${e.message}")
        }
    }

    suspend fun fetchAndActivate() {
        try {
            remoteConfig.fetchAndActivate().await()
            Log.d("FirebaseConfig", "Remote Config обновлен")
        } catch (e: Exception) {
            Log.e("FirebaseConfig", "Ошибка обновления: ${e.message}")
        }
    }

    fun getAppVersion(): String = remoteConfig.getString(Keys.APP_VERSION)
    fun isForceUpdate(): Boolean = remoteConfig.getBoolean(Keys.FORCE_UPDATE)
    fun isMaintenanceMode(): Boolean = remoteConfig.getBoolean(Keys.MAINTENANCE_MODE)
    fun isFeatureSearchEnabled(): Boolean = remoteConfig.getBoolean(Keys.FEATURE_SEARCH_ENABLED)
    fun isFeatureAnimationsEnabled(): Boolean = remoteConfig.getBoolean(Keys.FEATURE_ANIMATIONS_ENABLED)
    fun getDefaultLeague(): String = remoteConfig.getString(Keys.DEFAULT_LEAGUE)
    fun getUpdateMessage(): String = remoteConfig.getString(Keys.UPDATE_MESSAGE)
}