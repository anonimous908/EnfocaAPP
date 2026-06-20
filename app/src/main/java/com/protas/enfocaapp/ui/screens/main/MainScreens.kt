package com.protas.enfocaapp.ui.screens.main

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.protas.enfocaapp.R
import com.protas.enfocaapp.core.utils.hasBatteryOptimizationPermission
import com.protas.enfocaapp.core.utils.hasNotificationPermission
import com.protas.enfocaapp.core.utils.hasOverlayPermission
import com.protas.enfocaapp.core.utils.hasUsageStatsPermission
import com.protas.enfocaapp.core.utils.hasAccessibilityPermission
import com.protas.enfocaapp.ui.screens.main.components.DashboardView
import com.protas.enfocaapp.ui.screens.main.components.MissingPermissionsView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToIntervention: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var overlayGranted by remember { mutableStateOf(context.hasOverlayPermission()) }
    var notificationGranted by remember { mutableStateOf(context.hasNotificationPermission()) }
    var usageStatsGranted by remember { mutableStateOf(context.hasUsageStatsPermission()) }
    var batteryGranted by remember { mutableStateOf(context.hasBatteryOptimizationPermission()) }
    var accessibilityGranted by remember { mutableStateOf(context.hasAccessibilityPermission()) }

    val allGranted = overlayGranted && notificationGranted && usageStatsGranted && batteryGranted && accessibilityGranted

    fun refreshPermissions() {
        overlayGranted = context.hasOverlayPermission()
        notificationGranted = context.hasNotificationPermission()
        usageStatsGranted = context.hasUsageStatsPermission()
        batteryGranted = context.hasBatteryOptimizationPermission()
        accessibilityGranted = context.hasAccessibilityPermission()
    }

    LaunchedEffect(Unit) { refreshPermissions() }

    LaunchedEffect(allGranted) {
        if (allGranted) {
            val intent = Intent(context, Class.forName("com.protas.enfocaapp.core.service.EnfocaTrackerService"))
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadData()
                refreshPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(id = R.string.enfocapp_title), 
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (!allGranted && !uiState.isLoading) {
                MissingPermissionsView(
                    overlayGranted = overlayGranted,
                    notificationGranted = notificationGranted,
                    usageStatsGranted = usageStatsGranted,
                    batteryGranted = batteryGranted,
                    accessibilityGranted = accessibilityGranted,
                    onPermissionCheck = { refreshPermissions() }
                )
            } else if (allGranted) {
                DashboardView(
                    uiState = uiState,
                    onTestInterventionClick = onNavigateToIntervention
                )
            }
        }
    }
}


