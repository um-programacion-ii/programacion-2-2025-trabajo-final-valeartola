package org.example.project.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// IMPORTANTE: Imports específicos para Coil 3 Multiplatform
import coil3.compose.AsyncImage
import org.example.project.model.Evento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEventoScreen(evento: Evento, onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Volver", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // --- 1. BANNER DE IMAGEN (Coil 3) ---
            if (!evento.imagenUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = evento.imagenUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    onState = { state ->
                        if (state is coil3.compose.AsyncImagePainter.State.Error) {
                            // Esto imprime el error real en el Logcat (la pestaña de abajo en Android Studio)
                            println("ERROR COIL: ${state.result.throwable}")
                        }
                    }
                )
            } else {
                // Placeholder si no hay imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // --- 2. CUERPO DEL DETALLE (Padding lateral de 24dp) ---
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 20.dp)
            ) {
                // TÍTULO Y CATEGORÍA
                Text(
                    text = evento.nombre.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                evento.tipo?.let { tipo ->
                    Text(
                        text = tipo.nombre,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // TARJETA DE FECHA Y DIRECCIÓN
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(" ${evento.fecha.replace("T", " ")}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "📍 ${evento.direccion ?: "Ubicación a confirmar"}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // DESCRIPCIÓN AMPLIA (Corregida como pediste)
                Text(
                    text = "ACERCA DEL EVENTO",
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = evento.descripcion,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 19.sp,    // Letra más grande
                        lineHeight = 32.sp   // Espacio entre líneas para que sea "amplia"
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // SECCIÓN DE INTEGRANTES (Solo si existen)
                if (evento.integrantes.isNotEmpty()) {
                    Text(
                        text = "INTEGRANTES",
                        style = MaterialTheme.typography.labelLarge,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    evento.integrantes.forEach { integrante ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "${integrante.nombre} ${integrante.apellido}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))

                // PIE DE PÁGINA: DIVIDER, PRECIO Y BOTÓN
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Precio total", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "$ ${evento.precio}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = { /* Acción futura de compra */ },
                        modifier = Modifier.height(56.dp).width(160.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(8.dp)
                    ) {
                        Text("COMPRAR", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}