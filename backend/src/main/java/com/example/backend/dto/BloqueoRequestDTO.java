package com.example.backend.dto;

public record BloqueoRequestDTO(
        Long eventoId,
        int fila,
        int columna
) {}