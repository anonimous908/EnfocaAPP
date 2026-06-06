package com.protas.enfocaapp.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protas.enfocaapp.core.repository.AppUsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val appUsageRepository: AppUsageRepository
) : ViewModel() {

    private val _estimatedHours = MutableStateFlow(4)
    val estimatedHours: StateFlow<Int> = _estimatedHours.asStateFlow()

    private val _realHours = MutableStateFlow(0)
    val realHours: StateFlow<Int> = _realHours.asStateFlow()

    private val _unlocks = MutableStateFlow(0)
    val unlocks: StateFlow<Int> = _unlocks.asStateFlow()

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
            val (hours, unlockCount) = appUsageRepository.getTodayUsageStats()
            _realHours.value = hours
            _unlocks.value = unlockCount
        }
    }
}
