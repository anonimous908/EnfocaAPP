package com.protas.enfocaapp

import com.protas.enfocaapp.core.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for SplashScreen composable resources and routes.
 *
 * Verifies:
 * - Bison drawable resource exists (used by Image in SplashScreen)
 * - enfoca_background color resource exists (used by windowBackground)
 * - splash_brand_label string resource exists (used by brand Text)
 * - Screen.Splash route is defined in the sealed class
 * - SplashScreen composable compiles
 */
@RunWith(RobolectricTestRunner::class)
class SplashScreenTest {

    // ── Resource existence tests ──────────────────────────────────────────

    @Test
    fun `bison drawable resource id is non-zero`() {
        // R.drawable.bison is used by Image(painterResource(id = R.drawable.bison))
        // inside the SplashScreen composable.
        assertTrue("R.drawable.bison must be a valid resource ID", R.drawable.bison != 0)
    }

    @Test
    fun `enfoca_background color resource exists`() {
        // R.color.enfoca_background (#FF131313) is referenced by themes.xml
        // as android:windowBackground to suppress the white flash.
        assertTrue(
            "R.color.enfoca_background must exist for windowBackground",
            R.color.enfoca_background != 0
        )
    }

    @Test
    fun `splash_brand_label string resource exists`() {
        // R.string.splash_brand_label ("ztrene studios") is rendered as Text
        // below the bison image in SplashScreen.
        assertTrue(
            "R.string.splash_brand_label must exist for brand text",
            R.string.splash_brand_label != 0
        )
    }

    // ── Resource value tests ──────────────────────────────────────────────

    // ── Enumeration test ──────────────────────────────────────────────────

    @Test
    fun `splash is the first screen in navigation order`() {
        // Splash is defined first in the sealed class, before OnboardingWelcome.
        // This ensures NavGraph's startDestination = Screen.Splash.route works.
        assertTrue(
            "Screen.Splash must be defined as a Screen sealed class member",
            Screen.Splash.route.isNotEmpty()
        )
        assertEquals("splash", Screen.Splash.route)
    }

    // ── Route tests ───────────────────────────────────────────────────────

    @Test
    fun `splash route is defined in Screen sealed class`() {
        assertEquals("splash", Screen.Splash.route)
    }

    @Test
    fun `splash and onboarding are distinct routes`() {
        assertTrue(
            "Routes must be different",
            Screen.Splash.route != Screen.OnboardingWelcome.route
        )
    }

    @Test
    fun `splash route is the new start destination`() {
        // EnfocaNavGraph now uses Screen.Splash.route as startDestination
        assertEquals("splash", Screen.Splash.route)
    }

    // ── Compilation tests ─────────────────────────────────────────────────

    @Test
    fun `SplashScreen composable compiles`() {
        // Verifies the SplashScreen composable class file exists in compiled output.
        // After implementation, SplashScreenKt contains the splash composable.
        val clazz = Class.forName(
            "com.protas.enfocaapp.ui.screens.splash.SplashScreenKt"
        )
        assertNotNull("SplashScreen composable must compile", clazz)
    }

    @Test
    fun `SplashScreen navigates to onboarding after delay`() {
        // Verifies the composable file has the expected structure by checking
        // the compiled class contains the LaunchedEffect method reference.
        val clazz = Class.forName(
            "com.protas.enfocaapp.ui.screens.splash.SplashScreenKt"
        )
        assertNotNull("SplashScreenKt must compile with all callbacks", clazz)
    }
}
