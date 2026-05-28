package com.protas.enfocaapp.ui.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.protas.enfocaapp.ui.theme.*

// ─── Data model ───────────────────────────────────────────────────────────────

private data class PermissionItem(
    val icon: ImageVector,
    val title: String,
    val description: String
)

// ─── Main Screen ──────────────────────────────────────────────────────────────

@Composable
fun StatsScreen() {
    val permissions = listOf(
        PermissionItem(
            icon = Icons.Outlined.Layers,
            title = "Superposición de pantalla",
            description = "Necesario para mostrar alertas e intervenciones sobre otras aplicaciones."
        ),
        PermissionItem(
            icon = Icons.Outlined.Notifications,
            title = "Permiso de notificaciones",
            description = "Para enviarte recordatorios y alertas de límite alcanzado."
        ),
        PermissionItem(
            icon = Icons.Outlined.Analytics,
            title = "Análisis de datos de uso",
            description = "Permite identificar patrones de uso para personalizar tus límites."
        ),
        PermissionItem(
            icon = Icons.Outlined.BatterySaver,
            title = "Ignorar Optimización de Batería",
            description = "Evita que el sistema detenga Dopamina Guard en segundo plano."
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DopaminaBackground)
    ) {
        // Atmospheric blobs
        StatsAtmosphericBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))



            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Text(
                text = "Sincronización de Datos de Uso",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.02).em,
                    lineHeight = 38.sp
                ),
                color = DopaminaOnBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Necesitamos acceso a tus estadísticas para identificar patrones de impulsividad y activar las intervenciones.",
                style = MaterialTheme.typography.bodyLarge,
                color = DopaminaOnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Permissions list
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                permissions.forEach { item ->
                    StatsPermissionRow(item = item)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Components ───────────────────────────────────────────────────────────────

@Composable
private fun StatsAtmosphericBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Top-left blob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    DopaminaPrimaryContainer.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                radius = 480f,
                center = Offset(size.width * 0.25f, size.height * 0.25f)
            ),
            radius = 480f,
            center = Offset(size.width * 0.25f, size.height * 0.25f)
        )
        // Bottom-right blob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    DopaminaOutlineVariant.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                radius = 600f,
                center = Offset(size.width * 0.75f, size.height * 0.75f)
            ),
            radius = 600f,
            center = Offset(size.width * 0.75f, size.height * 0.75f)
        )
    }
}


@Composable
private fun StatsPermissionRow(item: PermissionItem) {
    var granted by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DopaminaSurfaceContainerLow)
            .border(
                width = 1.dp,
                color = DopaminaSurfaceContainerHigh,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = DopaminaOnSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                color = DopaminaOnBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = DopaminaOnSurfaceVariant
            )
        }

        OutlinedButton(
            onClick = { granted = true },
            enabled = !granted,
            shape = CircleShape,
            border = BorderStroke(
                width = 1.dp,
                color = if (granted) DopaminaPrimary.copy(alpha = 0.1f)
                else DopaminaPrimary.copy(alpha = 0.2f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (granted) DopaminaPrimary.copy(alpha = 0.2f)
                else DopaminaPrimary.copy(alpha = 0.1f),
                contentColor = DopaminaPrimary,
                disabledContainerColor = DopaminaPrimary.copy(alpha = 0.15f),
                disabledContentColor = DopaminaPrimary.copy(alpha = 0.5f)
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 5.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                text = if (granted) "✓" else "Permitir",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    letterSpacing = 0.05.em
                )
            )
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PreviewStatsScreen() {
    EnfocaAPPTheme {
        StatsScreen()
    }
}
