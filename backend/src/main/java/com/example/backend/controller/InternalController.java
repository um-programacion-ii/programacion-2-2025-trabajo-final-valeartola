package com.example.backend.controller; // Ajusta el paquete a tu proyecto

import com.example.backend.service.SincronizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalController {

    private final SincronizacionService sincronizacionService;

    @PostMapping("/notificacion/evento")
    public ResponseEntity<String> recibirNotificacion(@RequestBody Map<String, String> payload) {

        String mensaje = payload.get("rawMessage");

        System.out.println("InternalController: Notificación recibida: " + mensaje);

        sincronizacionService.procesarNotificacion(mensaje);

        return ResponseEntity.ok("Notificación procesada con éxito");
    }
}