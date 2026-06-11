package com.protas.enfocaapp

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for unify-theme-colors SDD change.
 *
 * Covers Phase 1 (color constants) and Phase 2 (screen refactors).
 * Uses Java reflection to verify ColorKt static fields exist/are absent.
 */
@RunWith(RobolectricTestRunner::class)
class UnifyThemeColorsTest {

    // ── Task 1.1: EnfocaLight* color variables exist ─────────────────────

    @Test
    fun `EnfocaLightBackground color variable exists in ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        val field = cls.getDeclaredField("EnfocaLightBackground")
        assertNotNull("EnfocaLightBackground must be defined in Color.kt", field)
    }

    @Test
    fun `EnfocaLightSurface color variable exists in ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        val field = cls.getDeclaredField("EnfocaLightSurface")
        assertNotNull("EnfocaLightSurface must be defined in Color.kt", field)
    }

    @Test
    fun `EnfocaLightOnBackground color variable exists in ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        val field = cls.getDeclaredField("EnfocaLightOnBackground")
        assertNotNull("EnfocaLightOnBackground must be defined in Color.kt", field)
    }

    @Test
    fun `EnfocaLightOnSurfaceVariant color variable exists in ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        val field = cls.getDeclaredField("EnfocaLightOnSurfaceVariant")
        assertNotNull("EnfocaLightOnSurfaceVariant must be defined in Color.kt", field)
    }

    @Test
    fun `EnfocaLightPrimary color variable exists in ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        val field = cls.getDeclaredField("EnfocaLightPrimary")
        assertNotNull("EnfocaLightPrimary must be defined in Color.kt", field)
    }

    @Test
    fun `EnfocaLightPrimaryContainer color variable exists in ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        val field = cls.getDeclaredField("EnfocaLightPrimaryContainer")
        assertNotNull("EnfocaLightPrimaryContainer must be defined in Color.kt", field)
    }

    @Test
    fun `EnfocaLightSurfaceContainer color variable exists in ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        val field = cls.getDeclaredField("EnfocaLightSurfaceContainer")
        assertNotNull("EnfocaLightSurfaceContainer must be defined in Color.kt", field)
    }

    @Test
    fun `EnfocaLightSurfaceContainerHigh color variable exists in ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        val field = cls.getDeclaredField("EnfocaLightSurfaceContainerHigh")
        assertNotNull("EnfocaLightSurfaceContainerHigh must be defined in Color.kt", field)
    }

    @Test
    fun `EnfocaLightSurfaceContainerLow color variable exists in ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        val field = cls.getDeclaredField("EnfocaLightSurfaceContainerLow")
        assertNotNull("EnfocaLightSurfaceContainerLow must be defined in Color.kt", field)
    }

    @Test
    fun `EnfocaLightOutlineVariant color variable exists in ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        val field = cls.getDeclaredField("EnfocaLightOutlineVariant")
        assertNotNull("EnfocaLightOutlineVariant must be defined in Color.kt", field)
    }

    @Test
    fun `EnfocaLightOutline color variable exists in ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        val field = cls.getDeclaredField("EnfocaLightOutline")
        assertNotNull("EnfocaLightOutline must be defined in Color.kt", field)
    }

    // ── Task 1.2: Legacy Purple/Pink variables are removed ────────────────

    @Test(expected = NoSuchFieldException::class)
    fun `Purple80 must be removed from ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        cls.getDeclaredField("Purple80")
    }

    @Test(expected = NoSuchFieldException::class)
    fun `PurpleGrey80 must be removed from ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        cls.getDeclaredField("PurpleGrey80")
    }

    @Test(expected = NoSuchFieldException::class)
    fun `Pink80 must be removed from ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        cls.getDeclaredField("Pink80")
    }

    @Test(expected = NoSuchFieldException::class)
    fun `Purple40 must be removed from ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        cls.getDeclaredField("Purple40")
    }

    @Test(expected = NoSuchFieldException::class)
    fun `PurpleGrey40 must be removed from ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        cls.getDeclaredField("PurpleGrey40")
    }

    @Test(expected = NoSuchFieldException::class)
    fun `Pink40 must be removed from ColorKt`() {
        val cls = Class.forName("com.protas.enfocaapp.ui.theme.ColorKt")
        cls.getDeclaredField("Pink40")
    }
}
