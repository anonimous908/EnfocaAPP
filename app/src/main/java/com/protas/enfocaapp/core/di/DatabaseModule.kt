package com.protas.enfocaapp.core.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.protas.enfocaapp.core.database.AppRestrictionDao
import com.protas.enfocaapp.core.database.EnfocaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import java.util.UUID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        sharedPreferences: SharedPreferences
    ): EnfocaDatabase {
        // Obtenemos la contraseña segura de las preferencias cifradas.
        // Si es la primera vez que se abre la app, generamos una aleatoria y la guardamos.
        var dbPassword = sharedPreferences.getString("db_password", null)
        if (dbPassword == null) {
            dbPassword = UUID.randomUUID().toString()
            sharedPreferences.edit().putString("db_password", dbPassword).apply()
        }

        // Creamos la fábrica de SQLCipher usando nuestra contraseña segura
        val factory = SupportFactory(dbPassword.toByteArray())

        return Room.databaseBuilder(
            context,
            EnfocaDatabase::class.java,
            "enfoca_database"
        )
        .openHelperFactory(factory)
        .build()
    }

    @Provides
    fun provideAppRestrictionDao(database: EnfocaDatabase): AppRestrictionDao {
        return database.appRestrictionDao()
    }
}
