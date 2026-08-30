package com.terraable.app.ui.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.terraable.app.core.model.HourlyForecastPoint
import com.terraable.app.core.model.ScoreBreakdown
import com.terraable.app.ui.theme.*

@Composable
fun HorizontalScoreBar(
    title: String,
    scorePercentage: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = scorePercentage / 100f,
        label = "scoreProgress"
    )

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
            Text(
                text = "$scorePercentage%",
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(SurfaceHighlight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedProgress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(color.copy(alpha = 0.7f), color)
                        )
                    )
            )
        }
    }
}

@Composable
fun ScoreBreakdownVisualizer(
    scores: ScoreBreakdown,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        HorizontalScoreBar("Accessibility", scores.accessibilityScore, AccessPurple)
        HorizontalScoreBar("Sustainability", scores.sustainabilityScore, EcoGreen)
        HorizontalScoreBar("Comfort", scores.comfortScore, RouteBlue)
        HorizontalScoreBar("Budget Fitness", scores.budgetScore, WarningAmber)
        HorizontalScoreBar("Reliability", scores.reliabilityScore, Color(0xFF38BDF8))
    }
}

@Composable
fun CarbonComparisonChart(
    flightKg: Double,
    carKg: Double,
    tripKg: Double,
    modifier: Modifier = Modifier
) {
    val maxVal = maxOf(flightKg, carKg, tripKg, 100.0)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Estimated CO₂e Comparison",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Text(
            text = "Based on DEFRA passenger travel emissions model",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        CarbonBarItem("Commercial Flight", flightKg, (flightKg / maxVal).toFloat(), SosRed)
        CarbonBarItem("Private Petrol Car", carKg, (carKg / maxVal).toFloat(), WarningOrange)
        CarbonBarItem("This TerraAble Plan", tripKg, (tripKg / maxVal).toFloat(), EcoGreen)
    }
}

@Composable
private fun CarbonBarItem(
    label: String,
    kgCo2: Double,
    fraction: Float,
    barColor: Color
) {
    val animatedFraction by animateFloatAsState(targetValue = fraction.coerceIn(0.05f, 1f), label = "carbonFraction")

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(text = "$kgCo2 kg CO₂e", style = MaterialTheme.typography.labelSmall, color = barColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(SurfaceHighlight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedFraction)
                    .clip(RoundedCornerShape(5.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
fun HourlyWeatherForecastRow(
    points: List<HourlyForecastPoint>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        points.take(5).forEach { pt ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = pt.timeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${pt.temperature.toInt()}°",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💧${pt.rainProbability}%",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = if (pt.rainProbability > 50) WarningAmber else RouteBlueLight
                    )
                }
            }
        }
    }
}
