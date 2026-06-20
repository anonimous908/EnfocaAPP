package com.protas.enfocaapp.ui.screens.barrier

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Background        = Color(0xFF131313)
private val Surface           = Color(0xFF131313)
private val SurfaceContainer  = Color(0xFF201F1F)
private val SurfaceContainerHigh = Color(0xFF2A2A2A)
private val OutlineVariant    = Color(0xFF434656)
private val Outline           = Color(0xFF8D90A2)
private val OnSurface         = Color(0xFFE5E2E1)
private val OnSurfaceVariant  = Color(0xFFC3C5D9)
private val Primary           = Color(0xFFB7C4FF)
private val PrimaryContainer  = Color(0xFF0052FF)
private val OnPrimaryContainer = Color(0xFFDFE3FF)
private val ErrorColor        = Color(0xFFFFB4AB)
private val GridLine          = Color(0xFF434656).copy(alpha = 0.15f)

private const val TOTAL_MINUTES = 15

@Composable
fun InterventionScreen(
    appName: String = "Instagram",
    onWaitAndEarn: () -> Unit = {},
    onUnlockNow: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    val totalSeconds = TOTAL_MINUTES * 60

    LaunchedEffect(Unit) {
        while (elapsedSeconds < totalSeconds) {
            delay(1_000L)
            elapsedSeconds++
        }
    }

    val progress = (elapsedSeconds.toFloat() / totalSeconds).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 2_000, easing = LinearEasing),
        label = "progress"
    )

    val minutesLeft = ((totalSeconds - elapsedSeconds) / 60).coerceAtLeast(0)
    val secondsLeft = ((totalSeconds - elapsedSeconds) % 60).coerceAtLeast(0)
    val elapsedMin  = elapsedSeconds / 60
    val elapsedSec  = elapsedSeconds % 60

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .drawTechGrid()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 360.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Surface)
                .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.Info, // Replaced Resource
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "La recompensa es mejor si esperas.",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 28.sp,
                letterSpacing = (-0.01).sp,
                color = OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(50)),
                    color = Primary,
                    trackColor = SurfaceContainerHigh,
                    strokeCap = StrokeCap.Round,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "%02d:%02d".format(elapsedMin, elapsedSec),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.08.sp,
                        color = Outline
                    )
                    Text(
                        text = "%02d:%02d".format(TOTAL_MINUTES, 0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.08.sp,
                        color = Outline
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainer)
                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Desbloquea $appName en ")
                        withStyle(SpanStyle(color = Primary, fontWeight = FontWeight.Bold)) {
                            append("%02d:%02d".format(minutesLeft, secondsLeft))
                        }
                        append(" para obtener un ")
                        withStyle(SpanStyle(color = Primary, fontWeight = FontWeight.Bold)) {
                            append("+5 bono de disciplina")
                        }
                        append(", o desbloquea ahora para recibir una ")
                        withStyle(SpanStyle(color = ErrorColor, fontWeight = FontWeight.Bold)) {
                            append("-10 penalización")
                        }
                        append(".")
                    },
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            androidx.compose.material3.Button(
                onClick = onWaitAndEarn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = PrimaryContainer,
                    contentColor = OnPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle, // Replaced Resource
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "ESPERAR Y GANAR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.1.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = onUnlockNow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "DESBLOQUEAR AHORA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.1.sp,
                    color = Outline
                )
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "SALIR DE LA APP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.1.sp,
                    color = OutlineVariant
                )
            }
        }
    }
}

private fun Modifier.drawTechGrid(): Modifier = this.drawBehind {
    val step = 32.dp.toPx()
    val cols = (size.width / step).toInt() + 1
    val rows = (size.height / step).toInt() + 1

    for (i in 0..cols) {
        val x = i * step
        drawLine(
            color = GridLine,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1f
        )
    }
    for (j in 0..rows) {
        val y = j * step
        drawLine(
            color = GridLine,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
fun InterventionScreenPreview() {
    InterventionScreen()
}
