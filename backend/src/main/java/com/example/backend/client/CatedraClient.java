package com.example.backend.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CatedraClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${catedra.url}")
    private String baseUrl;

    @Value("${catedra.token}")
    private String token;

    public Object getEventosResumidos() {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/api/endpoints/v1/eventos-resumidos")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object getEventoCompleto(Long id) {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/api/endpoints/v1/evento/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}