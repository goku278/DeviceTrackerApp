package com.ominfo.deviceusagetracker.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ominfo.deviceusagetracker.data.db.AppDatabase
import com.ominfo.deviceusagetracker.data.repository.UsageRepository
import com.ominfo.deviceusagetracker.utils.UsageStatsHelper

class AppUsageWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val database = AppDatabase.get(context)
    private val repository = UsageRepository(
        database.usageDao(),
        database.policyDao()
    )

    override suspend fun doWork(): Result {
        return try {
            Log.d("AppUsageWorker", "Starting periodic usage tracking")

            // Get current usage
            val usageList = UsageStatsHelper.getTodayUsage(applicationContext)

            if (usageList.isNotEmpty()) {
                // Save to database
                repository.saveUsage(usageList)
                Log.d("AppUsageWorker", "Saved ${usageList.size} app usage records")

                // Log category breakdown
                val categoryBreakdown = usageList.groupBy { it.category }
                    .mapValues { it.value.sumOf { it.usageMinutes } }
                Log.d("AppUsageWorker", "Category usage: $categoryBreakdown")
            } else {
                Log.d("AppUsageWorker", "No usage data found")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("AppUsageWorker", "Error tracking usage", e)
            Result.retry()
        }
    }
}