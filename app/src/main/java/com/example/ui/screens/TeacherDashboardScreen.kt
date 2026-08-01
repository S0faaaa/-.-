package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ExerciseRecord
import com.example.data.model.ExerciseType
import com.example.data.model.StudentProfile
import com.example.data.model.TeacherAssignment

@Composable
fun TeacherDashboardScreen(
    allStudents: List<StudentProfile>,
    allRecords: List<ExerciseRecord>,
    assignments: List<TeacherAssignment>,
    onCreateAssignment: (gradeClass: String, exerciseType: ExerciseType, targetReps: Int, title: String, dueDate: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGradeClassFilter by remember { mutableStateOf("Все") }
    var expandedStudentId by remember { mutableStateOf<String?>(null) }
    var showAddAssignmentDialog by remember { mutableStateOf(false) }

    val gradeClasses = listOf("Все", "7-А", "8-Б", "9-В")

    val filteredStudents = remember(allStudents, selectedGradeClassFilter) {
        if (selectedGradeClassFilter == "Все") allStudents
        else allStudents.filter { it.gradeClass == selectedGradeClassFilter }
    }

    val filteredRecords = remember(allRecords, selectedGradeClassFilter) {
        if (selectedGradeClassFilter == "Все") allRecords
        else allRecords.filter { it.gradeClass == selectedGradeClassFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF)),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Header Card Banner
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEADDFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Кабинет учителя ФК",
                            color = Color(0xFF1C1B1F),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Мониторинг успеваемости и видеоанализ",
                            color = Color(0xFF49454F),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 2. Class Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                gradeClasses.forEach { grade ->
                    val isSelected = grade == selectedGradeClassFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGradeClassFilter = grade },
                        label = {
                            Text(
                                text = if (grade == "Все") "Все классы" else "Класс $grade",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6750A4),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF3EDF7),
                            labelColor = Color(0xFF1C1B1F)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color(0xFFCAC4D0)
                        )
                    )
                }
            }
        }

        // 3. Class Performance Summary Cards
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${filteredStudents.size}",
                            color = Color(0xFF6750A4),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Учеников",
                            color = Color(0xFF49454F),
                            fontSize = 12.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${filteredRecords.size}",
                            color = Color(0xFF4CAF50),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Подходов",
                            color = Color(0xFF49454F),
                            fontSize = 12.sp
                        )
                    }

                    val avgAcc = if (filteredRecords.isNotEmpty())
                        filteredRecords.map { it.accuracyPercentage }.average().toInt()
                    else 0
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (avgAcc > 0) "$avgAcc%" else "-",
                            color = Color(0xFF6750A4),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ср. точность ГТО",
                            color = Color(0xFF49454F),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 4. Assignments Section Header & Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Задания класса",
                    color = Color(0xFF1C1B1F),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { showAddAssignmentDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Назначить", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // List Active Assignments
        items(assignments) { assignment ->
            val type = ExerciseType.fromId(assignment.exerciseType)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEADDFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = assignment.title,
                            color = Color(0xFF1C1B1F),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Класс: ${assignment.gradeClass} • Цель: ${assignment.targetRepsOrSeconds} ${type.unitRu}",
                            color = Color(0xFF49454F),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 5. Student List Header
        item {
            Text(
                text = "Ведомость успеваемости учеников",
                color = Color(0xFF1C1B1F),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
            )
        }

        // List Students
        items(filteredStudents) { student ->
            val studentRecs = filteredRecords.filter { it.studentId == student.id }
            val totalReps = studentRecs.sumOf { it.repsOrSeconds }
            val avgAccuracy = if (studentRecs.isNotEmpty())
                studentRecs.map { it.accuracyPercentage }.average().toInt()
            else 0

            val isExpanded = expandedStudentId == student.id

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedStudentId = if (isExpanded) null else student.id
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEADDFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.name.take(1),
                                color = Color(0xFF21005D),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.name,
                                color = Color(0xFF1C1B1F),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Класс ${student.gradeClass} • ${studentRecs.size} подходов • $totalReps повторов",
                                color = Color(0xFF49454F),
                                fontSize = 12.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEADDFF)
                        ) {
                            Text(
                                text = "$avgAccuracy% ГТО",
                                color = Color(0xFF21005D),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color(0xFF49454F)
                        )
                    }

                    // Expanded Student Workout Log View
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                        ) {
                            Text(
                                text = "История выполнений и видеоанализа:",
                                color = Color(0xFF6750A4),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            if (studentRecs.isEmpty()) {
                                Text(
                                    text = "Записей пока нет",
                                    color = Color(0xFF49454F),
                                    fontSize = 12.sp
                                )
                            } else {
                                studentRecs.forEach { rec ->
                                    val type = ExerciseType.fromId(rec.exerciseType)
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF3EDF7),
                                        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FitnessCenter,
                                                contentDescription = null,
                                                tint = Color(0xFF6750A4),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${type.titleRu}: ${rec.repsOrSeconds} ${type.unitRu}",
                                                    color = Color(0xFF1C1B1F),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Дата: ${rec.dateString} • Точность: ${rec.accuracyPercentage}%",
                                                    color = Color(0xFF49454F),
                                                    fontSize = 11.sp
                                                )
                                            }
                                            Icon(
                                                imageVector = Icons.Default.Videocam,
                                                contentDescription = null,
                                                tint = Color(0xFF4CAF50),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog to Create Assignment
    if (showAddAssignmentDialog) {
        var assignTitle by remember { mutableStateOf("Контрольный зачет") }
        var assignReps by remember { mutableStateOf("20") }
        var assignClass by remember { mutableStateOf("7-А") }
        var assignType by remember { mutableStateOf(ExerciseType.PUSH_UP) }

        Dialog(onDismissRequest = { showAddAssignmentDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF7FF)),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Назначить задание классу",
                        color = Color(0xFF1C1B1F),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = assignTitle,
                        onValueChange = { assignTitle = it },
                        label = { Text("Название задания") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCAC4D0),
                            focusedLabelColor = Color(0xFF6750A4),
                            unfocusedLabelColor = Color(0xFF49454F),
                            focusedTextColor = Color(0xFF1C1B1F),
                            unfocusedTextColor = Color(0xFF1C1B1F)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = assignClass,
                        onValueChange = { assignClass = it },
                        label = { Text("Класс (например: 7-А)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCAC4D0),
                            focusedLabelColor = Color(0xFF6750A4),
                            unfocusedLabelColor = Color(0xFF49454F),
                            focusedTextColor = Color(0xFF1C1B1F),
                            unfocusedTextColor = Color(0xFF1C1B1F)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = assignReps,
                        onValueChange = { assignReps = it },
                        label = { Text("Целевое количество (повторов/сек)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCAC4D0),
                            focusedLabelColor = Color(0xFF6750A4),
                            unfocusedLabelColor = Color(0xFF49454F),
                            focusedTextColor = Color(0xFF1C1B1F),
                            unfocusedTextColor = Color(0xFF1C1B1F)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val reps = assignReps.toIntOrNull() ?: 20
                            onCreateAssignment(assignClass, assignType, reps, assignTitle, "2026-08-02")
                            showAddAssignmentDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Опубликовать задание",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

