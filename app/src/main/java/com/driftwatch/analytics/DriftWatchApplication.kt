package com.driftwatch.analytics

import android.app.Application
import androidx.room.Room
import com.driftwatch.analytics.data.local.AppDatabase
import com.driftwatch.analytics.data.remote.RetrofitProvider
import com.driftwatch.analytics.repository.DriftWatchRepository

// Created by Wilber Amaya-Maurisio
class DriftWatchApplication : Application() {

    // Instantiates the Room database on app startup
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "driftwatch_database"
        ).build()
    }

    // Instantiates the central repository, injecting the database DAO and Retrofit engine
    val repository: DriftWatchRepository by lazy {
        DriftWatchRepository(
            dao = database.driftWatchDao(),
            apiService = RetrofitProvider.apiService
        )
    }
}