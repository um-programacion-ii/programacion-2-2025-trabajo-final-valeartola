package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EventoExternoDTO(
        Long id,
        String titulo,
        String descripcion,
        String resumen,
        String direccion,
        String imagen,
        String estado,

        @JsonProperty("precioEntrada") Double precio,
        @JsonProperty("fecha") ZonedDateTime fecha,

        @JsonProperty("filaAsientos") Integer filas,
        @JsonProperty("columnaAsientos") Integer columnas,

        @JsonProperty("tipo") EventoTipoDto eventoTipo,
        @JsonProperty("integrantes") List<IntegranteDto> integrantes
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EventoTipoDto(String nombre, String descripcion) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record IntegranteDto(String nombre, String apellido) {}
}