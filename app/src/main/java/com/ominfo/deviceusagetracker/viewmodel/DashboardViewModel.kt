package com.ominfo.deviceusagetracker.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ominfo.deviceusagetracker.R
import com.ominfo.deviceusagetracker.data.db.AppDatabase
import com.ominfo.deviceusagetracker.data.db.AppUsageEntity
import com.ominfo.deviceusagetracker.data.db.CategoryPolicyEntity
import com.ominfo.deviceusagetracker.data.repository.UsageRepository
import com.ominfo.deviceusagetracker.model.CategoryDashboardModel
import com.ominfo.deviceusagetracker.utils.AdsManager
import com.ominfo.deviceusagetracker.utils.NotificationHelper
import com.ominfo.deviceusagetracker.utils.UsageStatsHelper
import com.ominfo.deviceusagetracker.view.BlockActivity
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.get(application)
    private val repository = UsageRepository(
        database.usageDao(),
        database.policyDao()
    )

    private val _usage = MutableLiveData<List<AppUsageEntity>>()
    val usage: LiveData<List<AppUsageEntity>> = _usage

    private val _dashboardData = MediatorLiveData<List<CategoryDashboardModel>>()
    val dashboardData: LiveData<List<CategoryDashboardModel>> = _dashboardData

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _blockedCategory = MutableLiveData<String?>()
    val blockedCategory: LiveData<String?> = _blockedCategory

    val policies = repository.getPolicies().asLiveData()

    init {
        // Combine usage and policies to create dashboard data
        _dashboardData.addSource(_usage) { usageList ->
            combineData(usageList, policies.value ?: emptyList())
        }

        _dashboardData.addSource(policies) { policyList ->
            combineData(_usage.value ?: emptyList(), policyList)
        }

        // Load initial data
        loadUsage()
    }

    private fun combineData(usageList: List<AppUsageEntity>, policyList: List<CategoryPolicyEntity>) {
        val policyMap = policyList.associate { it.category to it.dailyLimitMinutes }

        // Filter out system apps and very low usage (less than 1 minute)
        val filteredUsage = usageList.filter {
            it.usageMinutes > 0
        }

        // Group by category
        val groupedByCategory = filteredUsage.groupBy { it.category }

        if (groupedByCategory.isEmpty()) {
            _dashboardData.postValue(emptyList())
            return
        }

        val dashboardList = groupedByCategory.map { (category, apps) ->
            val used = apps.sumOf { it.usageMinutes }
            val limit = policyMap[category] ?: 60 // Default 60 minutes

            // Get the icon resource ID from Constants, with a fallback
            val iconRes = Constants.CATEGORY_ICONS[category] ?: R.drawable.ic_default

            CategoryDashboardModel(
                category = category,
                usedMinutes = used,
                limitMinutes = limit,
                iconRes = iconRes
            )
        }.sortedByDescending { it.usedMinutes } // Sort by most used first

        _dashboardData.postValue(dashboardList)
        checkEnforcement(dashboardList)
    }

    fun loadUsage() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                // Check if we need to reset for new day
                repository.resetDailyUsageIfNeeded()

                // Get fresh usage data
                val list = UsageStatsHelper.getTodayUsage(getApplication())

                // Save to database
                if (list.isNotEmpty()) {
                    repository.saveUsage(list)
                }

                _usage.postValue(list)
                _errorMessage.postValue(null)
            } catch (e: SecurityException) {
                _errorMessage.postValue("Usage access permission denied")
            } catch (e: Exception) {
                _errorMessage.postValue("Error loading usage data: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun checkEnforcement(dashboardList: List<CategoryDashboardModel>) {
        dashboardList.forEach { item ->
            if (item.usedMinutes >= item.limitMinutes && item.limitMinutes > 0) {
                // Show notification for soft enforcement
                NotificationHelper.showLimitReachedNotification(
                    getApplication(),
                    item.category
                )

                // Trigger block activity for hard enforcement
                _blockedCategory.postValue(item.category)
            }
        }
    }

    fun incrementInterstitialCounter(activity: Activity) {
        AdsManager.incrementAndShowInterstitial(activity)
    }

    fun getAppsForCategory(category: String): List<AppUsageEntity> {
        return _usage.value?.filter { it.category == category } ?: emptyList()
    }

    fun clearBlockedCategory() {
        _blockedCategory.postValue(null)
    }
}