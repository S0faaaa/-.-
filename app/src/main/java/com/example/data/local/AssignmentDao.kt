package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.TeacherAssignment
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM teacher_assignments ORDER BY id DESC")
    fun getAllAssignments(): Flow<List<TeacherAssignment>>

    @Query("SELECT * FROM teacher_assignments WHERE gradeClass = :gradeClass ORDER BY id DESC")
    fun getAssignmentsForClass(gradeClass: String): Flow<List<TeacherAssignment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: TeacherAssignment): Long

    @Query("DELETE FROM teacher_assignments WHERE id = :id")
    suspend fun deleteAssignmentById(id: Long)
}
