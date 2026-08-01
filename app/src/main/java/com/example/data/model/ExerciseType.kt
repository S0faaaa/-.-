package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

enum class ExerciseType(
    val id: String,
    val titleRu: String,
    val descriptionRu: String,
    val unitRu: String,
    val targetDefault: Int,
    val caloriesPerRepOrSec: Float,
    val iconName: String,
    val primaryColorHex: Long
) {
    PUSH_UP(
        id = "PUSH_UP",
        titleRu = "Отжимания",
        descriptionRu = "Разгибание рук в упоре лёжа на ровную поверхность",
        unitRu = "повторов",
        targetDefault = 15,
        caloriesPerRepOrSec = 0.5f,
        iconName = "fitness_center",
        primaryColorHex = 0xFF3B82F6
    ),
    SQUAT(
        id = "SQUAT",
        titleRu = "Приседания",
        descriptionRu = "Приседания с прямой спиной и глубоким сгибанием коленей",
        unitRu = "повторов",
        targetDefault = 20,
        caloriesPerRepOrSec = 0.4f,
        iconName = "accessibility_new",
        primaryColorHex = 0xFF10B981
    ),
    CRUNCH(
        id = "CRUNCH",
        titleRu = "Пресс",
        descriptionRu = "Подъем туловища в сед из положения лежа на спине",
        unitRu = "повторов",
        targetDefault = 25,
        caloriesPerRepOrSec = 0.35f,
        iconName = "directions_run",
        primaryColorHex = 0xFFF59E0B
    ),
    LUNGE(
        id = "LUNGE",
        titleRu = "Выпады",
        descriptionRu = "Выпады ногами вперед под углом 90 градусов",
        unitRu = "повторов",
        targetDefault = 16,
        caloriesPerRepOrSec = 0.45f,
        iconName = "hiking",
        primaryColorHex = 0xFF8B5CF6
    ),
    PLANK(
        id = "PLANK",
        titleRu = "Планка",
        descriptionRu = "Удержание прямого корпуса в упоре на предплечьях",
        unitRu = "секунд",
        targetDefault = 45,
        caloriesPerRepOrSec = 0.2f,
        iconName = "timer",
        primaryColorHex = 0xFFEC4899
    );

    companion object {
        fun fromId(id: String): ExerciseType = entries.find { it.id == id } ?: PUSH_UP
    }
}
