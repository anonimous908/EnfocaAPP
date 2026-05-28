package com.protas.enfocaapp.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.protas.enfocaapp.ui.screens.onboarding.OnboardingPagerScreen

@Composable
fun EnfocaNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.OnboardingWelcome.route
    ) {
        composable(Screen.OnboardingWelcome.route) {  //start Destinatation
            OnboardingPagerScreen(
                onFinishOnboarding = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.OnboardingWelcome.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Main.route) {
            com.protas.enfocaapp.ui.screens.main.MainScreen()
        }
    }
}
