package com.ominfo.deviceusagetracker.utils

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object UsageWorkManager {
    
    private const val UNIQUE_WORK_NAME = "app_usage_tracking"
    
    fun schedulePeriodicTracking(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .build()
        
        val workRequest = PeriodicWorkRequestBuilder<AppUsageWorker>(
            15, TimeUnit.MINUTES, // Track every 15 minutes
            5, TimeUnit.MINUTES    // Flexible within 5 minutes
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                1, TimeUnit.MINUTES
            )
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // or REPLACE if you want to update
            workRequest
        )
    }
    
    fun stopPeriodicTracking(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }
    
    fun triggerImmediateTracking(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<AppUsageWorker>()
            .build()
        
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}