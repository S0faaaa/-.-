package com.example.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.data.model.ExerciseType
import kotlinx.coroutines.delay

@Composable
fun ExerciseTrackerView(
    exerciseType: ExerciseType,
    onFinishWorkout: (repsOrSeconds: Int, accuracyPercentage: Int, durationSeconds: Int, caloriesBurned: Int) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // Tracker Loop State
    var isTrackingActive by remember { mutableStateOf(true) }
    var isDemoMode by remember { mutableStateOf(true) } // Default demo mode ON so user can test without motion
    var repsCount by remember { mutableIntStateOf(0) }
    var holdSeconds by remember { mutableIntStateOf(0) }
    var workoutDurationSeconds by remember { mutableIntStateOf(0) }
    var isRecording by remember { mutableStateOf(true) }
    var showCompletionSummaryDialog by remember { mutableStateOf(false) }

    // Phase Simulation Animatable
    val phaseAnim = remember { Animatable(0f) }

    // Reps & Pose State Generator Loop
    LaunchedEffect(isTrackingActive, isDemoMode) {
        if (!isTrackingActive) return@LaunchedEffect

        var cycleCount = 0
        while (isTrackingActive) {
            delay(1000)
            workoutDurationSeconds++

            if (exerciseType == ExerciseType.PLANK) {
                holdSeconds++
            } else if (isDemoMode) {
                cycleCount++
                // Simulate rep every 3 seconds in demo mode
                if (cycleCount % 3 == 0) {
                    repsCount++
                }
            }
        }
    }

    // Animate synthetic skeleton motion phase
    LaunchedEffect(isTrackingActive) {
        while (isTrackingActive) {
            phaseAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
            )
            phaseAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
            )
        }
    }

    // Build Pose Analysis Object
    val currentPhaseVal = phaseAnim.value
    val syntheticSkeleton = remember(currentPhaseVal, exerciseType) {
        PoseUtils.getSyntheticSkeleton(currentPhaseVal, exerciseType)
    }

    val primaryAngle = remember(currentPhaseVal, exerciseType) {
        when (exerciseType) {
            ExerciseType.PUSH_UP -> 170f - (currentPhaseVal * 85f) // 170° top -> 85° bottom
            ExerciseType.SQUAT -> 175f - (currentPhaseVal * 85f)   // 175° top -> 90° bottom
            ExerciseType.CRUNCH -> 180f - (currentPhaseVal * 95f)  // 180° lying -> 85° sitting
            ExerciseType.LUNGE -> 170f - (currentPhaseVal * 80f)   // 170° top -> 90° lunge
            ExerciseType.PLANK -> 178f - (currentPhaseVal * 6f)   // near flat 180°
        }
    }

    val isCorrectForm = primaryAngle <= 95f || exerciseType == ExerciseType.PLANK
    val formAccuracyPct = if (isCorrectForm) 96 else 91

    val feedbackMsg = when {
        exerciseType == ExerciseType.PLANK -> "Идеальная планка! Корпус зафиксирован 💪"
        currentPhaseVal > 0.7f -> "Отлично! Угол 90° достигнут 🔥"
        currentPhaseVal > 0.3f -> "Опускайтесь ниже, держите ритм..."
        else -> "Спина ровно! Готовьтесь к повтору"
    }

    val analysisResult = MotionAnalysisResult(
        currentReps = repsCount,
        currentHoldSeconds = holdSeconds,
        exercisePhase = if (currentPhaseVal > 0.7f) ExercisePhase.PHASE_DOWN else ExercisePhase.PHASE_START,
        formAccuracyPercentage = formAccuracyPct,
        feedbackMessageRu = feedbackMsg,
        isFormCorrect = true,
        primaryAngleDegrees = primaryAngle,
        targetAngleDegrees = 90f,
        skeleton = syntheticSkeleton
    )

    val finalAmount = if (exerciseType == ExerciseType.PLANK) holdSeconds else repsCount
    val caloriesCalculated = (finalAmount * exerciseType.caloriesPerRepOrSec).toInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Live Camera Preview Feed
        CameraPreviewContainer(
            hasCameraPermission = hasCameraPermission,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Pose & Skeleton Overlay View
        PoseOverlayView(
            analysisResult = analysisResult,
            exerciseType = exerciseType,
            isDemoMode = isDemoMode,
            modifier = Modifier.fillMaxSize()
        )

        // 3. Top Navigation & Control Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xBB0F172A))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.White
                )
            }

            // Recording Status Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xCC0F172A)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val mins = workoutDurationSeconds / 60
                    val secs = workoutDurationSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Manual Rep Increment Button for Testing
            IconButton(
                onClick = {
                    if (exerciseType == ExerciseType.PLANK) {
                        holdSeconds += 10
                    } else {
                        repsCount++
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3B82F6))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "+1 Повтор",
                    tint = Color.White
                )
            }
        }

        // 4. Floating Action Controls (Demo Switch & Complete Button)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 20.dp, end = 20.dp)
                .align(Alignment.BottomCenter)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Simulation toggle
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xDD1E293B),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Демо-модель движений",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = isDemoMode,
                            onCheckedChange = { isDemoMode = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF3B82F6)
                            )
                        )
                    }
                }

                // Stop & Save Workout Button
                Button(
                    onClick = {
                        isTrackingActive = false
                        showCompletionSummaryDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Завершить и сохранить в календарь",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Completion Summary Dialog Modal
    if (showCompletionSummaryDialog) {
        Dialog(onDismissRequest = {}) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F172A)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Упражнение завершено!",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Результат записан в школьный дневник",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBadgeItem(
                            label = if (exerciseType == ExerciseType.PLANK) "Время" else "Повторы",
                            value = "$finalAmount ${exerciseType.unitRu}",
                            color = Color(0xFF38BDF8)
                        )
                        StatBadgeItem(
                            label = "Точность",
                            value = "$formAccuracyPct%",
                            color = Color(0xFF10B981)
                        )
                        StatBadgeItem(
                            label = "Калории",
                            value = "$caloriesCalculated ккал",
                            color = Color(0xFFF59E0B)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            showCompletionSummaryDialog = false
                            onFinishWorkout(
                                finalAmount,
                                formAccuracyPct,
                                workoutDurationSeconds,
                                caloriesCalculated
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Перейти к календарю",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadgeItem(
    label: String,
    value: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1E293B),
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
        }
    }
}
