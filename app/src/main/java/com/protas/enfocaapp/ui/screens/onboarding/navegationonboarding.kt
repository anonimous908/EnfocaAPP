package com.protas.enfocaapp.ui.screens.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun OnboardingPagerScreen(
    onFinishOnboarding: () -> Unit,
    onRigorSelected: (NivelRigor) -> Unit = {} // Callback para guardar el nivel elegido en DataStore/ViewModel
) {
    // Definimos las 4 páginas del flujo de onboarding
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1 // Mantiene la página adyacente pre-cargada para una transición fluida
    ) { page ->
        when (page) {
            // Página 0: Bienvenida Biométrica
            0 -> OnboardingWelcomeScreen(
                onNextClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(1) }
                }
            )

            // Página 1: Estadísticas de Realidad
            1 -> OnboardingStatsScreen(
                onEnfrentarRealidad = {
                    coroutineScope.launch { pagerState.animateScrollToPage(2) }
                }
            )

            // Página 2: Selección del Nivel de Rigor
            2 -> OnboardingRigorScreen(
                onEstablecerRigor = { nivel ->
                    onRigorSelected(nivel) // Captura el nivel (MODERADO, ESTRICTO, QUIRÚRGICO) y lo propaga
                    coroutineScope.launch { pagerState.animateScrollToPage(3) }
                }
            )

            // Página 3: Contrato de Compromiso y Cierre
            3 -> OnboardingContractScreen(
                onFirmarContrato = {
                    onFinishOnboarding()
                },
                onAtras = {
                    coroutineScope.launch { pagerState.animateScrollToPage(2) }
                }
            )
        }
    }
}
