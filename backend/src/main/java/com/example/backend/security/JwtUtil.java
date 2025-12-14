package com.example.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // --- GENERAR TOKEN PARA INVITADO ---
    // En el modelo Tótem, el username ES el sessionId
    public String generateTokenWithSession(String sessionId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", "GUEST"); // Agregamos rol por si acaso

        return createToken(claims, sessionId);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // Aquí va el sessionId (guest-xxx)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // --- VALIDACIONES ---
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // --- EXTRAER DATOS ---
    public String extractUsername(String token) { // Para Spring Security
        return extractClaim(token, Claims::getSubject);
    }

    public String extractSessionId(String token) {
        // En tu caso, el sessionId es el mismo Subject
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // --- CLAVE SECRETA SEGURA ---
    private Key getSigningKey() {
        // 1. Intentamos decodificar como Base64 (lo ideal)
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            // 2. Si falla (porque pusiste una contraseña simple en texto),
            // usamos los bytes directos para que no explote.
            // OJO: Esto es menos seguro, pero evita que te trabes ahora.
            return Keys.hmacShaKeyFor(secret.getBytes());
        }
    }
}