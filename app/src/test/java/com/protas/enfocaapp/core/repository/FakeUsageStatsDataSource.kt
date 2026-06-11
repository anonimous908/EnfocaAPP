package com.protas.enfocaapp.core.repository

/**
 * Fake [UsageStatsDataSource] for testing.
 *
 * Returns controlled values for foreground time and unlock count
 * without any Android framework dependencies.
 */
class FakeUsageStatsDataSource : UsageStatsDataSource {

    private var foregroundTimeMs: Long = 0L
    private var unlockCount: Int = 0
    private var permissionGranted: Boolean = true

    /** Convenience: set total foreground time in ms */
    fun setForegroundTimeMs(timeMs: Long) {
        foregroundTimeMs = timeMs
    }

    /** Convenience: set expected unlock count */
    fun setUnlockCount(count: Int) {
        unlockCount = count
    }

    /** Convenience: set permission state */
    fun setPermissionGranted(granted: Boolean) {
        permissionGranted = granted
    }

    /** Clears all state back to default (no usage, no unlocks, permission granted) */
    fun reset() {
        foregroundTimeMs = 0L
        unlockCount = 0
        permissionGranted = true
    }

    fun isPermissionGranted(): Boolean = permissionGranted

    override fun hasUsageStatsPermission(): Boolean = permissionGranted

    override fun queryTotalForegroundTimeMs(startTime: Long, endTime: Long): Long {
        return if (permissionGranted) foregroundTimeMs else 0L
    }

    override fun queryKeyguardHiddenCount(startTime: Long, endTime: Long): Int {
        return if (permissionGranted) unlockCount else 0
    }
}
