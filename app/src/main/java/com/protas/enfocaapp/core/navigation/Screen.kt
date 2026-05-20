package com.protas.enfocaapp.core.navigation

sealed class Screen(val route: String) {
    object OnboardingWelcome : Screen("onboarding_welcome")
    object Dashboard : Screen("dashboard")
    object Limits : Screen("limits")
    object Focus : Screen("focus")
    object Intervention : Screen("intervention")
}
