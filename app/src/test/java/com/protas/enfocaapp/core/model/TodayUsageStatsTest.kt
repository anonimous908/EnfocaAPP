package com.protas.enfocaapp.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class TodayUsageStatsTest {

    @Test
    fun `zero usage returns zero hours minutes and formatted time`() {
        val stats = TodayUsageStats(totalForegroundMs = 0, unlockCount = 0)
        assertEquals(0, stats.hours)
        assertEquals(0, stats.minutes)
        assertEquals("0h 0m", stats.formattedTime)
    }

    @Test
    fun `forty five minutes returns zero hours and 45 minutes`() {
        val stats = TodayUsageStats(totalForegroundMs = 2_700_000, unlockCount = 0)
        assertEquals(0, stats.hours)
        assertEquals(45, stats.minutes)
        assertEquals("0h 45m", stats.formattedTime)
    }

    @Test
    fun `two hours thirty minutes returns 2 hours and 30 minutes`() {
        val stats = TodayUsageStats(totalForegroundMs = 9_000_000, unlockCount = 5)
        assertEquals(2, stats.hours)
        assertEquals(30, stats.minutes)
        assertEquals("2h 30m", stats.formattedTime)
    }

    @Test
    fun `one hour exactly returns 1 hour and 0 minutes`() {
        val stats = TodayUsageStats(totalForegroundMs = 3_600_000, unlockCount = 3)
        assertEquals(1, stats.hours)
        assertEquals(0, stats.minutes)
        assertEquals("1h 0m", stats.formattedTime)
    }

    @Test
    fun `thirty seconds rounds down to zero minutes`() {
        val stats = TodayUsageStats(totalForegroundMs = 30_000, unlockCount = 0)
        assertEquals(0, stats.hours)
        assertEquals(0, stats.minutes)
        assertEquals("0h 0m", stats.formattedTime)
    }
}
