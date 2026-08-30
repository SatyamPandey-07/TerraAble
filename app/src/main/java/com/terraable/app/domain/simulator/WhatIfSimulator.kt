package com.terraable.app.domain.simulator

import com.terraable.app.core.model.TripCandidate

enum class SimulationScenario(val label: String, val chipDescription: String) {
    GREENER_ROUTE("🌱 Greener route", "Prioritize maximum carbon reduction"),
    STRICT_WALKING_200M("🚶 Walking ≤ 200m", "Enforce strict 200m walking limit"),
    BUDGET_PLUS_3000("₹ Budget +₹3,000", "Add ₹3,000 for 1st class accessible cabin"),
    COMFORT_PRIORITY("🛋 Max Comfort", "Direct accessible cab & private AC coupe")
}

data class MetricDelta(
    val title: String,
    val beforeValue: String,
    val afterValue: String,
    val impactType: ImpactType, // IMPROVED, WORSE, NEUTRAL
    val deltaNote: String
)

enum class ImpactType(val label: String) {
    IMPROVED("Improved"),
    WORSE("Trade-off"),
    NEUTRAL("Unchanged")
}

data class SimulationResult(
    val scenario: SimulationScenario,
    val headline: String,
    val summary: String,
    val simulatedCandidate: TripCandidate,
    val metricDeltas: List<MetricDelta>
)

class WhatIfSimulator {

    fun simulate(baseTrip: TripCandidate, scenario: SimulationScenario): SimulationResult {
        return when (scenario) {
            SimulationScenario.GREENER_ROUTE -> {
                val newCost = (baseTrip.totalCostInr - 1700).coerceAtLeast(15000.0)
                val newCarbon = (baseTrip.carbonKg * 0.72)
                val newWalking = (baseTrip.totalWalkingMeters + 180)
                val newTransfers = baseTrip.transferCount + 1

                val simulated = baseTrip.copy(
                    totalCostInr = newCost,
                    carbonKg = (newCarbon * 10).toInt() / 10.0,
                    totalWalkingMeters = newWalking,
                    transferCount = newTransfers,
                    accessibilityConfidencePercentage = (baseTrip.accessibilityConfidencePercentage - 8).coerceAtLeast(60)
                )

                SimulationResult(
                    scenario = scenario,
                    headline = "Greener Route Simulation",
                    summary = "Switching to regional electric express saves 9 kg CO₂ and ₹1,700, but adds 1 transfer and 180m extra walking at junction platform.",
                    simulatedCandidate = simulated,
                    metricDeltas = listOf(
                        MetricDelta("Carbon Emissions", "${baseTrip.carbonKg} kg", "${simulated.carbonKg} kg", ImpactType.IMPROVED, "-9 kg CO₂e saved"),
                        MetricDelta("Trip Cost", "₹${baseTrip.totalCostInr.toInt()}", "₹${simulated.totalCostInr.toInt()}", ImpactType.IMPROVED, "₹1,700 cheaper"),
                        MetricDelta("Continuous Walking", "${baseTrip.totalWalkingMeters}m", "${simulated.totalWalkingMeters}m", ImpactType.WORSE, "+180m extra transfer walk"),
                        MetricDelta("Transfers", "${baseTrip.transferCount}", "${simulated.transferCount}", ImpactType.WORSE, "+1 transfer added")
                    )
                )
            }

            SimulationScenario.STRICT_WALKING_200M -> {
                val newCost = baseTrip.totalCostInr + 1500.0
                val newWalking = 160
                val newCarbon = baseTrip.carbonKg + 4.0

                val simulated = baseTrip.copy(
                    totalCostInr = newCost,
                    carbonKg = newCarbon,
                    totalWalkingMeters = newWalking,
                    accessibilityConfidencePercentage = (baseTrip.accessibilityConfidencePercentage + 5).coerceAtMost(98)
                )

                SimulationResult(
                    scenario = scenario,
                    headline = "Strict Walking Constraint (≤ 200m)",
                    summary = "Adds a verified door-to-platform accessible buggy at CST station and accessible taxi shuttle at Madgaon. Walking drops to 160m.",
                    simulatedCandidate = simulated,
                    metricDeltas = listOf(
                        MetricDelta("Continuous Walking", "${baseTrip.totalWalkingMeters}m", "160m", ImpactType.IMPROVED, "-80m reduced walking"),
                        MetricDelta("Accessibility Confidence", "${baseTrip.accessibilityConfidencePercentage}%", "${simulated.accessibilityConfidencePercentage}%", ImpactType.IMPROVED, "+5% platform assist"),
                        MetricDelta("Trip Cost", "₹${baseTrip.totalCostInr.toInt()}", "₹${newCost.toInt()}", ImpactType.WORSE, "+₹1,500 dedicated buggy/taxi"),
                        MetricDelta("Carbon Emissions", "${baseTrip.carbonKg} kg", "${newCarbon} kg", ImpactType.WORSE, "+4 kg CO₂e (short taxi leg)")
                    )
                )
            }

            SimulationScenario.BUDGET_PLUS_3000 -> {
                val newCost = baseTrip.totalCostInr + 3000.0
                val newWalking = (baseTrip.totalWalkingMeters - 60).coerceAtLeast(120)

                val simulated = baseTrip.copy(
                    totalCostInr = newCost,
                    totalWalkingMeters = newWalking,
                    accessibilityConfidencePercentage = 95
                )

                SimulationResult(
                    scenario = scenario,
                    headline = "Premium Accessible Upgrade (+₹3,000)",
                    summary = "Upgrades to Executive Vande Bharat AC Chair Car with dedicated wide-aisle wheelchair spaces and priority porter assistance.",
                    simulatedCandidate = simulated,
                    metricDeltas = listOf(
                        MetricDelta("Accessibility Confidence", "${baseTrip.accessibilityConfidencePercentage}%", "95%", ImpactType.IMPROVED, "Vande Bharat dedicated bay"),
                        MetricDelta("Continuous Walking", "${baseTrip.totalWalkingMeters}m", "${newWalking}m", ImpactType.IMPROVED, "-60m platform buggy"),
                        MetricDelta("Trip Cost", "₹${baseTrip.totalCostInr.toInt()}", "₹${newCost.toInt()}", ImpactType.WORSE, "+₹3,000 premium cabin"),
                        MetricDelta("Comfort Level", "84%", "96%", ImpactType.IMPROVED, "Ergonomic wide seating")
                    )
                )
            }

            SimulationScenario.COMFORT_PRIORITY -> {
                val newCost = baseTrip.totalCostInr + 4500.0
                val simulated = baseTrip.copy(
                    totalCostInr = newCost,
                    carbonKg = baseTrip.carbonKg + 8.0,
                    totalWalkingMeters = 110,
                    accessibilityConfidencePercentage = 97
                )

                SimulationResult(
                    scenario = scenario,
                    headline = "Maximum Comfort Optimization",
                    summary = "Private door-to-door wheelchair van to station + 1st AC coupe with en-suite accessible washroom access.",
                    simulatedCandidate = simulated,
                    metricDeltas = listOf(
                        MetricDelta("Comfort Score", "84%", "98%", ImpactType.IMPROVED, "Private coupe & door pickup"),
                        MetricDelta("Continuous Walking", "${baseTrip.totalWalkingMeters}m", "110m", ImpactType.IMPROVED, "Zero station stairs/walking"),
                        MetricDelta("Trip Cost", "₹${baseTrip.totalCostInr.toInt()}", "₹${newCost.toInt()}", ImpactType.WORSE, "+₹4,500"),
                        MetricDelta("Carbon Emissions", "${baseTrip.carbonKg} kg", "${simulated.carbonKg} kg", ImpactType.WORSE, "+8 kg CO₂e")
                    )
                )
            }
        }
    }
}
