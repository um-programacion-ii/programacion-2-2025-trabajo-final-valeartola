package com.example.backend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record EventoDetalleDTO(
        Long id,
        String titulo,
        String resumen,
        String descripcion,
        String fecha,
        String direccion,
        @JsonProperty("precioEntrada") Double precio,
        @JsonProperty("imagen") String imagenUrl,      // <-- Agregá esto
        @JsonProperty("eventoTipo") DatosTipoEvento tipo, // <-- Agregá esto
        @JsonProperty("filaAsientos") Integer filas,
        @JsonProperty("columnAsientos") Integer columnas,
        List<IntegranteDetalleDTO> integrantes
) {
    public record DatosTipoEvento(String nombre, String descripcion) {}
    public record IntegranteDetalleDTO(String nombre, String apellido, String identificacion) {}
}