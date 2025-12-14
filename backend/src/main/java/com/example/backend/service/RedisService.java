package com.example.backend.service;

import com.example.backend.dto.CarritoDTO;
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

    // Prefijos para organizar las claves dentro de Redis
    // Ejemplo de clave final: "bloqueo:evento:1:5-10" (Evento 1, Fila 5, Columna 10)
    private static final String KEY_BLOQUEO = "bloqueo:evento:";
    // Ejemplo de clave final: "carrito:abc-123-xyz" (SessionId)
    private static final String KEY_CARRITO = "carrito:";


    /**
     * Intenta bloquear un asiento específico.
     */
    public boolean intentarBloquear(Long eventoId, int fila, int col, String sessionId) {
        String key = generarKeyBloqueo(eventoId, fila, col);


        Boolean exito = redisTemplate.opsForValue().setIfAbsent(key, sessionId, Duration.ofMinutes(5));

        return Boolean.TRUE.equals(exito);
    }

    /**
     * Libera un asiento manualmente (ej: si el usuario lo elimina del carrito).
     */
    public void liberarBloqueo(Long eventoId, int fila, int col) {
        String key = generarKeyBloqueo(eventoId, fila, col);
        redisTemplate.delete(key);
    }

    /**
     * Devuelve todos los asientos bloqueados actualmente para un evento.
     */
    public Set<String> obtenerBloqueados(Long eventoId) {
        // Buscamos todas las claves que coincidan con el patrón del evento
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

    /**
     * Guarda el estado actual de la compra (asientos + datos comprador).
     */
    public void guardarCarrito(String sessionId, CarritoDTO carrito) {
        redisTemplate.opsForValue().set(KEY_CARRITO + sessionId, carrito, Duration.ofMinutes(30));
    }

    /**
     * Recupera el carrito. Si no existe, devuelve uno nuevo vacío.
     */
    public CarritoDTO obtenerCarrito(String sessionId) {
        Object obj = redisTemplate.opsForValue().get(KEY_CARRITO + sessionId);


        if (obj instanceof CarritoDTO) {
            return (CarritoDTO) obj;
        }
        return new CarritoDTO();
    }

    /**
     * Borra el carrito completo (se usa al finalizar la compra).
     */
    public void limpiarCarrito(String sessionId) {
        redisTemplate.delete(KEY_CARRITO + sessionId);
    }
    public boolean verificarBloqueo(Long eventoId, int fila, int col, String sessionId) {
        String key = generarKeyBloqueo(eventoId, fila, col);
        Object duenoDelBloqueo = redisTemplate.opsForValue().get(key);

        if (duenoDelBloqueo == null) return false;

        return sessionId.equals(duenoDelBloqueo.toString());
    }
}