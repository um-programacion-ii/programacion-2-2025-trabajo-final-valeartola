package com.example.backend.config;

import com.example.backend.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Se eliminó la inyección por constructor de JwtRequestFilter para evitar la dependencia circular.

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // Bean para exponer el AuthenticationManager (necesario en AuthController)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * CADENA DE FILTROS DE SEGURIDAD
     * CLAVE para romper el ciclo: Inyectamos el JwtRequestFilter como un parámetro del metodo.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Permitir el acceso sin autenticación a la ruta de login
                        .requestMatchers("/api/authenticate").permitAll()
                        // Requerir autenticación para cualquier otra petición
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        // Configurar la aplicación para que sea sin estado (Stateless)
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Añadir nuestro filtro JWT ANTES del filtro estándar de Spring Security
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
