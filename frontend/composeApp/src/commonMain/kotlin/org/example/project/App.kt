package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import frontend.composeapp.generated.resources.Res
import frontend.composeapp.generated.resources.compose_multiplatform
import org.example.project.ui.MainScreen
import org.example.project.model.LoginResponse
import org.example.project.ui.EventosScreen


@Composable
@Preview
fun App() {
    MaterialTheme {

        var screenState by remember { mutableStateOf("login") }
        var token by remember { mutableStateOf<String?>(null) }
        var nombreUsuario by remember { mutableStateOf("") }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (screenState == "login") {
                // PANTALLA 1: LOGIN
                // Le pasamos la función que se ejecuta cuando el login es exitoso
                MainScreen(onNavigateToEvents = { response ->
                    token = response.token            // Guardamos el JWT en memoria
                    nombreUsuario = response.nombre   // Guardamos el nombre ("Invitado")

                    screenState = "eventos"           // Cambiamos de pantalla
                })
            } else {
                // PANTALLA 2: LISTA DE EVENTOS
                // Aquí llamamos a la pantalla que mostrará los datos de tu base de datos
                // LLamamos a la pantalla real de eventos que acabamos de crear
                EventosScreen(
                    token = token ?: "",
                    onBack = { screenState = "login" }
                )
            }
        }
    }
}

@Composable
fun EventosView(token: String, nombre: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Bienvenido, $nombre!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Conexión exitosa con la Netbook.",
            style = MaterialTheme.typography.bodyLarge
        )

        // Mostramos un pedacito del token para confirmar que llegó bien
        Text(
            text = "Token activo: ${token.take(15)}...",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = onBack) {
            Text("Volver al Login")
        }
    }

}