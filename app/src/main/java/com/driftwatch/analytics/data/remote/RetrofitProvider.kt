package com.driftwatch.analytics.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Created by Wilber Amaya-Maurisio
object RetrofitProvider {
    private const val BASE_URL = "https://api.openweathermap.org/"

    val apiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}