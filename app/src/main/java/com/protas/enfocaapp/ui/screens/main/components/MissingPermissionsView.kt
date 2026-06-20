package com.protas.enfocaapp.ui.screens.main.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.protas.enfocaapp.R
import com.protas.enfocaapp.core.utils.hasBatteryOptimizationPermission
import com.protas.enfocaapp.core.utils.hasNotificationPermission
import com.protas.enfocaapp.core.utils.hasOverlayPermission
import com.protas.enfocaapp.core.utils.hasUsageStatsPermission
import com.protas.enfocaapp.ui.components.PermissionRow

@Composable
fun MissingPermissionsView(
    overlayGranted: Boolean,
    notificationGranted: Boolean,
    usageStatsGranted: Boolean,
    batteryGranted: Boolean,
    accessibilityGranted: Boolean,
    onPermissionCheck: () -> Unit
) {
    val context = LocalContext.current

    val overlayLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { onPermissionCheck() }

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { onPermissionCheck() }

    val usageStatsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { onPermissionCheck() }

    val batteryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { onPermissionCheck() }

    val accessibilityLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { onPermissionCheck() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Lock, 
            contentDescription = null, 
            modifier = Modifier.size(64.dp), 
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.permissions_required), 
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PermissionRow(
                icon = Icons.Outlined.Layers,
                title = stringResource(R.string.permissions_overlay),
                description = stringResource(R.string.permissions_overlay_desc),
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

            PermissionRow(
                icon = Icons.Outlined.Notifications,
                title = stringResource(R.string.permissions_notifications),
                description = stringResource(R.string.permissions_notifications_desc),
                granted = notificationGranted,
                onClick = {
                    notificationLauncher.launch(
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    )
                }
            )

            PermissionRow(
                icon = Icons.Outlined.Analytics,
                title = stringResource(R.string.permissions_usage_stats),
                description = stringResource(R.string.permissions_usage_stats_desc),
                granted = usageStatsGranted,
                onClick = {
                    usageStatsLauncher.launch(
                        Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    )
                }
            )

            PermissionRow(
                icon = Icons.Outlined.BatterySaver,
                title = stringResource(R.string.permissions_battery),
                description = stringResource(R.string.permissions_battery_desc),
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

            PermissionRow(
                icon = Icons.Outlined.Accessibility,
                title = stringResource(R.string.permissions_accessibility),
                description = stringResource(R.string.permissions_accessibility_desc),
                granted = accessibilityGranted,
                onClick = {
                    accessibilityLauncher.launch(
                        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    )
                }
            )
        }
    }
}
