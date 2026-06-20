package com.protas.enfocaapp.core.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.protas.enfocaapp.core.database.AppRestrictionDao
import com.protas.enfocaapp.core.repository.AppUsageRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import javax.inject.Inject

@AndroidEntryPoint
class EnfocaAccessibilityService : AccessibilityService() {

    @Inject lateinit var appRestrictionDao: AppRestrictionDao
    @Inject lateinit var overlayManager: InterventionOverlayManager
    @Inject lateinit var appUsageRepository: AppUsageRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Lista local en memoria para chequeos rápidos
    private var activeRestrictions = listOf<com.protas.enfocaapp.core.model.AppRestriction>()

    override fun onServiceConnected() {
        super.onServiceConnected()
        
        // Observamos la base de datos en tiempo real
        serviceScope.launch {
            appRestrictionDao.getAllRestrictions().collectLatest { restrictions ->
                activeRestrictions = restrictions
                
                // Extraemos solo los nombres de los paquetes que están activos
                val packageList = restrictions
                    .filter { it.enabled }
                    .map { it.packageName }
                    .toMutableList()
                
                // Agregamos nuestra propia app para asegurar que el arreglo nunca esté completamente vacío
                // (Si es null, Android asume que queremos espiar TODOS los paquetes)
                packageList.add(packageName)

                // Actualizamos la configuración del servicio dinámicamente
                val currentInfo = serviceInfo ?: return@collectLatest
                currentInfo.packageNames = packageList.toTypedArray()
                serviceInfo = currentInfo
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        
        if (packageName == "com.protas.enfocaapp") return

        serviceScope.launch {
            // Ya no buscamos en la base de datos en cada evento, usamos la lista en memoria
            val restrictedApp = activeRestrictions.find { it.packageName == packageName && it.enabled }

            if (restrictedApp != null) {
                val usageMs = appUsageRepository.getAppUsageToday(packageName)
                val limitMs = restrictedApp.limitMinutes * 60_000L
                
                if (usageMs >= limitMs) {
                    launchMain {
                        overlayManager.showOverlay(
                            onClose = {
                                performGlobalAction(GLOBAL_ACTION_HOME)
                            }
                        )
                    }
                } else {
                    launchMain { overlayManager.hideOverlay() }
                }
            } else {
                launchMain {
                    overlayManager.hideOverlay()
                }
            }
        }
    }

    private fun launchMain(block: () -> Unit) {
        serviceScope.launch(Dispatchers.Main) { block() }
    }

    override fun onInterrupt() {
        launchMain { overlayManager.hideOverlay() }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Esconder overlay usando un scope independiente ya que vamos a destruir el servicio
        CoroutineScope(Dispatchers.Main).launch { overlayManager.hideOverlay() }
        serviceScope.cancel()
    }
}
