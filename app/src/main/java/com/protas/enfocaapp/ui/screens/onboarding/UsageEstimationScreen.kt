package com.protas.enfocaapp.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.res.stringResource
import com.protas.enfocaapp.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageEstimationScreen(
    onPermissionGranted: (Int) -> Unit = {},
    onOmitir: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var sliderValue by remember { mutableFloatStateOf(4f) }
    val hours = sliderValue.roundToInt()

    // ── BottomSheet state ──────────────────────────────────────────────────
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    var triggerScale by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (triggerScale) 1.12f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        finishedListener = { triggerScale = false },
        label = "numberScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Ambient glow
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-60).dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.03f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        // Botón flotante para regresar a la página anterior
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 24.dp) // SafeArea padding
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.onboarding_btn_back),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 64.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_usage_title),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.02).em,
                    lineHeight = 38.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.onboarding_usage_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.weight(1f))

            // Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$hours",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 64.sp,
                            lineHeight = 64.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "h",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    GradientSlider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it; triggerScale = true },
                        valueRange = 1f..12f,
                        steps = 10
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(id = R.string.onboarding_usage_1h), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(stringResource(id = R.string.onboarding_usage_12h), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                val conscious = stringResource(id = R.string.onboarding_level_conscious)
                val average = stringResource(id = R.string.onboarding_level_average)
                val high = stringResource(id = R.string.onboarding_level_high)
                val critical = stringResource(id = R.string.onboarding_level_critical)

                val (feedbackText, feedbackColor) = remember(hours, conscious, average, high, critical) {
                    when {
                        hours <= 2  -> conscious  to Color(0xFFC6C6C7)
                        hours <= 5  -> average    to Color(0xFF8D90A2)
                        hours <= 8  -> high       to Color(0xFFFFB4AB)
                        else        -> critical   to Color(0xFFFF6B6B)
                    }
                }
                AnimatedContent(
                    targetState = feedbackText,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label = "feedbackAnim"
                ) { text ->
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.05.em),
                        color = feedbackColor
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // ── CTA → abre el BottomSheet ──────────────────────────────────
            Button(
                onClick = { showSheet = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_btn_confirm),
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, modifier = Modifier.size(20.dp))
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.onboarding_usage_compare_text),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.05.em),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }

    // ── ModalBottomSheet ────────────────────────────────────────────────────
    if (showSheet) {
        RealityAnalysisSheet(
            sheetState = sheetState,
            onDismiss = { showSheet = false },
            onOmitir = {
                showSheet = false
                onOmitir()
            },
            onConfirm = {
                showSheet = false
                onPermissionGranted(hours)
            }
        )
    }
}

// TODO: Estos son componentes de reemplazo temporal porque no estaban en el snippet.
@Composable
fun GradientSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primaryContainer,
            activeTrackColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealityAnalysisSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onOmitir: () -> Unit = {},
    onConfirm: () -> Unit
) {
    // Pulsing glow animation para el ícono
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        dragHandle = {
            // Handle personalizado
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(48.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            )
        },
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Ícono con pulse ────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(64.dp)
            ) {
                // Anillo de pulso externo
                Box(
                    modifier = Modifier
                        .size((64 + pulseRadius).dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = pulseAlpha * 0.4f))
                )
                // Círculo base del ícono
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BarChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Copy ──────────────────────────────────────────────────────
            Text(
                text = stringResource(id = R.string.onboarding_usage_sheet_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.01).em
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.onboarding_usage_sheet_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.onboarding_usage_sheet_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // ── Botón primario ─────────────────────────────────────────────
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_btn_discover_truth),
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.05.em, fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Botón secundario ───────────────────────────────────────────
            OutlinedButton(
                onClick = { onDismiss(); onOmitir() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_btn_skip),
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.05.em, fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
