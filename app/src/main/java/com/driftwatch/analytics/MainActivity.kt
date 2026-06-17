package com.driftwatch.analytics

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driftwatch.analytics.ui.DriftWatchViewModel
import com.driftwatch.analytics.ui.navigation.AppNavigation
import com.driftwatch.analytics.ui.theme.DriftWatchAnalyticsTheme
import com.driftwatch.analytics.utils.LocationTracker
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.delay

// Added by Yevgeniy Mazur & Edited by Wilber Amaya-Maurisio
// Purpose: Architecture bridge connecting the Compose UI layer to the local repository.
class MainActivity : ComponentActivity() {

    private val viewModel: DriftWatchViewModel by viewModels {
        val app = application as DriftWatchApplication
        DriftWatchViewModel.Factory(
            app.repository,
            LocationTracker(app)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
                val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
                if (fineGranted || coarseGranted) {
                    viewModel.triggerApiSyncWithGps("5aef96ff3906cb812803c51549707542")
                }
            }

            LaunchedEffect(Unit) {
                // Request permissions immediately
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )

                // Periodic sync loop
                while (true) {
                    // Only attempt sync if we have permission
                    val tracker = LocationTracker(this@MainActivity)
                    if (tracker.hasLocationPermission()) {
                        Log.d("DriftWatch", "Periodic Auto-Sync triggered")
                        viewModel.triggerApiSyncWithGps("5aef96ff3906cb812803c51549707542")
                    }
                    delay(60000) // 1 minute
                }
            }

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
