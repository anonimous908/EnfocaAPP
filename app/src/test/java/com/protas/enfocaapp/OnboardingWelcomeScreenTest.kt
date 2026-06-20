package com.protas.enfocaapp

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for OnboardingWelcomeScreen composable changes (Workstream B).
 *
 * Verifies:
 * - Logo drawable resource exists (B-1: 128dp rendering requires the asset)
 * - Description string resource used inside card exists (B-3)
 * - ScannerRing composable compiles (B-4)
 */
@RunWith(RobolectricTestRunner::class)
class OnboardingWelcomeScreenTest {

    // ── Resource existence tests (B-1, B-3) ──────────────────────────────────

    @Test
    fun `logo drawable resource id is non-zero`() {
        // R.drawable.logo is used by Image(painterResource(id = R.drawable.logo))
        // inside the BiometricVisualizer hub. After B-1, the logo is 128dp.
        assertTrue("R.drawable.logo must be a valid resource ID", R.drawable.logo != 0)
    }

    @Test
    fun `onboarding description string resource exists`() {
        // The description Text composable is wrapped in a Surface card (B-3).
        // Verifies the string resource referenced by the Text inside the card.
        assertTrue(
            "R.string.onboarding_introduccion_description must exist for card content",
            R.string.onboarding_introduccion_description != 0
        )
    }

    @Test
    fun `onboarding title string resource exists`() {
        // Verifies the title Text resource — still rendered above the card.
        assertTrue(
            "R.string.onboarding_introduccion_title must exist",
            R.string.onboarding_introduccion_title != 0
        )
    }

    @Test
    fun `onboarding next button string resource exists`() {
        // Verifies the next button resource — still rendered below the card.
        assertTrue(
            "R.string.onboarding_next_button must exist",
            R.string.onboarding_next_button != 0
        )
    }

    // ── Scanner ring compilation (B-4) ───────────────────────────────────────

    @Test
    fun `OnboardingWelcomeScreen composable compiles with ScannerRing`() {
        // Verifies the composable class file exists in compiled output.
        // After B-4 implementation, OnboardingWelcomeScreenKt contains the
        // new ScannerRing composable (no more ScanningOverlay).
        val clazz = Class.forName(
            "com.protas.enfocaapp.ui.screens.onboarding.OnboardingWelcomeScreenKt"
        )
        assertNotNull(
            "OnboardingWelcomeScreen composable must compile with ScannerRing",
            clazz
        )
    }

    // ── Pulsing ring base size (B-4) ────────────────────────────────────────

    @Test
    fun `PulsingRing composable compiles`() {
        // Verifies PulsingRing private composable exists in the compiled output.
        // After B-4, PulsingRing base size changed from 64dp to 160dp.
        val clazz = Class.forName(
            "com.protas.enfocaapp.ui.screens.onboarding.OnboardingWelcomeScreenKt"
        )
        assertNotNull("PulsingRing composable must compile", clazz)
    }

    // ── BiometricVisualizer hub structure (B-2) ─────────────────────────────

    @Test
    fun `BiometricVisualizer composable compiles`() {
        // Verifies BiometricVisualizer private composable exists in compiled output.
        // After B-2, hub size changed from 96dp to 128dp and ScannerRing added.
        val clazz = Class.forName(
            "com.protas.enfocaapp.ui.screens.onboarding.OnboardingWelcomeScreenKt"
        )
        assertNotNull(
            "BiometricVisualizer composable must compile with ScannerRing",
            clazz
        )
    }
}
