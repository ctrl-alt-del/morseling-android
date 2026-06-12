package com.morseling.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.morseling.ui.HistoryScreen
import com.morseling.ui.LicensesScreen
import com.morseling.ui.MainScreen
import com.morseling.ui.SettingsScreen
import com.morseling.viewmodel.HistoryViewModel
import com.morseling.viewmodel.MorseConverterViewModel
import com.morseling.viewmodel.SettingsViewModel

@Composable
fun MorselingNavGraph() {
    val navController = rememberNavController()
    val mainViewModel: MorseConverterViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                viewModel = mainViewModel,
                onOpenHistory = { navController.navigate("history") },
                onOpenSettings = { navController.navigate("settings") },
            )
        }
        composable("history") {
            val historyViewModel: HistoryViewModel = hiltViewModel()
            HistoryScreen(
                viewModel = historyViewModel,
                onBack = { navController.popBackStack() },
                onItemClick = { textInput, morseOutput ->
                    mainViewModel.loadFromHistory(textInput, morseOutput)
                    navController.popBackStack()
                },
            )
        }
        composable("settings") {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() },
                onOpenLicenses = { navController.navigate("licenses") },
            )
        }
        composable("licenses") {
            LicensesScreen(onBack = { navController.popBackStack() })
        }
    }
}
