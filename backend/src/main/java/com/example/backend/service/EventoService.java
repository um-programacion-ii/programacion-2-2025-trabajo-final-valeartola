package com.example.backend.service;

import com.example.backend.dto.evento.EventoExternoDTO;
import com.example.backend.model.Evento;
import com.example.backend.model.Integrante;
import com.example.backend.model.TipoEvento;
import com.example.backend.repository.EventoRepository;
import com.example.backend.repository.IntegranteRepository;
import com.example.backend.repository.TipoEventoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventoService {

    private final EventoRepository eventoRepository;
    private final TipoEventoRepository tipoEventoRepository;
    private final RestTemplate restTemplate;
    private final IntegranteRepository integranteRepository;

    @Value("${catedra.url}")
    private String catedraUrl;

    @Value("${catedra.api.token}")
    private String catedraToken;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void syncEvents() {
        try {
            String url = catedraUrl + "/api/endpoints/v1/eventos";
            log.info("Sincronizando con: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + catedraToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<EventoExternoDTO[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, EventoExternoDTO[].class);

            if (response.getBody() != null) {
                procesarListaInteligente(Arrays.asList(response.getBody()));
            }
        } catch (Exception e) {
            log.error("Error en la sincronización: {}", e.getMessage());
            // No relanzamos la excepción para evitar que la app no arranque
        }
    }

    @Transactional
    public void actualizarDesdeNotificacion(EventoExternoDTO dto) {
        // Buscamos el evento o creamos uno nuevo si no existe
        Evento evento = eventoRepository.findById(dto.id())
                .orElseGet(() -> {
                    Evento nuevo = new Evento();
                    nuevo.setId(dto.id());
                    return nuevo;
                });

        // Reutilizamos tu lógica de mapeo que ya está en el archivo
        actualizarDatos(evento, dto);

        eventoRepository.save(evento);
        log.info("Evento {} actualizado exitosamente desde Kafka.", dto.id());
    }

    @Transactional
    public void actualizarUnSoloEvento(EventoExternoDTO dto) {
        // Buscamos si ya existe el evento
        Evento evento = eventoRepository.findById(dto.id())
                .orElseGet(() -> {
                    Evento nuevo = new Evento();
                    nuevo.setId(dto.id());
                    return nuevo;
                });

        // Usamos tu lógica de mapeo que ya funciona
        actualizarDatos(evento, dto);

        eventoRepository.save(evento);
        log.info("Evento ID {} actualizado INSTANTÁNEAMENTE desde la notificación.", dto.id());
    }

    private void procesarListaInteligente(List<EventoExternoDTO> externos) {
        // 1. Cargamos lo que tenemos en la DB
        Map<Long, Evento> mapaLocales = eventoRepository.findAll().stream()
                .collect(Collectors.toMap(Evento::getId, Function.identity()));

        for (EventoExternoDTO dto : externos) {
            Evento evento = mapaLocales.getOrDefault(dto.id(), new Evento());
            if (evento.getId() == null) {
                evento.setId(dto.id());
            }

            actualizarDatos(evento, dto);
            eventoRepository.save(evento);
        }
        log.info("Sincronización de eventos completada.");
    }

    private void actualizarDatos(Evento evento, EventoExternoDTO dto) {
        evento.setTitulo(dto.titulo());
        evento.setResumen(dto.resumen());
        evento.setDescripcion(dto.descripcion());
        evento.setImagenUrl(dto.imagenUrl());
        evento.setPrecio(dto.precio());
        evento.setDireccion(dto.direccion());
        evento.setFilas(dto.filas());
        evento.setColumnas(dto.columnas());

        if (dto.fecha() != null) {
            evento.setFechaHora(dto.fecha().toLocalDateTime());
        }

        // Manejo del Tipo de Evento
        if (dto.eventoTipo() != null) {
            TipoEvento tipo = tipoEventoRepository.findByNombre(dto.eventoTipo().nombre())
                    .orElseGet(() -> {
                        TipoEvento nt = new TipoEvento();
                        nt.setNombre(dto.eventoTipo().nombre());
                        nt.setDescripcion(dto.eventoTipo().descripcion());
                        return tipoEventoRepository.save(nt);
                    });
            evento.setEventoTipo(tipo);
        }

        // --- LÓGICA DE INTEGRANTES MEJORADA ---
        if (dto.integrantes() != null) {
            Set<Integrante> integrantesParaAsignar = new HashSet<>();

            // En EventoService.java, dentro del for de integrantes:
            for (EventoExternoDTO.IntegranteDto iDto : dto.integrantes()) {
                Integrante integrante;

                if (iDto.identificacion() != null && !iDto.identificacion().isBlank()) {
                    integrante = integranteRepository.findByIdentificacion(iDto.identificacion())
                            .orElse(new Integrante());
                } else {
                    integrante = integranteRepository.findByNombreAndApellido(iDto.nombre(), iDto.apellido())
                            .orElse(new Integrante());
                }

                integrante.setNombre(iDto.nombre());
                integrante.setApellido(iDto.apellido());
                // AGREGÁ ESTA LÍNEA AQUÍ:
                integrante.setIdentificacion(iDto.identificacion());

                integrante = integranteRepository.save(integrante);
                integrantesParaAsignar.add(integrante);
            }

            evento.getIntegrantes().clear();
            evento.getIntegrantes().addAll(integrantesParaAsignar);
        }

        evento.setUltimaActualizacion(LocalDateTime.now());
    }
}