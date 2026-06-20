package com.protas.enfocaapp.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protas.enfocaapp.core.database.AppRestrictionDao
import com.protas.enfocaapp.core.repository.AppUsageRepository
import com.protas.enfocaapp.core.model.AppRestriction
import com.protas.enfocaapp.core.utils.AppInfo
import com.protas.enfocaapp.core.utils.PackageManagerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppBlockViewModel @Inject constructor(
    private val appRestrictionDao: AppRestrictionDao,
    private val packageManagerHelper: PackageManagerHelper,
    private val appUsageRepository: AppUsageRepository
) : ViewModel() {

    val restrictions: StateFlow<List<AppRestriction>> = appRestrictionDao.getAllRestrictions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps

    private val _appUsages = MutableStateFlow<Map<String, Long>>(emptyMap())
    val appUsages: StateFlow<Map<String, Long>> = _appUsages

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Actualizar los usos instantáneamente si cambian las restricciones (ej. al agregar una nueva)
            restrictions.collect { currentRestrictions ->
                updateUsages(currentRestrictions)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            // Actualizar periódicamente cada 30 segundos
            while (true) {
                delay(30_000)
                updateUsages(restrictions.value)
            }
        }
    }

    private fun updateUsages(currentRestrictions: List<AppRestriction>) {
        val usages = currentRestrictions.associate { restriction ->
            restriction.packageName to appUsageRepository.getAppUsageToday(restriction.packageName)
        }
        _appUsages.value = usages
    }

    fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            _installedApps.value = packageManagerHelper.getInstalledApps()
        }
    }

    fun saveRestriction(packageName: String, appName: String, limitMinutes: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            appRestrictionDao.insertRestriction(
                AppRestriction(
                    packageName = packageName,
                    appName = appName,
                    limitMinutes = limitMinutes,
                    status = "ACTIVE_BLOCK",
                    enabled = true
                )
            )
        }
    }

    fun toggleRestriction(restriction: AppRestriction, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            appRestrictionDao.insertRestriction(restriction.copy(enabled = enabled))
        }
    }

    fun deleteRestriction(restriction: AppRestriction) {
        viewModelScope.launch(Dispatchers.IO) {
            appRestrictionDao.deleteRestriction(restriction)
        }
    }

    fun getAppIcon(packageName: String) = packageManagerHelper.getAppIcon(packageName)
}
