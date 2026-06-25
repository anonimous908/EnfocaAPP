package com.protas.enfocaapp.core.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.protas.enfocaapp.ui.screens.barrier.CognitiveCheckpointScreen
import com.protas.enfocaapp.ui.screens.barrier.IntentionalDelayScreen
import com.protas.enfocaapp.ui.screens.barrier.InterventionScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterventionOverlayManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SavedStateRegistryOwner {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var composeView: ComposeView? = null
    
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private var lifecycleInitialized = false
    
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry
        
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    private var onCloseCallback: (() -> Unit)? = null
    private var onSuccessCallback: (() -> Unit)? = null

    private val layoutParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY 
            else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            screenOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            // Cubrir el notch/cutout si lo hay
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
    }

    /**
     * Crea la ComposeView la primera vez que se necesita (lazy).
     * Esto evita inicializar el motor de Compose al arrancar el teléfono.
     */
    private fun ensureViewCreated(screenType: Int) {
        // Destruir la vista anterior si existe (limpieza completa)
        composeView = null
        
        if (!lifecycleInitialized) {
            savedStateRegistryController.performRestore(null)
            lifecycleInitialized = true
        }
        
        // Resetear lifecycle para la nueva vista
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        
        composeView = ComposeView(context).apply {
            // Fondo oscuro que cubre TODA la pantalla incluyendo barras del sistema
            setBackgroundColor(android.graphics.Color.parseColor("#1C1B1B"))
            setContent {
                // Padding para que el contenido no se tape con la barra de estado/navegación
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    when (screenType) {
                        0 -> CognitiveCheckpointScreen(
                            onConfirm = { hideOverlay(); onSuccessCallback?.invoke() }, 
                            onBack = { hideOverlay(); onCloseCallback?.invoke() }
                        )
                        1 -> IntentionalDelayScreen(
                            onTimerComplete = { hideOverlay(); onSuccessCallback?.invoke() }, 
                            onClose = { hideOverlay(); onCloseCallback?.invoke() }
                        )
                        2 -> InterventionScreen(
                            appName = "App Restringida",
                            onWaitAndEarn = { hideOverlay(); onCloseCallback?.invoke() }, 
                            onUnlockNow = { hideOverlay(); onSuccessCallback?.invoke() }, 
                            onClose = { hideOverlay(); onCloseCallback?.invoke() }
                        )
                    }
                }
            }
            setViewTreeLifecycleOwner(this@InterventionOverlayManager)
            setViewTreeSavedStateRegistryOwner(this@InterventionOverlayManager)
            
            // Ocultar barras del sistema en modo inmersivo
            @Suppress("DEPRECATION")
            systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
                android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    fun showOverlay(onClose: () -> Unit, onSuccess: () -> Unit = {}) {
        if (isShowing()) return // Ya hay una intervención activa
        
        onCloseCallback = onClose
        onSuccessCallback = onSuccess
        
        val screenType = (0..2).random()
        ensureViewCreated(screenType)
        
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        
        try {
            windowManager.addView(composeView, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideOverlay() {
        val view = composeView ?: return
        if (view.parent == null) return
        
        try {
            windowManager.removeView(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Detener el lifecycle para liberar recursos de Compose
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        
        composeView = null
        onCloseCallback = null
        onSuccessCallback = null
    }

    fun isShowing(): Boolean {
        return composeView?.parent != null
    }
}
