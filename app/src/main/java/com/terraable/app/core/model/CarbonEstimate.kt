package com.terraable.app.core.model

enum class TransportMode(
    val displayName: String,
    val gCo2PerPassengerKm: Double, // Grams of CO2 per passenger km
    val iconName: String
) {
    TRAIN_ELECTRIC("Electric Train / Express", 35.0, "train"),
    TRAIN_DIESEL("Standard Rail", 55.0, "train"),
    ELECTRIC_BUS("EV Transit Bus", 28.0, "bus"),
    BUS("Intercity Coach", 68.0, "bus"),
    ELECTRIC_CAR("EV Taxi", 45.0, "car"),
    CAR_PETROL("Petrol / Diesel Car", 145.0, "car"),
    FLIGHT("Commercial Flight", 210.0, "flight"),
    WALKING("Walking / Manual Mobility", 0.0, "walk"),
    CYCLING("Cycling", 0.0, "bike"),
    FERRY("Ferry / Water Transit", 80.0, "ferry")
}

data class CarbonEstimate(
    val transportMode: TransportMode,
    val distanceKm: Double,
    val estimatedKgCo2: Double,
    val methodology: String = "DEFRA 2024 Passenger Transport Emission Factors",
    val savingsVsBaselineKg: Double = 0.0
)

data class TripCarbonBreakdown(
    val totalEstimatedKgCo2: Double,
    val baselineFlightKgCo2: Double,
    val baselineCarKgCo2: Double,
    val co2SavedKg: Double,
    val segmentEstimates: List<CarbonEstimate>
)
