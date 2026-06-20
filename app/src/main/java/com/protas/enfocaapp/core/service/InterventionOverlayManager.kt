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
import androidx.compose.runtime.remember
import com.protas.enfocaapp.ui.screens.intervention.SimpleBlockOverlay
import com.protas.enfocaapp.ui.screens.barrier.CognitiveCheckpointScreen
import com.protas.enfocaapp.ui.screens.barrier.IntentionalDelayScreen
import com.protas.enfocaapp.ui.screens.barrier.InterventionScreen
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
    
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry
        
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    fun showOverlay(onClose: () -> Unit) {
        if (composeView != null) return

        composeView = ComposeView(context).apply {
            setContent {
                val screenType = remember { (0..2).random() }
                when (screenType) {
                    0 -> CognitiveCheckpointScreen(
                        onConfirm = { hideOverlay(); onClose() },
                        onBack = { hideOverlay(); onClose() }
                    )
                    1 -> IntentionalDelayScreen(
                        onTimerComplete = { hideOverlay(); onClose() },
                        onClose = { hideOverlay(); onClose() }
                    )
                    2 -> InterventionScreen(
                        appName = "App Restringida",
                        onWaitAndEarn = { hideOverlay(); onClose() },
                        onUnlockNow = { hideOverlay(); onClose() },
                        onClose = { hideOverlay(); onClose() }
                    )
                }
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY 
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER

        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        composeView?.setViewTreeLifecycleOwner(this)
        composeView?.setViewTreeSavedStateRegistryOwner(this)

        try {
            windowManager.addView(composeView, params)
        } catch (e: Exception) {
            e.printStackTrace()
            composeView = null
        }
    }

    fun hideOverlay() {
        composeView?.let {
            try {
                windowManager.removeView(it)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            composeView = null
        }
    }
}
