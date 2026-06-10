package com.driftwatch.analytics.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// Created by Wilber Amaya-Maurisio
interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun fetchLocalConditions(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): WeatherDto
}