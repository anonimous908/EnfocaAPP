package com.protas.enfocaapp.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protas.enfocaapp.core.model.TodayUsageStats
import com.protas.enfocaapp.core.repository.AppUsageRepository
import com.protas.enfocaapp.core.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val appUsageRepository: AppUsageRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _estimatedHours = MutableStateFlow(4)
    val estimatedHours: StateFlow<Int> = _estimatedHours.asStateFlow()

    private val _realUsageStats = MutableStateFlow(TodayUsageStats(0, 0))
    val realUsageStats: StateFlow<TodayUsageStats> = _realUsageStats.asStateFlow()

    private val _hasUsagePermission = MutableStateFlow(false)
    val hasUsagePermission: StateFlow<Boolean> = _hasUsagePermission.asStateFlow()

    init {
        checkUsagePermission()
    }

    fun setEstimatedHours(hours: Int) {
        _estimatedHours.value = hours
    }

    fun checkUsagePermission() {
        val hasPermission = appUsageRepository.hasUsageStatsPermission()
        _hasUsagePermission.value = hasPermission
        if (hasPermission) {
            loadRealUsageStats()
        }
    }

    private fun loadRealUsageStats() {
        viewModelScope.launch {
            _realUsageStats.value = appUsageRepository.getTodayUsageStats()
        }
    }

    fun completeOnboarding() {
        userPreferencesRepository.saveOnboardingCompleted(true)
    }
}
