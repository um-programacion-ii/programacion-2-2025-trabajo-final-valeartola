package com.example.backend.controller;

import com.example.backend.session.UserSession;
import com.example.backend.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/crear")
    public ResponseEntity<UserSession> crearSesion() {
        UserSession session = UserSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .currentStep("inicio")
                .build();

        sessionService.saveSession(session);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<UserSession> obtenerSesion(@PathVariable String sessionId) {
        UserSession session = sessionService.getSession(sessionId);
        if (session == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(session);
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<UserSession> actualizarSesion(
            @PathVariable String sessionId,
            @RequestBody UserSession nuevaSesion
    ) {
        nuevaSesion.setSessionId(sessionId);
        UserSession updated = sessionService.updateSession(sessionId, nuevaSesion);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> eliminarSesion(@PathVariable String sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
