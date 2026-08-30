package com.terraable.app.core.model

enum class EvidenceState(
    val symbol: String,
    val title: String,
    val description: String,
    val confidenceBase: Double
) {
    VERIFIED("✓", "Verified", "Reliable structured or official operator source", 0.95),
    REPORTED("◉", "Reported", "Supported by community reviews and field data", 0.80),
    INFERRED("◐", "Inferred", "Estimated via accessibility models and station profiles", 0.60),
    UNKNOWN("?", "Unknown", "No reliable evidence found; manual check required", 0.20)
}

enum class FacilityCategory(val title: String) {
    ENTRANCE("Entrance & Boarding"),
    BATHROOM("Restrooms & Sanitary"),
    ELEVATOR("Elevators & Ramps"),
    TRANSIT("Transit Vehicle Accessibility"),
    PATHWAY("Pathways & Transfer Walkway")
}

data class FacilityEvidence(
    val facilityCategory: FacilityCategory,
    val name: String,
    val state: EvidenceState,
    val details: String,
    val confidencePercentage: Int,
    val sourceName: String,
    val lastVerified: String
)

data class AccessibilityPassport(
    val overallConfidenceScore: Int, // e.g. 91%
    val meetsWheelchairRequirements: Boolean,
    val items: List<FacilityEvidence>,
    val disclaimer: String = "Accessibility information is evidence-based and may be incomplete. Always confirm critical requirements directly with the provider."
)
