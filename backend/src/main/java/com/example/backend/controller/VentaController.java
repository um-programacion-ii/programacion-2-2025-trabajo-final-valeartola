package com.example.backend.controller;

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
