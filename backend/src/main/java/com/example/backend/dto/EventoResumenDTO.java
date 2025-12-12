package com.example.backend.dto;

import com.example.backend.model.Evento;

public record EventoResumenDTO (
        Long id,
        String titulo,
        String resumen,
        Double precioEntrada
) {

    public static EventoResumenDTO fromEntity(Evento e) {
        return new EventoResumenDTO(
                e.getId(),
                e.getTitulo(),
                e.getResumen(),
                e.getPrecioEntrada()
        );
    }
}

