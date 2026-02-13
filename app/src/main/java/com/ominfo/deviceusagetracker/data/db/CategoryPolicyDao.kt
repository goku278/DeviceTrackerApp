package com.ominfo.deviceusagetracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryPolicyDao {

    @Query("SELECT * FROM category_policy")
    fun getPolicies(): Flow<List<CategoryPolicyEntity>>

    @Query("SELECT * FROM category_policy")
    suspend fun getPoliciesSync(): List<CategoryPolicyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(policy: CategoryPolicyEntity)

    @Query("SELECT * FROM category_policy WHERE category = :category")
    suspend fun getPolicyByCategory(category: String): CategoryPolicyEntity?

    @Query("DELETE FROM category_policy WHERE category = :category")
    suspend fun deletePolicy(category: String)

    @Query("DELETE FROM category_policy")
    suspend fun deleteAllPolicies()
}