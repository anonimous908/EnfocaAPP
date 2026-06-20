package com.protas.enfocaapp.core.model

data class AppUsageItem(
    val packageName: String,
    val appName: String,
    val timeMs: Long
) {
    val hours: Int get() = (timeMs / (1000 * 60 * 60)).toInt()
    val minutes: Int get() = ((timeMs % (1000 * 60 * 60)) / (1000 * 60)).toInt()
    val formattedTime: String get() = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

data class TodayUsageStats(
    val totalForegroundMs: Long,
    val unlockCount: Int,
    val topApps: List<AppUsageItem> = emptyList()
) {
    val hours: Int get() = (totalForegroundMs / (1000 * 60 * 60)).toInt()
    val minutes: Int get() = ((totalForegroundMs % (1000 * 60 * 60)) / (1000 * 60)).toInt()
    val formattedTime: String get() = "${hours}h ${minutes}m"
}
