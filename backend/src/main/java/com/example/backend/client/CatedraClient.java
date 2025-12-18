package com.example.backend.client;

import com.example.backend.dto.AsientoOcupadoExternoDTO;
import com.example.backend.dto.EventoResumenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CatedraClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${catedra.url}")
    private String baseUrl;

    @Value("${catedra.api.token}")
    private String token;

    /**
     * Obtiene la lista de eventos desde la API de la Cátedra
     */
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
            log.error("Error al obtener eventos de la Cátedra: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Obtiene los asientos ocupados/bloqueados de un evento específico
     */
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
            log.error("Error buscando asientos para el evento {}: {}", idEvento, e.getMessage());
            return Collections.emptyList();
        }
    }

    public Object bloquearAsientosExterno(Object payload6) {
        try {
            return webClientBuilder.build()
                    .post()
                    .uri(baseUrl + "/api/endpoints/v1/bloquear-asientos") // Ajusta la URL según el PDF de la cátedra
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(payload6)
                    .retrieve()
                    .bodyToMono(Map.class) // Esto hace que el retorno sea un Map
                    .block();
        } catch (Exception e) {
            log.error("Error al bloquear asientos en Cátedra: {}", e.getMessage());
            return Map.of("resultado", false, "descripcion", "Error de conexión con la Cátedra");
        }
    }

    /**
     * Llama al endpoint de la Cátedra para confirmar la compra (Payload 8)
     */
    public Object enviarCompraReal(Object payload) {
        try {
            return webClientBuilder.build()
                    .post()
                    .uri(baseUrl + "/api/endpoints/v1/realizar-venta")
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            log.error("Error al enviar compra a la Cátedra: {}", e.getMessage());
            return Map.of("resultado", false, "descripcion", "Error de comunicación");
        }
    }

    public List<Map<String, Object>> obtenerHistorialVentas() {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(baseUrl + "/api/endpoints/v1/listar-ventas")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                    .block();
        } catch (Exception e) {
            log.error("Error al obtener historial de ventas: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public Map<String, Object> obtenerDetalleVentaExterno(Long ventaId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(baseUrl + "/api/endpoints/v1/listar-venta/" + ventaId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            log.error("Error al obtener detalle de la venta {}: {}", ventaId, e.getMessage());
            return Map.of("resultado", false, "descripcion", "Error al conectar con la cátedra");
        }
    }
}