package com.driftwatch.analytics.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.driftwatch.analytics.ui.DriftWatchViewModel

// Added by Yevgeniy Mazur
// Purpose:
// Allows users to enter symptoms and save them
// through the existing ViewModel and Room architecture.

@Composable
fun SymptomScreen(
    navController: NavController,
    viewModel: DriftWatchViewModel
) {

    var symptomType by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
    ) {

        Text("Log Symptom")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = symptomType,
            onValueChange = { symptomType = it },
            label = { Text("Symptom Type") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = severity,
            onValueChange = { severity = it },
            label = { Text("Severity (1-10)") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                val severityValue = severity.toIntOrNull() ?: 1

                viewModel.logUserSymptom(
                    type = symptomType,
                    severity = severityValue,
                    notes = notes
                )

                navController.navigate("home")
            }
        ) {
            Text("Save Symptom")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate("home")
            }
        ) {
            Text("Cancel")
        }
    }
}