package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import frontend.composeapp.generated.resources.Res
import frontend.composeapp.generated.resources.compose_multiplatform
import org.example.project.ui.MainScreen
import org.example.project.model.LoginResponse


@Composable
@Preview
fun App() {
    MaterialTheme {
        var screenState by remember { mutableStateOf("login") }

        if (screenState == "login") {
            MainScreen(onNavigateToEvents = { response ->
                println("Logueado con éxito: ${response.nombre}")
                screenState = "eventos"
            })
        } else {
            // Aquí irá tu pantalla de eventos más adelante
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("¡Bienvenido a la lista de eventos!")
            }
        }
    }
}