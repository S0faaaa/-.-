package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ExerciseRecord
import com.example.data.model.ExerciseType
import com.example.data.model.StudentProfile
import com.example.data.model.TeacherAssignment
import com.example.data.repository.SportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class UserRole {
    STUDENT,
    TEACHER
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = SportRepository(
        exerciseDao = db.exerciseDao(),
        studentDao = db.studentDao(),
        assignmentDao = db.assignmentDao()
    )

    private val _userRole = MutableStateFlow(UserRole.STUDENT)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val _selectedDateString = MutableStateFlow(sdf.format(Date()))
    val selectedDateString: StateFlow<String> = _selectedDateString.asStateFlow()

    val allStudents: StateFlow<List<StudentProfile>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeStudent: StateFlow<StudentProfile?> = repository.activeStudent
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allRecords: StateFlow<List<ExerciseRecord>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAssignments: StateFlow<List<TeacherAssignment>> = repository.allAssignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active student's records for selected calendar date
    val studentRecordsForSelectedDate: StateFlow<List<ExerciseRecord>> = combine(
        activeStudent,
        selectedDateString,
        allRecords
    ) { student, dateStr, records ->
        if (student == null) emptyList()
        else records.filter { it.studentId == student.id && it.dateString == dateStr }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All active student's records
    val activeStudentRecords: StateFlow<List<ExerciseRecord>> = combine(
        activeStudent,
        allRecords
    ) { student, records ->
        if (student == null) emptyList()
        else records.filter { it.studentId == student.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.prepopulateInitialDataIfEmpty()
        }
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    fun setSelectedDate(dateString: String) {
        _selectedDateString.value = dateString
    }

    fun switchActiveStudent(studentId: String) {
        viewModelScope.launch {
            repository.setActiveStudent(studentId)
        }
    }

    fun saveWorkoutResult(
        exerciseType: ExerciseType,
        repsOrSeconds: Int,
        accuracyPercentage: Int,
        durationSeconds: Int,
        caloriesBurned: Int
    ) {
        viewModelScope.launch {
            val student = activeStudent.value ?: return@launch
            val dateStr = _selectedDateString.value

            val noteMsg = when {
                accuracyPercentage >= 95 -> "Идеальное выполнение ГТО 🔥"
                accuracyPercentage >= 85 -> "Хорошая техника"
                else -> "Требуется доработка формы"
            }

            val record = ExerciseRecord(
                studentId = student.id,
                studentName = student.name,
                gradeClass = student.gradeClass,
                exerciseType = exerciseType.id,
                repsOrSeconds = repsOrSeconds,
                accuracyPercentage = accuracyPercentage,
                durationSeconds = durationSeconds,
                caloriesBurned = caloriesBurned,
                dateString = dateStr,
                notes = noteMsg,
                videoRecorded = true
            )

            repository.insertRecord(record)
            val pointsEarned = (repsOrSeconds * 2) + (accuracyPercentage / 2)
            repository.updateStudentPoints(student.id, pointsEarned)
        }
    }

    fun createTeacherAssignment(
        gradeClass: String,
        exerciseType: ExerciseType,
        targetRepsOrSeconds: Int,
        title: String,
        dueDateString: String
    ) {
        viewModelScope.launch {
            val assignment = TeacherAssignment(
                gradeClass = gradeClass,
                exerciseType = exerciseType.id,
                targetRepsOrSeconds = targetRepsOrSeconds,
                title = title,
                dueDateString = dueDateString
            )
            repository.insertAssignment(assignment)
        }
    }

    fun addPlannedWorkout(
        exerciseType: ExerciseType,
        targetRepsOrSeconds: Int,
        title: String,
        dueDateString: String
    ) {
        viewModelScope.launch {
            val studentClass = activeStudent.value?.gradeClass ?: "7-А"
            val assignment = TeacherAssignment(
                gradeClass = studentClass,
                exerciseType = exerciseType.id,
                targetRepsOrSeconds = targetRepsOrSeconds,
                title = title,
                dueDateString = dueDateString,
                assignedBy = "Мой план тренировки"
            )
            repository.insertAssignment(assignment)
        }
    }
}
