package com.protas.enfocaapp.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

// Página 0: Bienvenida
// Página 1: Estadísticas
// Página 2: Rigor
// Página 3: Contrato

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPagerScreen(
    onFinishOnboarding: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1// pre-carga 1 página extra hacia atrás y 1 página extra hacia adelante
    ) { page ->
        when (page) {
            0 -> OnboardingWelcomeScreen(
                onNextClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            )
            1 -> OnboardingStatsScreen(
                onEnfrentarRealidad = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                }
            )
            2 -> OnboardingRigorScreen(
                onEstablecerRigor = { _ ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(3)
                    }
                }
            )
            3 -> OnboardingContractScreen(
                onFirmarContrato = {
                    onFinishOnboarding()
                },
                onAtras = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                }
            )
        }
    }
}
