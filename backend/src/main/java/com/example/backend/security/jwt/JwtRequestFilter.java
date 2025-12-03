package com.example.backend.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que se ejecuta una vez por petición para inspeccionar el encabezado
 * Authorization, extraer el token JWT y autenticar al usuario si el token es válido.
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    // Se inyectan el JwtUtil para leer el token y el UserDetailsService para cargar el usuario
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 1. Extraer el token 'Bearer' del encabezado
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // "Bearer " tiene 7 caracteres
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // El token es inválido (ej. expiró, clave incorrecta).
                // No autenticamos, pero permitimos que el filtro continúe para que
                // Spring Security devuelva un 401 si la ruta está protegida.
                logger.warn("Token JWT inválido o expirado: " + e.getMessage());
            }
        }

        // 2. Si el username es válido y NO hay autenticación previa en el contexto de seguridad
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar los detalles del usuario mock
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 3. Validar el token contra el usuario cargado
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // 4. Crear el objeto de autenticación
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Añadir detalles de la petición (ej. IP del cliente)
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. Establecer la autenticación en el contexto de seguridad de Spring
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // Continuar con el siguiente filtro en la cadena
        chain.doFilter(request, response);
    }
}