package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ExerciseType
import com.example.ui.camera.ExerciseTrackerView
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.ExercisesScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TeacherDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UserRole
import kotlinx.coroutines.delay

enum class AppTab {
    EXERCISES,
    CALENDAR,
    TEACHER,
    PROFILE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppShell()
            }
        }
    }
}

@Composable
fun MainAppShell(
    viewModel: MainViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(AppTab.EXERCISES) }
    var activeTrackingExercise by remember { mutableStateOf<ExerciseType?>(null) }
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1800)
        showSplash = false
    }

    val activeStudent by viewModel.activeStudent.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val selectedDateString by viewModel.selectedDateString.collectAsStateWithLifecycle()
    val studentRecordsForSelectedDate by viewModel.studentRecordsForSelectedDate.collectAsStateWithLifecycle()
    val activeStudentRecords by viewModel.activeStudentRecords.collectAsStateWithLifecycle()
    val allRecords by viewModel.allRecords.collectAsStateWithLifecycle()
    val allAssignments by viewModel.allAssignments.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
    ) {
        if (activeTrackingExercise != null) {
            // Live Camera Motion Tracker Fullscreen Screen
            ExerciseTrackerView(
                exerciseType = activeTrackingExercise!!,
                onFinishWorkout = { repsOrSeconds, accuracyPercentage, durationSeconds, caloriesBurned ->
                    viewModel.saveWorkoutResult(
                        exerciseType = activeTrackingExercise!!,
                        repsOrSeconds = repsOrSeconds,
                        accuracyPercentage = accuracyPercentage,
                        durationSeconds = durationSeconds,
                        caloriesBurned = caloriesBurned
                    )
                    activeTrackingExercise = null
                    selectedTab = AppTab.CALENDAR
                },
                onBackClick = {
                    activeTrackingExercise = null
                }
            )
        } else {
            // Main Bottom Bar Navigation Scaffold
            Scaffold(
                containerColor = Color(0xFFFEF7FF),
                bottomBar = {
                    NavigationBar(
                        containerColor = Color(0xFFF3EDF7),
                        tonalElevation = 2.dp
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == AppTab.EXERCISES,
                            onClick = { selectedTab = AppTab.EXERCISES },
                            icon = { Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null) },
                            label = { Text(text = "Упражнения", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1D192B),
                                selectedTextColor = Color(0xFF1D192B),
                                indicatorColor = Color(0xFFE8DEF8),
                                unselectedIconColor = Color(0xFF49454F),
                                unselectedTextColor = Color(0xFF49454F)
                            )
                        )

                        NavigationBarItem(
                            selected = selectedTab == AppTab.CALENDAR,
                            onClick = { selectedTab = AppTab.CALENDAR },
                            icon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null) },
                            label = { Text(text = "Календарь", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1D192B),
                                selectedTextColor = Color(0xFF1D192B),
                                indicatorColor = Color(0xFFE8DEF8),
                                unselectedIconColor = Color(0xFF49454F),
                                unselectedTextColor = Color(0xFF49454F)
                            )
                        )

                        NavigationBarItem(
                            selected = selectedTab == AppTab.TEACHER,
                            onClick = { selectedTab = AppTab.TEACHER },
                            icon = { Icon(imageVector = Icons.Default.School, contentDescription = null) },
                            label = { Text(text = "Учитель", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1D192B),
                                selectedTextColor = Color(0xFF1D192B),
                                indicatorColor = Color(0xFFE8DEF8),
                                unselectedIconColor = Color(0xFF49454F),
                                unselectedTextColor = Color(0xFF49454F)
                            )
                        )

                        NavigationBarItem(
                            selected = selectedTab == AppTab.PROFILE,
                            onClick = { selectedTab = AppTab.PROFILE },
                            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                            label = { Text(text = "Профиль", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1D192B),
                                selectedTextColor = Color(0xFF1D192B),
                                indicatorColor = Color(0xFFE8DEF8),
                                unselectedIconColor = Color(0xFF49454F),
                                unselectedTextColor = Color(0xFF49454F)
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        AppTab.EXERCISES -> {
                            ExercisesScreen(
                                activeStudent = activeStudent,
                                onSelectExercise = { exercise ->
                                    activeTrackingExercise = exercise
                                }
                            )
                        }

                        AppTab.CALENDAR -> {
                            CalendarScreen(
                                selectedDateString = selectedDateString,
                                dayRecords = studentRecordsForSelectedDate,
                                allStudentRecords = activeStudentRecords,
                                allAssignments = allAssignments,
                                activeStudent = activeStudent,
                                onDateSelect = { dateStr ->
                                    viewModel.setSelectedDate(dateStr)
                                },
                                onStartExercise = { exercise ->
                                    activeTrackingExercise = exercise
                                },
                                onAddPlannedWorkout = { exerciseType, target, title, dateStr ->
                                    viewModel.addPlannedWorkout(exerciseType, target, title, dateStr)
                                }
                            )
                        }

                        AppTab.TEACHER -> {
                            TeacherDashboardScreen(
                                allStudents = allStudents,
                                allRecords = allRecords,
                                assignments = allAssignments,
                                onCreateAssignment = { gradeClass, exerciseType, targetReps, title, dueDate ->
                                    viewModel.createTeacherAssignment(gradeClass, exerciseType, targetReps, title, dueDate)
                                }
                            )
                        }

                        AppTab.PROFILE -> {
                            ProfileScreen(
                                activeStudent = activeStudent,
                                allStudents = allStudents,
                                userRole = userRole,
                                onRoleChange = { role ->
                                    viewModel.setUserRole(role)
                                    if (role == UserRole.TEACHER) {
                                        selectedTab = AppTab.TEACHER
                                    }
                                },
                                onSwitchStudent = { studentId ->
                                    viewModel.switchActiveStudent(studentId)
                                }
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showSplash,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SplashScreenView(onDismiss = { showSplash = false })
        }
    }
}

@Composable
fun SplashScreenView(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF003884),
                        Color(0xFF0055B8),
                        Color(0xFF0284C7)
                    )
                )
            )
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_msport_splash_logo_1785598850872),
                    contentDescription = "М.Спорт Заставка",
                    modifier = Modifier
                        .size(148.dp)
                        .clip(RoundedCornerShape(30.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "М.Спорт",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ТВОЙ ПУТЬ К ПОБЕДЕ",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(44.dp))

            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppShellPreview() {
    MyApplicationTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                        label = { Text("Упражнения") }
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                ExercisesScreen(
                    activeStudent = null,
                    onSelectExercise = {}
                )
            }
        }
    }
}
