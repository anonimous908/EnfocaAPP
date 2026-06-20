package com.protas.enfocaapp.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.res.stringResource
import com.protas.enfocaapp.R
import com.protas.enfocaapp.core.model.TodayUsageStats

@Composable
fun RealityRevealScreen(
    estimatedHours: Int = 3,
    realUsageStats: TodayUsageStats = TodayUsageStats(0, 0),
    onContinue: () -> Unit = {}
) {
    // ── Staggered entry animations ─────────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val headerAlpha   by animateFloatAsState(if (visible) 1f else 0f, tween(800, 0),   label = "h")
    val headerOffset  by animateFloatAsState(if (visible) 0f else 10f, tween(800, 0),  label = "ho")
    val cardsAlpha    by animateFloatAsState(if (visible) 1f else 0f, tween(800, 150), label = "c")
    val cardsOffset   by animateFloatAsState(if (visible) 0f else 10f, tween(800, 150),label = "co")
    val ctaAlpha      by animateFloatAsState(if (visible) 1f else 0f, tween(800, 300), label = "a")
    val ctaOffset     by animateFloatAsState(if (visible) 0f else 10f, tween(800, 300),label = "ao")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(vertical = 32.dp)
                .widthIn(max = 480.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Header ─────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .graphicsLayer { alpha = headerAlpha; translationY = headerOffset.dp.toPx() }
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_reveal_title, estimatedHours),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Light,
                        letterSpacing = (-0.02).em,
                        lineHeight = 38.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Medium
                            )
                        ) { append(stringResource(id = R.string.onboarding_reveal_subtitle_1)) }
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primaryContainer)) {
                            append(realUsageStats.formattedTime)
                        }
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Medium
                            )
                        ) { append(stringResource(id = R.string.onboarding_reveal_subtitle_2)) }
                    },
                    style = MaterialTheme.typography.displaySmall.copy(
                        letterSpacing = (-0.02).em,
                        lineHeight = 38.sp
                    )
                )
            }

            // ── Data Cards ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .graphicsLayer { alpha = cardsAlpha; translationY = cardsOffset.dp.toPx() }
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Time Card
                TimeCard(hours = realUsageStats.hours, minutes = realUsageStats.minutes)
                // Unlocks Card
                UnlocksCard(count = realUsageStats.unlockCount)
            }

            Spacer(Modifier.height(32.dp))

            // ── CTA ────────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .graphicsLayer { alpha = ctaAlpha; translationY = ctaOffset.dp.toPx() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.onboarding_btn_face_reality),
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.onboarding_reveal_cta_desc),
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.05.em,
                        fontWeight = FontWeight.Light
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ─── Time Card ─────────────────────────────────────────────────────────────────

@Composable
private fun TimeCard(hours: Int, minutes: Int) {
    // Bar heights as fractions (decorative mini-chart)
    val barFractions = listOf(0.25f, 0.50f, 0.33f, 0.75f, 1.00f)
    val activeColor  = MaterialTheme.colorScheme.primaryContainer
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(192.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Label
        Text(
            text = stringResource(id = R.string.onboarding_reveal_time_usage),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.1.em),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.TopStart)
        )

        // Big number with minutes
        Row(
            modifier = Modifier.align(Alignment.BottomStart),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "${hours}h ${minutes}m",
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 56.sp,
                    lineHeight = 56.sp,
                    letterSpacing = (-0.03).em
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.onboarding_reveal_today),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Decorative mini bar chart (bottom-right)
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
                .height(80.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            barFractions.forEachIndexed { index, fraction ->
                val isLast = index == barFractions.lastIndex
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight(fraction)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isLast) activeColor else inactiveColor)
                        .then(
                            if (isLast) Modifier.shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(2.dp),
                                ambientColor = activeColor.copy(alpha = 0.6f),
                                spotColor = activeColor.copy(alpha = 0.6f)
                            ) else Modifier
                        )
                )
            }
        }
    }
}

// ─── Unlocks Card ──────────────────────────────────────────────────────────────

@Composable
private fun UnlocksCard(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(id = R.string.onboarding_reveal_unlocks),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.1.em),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$count",
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    lineHeight = 32.sp,
                    letterSpacing = (-0.02).em
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
