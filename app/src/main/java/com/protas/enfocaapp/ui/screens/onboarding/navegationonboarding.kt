package com.protas.enfocaapp.ui.screens.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OnboardingPagerScreen(
    onFinishOnboarding: () -> Unit,
    onOmitir: () -> Unit = {},
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val estimatedHours by viewModel.estimatedHours.collectAsState()
    val realUsageStats by viewModel.realUsageStats.collectAsState()
    val hasUsagePermission by viewModel.hasUsagePermission.collectAsState()

    // Definimos las  páginas del flujo de onboarding
    val pagerState = rememberPagerState(pageCount = { 5 })
    val coroutineScope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.checkUsagePermission()
    }

    LaunchedEffect(hasUsagePermission) {
        if (hasUsagePermission && pagerState.currentPage == 1) {
            pagerState.animateScrollToPage(2)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        // Desactiva swipe en pág 1 para forzar permisos, y usamos el botón Back
        userScrollEnabled = pagerState.currentPage != 1,
        beyondViewportPageCount = 1 // Mantiene la página adyacente pre-cargada para una transición fluida
    ) { page ->
        when (page) {
            // Página 0: Bienvenida Biométrica
            0 -> OnboardingWelcomeScreen(
                onNextClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(1) }
                }
            )

            // Página 1: Estimación de Uso (El juego de Expectativa)
            1 -> UsageEstimationScreen(
                onPermissionGranted = { hours ->
                    viewModel.setEstimatedHours(hours)
                    if (!hasUsagePermission) {
                        permissionLauncher.launch(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    } else {
                        coroutineScope.launch { pagerState.animateScrollToPage(2) }
                    }
                },
                onOmitir = {
                    coroutineScope.launch { pagerState.animateScrollToPage(3) }
                },
                onBack = {
                    coroutineScope.launch { pagerState.animateScrollToPage(0) }
                }
            )

            // Página 2: Estadísticas de Realidad
            2 -> RealityRevealScreen(
                estimatedHours = estimatedHours,
                realUsageStats = realUsageStats,
                onContinue = {
                    coroutineScope.launch { pagerState.animateScrollToPage(3) }
                }
            )

            // Página 3: Información de Enfoque
            3 -> OnboardingInfoScreen(
                onEnfrentarRealidad= {
                    coroutineScope.launch { pagerState.animateScrollToPage(4) }
                }
            )

            // Página 4: Contrato de Compromiso y Cierre
            4 -> OnboardingContractScreen(
                onFirmarContrato = {
                    viewModel.completeOnboarding()
                    onFinishOnboarding()
                },
                onAtras = {
                    coroutineScope.launch { pagerState.animateScrollToPage(3) }
                }
            )
        }
    }
}
