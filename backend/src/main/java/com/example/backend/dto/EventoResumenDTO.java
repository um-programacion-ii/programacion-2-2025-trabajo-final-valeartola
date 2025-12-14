package com.example.backend.dto;

public record EventoResumenDTO(
        Long id,
        String titulo,
        String resumen, // Descripción corta
        String fecha,
        Double precio
) {}
