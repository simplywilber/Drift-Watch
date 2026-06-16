package com.driftwatch.analytics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driftwatch.analytics.ui.DriftWatchViewModel
import com.driftwatch.analytics.ui.navigation.AppNavigation
import com.driftwatch.analytics.ui.theme.DriftWatchAnalyticsTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

// Added by Yevgeniy Mazur & Edited by Wilber Amaya-Maurisio
// Purpose: Architecture bridge connecting the Compose UI layer to the local repository.
// Responsibilities: Collects underlying StateFlow metrics, orchestrates screen routing parameters,
// and ensures decoupled data integrity across the Navigation lifecycle.
class MainActivity : ComponentActivity() {

    private val viewModel: DriftWatchViewModel by viewModels {
        DriftWatchViewModel.Factory(
            (application as DriftWatchApplication).repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // Custom splash screen added by Wilber Amaya-MAurisio
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {

            val readings by viewModel.environmentalReadings.collectAsStateWithLifecycle()
            val symptoms by viewModel.symptomLogs.collectAsStateWithLifecycle()

            DriftWatchAnalyticsTheme {
                AppNavigation(
                    viewModel = viewModel,
                    readings = readings,
                    symptoms = symptoms
                )
            }
        }
    }
}