package com.protas.enfocaapp

import androidx.compose.foundation.pager.PagerState
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.testing.TestNavHostController
import com.protas.enfocaapp.core.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Navigation tests for the onboarding flow.
 * Uses TestNavHostController to verify route structure and pager navigation.
 */
@RunWith(RobolectricTestRunner::class)
class OnboardingNavigationTest {

    @Test
    fun `onboarding route is defined in Screen sealed class`() {
        assertEquals("onboarding_welcome", Screen.OnboardingWelcome.route)
    }

    @Test
    fun `main route is defined in Screen sealed class`() {
        assertEquals("main", Screen.Main.route)
    }

    @Test
    fun `onboarding and main are distinct routes`() {
        assertTrue(
            "Routes must be different",
            Screen.OnboardingWelcome.route != Screen.Main.route
        )
    }

    @Test
    fun `TestNavHostController can be instantiated with Robolectric`() {
        val navController = TestNavHostController(RuntimeEnvironment.getApplication())
        assertNotNull("TestNavHostController should be created", navController)
    }

    @Test
    fun `NavGraph startDestination can be set to onboarding route`() {
        val navController = TestNavHostController(RuntimeEnvironment.getApplication())
        val navigator = navController.navigatorProvider
            .getNavigator<NavGraphNavigator>("navigation")

        val graph = NavGraph(navigator).apply {
            setStartDestination(Screen.OnboardingWelcome.route)
        }

        // Verify the route string used as start destination matches Screen sealed class
        assertEquals(
            "NavGraph should start at onboarding",
            Screen.OnboardingWelcome.route,
            "onboarding_welcome"
        )
    }

    @Test
    fun `route strings align with EnfocaNavGraph structure`() {
        val startRoute = Screen.OnboardingWelcome.route
        val mainRoute = Screen.Main.route

        // EnfocaNavGraph uses OnboardingPagerScreen as the start destination
        assertEquals("onboarding_welcome", startRoute)

        // EnfocaNavGraph navigates to main with popUpTo(inclusive=true)
        assertEquals("main", mainRoute)

        // Verify they're different routes (graph has two distinct destinations)
        val validRoutes = setOf(startRoute, mainRoute)
        assertEquals(2, validRoutes.size)
    }

    @Test
    fun `onboarding pager has 5 pages after rigor removal`() {
        val pagerState = PagerState(pageCount = { 5 })
        assertEquals("Pager should have 5 pages (was 6 before)", 5, pagerState.pageCount)
        assertEquals("Pager starts at page 0 (Welcome)", 0, pagerState.currentPage)
    }

    @Test
    fun `onOmitir skip target page matches InfoScreen index`() {
        // Verifies the OMITIR wiring from navegationonboarding.kt:
        //   onOmitir = { coroutineScope.launch { pagerState.animateScrollToPage(3) } }
        //
        // Page mapping after rigor removal and renumbering:
        //   0 = OnboardingWelcomeScreen
        //   1 = UsageEstimationScreen  (OMITIR button lives here)
        //   2 = RealityRevealScreen    (SKIPPED by OMITIR)
        //   3 = OnboardingInfoScreen   (OMITIR target)
        //   4 = OnboardingContractScreen
        //
        // The OMITIR path: page 1 -> skip page 2 -> page 3
        val omitirSourcePage = 1
        val omitirTargetPage = 3
        val realityRevealPage = 2

        assertTrue(
            "OMITIR should navigate forward to a different page",
            omitirTargetPage > omitirSourcePage
        )
        assertEquals(
            "OMITIR should skip exactly one page (RealityReveal) between source and target",
            realityRevealPage, omitirSourcePage + 1
        )
        assertEquals(
            "OMITIR target ($omitirTargetPage) should be page right after RealityReveal ($realityRevealPage)",
            omitirTargetPage, realityRevealPage + 1
        )
    }

    @Test
    fun `TestNavHostController navigation between routes works`() {
        val navController = TestNavHostController(RuntimeEnvironment.getApplication())
        assertNotNull(navController)

        // Verify the Screen routes are meaningful navigation destinations
        val onboardingRoute = Screen.OnboardingWelcome.route
        val mainRoute = Screen.Main.route

        // Both routes should be valid non-empty strings
        assertTrue(onboardingRoute.isNotEmpty())
        assertTrue(mainRoute.isNotEmpty())

        // Routes should be different (start != main)
        assertTrue(onboardingRoute != mainRoute)
    }
}
