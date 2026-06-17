package com.driftwatch.analytics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.driftwatch.analytics.data.local.EnvironmentalReadingEntity
import com.driftwatch.analytics.data.local.SymptomLogEntity
import com.driftwatch.analytics.repository.DriftWatchRepository
import com.driftwatch.analytics.utils.LocationTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Created by Wilber Amaya-Maurisio
class DriftWatchViewModel(
    private val repository: DriftWatchRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    // Converts cold database streams into hot, lifecycle aware UI state arrays
    val environmentalReadings: StateFlow<List<EnvironmentalReadingEntity>> =
        repository.allReadings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val symptomLogs: StateFlow<List<SymptomLogEntity>> =
        repository.allSymptoms.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Triggers safe background insertion or update for a user symptom card
    fun logUserSymptom(
        id: Long = 0,
        type: String,
        severity: Int,
        notes: String,
        rawClassifications: String = "",
        rawSubOptions: String = "",
        rawBodyParts: String = "",
        rawBowelTypes: String = "",
        customSymptomText: String = "",
        userNotes: String = ""
    ) {
        viewModelScope.launch {

            val latestReading = environmentalReadings.value.firstOrNull()
            
            // If editing, preserve the original timestamp and environmental data if possible
            val existing = if (id != 0L) repository.getSymptomById(id) else null

            val newSymptom = SymptomLogEntity(
                id = id,
                timestamp = existing?.timestamp ?: System.currentTimeMillis(),
                symptomType = type,
                severityLevel = severity,
                notes = notes,
                associatedPressure = existing?.associatedPressure ?: latestReading?.barometricPressure,
                associatedTemperature = existing?.associatedTemperature ?: latestReading?.ambientTemperature,
                associatedDriftEvent = existing?.associatedDriftEvent ?: latestReading?.isDriftEvent,
                rawClassifications = rawClassifications,
                rawSubOptions = rawSubOptions,
                rawBodyParts = rawBodyParts,
                rawBowelTypes = rawBowelTypes,
                customSymptomText = customSymptomText,
                userNotes = userNotes
            )

            repository.saveSymptomLog(newSymptom)
        }
    }

    suspend fun getSymptomById(id: Long): SymptomLogEntity? {
        return repository.getSymptomById(id)
    }

    // Added by Yevgeniy Mazur: Triggers weather sync with loading state
    fun triggerApiSyncFallback(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ) {
        viewModelScope.launch {
            _isSyncing.value = true
            _errorMessage.value = null
            try {
                repository.syncAtmosphericData(
                    latitude = latitude,
                    longitude = longitude,
                    apiKey = apiKey
                )
            } catch (e: Exception) {
                _errorMessage.value = "Sync Failed: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    // Added by Yevgeniy Mazur: GPS-based sync
    fun triggerApiSyncWithGps(apiKey: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            _errorMessage.value = null
            try {
                val location = locationTracker.getCurrentLocation()
                if (location != null) {
                    repository.syncAtmosphericData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        apiKey = apiKey
                    )
                } else {
                    _errorMessage.value = "GPS Location Unavailable"
                }
            } catch (e: Exception) {
                _errorMessage.value = "GPS Sync Failed: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    // Factory pattern to securely instantiate our ViewModel
    // with repository requirements.
    class Factory(
        private val repository: DriftWatchRepository,
        private val locationTracker: LocationTracker
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {

            if (modelClass.isAssignableFrom(DriftWatchViewModel::class.java)) {
                return DriftWatchViewModel(repository, locationTracker) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
