package com.terraable.app.feature.safety

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class EmergencyCategory(val title: String, val icon: String, val emergencyNumber: String) {
    MEDICAL("Medical Assistance", "🚑", "108"),
    FIRE("Fire & Rescue", "🚒", "101"),
    ACCIDENT("Road Accident", "🚗", "112"),
    POLICE("Police / Violence", "👮", "100")
}

data class EmergencyContact(
    val name: String,
    val relation: String,
    val phoneNumber: String
)

data class SafetyUiState(
    val selectedCategory: EmergencyCategory = EmergencyCategory.MEDICAL,
    val currentLat: Double = 18.9690,
    val currentLng: Double = 72.8205,
    val locationAddress: String = "Dr. DN Road, Fort, Mumbai, Maharashtra 400001",
    val emergencyContacts: List<EmergencyContact> = listOf(
        EmergencyContact("Rahul Sharma (Son)", "Primary Contact", "+91 98765 43210"),
        EmergencyContact("Pooja Sharma (Daughter)", "Family", "+91 98123 45678"),
        EmergencyContact("IRCTC Accessible Helpline", "Transit Support", "139")
    ),
    val isSosTriggered: Boolean = false,
    val sosAlertMessage: String? = null
)

class SafetyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SafetyUiState())
    val uiState: StateFlow<SafetyUiState> = _uiState.asStateFlow()

    fun selectCategory(category: EmergencyCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun triggerSos() {
        val category = _uiState.value.selectedCategory
        val msg = "[DEMO MODE] Emergency alert broadcasted for ${category.title}. Real-time coordinates (${_uiState.value.currentLat}, ${_uiState.value.currentLng}) shared with emergency contacts."
        _uiState.value = _uiState.value.copy(
            isSosTriggered = true,
            sosAlertMessage = msg
        )
    }

    fun dismissSosAlert() {
        _uiState.value = _uiState.value.copy(
            isSosTriggered = false,
            sosAlertMessage = null
        )
    }
}
