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
        String imagenUrl,
        DatosTipoEvento tipo,
        List<IntegranteDetalleDTO> integrantes
) {
    public record DatosTipoEvento(String nombre, String descripcion) {}
    public record IntegranteDetalleDTO(String nombre, String apellido, String identificacion) {}
}