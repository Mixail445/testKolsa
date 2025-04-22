package com.example.testkolsa.domain

import com.example.testkolsa.domain.model.Training
import com.example.testkolsa.domain.model.TrainingVideo
import com.example.testkolsa.utils.AppResult

interface TrainingRepository {
    suspend fun getTrainingRemote(): AppResult<List<Training>, Throwable>

    suspend fun getTrainingVideoRemote(id: Int): AppResult<TrainingVideo, Throwable>
}