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
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OnboardingPagerScreen(
    onFinishOnboarding: () -> Unit,
    onRigorSelected: (NivelRigor) -> Unit = {}, // Callback para guardar el nivel elegido en DataStore/ViewModel
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val estimatedHours by viewModel.estimatedHours.collectAsState()
    val realHours by viewModel.realHours.collectAsState()
    val unlocks by viewModel.unlocks.collectAsState()
    val hasUsagePermission by viewModel.hasUsagePermission.collectAsState()

    // Definimos las  páginas del flujo de onboarding
    val pagerState = rememberPagerState(pageCount = { 6 })
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
                }
            )

            // Página 2: Estadísticas de Realidad
            2 -> RealityRevealScreen(
                estimatedHours = estimatedHours,
                realHours = realHours,
                unlocks = unlocks,
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

            // Página 4: Selección del Nivel de Rigor
            4 -> OnboardingRigorScreen(
                onEstablecerRigor = { nivel ->
                    onRigorSelected(nivel) // Captura el nivel (MODERADO, ESTRICTO, QUIRÚRGICO) y lo propaga
                    coroutineScope.launch { pagerState.animateScrollToPage(5) }
                }
            )

            // Página 5: Contrato de Compromiso y Cierre
            5 -> OnboardingContractScreen(
                onFirmarContrato = {
                    onFinishOnboarding()
                },
                onAtras = {
                    coroutineScope.launch { pagerState.animateScrollToPage(4) }
                }
            )
        }
    }
}
