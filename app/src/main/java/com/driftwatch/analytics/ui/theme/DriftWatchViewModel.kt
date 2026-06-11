package com.driftwatch.analytics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.driftwatch.analytics.data.local.EnvironmentalReadingEntity
import com.driftwatch.analytics.data.local.SymptomLogEntity
import com.driftwatch.analytics.repository.DriftWatchRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Created by Wilber Amaya-Maurisio
class DriftWatchViewModel(
    private val repository: DriftWatchRepository
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

    // Triggers safe background insertion for a user symptom card
    fun logUserSymptom(
        type: String,
        severity: Int,
        notes: String
    ) {
        viewModelScope.launch {

            val currentTimestamp = System.currentTimeMillis()

            val newSymptom = SymptomLogEntity(
                timestamp = currentTimestamp,
                symptomType = type,
                severityLevel = severity,
                notes = notes
            )

            repository.saveSymptomLog(newSymptom)
        }
    }

    // Factory pattern to securely instantiate our ViewModel
    // with repository requirements.
    class Factory(
        private val repository: DriftWatchRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {

            if (modelClass.isAssignableFrom(DriftWatchViewModel::class.java)) {
                return DriftWatchViewModel(repository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}