package com.protas.enfocaapp.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.protas.enfocaapp.ui.theme.EnfocaAPPTheme

// ─── Pantalla ─────────────────────────────────────────────────────────────────
@Composable
fun OnboardingWelcomeScreen(
    onNextClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Fondo radial sutil (equivalente al radial-gradient del HTML)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Visualizador técnico con anillos pulsantes ──
            BiometricVisualizer()

            Spacer(modifier = Modifier.height(32.dp))

            // ── Texto ──
            Text(
                text = "Enfocapp",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-0.02).sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tu tiempo te pertenece.\n Tómalo de vuelta.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }

        // ── Botón fijo en la parte inferior ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Siguiente",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ─── Anillos pulsantes + ícono de huella ──────────────────────────────────────
@Composable
private fun BiometricVisualizer() {
    Box(
        modifier = Modifier.size(256.dp),
        contentAlignment = Alignment.Center
    ) {
        // Líneas de mira (crosshairs)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                .align(Alignment.Center)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                .align(Alignment.Center)
        )

        // Tres anillos con delay escalonado (tech-pulse-1, 2, 3)
        PulsingRing(delayMillis = 0)
        PulsingRing(delayMillis = 1000)
        PulsingRing(delayMillis = 2000)

        // Hub central con ícono
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Línea de escaneo animada sobre el ícono
            ScanningOverlay()

            // Ícono de huella (usa tu propio drawable o material icon)
            // Si tienes material-icons-extended en el proyecto:
            // Icon(Icons.Outlined.Fingerprint, ...)
            // Si no, reemplaza con un Icon de tu drawable:
            Text(
                text = "fingerprint",        // placeholder — reemplaza con Icon real
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                fontSize = 48.sp
                // En producción usa:
                // Icon(painter = painterResource(R.drawable.ic_fingerprint), ...)
            )
        }
    }
}

// ─── Anillo pulsante individual ───────────────────────────────────────────────
@Composable
private fun PulsingRing(delayMillis: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_$delayMillis")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                delayMillis = delayMillis,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale_$delayMillis"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                delayMillis = delayMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha_$delayMillis"
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .scale(scale)
            .alpha(alpha)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha),
                shape = CircleShape
            )
    )
}

// ─── Línea de escaneo ─────────────────────────────────────────────────────────
@Composable
private fun ScanningOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanY"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .align(Alignment.Center)
                .offset(y = (offsetY * 48).dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PreviewOnboardingWelcomeScreen() {
    EnfocaAPPTheme {
        OnboardingWelcomeScreen()
    }
}
