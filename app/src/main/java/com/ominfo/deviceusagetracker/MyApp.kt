package com.ominfo.deviceusagetracker

import android.app.Application
import com.ominfo.deviceusagetracker.data.db.AppDatabase
import com.ominfo.deviceusagetracker.data.db.CategoryPolicyEntity
import com.ominfo.deviceusagetracker.data.repository.UsageRepository
import com.ominfo.deviceusagetracker.utils.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.ominfo.deviceusagetracker.utils.UsageWorkManager
import kotlinx.coroutines.*

class MyApp : Application() {

    companion object {
        const val APP_USAGE_WORK_NAME = "app_usage_periodic_work"
        private const val TAG = "MyApp"
    }

    override fun onCreate() {
        super.onCreate()

        // 1️⃣ Create Notification Channel
        NotificationHelper.createNotificationChannel(this)

        // 2️⃣ Initialize default policies
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.get(this@MyApp)
            val repository = UsageRepository(
                database.usageDao(),
                database.policyDao()
            )
            repository.initializeDefaultPolicies()
        }

        // 3️⃣ Initialize Firebase Remote Config
        initRemoteConfig()
    }

    private fun initRemoteConfig() {

        val remoteConfig = Firebase.remoteConfig

        // Config settings
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour in production
        }

        remoteConfig.setConfigSettingsAsync(configSettings)

        // Default fallback values
        remoteConfig.setDefaultsAsync(
            mapOf(
                "tracking_enable" to true,
                "tracking_interval_minutes" to 15L
            )
        )

        // Fetch & activate
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    Log.d(TAG, "Remote Config fetched successfully")
                } else {
                    Log.d(TAG, "Remote Config fetch failed, using defaults")
                }

                applyTrackingConfig(remoteConfig)
            }
    }

    private fun applyTrackingConfig(remoteConfig: com.google.firebase.remoteconfig.FirebaseRemoteConfig) {

        val trackingEnabled =
            remoteConfig.getBoolean("tracking_enable")

        val intervalMinutes =
            remoteConfig.getLong("tracking_interval_minutes")

        if (trackingEnabled) {
            UsageWorkManager.schedulePeriodicTracking(
                this,
                intervalMinutes
            )
        } else {
            UsageWorkManager.stopPeriodicTracking(this)
        }
    }
}