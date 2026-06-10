package com.driftwatch.analytics.data.remote

// Created by Wilber Amaya-Maurisio
data class WeatherDto(
    val main: MainMetrics,
    val dt: Long
)
data class MainMetrics(
    val pressure: Double,
    val temp: Double
)