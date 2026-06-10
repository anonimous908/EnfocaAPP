package com.protas.enfocaapp.ui.screens.main

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.protas.enfocaapp.ui.theme.*

// ─── Permission helpers ────────────────────────────────────────────────────────

private fun Context.hasOverlayPermission(): Boolean =
    Settings.canDrawOverlays(this)

private fun Context.hasNotificationPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        NotificationManagerCompat.from(this).areNotificationsEnabled()
    else true

private fun Context.hasUsageStatsPermission(): Boolean {
    val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

private fun Context.hasBatteryOptimizationPermission(): Boolean =
    (getSystemService(Context.POWER_SERVICE) as? PowerManager)?.isIgnoringBatteryOptimizations(packageName) ?: false

// ─── Main Screen ──────────────────────────────────────────────────────────────

@Composable
fun StatsScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Individual permission states
    var overlayGranted by remember { mutableStateOf(context.hasOverlayPermission()) }
    var notificationGranted by remember { mutableStateOf(context.hasNotificationPermission()) }
    var usageStatsGranted by remember { mutableStateOf(context.hasUsageStatsPermission()) }
    var batteryGranted by remember { mutableStateOf(context.hasBatteryOptimizationPermission()) }

    // Helper to re-check all permissions
    fun refreshPermissions() {
        overlayGranted = context.hasOverlayPermission()
        notificationGranted = context.hasNotificationPermission()
        usageStatsGranted = context.hasUsageStatsPermission()
        batteryGranted = context.hasBatteryOptimizationPermission()
    }

    // Check on first composition
    LaunchedEffect(Unit) {
        refreshPermissions()
    }

    // Re-check when returning from system settings (ON_RESUME)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Intent launchers for each permission
    val overlayLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { refreshPermissions() }

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { refreshPermissions() }

    val usageStatsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { refreshPermissions() }

    val batteryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { refreshPermissions() }

    val allGranted = overlayGranted && notificationGranted && usageStatsGranted && batteryGranted

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EnfocaBackground)
    ) {
        // Atmospheric blobs (always visible)
        StatsAtmosphericBackground()

        if (allGranted) {
            // ── Content placeholder ─────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Estadísticas",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.02).em,
                        lineHeight = 38.sp
                    ),
                    color = EnfocaOnBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Contenido próximo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = EnfocaOnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // ── Permission mode ─────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Spacer(modifier = Modifier.height(24.dp))

                // Header
                Text(
                    text = "Sincronización de Datos de Uso",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.02).em,
                        lineHeight = 38.sp
                    ),
                    color = EnfocaOnBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Necesitamos acceso a tus estadísticas para identificar patrones de impulsividad y activar las intervenciones.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = EnfocaOnSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Permissions list
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    StatsPermissionRow(
                        icon = Icons.Outlined.Layers,
                        title = "Superposición de pantalla",
                        description = "Necesario para mostrar alertas e intervenciones sobre otras aplicaciones.",
                        granted = overlayGranted,
                        onClick = {
                            overlayLauncher.launch(
                                Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                )
                            )
                        }
                    )

                    StatsPermissionRow(
                        icon = Icons.Outlined.Notifications,
                        title = "Permiso de notificaciones",
                        description = "Para enviarte recordatorios y alertas de límite alcanzado.",
                        granted = notificationGranted,
                        onClick = {
                            notificationLauncher.launch(
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            )
                        }
                    )

                    StatsPermissionRow(
                        icon = Icons.Outlined.Analytics,
                        title = "Análisis de datos de uso",
                        description = "Permite identificar patrones de uso para personalizar tus límites.",
                        granted = usageStatsGranted,
                        onClick = {
                            usageStatsLauncher.launch(
                                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                            )
                        }
                    )

                    StatsPermissionRow(
                        icon = Icons.Outlined.BatterySaver,
                        title = "Ignorar Optimización de Batería",
                        description = "Evita que el sistema detenga EnfocaApp en segundo plano.",
                        granted = batteryGranted,
                        onClick = {
                            batteryLauncher.launch(
                                Intent(
                                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                    Uri.parse("package:${context.packageName}")
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ─── Components ───────────────────────────────────────────────────────────────

@Composable
private fun StatsAtmosphericBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Top-left blob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    EnfocaPrimaryContainer.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                radius = 480f,
                center = Offset(size.width * 0.25f, size.height * 0.25f)
            ),
            radius = 480f,
            center = Offset(size.width * 0.25f, size.height * 0.25f)
        )
        // Bottom-right blob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    EnfocaOutlineVariant.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                radius = 600f,
                center = Offset(size.width * 0.75f, size.height * 0.75f)
            ),
            radius = 600f,
            center = Offset(size.width * 0.75f, size.height * 0.75f)
        )
    }
}

@Composable
private fun StatsPermissionRow(
    icon: ImageVector,
    title: String,
    description: String,
    granted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(EnfocaSurfaceContainerLow)
            .border(
                width = 1.dp,
                color = EnfocaSurfaceContainerHigh,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EnfocaOnSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = EnfocaOnBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = EnfocaOnSurfaceVariant
            )
        }

        OutlinedButton(
            onClick = onClick,
            enabled = !granted,
            shape = CircleShape,
            border = BorderStroke(
                width = 1.dp,
                color = if (granted) EnfocaPrimary.copy(alpha = 0.1f)
                else EnfocaPrimary.copy(alpha = 0.2f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (granted) EnfocaPrimary.copy(alpha = 0.2f)
                else EnfocaPrimary.copy(alpha = 0.1f),
                contentColor = EnfocaPrimary,
                disabledContainerColor = EnfocaPrimary.copy(alpha = 0.15f),
                disabledContentColor = EnfocaPrimary.copy(alpha = 0.5f)
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 5.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                text = if (granted) "✓" else "Permitir",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    letterSpacing = 0.05.em
                )
            )
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PreviewStatsScreen() {
    EnfocaAPPTheme {
        StatsScreen()
    }
}
