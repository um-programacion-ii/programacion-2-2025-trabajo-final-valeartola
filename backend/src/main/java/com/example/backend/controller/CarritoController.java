package com.example.backend.controller;

import com.example.backend.dto.BloqueoRequestDTO;
import com.example.backend.dto.CarritoDTO;
import com.example.backend.repository.EventoRepository;
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
    private final EventoRepository eventoRepository;

    /**
     * 1. BLOQUEO LOCAL (Timer de 5 min)
     * Este es el "primer bloqueo". El que asegura que nadie más en TU sistema
     * elija estos asientos mientras el usuario llena sus datos.
     */
    @PostMapping("/reservar")
    public ResponseEntity<?> reservarAsientoLocal(
            @RequestHeader("X-Session-ID") String sessionId,
            @RequestBody BloqueoRequestDTO request
    ) {
        // Validamos que el evento exista en nuestra base de datos
        if (!eventoRepository.existsById(request.eventoId())) {
            return ResponseEntity.badRequest().body("Evento no encontrado");
        }

        boolean exito = redisService.intentarBloquear(
                request.eventoId(),
                request.fila(),
                request.columna(),
                sessionId
        );

        if (exito) {
            return ResponseEntity.ok("Asiento reservado temporalmente en tu sesión");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El asiento ya está siendo reservado por otro usuario");
        }
    }

    /**
     * 2. ACTUALIZAR CARRITO
     * Guarda la lista completa de asientos que el usuario tiene seleccionados.
     */
    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarCarrito(
            @RequestHeader("X-Session-ID") String sessionId,
            @RequestBody CarritoDTO carrito
    ) {
        redisService.guardarCarrito(sessionId, carrito);
        return ResponseEntity.ok("Lista de asientos actualizada en tu sesión");
    }

    /**
     * 3. OBTENER CARRITO
     * Recupera lo que el usuario tiene seleccionado (útil si refresca la página).
     */
    @GetMapping
    public ResponseEntity<CarritoDTO> obtenerCarrito(
            @RequestHeader("X-Session-ID") String sessionId
    ) {
        CarritoDTO carrito = redisService.obtenerCarrito(sessionId);
        if (carrito == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(carrito);
    }

    /**
     * 4. ELIMINAR/VACIAR CARRITO
     */
    @DeleteMapping("/limpiar")
    public ResponseEntity<?> vaciarCarrito(@RequestHeader("X-Session-ID") String sessionId) {
        redisService.limpiarCarrito(sessionId);
        return ResponseEntity.ok("Carrito vaciado");
    }
}