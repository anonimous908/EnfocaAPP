package com.protas.enfocaapp.ui.screens.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.protas.enfocaapp.ui.theme.DopaminaSurfaceContainerHigh
import com.protas.enfocaapp.ui.theme.EnfocaAPPTheme

enum class NivelRigor { MODERADO, ESTRICTO, QUIRURGICO }

data class RigorOption(
    val nivel: NivelRigor,
    val icon: ImageVector,
    val titulo: String,
    val descripcion: String
)

private val opciones = listOf(
    RigorOption(
        nivel = NivelRigor.MODERADO,
        icon = Icons.Outlined.Tune,
        titulo = "Moderado",
        descripcion = "Fricción sutil. Recordatorios gentiles "
    ),
    RigorOption(
        nivel = NivelRigor.ESTRICTO,
        icon = Icons.Outlined.Shield,
        titulo = "Estricto",
        descripcion = "Límites firmes. Retrasos obligatorios al abrir apps problemáticas "
    ),
    RigorOption(
        nivel = NivelRigor.QUIRURGICO,
        icon = Icons.Outlined.Psychology,
        titulo = "Quirúrgico",
        descripcion = "Fricción agresiva y bloqueos totales. "
    )
)

@Composable
fun OnboardingRigorScreen(
    onEstablecerRigor: (NivelRigor) -> Unit = {}
) {
    var seleccionado by remember { mutableStateOf(NivelRigor.ESTRICTO) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp)
            ) {
                Text(
                    text = "Nivel de Rigor",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.02).sp,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Selecciona la intensidad de la intervención del sistema puedes cambiarlo despues ",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                opciones.forEach { opcion ->
                    RigorCard(
                        opcion = opcion,
                        isSelected = seleccionado == opcion.nivel,
                        onClick = { seleccionado = opcion.nivel }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                    .border(
                        width = 1.dp,
                        color = DopaminaSurfaceContainerHigh,
                        shape = RoundedCornerShape(0.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = { onEstablecerRigor(seleccionado) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "ESTABLECER RIGOR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Spacer(modifier = Modifier.width(24.dp))
    }
}

@Composable
private fun RigorCard(
    opcion: RigorOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else DopaminaSurfaceContainerHigh,
        animationSpec = tween(200),
        label = "border_${opcion.nivel}"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "bg_${opcion.nivel}"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "icon_${opcion.nivel}"
    )
    val dotScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.5f,
        animationSpec = tween(200),
        label = "dot_${opcion.nivel}"
    )
    val dotAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(200),
        label = "dotAlpha_${opcion.nivel}"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = opcion.titulo,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.01).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = opcion.descripcion,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PreviewOnboardingRigorScreen() {
    EnfocaAPPTheme {
        OnboardingRigorScreen()
    }
}
