package com.terraable.app.core.model

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

data class LocationSuggestion(
    val id: String,
    val name: String,
    val formattedAddress: String,
    val coordinates: Coordinates,
    val country: String = "India"
)

data class RouteStop(
    val stopName: String,
    val arrivalTime: String,
    val departureTime: String,
    val isTransfer: Boolean,
    val transferWalkingMeters: Int = 0,
    val isShelteredTransfer: Boolean = true,
    val accessibleRestroomsAvailable: Boolean = true,
    val coordinates: Coordinates
)

data class RouteSegment(
    val segmentIndex: Int,
    val fromPlace: String,
    val toPlace: String,
    val mode: TransportMode,
    val distanceKm: Double,
    val durationMinutes: Int,
    val departureTime: String,
    val arrivalTime: String,
    val instructions: String,
    val walkingMeters: Int,
    val weatherAlert: String? = null,
    val isWheelchairAccessible: Boolean = true,
    val estimatedCarbonKg: Double = 0.0
)

data class WeatherCondition(
    val temperatureCelsius: Double,
    val weatherCode: Int,
    val description: String,
    val humidityPercentage: Int,
    val windSpeedKmh: Double,
    val rainProbabilityPercentage: Int,
    val precipitationMm: Double,
    val weatherAlertText: String? = null
)

data class HourlyForecastPoint(
    val timeLabel: String,
    val hourInt: Int,
    val temperature: Double,
    val rainProbability: Int,
    val weatherCode: Int
)
