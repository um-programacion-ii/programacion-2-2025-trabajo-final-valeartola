package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SincronizacionService {

    private final EventoService eventoService;

    public void procesarNotificacion(String mensaje) {
        System.out.println("SincronizacionService: Procesando notificación...");
        System.out.println("Mensaje recibido: " + mensaje);

        try {
            System.out.println("Estrategia: Sincronización completa del catálogo.");

            eventoService.syncEvents();

            System.out.println("Sincronización finalizada con éxito.");
        } catch (Exception e) {
            System.err.println("Error crítico en sincronización: " + e.getMessage());
        }
    }
}