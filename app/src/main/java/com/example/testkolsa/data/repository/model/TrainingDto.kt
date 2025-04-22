package com.example.testkolsa.data.repository.model

import com.example.testkolsa.domain.model.Training

data class TrainingDto(
    val id: Long,
    val title: String,
    val description: String?,
    val type: Long,
    val duration: String,
) {
    fun mapToDomain() = Training(
        id = id,
        title = title,
        description = description,
        type = type,
        duration = duration,
    )
}
