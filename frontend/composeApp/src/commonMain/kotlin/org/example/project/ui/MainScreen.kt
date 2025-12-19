package org.example.project.ui

import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.model.LoginResponse

import org.example.project.network.AuthRepository

@Composable
fun MainScreen(onNavigateToEvents: (LoginResponse) -> Unit) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() } // Tu repositorio con la IP 10.186.197.23
    var isConnecting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido a la App de Eventos", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                scope.launch {
                    isConnecting = true
                    // Llamamos al endpoint que definiste en Java
                    val response = authRepository.loginAsGuest()
                    isConnecting = false

                    if (response != null) {
                        onNavigateToEvents(response)
                    }
                }
            },
            enabled = !isConnecting
        ) {
            if (isConnecting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Text("Ver eventos")
            }
        }
    }
}