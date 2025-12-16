package com.example.proxy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SincronizadorService {

    private final RestTemplate restTemplate;

    @Value("${backend.service.url}")
    private String backendUrl;

    @Value("${internal.api.secret}")
    private String apiSecret;

    @KafkaListener(topics = "eventos-actualizacion", groupId = "${spring.kafka.consumer.group-id}")
    public void procesarNovedades(String mensajeOriginal) {

        log.info("KAFKA: Novedad recibida de la cátedra: {}", mensajeOriginal);

        try {
            // 1. Preparamos el destino
            String endpoint = backendUrl + "/api/internal/notificacion/evento";

            // 2. Preparamos el sobre (Headers)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiSecret);

            // 3. Preparamos la carta (Body)
            Map<String, String> payload = new HashMap<>();
            payload.put("rawMessage", mensajeOriginal);
            payload.put("origen", "KAFKA_PROXY");

            HttpEntity<Map<String, String>> peticion = new HttpEntity<>(payload, headers);

            // 4. ¡Enviamos!
            restTemplate.postForObject(endpoint, peticion, String.class);
            log.info("Reenviado al Backend con éxito.");

        } catch (Exception e) {
            log.error("🔥 Error al intentar sincronizar con el Backend: {}", e.getMessage());
        }
    }
}