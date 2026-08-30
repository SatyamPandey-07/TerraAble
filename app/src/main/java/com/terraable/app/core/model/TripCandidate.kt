package com.terraable.app.core.model

enum class ParetoTag(
    val title: String,
    val subtitle: String,
    val iconName: String
) {
    BEST_BALANCE("⚖ Best Balance", "Recommended for you • Optimal trade-off", "balance"),
    GREENEST("🌱 Greenest", "Lowest CO₂ impact", "eco"),
    MOST_ACCESSIBLE("♿ Most Accessible", "Maximum accessibility confidence", "accessible"),
    FASTEST("⚡ Fastest", "Minimum travel duration", "bolt")
}

data class ScoreBreakdown(
    val accessibilityScore: Int,   // 0-100%
    val sustainabilityScore: Int,  // 0-100%
    val comfortScore: Int,         // 0-100%
    val budgetScore: Int,          // 0-100%
    val reliabilityScore: Int      // 0-100%
)

data class TradeOffExplanation(
    val summary: String,
    val accessibilityRationale: String,
    val carbonRationale: String,
    val weatherSafetyRationale: String,
    val comparisonVsFastest: String,
    val whyNotGreenest: String? = null
)

data class TripCandidate(
    val id: String,
    val tag: ParetoTag,
    val isRecommended: Boolean,
    val totalCostInr: Double,
    val durationMinutes: Int,
    val carbonKg: Double,
    val transferCount: Int,
    val totalWalkingMeters: Int,
    val accessibilityConfidencePercentage: Int,
    val scoreBreakdown: ScoreBreakdown,
    val explanation: TradeOffExplanation,
    val passport: AccessibilityPassport,
    val segments: List<RouteSegment>,
    val weatherCondition: WeatherCondition,
    val isEligible: Boolean = true,
    val eligibilityViolationReason: String? = null
)

data class ParetoFrontier(
    val recommendedTrip: TripCandidate,
    val greenestTrip: TripCandidate,
    val mostAccessibleTrip: TripCandidate,
    val fastestTrip: TripCandidate,
    val rejectedCandidates: List<TripCandidate> = emptyList()
)
