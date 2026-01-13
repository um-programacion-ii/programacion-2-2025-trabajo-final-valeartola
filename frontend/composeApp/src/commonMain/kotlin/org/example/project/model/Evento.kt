package org.example.project.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Evento(
    val id: Long,

    @SerialName("titulo") // En Java es 'titulo', en tu UI preferimos 'nombre'
    val nombre: String,

    val resumen: String,
    val descripcion: String,
    val fecha: String,
    val direccion: String,

    @SerialName("precioEntrada") // El JSON viene como 'precioEntrada'
    val precio: Double,

    val imagenUrl: String? = null,
    val tipo: DatosTipoEvento? = null,
    val integrantes: List<IntegranteDetalleDTO> = emptyList()
)

@Serializable
data class DatosTipoEvento(
    val nombre: String,
    val descripcion: String
)

@Serializable
data class IntegranteDetalleDTO(
    val nombre: String,
    val apellido: String,
    val identificacion: String
)