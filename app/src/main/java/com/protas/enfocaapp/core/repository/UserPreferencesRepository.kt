package com.protas.enfocaapp.core.repository

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val secureStorage: SharedPreferences // Hilt inyectará el EncryptedSharedPreferences aquí
) {

    fun saveSensitiveData(token: String) {
        // Al guardar, se cifra automáticamente con AES-256
        secureStorage.edit().putString("USER_TOKEN", token).apply()
    }

    fun getSensitiveData(): String? {
        // Al leer, se descifra automáticamente
        return secureStorage.getString("USER_TOKEN", null)
    }
}
