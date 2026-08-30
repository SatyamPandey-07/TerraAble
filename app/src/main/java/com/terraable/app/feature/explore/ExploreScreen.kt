package com.terraable.app.feature.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.terraable.app.ui.components.HeroGradientCard
import com.terraable.app.ui.components.StatusBadge
import com.terraable.app.ui.components.TerraCard
import com.terraable.app.ui.theme.*

data class DestinationSpotlight(
    val title: String,
    val location: String,
    val accessibilityRating: Int, // e.g. 94%
    val carbonScore: String, // e.g. "Low Carbon"
    val highlights: String,
    val gradient: Brush
)

@Composable
fun ExploreScreen(
    onSelectDestination: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val destinations = listOf(
        DestinationSpotlight(
            title = "South Goa Accessible Beach Reserve",
            location = "Goa, India",
            accessibilityRating = 94,
            carbonScore = "Zero Emission Transit",
            highlights = "All-terrain beach wheelchairs, level boardwalks, solar-powered eco-lodges.",
            gradient = HeroGreenGradient
        ),
        DestinationSpotlight(
            title = "Mysore Heritage Step-Free Circuit",
            location = "Karnataka, India",
            accessibilityRating = 91,
            carbonScore = "Electric Rail Direct",
            highlights = "Electric golf-cart palace access, tactile museum trails, accessible gardens.",
            gradient = HeroBlueGradient
        ),
        DestinationSpotlight(
            title = "Kerala Solar Backwaters Houseboat",
            location = "Alleppey, Kerala",
            accessibilityRating = 88,
            carbonScore = "100% Solar Powered",
            highlights = "Ramp-equipped solar catamaran, roll-in cabins, certified mobility crew.",
            gradient = HeroPurpleGradient
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Explore Accessible Green Getaways",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
            color = TextPrimary
        )
        Text(
            text = "Verified step-free & low-carbon travel destinations",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Hero Spotlight
        HeroGradientCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                StatusBadge("Featured Spotlight", Color.White)
                Text("94% Access Score", style = MaterialTheme.typography.labelSmall, color = EcoGreenLight)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "South Goa Accessible Eco Retreat",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Text(
                text = "Connect via Mumbai CST electric train directly to Madgaon with step-free seaside chalets.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier.clickable { onSelectDestination("Goa") }
            ) {
                Text(
                    text = "Plan Trip to Goa →",
                    style = MaterialTheme.typography.labelMedium,
                    color = BgDark,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Eco-Certified Destinations",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        destinations.forEach { dest ->
            TerraCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectDestination(dest.location) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = dest.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = dest.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = RouteBlueLight
                        )
                    }
                    StatusBadge("♿ ${dest.accessibilityRating}%", AccessPurple)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = dest.highlights,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge("🌱 ${dest.carbonScore}", EcoGreen)
                    Text(
                        text = "Explore Route →",
                        style = MaterialTheme.typography.labelSmall,
                        color = RouteBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
