package com.protas.enfocaapp.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.protas.enfocaapp.core.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import kotlinx.coroutines.flow.first

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    suspend fun hasCompletedOnboarding(): Boolean {
        return userPreferencesRepository.onboardingCompletedFlow.first()
    }
}
