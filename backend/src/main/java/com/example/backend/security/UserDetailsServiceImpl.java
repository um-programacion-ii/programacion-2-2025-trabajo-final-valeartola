package com.example.backend.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementación del servicio de detalles de usuario (UserDetailsService).
 * Usa inyección por constructor (Buena Práctica) y provee un usuario mock.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Dependencia del codificador de contraseñas. Se inyecta por el constructor.
    private final PasswordEncoder passwordEncoder;

    // Contraseña mock ya codificada (BCrypt) para optimizar el rendimiento.
    private final String encodedMockPassword;

    /**
     * Constructor para la inyección de dependencias.
     * El PasswordEncoder se inyecta desde SecurityConfig.
     */
    public UserDetailsServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        // Cifrar la contraseña solo una vez al iniciar el servicio.
        this.encodedMockPassword = passwordEncoder.encode("password123");
    }

    /**
     * Carga el usuario por nombre de usuario.
     * En esta fase, solo reconoce al usuario 'mobileuser'.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // --- MOCK DE USUARIO TEMPORAL ---
        if ("mobileuser".equalsIgnoreCase(username)) {
            // Devuelve un objeto UserDetails de Spring Security
            return new User(
                    username,
                    encodedMockPassword, // Contraseña ya cifrada
                    Collections.emptyList() // Autoridades vacías
            );
        }

        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}