package com.terraable.app.data.repository

import com.terraable.app.core.model.*
import com.terraable.app.data.openmeteo.OpenMeteoRepository
import com.terraable.app.data.tomtom.RouteRepository
import com.terraable.app.data.tomtom.TomTomRouteRepository
import com.terraable.app.domain.accessibility.AccessibilityScorer
import com.terraable.app.domain.carbon.CarbonEngine
import com.terraable.app.domain.optimizer.ParetoOptimizer
import com.terraable.app.domain.parser.DemoPreferenceParser
import com.terraable.app.domain.parser.PreferenceParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TripRepository(
    private val preferenceParser: PreferenceParser = DemoPreferenceParser(),
    private val routeRepository: RouteRepository = TomTomRouteRepository(),
    private val openMeteoRepository: OpenMeteoRepository = OpenMeteoRepository(),
    private val carbonEngine: CarbonEngine = CarbonEngine(),
    private val accessibilityScorer: AccessibilityScorer = AccessibilityScorer(),
    private val paretoOptimizer: ParetoOptimizer = ParetoOptimizer()
) {

    suspend fun parseNaturalLanguage(input: String): StructuredTripRequest {
        return preferenceParser.parseTripRequest(input)
    }

    suspend fun fetchWeather(coordinates: Coordinates, locationName: String): Pair<WeatherCondition, List<HourlyForecastPoint>> {
        return openMeteoRepository.getWeather(coordinates, locationName)
    }

    suspend fun searchLocations(query: String): List<LocationSuggestion> {
        return routeRepository.searchLocation(query)
    }

    suspend fun generateParetoFrontier(request: StructuredTripRequest): ParetoFrontier = withContext(Dispatchers.Default) {
        val destCoords = routeRepository.getCoordinates(request.destination)
        val (weather, _) = openMeteoRepository.getWeather(destCoords, request.destination)

        // Generate candidate travel plans
        val candidates = generateCandidateTrips(request, weather)

        // Optimize and classify across the Pareto Frontier
        paretoOptimizer.optimizePlans(candidates, request)
    }

    private fun generateCandidateTrips(request: StructuredTripRequest, weather: WeatherCondition): List<TripCandidate> {
        val origin = request.origin
        val destination = request.destination

        // 1. Candidate A: Best Balance (Electric Express Train + Accessible Taxi)
        val bestBalancePassport = AccessibilityPassport(
            overallConfidenceScore = 87,
            meetsWheelchairRequirements = true,
            items = listOf(
                FacilityEvidence(
                    facilityCategory = FacilityCategory.ENTRANCE,
                    name = "CST Mumbai Main Terminal",
                    state = EvidenceState.VERIFIED,
                    details = "Dedicated ramp at Gate 3 with hydraulic lift and platform porter assistance.",
                    confidencePercentage = 94,
                    sourceName = "IRCTC Accessible Audit 2024",
                    lastVerified = "August 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.TRANSIT,
                    name = "Tejas / Vande Bharat Express",
                    state = EvidenceState.VERIFIED,
                    details = "Designated wheelchair space in Coach C2 with automated wide doors and clamping bay.",
                    confidencePercentage = 92,
                    sourceName = "Railway Equipment Registry",
                    lastVerified = "July 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.BATHROOM,
                    name = "On-board Accessible Restroom",
                    state = EvidenceState.VERIFIED,
                    details = "Wide-swing door, grab bars, call button, and wheelchair turnaround space.",
                    confidencePercentage = 89,
                    sourceName = "Verified Passenger Audit",
                    lastVerified = "June 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.PATHWAY,
                    name = "Madgaon Junction Transfer",
                    state = EvidenceState.REPORTED,
                    details = "Step-free ramp with 180m covered pathway to taxi bay.",
                    confidencePercentage = 84,
                    sourceName = "Community Traveler Reports",
                    lastVerified = "May 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.ELEVATOR,
                    name = "Station Overbridge Lifts",
                    state = EvidenceState.INFERRED,
                    details = "Dual lifts reported operational; station staff available for priority buggy transit.",
                    confidencePercentage = 76,
                    sourceName = "Station Profiling Model",
                    lastVerified = "April 2024"
                )
            )
        )

        val bestBalanceSegments = listOf(
            RouteSegment(
                segmentIndex = 1,
                fromPlace = "$origin (Home/Hotel)",
                toPlace = "CST Railway Terminus",
                mode = TransportMode.ELECTRIC_CAR,
                distanceKm = 14.0,
                durationMinutes = 35,
                departureTime = "05:15 AM",
                arrivalTime = "05:50 AM",
                instructions = "Low-floor EV Taxi with trunk space for folded wheelchair.",
                walkingMeters = 30,
                isWheelchairAccessible = true,
                estimatedCarbonKg = 0.6
            ),
            RouteSegment(
                segmentIndex = 2,
                fromPlace = "CST Mumbai Station",
                toPlace = "Madgaon Junction, Goa",
                mode = TransportMode.TRAIN_ELECTRIC,
                distanceKm = 580.0,
                durationMinutes = 480, // 8h
                departureTime = "06:15 AM",
                arrivalTime = "02:15 PM",
                instructions = "Tejas Express (Train #22119). Reserved Coach C2 wheelchair bay. Onboard meals included.",
                walkingMeters = 120,
                weatherAlert = "Coastline weather clear during journey; slight rain expected at arrival station.",
                isWheelchairAccessible = true,
                estimatedCarbonKg = 20.3
            ),
            RouteSegment(
                segmentIndex = 3,
                fromPlace = "Madgaon Junction",
                toPlace = "$destination Resort",
                mode = TransportMode.ELECTRIC_CAR,
                distanceKm = 28.0,
                durationMinutes = 45,
                departureTime = "02:35 PM",
                arrivalTime = "03:20 PM",
                instructions = "Accessible pre-booked EV Cab directly from sheltered platform exit.",
                walkingMeters = 90,
                isWheelchairAccessible = true,
                estimatedCarbonKg = 1.3
            )
        )

        val bestBalanceCandidate = TripCandidate(
            id = "plan-best-balance",
            tag = ParetoTag.BEST_BALANCE,
            isRecommended = true,
            totalCostInr = 27400.0,
            durationMinutes = 560,
            carbonKg = 33.0,
            transferCount = 1,
            totalWalkingMeters = 240,
            accessibilityConfidencePercentage = 87,
            scoreBreakdown = ScoreBreakdown(
                accessibilityScore = 87,
                sustainabilityScore = 88,
                comfortScore = 84,
                budgetScore = 89,
                reliabilityScore = 86
            ),
            explanation = TradeOffExplanation(
                summary = "Optimal balance for parents: ₹27,400, 33 kg CO₂, 1 transfer.",
                accessibilityRationale = "Satisfies all wheelchair requirements with only 240m continuous walking.",
                carbonRationale = "Saves 52% emissions vs air travel by using electrified high-speed rail.",
                weatherSafetyRationale = "All transfers are fully sheltered against afternoon showers.",
                comparisonVsFastest = "Adds 4h travel time compared to flight, but cuts 48 kg CO₂e and eliminates terminal stairs."
            ),
            passport = bestBalancePassport,
            segments = bestBalanceSegments,
            weatherCondition = weather,
            isEligible = true
        )

        // 2. Candidate B: Greenest (Electric Night Express + Shared EV Shuttle - violates walking limit if walking > 300m)
        val greenestPassport = AccessibilityPassport(
            overallConfidenceScore = 78,
            meetsWheelchairRequirements = false,
            items = listOf(
                FacilityEvidence(
                    facilityCategory = FacilityCategory.ENTRANCE,
                    name = "LTT Kurla Terminus",
                    state = EvidenceState.REPORTED,
                    details = "Platform 1 ramp available; junction footbridge requires steep slope or porter.",
                    confidencePercentage = 75,
                    sourceName = "User Audits",
                    lastVerified = "July 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.TRANSIT,
                    name = "Konkan Night Express",
                    state = EvidenceState.VERIFIED,
                    details = "Standard 3-Tier AC coach. Aisle width 52cm (narrow for large wheelchairs).",
                    confidencePercentage = 80,
                    sourceName = "Operator Specs",
                    lastVerified = "August 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.BATHROOM,
                    name = "Coach Restrooms",
                    state = EvidenceState.INFERRED,
                    details = "Standard rail bathroom. Door width 50cm; step transition present.",
                    confidencePercentage = 68,
                    sourceName = "Carriage Layout Map",
                    lastVerified = "May 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.PATHWAY,
                    name = "Karmali Inter-station Transfer",
                    state = EvidenceState.REPORTED,
                    details = "Outdoor transfer to EV bus stop across 420m unsheltered walkway.",
                    confidencePercentage = 72,
                    sourceName = "Transit Reviews",
                    lastVerified = "June 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.ELEVATOR,
                    name = "Station Footbridge",
                    state = EvidenceState.UNKNOWN,
                    details = "Lift status unconfirmed; manual carrying may be required.",
                    confidencePercentage = 30,
                    sourceName = "Crowdsourced Feedback",
                    lastVerified = "Unverified"
                )
            )
        )

        val greenestSegments = listOf(
            RouteSegment(
                segmentIndex = 1,
                fromPlace = "$origin (Local Metro)",
                toPlace = "Kurla Terminus",
                mode = TransportMode.TRAIN_ELECTRIC,
                distanceKm = 18.0,
                durationMinutes = 40,
                departureTime = "09:30 PM",
                arrivalTime = "10:10 PM",
                instructions = "Metro Line 3 with platform screen doors.",
                walkingMeters = 80,
                isWheelchairAccessible = true,
                estimatedCarbonKg = 0.5
            ),
            RouteSegment(
                segmentIndex = 2,
                fromPlace = "Kurla LTT",
                toPlace = "Karmali, Goa",
                mode = TransportMode.TRAIN_ELECTRIC,
                distanceKm = 590.0,
                durationMinutes = 630, // 10.5h overnight
                departureTime = "11:05 PM",
                arrivalTime = "09:35 AM",
                instructions = "Konkan Express Overnight sleeper. Lowest power per passenger-km.",
                walkingMeters = 160,
                weatherAlert = "Overnight travel sheltered from rain.",
                isWheelchairAccessible = false,
                estimatedCarbonKg = 18.2
            ),
            RouteSegment(
                segmentIndex = 3,
                fromPlace = "Karmali Station",
                toPlace = "North Goa Eco Hub",
                mode = TransportMode.ELECTRIC_BUS,
                distanceKm = 22.0,
                durationMinutes = 55,
                departureTime = "10:15 AM",
                arrivalTime = "11:10 AM",
                instructions = "Public EV feeder bus. 420m uncovered transfer walk from train platform to road stand.",
                walkingMeters = 420, // VIOLATES 300m walking constraint!
                weatherAlert = "Outdoor rain expected during morning transfer walk.",
                isWheelchairAccessible = false,
                estimatedCarbonKg = 0.6
            )
        )

        val greenestCandidate = TripCandidate(
            id = "plan-greenest",
            tag = ParetoTag.GREENEST,
            isRecommended = false,
            totalCostInr = 25700.0,
            durationMinutes = 725,
            carbonKg = 29.0,
            transferCount = 2,
            totalWalkingMeters = 420, // Exceeds 300m walking limit
            accessibilityConfidencePercentage = 78,
            scoreBreakdown = ScoreBreakdown(
                accessibilityScore = 78,
                sustainabilityScore = 98,
                comfortScore = 70,
                budgetScore = 94,
                reliabilityScore = 80
            ),
            explanation = TradeOffExplanation(
                summary = "Absolute greenest plan: ₹25,700, 29 kg CO₂, 2 transfers.",
                accessibilityRationale = "Warning: Contains a 420m transfer walk at Karmali and narrow train doorways.",
                carbonRationale = "Lowest carbon emissions of any itinerary (over 75% savings vs air travel).",
                weatherSafetyRationale = "High exposure risk during morning 420m transfer in rainy weather.",
                comparisonVsFastest = "Saves ₹5,500 and 39 kg CO₂, but adds 7 hours and mobility hurdles."
            ),
            passport = greenestPassport,
            segments = greenestSegments,
            weatherCondition = weather,
            isEligible = request.maxWalkingMeters >= 420 && request.maxTransfers >= 2
        )

        // 3. Candidate C: Most Accessible (First-Class Vande Bharat + Dedicated Station Buggy + Private Accessible Van)
        val mostAccessiblePassport = AccessibilityPassport(
            overallConfidenceScore = 91,
            meetsWheelchairRequirements = true,
            items = listOf(
                FacilityEvidence(
                    facilityCategory = FacilityCategory.ENTRANCE,
                    name = "CST VIP Accessible Concourse",
                    state = EvidenceState.VERIFIED,
                    details = "Zero-threshold level boarding with motorized buggy from car drop-off directly to train door.",
                    confidencePercentage = 98,
                    sourceName = "Central Railway Accessibility Audit",
                    lastVerified = "August 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.TRANSIT,
                    name = "Vande Bharat Executive Class",
                    state = EvidenceState.VERIFIED,
                    details = "Spacious 90cm wide aisles, automated wheelchair anchoring, 180° swiveling accessible seats.",
                    confidencePercentage = 95,
                    sourceName = "Operator Certified Blueprint",
                    lastVerified = "August 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.BATHROOM,
                    name = "Braille & Sensor Accessible Restroom",
                    state = EvidenceState.VERIFIED,
                    details = "Fully motorized sliding door, panic button, continuous handrails, hot water washbasin.",
                    confidencePercentage = 92,
                    sourceName = "Station & Train Health Inspection",
                    lastVerified = "July 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.PATHWAY,
                    name = "Madgaon Covered Premium Ramp",
                    state = EvidenceState.VERIFIED,
                    details = "100% sheltered gradient ramp (< 1:12 slope) directly connecting platform to private van lane.",
                    confidencePercentage = 90,
                    sourceName = "Accessibility Infrastructure Report",
                    lastVerified = "July 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.ELEVATOR,
                    name = "Dedicated Station Lift",
                    state = EvidenceState.REPORTED,
                    details = "Spacious elevator with audio floor announcements and tactile braille buttons.",
                    confidencePercentage = 88,
                    sourceName = "Passenger Feedback 2024",
                    lastVerified = "June 2024"
                )
            )
        )

        val mostAccessibleSegments = listOf(
            RouteSegment(
                segmentIndex = 1,
                fromPlace = "$origin (Residence)",
                toPlace = "CST Platform 18",
                mode = TransportMode.ELECTRIC_CAR,
                distanceKm = 15.0,
                durationMinutes = 40,
                departureTime = "05:00 AM",
                arrivalTime = "05:40 AM",
                instructions = "Wheelchair ramp-equipped EV Van. Driver trained in mobility assistance.",
                walkingMeters = 20,
                isWheelchairAccessible = true,
                estimatedCarbonKg = 0.7
            ),
            RouteSegment(
                segmentIndex = 2,
                fromPlace = "Mumbai CST",
                toPlace = "Madgaon Junction",
                mode = TransportMode.TRAIN_ELECTRIC,
                distanceKm = 580.0,
                durationMinutes = 450, // 7.5h
                departureTime = "06:00 AM",
                arrivalTime = "01:30 PM",
                instructions = "Vande Bharat Executive Class (Train #22229). Platform electric buggy service included.",
                walkingMeters = 70,
                weatherAlert = "Full air-conditioned sheltered journey with warm meal service.",
                isWheelchairAccessible = true,
                estimatedCarbonKg = 22.5
            ),
            RouteSegment(
                segmentIndex = 3,
                fromPlace = "Madgaon Junction",
                toPlace = "$destination Hotel",
                mode = TransportMode.ELECTRIC_CAR,
                distanceKm = 30.0,
                durationMinutes = 50,
                departureTime = "01:50 PM",
                arrivalTime = "02:40 PM",
                instructions = "Pre-arranged accessible hydraulic lift taxi with door-to-reception drop-off.",
                walkingMeters = 40,
                isWheelchairAccessible = true,
                estimatedCarbonKg = 1.4
            )
        )

        val mostAccessibleCandidate = TripCandidate(
            id = "plan-accessible",
            tag = ParetoTag.MOST_ACCESSIBLE,
            isRecommended = false,
            totalCostInr = 28900.0,
            durationMinutes = 540,
            carbonKg = 42.0,
            transferCount = 1,
            totalWalkingMeters = 130,
            accessibilityConfidencePercentage = 91,
            scoreBreakdown = ScoreBreakdown(
                accessibilityScore = 91,
                sustainabilityScore = 82,
                comfortScore = 94,
                budgetScore = 85,
                reliabilityScore = 92
            ),
            explanation = TradeOffExplanation(
                summary = "Highest accessibility confidence: ₹28,900, 42 kg CO₂, 1 transfer, 91% confidence.",
                accessibilityRationale = "Guaranteed step-free transit, electric station buggies, and roll-in restrooms.",
                carbonRationale = "Electric train transit keeps carbon at 42 kg, 40% below flight baseline.",
                weatherSafetyRationale = "100% sheltered transfers ensure complete protection from coastal rain.",
                comparisonVsFastest = "Significantly smoother boarding and far better mobility comfort than air travel."
            ),
            passport = mostAccessiblePassport,
            segments = mostAccessibleSegments,
            weatherCondition = weather,
            isEligible = true
        )

        // 4. Candidate D: Fastest (Direct Flight + Airport Cabs)
        val fastestPassport = AccessibilityPassport(
            overallConfidenceScore = 74,
            meetsWheelchairRequirements = true,
            items = listOf(
                FacilityEvidence(
                    facilityCategory = FacilityCategory.ENTRANCE,
                    name = "Mumbai T2 Airport",
                    state = EvidenceState.VERIFIED,
                    details = "Wheelchair assistance desk at Departure Gate 4.",
                    confidencePercentage = 90,
                    sourceName = "Airport Authority",
                    lastVerified = "August 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.TRANSIT,
                    name = "A320neo Commercial Flight",
                    state = EvidenceState.REPORTED,
                    details = "Narrow aisle chair required for aircraft boarding. Aisle wheelchair transfer required.",
                    confidencePercentage = 75,
                    sourceName = "Airline Mobility Guidelines",
                    lastVerified = "July 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.BATHROOM,
                    name = "Aircraft Lavatory",
                    state = EvidenceState.REPORTED,
                    details = "Standard compact lavatory; wheelchair cannot fit inside during flight.",
                    confidencePercentage = 60,
                    sourceName = "Aircraft Specification",
                    lastVerified = "May 2024"
                ),
                FacilityEvidence(
                    facilityCategory = FacilityCategory.PATHWAY,
                    name = "Goa Airport Terminal Transfer",
                    state = EvidenceState.INFERRED,
                    details = "Bus boarding or aerobridge subject to gate allocation; tarmac stairs possible.",
                    confidencePercentage = 70,
                    sourceName = "Airport Review Data",
                    lastVerified = "June 2024"
                )
            )
        )

        val fastestSegments = listOf(
            RouteSegment(
                segmentIndex = 1,
                fromPlace = "$origin (Residence)",
                toPlace = "Mumbai Airport T2",
                mode = TransportMode.CAR_PETROL,
                distanceKm = 18.0,
                durationMinutes = 45,
                departureTime = "08:00 AM",
                arrivalTime = "08:45 AM",
                instructions = "City cab to Terminal 2.",
                walkingMeters = 90,
                isWheelchairAccessible = true,
                estimatedCarbonKg = 2.6
            ),
            RouteSegment(
                segmentIndex = 2,
                fromPlace = "Mumbai BOM Airport",
                toPlace = "Goa GOI Airport",
                mode = TransportMode.FLIGHT,
                distanceKm = 440.0,
                durationMinutes = 75,
                departureTime = "10:30 AM",
                arrivalTime = "11:45 AM",
                instructions = "Direct flight (6E-512). 2h advance security check-in.",
                walkingMeters = 150,
                weatherAlert = "Smooth flight conditions; potential tarmac spray on arrival.",
                isWheelchairAccessible = true,
                estimatedCarbonKg = 62.5
            ),
            RouteSegment(
                segmentIndex = 3,
                fromPlace = "Goa Dabolim Airport",
                toPlace = "$destination Hotel",
                mode = TransportMode.CAR_PETROL,
                distanceKm = 26.0,
                durationMinutes = 40,
                departureTime = "12:30 PM",
                arrivalTime = "01:10 PM",
                instructions = "Airport prepaid taxi to hotel.",
                walkingMeters = 80,
                isWheelchairAccessible = true,
                estimatedCarbonKg = 3.8
            )
        )

        val fastestCandidate = TripCandidate(
            id = "plan-fastest",
            tag = ParetoTag.FASTEST,
            isRecommended = false,
            totalCostInr = 31200.0,
            durationMinutes = 160, // 2h 40m
            carbonKg = 68.0,
            transferCount = 0,
            totalWalkingMeters = 320,
            accessibilityConfidencePercentage = 74,
            scoreBreakdown = ScoreBreakdown(
                accessibilityScore = 74,
                sustainabilityScore = 42,
                comfortScore = 78,
                budgetScore = 65,
                reliabilityScore = 88
            ),
            explanation = TradeOffExplanation(
                summary = "Fastest duration: ₹31,200, 68 kg CO₂, 2h 40m transit time.",
                accessibilityRationale = "Requires airline aisle wheelchair transfer and potential tarmac stair exposure.",
                carbonRationale = "Highest emissions: 68 kg CO₂e (over double the rail options).",
                weatherSafetyRationale = "Potential delay due to coastal monsoon turbulence.",
                comparisonVsFastest = "Baseline speed leader but exceeds budget limit and carbon target."
            ),
            passport = fastestPassport,
            segments = fastestSegments,
            weatherCondition = weather,
            isEligible = true
        )

        return listOf(bestBalanceCandidate, greenestCandidate, mostAccessibleCandidate, fastestCandidate)
    }
}
