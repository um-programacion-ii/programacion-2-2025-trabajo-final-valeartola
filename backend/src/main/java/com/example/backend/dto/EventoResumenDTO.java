package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public record EventoResumenDTO(
        Long id,
        String titulo,
        String resumen,
        String fecha,
        @JsonProperty("precioEntrada")
        Double precio
){}
