package com.driftwatch.analytics.repository

import com.driftwatch.analytics.data.local.DriftWatchDao
import com.driftwatch.analytics.data.local.EnvironmentalReadingEntity
import com.driftwatch.analytics.data.local.SymptomLogEntity
import com.driftwatch.analytics.data.remote.WeatherApiService
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

    suspend fun syncAtmosphericData(latitude: Double, longitude: Double, apiKey: String): Result<Unit> {
        return try {
            val response = apiService.fetchLocalConditions(latitude, longitude, apiKey)

            val isDriftDetected = response.main.pressure < 1013.25 && response.main.temp > 25.0

            val reading = EnvironmentalReadingEntity(
                timestamp = response.dt * 1000,
                barometricPressure = response.main.pressure,
                ambientTemperature = response.main.temp,
                isDriftEvent = isDriftDetected
            )

            dao.insertReading(reading)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}