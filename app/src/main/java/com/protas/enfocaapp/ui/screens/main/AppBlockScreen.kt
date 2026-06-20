package com.protas.enfocaapp.ui.screens.main

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.protas.enfocaapp.R
import com.protas.enfocaapp.core.model.AppRestriction
import com.protas.enfocaapp.ui.screens.main.components.AppPickerBottomSheet

private val CBackground        = Color(0xFF131313)
private val CSurfContainerLow  = Color(0xFF1C1B1B)
private val CSurfContainer     = Color(0xFF201F1F)
private val CSurfContainerHigh = Color(0xFF2A2A2A)
private val CSurfVariant       = Color(0xFF353534)
private val COnSurface         = Color(0xFFE5E2E1)
private val COnSurfVariant     = Color(0xFFC3C5D9)
private val CPrimary           = Color(0xFFB7C4FF)
private val CPrimaryContainer  = Color(0xFF0052FF)
private val CSecondary         = Color(0xFFC6C6C7)
private val CError             = Color(0xFFFFB4AB)
private val COutlineVariant    = Color(0xFF434656)

@Composable
fun AppBlockScreen(viewModel: AppBlockViewModel = hiltViewModel()) {
    val restrictions by viewModel.restrictions.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val appUsages by viewModel.appUsages.collectAsState()
    var showAppPicker by remember { mutableStateOf(false) }
    var appToModify by remember { mutableStateOf<AppRestriction?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps()
    }

    if (showAppPicker) {
        AppPickerBottomSheet(
            installedApps = installedApps,
            initialRestriction = appToModify,
            onDismiss = { 
                showAppPicker = false
                appToModify = null
            },
            onAppSelected = { appInfo, limitMinutes ->
                viewModel.saveRestriction(appInfo.packageName, appInfo.appName, limitMinutes)
                showAppPicker = false
                appToModify = null
            }
        )
    }

    Scaffold(
        containerColor = CBackground,
        topBar = { DDTopAppBar() }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 260.dp),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryLimitsHeader(onNewRuleClick = { showAppPicker = true })
            }
            
            if (restrictions.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = stringResource(R.string.app_block_no_restrictions),
                        color = COnSurfVariant,
                        modifier = Modifier.padding(top = 32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(restrictions) { restriction ->
                    val iconBitmap = remember(restriction.packageName) {
                        viewModel.getAppIcon(restriction.packageName)?.toBitmap()?.asImageBitmap()
                    }
                    AppRestrictionCard(
                        restriction = restriction,
                        icon = iconBitmap,
                        usageMs = appUsages[restriction.packageName] ?: 0L,
                        onToggle = { enabled ->
                            viewModel.toggleRestriction(restriction, enabled)
                        },
                        onDelete = {
                            viewModel.deleteRestriction(restriction)
                        },
                        onModify = {
                            appToModify = restriction
                            showAppPicker = true
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DDTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.enfocaapp_title),
                color = COnSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        },
        windowInsets = WindowInsets(0.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CBackground,
            scrolledContainerColor = CBackground,
        ),
        modifier = Modifier.drawBehind {
            val y = size.height
            drawLine(
                color = CSurfVariant,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx(),
            )
        },
    )
}

@Composable
private fun CategoryLimitsHeader(onNewRuleClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.app_block_category_limits),
            color = COnSurface,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )
        
        Button(
            onClick = onNewRuleClick,
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CPrimaryContainer),
            contentPadding = PaddingValues(vertical = 14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.app_block_new_rule),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
            )
        }
    }
}

@Composable
private fun AppRestrictionCard(
    restriction: AppRestriction, 
    icon: androidx.compose.ui.graphics.ImageBitmap?, 
    usageMs: Long,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onModify: () -> Unit
) {
    val usageMinutes = (usageMs / 60_000).toInt()
    val progress = if (restriction.limitMinutes > 0) {
        (usageMinutes.toFloat() / restriction.limitMinutes).coerceIn(0f, 1f)
    } else 0f

    val isActive  = restriction.status == "ACTIVE_BLOCK"
    val isWarning = restriction.status == "WARNING"

    val borderColor   = if (isActive) CPrimary else CSurfVariant
    val progressColor = when (restriction.status) {
        "ACTIVE_BLOCK" -> CPrimary
        "MONITORING"   -> CSecondary
        else           -> CError
    }
    val statusLabel = when (restriction.status) {
        "ACTIVE_BLOCK" -> stringResource(R.string.app_block_status_active)
        "MONITORING"   -> stringResource(R.string.app_block_status_monitoring)
        else           -> stringResource(R.string.app_block_status_warning)
    }

    Box(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(CSurfContainerLow),
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(CPrimary.copy(alpha = 0.05f)),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isActive) CPrimaryContainer.copy(alpha = 0.2f) else CSurfVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (icon != null) {
                            Image(bitmap = icon, contentDescription = null, modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)))
                        } else {
                            Icon(Icons.Outlined.Apps, contentDescription = null, tint = if (isActive) CPrimary else COnSurfVariant, modifier = Modifier.size(20.dp))
                        }
                    }

                    Column {
                        Text(
                            text = restriction.appName,
                            color = COnSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .border(1.dp, COutlineVariant, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text(
                                text = statusLabel,
                                color = COnSurfVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.7.sp,
                            )
                        }
                    }
                }

                Box {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.app_block_more_options), tint = COnSurfVariant)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = CSurfContainerHigh
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.app_block_modify_limit), color = COnSurface) },
                            onClick = { 
                                expanded = false
                                onModify()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.app_block_delete), color = CError) },
                            onClick = { 
                                expanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.app_block_usage_format, usageMinutes),
                        color = if (isWarning) CError else COnSurfVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                    )
                    Text(
                        text = stringResource(R.string.app_block_limit_format, restriction.limitMinutes),
                        color = if (isActive) CPrimary else COnSurfVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(CSurfVariant),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(progressColor),
                    )
                }
            }

            HorizontalDivider(color = CSurfVariant, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = COnSurfVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(R.string.app_block_no_active_blocks),
                        color = COnSurfVariant.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Switch(
                    checked = restriction.enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = CPrimary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = CSurfVariant,
                        uncheckedBorderColor = CSurfVariant,
                    ),
                )
            }
        }
    }
}
