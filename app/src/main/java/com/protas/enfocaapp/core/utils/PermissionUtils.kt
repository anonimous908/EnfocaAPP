package com.protas.enfocaapp.core.utils

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

fun Context.hasOverlayPermission(): Boolean =
    Settings.canDrawOverlays(this)

fun Context.hasNotificationPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        NotificationManagerCompat.from(this).areNotificationsEnabled()
    else true

fun Context.hasUsageStatsPermission(): Boolean {
    val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

fun Context.hasBatteryOptimizationPermission(): Boolean =
    (getSystemService(Context.POWER_SERVICE) as? PowerManager)?.isIgnoringBatteryOptimizations(packageName) ?: false

fun Context.hasAccessibilityPermission(): Boolean {
    var accessibilityEnabled = 0
    try {
        accessibilityEnabled = Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED
        )
    } catch (e: Settings.SettingNotFoundException) {
    }
    if (accessibilityEnabled == 1) {
        val settingValue = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (settingValue != null) {
            return settingValue.contains(packageName)
        }
    }
    return false
}
