package com.example.data.repository

import com.example.data.local.AssignmentDao
import com.example.data.local.ExerciseDao
import com.example.data.local.StudentDao
import com.example.data.model.ExerciseRecord
import com.example.data.model.StudentProfile
import com.example.data.model.TeacherAssignment
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SportRepository(
    private val exerciseDao: ExerciseDao,
    private val studentDao: StudentDao,
    private val assignmentDao: AssignmentDao
) {
    val allRecords: Flow<List<ExerciseRecord>> = exerciseDao.getAllRecords()
    val allStudents: Flow<List<StudentProfile>> = studentDao.getAllStudents()
    val activeStudent: Flow<StudentProfile?> = studentDao.getActiveStudent()
    val allAssignments: Flow<List<TeacherAssignment>> = assignmentDao.getAllAssignments()

    fun getRecordsForStudent(studentId: String): Flow<List<ExerciseRecord>> =
        exerciseDao.getRecordsForStudent(studentId)

    fun getRecordsForDate(dateString: String): Flow<List<ExerciseRecord>> =
        exerciseDao.getRecordsForDate(dateString)

    fun getRecordsForClass(gradeClass: String): Flow<List<ExerciseRecord>> =
        exerciseDao.getRecordsForClass(gradeClass)

    suspend fun insertRecord(record: ExerciseRecord): Long =
        exerciseDao.insertRecord(record)

    suspend fun deleteRecord(id: Long) =
        exerciseDao.deleteRecordById(id)

    suspend fun setActiveStudent(studentId: String) =
        studentDao.setActiveStudent(studentId)

    suspend fun updateStudentPoints(studentId: String, addedPoints: Int) {
        val student = studentDao.getStudentById(studentId)
        if (student != null) {
            val updated = student.copy(
                totalPoints = student.totalPoints + addedPoints
            )
            studentDao.updateStudent(student = updated)
        }
    }

    suspend fun insertAssignment(assignment: TeacherAssignment) =
        assignmentDao.insertAssignment(assignment)

    suspend fun prepopulateInitialDataIfEmpty() {
        // Initial setup for students and records if needed
        val initialStudents = listOf(
            StudentProfile(id = "s1", name = "Иван Иванов", gradeClass = "7-А", avatarColorHex = 0xFF3B82F6, totalPoints = 320, streakDays = 5, isCurrentActiveUser = true),
            StudentProfile(id = "s2", name = "Мария Соколова", gradeClass = "7-А", avatarColorHex = 0xFFEC4899, totalPoints = 480, streakDays = 7, isCurrentActiveUser = false),
            StudentProfile(id = "s3", name = "Алексей Петров", gradeClass = "7-А", avatarColorHex = 0xFF10B981, totalPoints = 210, streakDays = 3, isCurrentActiveUser = false),
            StudentProfile(id = "s4", name = "Дмитрий Волков", gradeClass = "8-Б", avatarColorHex = 0xFFF59E0B, totalPoints = 550, streakDays = 8, isCurrentActiveUser = false),
            StudentProfile(id = "s5", name = "Елена Ковалева", gradeClass = "8-Б", avatarColorHex = 0xFF8B5CF6, totalPoints = 390, streakDays = 4, isCurrentActiveUser = false),
            StudentProfile(id = "s6", name = "София Смирнова", gradeClass = "9-В", avatarColorHex = 0xFF06B6D4, totalPoints = 610, streakDays = 12, isCurrentActiveUser = false)
        )
        studentDao.insertStudents(initialStudents)

        // Generate date strings for recent days
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()

        val todayStr = sdf.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = sdf.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val day2AgoStr = sdf.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, -2)
        val day4AgoStr = sdf.format(cal.time)

        val sampleRecords = listOf(
            ExerciseRecord(
                studentId = "s1", studentName = "Иван Иванов", gradeClass = "7-А",
                exerciseType = "PUSH_UP", repsOrSeconds = 20, accuracyPercentage = 95,
                durationSeconds = 45, caloriesBurned = 10, dateString = todayStr,
                notes = "Отличная фиксация локтей!"
            ),
            ExerciseRecord(
                studentId = "s1", studentName = "Иван Иванов", gradeClass = "7-А",
                exerciseType = "PLANK", repsOrSeconds = 60, accuracyPercentage = 98,
                durationSeconds = 60, caloriesBurned = 12, dateString = todayStr,
                notes = "Корпус ровный весь подход"
            ),
            ExerciseRecord(
                studentId = "s1", studentName = "Иван Иванов", gradeClass = "7-А",
                exerciseType = "SQUAT", repsOrSeconds = 25, accuracyPercentage = 92,
                durationSeconds = 50, caloriesBurned = 10, dateString = yesterdayStr,
                notes = "Глубокий присед"
            ),
            ExerciseRecord(
                studentId = "s1", studentName = "Иван Иванов", gradeClass = "7-А",
                exerciseType = "CRUNCH", repsOrSeconds = 30, accuracyPercentage = 90,
                durationSeconds = 55, caloriesBurned = 11, dateString = day2AgoStr,
                notes = "Подъем туловища в сед"
            ),
            ExerciseRecord(
                studentId = "s2", studentName = "Мария Соколова", gradeClass = "7-А",
                exerciseType = "PUSH_UP", repsOrSeconds = 18, accuracyPercentage = 96,
                durationSeconds = 40, caloriesBurned = 9, dateString = todayStr,
                notes = "Техника ГТО 100%"
            ),
            ExerciseRecord(
                studentId = "s2", studentName = "Мария Соколова", gradeClass = "7-А",
                exerciseType = "LUNGE", repsOrSeconds = 24, accuracyPercentage = 94,
                durationSeconds = 60, caloriesBurned = 11, dateString = yesterdayStr,
                notes = "Угол 90 градусов выдержан"
            ),
            ExerciseRecord(
                studentId = "s4", studentName = "Дмитрий Волков", gradeClass = "8-Б",
                exerciseType = "SQUAT", repsOrSeconds = 35, accuracyPercentage = 97,
                durationSeconds = 70, caloriesBurned = 14, dateString = day4AgoStr,
                notes = "Рекорд класса!"
            )
        )

        for (record in sampleRecords) {
            exerciseDao.insertRecord(record)
        }

        val sampleAssignments = listOf(
            TeacherAssignment(
                gradeClass = "7-А",
                exerciseType = "PUSH_UP",
                targetRepsOrSeconds = 20,
                title = "Разгибание рук в упоре лёжа (Норматив)",
                dueDateString = todayStr
            ),
            TeacherAssignment(
                gradeClass = "7-А",
                exerciseType = "PLANK",
                targetRepsOrSeconds = 45,
                title = "Удержание планки на оценку 5",
                dueDateString = yesterdayStr
            ),
            TeacherAssignment(
                gradeClass = "8-Б",
                exerciseType = "SQUAT",
                targetRepsOrSeconds = 30,
                title = "Приседания — Контрольный зачет",
                dueDateString = todayStr
            )
        )

        for (assignment in sampleAssignments) {
            assignmentDao.insertAssignment(assignment)
        }
    }
}
