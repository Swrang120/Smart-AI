package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.UserMemoryInsight
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM user_memory_insights ORDER BY recordedAt DESC")
    fun getAllInsights(): Flow<List<UserMemoryInsight>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsight(insight: UserMemoryInsight): Long

    @Query("DELETE FROM user_memory_insights WHERE id = :id")
    suspend fun deleteInsight(id: Long)

    @Query("SELECT COUNT(*) FROM user_memory_insights")
    suspend fun getInsightCount(): Int
}
