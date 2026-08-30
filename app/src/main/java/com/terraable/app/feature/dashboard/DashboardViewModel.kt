package com.terraable.app.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terraable.app.core.model.Coordinates
import com.terraable.app.core.model.HourlyForecastPoint
import com.terraable.app.core.model.TravelerProfile
import com.terraable.app.core.model.WeatherCondition
import com.terraable.app.data.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val currentLocation: String = "Mumbai, India",
    val monthlyCo2SavedKg: Double = 42.8,
    val totalTripsCount: Int = 5,
    val travelerProfile: TravelerProfile = TravelerProfile(),
    val weatherCondition: WeatherCondition = WeatherCondition(
        temperatureCelsius = 28.0,
        weatherCode = 2,
        description = "Partly cloudy",
        humidityPercentage = 78,
        windSpeedKmh = 14.0,
        rainProbabilityPercentage = 40,
        precipitationMm = 0.2,
        weatherAlertText = "Moderate rain expected at 6:00 PM along coastal route. Alternative sheltered transfer reduces outdoor walking by 420m."
    ),
    val hourlyForecast: List<HourlyForecastPoint> = listOf(
        HourlyForecastPoint("12 PM", 12, 28.0, 15, 2),
        HourlyForecastPoint("2 PM", 14, 29.5, 35, 3),
        HourlyForecastPoint("4 PM", 16, 28.8, 65, 61),
        HourlyForecastPoint("6 PM", 18, 27.2, 80, 63),
        HourlyForecastPoint("8 PM", 20, 26.5, 40, 2)
    ),
    val isLoadingWeather: Boolean = false,
    val isFavorite: Boolean = true
)

class DashboardViewModel(
    private val repository: TripRepository = TripRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingWeather = true)
            try {
                val coords = Coordinates(18.9690, 72.8205) // Mumbai
                val (weather, hourly) = repository.fetchWeather(coords, "Mumbai")
                _uiState.value = _uiState.value.copy(
                    weatherCondition = weather,
                    hourlyForecast = hourly,
                    isLoadingWeather = false
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingWeather = false)
            }
        }
    }

    fun toggleFavorite() {
        _uiState.value = _uiState.value.copy(isFavorite = !_uiState.value.isFavorite)
    }
}
