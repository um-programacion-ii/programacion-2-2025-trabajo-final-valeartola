package com.example.backend.controller;

import com.example.backend.model.Evento;
import com.example.backend.repository.EventoRepository;
import com.example.backend.service.EventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoRepository eventoRepository;
    private final EventoService eventoService;

    public EventoController(EventoRepository eventoRepository, EventoService eventoService) {
        this.eventoRepository = eventoRepository;
        this.eventoService = eventoService;
    }

    // -------------------------------
    // 🔵 EVENTOS LOCALES (tu código)
    // -------------------------------

    @GetMapping
    public ResponseEntity<List<Evento>> getAllEventos() {
        List<Evento> eventos = eventoRepository.findAll();

        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> getEventoById(@PathVariable Long id) {
        return eventoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // ----------------------------------------
    // 🟦 EVENTOS DESDE LA CÁTEDRA (nuevo)
    // ----------------------------------------

    @GetMapping("/catedra")
    public ResponseEntity<?> getEventosDesdeCatedra() {
        return ResponseEntity.ok(eventoService.obtenerEventosDesdeCatedra());
    }

    @GetMapping("/{id}/catedra")
    public ResponseEntity<?> getEventoDesdeCatedra(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.obtenerEventoDesdeCatedra(id));
    }
}
