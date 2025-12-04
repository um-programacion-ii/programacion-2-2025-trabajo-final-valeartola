package com.example.backend.web.controller;

import com.example.backend.security.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // DTO para la petición de Login
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    // DTO para la respuesta de Autenticación
    @Data
    public static class AuthResponse {
        private final String jwt;
        private final String message = "Authentication successful";
    }

    /**
     * Endpoint POST /api/authenticate: Procesa el login.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) {
        try {
            // 1. Intenta autenticar. Si las credenciales son incorrectas, lanza una excepción.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );

            // 2. Si la autenticación es exitosa, carga los detalles
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

            // 3. Genera el token JWT
            final String jwt = jwtUtil.generateToken(userDetails);

            // 4. Devuelve el token al cliente
            return ResponseEntity.ok(new AuthResponse(jwt));

        } catch (Exception e) {
            // Manejo de credenciales inválidas (ej: BadCredentialsException)
            return ResponseEntity.status(401).body("{\"error\": \"Credenciales inválidas\"}");
        }
    }

    /**
     * Endpoint de prueba para verificar que la autenticación con JWT funciona.
     */
    @GetMapping("/protected-test")
    public ResponseEntity<String> protectedTest() {
        return ResponseEntity.ok("¡Acceso Protegido Exitoso! (Token JWT OK)");
    }
}
