package com.example.testkolsa.data.repository.model

import com.example.testkolsa.domain.model.Training
import com.example.testkolsa.domain.model.TrainingVideo

data class TrainingVideoDto(
    val id: Long,
    val duration: String,
    val link: String,
) {
    fun mapToDomain() = TrainingVideo(
        id = id,
        duration = duration,
        link = link
    )
}
