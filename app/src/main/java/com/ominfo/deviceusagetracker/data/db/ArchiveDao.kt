package com.ominfo.deviceusagetracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ArchiveDao {
    
    @Insert
    suspend fun archiveUsage(usage: ArchivedUsageEntity)
    
    @Query("SELECT * FROM archived_usage WHERE date = :date")
    suspend fun getArchivedUsage(date: String): List<ArchivedUsageEntity>
}