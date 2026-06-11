package com.protas.enfocaapp.core.repository

import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.protas.enfocaapp.core.model.TodayUsageStats
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUsageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val usageStatsDataSource: UsageStatsDataSource
) {

    fun hasUsageStatsPermission(): Boolean {
        return usageStatsDataSource.hasUsageStatsPermission()
    }

    /**
     * Returns accurate usage stats for today using [UsageStatsDataSource].
     *
     * Uses [UsageStatsDataSource.queryTotalForegroundTimeMs] for real foreground time
     * and [UsageStatsDataSource.queryKeyguardHiddenCount] for unlock count.
     * No artificial inflation: 0 usage = "0h 0m", no minimum unlock count.
     */
    fun getTodayUsageStats(): TodayUsageStats {
        if (!hasUsageStatsPermission()) return TodayUsageStats(0, 0)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val totalForegroundMs = usageStatsDataSource.queryTotalForegroundTimeMs(startTime, endTime)
        val unlocks = usageStatsDataSource.queryKeyguardHiddenCount(startTime, endTime)

        return TodayUsageStats(totalForegroundMs, unlocks)
    }

    /**
     * Verifica si el permiso SYSTEM_ALERT_WINDOW está concedido.
     */
    fun hasOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    /**
     * Verifica si el permiso de notificaciones está concedido.
     * En API 33+ se consulta NotificationManagerCompat; en versiones
     * anteriores el permiso se otorga automáticamente en el manifest.
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } else {
            true
        }
    }

    /**
     * Verifica si la app está excluida de optimizaciones de batería.
     */
    fun hasBatteryOptimizationPermission(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        return powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false
    }
}
