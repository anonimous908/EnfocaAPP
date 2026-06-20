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
import com.protas.enfocaapp.ui.screens.splash.SplashScreen
import com.protas.enfocaapp.ui.screens.intervention.FutureMessageInterventionScreen

@Composable
fun EnfocaNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.OnboardingWelcome.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
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
            com.protas.enfocaapp.ui.screens.main.MainScreen(
                onNavigateToIntervention = {
                    navController.navigate(Screen.Intervention.route)
                }
            )
        }
        composable(Screen.Intervention.route) {
            FutureMessageInterventionScreen(
                onFollowAdvice = { navController.popBackStack() },
                onIgnoreAndContinue = { navController.popBackStack() }
            )
        }
    }
}
