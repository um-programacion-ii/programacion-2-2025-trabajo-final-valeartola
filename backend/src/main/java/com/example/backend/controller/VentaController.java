package com.example.backend.controller;

import com.example.backend.model.Venta;
import com.example.backend.repository.VentaRepository;
import com.example.backend.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaRepository ventaRepository;
    private final VentaService ventaService;

    // Endpoint para confirmar la compra (Llama al Service)
    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarCompra(@RequestHeader("X-Session-ID") String sessionId) {
        try {
            Object ticket = ventaService.procesarCompra(sessionId);
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    // Endpoints de lectura (Ya los tenías, se mantienen igual)
    @GetMapping("/{id}")
    public Venta obtenerVentaPorId(@PathVariable Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    @GetMapping
    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }
}
