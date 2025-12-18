package com.example.backend.controller;

import com.example.backend.dto.DatosCompradorDTO;
import com.example.backend.model.Venta;
import com.example.backend.repository.VentaRepository;
import com.example.backend.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaRepository ventaRepository;
    private final VentaService ventaService;

    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarCompra(
            @RequestHeader("X-Session-ID") String sessionId,
            @RequestBody DatosCompradorDTO comprador // <--- Usamos tu nuevo DTO
    ) {
        try {
            // Pasamos el DTO completo al service
            Object ticket = ventaService.procesarCompra(sessionId, comprador);
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar la compra");
        }
    }

    @GetMapping("/detalle/{id}")
    public ResponseEntity<Map<String, Object>> verDetalleVenta(@PathVariable Long id) {
        Map<String, Object> detalle = ventaService.obtenerDetalleVenta(id);
        return ResponseEntity.ok(detalle);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<Map<String, Object>>> obtenerHistorial() {
        return ResponseEntity.ok(ventaService.listarVentasCatedra());
    }

    @GetMapping
    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }
}
