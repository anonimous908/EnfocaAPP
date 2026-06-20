package com.protas.enfocaapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.protas.enfocaapp.core.repository.UserPreferencesRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlinx.coroutines.runBlocking

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CompletarOnboardingTest {

    // El orden (order) es importante: Primero Hilt inyecta dependencias, luego arranca la vista
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var repository: UserPreferencesRepository

    @Before
    fun init() {
        hiltRule.inject()
        // Aseguramos que la base de datos esté limpia antes del test para forzar que aparezca el Onboarding
        runBlocking {
            repository.saveOnboardingCompleted(false)
        }
    }

    @Test
    fun testFlujoCompletoDeOnboarding() {
        // 1. ESPERAMOS EL SPLASH (1.5 segundos)
        // La pantalla de Splash debería llevar al usuario al Onboarding (Página 1)
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            // Espera hasta que aparezca el texto de bienvenida de la página 1
            composeTestRule.onAllNodes(androidx.compose.ui.test.hasText("Recupera tu atencion", substring = true, ignoreCase = true)).fetchSemanticsNodes().isNotEmpty()
        }

        // 2. EL ROBOT HACE CLIC EN LOS BOTONES (Navegación precisa)
        // Pantalla 1: Bienvenida
        composeTestRule.onNodeWithText("Siguiente", ignoreCase = true)
            .assertIsDisplayed()
            .performClick()
        composeTestRule.waitForIdle()

        // Pantalla 2: Estimación de Uso (Usamos el botón "Omitir" para saltar rápido a la info)
        composeTestRule.onNodeWithText("Omitir", ignoreCase = true)
            .assertIsDisplayed()
            .performClick()
        composeTestRule.waitForIdle()

        // Pantalla 3: Información ("Así te ayudamos")
        composeTestRule.onNodeWithText("Entendido, continuar", ignoreCase = true)
            .assertIsDisplayed()
            .performClick()
        composeTestRule.waitForIdle()

        // 3. LLEGAMOS AL CONTRATO. El robot espera a que el botón sea visible y lo presiona por 1.5 segundos
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(androidx.compose.ui.test.hasText("Mantén", substring = true, ignoreCase = true)).fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule.onNodeWithText("Mantén", substring = true, ignoreCase = true)
            .performTouchInput {
                longClick(durationMillis = 1500) 
            }

        // 4. VERIFICAMOS QUE SE ABRA EL DASHBOARD PRINCIPAL
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            // Asumiendo que el Dashboard tiene algún texto o pestaña que diga "Inicio", "Estadísticas", etc.
            // (Revisaremos luego los nombres exactos en MainScreen, por ahora verificamos que el contrato ya no esté)
            composeTestRule.onAllNodes(androidx.compose.ui.test.hasText("Mantén", substring = true, ignoreCase = true)).fetchSemanticsNodes().isEmpty()
        }
    }
}
