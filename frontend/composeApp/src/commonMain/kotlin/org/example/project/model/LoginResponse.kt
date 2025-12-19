package org.example.project.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("jwt") // IMPORTANTE: Así se llama en tu Java
    val token: String,

    val sessionId: String,

    // Como el Java no manda nombre, le damos uno por defecto
    val nombre: String = "Invitado"
)