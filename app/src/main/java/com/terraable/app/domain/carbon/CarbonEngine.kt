package com.terraable.app.domain.carbon

import com.terraable.app.core.model.CarbonEstimate
import com.terraable.app.core.model.TransportMode
import com.terraable.app.core.model.TripCarbonBreakdown

class CarbonEngine {

    /**
     * Estimates carbon footprint for a given segment based on transport mode and distance in km.
     * All factors are transparently calibrated in g CO2 / passenger-km.
     */
    fun estimateSegmentCarbon(mode: TransportMode, distanceKm: Double): CarbonEstimate {
        val kgCo2 = (mode.gCo2PerPassengerKm * distanceKm) / 1000.0
        val baselineFlightKg = (TransportMode.FLIGHT.gCo2PerPassengerKm * distanceKm) / 1000.0
        val savings = (baselineFlightKg - kgCo2).coerceAtLeast(0.0)

        return CarbonEstimate(
            transportMode = mode,
            distanceKm = distanceKm,
            estimatedKgCo2 = (kgCo2 * 10.0).toInt() / 10.0,
            methodology = "DEFRA 2024 / IPCC Tier 1 Passenger Model",
            savingsVsBaselineKg = (savings * 10.0).toInt() / 10.0
        )
    }

    /**
     * Calculates trip-level carbon breakdown and savings compared with conventional flight & car choices.
     */
    fun calculateTripBreakdown(segments: List<Pair<TransportMode, Double>>): TripCarbonBreakdown {
        var totalKg = 0.0
        var totalDistance = 0.0
        val estimates = mutableListOf<CarbonEstimate>()

        for ((mode, distance) in segments) {
            val est = estimateSegmentCarbon(mode, distance)
            totalKg += est.estimatedKgCo2
            totalDistance += distance
            estimates.add(est)
        }

        val baselineFlight = (TransportMode.FLIGHT.gCo2PerPassengerKm * totalDistance) / 1000.0
        val baselineCar = (TransportMode.CAR_PETROL.gCo2PerPassengerKm * totalDistance) / 1000.0
        val co2Saved = (baselineCar - totalKg).coerceAtLeast(0.0)

        return TripCarbonBreakdown(
            totalEstimatedKgCo2 = (totalKg * 10.0).toInt() / 10.0,
            baselineFlightKgCo2 = (baselineFlight * 10.0).toInt() / 10.0,
            baselineCarKgCo2 = (baselineCar * 10.0).toInt() / 10.0,
            co2SavedKg = (co2Saved * 10.0).toInt() / 10.0,
            segmentEstimates = estimates
        )
    }
}
