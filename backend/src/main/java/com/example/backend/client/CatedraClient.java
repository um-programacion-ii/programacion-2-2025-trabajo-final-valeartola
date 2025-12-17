package com.example.backend.client;

import com.example.backend.dto.AsientoOcupadoExternoDTO;
import com.example.backend.dto.EventoResumenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CatedraClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${catedra.url}")
    private String baseUrl;

    @Value("${catedra.api.token}")
    private String token;

    public List<EventoResumenDTO> getEventosResumidos() {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(baseUrl + "/api/eventos")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<EventoResumenDTO>>() {})
                    .block();
        } catch (Exception e) {
            System.out.println("Error en CatedraClient: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<AsientoOcupadoExternoDTO> getAsientosOcupados(Long idEvento) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(baseUrl + "/api/endpoints/v1/eventos/" + idEvento + "/asientos")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<AsientoOcupadoExternoDTO>>() {})
                    .block();
        } catch (Exception e) {
            System.out.println("Error buscando asientos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Object enviarCompra(Object solicitud) {
        System.out.println("MODO MOCK ACTIVADO: Simulando respuesta exitosa de Cátedra...");
        return Map.of("mensaje", "Compra exitosa (SIMULADA)", "idTicket", 99999);
    }
}