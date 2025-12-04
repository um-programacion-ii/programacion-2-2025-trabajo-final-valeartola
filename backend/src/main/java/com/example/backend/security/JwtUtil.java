package com.example.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Clase de utilidad para gestionar los JSON Web Tokens (JWT):
 * crear, validar y extraer información de ellos.
 *
 * Utiliza la sintaxis moderna de la librería JJWT (0.12.x) que requiere
 * parserBuilder().build() para la lectura.
 */
@Component
public class JwtUtil {

    // Se inyectan las propiedades definidas en application.yml (o application.properties)
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Genera un token JWT a partir de los detalles del usuario.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Opcional: añadir los roles del usuario como claims en el token
        claims.put("roles", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        // Usa la sintaxis Jwts.builder() con setClaims() para la construcción
        return Jwts.builder()
                .setClaims(claims) // Carga los claims personalizados
                .setSubject(subject) // Establece el nombre de usuario (subject)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Fecha de expiración
                .signWith(getSigningKey()) // Firma el token con la clave secreta
                .compact(); // Finaliza y comprime el token
    }

    // --- Métodos de Extracción y Validación ---

    /**
     * Extrae el nombre de usuario (subject) del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, io.jsonwebtoken.Claims::getSubject);
    }

    /**
     * Valida si el token es legítimo (firma OK) y no ha expirado.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // --- Métodos de bajo nivel ---

    private boolean isTokenExpired(String token) {
        return extractClaim(token, io.jsonwebtoken.Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<io.jsonwebtoken.Claims, T> claimsResolver) {
        final io.jsonwebtoken.Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private io.jsonwebtoken.Claims extractAllClaims(String token) {
        // Uso de parserBuilder().setSigningKey().build() necesario para versiones modernas de JJWT
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build() // Crea el parser
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Decodifica la clave secreta (Base64) en un objeto Key utilizable para la firma.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}