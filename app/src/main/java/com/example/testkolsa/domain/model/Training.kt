package com.example.testkolsa.domain.model

import com.example.testkolsa.presentation.traininglist.TrainingUi

data class Training(
    val id: Long,
    val title: String,
    val description: String?,
    val type: Long,
    val duration: String,
) {
    fun mapToUi() = TrainingUi(
        id = id,
        title = title,
        description = description,
        type = type,
        duration = duration,
        itemId = id.toString()
    )
}
