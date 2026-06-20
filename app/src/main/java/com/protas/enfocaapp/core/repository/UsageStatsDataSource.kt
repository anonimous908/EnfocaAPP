package com.protas.enfocaapp.core.repository

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class RawAppUsage(val packageName: String, val timeMs: Long)

/**
 * Abstraction over [UsageStatsManager] that returns primitive aggregates,
 * enabling unit testing without Android framework dependencies.
 */
interface UsageStatsDataSource {
    /** Returns true if PACKAGE_USAGE_STATS permission is granted. */
    fun hasUsageStatsPermission(): Boolean

    /** Returns total foreground time in milliseconds for all apps in [startTime..endTime]. */
    fun queryTotalForegroundTimeMs(startTime: Long, endTime: Long): Long

    /** Returns count of [UsageEvents.Event.KEYGUARD_HIDDEN] events in [startTime..endTime]. */
    fun queryKeyguardHiddenCount(startTime: Long, endTime: Long): Int

    /** Returns the top [limit] apps sorted by foreground time. */
    fun queryTopApps(startTime: Long, endTime: Long, limit: Int): List<RawAppUsage>

    /** Returns the total foreground time in ms for a specific package in [startTime..endTime]. */
    fun queryAppUsage(packageName: String, startTime: Long, endTime: Long): Long
}

@Singleton
class RealUsageStatsDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : UsageStatsDataSource {

    private val usageStatsManager: UsageStatsManager
        get() = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    private var lastQueryTime = 0L
    private var lastStartTime = 0L
    private var cachedUsageMap: Map<String, android.app.usage.UsageStats> = emptyMap()

    @Synchronized
    private fun getAggregatedUsageMap(startTime: Long, endTime: Long): Map<String, android.app.usage.UsageStats> {
        val now = System.currentTimeMillis()
        // Cache valid for 15 seconds to prevent melting the CPU on loops
        if (now - lastQueryTime > 15_000L || startTime != lastStartTime || cachedUsageMap.isEmpty()) {
            cachedUsageMap = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
            lastQueryTime = now
            lastStartTime = startTime
        }
        return cachedUsageMap
    }

    override fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override fun queryTotalForegroundTimeMs(startTime: Long, endTime: Long): Long {
        val appUsageMap = getAggregatedUsageMap(startTime, endTime)
        return appUsageMap.values.sumOf { it.totalTimeInForeground }
    }

    override fun queryKeyguardHiddenCount(startTime: Long, endTime: Long): Int {
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()
        var count = 0
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.KEYGUARD_HIDDEN) {
                count++
            }
        }
        return count
    }

    override fun queryTopApps(startTime: Long, endTime: Long, limit: Int): List<RawAppUsage> {
        val appUsageMap = getAggregatedUsageMap(startTime, endTime)
        
        return appUsageMap.values
            .filter { it.totalTimeInForeground > 0 }
            .sortedByDescending { it.totalTimeInForeground }
            .map { usageStats ->
                RawAppUsage(usageStats.packageName, usageStats.totalTimeInForeground)
            }
            .take(limit)
    }

    override fun queryAppUsage(packageName: String, startTime: Long, endTime: Long): Long {
        val appUsageMap = getAggregatedUsageMap(startTime, endTime)
        return appUsageMap[packageName]?.totalTimeInForeground ?: 0L
    }
}
