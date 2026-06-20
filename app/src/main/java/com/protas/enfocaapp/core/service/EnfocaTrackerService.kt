package com.protas.enfocaapp.core.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.protas.enfocaapp.core.repository.AppUsageRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@AndroidEntryPoint
class EnfocaTrackerService : Service() {

    @Inject
    lateinit var appUsageRepository: AppUsageRepository

    @Inject
    lateinit var notificationManager: EnfocaNotificationManager

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var trackerJob: Job? = null

    private var sessionStart: Long = 0L
    private var hasWarnedSession: Boolean = false
    private var hasWarnedDaily: Boolean = false
    private var hasWarnedUnlocks: Boolean = false
    private var lastDayStart: Long = 0L

    private var dailyUnlocks: Int = 0

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> {
                    sessionStart = System.currentTimeMillis()
                    hasWarnedSession = false
                    dailyUnlocks++
                }
                Intent.ACTION_SCREEN_OFF -> {
                    sessionStart = 0L
                    hasWarnedSession = false
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenReceiver, filter)
        
        sessionStart = System.currentTimeMillis()
        resetDailyFlagsIfNeeded(sessionStart)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = notificationManager.createPersistentNotification()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            startForeground(
                EnfocaNotificationManager.PERSISTENT_NOTIFICATION_ID, 
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(EnfocaNotificationManager.PERSISTENT_NOTIFICATION_ID, notification)
        }

        startTrackingLoop()

        return START_STICKY
    }

    private fun startTrackingLoop() {
        trackerJob?.cancel()
        trackerJob = serviceScope.launch {
            while (isActive) {
                delay(60_000) // 1 minute
                checkStats()
            }
        }
    }

    private fun checkStats() {
        val currentTime = System.currentTimeMillis()
        
        resetDailyFlagsIfNeeded(currentTime)

        // Session checking (30 min)
        if (sessionStart > 0 && currentTime - sessionStart > 30 * 60 * 1000L && !hasWarnedSession) {
            notificationManager.showSessionWarning(30)
            hasWarnedSession = true
        }

        // Daily checking (4 hours usage, 70 unlocks)
        try {
            // We use the raw UsageStatsManager API directly here to avoid O(N) loops in the repository
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
            val stats = usageStatsManager.queryAndAggregateUsageStats(lastDayStart, currentTime)
            val totalForegroundMs = stats.values.sumOf { it.totalTimeInForeground }
            
            // Usage > 4 hours
            if (totalForegroundMs > 4 * 60 * 60 * 1000L && !hasWarnedDaily) {
                notificationManager.showDailyWarning(4)
                hasWarnedDaily = true
            }

            // Unlocks > 70
            if (dailyUnlocks > 70 && !hasWarnedUnlocks) {
                notificationManager.showUnlocksWarning(70)
                hasWarnedUnlocks = true
            }
        } catch (e: SecurityException) {
            Log.e("EnfocaTrackerService", "Missing usage stats permission: ${e.message}")
            // Consider showing a warning notification to the user here
        } catch (e: Exception) {
            Log.e("EnfocaTrackerService", "Unexpected error in checkStats", e)
        }
    }

    private fun resetDailyFlagsIfNeeded(currentTime: Long) {
        val currentDayStart = getStartOfDay(currentTime)
        if (currentDayStart != lastDayStart) {
            lastDayStart = currentDayStart
            hasWarnedDaily = false
            hasWarnedUnlocks = false
            dailyUnlocks = 0 // Reset unlocks at midnight
        }
    }

    private fun getStartOfDay(time: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = time
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    override fun onDestroy() {
        super.onDestroy()
        trackerJob?.cancel()
        unregisterReceiver(screenReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
