package com.example.backend.controller;

import com.example.backend.dto.BloqueoRequestDTO;
import com.example.backend.dto.CarritoDTO;
import com.example.backend.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:3000") // Descomentar para conectar con React
public class CarritoController {

    private final RedisService redisService;

    // 1. BLOQUEAR UN ASIENTO (El Timer de 5 min)
    @PostMapping("/bloquear")
    public ResponseEntity<?> bloquearAsiento(
            @RequestBody BloqueoRequestDTO request,
            @RequestHeader("X-Session-ID") String sessionId // El Front manda el ID de sesión
    ) {
        boolean exito = redisService.intentarBloquear(
                request.eventoId(),
                request.fila(),
                request.columna(),
                sessionId
        );

        if (exito) {
            return ResponseEntity.ok("Asiento bloqueado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El asiento ya está ocupado o bloqueado por otro usuario");
        }
    }

    // 2. ELIMINAR BLOQUEO (Si el usuario se arrepiente)
    @PostMapping("/desbloquear")
    public ResponseEntity<?> desbloquearAsiento(
            @RequestBody BloqueoRequestDTO request
    ) {
        redisService.liberarBloqueo(request.eventoId(), request.fila(), request.columna());
        return ResponseEntity.ok("Bloqueo liberado");
    }

    // 3. ACTUALIZAR CARRITO (Guardar nombres, apellidos, etc.)
    @PutMapping
    public ResponseEntity<?> actualizarCarrito(
            @RequestBody CarritoDTO carrito,
            @RequestHeader("X-Session-ID") String sessionId
    ) {
        redisService.guardarCarrito(sessionId, carrito);
        return ResponseEntity.ok("Carrito guardado en Redis");
    }

    // 4. VER CARRITO (Recuperar estado al recargar página)
    @GetMapping
    public ResponseEntity<CarritoDTO> obtenerCarrito(
            @RequestHeader("X-Session-ID") String sessionId
    ) {
        CarritoDTO carrito = redisService.obtenerCarrito(sessionId);
        return ResponseEntity.ok(carrito);
    }
}