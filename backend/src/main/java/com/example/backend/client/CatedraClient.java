package com.example.backend.client;

import com.example.backend.dto.AsientoOcupadoExternoDTO;
import com.example.backend.dto.EventoExternoDTO;
import com.example.backend.dto.EventoResumenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CatedraClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${catedra.url}")
    private String baseUrl;

    @Value("${catedra.token}")
    private String token;

    // 1. Traer lista de eventos (Devuelve DTO EXTERNO)
    public List<EventoExternoDTO> getEventosResumidos() {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(baseUrl + "/api/endpoints/v1/eventos-resumidos") // Revisa esta URL con el profe
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<EventoExternoDTO>>() {})
                    .block(); // Síncrono (bloqueante)
        } catch (Exception e) {
            // Manejo básico de errores: devolver lista vacía o lanzar excepción personalizada
            System.err.println("Error conectando con Cátedra: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 2. Traer asientos ocupados de un evento específico
    // Si es JSON puro, esto funciona. Si es un String raro, habría que parsearlo manualmente.
    public List<AsientoOcupadoExternoDTO> getAsientosOcupados(Long idEvento) {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/api/endpoints/v1/eventos/" + idEvento + "/asientos") // URL hipotética
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AsientoOcupadoExternoDTO>>() {})
                .block();
    }

    public Object enviarCompra(Object solicitud) {
        // Implementación básica para que compile la lógica de negocios.
        // En el siguiente issue refinas la URL y el manejo de errores.
        return webClientBuilder.build()
                .post()
                .uri(baseUrl + "/api/endpoints/v1/ventas") // URL a confirmar
                .header("Authorization", "Bearer " + token)
                .bodyValue(solicitud)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}