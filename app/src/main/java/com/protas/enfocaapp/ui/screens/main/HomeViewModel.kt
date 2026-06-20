package com.protas.enfocaapp.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protas.enfocaapp.core.model.AppUsageItem
import com.protas.enfocaapp.core.repository.AppUsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val hasPermission: Boolean = false,
    val disciplineScore: Int = 100,
    val unlocks: Int = 0,
    val totalTimeStr: String = "0h 0m",
    val topApps: List<AppUsageItem> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appUsageRepository: AppUsageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val hasPermission = appUsageRepository.hasUsageStatsPermission()
            if (hasPermission) {
                val stats = appUsageRepository.getTodayUsageStats()
                
                // Cálculo de disciplina: 100 - (desbloqueos * 1) - (horas * 5)
                var dScore = 100 - (stats.unlockCount * 1) - (stats.hours * 5)
                if (dScore < 0) dScore = 0

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasPermission = true,
                    disciplineScore = dScore,
                    unlocks = stats.unlockCount,
                    totalTimeStr = stats.formattedTime,
                    topApps = stats.topApps
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasPermission = false,
                    disciplineScore = 0,
                    unlocks = 0,
                    totalTimeStr = "0h 0m",
                    topApps = emptyList()
                )
            }
        }
    }
}
