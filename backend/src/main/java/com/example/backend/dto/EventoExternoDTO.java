package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EventoExternoDTO(
        Long id,
        @JsonProperty("titulo") String titulo,
        @JsonProperty("descripcion") String descripcion,
        @JsonProperty("fecha") String fecha,
        @JsonProperty("precio") Double precio,
        @JsonProperty("estado") String estado
) {}