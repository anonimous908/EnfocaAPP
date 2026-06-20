package com.protas.enfocaapp.core.repository

import android.content.Context
import com.protas.enfocaapp.core.model.TodayUsageStats
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AppUsageRepositoryTest {

    private lateinit var fakeDataSource: FakeUsageStatsDataSource
    private lateinit var repository: AppUsageRepository

    @Before
    fun setUp() {
        fakeDataSource = FakeUsageStatsDataSource()
        val context: Context = RuntimeEnvironment.getApplication()
        repository = AppUsageRepository(context, fakeDataSource)
    }

    // SCE-09: Permiso denegado → ceros
    @Test
    fun `when permission denied returns zero usage stats`() {
        fakeDataSource.setPermissionGranted(false)
        val stats = repository.getTodayUsageStats()
        assertEquals(TodayUsageStats(0, 0), stats)
    }

    // SCE-01: 0 uso — 0h 0m, 0 unlocks
    @Test
    fun `when no usage today returns zero hours and zero unlocks`() {
        fakeDataSource.setForegroundTimeMs(0)
        fakeDataSource.setUnlockCount(0)
        val stats = repository.getTodayUsageStats()
        assertEquals(TodayUsageStats(0, 0), stats)
    }

    // SCE-04: Mapa vacío → 0 uso
    @Test
    fun `when no apps have stats returns zero usage`() {
        fakeDataSource.setForegroundTimeMs(0)
        val stats = repository.getTodayUsageStats()
        assertEquals(0L, stats.totalForegroundMs)
        assertEquals(0, stats.unlockCount)
    }

    // SCE-03: 45 min — NO redondea a 1h
    @Test
    fun `when 45 minutes of use does not round up to 1 hour`() {
        fakeDataSource.setForegroundTimeMs(2_700_000) // 45 min
        fakeDataSource.setUnlockCount(3)
        val stats = repository.getTodayUsageStats()
        assertEquals(2_700_000L, stats.totalForegroundMs)
        assertEquals(3, stats.unlockCount)
        // Verify no artificial inflation: 0h 45m, not 1h
        assertEquals(0, stats.hours)
        assertEquals(45, stats.minutes)
    }

    // SCE-02: 2h 30m de uso real
    @Test
    fun `when 2 hours 30 minutes of use returns correct stats`() {
        fakeDataSource.setForegroundTimeMs(9_000_000) // 2h 30m
        fakeDataSource.setUnlockCount(15)
        val stats = repository.getTodayUsageStats()
        assertEquals(9_000_000L, stats.totalForegroundMs)
        assertEquals(2, stats.hours)
        assertEquals(30, stats.minutes)
        assertEquals(15, stats.unlockCount)
    }

    // SCE-05: 15 desbloqueos reales
    @Test
    fun `when 15 real unlocks returns unlockCount of 15`() {
        fakeDataSource.setForegroundTimeMs(5_000_000)
        fakeDataSource.setUnlockCount(15)
        val stats = repository.getTodayUsageStats()
        assertEquals(15, stats.unlockCount)
    }

    // SCE-06: 0 desbloqueos (sin inflación)
    @Test
    fun `when zero real unlocks does not inflate to 1`() {
        fakeDataSource.setForegroundTimeMs(3_600_000)
        fakeDataSource.setUnlockCount(0)
        val stats = repository.getTodayUsageStats()
        assertEquals(0, stats.unlockCount)
    }

    // Multiple apps with combined foreground time
    @Test
    fun `sums foreground time across multiple packages`() {
        fakeDataSource.setForegroundTimeMs(5_000_000)
        fakeDataSource.setUnlockCount(2)
        val stats = repository.getTodayUsageStats()
        assertEquals(5_000_000L, stats.totalForegroundMs)
    }
}
