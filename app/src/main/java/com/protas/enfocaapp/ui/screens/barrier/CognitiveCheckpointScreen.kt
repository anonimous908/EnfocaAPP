package com.protas.enfocaapp.ui.screens.barrier

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

private object CheckpointColors {
    val Background = Color(0xFF131313)
    val OnBackground = Color(0xFFE5E2E1)
    val Surface = Color(0xFF131313)
    val SurfaceContainerLow = Color(0xFF1C1B1B)
    val SurfaceContainerHigh = Color(0xFF2A2A2A)
    val SurfaceContainerHighest = Color(0xFF353534)
    val Primary = Color(0xFFB7C4FF)
    val PrimaryContainer = Color(0xFF0052FF)
    val OnPrimaryContainer = Color(0xFFDFE3FF)
    val OnSurface = Color(0xFFE5E2E1)
    val OnSurfaceVariant = Color(0xFFC3C5D9)
    val Outline = Color(0xFF8D90A2)
    val OutlineVariant = Color(0xFF434656)
    val InversePrimary = Color(0xFF004CED)
}

private const val TARGET_PHRASE = "Tengo el control de mi atención."

private fun normalize(text: String): String {
    val unaccented = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
    return unaccented.lowercase().replace(".", "").replace(",", "").trim()
}

private val normalizedTarget = normalize(TARGET_PHRASE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CognitiveCheckpointScreen(
    discipline_score: Int = 92,
    onConfirm: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    var input by remember { mutableStateOf("") }

    val normalizedInput = remember(input) { normalize(input) }
    val isMatch = remember(normalizedInput) { normalizedInput == normalizedTarget }

    val targetProgress = remember(normalizedInput, isMatch) {
        if (isMatch) 1f
        else if (normalizedInput.isEmpty()) 0f
        else (normalizedInput.length.toFloat() / normalizedTarget.length.toFloat()).coerceAtMost(0.95f)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 300),
        label = "checkpoint_progress"
    )

    val statusColor = if (isMatch) CheckpointColors.Primary else CheckpointColors.OutlineVariant
    val statusText = when {
        isMatch -> "Frase verificada."
        input.isNotEmpty() -> "Verificando..."
        else -> "Esperando texto..."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CheckpointColors.Background)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(CheckpointColors.SurfaceContainerLow)
                        .border(
                            width = 1.dp,
                            color = CheckpointColors.OutlineVariant,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = CheckpointColors.Primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PUNTOS DE DISCIPLINA",
                        style = TextStyle(
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = CheckpointColors.OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = discipline_score.toString(),
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
                        color = CheckpointColors.Primary
                    )
                }

                Text(
                    text = "Punto de Control Cognitivo",
                    style = TextStyle(
                        fontSize = 32.sp,
                        lineHeight = 38.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.02).sp
                    ),
                    color = CheckpointColors.OnSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Escribe la frase de compromiso para proceder y desbloquear tu sesión.",
                    style = TextStyle(fontSize = 18.sp, lineHeight = 28.sp),
                    color = CheckpointColors.OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CheckpointColors.SurfaceContainerLow)
                    .border(1.dp, CheckpointColors.OutlineVariant, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "FRASE DE COMPROMISO",
                        style = TextStyle(fontSize = 12.sp, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold),
                        color = CheckpointColors.Outline
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                            .background(CheckpointColors.SurfaceContainerHigh.copy(alpha = 0.3f))
                    ) {
                        Row {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .fillMaxHeight()
                                    .background(CheckpointColors.PrimaryContainer)
                            )
                            Text(
                                text = "“Tengo el control de mi atención.”",
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    lineHeight = 32.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = CheckpointColors.OnSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        placeholder = { Text("Escribe la frase exactamente...", color = CheckpointColors.Outline) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        trailingIcon = {
                            Icon(
                                imageVector = when {
                                    isMatch -> Icons.Filled.CheckCircle
                                    input.isNotEmpty() -> Icons.Filled.MoreHoriz
                                    else -> Icons.Filled.Edit
                                },
                                contentDescription = null,
                                tint = statusColor
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CheckpointColors.Surface,
                            unfocusedContainerColor = CheckpointColors.Surface,
                            focusedBorderColor = if (isMatch) CheckpointColors.PrimaryContainer else CheckpointColors.PrimaryContainer,
                            unfocusedBorderColor = CheckpointColors.OutlineVariant,
                            focusedTextColor = CheckpointColors.OnSurface,
                            unfocusedTextColor = CheckpointColors.OnSurface,
                            cursorColor = CheckpointColors.Primary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(CheckpointColors.SurfaceContainerHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedProgress)
                                .clip(RoundedCornerShape(50))
                                .background(CheckpointColors.PrimaryContainer)
                        )
                    }

                    Text(
                        text = statusText,
                        style = TextStyle(fontSize = 12.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.SemiBold),
                        color = statusColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Button(
                    onClick = onConfirm,
                    enabled = isMatch,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CheckpointColors.PrimaryContainer,
                        contentColor = CheckpointColors.OnPrimaryContainer,
                        disabledContainerColor = CheckpointColors.SurfaceContainerHighest,
                        disabledContentColor = CheckpointColors.OnSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LockOpen,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CONFIRMAR INTENCIÓN",
                        style = TextStyle(fontSize = 12.sp, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            TextButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = CheckpointColors.OutlineVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Volver al Inicio",
                    style = TextStyle(fontSize = 12.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.SemiBold),
                    color = CheckpointColors.OutlineVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun CognitiveCheckpointScreenPreview() {
    MaterialTheme {
        CognitiveCheckpointScreen()
    }
}
