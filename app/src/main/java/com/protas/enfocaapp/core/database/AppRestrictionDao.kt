package com.protas.enfocaapp.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.protas.enfocaapp.core.model.AppRestriction
import kotlinx.coroutines.flow.Flow

@Dao
interface AppRestrictionDao {
    @Query("SELECT * FROM app_restrictions")
    fun getAllRestrictions(): Flow<List<AppRestriction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestriction(restriction: AppRestriction)

    @Delete
    fun deleteRestriction(restriction: AppRestriction)
}
