package com.driftwatch.analytics.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.driftwatch.analytics.data.local.EnvironmentalReadingEntity
import com.driftwatch.analytics.data.local.SymptomLogEntity
import com.driftwatch.analytics.ui.DriftWatchViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Added by Yevgeniy Mazur and Edited by Wilber Amaya-Maurisio
// Purpose:
// Main dashboard screen for DriftWatch.
// Displays current atmospheric conditions,
// latest symptom entry, weather sync controls,
// and navigation controls.

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: DriftWatchViewModel,
    readings: List<EnvironmentalReadingEntity>,
    symptoms: List<SymptomLogEntity>
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .statusBarsPadding()
    ) {
        // App Header Section Area
        Text(
            text = "DRIFTWATCH",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "Environmental Analytics Dashboard",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Current Conditions",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (readings.isEmpty()) {

                    Text("No environmental readings found.")

                } else {

                    val latest = readings.first()

                    Text("Pressure: ${latest.barometricPressure} hPa")
                    Text("Temperature: ${latest.ambientTemperature}°F")

                    Text(
                        if (latest.isDriftEvent)
                            "Drift Event Detected"
                        else
                            "No Drift Event"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Latest Symptom",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (symptoms.isEmpty()) {

                    Text("No symptom logs found.")

                } else {

                    val latest = symptoms.first()

                    Text("Type: ${latest.symptomType}")
                    Text("Severity: ${latest.severityLevel}")
                    Text("Notes: ${latest.notes}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.triggerApiSyncFallback(
                    latitude = 47.6062,
                    longitude = -122.3321,
                    apiKey = "5aef96ff3906cb812803c51549707542"
                )
            }
        ) {
            Text("Fetch Weather")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate("symptom")
            }
        ) {
            Text("Add Symptom")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate("history")
            }
        ) {
            Text("View History")
        }
    }
}