package com.ominfo.deviceusagetracker.utils

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.ominfo.deviceusagetracker.data.db.AppUsageEntity
import java.util.Calendar

object UsageStatsHelper {

    private const val TAG = "UsageStatsHelper"

    fun getTodayUsage(context: Context): List<AppUsageEntity> {
        val usageManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val stats = usageManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            calendar.timeInMillis,
            System.currentTimeMillis()
        ) ?: emptyList()

        Log.d(TAG, "Found ${stats.size} usage stats")

        if (stats.isEmpty()) {
            return emptyList()
        }

        val pm = context.packageManager
        val todayDate = Constants.today()
        val result = mutableListOf<AppUsageEntity>()

        stats.forEach { usageStats ->
            try {
                // Skip if no foreground time
                if (usageStats.totalTimeInForeground <= 0) return@forEach

                // Log all package names for debugging
                Log.d(TAG, "Processing package: ${usageStats.packageName}, Time: ${usageStats.totalTimeInForeground / 60000}min")

                val appInfo = try {
                    pm.getApplicationInfo(usageStats.packageName, 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.d(TAG, "Package not found: ${usageStats.packageName}")
                    return@forEach
                }

                // Skip system apps
                if (isSystemApp(usageStats.packageName)) {
                    Log.d(TAG, "Skipping system app: ${usageStats.packageName}")
                    return@forEach
                }

                val appName = pm.getApplicationLabel(appInfo).toString()

                // Convert milliseconds to minutes (round to nearest minute)
                val minutes = (usageStats.totalTimeInForeground / 60000).toInt()

                if (minutes > 0) {
                    val category = Constants.getCategory(usageStats.packageName)
                    Log.d(TAG, "✅ App: $appName, Category: $category, Minutes: $minutes, Package: ${usageStats.packageName}")

                    result.add(
                        AppUsageEntity(
                            packageName = usageStats.packageName,
                            appName = appName,
                            category = category,
                            usageMinutes = minutes.toLong(),
                            date = todayDate
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing app: ${usageStats.packageName}", e)
            }
        }

        Log.d(TAG, "Total apps with usage today: ${result.size}")

        // Log category breakdown
        val categoryBreakdown = result.groupBy { it.category }
            .mapValues { it.value.sumOf { app -> app.usageMinutes } }
        Log.d(TAG, "Category breakdown: $categoryBreakdown")

        return result
    }

    private fun isSystemApp(packageName: String): Boolean {
        val systemPrefixes = listOf(
            "com.android.",
            "com.google.android.",
            "android",
            "com.sec.android.",
            "com.samsung.android.",
            "com.qualcomm.",
            "com.mediatek.",
            "com.miui.",
            "com.oneplus.",
            "com.vivo.",
            "com.oppo.",
            "com.lge.",
            "com.sony.",
            "com.huawei.",
            "com.asus.",
            "com.lenovo",
            "com.motorola",
            "com.nokia",
            "com.htc"
        )
        return systemPrefixes.any { packageName.startsWith(it) }
    }
}