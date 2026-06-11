package com.driftwatch.analytics.repository

import com.driftwatch.analytics.data.local.DriftWatchDao
import com.driftwatch.analytics.data.local.EnvironmentalReadingEntity
import com.driftwatch.analytics.data.local.SymptomLogEntity
import com.driftwatch.analytics.data.remote.WeatherApiService
import android.util.Log
import kotlinx.coroutines.flow.Flow

// Created by Wilber Amaya-Maurisio
class DriftWatchRepository(
    private val dao: DriftWatchDao,
    private val apiService: WeatherApiService
) {
    val allReadings: Flow<List<EnvironmentalReadingEntity>> = dao.observeReadings()
    val allSymptoms: Flow<List<SymptomLogEntity>> = dao.observeSymptoms()

    suspend fun saveSymptomLog(symptom: SymptomLogEntity) {
        dao.insertSymptom(symptom)
    }

    suspend fun syncAtmosphericData(latitude: Double, longitude: Double, apiKey: String) {
        Log.d("DriftWatchRepo", "Fetching weather for lat=$latitude, lon=$longitude")
        val response = apiService.fetchLocalConditions(latitude, longitude, apiKey)
        Log.d("DriftWatchRepo", "API Response: $response")

        // Using metric units for original logic: pressure < 1013.25 hPa and temp > 25 C
        val isDriftDetected = response.main.pressure < 1013.25 && response.main.temp > 25.0

        val reading = EnvironmentalReadingEntity(
            timestamp = response.dt * 1000,
            barometricPressure = response.main.pressure,
            ambientTemperature = response.main.temp,
            isDriftEvent = isDriftDetected
        )

        dao.insertReading(reading)
        Log.d("DriftWatchRepo", "Inserted reading into database")
    }
}