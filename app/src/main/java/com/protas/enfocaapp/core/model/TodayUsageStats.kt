package com.protas.enfocaapp.core.model

data class TodayUsageStats(
    val totalForegroundMs: Long,
    val unlockCount: Int
) {
    val hours: Int get() = (totalForegroundMs / (1000 * 60 * 60)).toInt()
    val minutes: Int get() = ((totalForegroundMs % (1000 * 60 * 60)) / (1000 * 60)).toInt()
    val formattedTime: String get() = "${hours}h ${minutes}m"
}
