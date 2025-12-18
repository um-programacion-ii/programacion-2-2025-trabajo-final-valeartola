package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public record EventoResumenDTO(
        String titulo,
        String resumen,
        String descripcion,
        String fecha,
        @JsonProperty("precioEntrada")
        Double precio,
        @JsonProperty("eventoTipo")
        DatosTipoEvento eventoTipo,
        Long id
){
        public record DatosTipoEvento(String nombre, String descripcion) {}

        public record IntegranteResumenDTO(String nombre, String apellido, String identificacion) {}
}
