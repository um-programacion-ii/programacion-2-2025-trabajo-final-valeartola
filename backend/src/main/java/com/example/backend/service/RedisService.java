package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_BLOQUEO = "bloqueo:evento:";

    public boolean intentarBloquear(Long eventoId, int fila, int col, String sessionId) {
        String key = generarKeyBloqueo(eventoId, fila, col);
        // Bloqueo de 5 minutos en tu Redis local
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, sessionId, Duration.ofMinutes(5))
        );
    }

    public void liberarBloqueo(Long eventoId, int fila, int col) {
        redisTemplate.delete(generarKeyBloqueo(eventoId, fila, col));
    }

    public Set<String> obtenerBloqueados(Long eventoId) {
        String pattern = KEY_BLOQUEO + eventoId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        Set<String> bloqueados = new HashSet<>();
        if (keys != null) {
            for (String key : keys) {
                String[] partes = key.split(":");
                bloqueados.add(partes[partes.length - 1]);
            }
        }
        return bloqueados;
    }

    private String generarKeyBloqueo(Long eventoId, int fila, int col) {
        return KEY_BLOQUEO + eventoId + ":" + fila + "-" + col;
    }
}