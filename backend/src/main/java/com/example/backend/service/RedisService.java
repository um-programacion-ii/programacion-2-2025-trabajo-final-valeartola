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

    // ==========================================
    // 🛑 LÓGICA DE BLOQUEOS (El Timer de 5 min)
    // ==========================================

    /**
     * Intenta bloquear un asiento específico.
     * @return true si logró bloquearlo, false si ya estaba ocupado por otro.
     */
    public boolean intentarBloquear(Long eventoId, int fila, int col, String sessionId) {
        String key = generarKeyBloqueo(eventoId, fila, col);

        // 'setIfAbsent' es la clave: es una operación ATÓMICA.
        // Si la clave NO existe, la crea y devuelve TRUE.
        // Si la clave YA existe (alguien ganó de mano), no hace nada y devuelve FALSE.
        // Además, le ponemos expiración automática de 5 minutos.
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
     * Usado por EventoService para pintar los asientos en gris/rojo.
     */
    public Set<String> obtenerBloqueados(Long eventoId) {
        // Buscamos todas las claves que coincidan con el patrón del evento
        String pattern = KEY_BLOQUEO + eventoId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);

        Set<String> bloqueados = new HashSet<>();
        if (keys != null) {
            for (String key : keys) {
                // La key viene completa: "bloqueo:evento:1:5-10"
                // Cortamos el String para quedarnos solo con "5-10"
                String[] partes = key.split(":");
                // Asumimos que la parte "fila-col" es siempre la última
                bloqueados.add(partes[partes.length - 1]);
            }
        }
        return bloqueados;
    }

    private String generarKeyBloqueo(Long eventoId, int fila, int col) {
        return KEY_BLOQUEO + eventoId + ":" + fila + "-" + col;
    }

    // ==========================================
    // 🛒 LÓGICA DEL CARRITO (Persistencia Sesión)
    // ==========================================

    /**
     * Guarda el estado actual de la compra (asientos + datos comprador).
     * Se renueva el tiempo de vida (TTL) a 30 mins con cada actualización.
     */
    public void guardarCarrito(String sessionId, CarritoDTO carrito) {
        redisTemplate.opsForValue().set(KEY_CARRITO + sessionId, carrito, Duration.ofMinutes(30));
    }

    /**
     * Recupera el carrito. Si no existe, devuelve uno nuevo vacío.
     * Esto evita NullPointerExceptions en el Controller.
     */
    public CarritoDTO obtenerCarrito(String sessionId) {
        Object obj = redisTemplate.opsForValue().get(KEY_CARRITO + sessionId);

        // Jackson y Redis a veces devuelven LinkedHashMap si la deserialización falla,
        // pero con la configuración correcta de RedisConfig devuelve CarritoDTO.
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

        // Si nadie lo tiene bloqueado, o lo tiene otro -> Retorna false
        if (duenoDelBloqueo == null) return false;

        // Solo retorna true si el sessionId coincide con el que guardamos en Redis
        return sessionId.equals(duenoDelBloqueo.toString());
    }
}