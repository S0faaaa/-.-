package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_records")
data class ExerciseRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val studentName: String,
    val gradeClass: String,
    val exerciseType: String, // PUSH_UP, SQUAT, etc.
    val repsOrSeconds: Int,
    val accuracyPercentage: Int, // e.g. 94%
    val durationSeconds: Int,
    val caloriesBurned: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String, // "YYYY-MM-DD"
    val notes: String = "",
    val videoRecorded: Boolean = true
)
