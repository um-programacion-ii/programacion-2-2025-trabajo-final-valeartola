package com.example.backend.controller;

import com.example.backend.security.JwtUtil;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // EL FRONTEND LLAMA A ESTO AL INICIAR LA APP
    @PostMapping("/invitado")
    public ResponseEntity<?> iniciarSesionInvitado() {
        // 1. Generamos un ID único para este cliente
        String guestId = "guest-" + UUID.randomUUID().toString();

        // 2. Generamos el Token JWT para ese ID fantasma
        // (Asegúrate de haber agregado el metodo generateToken(String) en el paso 1)
        String jwt = jwtUtil.generateToken(guestId);

        return ResponseEntity.ok(new AuthResponse(jwt, guestId));
    }

    @Data
    public static class AuthResponse {
        private final String jwt;
        private final String sessionId;
    }
}
