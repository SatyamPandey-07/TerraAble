package com.terraable.app.feature.planner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.terraable.app.core.model.ParetoFrontier
import com.terraable.app.ui.components.*
import com.terraable.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel,
    onTripGenerated: (ParetoFrontier) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val chipScrollState = rememberScrollState()

    if (state.isGenerating) {
        GenerationLoadingDialog(stepText = state.generationStep, progress = state.generationProgress)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Header
        Text(
            text = "Where do you want to go?",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 26.sp),
            color = TextPrimary
        )
        Text(
            text = "AI-powered accessible and sustainable travel planner",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Large Prompt Input
        TerraCard(
            modifier = Modifier.fillMaxWidth(),
            borderBrush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                listOf(EcoGreen.copy(alpha = 0.5f), RouteBlue.copy(alpha = 0.5f))
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Prompt",
                        tint = EcoGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Natural Language Prompt",
                        style = MaterialTheme.typography.labelMedium,
                        color = EcoGreen
                    )
                }
                StatusBadge("AI Parser Ready", RouteBlue)
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = state.promptInput,
                onValueChange = { viewModel.onPromptChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                placeholder = {
                    Text(
                        text = "Plan a sustainable and accessible trip...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceElevated,
                    unfocusedContainerColor = SurfaceElevated,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Prompt Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(chipScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val chips = listOf(
                    "♿ Wheelchair friendly",
                    "🌱 Lowest carbon",
                    "₹ Budget trip",
                    "👴 Senior friendly",
                    "🚶 Minimal walking"
                )
                chips.forEach { chip ->
                    PromptChip(
                        text = chip,
                        onClick = { viewModel.applyPromptChip(chip) },
                        isSelected = state.promptInput.contains(chip.substring(2).trim(), ignoreCase = true)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Structured Trip Form (Auto-parsed from Prompt, and User-editable)
        ExpandableCard(
            title = "Trip Parameters",
            subtitle = "${state.structuredRequest.origin} → ${state.structuredRequest.destination} • ${state.structuredRequest.durationDays} Days • ₹${state.structuredRequest.budgetInr.toInt()}",
            icon = {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = RouteBlue,
                    modifier = Modifier.size(20.dp)
                )
            },
            initiallyExpanded = true,
            accentColor = RouteBlue
        ) {
            // Origin and Destination Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Origin", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.structuredRequest.origin,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Destination", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.structuredRequest.destination,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Duration, Budget, Travelers Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Duration", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${state.structuredRequest.durationDays} days",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Budget", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "₹${state.structuredRequest.budgetInr.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EcoGreenLight,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Travelers", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${state.structuredRequest.travelerCount} adults",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Accessibility Requirements Card
        TerraCard(
            modifier = Modifier.fillMaxWidth(),
            borderBrush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                listOf(AccessPurple.copy(alpha = 0.4f), Color.Transparent)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Accessible,
                        contentDescription = "Accessibility",
                        tint = AccessPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "♿ Accessibility Constraints",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                StatusBadge("Hard Constraints", AccessPurple)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Wheelchair Access Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Wheelchair Access", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Text("Guarantees ramp / lift access", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                Switch(
                    checked = state.structuredRequest.wheelchairRequired,
                    onCheckedChange = {
                        viewModel.updateStructuredRequest(state.structuredRequest.copy(wheelchairRequired = it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccessPurple
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Accessible Bathroom Requirement Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Accessible Restroom", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Text("Wide-swing door & grab bars", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                Switch(
                    checked = state.structuredRequest.accessibleBathroomRequired,
                    onCheckedChange = {
                        viewModel.updateStructuredRequest(state.structuredRequest.copy(accessibleBathroomRequired = it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccessPurple
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Max Continuous Walking Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Max Continuous Walking", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Text("${state.structuredRequest.maxWalkingMeters} meters", style = MaterialTheme.typography.labelMedium, color = AccessPurpleLight)
                }
                Slider(
                    value = state.structuredRequest.maxWalkingMeters.toFloat(),
                    onValueChange = {
                        viewModel.updateStructuredRequest(state.structuredRequest.copy(maxWalkingMeters = it.toInt()))
                    },
                    valueRange = 100f..1000f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = AccessPurple,
                        activeTrackColor = AccessPurple,
                        inactiveTrackColor = SurfaceHighlight
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Maximum Transfers Selector (0, 1, 2, 3+)
            Column {
                Text("Maximum Allowed Transfers", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val transferOptions = listOf(0, 1, 2, 3)
                    transferOptions.forEach { count ->
                        val isSelected = state.structuredRequest.maxTransfers == count
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.updateStructuredRequest(state.structuredRequest.copy(maxTransfers = count))
                                },
                            color = if (isSelected) AccessPurple else SurfaceElevated,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderSubtle)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (count == 3) "3+" else "$count",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Sustainability & Mode Preferences
        TerraCard(
            modifier = Modifier.fillMaxWidth(),
            borderBrush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                listOf(EcoGreen.copy(alpha = 0.4f), Color.Transparent)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Forest,
                        contentDescription = "Sustainability",
                        tint = EcoGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🌱 Sustainability Preferences",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                StatusBadge("Optimization Weights", EcoGreen)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Carbon Priority Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Carbon Priority", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Text(
                        text = if (state.structuredRequest.carbonPriority > 0.7f) "High" else "Medium",
                        style = MaterialTheme.typography.labelMedium,
                        color = EcoGreenLight
                    )
                }
                Slider(
                    value = state.structuredRequest.carbonPriority,
                    onValueChange = {
                        viewModel.updateStructuredRequest(state.structuredRequest.copy(carbonPriority = it))
                    },
                    valueRange = 0.1f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = EcoGreen,
                        activeTrackColor = EcoGreen,
                        inactiveTrackColor = SurfaceHighlight
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Public Transport / Rail Preference Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Public Transit / Train Preference", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Text(
                        text = if (state.structuredRequest.publicTransportPreference > 0.7f) "High" else "Medium",
                        style = MaterialTheme.typography.labelMedium,
                        color = RouteBlueLight
                    )
                }
                Slider(
                    value = state.structuredRequest.publicTransportPreference,
                    onValueChange = {
                        viewModel.updateStructuredRequest(state.structuredRequest.copy(publicTransportPreference = it))
                    },
                    valueRange = 0.1f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = RouteBlue,
                        activeTrackColor = RouteBlue,
                        inactiveTrackColor = SurfaceHighlight
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Generate My Trip Primary Action
        PrimaryGradientButton(
            text = "Generate My Trip",
            icon = {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = Color.White
                )
            },
            onClick = {
                viewModel.generateTrip { frontier ->
                    onTripGenerated(frontier)
                }
            }
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun GenerationLoadingDialog(
    stepText: String,
    progress: Float
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, EcoGreen.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(64.dp),
                    color = EcoGreen,
                    strokeWidth = 5.dp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Generating Multi-Objective Plans",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stepText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = EcoGreenLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = EcoGreen,
                    trackColor = SurfaceHighlight
                )
            }
        }
    }
}
