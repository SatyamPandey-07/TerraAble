package com.terraable.app.core.model

data class TravelerProfile(
    val mobilityType: MobilityType = MobilityType.WHEELCHAIR_MANUAL,
    val maxContinuousWalkingMeters: Int = 300,
    val maxTransfers: Int = 2,
    val requiresAccessibleBathroom: Boolean = true,
    val requiresStepFreeAccess: Boolean = true,
    val requiresElevator: Boolean = true,
    val requiresAccessibleTransit: Boolean = true,
    val hasHearingAssistance: Boolean = false,
    val hasVisualAssistance: Boolean = false,
    val sensoryFriendly: Boolean = false
)

enum class MobilityType(val displayName: String) {
    WHEELCHAIR_MANUAL("Manual Wheelchair"),
    WHEELCHAIR_POWERED("Powered Wheelchair"),
    WALKER_CANE("Walker / Cane"),
    LIMITED_STAMINA("Limited Stamina"),
    FULLY_MOBILE("Fully Mobile")
}

data class StructuredTripRequest(
    val origin: String = "Mumbai",
    val destination: String = "Goa",
    val durationDays: Int = 4,
    val budgetInr: Double = 30000.0,
    val travelerCount: Int = 2,
    val wheelchairRequired: Boolean = true,
    val accessibleBathroomRequired: Boolean = true,
    val stepFreeRequired: Boolean = true,
    val elevatorRequired: Boolean = true,
    val accessibleTransitRequired: Boolean = true,
    val hearingAssistance: Boolean = false,
    val visualAssistance: Boolean = false,
    val maxWalkingMeters: Int = 300,
    val maxTransfers: Int = 2,
    val carbonPriority: Float = 0.85f,
    val publicTransportPreference: Float = 0.90f,
    val comfortPriority: Float = 0.75f,
    val naturalLanguageQuery: String = ""
)
