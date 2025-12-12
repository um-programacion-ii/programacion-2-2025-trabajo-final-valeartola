package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    @PostMapping("/eventos")
    public String sincronizarEventos() {
        return "Solicitud de sincronización de eventos recibida (implementación pendiente)";
    }


    @PostMapping("/full")
    public String sincronizacionCompleta() {
        return "Sincronización completa solicitada (implementación pendiente)";
    }
}
