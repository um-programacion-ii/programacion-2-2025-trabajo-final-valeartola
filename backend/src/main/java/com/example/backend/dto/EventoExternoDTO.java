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

        @JsonProperty("eventoTipo") EventoTipoDto eventoTipo,
        @JsonProperty("integrantes") List<IntegranteDto> integrantes
) {
    public record EventoTipoDto(
            @JsonProperty("nombre") String nombre,       // Asegúrate que en el JSON diga "nombre"
            @JsonProperty("descripcion") String descripcion // Asegúrate que en el JSON diga "descripcion"
    ) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record IntegranteDto(
            Long id,               // <--- AGREGA ESTO
            String nombre,
            String apellido,
            String identificacion  // <--- AGREGA ESTO
    ) {}
}