package com.ominfo.deviceusagetracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_policy")
data class CategoryPolicyEntity(
    @PrimaryKey val category: String,
    val dailyLimitMinutes: Int
)