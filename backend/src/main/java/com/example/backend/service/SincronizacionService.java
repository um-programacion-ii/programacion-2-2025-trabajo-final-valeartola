package com.example.backend.service;

import com.example.backend.dto.EventoExternoDTO;
import com.fasterxml.jackson.databind.ObjectMapper; // Necesitás este import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SincronizacionService {

    private final EventoService eventoService;
    private final ObjectMapper objectMapper; // Spring lo inyecta automáticamente

    public void procesarNotificacion(String mensaje) {
        System.out.println("SincronizacionService: Procesando mensaje de Kafka...");

        try {
            // Convertimos el JSON del mensaje directamente al objeto DTO
            EventoExternoDTO eventoDto = objectMapper.readValue(mensaje, EventoExternoDTO.class);

            System.out.println("Actualizando evento específico ID: " + eventoDto.id());

            // Llamamos al nuevo metodo quirúrgico en EventoService
            eventoService.actualizarUnSoloEvento(eventoDto);

        } catch (Exception e) {
            System.err.println("Error al parsear mensaje, reintentando con sync completo: " + e.getMessage());
            eventoService.syncEvents();
        }
    }
}