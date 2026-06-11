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

// Added by Yevgeniy Mazur
// Purpose:
// Connects the Compose UI layer to the existing repository architecture.
// Collects StateFlow data from the ViewModel and passes it into
// the Navigation system while preserving the repository pattern
// created by Wilber Amaya-Maurisio.

class MainActivity : ComponentActivity() {

    private val viewModel: DriftWatchViewModel by viewModels {
        DriftWatchViewModel.Factory(
            (application as DriftWatchApplication).repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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