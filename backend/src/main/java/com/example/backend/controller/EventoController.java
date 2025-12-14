package com.example.backend.controller;

import com.example.backend.dto.EventoResumenDTO;
import com.example.backend.model.EstadoEvento;
import com.example.backend.model.Evento;
import com.example.backend.repository.EventoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoRepository eventoRepository;

    public EventoController(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @GetMapping
    public List<Evento> obtenerEventosActivos() {
        return eventoRepository.findByEstado(EstadoEvento.ACTIVO);
    }

    @GetMapping("/resumidos")
    public List<EventoResumenDTO> obtenerEventosResumidos() {
        return eventoRepository.findByEstado(EstadoEvento.ACTIVO)
                .stream()
                // CORRECCIÓN: Hacemos el mapeo manualmente aquí con una lambda
                .map(evento -> new EventoResumenDTO(
                        evento.getId(),
                        evento.getTitulo(),
                        evento.getResumen(),
                        evento.getFecha().toString(), // Convertimos Instant a String
                        evento.getPrecioEntrada()     // Mapeamos precioEntrada a precio
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public Evento obtenerEventoPorId(@PathVariable Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Validación extra de estado
        if (evento.getEstado() == EstadoEvento.BAJA) {
            throw new RuntimeException("Evento dado de baja");
        }
        return evento;
    }

    @GetMapping("/catedra/{idCatedra}")
    public ResponseEntity<?> obtenerPorIdCatedra(@PathVariable Long idCatedra) {
        return eventoRepository.findByIdCatedra(idCatedra)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/asientos")
    public String obtenerMapaAsientos(@PathVariable Long id) {
        return "Mapa de asientos (implementación pendiente - ver EventoService)";
    }
}