package com.ominfo.deviceusagetracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AppUsageEntity::class, CategoryPolicyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usageDao(): AppUsageDao
    abstract fun policyDao(): CategoryPolicyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "usage_db"
                )
                    .fallbackToDestructiveMigration() // Add this for development
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}