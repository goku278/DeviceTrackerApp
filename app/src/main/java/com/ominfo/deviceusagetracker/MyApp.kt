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

/*
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize default policies
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.get(this@MyApp)
            val policies = database.policyDao().getPolicies().firstOrNull()

            if (policies.isNullOrEmpty()) {
                val defaultPolicies = listOf(
                    CategoryPolicyEntity("Social", 60),
                    CategoryPolicyEntity("Entertainment", 60),
                    CategoryPolicyEntity("Others", 60)
                )

                defaultPolicies.forEach { policy ->
                    database.policyDao().insert(policy)
                }
            }
        }
    }
}*/


import com.ominfo.deviceusagetracker.utils.UsageWorkManager

class MyApp : Application() {

    companion object {
        const val APP_USAGE_WORK_NAME = "app_usage_periodic_work"
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize notification channel
        NotificationHelper.createNotificationChannel(this)

        // Initialize default policies
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.get(this@MyApp)
            val repository = UsageRepository(
                database.usageDao(),
                database.policyDao()
            )
            repository.initializeDefaultPolicies()
        }

        // Schedule periodic usage tracking using UsageWorkManager
        UsageWorkManager.schedulePeriodicTracking(this)
    }
}