package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StudentProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM student_profiles ORDER BY gradeClass ASC, name ASC")
    fun getAllStudents(): Flow<List<StudentProfile>>

    @Query("SELECT * FROM student_profiles WHERE gradeClass = :gradeClass ORDER BY name ASC")
    fun getStudentsInClass(gradeClass: String): Flow<List<StudentProfile>>

    @Query("SELECT * FROM student_profiles WHERE isCurrentActiveUser = 1 LIMIT 1")
    fun getActiveStudent(): Flow<StudentProfile?>

    @Query("SELECT * FROM student_profiles WHERE id = :studentId LIMIT 1")
    suspend fun getStudentById(studentId: String): StudentProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentProfile>)

    @Update
    suspend fun updateStudent(student: StudentProfile)

    @Query("UPDATE student_profiles SET isCurrentActiveUser = (id = :activeStudentId)")
    suspend fun setActiveStudent(activeStudentId: String)
}
