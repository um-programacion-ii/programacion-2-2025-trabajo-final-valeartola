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
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/internal/**").permitAll()
                        .requestMatchers("api/sessions/**").permitAll()
                        .requestMatchers("/api/invitado").permitAll()
                        .requestMatchers("/api/eventos/**").permitAll()
                        .requestMatchers("/api/carrito/**").permitAll()
                        .requestMatchers("/api/ventas/**").permitAll()
                        .requestMatchers("/api/asientos/**").permitAll()


                        .anyRequest().authenticated()

                );

        return http.build();
    }
}
