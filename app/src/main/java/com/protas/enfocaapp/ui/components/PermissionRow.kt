package com.protas.enfocaapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.protas.enfocaapp.ui.theme.*

@Composable
fun PermissionRow(
    icon: ImageVector,
    title: String,
    description: String,
    granted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(EnfocaSurfaceContainerLow)
            .border(
                width = 1.dp,
                color = EnfocaSurfaceContainerHigh,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EnfocaOnSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = EnfocaOnBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = EnfocaOnSurfaceVariant
            )
        }

        OutlinedButton(
            onClick = onClick,
            enabled = !granted,
            shape = CircleShape,
            border = BorderStroke(
                width = 1.dp,
                color = if (granted) EnfocaPrimary.copy(alpha = 0.1f)
                else EnfocaPrimary.copy(alpha = 0.2f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (granted) EnfocaPrimary.copy(alpha = 0.2f)
                else EnfocaPrimary.copy(alpha = 0.1f),
                contentColor = EnfocaPrimary,
                disabledContainerColor = EnfocaPrimary.copy(alpha = 0.15f),
                disabledContentColor = EnfocaPrimary.copy(alpha = 0.5f)
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            modifier = Modifier.defaultMinSize(minHeight = 44.dp)
        ) {
            Text(
                text = if (granted) "✓" else "Permitir",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.02.em
                )
            )
        }
    }
}
