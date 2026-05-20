package com.protas.enfocaapp.ui.screens.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.protas.enfocaapp.ui.theme.DopaminaSurfaceContainerHighest
import com.protas.enfocaapp.ui.theme.DopaminaSurfaceContainerLow
import com.protas.enfocaapp.ui.theme.EnfocaAPPTheme
import kotlinx.coroutines.delay

@Composable
fun OnboardingContractScreen(
    onFirmarContrato: () -> Unit = {},
    onAtras: () -> Unit = {}
) {
    var firmado by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            ContratoHeader(onAtras = onAtras)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Gavel,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Contrato de Compromiso",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.02).sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Un pacto formal con tu futuro yo.",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                ContratoCard()

                Spacer(modifier = Modifier.height(32.dp))

                FirmaButton(
                    firmado = firmado,
                    onFirmado = { firmado = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                val btnColor by animateColorAsState(
                    targetValue = if (firmado) MaterialTheme.colorScheme.primaryContainer else DopaminaSurfaceContainerHighest,
                    animationSpec = tween(300),
                    label = "btnColor"
                )
                val btnTextColor by animateColorAsState(
                    targetValue = if (firmado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(300),
                    label = "btnTextColor"
                )

                Button(
                    onClick = { if (firmado) onFirmarContrato() },
                    enabled = firmado,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = btnColor,
                        contentColor = btnTextColor,
                        disabledContainerColor = DopaminaSurfaceContainerHighest,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = "FIRMAR CONTRATO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (firmado)
                            Icons.AutoMirrored.Outlined.ArrowForward
                        else
                            Icons.Outlined.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ContratoHeader(onAtras: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {


        Text(
            text = "Enfocapp",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = (-0.01).sp
        )

        Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
private fun ContratoCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DopaminaSurfaceContainerLow)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "POLÍTICA DE PRIVACIDAD RADICAL",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.1.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = buildAnnotatedString {
                append("En ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)) {
                    append("Enfocapp")
                }
                append(", tu privacidad no es negociable. No usamos, no vendemos ni recolectamos tus datos personales. Toda tu información de uso se procesa localmente en tu dispositivo y nunca sale de él. Tu disciplina es un asunto privado.")
            },
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        val puntos = listOf(
            "Procesamiento On-Device" to "Los algoritmos de análisis de comportamiento operan exclusivamente en el hardware de tu teléfono.",
            "Cifrado de Extremo a Extremo" to "Cualquier estadística generada está protegida por llaves de seguridad que solo tú posees.",
            "Transparencia Total" to "No existen rastreadores de terceros ni integración con redes publicitarias.",
            "Propiedad de Datos" to "Tú eres el único dueño de tu historial de enfoque. Puedes borrarlo permanentemente en cualquier momento."
        )

        puntos.forEach { (titulo, desc) ->
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Text("• ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)) {
                            append("$titulo: ")
                        }
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                            append(desc)
                        }
                    },
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp)
                )
                .padding(start = 12.dp)
        ) {
            Text(
                text = "\"La disciplina es el puente entre las metas y los logros.\"",
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
private fun FirmaButton(
    firmado: Boolean,
    onFirmado: () -> Unit
) {
    var progreso by remember { mutableFloatStateOf(0f) }
    var presionando by remember { mutableStateOf(false) }

    val escala by animateFloatAsState(
        targetValue = if (presionando) 0.95f else 1f,
        label = "escala"
    )
    val borderColor by animateColorAsState(
        targetValue = if (firmado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        label = "border"
    )

    LaunchedEffect(presionando) {
        if (presionando && !firmado) {
            while (presionando && progreso < 1f) {
                progreso += 0.02f
                delay(20)
                if (progreso >= 1f) {
                    onFirmado()
                    presionando = false
                }
            }
        } else if (!presionando) {
            if (progreso < 1f) progreso = 0f
        }
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(escala)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background)
            .border(2.dp, borderColor, CircleShape)
            .pointerInput(firmado) {
                if (!firmado) {
                    detectTapGestures(
                        onPress = {
                            presionando = true
                            tryAwaitRelease()
                            presionando = false
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (!firmado) {
            val primaryColor = MaterialTheme.colorScheme.primary
            Canvas(modifier = Modifier.size(120.dp)) {
                val stroke = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progreso,
                    useCenter = false,
                    style = stroke
                )
            }
        }

        if (firmado) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Firmado",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.Gavel,
                    contentDescription = null,
                    tint = if (presionando) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (presionando) "Firmando..." else "Mantén\npulsado",
                    fontSize = 11.sp,
                    color = if (presionando) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 14.sp,
                    letterSpacing = 0.05.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PreviewOnboardingContractScreen() {
    EnfocaAPPTheme {
        OnboardingContractScreen()
    }
}
