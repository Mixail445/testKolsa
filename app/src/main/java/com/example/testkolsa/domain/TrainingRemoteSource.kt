package com.example.testkolsa.domain

import com.example.testkolsa.data.repository.model.TrainingDto
import com.example.testkolsa.data.repository.model.TrainingVideoDto

interface TrainingRemoteSource {

    suspend fun getTrainingList(): List<TrainingDto>

    suspend fun getTrainingVideo(id: Int): TrainingVideoDto

}