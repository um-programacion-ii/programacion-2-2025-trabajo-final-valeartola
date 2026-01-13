package com.example.backend.dto.asiento;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AsientoOcupadoExternoDTO(
        int fila,
        int columna,
        String estado, // "Bloqueado" o "Vendido"
        String expira  // Viene como String (ISO-8601) desde Redis
) {}