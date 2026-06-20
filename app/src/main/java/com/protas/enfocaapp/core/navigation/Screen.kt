package com.protas.enfocaapp.core.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object OnboardingWelcome : Screen("onboarding_welcome")
    object Main : Screen("main")
    object Home : Screen("home")
    object Stats : Screen("stats")
    object AppBlock : Screen("app_block")
    object Settings : Screen("settings")
    object Intervention : Screen("intervention")
}
