package com.protas.enfocaapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EnfocaPrimary,
    onPrimary = EnfocaBackground,
    primaryContainer = EnfocaPrimaryContainer,
    onPrimaryContainer = EnfocaOnBackground,
    secondary = EnfocaOnSurfaceVariant,
    onSecondary = EnfocaBackground,
    background = EnfocaBackground,
    onBackground = EnfocaOnBackground,
    surface = EnfocaBackground,
    onSurface = EnfocaOnBackground,
    surfaceVariant = EnfocaSurfaceContainer,
    onSurfaceVariant = EnfocaOnSurfaceVariant,
    outline = EnfocaSurfaceContainerHighest,
    outlineVariant = EnfocaOutlineVariant,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = EnfocaLightPrimary,
    onPrimary = Color.White,
    primaryContainer = EnfocaLightPrimaryContainer,
    onPrimaryContainer = Color(0xFF001B3E),
    secondary = EnfocaLightOnSurfaceVariant,
    onSecondary = Color.White,
    background = EnfocaLightBackground,
    onBackground = EnfocaLightOnBackground,
    surface = EnfocaLightSurface,
    onSurface = EnfocaLightOnBackground,
    surfaceVariant = EnfocaLightSurfaceContainer,
    onSurfaceVariant = EnfocaLightOnSurfaceVariant,
    outline = EnfocaLightOutline,
    outlineVariant = EnfocaLightOutlineVariant,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun EnfocaAPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
}