package com.terraable.app.domain.parser

import com.terraable.app.core.model.StructuredTripRequest
import java.util.Locale
import java.util.regex.Pattern

interface PreferenceParser {
    suspend fun parseTripRequest(input: String): StructuredTripRequest
}

class DemoPreferenceParser : PreferenceParser {

    override suspend fun parseTripRequest(input: String): StructuredTripRequest {
        if (input.isBlank()) {
            return StructuredTripRequest()
        }

        val text = input.lowercase(Locale.ROOT)

        // 1. Origin & Destination parsing
        var origin = "Mumbai"
        var destination = "Goa"

        val fromToPattern = Pattern.compile("from\\s+([a-zA-Z\\s]+?)\\s+to\\s+([a-zA-Z\\s]+?)(?:\\.|\$|\\s+for|\\s+with|\\s+budget|\\s+in)")
        val matcher = fromToPattern.matcher(input)
        if (matcher.find()) {
            origin = matcher.group(1)?.trim()?.replaceFirstChar { it.uppercase() } ?: origin
            destination = matcher.group(2)?.trim()?.replaceFirstChar { it.uppercase() } ?: destination
        } else {
            if (text.contains("mumbai") && text.contains("goa")) {
                origin = "Mumbai"
                destination = "Goa"
            } else if (text.contains("delhi") && text.contains("jaipur")) {
                origin = "Delhi"
                destination = "Jaipur"
            } else if (text.contains("bangalore") && text.contains("mysore")) {
                origin = "Bangalore"
                destination = "Mysore"
            }
        }

        // 2. Duration parsing
        var durationDays = 4
        val durationPattern = Pattern.compile("(\\d+)[\\s-]*(?:day|days|night|nights)")
        val durationMatcher = durationPattern.matcher(text)
        if (durationMatcher.find()) {
            durationDays = durationMatcher.group(1)?.toIntOrNull() ?: 4
        }

        // 3. Budget parsing (₹30,000 / 30000 / rs 30000 / 30k)
        var budgetInr = 30000.0
        val budgetPattern = Pattern.compile("(?:₹|rs\\.?|inr|budget\\s*(?:of)?)\\s*(\\d+)(?:[kK]|(?:,\\d{3})+)?")
        val budgetMatcher = budgetPattern.matcher(text)
        if (budgetMatcher.find()) {
            val numStr = budgetMatcher.group(1) ?: "30000"
            var rawVal = numStr.toDoubleOrNull() ?: 30000.0
            if (text.contains(numStr + "k") || text.contains(numStr + "000")) {
                if (rawVal < 1000) rawVal *= 1000
            }
            budgetInr = rawVal
        } else if (text.contains("30,000") || text.contains("30000")) {
            budgetInr = 30000.0
        }

        // 4. Travelers count (parents = 2, family = 3-4, etc.)
        var travelerCount = 2
        if (text.contains("parents") || text.contains("couple") || text.contains("2 people") || text.contains("two people")) {
            travelerCount = 2
        } else if (text.contains("solo") || text.contains("myself") || text.contains("1 person")) {
            travelerCount = 1
        } else if (text.contains("family") || text.contains("4 people")) {
            travelerCount = 4
        }

        // 5. Wheelchair & Accessibility parsing
        val wheelchairRequired = text.contains("wheelchair") || text.contains("mobility scooter") || text.contains("walker")
        val stepFreeRequired = wheelchairRequired || text.contains("step free") || text.contains("step-free") || text.contains("no stairs")
        val elevatorRequired = stepFreeRequired || text.contains("elevator") || text.contains("lift")
        val accessibleBathroomRequired = wheelchairRequired || text.contains("bathroom") || text.contains("restroom") || text.contains("washroom")

        // 6. Max continuous walking parsing (e.g. 300m, 300 meters, minimal walking)
        var maxWalkingMeters = 300
        val walkingPattern = Pattern.compile("(?:avoid\\s+more\\s+than|max|maximum|under|less\\s+than|stay\\s+below|cannot\\s+walk\\s+more\\s+than)\\s*(\\d+)\\s*(?:m|meter|meters)?")
        val walkingMatcher = walkingPattern.matcher(text)
        if (walkingMatcher.find()) {
            maxWalkingMeters = walkingMatcher.group(1)?.toIntOrNull() ?: 300
        } else if (text.contains("minimal walking") || text.contains("low walking")) {
            maxWalkingMeters = 200
        }

        // 7. Max transfers parsing
        var maxTransfers = 2
        val transferPattern = Pattern.compile("(?:no\\s+more\\s+than|max|maximum|at\\s+most)\\s*(\\d+|two|three|one|zero)\\s*transfers?")
        val transferMatcher = transferPattern.matcher(text)
        if (transferMatcher.find()) {
            val transferStr = transferMatcher.group(1) ?: "2"
            maxTransfers = when (transferStr) {
                "zero", "0" -> 0
                "one", "1" -> 1
                "two", "2" -> 2
                "three", "3" -> 3
                else -> transferStr.toIntOrNull() ?: 2
            }
        } else if (text.contains("direct") || text.contains("no transfer")) {
            maxTransfers = 0
        } else if (text.contains("two transfers") || text.contains("2 transfers")) {
            maxTransfers = 2
        }

        // 8. Sustainability & Carbon Priority
        var carbonPriority = 0.85f
        if (text.contains("minimize carbon") || text.contains("low carbon") || text.contains("greenest") || text.contains("eco friendly") || text.contains("sustainable")) {
            carbonPriority = 0.92f
        }

        var publicTransportPreference = 0.85f
        if (text.contains("prefer trains") || text.contains("train") || text.contains("public transport") || text.contains("metro") || text.contains("bus")) {
            publicTransportPreference = 0.95f
        }

        return StructuredTripRequest(
            origin = origin,
            destination = destination,
            durationDays = durationDays,
            budgetInr = budgetInr,
            travelerCount = travelerCount,
            wheelchairRequired = wheelchairRequired,
            accessibleBathroomRequired = accessibleBathroomRequired,
            stepFreeRequired = stepFreeRequired,
            elevatorRequired = elevatorRequired,
            accessibleTransitRequired = wheelchairRequired,
            hearingAssistance = text.contains("hearing") || text.contains("deaf"),
            visualAssistance = text.contains("visual") || text.contains("blind") || text.contains("low vision"),
            maxWalkingMeters = maxWalkingMeters,
            maxTransfers = maxTransfers,
            carbonPriority = carbonPriority,
            publicTransportPreference = publicTransportPreference,
            comfortPriority = 0.80f,
            naturalLanguageQuery = input
        )
    }
}
