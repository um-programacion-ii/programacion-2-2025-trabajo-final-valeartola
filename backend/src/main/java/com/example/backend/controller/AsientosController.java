package com.example.backend.controller;

import com.example.backend.dto.TicketItemDTO;
import com.example.backend.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asientos")
@RequiredArgsConstructor
public class AsientosController {

    private final VentaService ventaService;
    private final RestTemplate restTemplate;

    @GetMapping("/{eventoId}")
    public ResponseEntity<String> obtenerMapaAsientos(@PathVariable Long eventoId) {
        try {
            // Llamamos a tu Proxy para obtener el estado actual de los asientos
            String urlProxy = "http://localhost:8081/proxy/redis/evento/" + eventoId;
            String mapa = restTemplate.getForObject(urlProxy, String.class);
            return ResponseEntity.ok(mapa);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al conectar con el Proxy de Redis");
        }
    }
    @PostMapping("/{eventoId}/bloquear")
    public ResponseEntity<?> bloquearAsientos(
            @PathVariable Long eventoId,
            @RequestBody List<TicketItemDTO> asientos
    ) {
        try {
            // Usamos el metodo que creamos en VentaService para el Payload 6
            Map<String, Object> resultado = ventaService.reservarAsientosEnCatedra(eventoId, asientos);

            if (Boolean.TRUE.equals(resultado.get("resultado"))) {
                return ResponseEntity.ok(resultado);
            } else {
                return ResponseEntity.status(409).body(resultado); // Conflicto: ya están ocupados
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar el bloqueo externo");
        }
    }

    @PostMapping("/{eventoId}/liberar")
    public String liberarAsientos(
            @PathVariable Long eventoId,
            @RequestBody List<TicketItemDTO> asientos
    ) {
        // Aquí podrías llamar a tu redisService.liberarAsientos(...)
        return "Asientos liberados localmente para el evento " + eventoId;
    }
}
