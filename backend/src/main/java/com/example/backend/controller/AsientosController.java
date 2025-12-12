package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asientos")
public class AsientosController {


    @GetMapping("/{eventoId}")
    public String obtenerMapaAsientos(@PathVariable Long eventoId) {
        return "Mapa de asientos del evento " + eventoId + " (pendiente de implementación)";
    }


    @PostMapping("/{eventoId}/bloquear")
    public String bloquearAsientos(
            @PathVariable Long eventoId,
            @RequestBody List<AsientoRequest> asientos
    ) {
        return "Bloqueo recibido para evento " + eventoId + " (pendiente de implementación)";
    }


    @PostMapping("/{eventoId}/liberar")
    public String liberarAsientos(
            @PathVariable Long eventoId,
            @RequestBody List<AsientoRequest> asientos
    ) {
        return "Liberación recibida para evento " + eventoId + " (pendiente de implementación)";
    }


    public record AsientoRequest(int fila, int columna) {}
}
