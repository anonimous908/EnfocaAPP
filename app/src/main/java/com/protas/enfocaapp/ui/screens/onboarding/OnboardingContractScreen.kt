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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.protas.enfocaapp.ui.theme.EnfocaAPPTheme
import kotlinx.coroutines.delay
import androidx.compose.ui.res.stringResource
import com.protas.enfocaapp.R

@Composable
fun OnboardingContractScreen(
    onFirmarContrato: () -> Unit = {},
    onAtras: () -> Unit = {}
) {
    var firmado by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showTermsOfService by remember { mutableStateOf(false) }

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
                Text(
                    text = stringResource(id = R.string.onboarding_contract_title),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.02).sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.onboarding_contract_subtitle),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Checkbox y textos legales
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
                    val annotatedText = buildAnnotatedString {
                        withStyle(SpanStyle(color = textColor)) {
                            append(stringResource(id = R.string.onboarding_contract_terms_1))
                        }
                        pushStringAnnotation(tag = "privacy", annotation = "privacy")
                        withStyle(SpanStyle(color = primaryColor, fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.onboarding_contract_privacy_policy))
                        }
                        pop()
                        withStyle(SpanStyle(color = textColor)) {
                            append(stringResource(id = R.string.onboarding_contract_terms_2))
                        }
                        pushStringAnnotation(tag = "terms", annotation = "terms")
                        withStyle(SpanStyle(color = primaryColor, fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.onboarding_contract_terms_of_service))
                        }
                        pop()
                        withStyle(SpanStyle(color = textColor)) {
                            append(stringResource(id = R.string.onboarding_contract_terms_3))
                        }
                    }
                    ClickableText(
                        text = annotatedText,
                        style = TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
                        onClick = { offset ->
                            annotatedText.getStringAnnotations(tag = "privacy", start = offset, end = offset)
                                .firstOrNull()?.let { showPrivacyPolicy = true }
                            annotatedText.getStringAnnotations(tag = "terms", start = offset, end = offset)
                                .firstOrNull()?.let { showTermsOfService = true }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FirmaButton(
                    firmado = firmado,
                    enabled = termsAccepted,
                    onFirmado = { firmado = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                val btnColor by animateColorAsState(
                    targetValue = if (firmado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
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
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = btnColor,
                        contentColor = btnTextColor,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.onboarding_contract_btn_sign),
                        fontSize = 14.sp,
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

        if (showPrivacyPolicy) {
            LegalDialog(
                title = stringResource(id = R.string.onboarding_contract_dialog_privacy_title),
                text = stringResource(id = R.string.onboarding_contract_dialog_privacy_text),
                onDismiss = { showPrivacyPolicy = false }
            )
        }

        if (showTermsOfService) {
            LegalDialog(
                title = stringResource(id = R.string.onboarding_contract_dialog_terms_title),
                text = stringResource(id = R.string.onboarding_contract_dialog_terms_text),
                onDismiss = { showTermsOfService = false }
            )
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
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onAtras) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(id = R.string.onboarding_btn_back),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = (-0.01).sp,
            modifier = Modifier.padding(end = 48.dp) // Compensar visualmente el IconButton
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}


@Composable
private fun FirmaButton(
    firmado: Boolean,
    enabled: Boolean = true,
    onFirmado: () -> Unit
) {
    // Usamos Animatable para una gestión de hilos y frames nativa y eficiente
    val progreso = remember { Animatable(0f) }
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
            // Animación fluida de 0 a 1 sincronizada con los Hz de la pantalla
            progreso.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
            if (progreso.value >= 1f) {
                onFirmado()
            }
        } else if (!presionando && !firmado) {
            // Retorno fluido a la posición inicial si se interrumpe la presión
            progreso.animateTo(0f, tween(durationMillis = 200))
        }
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(escala)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background)
            .border(2.dp, borderColor, CircleShape)
            .pointerInput(firmado, enabled) {
                if (!firmado && enabled) {
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
                    sweepAngle = 360f * progreso.value, // Lectura optimizada del valor animado
                    useCenter = false,
                    style = stroke
                )
            }
        }

        if (firmado) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = stringResource(id = R.string.onboarding_contract_signed),
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
                    text = if (presionando) stringResource(id = R.string.onboarding_contract_signing) else stringResource(id = R.string.onboarding_contract_hold),
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

@Composable
private fun LegalDialog(title: String, text: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = text,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Justify,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.onboarding_contract_btn_understood), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
