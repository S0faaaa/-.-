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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import com.example.data.model.StudentProfile
import com.example.ui.viewmodel.UserRole

data class BadgeAchievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean
)

@Composable
fun ProfileScreen(
    activeStudent: StudentProfile?,
    allStudents: List<StudentProfile>,
    userRole: UserRole,
    onRoleChange: (UserRole) -> Unit,
    onSwitchStudent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showStudentDropdown by remember { mutableStateOf(false) }

    val badges = listOf(
        BadgeAchievement("b1", "Мастер отжиманий", "Выполнить отжимания на 95%+", "💪", true),
        BadgeAchievement("b2", "Железная планка", "Удержать планку более 60 сек", "⏳", true),
        BadgeAchievement("b3", "7 Дней в спорте", "Заниматься 7 дней подряд", "🔥", (activeStudent?.streakDays ?: 0) >= 7),
        BadgeAchievement("b4", "Чемпион класса", "Занять 1 место в рейтинге ГТО", "🏆", true),
        BadgeAchievement("b5", "500 Баллов ГТО", "Набрать более 500 баллов", "⭐", (activeStudent?.totalPoints ?: 0) >= 500),
        BadgeAchievement("b6", "Аналитик техники", "Записать 10 видеоотчетов", "📹", true)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Role Selector Bar
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = userRole == UserRole.STUDENT,
                            onClick = { onRoleChange(UserRole.STUDENT) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Режим Ученика", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        SegmentedButton(
                            selected = userRole == UserRole.TEACHER,
                            onClick = { onRoleChange(UserRole.TEACHER) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Режим Учителя", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. Profile Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEADDFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activeStudent?.name?.take(1) ?: "У",
                            color = Color(0xFF21005D),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = activeStudent?.name ?: "Иван Иванов",
                        color = Color(0xFF1C1B1F),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Ученик ${activeStudent?.gradeClass ?: "7-А"} класса",
                        color = Color(0xFF49454F),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF3EDF7),
                            border = BorderStroke(1.dp, Color(0xFFCAC4D0))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🔥 ${activeStudent?.streakDays ?: 1} дн",
                                    color = Color(0xFF6750A4),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Серия тренировок",
                                    color = Color(0xFF49454F),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF3EDF7),
                            border = BorderStroke(1.dp, Color(0xFFCAC4D0))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⭐ ${activeStudent?.totalPoints ?: 320}",
                                    color = Color(0xFF6750A4),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Баллы ГТО",
                                    color = Color(0xFF49454F),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Account Switcher Button
                    Box {
                        Button(
                            onClick = { showStudentDropdown = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEADDFF)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Сменить профиль ученика",
                                color = Color(0xFF21005D),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showStudentDropdown,
                            onDismissRequest = { showStudentDropdown = false }
                        ) {
                            allStudents.forEach { student ->
                                DropdownMenuItem(
                                    text = { Text("${student.name} (${student.gradeClass})") },
                                    onClick = {
                                        onSwitchStudent(student.id)
                                        showStudentDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Badges Grid Header
        item {
            Text(
                text = "Достижения и значки ГТО",
                color = Color(0xFF1C1B1F),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }

        // Badges Items
        items(badges.chunked(2)) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { badge ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (badge.isUnlocked) Color(0xFFFFFFFF) else Color(0xFFF3EDF7)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = badge.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = badge.title,
                                    color = if (badge.isUnlocked) Color(0xFF1C1B1F) else Color(0xFF49454F),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = badge.description,
                                color = Color(0xFF49454F),
                                fontSize = 11.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

