package org.example.project.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.model.Evento
import org.example.project.network.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(token: String, onBack: () -> Unit) {
    val repository = remember { AuthRepository() }
    var listaEventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }

    LaunchedEffect(Unit) {
        listaEventos = repository.getEvento(token)
        cargando = false
    }

    if (eventoSeleccionado != null) {
        // Mostramos la pantalla de detalle
        DetalleEventoScreen(
            evento = eventoSeleccionado!!,
            onVolver = { eventoSeleccionado = null }
        )
    } else {
        // Mostramos tu lista original (con un pequeño cambio en ItemEvento)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Eventos Disponibles") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (cargando) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (listaEventos.isEmpty()) {
                    Text("No hay eventos disponibles.", modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(listaEventos) { evento ->
                            // Pasamos el clic para guardar el evento seleccionado
                            ItemEvento(evento, onClick = { eventoSeleccionado = evento })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemEvento(evento: Evento, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = evento.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = " ${evento.fecha.replace("T", " ")}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = evento.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "$ ${evento.precio}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black
            )
        }
    }
}

