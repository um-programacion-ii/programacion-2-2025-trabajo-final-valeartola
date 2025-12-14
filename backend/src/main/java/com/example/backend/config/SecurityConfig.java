package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desactivamos CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configuramos los permisos de las rutas
                .authorizeHttpRequests(auth -> auth
                        // Permitimos entrar a /api/invitado para generar la sesión
                        .requestMatchers("/api/invitado").permitAll()

                        // Permitimos ver el catálogo de eventos (público)
                        .requestMatchers("/api/eventos/**").permitAll()

                        // Permitimos los endpoints del carrito y ventas
                        .requestMatchers("/api/carrito/**").permitAll()
                        .requestMatchers("/api/ventas/**").permitAll()

                        // Cualquier otra cosa requiere autenticación (por seguridad general)
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}