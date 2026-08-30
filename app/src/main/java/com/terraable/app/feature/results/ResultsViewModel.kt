package com.terraable.app.feature.results

import androidx.lifecycle.ViewModel
import com.terraable.app.core.model.ParetoFrontier
import com.terraable.app.core.model.ParetoTag
import com.terraable.app.core.model.TripCandidate
import com.terraable.app.domain.simulator.SimulationResult
import com.terraable.app.domain.simulator.SimulationScenario
import com.terraable.app.domain.simulator.WhatIfSimulator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ResultsUiState(
    val frontier: ParetoFrontier? = null,
    val selectedTag: ParetoTag = ParetoTag.BEST_BALANCE,
    val activeSimulation: SimulationResult? = null
)

class ResultsViewModel(
    private val simulator: WhatIfSimulator = WhatIfSimulator()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    fun setFrontier(frontier: ParetoFrontier) {
        _uiState.value = _uiState.value.copy(
            frontier = frontier,
            selectedTag = ParetoTag.BEST_BALANCE,
            activeSimulation = null
        )
    }

    fun selectTag(tag: ParetoTag) {
        _uiState.value = _uiState.value.copy(
            selectedTag = tag,
            activeSimulation = null
        )
    }

    fun getCurrentSelectedTrip(): TripCandidate? {
        val f = _uiState.value.frontier ?: return null
        return when (_uiState.value.selectedTag) {
            ParetoTag.BEST_BALANCE -> f.recommendedTrip
            ParetoTag.GREENEST -> f.greenestTrip
            ParetoTag.MOST_ACCESSIBLE -> f.mostAccessibleTrip
            ParetoTag.FASTEST -> f.fastestTrip
        }
    }

    fun runSimulation(scenario: SimulationScenario) {
        val trip = getCurrentSelectedTrip() ?: return
        val result = simulator.simulate(trip, scenario)
        _uiState.value = _uiState.value.copy(activeSimulation = result)
    }

    fun clearSimulation() {
        _uiState.value = _uiState.value.copy(activeSimulation = null)
    }
}
