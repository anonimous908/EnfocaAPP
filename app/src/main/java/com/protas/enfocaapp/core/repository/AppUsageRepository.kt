package com.protas.enfocaapp.core.repository

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUsageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /**
     * Devuelve un Pair: (Horas Totales de Pantalla, Número de Desbloqueos)
     */
    fun getTodayUsageStats(): Pair<Int, Int> {
        if (!hasUsageStatsPermission()) return Pair(0, 0)

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // Set time range for today (from 00:00 to now)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()

        var totalInteractiveTimeMs = 0L
        var unlocks = 0
        var lastInteractiveTime = 0L

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            when (event.eventType) {
                UsageEvents.Event.SCREEN_INTERACTIVE -> {
                    lastInteractiveTime = event.timeStamp
                }
                UsageEvents.Event.SCREEN_NON_INTERACTIVE -> {
                    if (lastInteractiveTime > 0) {
                        totalInteractiveTimeMs += (event.timeStamp - lastInteractiveTime)
                        lastInteractiveTime = 0L
                    }
                }
                UsageEvents.Event.KEYGUARD_HIDDEN -> {
                    unlocks++
                }
            }
        }

        // Si la pantalla sigue encendida ahora, sumamos ese tiempo también
        if (lastInteractiveTime > 0) {
            totalInteractiveTimeMs += (endTime - lastInteractiveTime)
        }

        val totalHours = (totalInteractiveTimeMs / (1000 * 60 * 60)).toInt()
        
        // Si hay tiempo de uso pero no llega a la hora, mostramos 1h por redondeo amigable, sino se vería 0h.
        val displayHours = if (totalHours == 0 && totalInteractiveTimeMs > 0) 1 else totalHours

        // Evitar que devuelva 0 desbloqueos si sabemos que al menos abrió la app
        val displayUnlocks = if (unlocks == 0) 1 else unlocks

        return Pair(displayHours, displayUnlocks)
    }
}
