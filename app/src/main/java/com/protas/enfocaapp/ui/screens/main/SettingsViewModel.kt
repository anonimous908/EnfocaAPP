package com.protas.enfocaapp.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protas.enfocaapp.core.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val adaptiveFriction: StateFlow<Boolean> = userPreferencesRepository.friccionAdaptativaFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val nightRestriction: StateFlow<Boolean> = userPreferencesRepository.restriccionNocturnaFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val appLimitsEnabled: StateFlow<Boolean> = userPreferencesRepository.limitesAplicacionesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val frictionLevel: StateFlow<Int> = userPreferencesRepository.intensidadFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    fun updateAdaptiveFriction(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.saveFriccionAdaptativa(enabled)
        }
    }

    fun updateNightRestriction(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.saveRestriccionNocturna(enabled)
        }
    }

    fun updateAppLimitsEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.saveLimitesAplicaciones(enabled)
        }
    }

    fun updateFrictionLevel(level: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.saveIntensidad(level.toInt())
        }
    }
}
