package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AsientoOcupadoExternoDTO(
        int fila,
        int columna
) {}