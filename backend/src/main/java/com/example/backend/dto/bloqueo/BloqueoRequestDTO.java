package com.example.backend.dto.bloqueo;

    public record BloqueoRequestDTO(
            Long eventoId,
            int fila,
            int columna
    ) {}