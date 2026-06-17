package com.driftwatch.analytics.ui.screens

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.driftwatch.analytics.ui.DriftWatchViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SymptomScreen(
    navController: NavController,
    viewModel: DriftWatchViewModel,
    editingSymptomId: Long = 0
) {
    // Added by Yevgeniy Mazur
    // Purpose: Multi-stage diagnostic logging with stacked classifications and interactive validation pop-ups.
    // Now supports editing existing logs.

    // --- Data Models ---
    val classifications = listOf("Migraine", "Pain", "Respiratory", "Skin", "Digestive", "Other")
    val subOptionsMap = mapOf(
        "Migraine" to listOf("Light Sensitivity", "Nausea", "Aura", "Dizziness", "Pain"),
        "Respiratory" to listOf("Cough", "Wheezing", "Shortness of Breath", "Chest Tightness"),
        "Pain" to listOf("Sharp", "Dull", "Throbbing", "Burning", "Stinging"),
        "Skin" to listOf("Rash", "Itching", "Burning", "Redness", "Swelling"),
        "Digestive" to listOf("Nausea", "Stomach Ache", "Bloating", "Heartburn", "Bowel Movement")
    )
    val bodyParts =
        listOf("Head", "Neck", "Chest", "Back", "Abdomen", "Arms", "Legs", "Hands", "Feet")
    val bowelTypes = listOf("Constipation", "Diarrhea", "Loose", "Hard", "Bloody", "Normal")

    // --- State ---
    var selectedClassifications by remember { mutableStateOf(setOf<String>()) }
    var selectedSubOptions by remember { mutableStateOf(setOf<String>()) }
    var selectedBodyParts by remember { mutableStateOf(setOf<String>()) }
    var selectedBowelTypes by remember { mutableStateOf(setOf<String>()) }
    var customSymptom by remember { mutableStateOf("") }
    var userNotes by remember { mutableStateOf("") }
    var showValidationDialog by remember { mutableStateOf(false) }
    // Added by Giecel Tumbaga: Created recommendation pop-up
    var showRecommendationDialog by remember { mutableStateOf(false) }
    var recommendations by remember { mutableStateOf(listOf<String>()) }

    // --- Rotary State for Severity ---
    val severityListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val severitySnapBehavior = rememberSnapFlingBehavior(lazyListState = severityListState)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemSize = 64.dp
    val horizontalPaddingSeverity = (screenWidth / 2) - (itemSize / 2) - 24.dp

    // --- Load existing data for editing ---
    val scope = rememberCoroutineScope()
    LaunchedEffect(editingSymptomId) {
        if (editingSymptomId != 0L) {
            val existing = viewModel.getSymptomById(editingSymptomId)
            if (existing != null) {
                selectedClassifications =
                    existing.rawClassifications.split(",").filter { it.isNotBlank() }.toSet()
                selectedSubOptions =
                    existing.rawSubOptions.split(",").filter { it.isNotBlank() }.toSet()
                selectedBodyParts =
                    existing.rawBodyParts.split(",").filter { it.isNotBlank() }.toSet()
                selectedBowelTypes =
                    existing.rawBowelTypes.split(",").filter { it.isNotBlank() }.toSet()
                customSymptom = existing.customSymptomText
                userNotes = existing.userNotes

                // Scroll severity wheel to correct position
                scope.launch {
                    severityListState.scrollToItem(existing.severityLevel - 1)
                }
            }
        }
    }

    // Logic for dynamic sections
    val showBodyPart = selectedClassifications.contains("Pain") ||
            selectedSubOptions.contains("Pain") ||
            selectedClassifications.contains("Skin")

    val showBowelType = selectedSubOptions.contains("Bowel Movement")

    // --- Validation Logic ---
    val validationMessage by remember {
        derivedStateOf {
            if (selectedClassifications.isEmpty()) return@derivedStateOf "Please select at least one symptom classification."

            for (category in selectedClassifications) {
                val options = subOptionsMap[category]
                if (options != null && !selectedSubOptions.any { it in options }) {
                    return@derivedStateOf "Please pick at least one detail option for $category."
                }
            }

            if (selectedClassifications.contains("Other") && customSymptom.isBlank()) {
                return@derivedStateOf "Please specify the 'Other' symptom text."
            }

            if (showBodyPart && selectedBodyParts.isEmpty()) {
                return@derivedStateOf "Please select at least one body location."
            }

            if (showBowelType && selectedBowelTypes.isEmpty()) {
                return@derivedStateOf "Please select a bowel movement type."
            }

            null
        }
    }

    val isFormValid = validationMessage == null

    // Added by: Giecel Tumbaga
    // Created the recommendation pop up letting the user know ways to relieve
    // their symptoms based on their severity level and what was chosen.
    fun generateRecommendations(classifications: Set<String>, severity: Int): List<String> {
        val tips = mutableListOf<String>()
        
        if (classifications.contains("Migraine")) {
            tips.add("Rest in a quiet, dark room to reduce sensory overload.")
            tips.add("Stay hydrated and avoid known dietary triggers.")
        }
        if (classifications.contains("Respiratory")) {
            tips.add("Avoid outdoor activity if air quality is poor.")
            tips.add("Keep your rescue inhaler nearby if prescribed.")
        }
        if (classifications.contains("Pain")) {
            tips.add("Apply a warm or cold compress to the affected area.")
            tips.add("Try gentle stretching or light movement if tolerable.")
        }
        if (classifications.contains("Skin")) {
            tips.add("Avoid scratching the affected area to prevent infection.")
            tips.add("Use a cool compress or fragrance-free moisturizer.")
        }
        if (classifications.contains("Digestive")) {
            tips.add("Sip clear fluids to maintain hydration.")
            tips.add("Stick to bland foods like bananas, rice, or toast (BRAT diet).")
        }
        
        if (severity >= 7) {
            tips.add("Your severity level is high. Please consult a healthcare professional if symptoms persist or worsen.")
        } else if (tips.isEmpty()) {
            tips.add("Monitor your symptoms and rest as needed.")
        }
        
        return tips
    }

    val severity by remember {
        derivedStateOf {
            val layoutInfo = severityListState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) 1
            else {
                val viewportCenter =
                    (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                visibleItems.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }?.index?.plus(
                    1
                ) ?: 1
            }
        }
    }

    fun getSeverityColor(level: Int): Color {
        val fraction = (level - 1) / 9f
        return if (fraction < 0.5f) {
            lerp(Color(0xFF00C853), Color(0xFFFFD600), fraction * 2)
        } else {
            lerp(Color(0xFFFFD600), Color(0xFFFF0000), (fraction - 0.5f) * 2)
        }
    }

    // Validation Pop-up
    if (showValidationDialog) {
        AlertDialog(
            onDismissRequest = { showValidationDialog = false },
            confirmButton = {
                TextButton(onClick = { showValidationDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Missing Information") },
            text = { Text(validationMessage ?: "") }
        )
    }

    // Added by Giecel Tumbaga
    // Recommendation Pop-up
    if (showRecommendationDialog) {
        AlertDialog(
            onDismissRequest = { 
                showRecommendationDialog = false
                navController.navigate("home")
            },
            confirmButton = {
                Button(
                    onClick = { 
                        showRecommendationDialog = false
                        navController.navigate("home")
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Got it, thanks!")
                }
            },
            title = { 
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💡 Recommended Actions", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Based on your $severity/10 log, here are some suggestions to help you feel better:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    recommendations.forEach { tip ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("• ", fontWeight = FontWeight.Bold)
                            Text(tip, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
                    .statusBarsPadding()
            ) {
                Text(
                    text = "DRIFTWATCH",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (editingSymptomId != 0L) "Edit History Log" else "Diagnostic Assistant",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (!isFormValid) {
                            showValidationDialog = true
                        } else {
                            val detailSummary = buildString {
                                if (selectedSubOptions.isNotEmpty()) {
                                    append("Symptoms: ${selectedSubOptions.joinToString(", ")}")
                                }
                                if (showBodyPart && selectedBodyParts.isNotEmpty()) {
                                    append("\nLocation: ${selectedBodyParts.joinToString(", ")}")
                                }
                                if (showBowelType && selectedBowelTypes.isNotEmpty()) {
                                    append("\nBowel Type: ${selectedBowelTypes.joinToString(", ")}")
                                }
                                if (userNotes.isNotBlank()) {
                                    append("\nUser Notes: $userNotes")
                                }
                            }

                            val classificationName =
                                if (selectedClassifications.contains("Other") && customSymptom.isNotBlank()) {
                                    (selectedClassifications - "Other" + customSymptom).joinToString(
                                        " + "
                                    )
                                } else {
                                    selectedClassifications.joinToString(" + ")
                                }

                            val recs = generateRecommendations(selectedClassifications, severity)
                            recommendations = recs

                            viewModel.logUserSymptom(
                                id = editingSymptomId,
                                type = classificationName.ifBlank { "Unspecified" },
                                severity = severity,
                                notes = detailSummary.trim(),
                                rawClassifications = selectedClassifications.joinToString(","),
                                rawSubOptions = selectedSubOptions.joinToString(","),
                                rawBodyParts = selectedBodyParts.joinToString(","),
                                rawBowelTypes = selectedBowelTypes.joinToString(","),
                                customSymptomText = customSymptom,
                                userNotes = userNotes
                            )

                            // to show the recommendation pop up after logging symptoms
                            showRecommendationDialog = true
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        if (editingSymptomId != 0L) "Update Log" else "Save Log",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Step 1: Stacked Classifications (Multi-Select Chips) ---
            item {
                Text(
                    "Symptom Classifications",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    classifications.forEach { category ->
                        val isSelected = selectedClassifications.contains(category)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedClassifications =
                                    if (isSelected) selectedClassifications - category else selectedClassifications + category
                                if (isSelected) {
                                    val relatedSubs = subOptionsMap[category] ?: emptyList()
                                    selectedSubOptions =
                                        selectedSubOptions.filter { !relatedSubs.contains(it) }
                                            .toSet()
                                }
                            },
                            label = { Text(category) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            // --- Step 2: Dynamic Sub-Options ---
            if (selectedClassifications.contains("Other")) {
                item {
                    OutlinedTextField(
                        value = customSymptom,
                        onValueChange = { customSymptom = it },
                        label = { Text("Specify Other Symptom") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            selectedClassifications.filter { subOptionsMap.containsKey(it) }.forEach { category ->
                item {
                    Text(
                        "$category Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        subOptionsMap[category]?.forEach { option ->
                            val isSelected = selectedSubOptions.contains(option)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedSubOptions =
                                        if (isSelected) selectedSubOptions - option else selectedSubOptions + option
                                },
                                label = { Text(option) },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }

            // --- Step 3: Location ---
            if (showBodyPart) {
                item {
                    Text(
                        "Select Location",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        bodyParts.forEach { part ->
                            val isSelected = selectedBodyParts.contains(part)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedBodyParts =
                                        if (isSelected) selectedBodyParts - part else selectedBodyParts + part
                                },
                                label = { Text(part) },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onTertiary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }

            // --- Step 3.5: Bowel Type ---
            if (showBowelType) {
                item {
                    Text(
                        "Bowel Movement Type",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        bowelTypes.forEach { type ->
                            val isSelected = selectedBowelTypes.contains(type)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedBowelTypes =
                                        if (isSelected) selectedBowelTypes - type else selectedBowelTypes + type
                                },
                                label = { Text(type) },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF795548),
                                    selectedLabelColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }

            // --- Step 4: Severity Rotary ---
            item {
                Text(
                    text = "Severity: $severity",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = getSeverityColor(severity)
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Added by Yevgeniy Mazur: Apple-style rotary severity selector
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    LazyRow(
                        state = severityListState,
                        flingBehavior = severitySnapBehavior,
                        contentPadding = PaddingValues(horizontal = horizontalPaddingSeverity),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.height(110.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(10) { index ->
                            val level = index + 1
                            val color = getSeverityColor(level)

                            Box(
                                modifier = Modifier.size(itemSize),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    modifier = Modifier.size(if (severity == level) 64.dp else 48.dp),
                                    shape = CircleShape,
                                    color = if (severity == level) color else color.copy(alpha = 0.1f),
                                    border = if (severity == level) null else androidx.compose.foundation.BorderStroke(
                                        2.dp,
                                        color.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = level.toString(),
                                            fontWeight = if (severity == level) FontWeight.Black else FontWeight.Bold,
                                            color = if (severity == level) Color.Black else color,
                                            fontSize = if (severity == level) 24.sp else 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Surface(
                        modifier = Modifier.size(72.dp),
                        color = Color.Transparent,
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.primary.copy(0.2f)
                        )
                    ) {}
                }
            }

            // --- Step 5: Notes ---
            item {
                Text(
                    "Additional Context",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = userNotes,
                    onValueChange = { userNotes = it },
                    placeholder = { Text("How do you feel otherwise?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}
