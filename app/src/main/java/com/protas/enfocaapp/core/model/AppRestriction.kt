package com.protas.enfocaapp.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_restrictions")
data class AppRestriction(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val limitMinutes: Int,
    val status: String = "ACTIVE_BLOCK",
    val enabled: Boolean = true
)
