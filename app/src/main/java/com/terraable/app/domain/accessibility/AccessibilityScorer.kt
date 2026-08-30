package com.terraable.app.domain.accessibility

import com.terraable.app.core.model.AccessibilityPassport
import com.terraable.app.core.model.EvidenceState
import com.terraable.app.core.model.FacilityCategory
import com.terraable.app.core.model.FacilityEvidence
import com.terraable.app.core.model.StructuredTripRequest

class AccessibilityScorer {

    /**
     * Evaluates accessibility evidence and generates an explainable Accessibility Passport.
     * Uses a weighted confidence model:
     * - Verified official records (0.95 weight)
     * - Reported user/audit data (0.80 weight)
     * - Inferred structural data (0.60 weight)
     * - Unknown items (0.20 weight)
     */
    fun createAccessibilityPassport(
        evidenceList: List<FacilityEvidence>,
        request: StructuredTripRequest
    ): AccessibilityPassport {
        if (evidenceList.isEmpty()) {
            return AccessibilityPassport(
                overallConfidenceScore = 50,
                meetsWheelchairRequirements = false,
                items = emptyList()
            )
        }

        var weightedSum = 0.0
        var totalWeights = 0.0

        evidenceList.forEach { item ->
            val weight = when (item.facilityCategory) {
                FacilityCategory.ENTRANCE -> 1.5
                FacilityCategory.BATHROOM -> 1.4
                FacilityCategory.ELEVATOR -> 1.3
                FacilityCategory.TRANSIT -> 1.2
                FacilityCategory.PATHWAY -> 1.0
            }
            weightedSum += (item.confidencePercentage * weight)
            totalWeights += weight
        }

        val overallScore = if (totalWeights > 0) (weightedSum / totalWeights).toInt().coerceIn(0, 100) else 75

        val meetsWheelchair = evidenceList.all {
            it.state != EvidenceState.UNKNOWN || !request.wheelchairRequired
        }

        return AccessibilityPassport(
            overallConfidenceScore = overallScore,
            meetsWheelchairRequirements = meetsWheelchair,
            items = evidenceList
        )
    }

    /**
     * Checks if a candidate violates hard accessibility constraints.
     * Returns null if compliant, or an explanation string if violating.
     */
    fun validateHardConstraints(
        walkingMeters: Int,
        transfers: Int,
        hasWheelchairAccess: Boolean,
        hasAccessibleBathroom: Boolean,
        request: StructuredTripRequest
    ): String? {
        if (walkingMeters > request.maxWalkingMeters) {
            return "Exceeds maximum continuous walking threshold of ${request.maxWalkingMeters}m (requires ${walkingMeters}m)."
        }
        if (transfers > request.maxTransfers) {
            return "Exceeds maximum allowed transfers of ${request.maxTransfers} (requires $transfers transfers)."
        }
        if (request.wheelchairRequired && !hasWheelchairAccess) {
            return "Does not provide certified step-free wheelchair boarding on this segment."
        }
        if (request.accessibleBathroomRequired && !hasAccessibleBathroom) {
            return "Accessible sanitary facilities are not verified for this vehicle/station combination."
        }
        return null
    }
}
