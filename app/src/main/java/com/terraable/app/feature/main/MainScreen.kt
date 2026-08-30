package com.terraable.app.feature.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.terraable.app.core.model.ParetoFrontier
import com.terraable.app.feature.dashboard.DashboardScreen
import com.terraable.app.feature.dashboard.DashboardViewModel
import com.terraable.app.feature.explore.ExploreScreen
import com.terraable.app.feature.planner.PlannerScreen
import com.terraable.app.feature.planner.PlannerViewModel
import com.terraable.app.feature.results.ResultsScreen
import com.terraable.app.feature.results.ResultsViewModel
import com.terraable.app.feature.safety.SafetyScreen
import com.terraable.app.feature.safety.SafetyViewModel
import com.terraable.app.feature.settings.SettingsScreen
import com.terraable.app.feature.settings.SettingsViewModel
import com.terraable.app.ui.components.NavSection
import com.terraable.app.ui.components.TerraBottomNavBar
import com.terraable.app.ui.theme.BgDark

@Composable
fun MainScreen(
    dashboardViewModel: DashboardViewModel = viewModel(),
    plannerViewModel: PlannerViewModel = viewModel(),
    resultsViewModel: ResultsViewModel = viewModel(),
    safetyViewModel: SafetyViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    var currentSection by remember { mutableStateOf(NavSection.DASHBOARD) }
    var viewingResults by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgDark,
        bottomBar = {
            TerraBottomNavBar(
                currentSection = currentSection,
                onSectionSelected = { section ->
                    currentSection = section
                    viewingResults = false
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark)
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            if (viewingResults) {
                ResultsScreen(
                    viewModel = resultsViewModel,
                    onBackToPlanner = {
                        viewingResults = false
                        currentSection = NavSection.PLAN
                    }
                )
            } else {
                Crossfade(targetState = currentSection, label = "tabCrossfade") { section ->
                    when (section) {
                        NavSection.DASHBOARD -> DashboardScreen(
                            viewModel = dashboardViewModel,
                            onNavigateToPlan = { currentSection = NavSection.PLAN },
                            onNavigateToSafety = { currentSection = NavSection.SAFETY }
                        )

                        NavSection.EXPLORE -> ExploreScreen(
                            onSelectDestination = { dest ->
                                plannerViewModel.onPromptChange("Plan a trip to $dest for parents with wheelchair accessibility and low carbon emissions.")
                                currentSection = NavSection.PLAN
                            }
                        )

                        NavSection.PLAN -> PlannerScreen(
                            viewModel = plannerViewModel,
                            onTripGenerated = { frontier: ParetoFrontier ->
                                resultsViewModel.setFrontier(frontier)
                                viewingResults = true
                            }
                        )

                        NavSection.SAFETY -> SafetyScreen(
                            viewModel = safetyViewModel
                        )

                        NavSection.SETTINGS -> SettingsScreen(
                            viewModel = settingsViewModel
                        )
                    }
                }
            }
        }
    }
}
