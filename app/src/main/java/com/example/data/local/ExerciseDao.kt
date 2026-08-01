package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ExerciseRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<ExerciseRecord>>

    @Query("SELECT * FROM exercise_records WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getRecordsForStudent(studentId: String): Flow<List<ExerciseRecord>>

    @Query("SELECT * FROM exercise_records WHERE dateString = :dateString ORDER BY timestamp DESC")
    fun getRecordsForDate(dateString: String): Flow<List<ExerciseRecord>>

    @Query("SELECT * FROM exercise_records WHERE studentId = :studentId AND dateString = :dateString ORDER BY timestamp DESC")
    fun getRecordsForStudentAndDate(studentId: String, dateString: String): Flow<List<ExerciseRecord>>

    @Query("SELECT * FROM exercise_records WHERE gradeClass = :gradeClass ORDER BY timestamp DESC")
    fun getRecordsForClass(gradeClass: String): Flow<List<ExerciseRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ExerciseRecord): Long

    @Query("DELETE FROM exercise_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)
}
