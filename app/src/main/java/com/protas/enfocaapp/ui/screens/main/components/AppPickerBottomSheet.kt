package com.protas.enfocaapp.ui.screens.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.protas.enfocaapp.core.utils.AppInfo
import com.protas.enfocaapp.core.model.AppRestriction
import androidx.compose.ui.res.stringResource
import com.protas.enfocaapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPickerBottomSheet(
    installedApps: List<AppInfo>,
    initialRestriction: AppRestriction? = null,
    onDismiss: () -> Unit,
    onAppSelected: (AppInfo, Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }
    var limitMinutes by remember { mutableStateOf(60f) }

    LaunchedEffect(initialRestriction) {
        if (initialRestriction != null) {
            selectedApp = installedApps.find { it.packageName == initialRestriction.packageName }
            limitMinutes = initialRestriction.limitMinutes.toFloat()
        }
    }

    val filteredApps = installedApps.filter {
        it.appName.contains(searchQuery, ignoreCase = true)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1B1B),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFC3C5D9)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            if (selectedApp == null) {
                Text(
                    text = stringResource(R.string.app_picker_select_app),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(R.string.app_picker_search), color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0052FF),
                        unfocusedBorderColor = Color(0xFF353534),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                    items(filteredApps) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedApp = app }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val bitmap = remember(app.icon) { app.icon.toBitmap().asImageBitmap() }
                            Image(
                                bitmap = bitmap,
                                contentDescription = app.appName,
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = app.appName,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            } else {
                // Configurar Límite
                val app = selectedApp ?: return@Column
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                    val bitmap = remember(app.icon) { app.icon.toBitmap().asImageBitmap() }
                    Image(
                        bitmap = bitmap,
                        contentDescription = app.appName,
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = app.appName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = app.packageName, color = Color.Gray, fontSize = 12.sp)
                    }
                }

                Text(
                    text = stringResource(R.string.app_picker_limit_to_format, limitMinutes.toInt()),
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Slider(
                    value = limitMinutes,
                    onValueChange = { limitMinutes = it },
                    valueRange = 5f..300f,
                    steps = 58,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF0052FF),
                        activeTrackColor = Color(0xFF0052FF)
                    )
                )

                Spacer(Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = { 
                            if (initialRestriction != null) onDismiss() 
                            else selectedApp = null 
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text(stringResource(R.string.app_picker_back))
                    }
                    Button(
                        onClick = { onAppSelected(app, limitMinutes.toInt()) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052FF))
                    ) {
                        Text(stringResource(R.string.app_picker_save), color = Color.White)
                    }
                }
            }
        }
    }
}
