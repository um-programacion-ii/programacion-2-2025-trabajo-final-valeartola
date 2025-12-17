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

    @PostMapping("/invitado")
    public ResponseEntity<?> iniciarSesionInvitado() {
        String guestId = "guest-" + UUID.randomUUID().toString();

        String jwt = jwtUtil.generateTokenWithSession(guestId);

        return ResponseEntity.ok(new AuthResponse(jwt, guestId));
    }

    @Data
    public static class AuthResponse {
        private final String jwt;
        private final String sessionId;
    }
}
