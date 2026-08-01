package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profiles")
data class StudentProfile(
    @PrimaryKey val id: String,
    val name: String,
    val gradeClass: String, // e.g., "7-А", "8-Б", "9-В"
    val avatarColorHex: Long = 0xFF3B82F6,
    val totalPoints: Int = 0,
    val streakDays: Int = 1,
    val isCurrentActiveUser: Boolean = false
)
