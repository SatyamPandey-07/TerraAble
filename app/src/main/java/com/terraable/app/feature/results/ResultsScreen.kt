package com.terraable.app.feature.results

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.terraable.app.core.model.*
import com.terraable.app.domain.simulator.ImpactType
import com.terraable.app.domain.simulator.SimulationScenario
import com.terraable.app.ui.charts.CarbonComparisonChart
import com.terraable.app.ui.charts.ScoreBreakdownVisualizer
import com.terraable.app.ui.components.*
import com.terraable.app.ui.theme.*

@Composable
fun ResultsScreen(
    viewModel: ResultsViewModel,
    onBackToPlanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val tabScrollState = rememberScrollState()
    val simScrollState = rememberScrollState()

    val frontier = state.frontier
    if (frontier == null) {
        Box(
            modifier = modifier.fillMaxSize().background(BgDark),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No generated plan available.", color = TextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryGradientButton(text = "Go to Planner", onClick = onBackToPlanner, modifier = Modifier.width(200.dp))
            }
        }
        return
    }

    val currentTrip = viewModel.getCurrentSelectedTrip() ?: frontier.recommendedTrip

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToPlanner,
                    modifier = Modifier.clip(CircleShape).background(SurfaceElevated)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Pareto Frontier Plans",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "4 Optimized Trade-off Candidates",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            StatusBadge("Pareto Solved", EcoGreen)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Pareto Frontier Tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(tabScrollState),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val tabs = listOf(
                ParetoTag.BEST_BALANCE to (EcoGreen to "₹27,400 • 33kg"),
                ParetoTag.GREENEST to (Color(0xFF10B981) to "₹25,700 • 29kg"),
                ParetoTag.MOST_ACCESSIBLE to (AccessPurple to "₹28,900 • 91%"),
                ParetoTag.FASTEST to (RouteBlue to "₹31,200 • 2h 40m")
            )

            tabs.forEach { (tag, meta) ->
                val (color, priceInfo) = meta
                val isSelected = state.selectedTag == tag
                Surface(
                    modifier = Modifier
                        .width(150.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { viewModel.selectTag(tag) },
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) SurfaceHighlight else SurfaceDark,
                    border = if (isSelected) BorderStroke(1.5.dp, color) else BorderStroke(1.dp, SurfaceBorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = tag.title,
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
                            color = if (isSelected) color else TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = priceInfo,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Main Candidate Hero Card
        TerraCard(
            modifier = Modifier.fillMaxWidth(),
            borderBrush = Brush.horizontalGradient(
                listOf(
                    when (currentTrip.tag) {
                        ParetoTag.BEST_BALANCE -> EcoGreen
                        ParetoTag.GREENEST -> Color(0xFF10B981)
                        ParetoTag.MOST_ACCESSIBLE -> AccessPurple
                        ParetoTag.FASTEST -> RouteBlue
                    },
                    Color.Transparent
                )
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    if (currentTrip.isRecommended) {
                        StatusBadge("★ Recommended For You", EcoGreen)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    Text(
                        text = currentTrip.tag.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = currentTrip.tag.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${currentTrip.totalCostInr.toInt()}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                        color = EcoGreenLight
                    )
                    Text(
                        text = "for 2 travelers",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4-Column Key Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn(title = "Carbon", value = "${currentTrip.carbonKg} kg", unit = "CO₂e", color = EcoGreen)
                MetricColumn(title = "Duration", value = "${currentTrip.durationMinutes / 60}h ${currentTrip.durationMinutes % 60}m", unit = "travel", color = RouteBlue)
                MetricColumn(title = "Transfers", value = "${currentTrip.transferCount}", unit = "transfers", color = TextPrimary)
                MetricColumn(title = "Continuous Walk", value = "${currentTrip.totalWalkingMeters}m", unit = "max", color = AccessPurpleLight)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. "Why This?" Explainable Recommendation Card (Core Deliverable)
        ExpandableCard(
            title = "Why This Recommendation?",
            subtitle = "Explainable multi-objective trade-off analysis",
            icon = {
                Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(20.dp))
            },
            initiallyExpanded = true,
            accentColor = WarningAmber
        ) {
            // Core Rationale Summary
            Text(
                text = currentTrip.explanation.summary,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            ExplanationBullet(
                icon = "♿",
                title = "Accessibility Impact",
                detail = currentTrip.explanation.accessibilityRationale
            )

            ExplanationBullet(
                icon = "🌱",
                title = "Carbon Trade-off",
                detail = currentTrip.explanation.carbonRationale
            )

            ExplanationBullet(
                icon = "🌦",
                title = "Weather Resilience",
                detail = currentTrip.explanation.weatherSafetyRationale
            )

            currentTrip.explanation.whyNotGreenest?.let { whyNot ->
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceHighlight)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "💡 Why isn't the greenest option the best?",
                            style = MaterialTheme.typography.labelMedium,
                            color = WarningAmber
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = whyNot,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Accessibility Passport (Detailed Evidence Layer)
        ExpandableCard(
            title = "Accessibility Passport",
            subtitle = "Overall Confidence: ${currentTrip.passport.overallConfidenceScore}% • Evidence-based verification",
            icon = {
                Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = AccessPurple, modifier = Modifier.size(20.dp))
            },
            initiallyExpanded = true,
            accentColor = AccessPurple
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Confidence Score",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Text(
                        text = "${currentTrip.passport.overallConfidenceScore}%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AccessPurpleLight
                    )
                }

                StatusBadge(
                    text = if (currentTrip.passport.meetsWheelchairRequirements) "Wheelchair Certified" else "Mobility Attention",
                    color = if (currentTrip.passport.meetsWheelchairRequirements) EcoGreen else WarningOrange
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Evidence Items List
            currentTrip.passport.items.forEach { evidence ->
                EvidenceItemRow(evidence)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = currentTrip.passport.disclaimer,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Trip Score Visualizer & Radar Bars
        TerraCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Trip Score Visualizer",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Text(
                text = "Normalized multi-objective fitness scores",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            ScoreBreakdownVisualizer(scores = currentTrip.scoreBreakdown)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Carbon Estimation Chart
        TerraCard(modifier = Modifier.fillMaxWidth()) {
            CarbonComparisonChart(
                flightKg = 68.0,
                carKg = 54.0,
                tripKg = currentTrip.carbonKg
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 7. Route Segments & Timeline
        TerraCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Journey Timeline & Stops",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Text(
                text = "${currentTrip.segments.size} travel legs • Sheltered connections",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            currentTrip.segments.forEachIndexed { index, seg ->
                SegmentTimelineItem(segment = seg, isLast = index == currentTrip.segments.size - 1)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 8. "What If?" Interactive Simulator (Interactive Feature)
        TerraCard(
            modifier = Modifier.fillMaxWidth(),
            borderBrush = Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFFA855F7)))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = RouteBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "“What If?” Simulator",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                StatusBadge("Interactive Delta", RouteBlue)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Test instant constraints and explore mathematical trade-off changes.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Simulator Scenario Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(simScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val scenarios = listOf(
                    SimulationScenario.GREENER_ROUTE,
                    SimulationScenario.STRICT_WALKING_200M,
                    SimulationScenario.BUDGET_PLUS_3000,
                    SimulationScenario.COMFORT_PRIORITY
                )
                scenarios.forEach { sc ->
                    PromptChip(
                        text = sc.label,
                        onClick = { viewModel.runSimulation(sc) },
                        isSelected = state.activeSimulation?.scenario == sc,
                        accentColor = RouteBlue
                    )
                }
            }

            // Simulation Result Delta Card
            state.activeSimulation?.let { sim ->
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceElevated,
                    border = BorderStroke(1.2.dp, RouteBlue.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sim.headline,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            IconButton(onClick = { viewModel.clearSimulation() }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                            }
                        }

                        Text(
                            text = sim.summary,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        sim.metricDeltas.forEach { delta ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text(text = delta.title, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                    Text(text = delta.deltaNote, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                }
                                Row(
                                    modifier = Modifier.weight(1.5f),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = delta.beforeValue, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                    Text(text = " → ", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    Text(
                                        text = delta.afterValue,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (delta.impactType == ImpactType.IMPROVED) EcoGreenLight else WarningOrange
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    StatusBadge(
                                        text = delta.impactType.label,
                                        color = if (delta.impactType == ImpactType.IMPROVED) EcoGreen else WarningOrange
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Book / Save Itinerary Button
        PrimaryGradientButton(
            text = "Save Itinerary & Share Passport",
            icon = {
                Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, tint = Color.White)
            },
            onClick = { /* Saved locally */ }
        )

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun MetricColumn(title: String, value: String, unit: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = color)
        Text(text = unit, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary)
    }
}

@Composable
private fun ExplanationBullet(icon: String, title: String, detail: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = icon, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = TextPrimary)
            Text(text = detail, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun EvidenceItemRow(item: FacilityEvidence) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceElevated,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Evidence Badge (Verified ✓, Reported ◉, Inferred ◐, Unknown ?)
            val (badgeBg, badgeTextColor) = when (item.state) {
                EvidenceState.VERIFIED -> EcoGreen to Color.White
                EvidenceState.REPORTED -> RouteBlue to Color.White
                EvidenceState.INFERRED -> WarningAmber to Color.Black
                EvidenceState.UNKNOWN -> Color(0xFF64748B) to Color.White
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.state.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    color = badgeTextColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${item.confidencePercentage}% conf.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Text(
                    text = item.details,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Source: ${item.sourceName} • ${item.lastVerified}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun SegmentTimelineItem(segment: RouteSegment, isLast: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when (segment.mode) {
                            TransportMode.TRAIN_ELECTRIC, TransportMode.TRAIN_DIESEL -> EcoGreen
                            TransportMode.ELECTRIC_CAR, TransportMode.ELECTRIC_BUS -> RouteBlue
                            TransportMode.FLIGHT -> SosRed
                            else -> TextSecondary
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${segment.segmentIndex}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(SurfaceHighlight)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(bottom = if (isLast) 0.dp else 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${segment.fromPlace} → ${segment.toPlace}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary
                )
                Text(
                    text = segment.departureTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Text(
                text = "${segment.mode.displayName} • ${segment.distanceKm} km • ${segment.durationMinutes} mins",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Text(
                text = segment.instructions,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            segment.weatherAlert?.let { alert ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🌦 $alert",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarningAmber
                )
            }
        }
    }
}
