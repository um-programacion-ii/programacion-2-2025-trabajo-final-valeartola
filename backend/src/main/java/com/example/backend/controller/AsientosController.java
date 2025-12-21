package com.example.backend.controller;

import com.example.backend.dto.bloqueo.BloqueoRequestDTO;
import com.example.backend.dto.bloqueo.ReservaRequestDTO;
import com.example.backend.dto.venta.CompraRequestDTO;
import com.example.backend.service.AsientoService;
import com.example.backend.service.RedisService;
import com.example.backend.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asientos")
@RequiredArgsConstructor
public class AsientosController {

    private final RedisService redisService;
    private final VentaService ventaService;
    private final AsientoService asientoService;

    @PostMapping("/reservar")
    public ResponseEntity<?> reservar(@RequestBody ReservaRequestDTO request) {
        Object resultado = asientoService.bloquearAsiento(request);
        return ResponseEntity.ok(resultado);
    }

    // 2. COMPRAR: Recibe el ID del evento y la lista de asientos con nombres y apellidos
    @PostMapping("/comprar")
    public ResponseEntity<?> confirmarCompra(
            @RequestHeader("X-Session-ID") String sessionId,
            @RequestBody CompraRequestDTO compraRequest
    ) {
        try {
            // El service arma el Payload 7 y lo envía a la cátedra
            Object respuesta = ventaService.procesarCompra(compraRequest);

            // Si la compra fue exitosa, liberamos los bloqueos de Redis
            // (Opcional: podés hacerlo dentro del service)
            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar la compra");
        }
    }
}
