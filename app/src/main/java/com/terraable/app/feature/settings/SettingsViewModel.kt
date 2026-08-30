package com.terraable.app.feature.settings

import androidx.lifecycle.ViewModel
import com.terraable.app.core.model.MobilityType
import com.terraable.app.core.model.TravelerProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val isDarkMode: Boolean = true,
    val homeLocation: String = "Mumbai, India",
    val isMetricUnit: Boolean = true,
    val profile: TravelerProfile = TravelerProfile(),
    val exportStatus: String? = null
)

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateMobilityType(type: MobilityType) {
        _uiState.value = _uiState.value.copy(
            profile = _uiState.value.profile.copy(mobilityType = type)
        )
    }

    fun updateWalkingTolerance(meters: Int) {
        _uiState.value = _uiState.value.copy(
            profile = _uiState.value.profile.copy(maxContinuousWalkingMeters = meters)
        )
    }

    fun toggleAccessibleBathroom(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            profile = _uiState.value.profile.copy(requiresAccessibleBathroom = enabled)
        )
    }

    fun toggleElevator(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            profile = _uiState.value.profile.copy(requiresElevator = enabled)
        )
    }

    fun toggleStepFree(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            profile = _uiState.value.profile.copy(requiresStepFreeAccess = enabled)
        )
    }

    fun exportProfile() {
        _uiState.value = _uiState.value.copy(exportStatus = "Exported 1 traveler profile & 5 itineraries to device storage.")
    }

    fun clearExportStatus() {
        _uiState.value = _uiState.value.copy(exportStatus = null)
    }
}
