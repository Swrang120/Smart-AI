package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_memory_insights")
data class UserMemoryInsight(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // "Life Journey", "Emotional Anchor", "Goal", "Preference"
    val insightText: String,
    val recordedAt: Long = System.currentTimeMillis()
)
