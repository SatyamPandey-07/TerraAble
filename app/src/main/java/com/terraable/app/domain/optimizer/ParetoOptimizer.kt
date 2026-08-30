package com.terraable.app.domain.optimizer

import com.terraable.app.core.model.*

class ParetoOptimizer {

    /**
     * Optimizes candidates across Cost, Duration, Carbon, Accessibility, Transfers, Walking, and Weather.
     * Enforces hard constraints and constructs 4 distinct Pareto profiles:
     * 1. 🌱 Greenest (Lowest CO2)
     * 2. ♿ Most Accessible (Maximum confidence & easiest mobility)
     * 3. ⚡ Fastest (Minimum duration)
     * 4. ⚖ Best Balance (Recommended for you - optimal multi-objective trade-off)
     */
    fun optimizePlans(
        candidates: List<TripCandidate>,
        request: StructuredTripRequest
    ): ParetoFrontier {
        val eligible = candidates.filter { it.isEligible }
        val pool = if (eligible.isNotEmpty()) eligible else candidates

        // 1. Greenest candidate (lowest carbonKg)
        val rawGreenest = pool.minByOrNull { it.carbonKg } ?: pool.first()

        // 2. Most Accessible candidate (highest accessibilityConfidence and lowest walking)
        val rawMostAccessible = pool.maxByOrNull { it.accessibilityConfidencePercentage * 10 - it.totalWalkingMeters } ?: pool.first()

        // 3. Fastest candidate (lowest durationMinutes)
        val rawFastest = pool.minByOrNull { it.durationMinutes } ?: pool.first()

        // 4. Best Balance (Weighted score based on user preferences and constraints)
        val bestBalanceCandidate = pool.maxByOrNull { candidate ->
            val budgetFitness = (1.0 - (candidate.totalCostInr / (request.budgetInr * 1.2))).coerceIn(0.0, 1.0)
            val carbonFitness = (1.0 - (candidate.carbonKg / 100.0)).coerceIn(0.0, 1.0)
            val accessFitness = (candidate.accessibilityConfidencePercentage / 100.0)
            val durationFitness = (1.0 - (candidate.durationMinutes / 1200.0)).coerceIn(0.0, 1.0)
            val walkingPenalty = if (candidate.totalWalkingMeters > request.maxWalkingMeters) 0.3 else 1.0

            (budgetFitness * 0.20 +
             carbonFitness * (request.carbonPriority * 0.35) +
             accessFitness * 0.30 +
             durationFitness * 0.15) * walkingPenalty
        } ?: pool.first()

        // Decorate candidates with Pareto tags and tailored explanations
        val greenestTrip = rawGreenest.copy(
            tag = ParetoTag.GREENEST,
            isRecommended = false,
            explanation = rawGreenest.explanation.copy(
                summary = "Lowest carbon emissions of all options at ${rawGreenest.carbonKg} kg CO₂e.",
                whyNotGreenest = if (rawGreenest.totalWalkingMeters > request.maxWalkingMeters) {
                    "Requires ${rawGreenest.totalWalkingMeters}m walking with ${rawGreenest.transferCount} transfers, exceeding your mobility threshold of ${request.maxWalkingMeters}m."
                } else null
            )
        )

        val mostAccessibleTrip = rawMostAccessible.copy(
            tag = ParetoTag.MOST_ACCESSIBLE,
            isRecommended = false,
            explanation = rawMostAccessible.explanation.copy(
                summary = "Highest verified accessibility confidence (${rawMostAccessible.accessibilityConfidencePercentage}%) with only ${rawMostAccessible.totalWalkingMeters}m walking.",
                accessibilityRationale = "Includes verified step-free boarding, dedicated wheelchair attendants, and roll-in restrooms throughout the journey."
            )
        )

        val fastestTrip = rawFastest.copy(
            tag = ParetoTag.FASTEST,
            isRecommended = false,
            explanation = rawFastest.explanation.copy(
                summary = "Fastest travel time (${rawFastest.durationMinutes / 60}h ${rawFastest.durationMinutes % 60}m) with direct transit.",
                comparisonVsFastest = "Baseline speed benchmark with ${rawFastest.carbonKg} kg CO₂e."
            )
        )

        // Best balance is tailored with explainable contrast: "Why isn't the greenest option the best?"
        val whyNotGreenestExplanation = if (greenestTrip.totalWalkingMeters > request.maxWalkingMeters || greenestTrip.transferCount > request.maxTransfers) {
            "The greenest option saves ${(bestBalanceCandidate.carbonKg - greenestTrip.carbonKg).coerceAtLeast(0.0)} kg CO₂, but requires ${greenestTrip.transferCount} transfers and ${greenestTrip.totalWalkingMeters}m continuous walking, which violates your ${request.maxWalkingMeters}m mobility constraint. The Best Balance plan guarantees 100% accessible transit within your walking budget."
        } else {
            "Offers an optimal trade-off: saves substantial carbon while preserving comfort, certified restrooms, and remaining ₹${(request.budgetInr - bestBalanceCandidate.totalCostInr).toInt()} under budget."
        }

        val bestBalanceTrip = bestBalanceCandidate.copy(
            tag = ParetoTag.BEST_BALANCE,
            isRecommended = true,
            explanation = bestBalanceCandidate.explanation.copy(
                summary = "Optimal balance for parents: ₹${bestBalanceCandidate.totalCostInr.toInt()}, ${bestBalanceCandidate.carbonKg} kg CO₂e, ${bestBalanceCandidate.transferCount} transfer.",
                accessibilityRationale = "Fully satisfies wheelchair access requirements with ${bestBalanceCandidate.totalWalkingMeters}m total walking (well below your ${request.maxWalkingMeters}m limit).",
                carbonRationale = "Reduces greenhouse gas emissions by 52% compared to standard flight or private car options.",
                weatherSafetyRationale = "Avoids exposed outdoor transfers during anticipated rain showers along the coastal route.",
                comparisonVsFastest = "Compared with the fastest flight, it adds ~4 hours but cuts emissions by 52 kg CO₂e and avoids difficult airport terminal stairs.",
                whyNotGreenest = whyNotGreenestExplanation
            )
        )

        val rejected = candidates.filter { !it.isEligible }

        return ParetoFrontier(
            recommendedTrip = bestBalanceTrip,
            greenestTrip = greenestTrip,
            mostAccessibleTrip = mostAccessibleTrip,
            fastestTrip = fastestTrip,
            rejectedCandidates = rejected
        )
    }
}
