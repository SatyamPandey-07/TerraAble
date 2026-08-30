package com.terraable.app.feature.safety

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.terraable.app.ui.components.HoldToConfirmSosButton
import com.terraable.app.ui.components.StatusBadge
import com.terraable.app.ui.components.TerraCard
import com.terraable.app.ui.theme.*

@Composable
fun SafetyScreen(
    viewModel: SafetyViewModel,
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
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Emergency & SOS",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                    color = TextPrimary
                )
                Text(
                    text = "Are you in an emergency?",
                    style = MaterialTheme.typography.bodySmall,
                    color = SosRed
                )
            }
            StatusBadge("Demo Mode Active", SosRed)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SOS Button (Hold for 3 seconds)
        HoldToConfirmSosButton(
            onTriggered = { viewModel.triggerSos() },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // SOS Triggered Confirmation Banner
        AnimatedVisibility(visible = state.isSosTriggered) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0x33EF4444),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, SosRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🚨 SOS Alert Triggered",
                            style = MaterialTheme.typography.titleMedium,
                            color = SosRed
                        )
                        IconButton(onClick = { viewModel.dismissSosAlert() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = TextPrimary)
                        }
                    }
                    Text(
                        text = state.sosAlertMessage ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Emergency Category Selector Grid
        Text(
            text = "Select Emergency Type",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val categories = EmergencyCategory.values()
            categories.forEach { cat ->
                val isSelected = state.selectedCategory == cat
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectCategory(cat) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) SurfaceHighlight else SurfaceDark,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, SosRed) else androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderSubtle)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = cat.icon, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cat.title.split(" ").first(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) TextPrimary else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Coordinates & Location Sharing Preparation
        TerraCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, tint = RouteBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Prepared Location Sharing",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                StatusBadge("GPS Active", RouteBlue)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Lat: ${state.currentLat}° N, Lng: ${state.currentLng}° E",
                style = MaterialTheme.typography.labelMedium,
                color = RouteBlueLight
            )
            Text(
                text = state.locationAddress,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Emergency Contacts Card
        TerraCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Configured Emergency Contacts",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Text(
                text = "These contacts will receive your location and trip passport during an alert.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            state.emergencyContacts.forEach { contact ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceElevated,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = contact.name, style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                            Text(text = contact.relation, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Text(text = contact.phoneNumber, style = MaterialTheme.typography.labelSmall, color = EcoGreenLight)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
