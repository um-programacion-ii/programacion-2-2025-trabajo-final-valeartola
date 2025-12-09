package com.example.backend.catedra;

import com.example.backend.event.model.Evento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final CatedraService catedraService;

    public SyncController(CatedraService catedraService) {
        this.catedraService = catedraService;
    }

    @GetMapping
    public ResponseEntity<?> sincronizar() {
        try {
            List<Evento> eventos = catedraService.sincronizarEventos();
            return ResponseEntity.ok("Sincronización finalizada. Eventos guardados: " + eventos.size());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}