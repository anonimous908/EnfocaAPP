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
import com.protas.enfocaapp.ui.screens.intervention.FutureMessageInterventionScreen
import androidx.compose.ui.res.stringResource
import com.protas.enfocaapp.R

import com.protas.enfocaapp.core.utils.hasAccessibilityPermission
import com.protas.enfocaapp.core.utils.hasBatteryOptimizationPermission
import com.protas.enfocaapp.core.utils.hasNotificationPermission
import com.protas.enfocaapp.core.utils.hasOverlayPermission
import com.protas.enfocaapp.core.utils.hasUsageStatsPermission

// ─── Main Screen ──────────────────────────────────────────────────────────────

@Composable
fun StatsScreen() {
    FutureMessageInterventionScreen(
        onFollowAdvice = {},
        onIgnoreAndContinue = {}
    )
}


// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PreviewStatsScreen() {
    EnfocaAPPTheme {
        StatsScreen()
    }
}
