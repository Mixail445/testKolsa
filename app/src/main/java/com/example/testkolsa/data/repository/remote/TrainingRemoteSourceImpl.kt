package com.example.testkolsa.data.repository.remote

import android.util.Log
import com.example.testkolsa.data.repository.model.TrainingDto
import com.example.testkolsa.data.repository.model.TrainingVideoDto
import com.example.testkolsa.domain.TrainingRemoteSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

class TrainingRemoteSourceImpl(
    private val api: TrainingApi,
) : TrainingRemoteSource {
    override suspend fun getTrainingList(): List<TrainingDto> = withContext(Dispatchers.IO) {
        return@withContext api.getTrainingList()
    }

    override suspend fun getTrainingVideo(id: Int): TrainingVideoDto= withContext(Dispatchers.IO) {
        return@withContext api.getTrainingVideo(id = id)
    }

}
