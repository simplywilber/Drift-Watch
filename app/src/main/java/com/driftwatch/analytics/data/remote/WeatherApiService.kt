package com.driftwatch.analytics.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// Created by Wilber Amaya-Maurisio
// Purpose:
// Defines the Retrofit interface used to communicate
// with the OpenWeatherMap API and retrieve current
// atmospheric conditions for a given location.

interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun fetchLocalConditions(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "imperial"
    ): WeatherDto
}