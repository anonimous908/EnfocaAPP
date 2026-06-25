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
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class EnfocaAccessibilityService : AccessibilityService() {

    @Inject lateinit var appRestrictionDao: AppRestrictionDao
    @Inject lateinit var overlayManager: InterventionOverlayManager
    @Inject lateinit var appUsageRepository: AppUsageRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private var activeRestrictions = listOf<com.protas.enfocaapp.core.model.AppRestriction>()
    
    // Caché ultra rápida de apps agotadas para bloqueo en 0 milisegundos
    private var exhaustedApps = mutableSetOf<String>()

    // Lista de launchers del sistema
    private var launcherPackages = listOf<String>()

    override fun onServiceConnected() {
        super.onServiceConnected()
        
        val info = serviceInfo ?: return
        info.packageNames = null // null significa que escucha a todos los paquetes
        serviceInfo = info

        // Detectar todos los launchers instalados para saber cuándo el usuario va al inicio
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        val resolveInfos = packageManager.queryIntentActivities(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
        launcherPackages = resolveInfos.map { it.activityInfo.packageName }
        
        // Observamos la base de datos en tiempo real
        serviceScope.launch {
            appRestrictionDao.getAllRestrictions().collectLatest { restrictions ->
                activeRestrictions = restrictions
                // Al cambiar las restricciones, forzamos una actualización del caché
                updateExhaustedCache()
            }
        }
        
        // Bucle en segundo plano que actualiza el caché de apps agotadas periódicamente
        serviceScope.launch {
            while (true) {
                delay(60_000) // cada 60 segundos (ahorro de batería)
                updateExhaustedCache()
            }
        }
    }
    
    private suspend fun updateExhaustedCache() {
        val newExhausted = mutableSetOf<String>()
        for (app in activeRestrictions) {
            if (!app.enabled) continue
            val limitMs = app.limitMinutes * 60_000L
            if (limitMs == 0L) {
                newExhausted.add(app.packageName)
            } else {
                val usageMs = appUsageRepository.getAppUsageToday(app.packageName)
                if (usageMs >= limitMs) {
                    newExhausted.add(app.packageName)
                }
            }
        }
        exhaustedApps = newExhausted
    }

    // Bypass temporal: después de resolver la intervención, dejar pasar la app por N segundos
    private var bypassPackage: String? = null
    private var bypassUntil: Long = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        
        if (packageName == "com.protas.enfocaapp") return
        
        // Ignorar la interfaz del sistema (teclado, volumen, barra de notificaciones)
        // para que la ventana de intervención no se esconda y reaparezca parpadeando.
        if (packageName.contains("systemui")) return

        val restrictedApp = activeRestrictions.find { it.packageName == packageName && it.enabled }
        if (restrictedApp != null) {
            // Verificar si tiene bypass temporal (acaba de resolver la intervención)
            if (packageName == bypassPackage && System.currentTimeMillis() < bypassUntil) {
                return // Dejar pasar, ya resolvió el reto
            }
            
            val limitMs = restrictedApp.limitMinutes * 60_000L
            
            // Si sabemos que está agotada (o límite es 0), bloqueamos
            if (limitMs == 0L || exhaustedApps.contains(packageName)) {
                // PRIMERO: Cerrar la app inmediatamente (mandarla al inicio)
                performGlobalAction(GLOBAL_ACTION_HOME)
                // DESPUÉS: Mostrar la pantalla de intervención
                overlayManager.showOverlay(
                    onClose = { performGlobalAction(GLOBAL_ACTION_HOME) },
                    onSuccess = {
                        // El usuario resolvió el reto: darle bypass temporal de 5 segundos
                        bypassPackage = packageName
                        bypassUntil = System.currentTimeMillis() + 5_000L
                    }
                )
            } else {
                // Si no está en el caché, hacemos la consulta a la base de datos.
                serviceScope.launch {
                    val usageMs = appUsageRepository.getAppUsageToday(packageName)
                    if (usageMs >= limitMs) {
                        exhaustedApps.add(packageName) // Lo metemos al caché rápido
                        launchMain {
                            performGlobalAction(GLOBAL_ACTION_HOME)
                            overlayManager.showOverlay(
                                onClose = { performGlobalAction(GLOBAL_ACTION_HOME) },
                                onSuccess = {
                                    bypassPackage = packageName
                                    bypassUntil = System.currentTimeMillis() + 5_000L
                                }
                            )
                        }
                    } else {
                        launchMain { overlayManager.hideOverlay() }
                    }
                }
            }
        } else {
            if (launcherPackages.contains(packageName) && !overlayManager.isShowing()) {
                // Esconder overlay solo si NO hay una intervención activa
                overlayManager.hideOverlay()
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
