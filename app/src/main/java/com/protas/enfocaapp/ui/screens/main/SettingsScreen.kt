package com.protas.enfocaapp.ui.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.protas.enfocaapp.R

// ══════════════════════════════════════════════════════════════════════════════
// PALETA DE COLORES  (tokens del diseño original)
// ══════════════════════════════════════════════════════════════════════════════
private object C {
    val Bg              = Color(0xFF131313)
    val SurfCont        = Color(0xFF201F1F)
    val SurfContHigh    = Color(0xFF2A2A2A)
    val SurfContHighest = Color(0xFF353534)
    val SurfContLow     = Color(0xFF1C1B1B)
    val OnSurf          = Color(0xFFE5E2E1)
    val OnSurfVar       = Color(0xFFC3C5D9)
    val Primary         = Color(0xFFB7C4FF)
    val OutlineVar      = Color(0xFF434656)
    val Error           = Color(0xFFFFB4AB)
    val Tertiary        = Color(0xFFC8C6C5)
    val SecCont         = Color(0xFF454747)
    val OnSecCont       = Color(0xFFB4B5B5)
}

// ══════════════════════════════════════════════════════════════════════════════
// DATOS ESTÁTICOS
// ══════════════════════════════════════════════════════════════════════════════
private val FRICTION_DESCRIPTIONS = listOf(
    R.string.settings_friccion_desc_1,
    R.string.settings_friccion_desc_2,
    R.string.settings_friccion_desc_3,
    R.string.settings_friccion_desc_4,
    R.string.settings_friccion_desc_5
)

private data class AppItem(
    val nameRes:       Int,
    val riskLabelRes:  Int,
    val riskColor:  Color,
    val limitLabelRes: Int,
    val icon:       ImageVector,
    val iconBg:     Color,
    val gradient:   Brush? = null
)

private val instagramGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFF9CE34), Color(0xFFEE2A7B), Color(0xFF6228D7))
)

private val MONITORED_APPS = listOf(
    AppItem(R.string.app_musica,    R.string.risk_critical,  C.Error,    R.string.limit_30m, Icons.Outlined.MusicNote,     Color(0xFF000000)),
    AppItem(R.string.app_instagram, R.string.risk_moderate, C.Tertiary, R.string.limit_30m, Icons.Outlined.PhotoCamera,   Color.Transparent, instagramGradient),
    AppItem(R.string.app_juegos,    R.string.risk_low,     C.Tertiary, R.string.limit_blocked,  Icons.Outlined.SportsEsports, C.SecCont)
)

// ══════════════════════════════════════════════════════════════════════════════
// RAÍZ  —  punto de entrada
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val adaptiveFriction by viewModel.adaptiveFriction.collectAsState()
    val nightRestriction by viewModel.nightRestriction.collectAsState()
    val appLimitsEnabled by viewModel.appLimitsEnabled.collectAsState()
    val frictionLevelInt by viewModel.frictionLevel.collectAsState()
    val frictionLevel = frictionLevelInt.toFloat()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(C.Bg)
    ) {
        val isWide = maxWidth > 720.dp

        ContentArea(
            modifier           = Modifier.fillMaxSize(),
            isWide             = isWide,
            adaptiveFriction   = adaptiveFriction,
            onAdaptiveFriction = { viewModel.updateAdaptiveFriction(it) },
            nightRestriction   = nightRestriction,
            onNightRestriction = { viewModel.updateNightRestriction(it) },
            appLimitsEnabled   = appLimitsEnabled,
            onAppLimits        = { viewModel.updateAppLimitsEnabled(it) },
            frictionLevel      = frictionLevel,
            onFrictionLevel    = { viewModel.updateFrictionLevel(it) }
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// ÁREA DE CONTENIDO
// ══════════════════════════════════════════════════════════════════════════════
@Composable
private fun ContentArea(
    modifier:           Modifier = Modifier,
    isWide:             Boolean,
    adaptiveFriction:   Boolean,
    onAdaptiveFriction: (Boolean) -> Unit,
    nightRestriction:   Boolean,
    onNightRestriction: (Boolean) -> Unit,
    appLimitsEnabled:   Boolean,
    onAppLimits:        (Boolean) -> Unit,
    frictionLevel:      Float,
    onFrictionLevel:    (Float) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Encabezado de pantalla
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text       = stringResource(R.string.settings_control_enfoque),
                color      = C.OnSurf,
                fontSize   = if (isWide) 40.sp else 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = if (isWide) 48.sp else 38.sp
            )
            Text(
                text     = stringResource(R.string.settings_arquitecta_entorno),
                color    = C.OnSurfVar,
                fontSize = 18.sp
            )
        }

        // Grid responsivo
        if (isWide) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier            = Modifier.weight(7f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SettingsCard(
                        adaptiveFriction   = adaptiveFriction,
                        onAdaptiveFriction = onAdaptiveFriction,
                        nightRestriction   = nightRestriction,
                        onNightRestriction = onNightRestriction,
                        appLimitsEnabled   = appLimitsEnabled,
                        onAppLimits        = onAppLimits
                    )
                    FrictionCard(level = frictionLevel, onLevelChange = onFrictionLevel)
                }
                AppsCard(modifier = Modifier.weight(5f))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SettingsCard(
                    adaptiveFriction   = adaptiveFriction,
                    onAdaptiveFriction = onAdaptiveFriction,
                    nightRestriction   = nightRestriction,
                    onNightRestriction = onNightRestriction,
                    appLimitsEnabled   = appLimitsEnabled,
                    onAppLimits        = onAppLimits
                )
                FrictionCard(level = frictionLevel, onLevelChange = onFrictionLevel)
                AppsCard()
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// CARD: TOGGLES DE CONFIGURACIÓN
// ══════════════════════════════════════════════════════════════════════════════
@Composable
private fun SettingsCard(
    adaptiveFriction:   Boolean,
    onAdaptiveFriction: (Boolean) -> Unit,
    nightRestriction:   Boolean,
    onNightRestriction: (Boolean) -> Unit,
    appLimitsEnabled:   Boolean,
    onAppLimits:        (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(C.SurfCont)
            .border(1.dp, C.OutlineVar, RoundedCornerShape(12.dp))
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        ToggleRow(
            title       = stringResource(R.string.settings_friccion_adaptativa),
            description = stringResource(R.string.settings_friccion_adaptativa_desc),
            checked     = adaptiveFriction,
            onChecked   = onAdaptiveFriction,
            showDivider = true
        )
        ToggleRow(
            title       = stringResource(R.string.settings_restriccion_nocturna),
            description = stringResource(R.string.settings_restriccion_nocturna_desc),
            checked     = nightRestriction,
            onChecked   = onNightRestriction,
            showDivider = true
        )
        ToggleRow(
            title       = stringResource(R.string.settings_limites_aplicaciones),
            description = stringResource(R.string.settings_limites_aplicaciones_desc),
            checked     = appLimitsEnabled,
            onChecked   = onAppLimits,
            showDivider = false
        )
    }
}

@Composable
private fun ToggleRow(
    title:       String,
    description: String,
    checked:     Boolean,
    onChecked:   (Boolean) -> Unit,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text       = title,
                    color      = C.OnSurf,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp
                )
                Text(
                    text       = description,
                    color      = C.OnSurfVar,
                    fontSize   = 13.sp,
                    lineHeight = 18.sp
                )
            }
            Switch(
                checked         = checked,
                onCheckedChange = onChecked,
                colors          = SwitchDefaults.colors(
                    checkedThumbColor    = C.OnSurf,
                    checkedTrackColor    = C.Primary,
                    uncheckedThumbColor  = C.OnSurfVar,
                    uncheckedTrackColor  = C.SurfContHigh,
                    uncheckedBorderColor = C.OutlineVar
                )
            )
        }
        if (showDivider) {
            HorizontalDivider(color = C.SurfContHighest, thickness = 1.dp)
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// CARD: INTENSIDAD DE FRICCIÓN
// ══════════════════════════════════════════════════════════════════════════════
@Composable
private fun FrictionCard(level: Float, onLevelChange: (Float) -> Unit) {
    val levelInt = level.roundToInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(C.SurfCont)
            .border(1.dp, C.OutlineVar, RoundedCornerShape(12.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = stringResource(R.string.settings_intensidad_friccion),
                color      = C.OnSurf,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 16.sp
            )
            Text(
                text          = stringResource(R.string.settings_nivel_format, levelInt),
                color         = C.Primary,
                fontSize      = 11.sp,
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        // steps = 3  →  5 posiciones discretas: 1, 2, 3, 4, 5
        Slider(
            value         = level,
            onValueChange = onLevelChange,
            valueRange    = 1f..5f,
            steps         = 3,
            modifier      = Modifier.fillMaxWidth(),
            colors        = SliderDefaults.colors(
                thumbColor         = C.Primary,
                activeTrackColor   = C.Primary,
                inactiveTrackColor = C.SurfContHigh,
                activeTickColor    = Color.Transparent,
                inactiveTickColor  = Color.Transparent
            )
        )

        // Etiquetas de los niveles
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (1..5).forEach {
                Text("$it", color = C.OnSurfVar, fontSize = 11.sp)
            }
        }

        // Descripción del nivel seleccionado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(C.SurfContLow)
                .border(1.dp, C.SurfContHighest, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text       = stringResource(FRICTION_DESCRIPTIONS.getOrElse(levelInt - 1) { R.string.settings_friccion_desc_1 }),
                color      = C.OnSurfVar,
                fontSize   = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// CARD: VECTORES MONITOREADOS
// ══════════════════════════════════════════════════════════════════════════════
@Composable
private fun AppsCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, C.OutlineVar, RoundedCornerShape(12.dp))
    ) {
        // Encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(C.SurfContLow)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text       = stringResource(R.string.settings_vectores_monitoreados),
                color      = C.OnSurf,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 16.sp
            )
            Text(
                text     = stringResource(R.string.settings_vectores_desc),
                color    = C.OnSurfVar,
                fontSize = 13.sp
            )
        }

        HorizontalDivider(color = C.SurfContHighest)

        // Lista de apps
        Column(modifier = Modifier.background(C.SurfCont)) {
            MONITORED_APPS.forEachIndexed { idx, app ->
                AppRow(app)
                if (idx < MONITORED_APPS.lastIndex) {
                    HorizontalDivider(color = C.SurfContHighest)
                }
            }
        }

        HorizontalDivider(color = C.SurfContHighest)

        // Botón añadir
        Box(
            modifier         = Modifier
                .fillMaxWidth()
                .background(C.SurfContLow)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            OutlinedButton(
                onClick  = {},
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(4.dp),
                border   = BorderStroke(1.dp, C.OutlineVar),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = C.OnSurf)
            ) {
                Text(
                    text          = stringResource(R.string.settings_anadir_aplicacion),
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    modifier      = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun AppRow(app: AppItem) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Icono + info
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .then(
                        if (app.gradient != null)
                            Modifier.background(app.gradient)
                        else
                            Modifier.background(app.iconBg)
                    )
                    .border(1.dp, C.OutlineVar, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = app.icon,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text       = stringResource(app.nameRes),
                    color      = C.OnSurf,
                    fontWeight = FontWeight.Medium,
                    fontSize   = 14.sp
                )
                Text(
                    text     = stringResource(app.riskLabelRes),
                    color    = app.riskColor,
                    fontSize = 11.sp
                )
            }
        }

        // Badge de límite
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(C.Bg)
                .border(1.dp, C.OutlineVar, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text       = stringResource(app.limitLabelRes),
                color      = C.OnSurfVar,
                fontSize   = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
