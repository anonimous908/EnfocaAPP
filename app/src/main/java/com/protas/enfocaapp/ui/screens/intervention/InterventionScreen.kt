package com.protas.enfocaapp.ui.screens.intervention

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.protas.enfocaapp.R

// ============================================================================
// COLOR SCHEME
// Paleta extraída directamente de los tokens del HTML (M3 dark "cognitivo").
// ============================================================================

private val InterventionColorScheme = darkColorScheme(
    primary = Color(0xFFB7C4FF),
    onPrimary = Color(0xFF002682),
    primaryContainer = Color(0xFF0052FF),
    onPrimaryContainer = Color(0xFFDFE3FF),
    inversePrimary = Color(0xFF004CED),
    secondary = Color(0xFFC6C6C7),
    onSecondary = Color(0xFF2F3131),
    secondaryContainer = Color(0xFF454747),
    onSecondaryContainer = Color(0xFFB4B5B5),
    tertiary = Color(0xFFC8C6C5),
    onTertiary = Color(0xFF313030),
    tertiaryContainer = Color(0xFF676666),
    onTertiaryContainer = Color(0xFFE7E4E4),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF131313),
    onBackground = Color(0xFFE5E2E1),
    surface = Color(0xFF131313),
    onSurface = Color(0xFFE5E2E1),
    surfaceVariant = Color(0xFF353534),
    onSurfaceVariant = Color(0xFFC3C5D9),
    surfaceTint = Color(0xFFB7C4FF),
    inverseSurface = Color(0xFFE5E2E1),
    inverseOnSurface = Color(0xFF313030),
    outline = Color(0xFF8D90A2),
    outlineVariant = Color(0xFF434656),
    surfaceContainer = Color(0xFF201F1F),
    surfaceContainerLow = Color(0xFF1C1B1B),
    surfaceContainerHigh = Color(0xFF2A2A2A),
    surfaceContainerHighest = Color(0xFF353534),
    surfaceContainerLowest = Color(0xFF0E0E0E),
    surfaceBright = Color(0xFF3A3939),
    surfaceDim = Color(0xFF131313),
)

// ============================================================================
// TYPOGRAPHY
// Equivalentes a los fontSize personalizados del config de Tailwind.
// ============================================================================

private val DisplayLgMobile = TextStyle(
    fontSize = 32.sp,
    lineHeight = 38.sp,
    letterSpacing = (-0.02).em,
    fontWeight = FontWeight.Bold,
)

private val HeadlineMd = TextStyle(
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = (-0.01).em,
    fontWeight = FontWeight.SemiBold,
)

private val BodyLg = TextStyle(
    fontSize = 18.sp,
    lineHeight = 28.sp,
    fontWeight = FontWeight.Normal,
)

private val BodyMd = TextStyle(
    fontSize = 16.sp,
    lineHeight = 24.sp,
    fontWeight = FontWeight.Normal,
)

private val LabelSm = TextStyle(
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.05.em,
    fontWeight = FontWeight.SemiBold,
)

// ============================================================================
// STATE
// Lo que normalmente vendría del ViewModel (datos dinámicos del mensaje).
// ============================================================================

data class FutureMessageInterventionState(
    val futureSelfImageUrl: String? = null,
    val minutesAhead: Int = 15,
    val message: String = "Tu yo del futuro se arrepentirá de estos 15 minutos. " +
        "El golpe de dopamina es temporal, la interrupción del progreso es permanente. " +
        "Mantén el rumbo.",
    val riskLabel: String = "Riesgo Alto",
    val regretIndexPercent: Int = -24,
    val productivityDropHours: Double = 1.5,
    val warningMessage: String = "La rendición inmediata comprometerá tu arquitectura " +
        "cognitiva a largo plazo.",
)

// ============================================================================
// SCREEN
// ============================================================================

/**
 * Pantalla de intervención mostrada cuando el usuario intenta abrir una app
 * bloqueada durante un bloque de enfoque (overlay de AccessibilityService).
 *
 * Usa su propio [MaterialTheme] para mantener la identidad visual de "modo
 * intervención" independiente del tema general de la app.
 */
@Composable
fun FutureMessageInterventionScreen(
    state: FutureMessageInterventionState = FutureMessageInterventionState(),
    onFollowAdvice: () -> Unit,
    onIgnoreAndContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MaterialTheme(colorScheme = InterventionColorScheme) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.background,
                            ),
                            radius = 900f,
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    HeaderSection()

                    Spacer(modifier = Modifier.height(32.dp))

                    FutureSelfProjectionCard(
                        imageUrl = state.futureSelfImageUrl,
                        minutesAhead = state.minutesAhead,
                        message = state.message,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FutureRegretMetricCard(
                        riskLabel = state.riskLabel,
                        regretIndexPercent = state.regretIndexPercent,
                        productivityDropHours = state.productivityDropHours,
                        warningMessage = state.warningMessage,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    ActionButtons(
                        onFollowAdvice = onFollowAdvice,
                        onIgnoreAndContinue = onIgnoreAndContinue,
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------

@Composable
private fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Psychology,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = stringResource(R.string.intervention_header_title),
            style = DisplayLgMobile,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.intervention_header_subtitle),
            style = BodyLg,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// ----------------------------------------------------------------------------

@Composable
private fun FutureSelfProjectionCard(
    imageUrl: String?,
    minutesAhead: Int,
    message: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                FutureSelfAvatar(imageUrl = imageUrl)

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(R.string.intervention_incoming_transmission),
                        style = LabelSm,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(R.string.intervention_from_future_format, minutesAhead),
                        style = BodyMd,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(IntrinsicSize.Min),
                ) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.intervention_message_format, message),
                        style = BodyLg,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------

@Composable
private fun FutureSelfAvatar(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.intervention_future_projection_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(4.dp) // blur-sm — requiere API 31+
                    .alpha(0.7f), // opacity-70
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .alpha(0.7f),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------

@Composable
private fun FutureRegretMetricCard(
    riskLabel: String,
    regretIndexPercent: Int,
    productivityDropHours: Double,
    warningMessage: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.TrendingDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = stringResource(R.string.intervention_projected_trajectory),
                        style = LabelSm,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = riskLabel,
                        style = LabelSm,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = stringResource(R.string.intervention_regret_index),
                        style = BodyMd,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(R.string.intervention_regret_index_format, regretIndexPercent),
                        style = DisplayLgMobile,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stringResource(R.string.intervention_productivity_drop),
                        style = BodyMd,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                    )
                    Text(
                        text = stringResource(R.string.intervention_productivity_drop_format, productivityDropHours.toString()),
                        style = HeadlineMd,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = warningMessage,
                    style = LabelSm,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------

@Composable
private fun ActionButtons(
    onFollowAdvice: () -> Unit,
    onIgnoreAndContinue: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onFollowAdvice,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.intervention_follow_advice), style = LabelSm)
        }

        OutlinedButton(
            onClick = onIgnoreAndContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(2.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.intervention_ignore_continue), style = LabelSm)
        }
    }
}

// ----------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun FutureMessageInterventionScreenPreview() {
    FutureMessageInterventionScreen(
        onFollowAdvice = {},
        onIgnoreAndContinue = {},
    )
}
