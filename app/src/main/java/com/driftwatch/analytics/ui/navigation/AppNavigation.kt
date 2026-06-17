package com.driftwatch.analytics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.driftwatch.analytics.data.local.EnvironmentalReadingEntity
import com.driftwatch.analytics.data.local.SymptomLogEntity
import com.driftwatch.analytics.ui.DriftWatchViewModel
import com.driftwatch.analytics.ui.screens.HistoryScreen
import com.driftwatch.analytics.ui.screens.HomeScreen
import com.driftwatch.analytics.ui.screens.SymptomScreen

// Added by Yevgeniy Mazur
// Purpose:
// Provides navigation between Home, Symptom Entry,
// and History screens using Navigation Compose.

@Composable
fun AppNavigation(
    viewModel: DriftWatchViewModel,
    readings: List<EnvironmentalReadingEntity>,
    symptoms: List<SymptomLogEntity>
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel,
                readings = readings,
                symptoms = symptoms
            )
        }

        composable("symptom") {
            SymptomScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(
            route = "edit_symptom/{symptomId}",
            arguments = listOf(navArgument("symptomId") { type = NavType.LongType })
        ) { backStackEntry ->
            val symptomId = backStackEntry.arguments?.getLong("symptomId") ?: -1L
            SymptomScreen(
                navController = navController,
                viewModel = viewModel,
                editingSymptomId = symptomId
            )
        }

        composable("history") {
            HistoryScreen(
                navController = navController,
                readings = readings,
                symptoms = symptoms
            )
        }
    }
}
