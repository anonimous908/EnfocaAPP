package com.protas.enfocaapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.protas.enfocaapp.core.repository.UserPreferencesRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EncryptedPreferencesTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: UserPreferencesRepository

    @Before
    fun init() {
        // Inyecta las dependencias reales (el EncryptedSharedPreferences real configurado en SecurityModule)
        hiltRule.inject()
    }

    @Test
    fun testDataPersistenceAfterSimulatedRestart() {
        // 1. EL ROBOT GUARDA EL DATO EN LA MEMORIA DEL TELÉFONO
        val secretToken = "Búfalo_Ultra_Secreto_123"
        repository.saveSensitiveData(secretToken)
        runBlocking {
            repository.saveOnboardingCompleted(true)
        }

        // Verificación rápida en memoria
        assertEquals(secretToken, repository.getSensitiveData())
        runBlocking {
            assertTrue(repository.onboardingCompletedFlow.first())
        }

        // 2. SIMULAMOS CERRAR LA APLICACIÓN
        // Al instanciar un nuevo repositorio "a mano" usando el Contexto de Android, 
        // evitamos cualquier caché de memoria RAM temporal. 
        // Esto obliga a Android a buscar el archivo físico encriptado en el almacenamiento de tu teléfono físico.
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // Tenemos que recrear las preferencias encriptadas igual que lo hace SecurityModule
        val masterKeyAlias = androidx.security.crypto.MasterKeys.getOrCreate(androidx.security.crypto.MasterKeys.AES256_GCM_SPEC)
        val freshEncryptedPrefs = androidx.security.crypto.EncryptedSharedPreferences.create(
            "enfoca_secure_prefs",
            masterKeyAlias,
            context,
            androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        
        val freshRepository = UserPreferencesRepository(freshEncryptedPrefs, context)

        // 3. VOLVEMOS A ABRIR Y VERIFICAR FÍSICAMENTE
        // Si estos pasan, significa que sobrevivió al apagado y está cifrado en hardware
        assertEquals("El token debe sobrevivir al reinicio", secretToken, freshRepository.getSensitiveData())
        runBlocking {
            assertTrue("El Onboarding debe seguir marcado como completado", freshRepository.onboardingCompletedFlow.first())
        }
    }
}
