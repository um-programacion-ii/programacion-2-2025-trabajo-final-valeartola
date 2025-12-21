package com.example.proxy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/proxy/redis")
@RequiredArgsConstructor
@Slf4j
public class RedisController {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper; // Para manipular el JSON de los asientos

    /**
     * CONSULTA (GET)
     * Usado por el Backend para obtener el mapa de asientos actual.
     */
    @GetMapping("/evento/{id}")
    public ResponseEntity<String> obtenerAsientosEvento(@PathVariable Long id) {
        String key = "evento_" + id;
        String asientosJson = redisTemplate.opsForValue().get(key);

        if (asientosJson == null) {
            return ResponseEntity.ok("[]");
        }
        return ResponseEntity.ok(asientosJson);
    }

    /**
     * BLOQUEO (POST) - Payload 6
     * Este es el mettodo que te faltaba y causaba el error 404.
     * Recibe la orden del Backend y marca los asientos como OCUPADOS en el JSON del Redis.
     */
    @PostMapping("/bloquear")
    public ResponseEntity<?> bloquearAsientosEnCache(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Extraer datos del Payload
            Integer eventoId = (Integer) payload.get("eventoId");
            List<Map<String, Object>> asientosABloquear = (List<Map<String, Object>>) payload.get("asientos");

            String key = "evento_" + eventoId;
            String asientosJson = redisTemplate.opsForValue().get(key);

            if (asientosJson == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento no encontrado en el Proxy");
            }

            // 2. Procesar el JSON para cambiar el estado de los asientos
            JsonNode rootNode = objectMapper.readTree(asientosJson);
            if (rootNode.isArray()) {
                ArrayNode asientosArray = (ArrayNode) rootNode;

                for (Map<String, Object> asientoReq : asientosABloquear) {
                    int filaReq = (int) asientoReq.get("fila");
                    int colReq = (int) asientoReq.get("columna");

                    // Buscamos el asiento en el array del JSON
                    for (JsonNode asientoNode : asientosArray) {
                        if (asientoNode.get("fila").asInt() == filaReq &&
                                asientoNode.get("columna").asInt() == colReq) {

                            // Cambiamos el estado a BLOQUEADO
                            ((ObjectNode) asientoNode).put("estado", "BLOQUEADO");
                            log.info("Asiento F:{} C:{} marcado como BLOQUEADO en Proxy", filaReq, colReq);
                        }
                    }
                }

                // 3. Guardar el JSON actualizado de vuelta en Redis
                String nuevoJson = objectMapper.writeValueAsString(asientosArray);
                redisTemplate.opsForValue().set(key, nuevoJson);
            }

            return ResponseEntity.ok("Bloqueo sincronizado en el Proxy");

        } catch (JsonProcessingException e) {
            log.error("Error al procesar el JSON de asientos", e);
            return ResponseEntity.internalServerError().body("Error interno al actualizar el caché");
        } catch (Exception e) {
            log.error("Error inesperado en el Proxy", e);
            return ResponseEntity.badRequest().body("Datos de bloqueo inválidos");
        }
    }
}