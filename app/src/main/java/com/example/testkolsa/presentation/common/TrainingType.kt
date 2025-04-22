package com.example.testkolsa.presentation.common

enum class TrainingType(val id: Long, val title: String) {
    CARDIO(1L, TrainingTypeTitles.CARDIO),
    STRENGTH(2L, TrainingTypeTitles.STRENGTH),
    STRETCHING(3L, TrainingTypeTitles.STRETCHING),
    YOGA(4L, TrainingTypeTitles.YOGA),
    BOX(5L, TrainingTypeTitles.BOX);

    companion object {
        fun fromId(id: Long): TrainingType? {
            return entries.find { it.id == id }
        }
    }
}

object TrainingTypeTitles {
    const val CARDIO = "Кардио"
    const val STRENGTH = "Силовые"
    const val STRETCHING = "Растяжка"
    const val YOGA = "Йога"
    const val BOX = "Бокс"
}