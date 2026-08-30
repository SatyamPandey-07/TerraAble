package com.terraable.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.terraable.app.ui.theme.*

enum class NavSection(val title: String) {
    DASHBOARD("Dashboard"),
    EXPLORE("Explore"),
    PLAN("Plan"),
    SAFETY("Safety"),
    SETTINGS("Settings")
}

@Composable
fun TerraBottomNavBar(
    currentSection: NavSection,
    onSectionSelected: (NavSection) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(72.dp),
        shape = RoundedCornerShape(36.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, SurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Dashboard
            NavItem(
                icon = if (currentSection == NavSection.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                label = "Dashboard",
                isSelected = currentSection == NavSection.DASHBOARD,
                onClick = { onSectionSelected(NavSection.DASHBOARD) },
                activeColor = EcoGreen
            )

            // 2. Explore
            NavItem(
                icon = if (currentSection == NavSection.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                label = "Explore",
                isSelected = currentSection == NavSection.EXPLORE,
                onClick = { onSectionSelected(NavSection.EXPLORE) },
                activeColor = RouteBlue
            )

            // 3. Plan Trip (Emphasized Center Action)
            CenterPlanButton(
                isSelected = currentSection == NavSection.PLAN,
                onClick = { onSectionSelected(NavSection.PLAN) }
            )

            // 4. Safety
            NavItem(
                icon = if (currentSection == NavSection.SAFETY) Icons.Filled.Shield else Icons.Outlined.Shield,
                label = "Safety",
                isSelected = currentSection == NavSection.SAFETY,
                onClick = { onSectionSelected(NavSection.SAFETY) },
                activeColor = SosRed
            )

            // 5. Settings
            NavItem(
                icon = if (currentSection == NavSection.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                label = "Settings",
                isSelected = currentSection == NavSection.SETTINGS,
                onClick = { onSectionSelected(NavSection.SETTINGS) },
                activeColor = AccessPurple
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    activeColor: Color
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                color = if (isSelected) activeColor else TextMuted
            )
        )
    }
}

@Composable
private fun CenterPlanButton(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(EcoGreen, Color(0xFF0284C7))
                )
            )
            .border(2.dp, if (isSelected) Color.White else Color(0x66FFFFFF), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.AddLocationAlt,
            contentDescription = "Plan Trip",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}
