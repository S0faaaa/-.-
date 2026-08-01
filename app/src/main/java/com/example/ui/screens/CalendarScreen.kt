package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExerciseRecord
import com.example.data.model.ExerciseType
import com.example.data.model.StudentProfile
import com.example.data.model.TeacherAssignment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private data class CalendarDayItem(
    val dateStr: String,
    val dayLabel: String,
    val weekLabel: String,
    val isToday: Boolean,
    val isFuture: Boolean
)

@Composable
fun CalendarScreen(
    selectedDateString: String,
    dayRecords: List<ExerciseRecord>,
    allStudentRecords: List<ExerciseRecord>,
    allAssignments: List<TeacherAssignment> = emptyList(),
    activeStudent: StudentProfile? = null,
    onDateSelect: (String) -> Unit,
    onStartExercise: ((ExerciseType) -> Unit)? = null,
    onAddPlannedWorkout: ((exerciseType: ExerciseType, target: Int, title: String, dateStr: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showAddPlanDialog by remember { mutableStateOf(false) }
    var selectedFilterTabIndex by remember { mutableIntStateOf(0) } // 0: Все, 1: Запланированные, 2: Выполненные

    val sdfDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val todayStr = remember { sdfDate.format(Date()) }

    // Generate date wheel items: 14 days in past, today, 14 days in future
    val calendarDays = remember {
        val list = mutableListOf<CalendarDayItem>()
        val sdfDay = SimpleDateFormat("d MMM", Locale("ru"))
        val sdfWeek = SimpleDateFormat("EEE", Locale("ru"))

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -14)

        for (i in -14..14) {
            val dStr = sdfDate.format(cal.time)
            val dLabel = sdfDay.format(cal.time)
            val wLabel = sdfWeek.format(cal.time).uppercase()
            val isToday = (dStr == todayStr)
            val isFuture = cal.time.after(Date()) && !isToday

            list.add(
                CalendarDayItem(
                    dateStr = dStr,
                    dayLabel = dLabel,
                    weekLabel = wLabel,
                    isToday = isToday,
                    isFuture = isFuture
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    // Dates with completed records
    val datesWithCompletedWorkouts = remember(allStudentRecords) {
        allStudentRecords.map { it.dateString }.toSet()
    }

    // Dates with planned assignments
    val studentClass = activeStudent?.gradeClass ?: "7-А"
    val relevantAssignments = remember(allAssignments, studentClass) {
        allAssignments.filter { it.gradeClass == studentClass }
    }
    val datesWithPlannedWorkouts = remember(relevantAssignments) {
        relevantAssignments.map { it.dueDateString }.toSet()
    }

    // Filtered assignments for the selected date
    val dayAssignments = remember(relevantAssignments, selectedDateString) {
        relevantAssignments.filter { it.dueDateString == selectedDateString }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // 1. Header Card
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
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Color(0xFF6750A4),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Календарь и План тренировок",
                                color = Color(0xFF1C1B1F),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Просмотр истории и планирование занятий",
                                color = Color(0xFF49454F),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // 2. Extended Interactive Calendar Selector
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Выберите дату:",
                            color = Color(0xFF1C1B1F),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(onClick = { onDateSelect(todayStr) }) {
                            Text(
                                text = "Сегодня",
                                color = Color(0xFF6750A4),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(calendarDays) { day ->
                            val isSelected = (day.dateStr == selectedDateString)
                            val hasCompleted = datesWithCompletedWorkouts.contains(day.dateStr)
                            val hasPlanned = datesWithPlannedWorkouts.contains(day.dateStr)

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = when {
                                    isSelected -> Color(0xFF6750A4)
                                    day.isToday -> Color(0xFFE8DEF8)
                                    hasCompleted -> Color(0xFFE8F5E9)
                                    hasPlanned -> Color(0xFFE0F2FE)
                                    else -> Color(0xFFF3EDF7)
                                },
                                border = BorderStroke(
                                    width = if (isSelected || day.isToday) 2.dp else 1.dp,
                                    color = when {
                                        isSelected -> Color(0xFF6750A4)
                                        day.isToday -> Color(0xFF6750A4)
                                        else -> Color(0xFFCAC4D0)
                                    }
                                ),
                                modifier = Modifier.clickable { onDateSelect(day.dateStr) }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = day.weekLabel,
                                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF49454F),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = day.dayLabel,
                                        color = if (isSelected) Color.White else Color(0xFF1C1B1F),
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Status badges on calendar day pill
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (hasCompleted) {
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) Color.White else Color(0xFF2E7D32))
                                            )
                                        }
                                        if (hasPlanned) {
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) Color(0xFFFFD54F) else Color(0xFF0284C7))
                                            )
                                        }
                                        if (!hasCompleted && !hasPlanned) {
                                            Spacer(modifier = Modifier.height(7.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Stats Summary Card for Selected Date
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${dayRecords.size}",
                                color = Color(0xFF2E7D32),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Выполнено",
                                color = Color(0xFF49454F),
                                fontSize = 12.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${dayAssignments.size}",
                                color = Color(0xFF0284C7),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Запланировано",
                                color = Color(0xFF49454F),
                                fontSize = 12.sp
                            )
                        }

                        val totalReps = dayRecords.sumOf { it.repsOrSeconds }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$totalReps",
                                color = Color(0xFF6750A4),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Всего повторов",
                                color = Color(0xFF49454F),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // 4. Tab Filter Row: [Все], [Запланированные], [Выполненные]
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilterTabIndex == 0,
                        onClick = { selectedFilterTabIndex = 0 },
                        label = { Text("Все (${dayAssignments.size + dayRecords.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFEADDFF),
                            selectedLabelColor = Color(0xFF21005D)
                        )
                    )
                    FilterChip(
                        selected = selectedFilterTabIndex == 1,
                        onClick = { selectedFilterTabIndex = 1 },
                        label = { Text("Запланированные (${dayAssignments.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE0F2FE),
                            selectedLabelColor = Color(0xFF0369A1)
                        )
                    )
                    FilterChip(
                        selected = selectedFilterTabIndex == 2,
                        onClick = { selectedFilterTabIndex = 2 },
                        label = { Text("Выполненные (${dayRecords.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE8F5E9),
                            selectedLabelColor = Color(0xFF1B5E20)
                        )
                    )
                }
            }

            // 5. PLANNED WORKOUTS SECTION
            if (selectedFilterTabIndex == 0 || selectedFilterTabIndex == 1) {
                if (dayAssignments.isNotEmpty()) {
                    item {
                        Text(
                            text = "📅 Запланированные занятия:",
                            color = Color(0xFF1C1B1F),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }

                    items(dayAssignments) { assignment ->
                        val exType = ExerciseType.fromId(assignment.exerciseType)
                        val isDone = dayRecords.any { it.exerciseType == assignment.exerciseType }

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDone) Color(0xFFF0FDF4) else Color(0xFFF0F9FF)
                            ),
                            border = BorderStroke(1.dp, if (isDone) Color(0xFF86EFAC) else Color(0xFFBAE6FD)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isDone) Color(0xFFDCFCE7) else Color(0xFFE0F2FE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = if (isDone) Color(0xFF16A34A) else Color(0xFF0284C7),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = assignment.title,
                                            color = Color(0xFF0F172A),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Цель: ${assignment.targetRepsOrSeconds} ${exType.unitRu} (${exType.titleRu})",
                                            color = Color(0xFF0284C7),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Назначил: ${assignment.assignedBy}",
                                            color = Color(0xFF64748B),
                                            fontSize = 11.sp
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isDone) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            text = if (isDone) "Выполнено ✅" else "Запланировано",
                                            color = if (isDone) Color(0xFF15803D) else Color(0xFFB45309),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                if (!isDone && onStartExercise != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { onStartExercise(exType) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Начать тренировку с ИИ-камерой",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. COMPLETED PAST WORKOUTS SECTION
            if (selectedFilterTabIndex == 0 || selectedFilterTabIndex == 2) {
                if (dayRecords.isNotEmpty()) {
                    item {
                        Text(
                            text = "✅ Выполненные тренировки:",
                            color = Color(0xFF1C1B1F),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }

                    items(dayRecords) { record ->
                        val type = ExerciseType.fromId(record.exerciseType)
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFEADDFF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            tint = Color(0xFF6750A4),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = type.titleRu,
                                            color = Color(0xFF1C1B1F),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${record.repsOrSeconds} ${type.unitRu} • ${record.caloriesBurned} ккал",
                                            color = Color(0xFF6750A4),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFE8F5E9)
                                    ) {
                                        Text(
                                            text = "${record.accuracyPercentage}% точность",
                                            color = Color(0xFF2E7D32),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                if (record.notes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF3EDF7),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "💬 ИИ Аналитик: ${record.notes}",
                                            color = Color(0xFF49454F),
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }

                                if (record.videoRecorded) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Видеозапись упражнения сохранена для учителя",
                                            color = Color(0xFF2E7D32),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 7. Empty State for Selected Date
            if (dayAssignments.isEmpty() && dayRecords.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.EventRepeat,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "На эту дату занятий пока нет",
                                color = Color(0xFF475569),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Нажмите кнопк «+ Запланировать», чтобы добавить новую тренировку в календарь",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add Planned Workout
        FloatingActionButton(
            onClick = { showAddPlanDialog = true },
            containerColor = Color(0xFF6750A4),
            contentColor = Color.White,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Запланировать")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Запланировать",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Add Planned Workout Dialog
        if (showAddPlanDialog) {
            AddPlannedWorkoutDialog(
                selectedDateString = selectedDateString,
                onDismiss = { showAddPlanDialog = false },
                onSave = { exerciseType, target, title ->
                    onAddPlannedWorkout?.invoke(exerciseType, target, title, selectedDateString)
                    showAddPlanDialog = false
                }
            )
        }
    }
}

@Composable
fun AddPlannedWorkoutDialog(
    selectedDateString: String,
    onDismiss: () -> Unit,
    onSave: (ExerciseType, Int, String) -> Unit
) {
    var selectedType by remember { mutableStateOf(ExerciseType.PUSH_UP) }
    var targetValueText by remember { mutableStateOf("20") }
    var titleText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Запланировать тренировку",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Дата: $selectedDateString",
                    color = Color(0xFF6750A4),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Text(
                    text = "Выберите упражнение:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ExerciseType.entries) { ex ->
                        FilterChip(
                            selected = (selectedType == ex),
                            onClick = {
                                selectedType = ex
                                if (titleText.isEmpty()) {
                                    titleText = "Тренировка: ${ex.titleRu}"
                                }
                            },
                            label = { Text(ex.titleRu) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFEADDFF),
                                selectedLabelColor = Color(0xFF21005D)
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Название (например: Вечерний норматив)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetValueText,
                    onValueChange = { targetValueText = it },
                    label = { Text("Цель (${selectedType.unitRu})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetValueText.toIntOrNull() ?: 20
                    val title = if (titleText.isNotBlank()) titleText else "Занятие: ${selectedType.titleRu}"
                    onSave(selectedType, target, title)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Сохранить в календарь")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = Color(0xFF64748B))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    MaterialTheme {
        CalendarScreen(
            selectedDateString = "2026-08-01",
            dayRecords = emptyList(),
            allStudentRecords = emptyList(),
            allAssignments = emptyList(),
            onDateSelect = {}
        )
    }
}
