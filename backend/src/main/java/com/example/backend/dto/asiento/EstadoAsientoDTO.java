package com.example.backend.dto.asiento;

public record EstadoAsientoDTO(
        int fila,
        int columna,
        String estado
) {}