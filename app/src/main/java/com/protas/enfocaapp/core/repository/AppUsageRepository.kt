package com.protas.enfocaapp.core.repository

import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.protas.enfocaapp.core.model.AppUsageItem
import com.protas.enfocaapp.core.model.TodayUsageStats
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import com.protas.enfocaapp.core.utils.hasUsageStatsPermission
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUsageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val usageStatsDataSource: UsageStatsDataSource
) {

    fun hasUsageStatsPermission(): Boolean {
        return context.hasUsageStatsPermission()
    }

    /**
     * Returns accurate usage stats for today using [UsageStatsDataSource].
     *
     * Uses [UsageStatsDataSource.queryTotalForegroundTimeMs] for real foreground time
     * and [UsageStatsDataSource.queryKeyguardHiddenCount] for unlock count.
     * No artificial inflation: 0 usage = "0h 0m", no minimum unlock count.
     */
    fun getTodayUsageStats(): TodayUsageStats {
        if (!hasUsageStatsPermission()) return TodayUsageStats(0, 0, emptyList())

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val totalForegroundMs = usageStatsDataSource.queryTotalForegroundTimeMs(startTime, endTime)
        val unlocks = usageStatsDataSource.queryKeyguardHiddenCount(startTime, endTime)
        val rawTopApps = usageStatsDataSource.queryTopApps(startTime, endTime, 10) // Extraemos 10 por si filtramos varias
        val packageManager = context.packageManager
        
        val topApps = rawTopApps.mapNotNull { rawUsage ->
            val packageName = rawUsage.packageName
            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                // Ignorar la propia app (EnfocaApp)
                if (packageName == context.packageName) return@mapNotNull null
                
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                AppUsageItem(packageName, appName, rawUsage.timeMs)
            } catch (e: Exception) {
                null
            }
        }.take(3) // Nos quedamos solo con el Top 3 final

        return TodayUsageStats(totalForegroundMs, unlocks, topApps)
    }

    /**
     * Returns the foreground time in milliseconds for a specific app for today.
     */
    fun getAppUsageToday(packageName: String): Long {
        if (!hasUsageStatsPermission()) return 0L

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        return usageStatsDataSource.queryAppUsage(packageName, startTime, endTime)
    }


}
