package com.ominfo.deviceusagetracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity for archived data
@Entity(tableName = "archived_usage")
data class ArchivedUsageEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val category: String,
    val usageMinutes: Long,
    val date: String
)