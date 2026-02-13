package com.ominfo.deviceusagetracker.data.repository

import com.ominfo.deviceusagetracker.R
import com.ominfo.deviceusagetracker.data.db.AppUsageDao
import com.ominfo.deviceusagetracker.data.db.AppUsageEntity
import com.ominfo.deviceusagetracker.data.db.CategoryPolicyDao
import com.ominfo.deviceusagetracker.data.db.CategoryPolicyEntity
import com.ominfo.deviceusagetracker.model.CategoryDashboardModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class UsageRepository(
    private val usageDao: AppUsageDao,
    private val policyDao: CategoryPolicyDao
) {

    suspend fun saveUsage(list: List<AppUsageEntity>) {
        if (list.isNotEmpty()) {
            usageDao.insertAll(list)
        }
    }

    suspend fun getTodayUsage(): List<AppUsageEntity> {
        return usageDao.getTodayUsage(Constants.today())
    }

    fun getPolicies(): Flow<List<CategoryPolicyEntity>> {
        return policyDao.getPolicies()
    }

    suspend fun savePolicy(policy: CategoryPolicyEntity) {
        policyDao.insert(policy)
    }

    suspend fun getPolicyForCategory(category: String): CategoryPolicyEntity? {
        return policyDao.getPolicyByCategory(category)
    }

    suspend fun resetDailyUsageIfNeeded() {
        val lastUsage = try {
            usageDao.getLastUsageDate()
        } catch (e: Exception) {
            null
        }

        if (lastUsage != null && Constants.isNewDay(lastUsage)) {
            usageDao.deleteAll()
        }
    }

    fun getCategoryDashboardData(): Flow<List<CategoryDashboardModel>> {
        return combine(
            getPolicies(),
            flow { emit(getTodayUsage()) }
        ) { policies, usage ->
            val policyMap = policies.associate { it.category to it.dailyLimitMinutes }

            usage.groupBy { it.category }.map { (category, apps) ->
                val used = apps.sumOf { it.usageMinutes }
                val limit = policyMap[category] ?: 60

                CategoryDashboardModel(
                    category = category,
                    usedMinutes = used,
                    limitMinutes = limit,
                    iconRes = Constants.CATEGORY_ICONS[category] ?: R.drawable.ic_default
                )
            }
        }
    }

    suspend fun getCategoryUsage(category: String): List<AppUsageEntity> {
        return usageDao.getUsageByCategory(category, Constants.today())
    }

    suspend fun getCategoryTotalUsage(category: String): Long {
        return usageDao.getTotalUsageForCategory(category, Constants.today()) ?: 0
    }

    suspend fun initializeDefaultPolicies() {
        val existingPolicies = policyDao.getPoliciesSync()
        if (existingPolicies.isEmpty()) {
            Constants.ALL_CATEGORIES.forEach { category ->
                policyDao.insert(
                    CategoryPolicyEntity(
                        category = category,
                        dailyLimitMinutes = 60
                    )
                )
            }
        }
    }
}