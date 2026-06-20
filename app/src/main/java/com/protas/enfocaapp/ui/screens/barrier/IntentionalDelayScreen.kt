package com.protas.enfocaapp.ui.screens.barrier

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Background = Color(0xFF131313)
private val SurfaceContainer = Color(0xFF201F1F)
private val SurfaceContainerLow = Color(0xFF1C1B1B)
private val SurfaceContainerHighest = Color(0xFF353534)
private val OutlineVariant = Color(0xFF434656)
private val Primary = Color(0xFFB7C4FF)
private val OnSurface = Color(0xFFE5E2E1)
private val OnSurfaceVariant = Color(0xFFC3C5D9)
private val Secondary = Color(0xFFC6C6C7)
private val Error = Color(0xFFFFB4AB)
private val ErrorContainer = Color(0xFF93000A)

@Composable
fun IntentionalDelayScreen(
    totalSeconds: Int = 20,
    impulsivityCount: Int = 42,
    onTimerComplete: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    var timeLeft by remember { mutableIntStateOf(totalSeconds) }
    var isComplete by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        isComplete = true
        onTimerComplete()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ring_pulse")
    val ringColor by infiniteTransition.animateColor(
        initialValue = OutlineVariant,
        targetValue = Primary,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring_color"
    )

    val borderColor = if (isComplete) OutlineVariant else ringColor
    val numberColor = if (isComplete) OnSurface else Primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(36.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Retraso Intencional",
            color = OnSurface,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .background(ErrorContainer.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .border(1.dp, ErrorContainer.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Error,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Alta impulsividad detectada: $impulsivityCount intentos hoy.",
                color = Error,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(256.dp)
                .shadow(0.dp, CircleShape)
                .background(SurfaceContainerLow, CircleShape)
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$timeLeft",
                    color = numberColor,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 80.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "SEGUNDOS",
                    color = OnSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainer, RoundedCornerShape(16.dp))
                .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(SurfaceContainerHighest, CircleShape)
                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.MonitorHeart,
                    contentDescription = null,
                    tint = Secondary
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = "ESTADO BIOMÉTRICO",
                    color = OnSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (timeLeft > 0) "Frecuencia cardíaca alta. Toma un respiro." else "Ventana de enfoque restaurada. Procede con intención.",
                    color = OnSurface,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = if (isComplete) "Ventana de enfoque restaurada. Procede con intención." else "Espera a que termine el temporizador o bloquea tu teléfono para recuperar el enfoque.",
            color = if (isComplete) Primary else OnSurfaceVariant,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(24.dp))

        TextButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = OutlineVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Salir",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = OutlineVariant
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun IntentionalDelayScreenPreview() {
    IntentionalDelayScreen()
}
