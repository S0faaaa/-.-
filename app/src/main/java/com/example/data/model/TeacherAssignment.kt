package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teacher_assignments")
data class TeacherAssignment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gradeClass: String,
    val exerciseType: String,
    val targetRepsOrSeconds: Int,
    val title: String,
    val dueDateString: String,
    val assignedBy: String = "Учитель ФК: Смирнов В.П."
)
