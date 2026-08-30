package com.terraable.app.feature.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terraable.app.core.model.ParetoFrontier
import com.terraable.app.core.model.StructuredTripRequest
import com.terraable.app.data.repository.TripRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlannerUiState(
    val promptInput: String = "Plan a 4-day trip from Mumbai to Goa for my parents. One uses a wheelchair. Budget ₹30,000. Avoid more than 300m continuous walking. Prefer trains and minimize carbon.",
    val structuredRequest: StructuredTripRequest = StructuredTripRequest(),
    val isGenerating: Boolean = false,
    val generationStep: String = "",
    val generationProgress: Float = 0f,
    val generatedFrontier: ParetoFrontier? = null,
    val isFormExpanded: Boolean = true
)

class PlannerViewModel(
    private val repository: TripRepository = TripRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()

    init {
        // Parse the initial natural language prompt to sync the structured form
        parsePrompt(_uiState.value.promptInput)
    }

    fun onPromptChange(newText: String) {
        _uiState.value = _uiState.value.copy(promptInput = newText)
        parsePrompt(newText)
    }

    fun applyPromptChip(chipText: String) {
        val updated = when (chipText) {
            "♿ Wheelchair friendly" -> _uiState.value.promptInput + " Ensure full wheelchair accessibility with elevator and roll-in washroom."
            "🌱 Lowest carbon" -> _uiState.value.promptInput + " Prioritize lowest carbon electric train."
            "₹ Budget trip" -> _uiState.value.promptInput + " Keep cost under ₹25,000."
            "👴 Senior friendly" -> _uiState.value.promptInput + " Step-free senior friendly path."
            "🚶 Minimal walking" -> _uiState.value.promptInput + " Max continuous walking under 200m."
            else -> _uiState.value.promptInput
        }
        _uiState.value = _uiState.value.copy(promptInput = updated)
        parsePrompt(updated)
    }

    private fun parsePrompt(input: String) {
        viewModelScope.launch {
            val parsed = repository.parseNaturalLanguage(input)
            _uiState.value = _uiState.value.copy(structuredRequest = parsed)
        }
    }

    fun updateStructuredRequest(updated: StructuredTripRequest) {
        _uiState.value = _uiState.value.copy(structuredRequest = updated)
    }

    fun generateTrip(onComplete: (ParetoFrontier) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, generationProgress = 0.1f)

            val steps = listOf(
                "Understanding your mobility needs..." to 0.2f,
                "Retrieving TomTom route geometries..." to 0.4f,
                "Checking Open-Meteo precipitation forecasts..." to 0.6f,
                "Calculating carbon footprint model..." to 0.75f,
                "Evaluating accessibility passport evidence..." to 0.9f,
                "Solving Pareto frontier trade-offs..." to 1.0f
            )

            for ((stepText, progress) in steps) {
                _uiState.value = _uiState.value.copy(
                    generationStep = stepText,
                    generationProgress = progress
                )
                delay(380)
            }

            val frontier = repository.generateParetoFrontier(_uiState.value.structuredRequest)
            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                generatedFrontier = frontier
            )
            onComplete(frontier)
        }
    }
}
