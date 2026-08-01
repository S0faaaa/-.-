package com.example.ui.camera

import androidx.compose.ui.geometry.Offset
import com.example.data.model.ExerciseType
import kotlin.math.atan2

data class JointPoint(
    val x: Float, // Normalized 0.0 .. 1.0
    val y: Float, // Normalized 0.0 .. 1.0
    val name: String,
    val confidence: Float = 0.95f
)

data class PoseSkeleton(
    val head: JointPoint,
    val leftShoulder: JointPoint,
    val rightShoulder: JointPoint,
    val leftElbow: JointPoint,
    val rightElbow: JointPoint,
    val leftWrist: JointPoint,
    val rightWrist: JointPoint,
    val leftHip: JointPoint,
    val rightHip: JointPoint,
    val leftKnee: JointPoint,
    val rightKnee: JointPoint,
    val leftAnkle: JointPoint,
    val rightAnkle: JointPoint
)

enum class ExercisePhase {
    IDLE,
    PREPARING,
    PHASE_START, // e.g., Top position for Push-up / Standing for Squat
    PHASE_DOWN,  // e.g., Bottom flex position
    PHASE_HOLD,  // for Plank
    REP_COMPLETED
}

data class MotionAnalysisResult(
    val currentReps: Int,
    val currentHoldSeconds: Int,
    val exercisePhase: ExercisePhase,
    val formAccuracyPercentage: Int, // e.g. 96%
    val feedbackMessageRu: String,
    val isFormCorrect: Boolean,
    val primaryAngleDegrees: Float,
    val targetAngleDegrees: Float,
    val skeleton: PoseSkeleton
)

object PoseUtils {

    /** Calculates angle in degrees at vertex joint B formed by vectors BA and BC */
    fun calculateAngle(a: JointPoint, b: JointPoint, c: JointPoint): Float {
        val radians = atan2(c.y - b.y, c.x - b.x) - atan2(a.y - b.y, a.x - b.x)
        var angle = Math.toDegrees(radians.toDouble()).toFloat()
        angle = Math.abs(angle)
        if (angle > 180f) {
            angle = 360f - angle
        }
        return angle
    }

    /** Returns simulated reference posture skeleton for animation/demo or frame synthesis */
    fun getSyntheticSkeleton(
        progressPhase: Float, // 0.0 (top) to 1.0 (bottom flex)
        exerciseType: ExerciseType
    ): PoseSkeleton {
        return when (exerciseType) {
            ExerciseType.PUSH_UP -> {
                val elbowY = 0.45f + (progressPhase * 0.12f)
                val shoulderY = 0.40f + (progressPhase * 0.12f)
                val headY = 0.35f + (progressPhase * 0.12f)
                PoseSkeleton(
                    head = JointPoint(0.20f, headY, "Head"),
                    leftShoulder = JointPoint(0.32f, shoulderY, "L_Shoulder"),
                    rightShoulder = JointPoint(0.32f, shoulderY, "R_Shoulder"),
                    leftElbow = JointPoint(0.32f, elbowY, "L_Elbow"),
                    rightElbow = JointPoint(0.32f, elbowY, "R_Elbow"),
                    leftWrist = JointPoint(0.32f, 0.58f, "L_Wrist"),
                    rightWrist = JointPoint(0.32f, 0.58f, "R_Wrist"),
                    leftHip = JointPoint(0.55f, shoulderY + 0.05f, "L_Hip"),
                    rightHip = JointPoint(0.55f, shoulderY + 0.05f, "R_Hip"),
                    leftKnee = JointPoint(0.72f, shoulderY + 0.08f, "L_Knee"),
                    rightKnee = JointPoint(0.72f, shoulderY + 0.08f, "R_Knee"),
                    leftAnkle = JointPoint(0.88f, 0.60f, "L_Ankle"),
                    rightAnkle = JointPoint(0.88f, 0.60f, "R_Ankle")
                )
            }
            ExerciseType.SQUAT -> {
                val hipY = 0.48f + (progressPhase * 0.22f)
                val kneeX = 0.48f + (progressPhase * 0.08f)
                PoseSkeleton(
                    head = JointPoint(0.50f, 0.18f + (progressPhase * 0.18f), "Head"),
                    leftShoulder = JointPoint(0.44f, 0.30f + (progressPhase * 0.18f), "L_Shoulder"),
                    rightShoulder = JointPoint(0.56f, 0.30f + (progressPhase * 0.18f), "R_Shoulder"),
                    leftElbow = JointPoint(0.38f, 0.38f + (progressPhase * 0.18f), "L_Elbow"),
                    rightElbow = JointPoint(0.62f, 0.38f + (progressPhase * 0.18f), "R_Elbow"),
                    leftWrist = JointPoint(0.40f, 0.48f + (progressPhase * 0.18f), "L_Wrist"),
                    rightWrist = JointPoint(0.60f, 0.48f + (progressPhase * 0.18f), "R_Wrist"),
                    leftHip = JointPoint(0.45f, hipY, "L_Hip"),
                    rightHip = JointPoint(0.55f, hipY, "R_Hip"),
                    leftKnee = JointPoint(0.42f, 0.70f, "L_Knee"),
                    rightKnee = JointPoint(0.58f, 0.70f, "R_Knee"),
                    leftAnkle = JointPoint(0.42f, 0.88f, "L_Ankle"),
                    rightAnkle = JointPoint(0.58f, 0.88f, "R_Ankle")
                )
            }
            ExerciseType.CRUNCH -> {
                val headX = 0.25f + (progressPhase * 0.25f)
                val headY = 0.75f - (progressPhase * 0.30f)
                val shoulderX = 0.32f + (progressPhase * 0.20f)
                val shoulderY = 0.75f - (progressPhase * 0.25f)
                PoseSkeleton(
                    head = JointPoint(headX, headY, "Head"),
                    leftShoulder = JointPoint(shoulderX, shoulderY, "L_Shoulder"),
                    rightShoulder = JointPoint(shoulderX, shoulderY, "R_Shoulder"),
                    leftElbow = JointPoint(headX - 0.05f, headY + 0.05f, "L_Elbow"),
                    rightElbow = JointPoint(headX - 0.05f, headY + 0.05f, "R_Elbow"),
                    leftWrist = JointPoint(headX, headY, "L_Wrist"),
                    rightWrist = JointPoint(headX, headY, "R_Wrist"),
                    leftHip = JointPoint(0.55f, 0.78f, "L_Hip"),
                    rightHip = JointPoint(0.55f, 0.78f, "R_Hip"),
                    leftKnee = JointPoint(0.70f, 0.62f, "L_Knee"),
                    rightKnee = JointPoint(0.70f, 0.62f, "R_Knee"),
                    leftAnkle = JointPoint(0.85f, 0.78f, "L_Ankle"),
                    rightAnkle = JointPoint(0.85f, 0.78f, "R_Ankle")
                )
            }
            ExerciseType.LUNGE -> {
                val frontKneeY = 0.65f + (progressPhase * 0.12f)
                val backKneeY = 0.65f + (progressPhase * 0.18f)
                PoseSkeleton(
                    head = JointPoint(0.50f, 0.20f + (progressPhase * 0.10f), "Head"),
                    leftShoulder = JointPoint(0.46f, 0.32f + (progressPhase * 0.10f), "L_Shoulder"),
                    rightShoulder = JointPoint(0.54f, 0.32f + (progressPhase * 0.10f), "R_Shoulder"),
                    leftElbow = JointPoint(0.42f, 0.40f + (progressPhase * 0.10f), "L_Elbow"),
                    rightElbow = JointPoint(0.58f, 0.40f + (progressPhase * 0.10f), "R_Elbow"),
                    leftWrist = JointPoint(0.44f, 0.48f + (progressPhase * 0.10f), "L_Wrist"),
                    rightWrist = JointPoint(0.56f, 0.48f + (progressPhase * 0.10f), "R_Wrist"),
                    leftHip = JointPoint(0.48f, 0.50f + (progressPhase * 0.10f), "L_Hip"),
                    rightHip = JointPoint(0.52f, 0.50f + (progressPhase * 0.10f), "R_Hip"),
                    leftKnee = JointPoint(0.38f, frontKneeY, "L_Knee"),
                    rightKnee = JointPoint(0.68f, backKneeY, "R_Knee"),
                    leftAnkle = JointPoint(0.38f, 0.88f, "L_Ankle"),
                    rightAnkle = JointPoint(0.78f, 0.88f, "R_Ankle")
                )
            }
            ExerciseType.PLANK -> {
                val spineDev = (progressPhase - 0.5f) * 0.08f
                PoseSkeleton(
                    head = JointPoint(0.20f, 0.48f + spineDev, "Head"),
                    leftShoulder = JointPoint(0.32f, 0.50f + spineDev, "L_Shoulder"),
                    rightShoulder = JointPoint(0.32f, 0.50f + spineDev, "R_Shoulder"),
                    leftElbow = JointPoint(0.32f, 0.62f, "L_Elbow"),
                    rightElbow = JointPoint(0.32f, 0.62f, "R_Elbow"),
                    leftWrist = JointPoint(0.38f, 0.62f, "L_Wrist"),
                    rightWrist = JointPoint(0.38f, 0.62f, "R_Wrist"),
                    leftHip = JointPoint(0.55f, 0.50f + spineDev, "L_Hip"),
                    rightHip = JointPoint(0.55f, 0.50f + spineDev, "R_Hip"),
                    leftKnee = JointPoint(0.72f, 0.51f + spineDev, "L_Knee"),
                    rightKnee = JointPoint(0.72f, 0.51f + spineDev, "R_Knee"),
                    leftAnkle = JointPoint(0.88f, 0.52f, "L_Ankle"),
                    rightAnkle = JointPoint(0.88f, 0.52f, "R_Ankle")
                )
            }
        }
    }
}
