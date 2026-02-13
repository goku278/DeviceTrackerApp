package com.ominfo.deviceusagetracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppUsageDao {

    @Query("SELECT * FROM app_usage WHERE date = :date")
    suspend fun getTodayUsage(date: String): List<AppUsageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<AppUsageEntity>)

    @Query("SELECT date FROM app_usage ORDER BY date DESC LIMIT 1")
    suspend fun getLastUsageDate(): String?

    @Query("DELETE FROM app_usage")
    suspend fun deleteAll()

    @Query("DELETE FROM app_usage WHERE date < :date")
    suspend fun deleteOldUsage(date: String)

    @Query("SELECT * FROM app_usage WHERE category = :category AND date = :date")
    suspend fun getUsageByCategory(category: String, date: String): List<AppUsageEntity>

    @Query("SELECT SUM(usageMinutes) FROM app_usage WHERE category = :category AND date = :date")
    suspend fun getTotalUsageForCategory(category: String, date: String): Long?
}