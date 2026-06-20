package com.protas.enfocaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.protas.enfocaapp.core.navigation.EnfocaNavGraph
import com.protas.enfocaapp.ui.theme.EnfocaAPPTheme
import dagger.hilt.android.AndroidEntryPoint

import android.view.ViewGroup

// @AndroidEntryPoint le indica a Hilt que esta pantalla recibirá inyección de dependencias (objetos globales).
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Parche para evitar el crasheo de "Splitting motion events" en Jetpack Compose
        window.decorView.findViewById<ViewGroup>(android.R.id.content).isMotionEventSplittingEnabled = false
        
        // Dibuja la app debajo de la barra de estado y de navegación (diseño inmersivo / sin bordes)
        enableEdgeToEdge()
        
        // Define que la interfaz gráfica se construirá con código (Jetpack Compose) en lugar de XML
        setContent {
            // Aplica nuestra paleta de colores y fuentes personalizada
            EnfocaAPPTheme {
                // Crea el controlador principal que nos permite saltar de una pantalla a otra
                val navController = rememberNavController()
                
                // Scaffold es el "andamiaje" de la pantalla. Nos da los márgenes seguros (innerPadding)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // Carga el mapa de rutas de la app (Splash -> Onboarding -> Main)
                        EnfocaNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}