package com.example.backend.dto;

public record TicketItemDTO(
        int fila,
        int columna,
        String nombreAsistente,
        String apellidoAsistente
) {}
