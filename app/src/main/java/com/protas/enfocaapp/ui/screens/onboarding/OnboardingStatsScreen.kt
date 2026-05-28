package com.protas.enfocaapp.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.protas.enfocaapp.R
import com.protas.enfocaapp.ui.theme.DopaminaSurfaceContainerHigh
import com.protas.enfocaapp.ui.theme.DopaminaSurfaceContainerLow
import com.protas.enfocaapp.ui.theme.EnfocaAPPTheme

@Composable
fun OnboardingStatsScreen(
    onEnfrentarRealidad: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 50.dp)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.onboarding_Stats_Screen),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.02).sp,
                lineHeight = 38.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(

                text = stringResource(id = R.string.onboarding_Stats_Screen_descripcion),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(96.dp))

            StatCard(
                icon = Icons.Filled.Schedule,
                stat = stringResource(id = R.string.onboarding_stats_time_value),
                statColor = MaterialTheme.colorScheme.primary,
                label = stringResource(id = R.string.onboarding_stats_time_label),
                highlight = true
            )

            Spacer(modifier = Modifier.height(42.dp))

            StatCard(
                icon = Icons.Filled.LockOpen,
                stat = stringResource(id = R.string.onboarding_stats_unlocks_value),
                statColor = MaterialTheme.colorScheme.onSurface,
                label = stringResource(id = R.string.onboarding_stats_unlocks_label),
                highlight = false
            )
            Spacer(modifier = Modifier.height(64.dp))


            Button(
                onClick = onEnfrentarRealidad,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_btn_face_reality),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.1.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Filled.LockOpen,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    stat: String,
    statColor: Color,
    label: String,
    highlight: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DopaminaSurfaceContainerLow)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (highlight) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f))
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stat,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = statColor,
                letterSpacing = (-0.02).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.1.sp
            )
        }
    }
}



@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun PreviewOnboardingStatsScreen() {
    EnfocaAPPTheme {
        OnboardingStatsScreen()
    }
}
