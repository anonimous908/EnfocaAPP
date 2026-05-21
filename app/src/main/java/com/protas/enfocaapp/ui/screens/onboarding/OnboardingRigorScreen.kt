package com.protas.enfocaapp.ui.screens.onboarding

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.protas.enfocaapp.R
import com.protas.enfocaapp.ui.theme.DopaminaSurfaceContainerHigh
import com.protas.enfocaapp.ui.theme.EnfocaAPPTheme

enum class NivelRigor { MODERADO, ESTRICTO, QUIRURGICO }

// 1. Modelo limpio que usa IDs numéricos de recursos en lugar de Strings quemados o vectores gráficos
data class RigorOption(
    val nivel: NivelRigor,
    @StringRes val tituloRes: Int,
    @StringRes val descripcionRes: Int
)

// 2. Lista vinculada a tus nuevos recursos de strings.xml
private val opciones = listOf(
    RigorOption(
        nivel = NivelRigor.MODERADO,
        tituloRes = R.string.rigor_moderado_title,
        descripcionRes = R.string.rigor_moderado_desc
    ),
    RigorOption(
        nivel = NivelRigor.ESTRICTO,
        tituloRes = R.string.rigor_estricto_title,
        descripcionRes = R.string.rigor_estricto_desc
    ),
    RigorOption(
        nivel = NivelRigor.QUIRURGICO,
        tituloRes = R.string.rigor_quirurgico_title,
        descripcionRes = R.string.rigor_quirurgico_desc
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp)
            ) {
                // 3. Uso de stringResource para los textos principales de la pantalla
                Text(
                    text = stringResource(id = R.string.onboarding_rigor_title),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.02).sp,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.onboarding_rigor_description),
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
                        text = stringResource(id = R.string.onboarding_rigor_btn_establish),
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

            Text(
                text = stringResource(id = opcion.tituloRes),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.01).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = opcion.descripcionRes),
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