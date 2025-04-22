package com.example.testkolsa.data.network

import com.example.testkolsa.Constants.BASE_URL
import com.example.testkolsa.R
import com.example.testkolsa.data.repository.TrainingRepositoryImpl
import com.example.testkolsa.data.repository.remote.TrainingApi
import com.example.testkolsa.data.repository.remote.TrainingRemoteSourceImpl
import com.example.testkolsa.domain.TrainingRemoteSource
import com.example.testkolsa.domain.TrainingRepository
import com.example.testkolsa.presentation.common.Router
import com.example.testkolsa.presentation.common.RouterImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideTrainingApi(retrofit: Retrofit): TrainingApi =
        retrofit.create(TrainingApi::class.java)

    @Provides
    fun provideReviewsRemoteSource(
        api: TrainingApi,
    ): TrainingRemoteSource = TrainingRemoteSourceImpl(api)


    @Provides
    @Named("Host")
    fun provideRouter(): Router = RouterImpl(R.id.NavHostFragment)

    @Provides
    fun provideTrainingRepository(trainingRepositoryImpl: TrainingRepositoryImpl): TrainingRepository =
        trainingRepositoryImpl

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()


    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    private val moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

    @Provides
    fun provideOkHttpClient() =
        OkHttpClient.Builder()
            .addInterceptor(RequestInterceptor())
            .addInterceptor(interceptor)
            .build()

    private val interceptor =
        run {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
        }
}