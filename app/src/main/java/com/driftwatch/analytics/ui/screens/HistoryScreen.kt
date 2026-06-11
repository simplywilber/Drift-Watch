package com.driftwatch.analytics.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.driftwatch.analytics.data.local.EnvironmentalReadingEntity
import com.driftwatch.analytics.data.local.SymptomLogEntity

@Composable
fun HistoryScreen(
    navController: NavController,
    readings: List<EnvironmentalReadingEntity>,
    symptoms: List<SymptomLogEntity>
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
    ) {

        Text("History")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("home")
            }
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {

            items(symptoms) { symptom ->

                Card(
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text("Type: ${symptom.symptomType}")
                        Text("Severity: ${symptom.severityLevel}")
                        Text("Notes: ${symptom.notes}")
                    }
                }
            }
        }
    }
}