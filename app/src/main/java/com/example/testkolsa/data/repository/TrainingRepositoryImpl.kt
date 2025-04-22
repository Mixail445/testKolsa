package com.example.testkolsa.data.repository

import android.util.Log
import com.example.testkolsa.domain.TrainingRemoteSource
import com.example.testkolsa.domain.TrainingRepository
import com.example.testkolsa.domain.model.Training
import com.example.testkolsa.domain.model.TrainingVideo
import com.example.testkolsa.utils.AppResult
import com.example.testkolsa.utils.ResultWrapper
import jakarta.inject.Inject
import jakarta.inject.Singleton

class TrainingRepositoryImpl @Inject constructor(
    private val remoteSource: TrainingRemoteSource,
    private val wrapper: ResultWrapper,
) : TrainingRepository {

    override suspend fun getTrainingRemote(): AppResult<List<Training>, Throwable> = wrapper.wrap {
        remoteSource.getTrainingList().map { it.mapToDomain() }
    }

    override suspend fun getTrainingVideoRemote(id: Int): AppResult<TrainingVideo, Throwable> =
        wrapper.wrap {
            remoteSource.getTrainingVideo(id).mapToDomain()
        }
}