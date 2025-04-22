package com.example.testkolsa.data.repository.remote

import com.example.testkolsa.data.repository.model.TrainingDto
import com.example.testkolsa.data.repository.model.TrainingVideoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TrainingApi {

    @GET("get_workouts")
    suspend fun getTrainingList(): List<TrainingDto>

    @GET("get_video")
    suspend fun getTrainingVideo(
        @Query("id") id: Int
    ): TrainingVideoDto

}