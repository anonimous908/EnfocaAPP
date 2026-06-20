package com.protas.enfocaapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.protas.enfocaapp.core.model.AppRestriction

@Database(entities = [AppRestriction::class], version = 2, exportSchema = false)
abstract class EnfocaDatabase : RoomDatabase() {
    abstract fun appRestrictionDao(): AppRestrictionDao
}
