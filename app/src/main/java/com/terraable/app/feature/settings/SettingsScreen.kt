package com.terraable.app.feature.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.terraable.app.core.model.MobilityType
import com.terraable.app.ui.components.StatusBadge
import com.terraable.app.ui.components.TerraCard
import com.terraable.app.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Settings & Profile",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
            color = TextPrimary
        )
        Text(
            text = "Manage your accessibility profile, units & local data",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 1. Accessibility Profile Configuration
        TerraCard(
            modifier = Modifier.fillMaxWidth(),
            borderBrush = Brush.horizontalGradient(listOf(AccessPurple.copy(alpha = 0.5f), Color.Transparent))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Accessible, contentDescription = null, tint = AccessPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Accessibility Profile", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                }
                StatusBadge("Active", AccessPurple)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mobility Mode Selector
            Text("Mobility Equipment", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            val mobilityTypes = MobilityType.values()
            mobilityTypes.forEach { type ->
                val isSelected = state.profile.mobilityType == type
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) AccessPurple.copy(alpha = 0.2f) else SurfaceElevated,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.2.dp, AccessPurple) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { viewModel.updateMobilityType(type) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = type.displayName, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AccessPurple, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Max continuous walking slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Default Walking Tolerance", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Text("${state.profile.maxContinuousWalkingMeters}m", style = MaterialTheme.typography.labelMedium, color = AccessPurpleLight)
            }
            Slider(
                value = state.profile.maxContinuousWalkingMeters.toFloat(),
                onValueChange = { viewModel.updateWalkingTolerance(it.toInt()) },
                valueRange = 100f..800f,
                steps = 6,
                colors = SliderDefaults.colors(thumbColor = AccessPurple, activeTrackColor = AccessPurple, inactiveTrackColor = SurfaceHighlight)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Required facilities toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Roll-in Accessible Bathroom", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Switch(
                    checked = state.profile.requiresAccessibleBathroom,
                    onCheckedChange = { viewModel.toggleAccessibleBathroom(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccessPurple)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Elevator Required at Stations", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Switch(
                    checked = state.profile.requiresElevator,
                    onCheckedChange = { viewModel.toggleElevator(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccessPurple)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Preferences & Units
        TerraCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "App Preferences & Location", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Home Location", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Text(state.homeLocation, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                StatusBadge("Default Origin", RouteBlue)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Theme", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Text("OLED Deep Dark", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                StatusBadge("Enabled", EcoGreen)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Measurement Units", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Text("Metric (°C, km, meters)", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                StatusBadge("SI Standard", RouteBlue)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Data & Privacy Card
        TerraCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Data Storage & Privacy", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "All travel constraints, health profiles, and search queries are stored locally on your device. No biometric or health data is sold or sent to 3rd-party ad networks.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.exportProfile() },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Export Profile", color = TextPrimary)
                }

                Button(
                    onClick = { /* Import flow */ },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Import Data", color = TextPrimary)
                }
            }

            AnimatedVisibility(visible = state.exportStatus != null) {
                state.exportStatus?.let { status ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = status, style = MaterialTheme.typography.labelSmall, color = EcoGreenLight)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
