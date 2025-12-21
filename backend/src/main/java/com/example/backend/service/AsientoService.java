package com.example.backend.service;

import com.example.backend.client.CatedraClient;
import com.example.backend.dto.asiento.AsientoOcupadoExternoDTO;
import com.example.backend.dto.asiento.EstadoAsientoDTO;
import com.example.backend.dto.asiento.RespuestaRedisDTO;
import com.example.backend.dto.bloqueo.ReservaRequestDTO;
import com.example.backend.model.Evento;
import com.example.backend.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsientoService {

    private final EventoRepository eventoRepository;
    private final RestTemplate restTemplate;
    private final CatedraClient catedraClient;

    @Value("${PROXY_SERVICE_URL}")
    private String proxyBaseUrl;

    @Value("${catedra.url}")
    private String baseUrl;

    @Value("${catedra.api.token}")
    private String catedraToken;


    public List<EstadoAsientoDTO> obtenerMapaCompleto(Long eventoId) {
        // 1. Obtener dimensiones del evento desde nuestra DB local
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // 2. Consultar asientos ocupados/bloqueados al Proxy (Redis)
        String url = proxyBaseUrl + "/proxy/redis/evento/" + eventoId;
        RespuestaRedisDTO redisData;
        try {
            redisData = restTemplate.getForObject(url, RespuestaRedisDTO.class);
        } catch (Exception e) {
            log.error("Error al conectar con Proxy Redis: {}", e.getMessage());
            redisData = new RespuestaRedisDTO(eventoId, new ArrayList<>());
        }

        List<EstadoAsientoDTO> mapaFinal = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        // 3. Construir la matriz (Filas x Columnas)
        for (int f = 1; f <= evento.getFilas(); f++) {
            for (int c = 1; c <= evento.getColumnas(); c++) {

                final int filaActual = f;
                final int colActual = c;

                // Buscamos si el asiento (f, c) figura en la respuesta de Redis
                Optional<AsientoOcupadoExternoDTO> ocupado = redisData.asientos().stream()
                        .filter(a -> a.fila() == filaActual && a.columna() == colActual)
                        .findFirst();

                String estado = "LIBRE";

                if (ocupado.isPresent()) {
                    AsientoOcupadoExternoDTO info = ocupado.get();

                    if ("Vendido".equalsIgnoreCase(info.estado())) {
                        estado = "VENDIDO";
                    } else if ("BLOQUEADO".equalsIgnoreCase(info.estado())) {
                        // VALIDACIÓN CRÍTICA: ¿Expiró el bloqueo?
                        if (info.expira() != null) {
                            LocalDateTime fechaExpira = ZonedDateTime.parse(info.expira()).toLocalDateTime();
                            estado = ahora.isBefore(fechaExpira) ? "BLOQUEADO" : "LIBRE";
                        }
                    }
                }
                mapaFinal.add(new EstadoAsientoDTO(f, c, estado));
            }
        }
        return mapaFinal;
    }

    public Object bloquearAsiento(ReservaRequestDTO request) {
        // 1. VALIDACIÓN: Recorremos los asientos para ver si tienen nombre y apellido
        for (var asiento : request.asientos()) {
            if (asiento.nombre() == null || asiento.nombre().isBlank() ||
                    asiento.apellido() == null || asiento.apellido().isBlank()) {

                log.error("Intento de compra sin datos completos: {}", asiento);
                return Map.of(
                        "resultado", false,
                        "descripcion", "Error: El nombre y apellido son obligatorios para todos los asientos."
                );
            }
        }

        // 2. Si pasó la validación, seguimos con el proceso normal
        Map<String, Object> payload6 = Map.of(
                "eventoId", request.eventoId(),
                "asientos", request.asientos()
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + catedraToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload6, headers);

            String urlCatedra = baseUrl + "/api/endpoints/v1/bloquear-asientos";

            // Llamamos a la Cátedra
            ResponseEntity<Object> respuesta = restTemplate.postForEntity(urlCatedra, entity, Object.class);

            // Notificamos al Proxy (Redis)
            try {
                restTemplate.postForEntity(proxyBaseUrl + "/proxy/redis/bloquear", payload6, String.class);
                log.info("Proxy Redis actualizado correctamente");
            } catch (Exception e) {
                log.warn("El Proxy no respondió, pero la reserva en Cátedra fue exitosa");
            }

            return respuesta.getBody();

        } catch (Exception e) {
            log.error("Error en comunicación: {}", e.getMessage());
            return Map.of("resultado", false, "descripcion", "Error al conectar con el servidor externo");
        }
    }
}