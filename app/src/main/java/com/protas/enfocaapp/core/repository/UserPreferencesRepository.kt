package com.protas.enfocaapp.core.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.protas.enfocaapp.core.model.NivelRigor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val secureStorage: SharedPreferences, // Hilt inyectará el EncryptedSharedPreferences aquí
    @ApplicationContext private val context: Context
) {

    companion object {
        val PREF_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val PREF_NIVEL_RIGOR = stringPreferencesKey("nivel_rigor")
        val PREF_FRICCION_ADAPTATIVA = booleanPreferencesKey("friccion_adaptativa")
        val PREF_RESTRICCION_NOCTURNA = booleanPreferencesKey("restriccion_nocturna")
        val PREF_INTENSIDAD = intPreferencesKey("intensidad")
        val PREF_LIMITES_APLICACIONES = booleanPreferencesKey("limites_aplicaciones")
    }

    fun saveSensitiveData(token: String) {
        // Al guardar, se cifra automáticamente con AES-256
        secureStorage.edit().putString("USER_TOKEN", token).apply()
    }

    fun getSensitiveData(): String? {
        // Al leer, se descifra automáticamente
        return secureStorage.getString("USER_TOKEN", null)
    }

    suspend fun saveOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PREF_ONBOARDING_COMPLETED] = completed
        }
    }

    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PREF_ONBOARDING_COMPLETED] ?: false
    }

    suspend fun saveNivelRigor(nivel: NivelRigor) {
        context.dataStore.edit { preferences ->
            preferences[PREF_NIVEL_RIGOR] = nivel.name
        }
    }

    val nivelRigorFlow: Flow<NivelRigor> = context.dataStore.data.map { preferences ->
        val name = preferences[PREF_NIVEL_RIGOR] ?: NivelRigor.ESTRICTO.name
        try {
            NivelRigor.valueOf(name)
        } catch (e: Exception) {
            NivelRigor.ESTRICTO
        }
    }

    // --- Configuraciones de Límites ---
    suspend fun saveFriccionAdaptativa(enabled: Boolean) {
        context.dataStore.edit { it[PREF_FRICCION_ADAPTATIVA] = enabled }
    }
    val friccionAdaptativaFlow: Flow<Boolean> = context.dataStore.data.map { it[PREF_FRICCION_ADAPTATIVA] ?: true }

    suspend fun saveRestriccionNocturna(enabled: Boolean) {
        context.dataStore.edit { it[PREF_RESTRICCION_NOCTURNA] = enabled }
    }
    val restriccionNocturnaFlow: Flow<Boolean> = context.dataStore.data.map { it[PREF_RESTRICCION_NOCTURNA] ?: false }

    suspend fun saveIntensidad(nivel: Int) {
        context.dataStore.edit { it[PREF_INTENSIDAD] = nivel }
    }
    val intensidadFlow: Flow<Int> = context.dataStore.data.map { it[PREF_INTENSIDAD] ?: 3 }

    suspend fun saveLimitesAplicaciones(enabled: Boolean) {
        context.dataStore.edit { it[PREF_LIMITES_APLICACIONES] = enabled }
    }
    val limitesAplicacionesFlow: Flow<Boolean> = context.dataStore.data.map { it[PREF_LIMITES_APLICACIONES] ?: true }
}
