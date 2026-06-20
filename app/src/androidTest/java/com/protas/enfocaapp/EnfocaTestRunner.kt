package com.protas.enfocaapp

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

// Este TestRunner es la clave mágica para Hilt. 
// En lugar de arrancar la aplicación normal (EnfocaApplication), 
// arranca una versión de prueba vacía (HiltTestApplication) que nos permite inyectar dependencias falsas o reales en el entorno de pruebas.
class EnfocaTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
