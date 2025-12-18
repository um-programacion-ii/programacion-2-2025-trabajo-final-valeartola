package com.example.proxy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proxy/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisTemplate<String, String> redisTemplate;

    // Endpoint que llamará tu Backend
    // GET http://localhost:8081/proxy/redis/evento/1
    @GetMapping("/evento/{id}")
    public ResponseEntity<String> obtenerAsientosEvento(@PathVariable Long id) {
        // 1. Construimos la clave
        String key = "evento_" + id;

        // 2. Buscamos en Redis
        String asientosJson = redisTemplate.opsForValue().get(key);

        // 3. Validación
        if (asientosJson == null) {
            return ResponseEntity.ok("[]");
        }

        // 4. Devolvemos el JSON tal cual vino de la Cátedra
        return ResponseEntity.ok(asientosJson);
    }


}