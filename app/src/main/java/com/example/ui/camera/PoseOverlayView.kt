package com.example.ui.camera

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExerciseType

@Composable
fun PoseOverlayView(
    analysisResult: MotionAnalysisResult,
    exerciseType: ExerciseType,
    isDemoMode: Boolean,
    modifier: Modifier = Modifier
) {
    val skeleton = analysisResult.skeleton
    val isCorrect = analysisResult.isFormCorrect

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Draw Skeleton Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            fun toOffset(joint: JointPoint): Offset {
                return Offset(joint.x * canvasWidth, joint.y * canvasHeight)
            }

            val pHead = toOffset(skeleton.head)
            val pLShoulder = toOffset(skeleton.leftShoulder)
            val pRShoulder = toOffset(skeleton.rightShoulder)
            val pLElbow = toOffset(skeleton.leftElbow)
            val pRElbow = toOffset(skeleton.rightElbow)
            val pLWrist = toOffset(skeleton.leftWrist)
            val pRWrist = toOffset(skeleton.rightWrist)
            val pLHip = toOffset(skeleton.leftHip)
            val pRHip = toOffset(skeleton.rightHip)
            val pLKnee = toOffset(skeleton.leftKnee)
            val pRKnee = toOffset(skeleton.rightKnee)
            val pLAnkle = toOffset(skeleton.leftAnkle)
            val pRAnkle = toOffset(skeleton.rightAnkle)

            // Connection bone color
            val boneColor = if (isCorrect) Color(0xFF10B981) else Color(0xFFF59E0B)
            val glowColor = if (isCorrect) Color(0x6610B981) else Color(0x66F59E0B)
            val strokeWidth = 8.dp.toPx()

            // Draw glowing background lines
            val connections = listOf(
                pHead to pLShoulder, pHead to pRShoulder,
                pLShoulder to pRShoulder,
                pLShoulder to pLElbow, pLElbow to pLWrist,
                pRShoulder to pRElbow, pRElbow to pRWrist,
                pLShoulder to pLHip, pRShoulder to pRHip,
                pLHip to pRHip,
                pLHip to pLKnee, pLKnee to pLAnkle,
                pRHip to pRKnee, pRKnee to pRAnkle
            )

            for ((start, end) in connections) {
                drawLine(
                    color = glowColor,
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth * 1.8f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = boneColor,
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            // Draw joint node circles
            val joints = listOf(
                pHead, pLShoulder, pRShoulder, pLElbow, pRElbow,
                pLWrist, pRWrist, pLHip, pRHip, pLKnee, pRKnee,
                pLAnkle, pRAnkle
            )

            for (jointOffset in joints) {
                drawCircle(
                    color = Color.White,
                    radius = 12.dp.toPx(),
                    center = jointOffset
                )
                drawCircle(
                    color = if (isCorrect) Color(0xFF059669) else Color(0xFFD97706),
                    radius = 8.dp.toPx(),
                    center = jointOffset
                )
            }

            // Target Depth Guidance Line
            if (exerciseType == ExerciseType.PUSH_UP || exerciseType == ExerciseType.SQUAT) {
                val targetY = canvasHeight * 0.65f
                drawLine(
                    color = Color(0xAA38BDF8),
                    start = Offset(0f, targetY),
                    end = Offset(canvasWidth, targetY),
                    strokeWidth = 3.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
                )
            }
        }

        // 2. Top Info Banner - Exercise Name & Live Rep Counter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xDD0F172A),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(exerciseType.primaryColorHex),
                                        Color(0xFF38BDF8)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = exerciseType.titleRu,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (exerciseType == ExerciseType.PLANK) "Удержание" else "Автосчетчик ГТО",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                    }

                    // Large Counter Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (exerciseType == ExerciseType.PLANK)
                                    "${analysisResult.currentHoldSeconds}"
                                else
                                    "${analysisResult.currentReps}",
                                color = Color(0xFF38BDF8),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = exerciseType.unitRu,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // 3. Bottom Form Coaching & Precision Indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Live Speech Coaching Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrect) Color(0xEE065F46) else Color(0xEE92400E)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isCorrect) Color(0xFF34D399) else Color(0xFFFBBF24),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = analysisResult.feedbackMessageRu,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Точность выполнения: ${analysisResult.formAccuracyPercentage}%",
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Accuracy Bar
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xCC0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Анализ угла сгиба: ${analysisResult.primaryAngleDegrees.toInt()}°",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (isDemoMode) {
                                Surface(
                                    color = Color(0xFF3B82F6),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "ДЕМО СИМУЛЯЦИЯ",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { analysisResult.formAccuracyPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = if (isCorrect) Color(0xFF10B981) else Color(0xFFF59E0B),
                            trackColor = Color(0xFF334155)
                        )
                    }
                }
            }
        }
    }
}
