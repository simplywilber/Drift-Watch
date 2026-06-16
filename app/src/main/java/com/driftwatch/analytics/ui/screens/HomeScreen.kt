package com.driftwatch.analytics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.driftwatch.analytics.data.local.EnvironmentalReadingEntity
import com.driftwatch.analytics.data.local.SymptomLogEntity
import com.driftwatch.analytics.ui.DriftWatchViewModel

// Created by Yevgeniy Mazur & Edited by Wilber Amaya-Maurisio
// Purpose: Main dashboard screen for DriftWatch. Displays atmospheric metrics,
// recent symptom logs, and syncing controls.
// UI Enhancements (Wilber): Upgraded to Material 3 layout composition,
// color coded dynamic drift event warning cards, and optimized layout spacing.

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
        // Styling added by Wilber Amaya-MAurisio
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

        // Current Conditions Metric Card
        // Styling added by Wilber Amaya-MAurisio
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Current Conditions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (readings.isEmpty()) {
                    Text(
                        text = "No environmental readings cached locally.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val latest = readings.first()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Barometric Pressure:", fontWeight = FontWeight.Medium)
                        Text("${latest.barometricPressure} hPa", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Ambient Temperature:", fontWeight = FontWeight.Medium)
                        Text("${latest.ambientTemperature}°F", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic Warning Event Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (latest.isDriftEvent)
                                MaterialTheme.colorScheme.errorContainer
                            else
                                MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (latest.isDriftEvent) "⚠️ DRIFT EVENT DETECTED" else "✓ SYSTEM SAFE: NO DRIFT EVENT",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = if (latest.isDriftEvent)
                                MaterialTheme.colorScheme.onErrorContainer
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        )
                    }
                }
            }
        }

        // Symptom Track Card
        // Styling added by Wilber Amaya-Maurisio
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Latest Symptom Log",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (symptoms.isEmpty()) {
                    Text(
                        text = "No recent symptom logs found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val latest = symptoms.first()

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Symptom Classification:", fontWeight = FontWeight.Medium)
                            Text(latest.symptomType, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Severity Level Threshold:", fontWeight = FontWeight.Medium)
                            Text("Level ${latest.severityLevel}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Notes:", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = latest.notes.ifBlank { "No additional logs noted." },
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
            }
        }

        // Synchronization Button Control
        // Styling added by Wilber Amaya-Maurisio
        Button(
            onClick = {
                viewModel.triggerApiSyncFallback(
                    latitude = 47.6062,
                    longitude = -122.3321,
                    apiKey = "5aef96ff3906cb812803c51549707542"
                )
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sync Live Weather Telemetry", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation Action Bar Group
        // Styling added by Wilber Amaya-Maurisio
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { navController.navigate("symptom") },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Log Symptom", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { navController.navigate("history") },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("View History", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}