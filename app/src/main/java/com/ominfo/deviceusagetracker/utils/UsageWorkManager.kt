package com.ominfo.deviceusagetracker.utils

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object UsageWorkManager {

    private const val UNIQUE_WORK_NAME = "app_usage_tracking"

    fun schedulePeriodicTracking(
        context: Context,
        intervalMinutes: Long = 15L // default fallback
    ) {

        // WorkManager minimum allowed interval = 15 minutes
        val safeInterval = intervalMinutes.coerceAtLeast(15L)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<AppUsageWorker>(
            safeInterval, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES // flex interval
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                1, TimeUnit.MINUTES
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE, // 🔥 IMPORTANT: allows interval change
            workRequest
        )
    }

    fun stopPeriodicTracking(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    fun triggerImmediateTracking(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<AppUsageWorker>()
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}