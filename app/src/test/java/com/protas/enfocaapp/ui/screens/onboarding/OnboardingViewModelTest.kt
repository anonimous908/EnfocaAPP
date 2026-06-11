package com.protas.enfocaapp.ui.screens.onboarding

import android.content.Context
import com.protas.enfocaapp.core.model.TodayUsageStats
import com.protas.enfocaapp.core.repository.AppUsageRepository
import com.protas.enfocaapp.core.repository.FakeUsageStatsDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class OnboardingViewModelTest {

    private lateinit var viewModel: OnboardingViewModel
    private lateinit var fakeDataSource: FakeUsageStatsDataSource
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDataSource = FakeUsageStatsDataSource()
        val context: Context = RuntimeEnvironment.getApplication()
        val repository = AppUsageRepository(context, fakeDataSource)
        viewModel = OnboardingViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has zero usage stats`() {
        val stats = viewModel.realUsageStats.value
        assertEquals(TodayUsageStats(0, 0), stats)
    }

    @Test
    fun `setEstimatedHours updates estimatedHours StateFlow`() {
        viewModel.setEstimatedHours(6)
        assertEquals(6, viewModel.estimatedHours.value)
    }

    @Test
    fun `hasUsagePermission reflects data source permission state`() {
        fakeDataSource.setPermissionGranted(true)
        viewModel.checkUsagePermission()
        assertTrue(viewModel.hasUsagePermission.value)
    }

    @Test
    fun `hasUsagePermission is false when data source denies permission`() {
        fakeDataSource.setPermissionGranted(false)
        viewModel.checkUsagePermission()
        assertFalse(viewModel.hasUsagePermission.value)
    }

    @Test
    fun `loadRealUsageStats emits TodayUsageStats from repository`() {
        fakeDataSource.setPermissionGranted(true)
        fakeDataSource.setForegroundTimeMs(5_400_000) // 1h 30m
        fakeDataSource.setUnlockCount(7)

        viewModel.checkUsagePermission()
        testDispatcher.scheduler.advanceUntilIdle()

        val stats = viewModel.realUsageStats.value
        assertEquals(5_400_000L, stats.totalForegroundMs)
        assertEquals(7, stats.unlockCount)
        assertEquals("1h 30m", stats.formattedTime)
    }

    @Test
    fun `loadRealUsageStats keeps zero stats when permission denied`() {
        fakeDataSource.setPermissionGranted(false)
        fakeDataSource.setForegroundTimeMs(9_000_000)
        fakeDataSource.setUnlockCount(15)

        viewModel.checkUsagePermission()
        testDispatcher.scheduler.advanceUntilIdle()

        val stats = viewModel.realUsageStats.value
        assertEquals(TodayUsageStats(0, 0), stats)
    }
}
