package com.example.backend.service;

import com.example.backend.client.CatedraClient;
import com.example.backend.dto.AsientoOcupadoExternoDTO;
import com.example.backend.dto.EstadoAsientoDTO;
import com.example.backend.dto.EventoExternoDTO;
import com.example.backend.dto.EventoResumenDTO;
import com.example.backend.model.Evento;
import com.example.backend.repository.EventoRepository;
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
    private final RestTemplate restTemplate;

    @Value("${catedra.url}")
    private String catedraUrl;

    @Value("${catedra.api.token}")
    private String catedraToken;

    public List<Evento> getAllEvents() {

        syncEvents();
        return eventoRepository.findAll();
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void syncEvents() {
        try {
            String url = catedraUrl + "/api/eventos";
            log.info("Conectando a Cátedra: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + catedraToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<EventoExternoDTO[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, EventoExternoDTO[].class);

            if (response.getBody() != null) {
                List<EventoExternoDTO> eventosExternos = Arrays.asList(response.getBody());
                procesarListaInteligente(eventosExternos);
            }
        } catch (Exception e) {
            log.error("Falló la sincronización: {}", e.getMessage());
        }
    }

    private void procesarListaInteligente(List<EventoExternoDTO> externos) {
        List<Evento> locales = eventoRepository.findAll();

        Map<Long, Evento> mapaLocales = locales.stream()
                .collect(Collectors.toMap(Evento::getId, Function.identity()));

        Set<Long> idsExternos = new HashSet<>();

        int actualizados = 0;
        int creados = 0;
        int borrados = 0;
        int sinCambios = 0;

        for (EventoExternoDTO dto : externos) {
            idsExternos.add(dto.id());

            if (mapaLocales.containsKey(dto.id())) {
                // YA EXISTE: Verificar si cambió algo
                Evento eventoLocal = mapaLocales.get(dto.id());
                if (huboCambios(eventoLocal, dto)) {
                    actualizarDatos(eventoLocal, dto);
                    eventoRepository.save(eventoLocal);
                    actualizados++;
                    log.debug("Evento actualizado: {}", dto.titulo());
                } else {
                    sinCambios++;
                }
            } else {
                // NO EXISTE: Crear nuevo
                Evento nuevo = new Evento();
                nuevo.setId(dto.id()); // Asignamos el ID manual que viene de afuera
                actualizarDatos(nuevo, dto);
                eventoRepository.save(nuevo);
                creados++;
                log.debug("Evento nuevo creado: {}", dto.titulo());
            }
        }

        // 3. Detectar eliminados (Están en Local pero NO en Externos)
        List<Evento> aBorrar = locales.stream()
                .filter(e -> !idsExternos.contains(e.getId()))
                .collect(Collectors.toList());

        if (!aBorrar.isEmpty()) {
            eventoRepository.deleteAll(aBorrar);
            borrados = aBorrar.size();
            log.info("Se eliminaron {} eventos que ya no existen en la cátedra.", borrados);
        }

        log.info("Resumen Sync: {} Nuevos, {} Actualizados, {} Borrados, {} Intactos.",
                creados, actualizados, borrados, sinCambios);
    }

    // Compara campo por campo para ver si vale la pena actualizar
    private boolean huboCambios(Evento local, EventoExternoDTO externo) {
        // Comparamos los campos clave. Si alguno es distinto, retorna true.
        return !Objects.equals(local.getTitulo(), externo.titulo()) ||
                !Objects.equals(local.getDescripcion(), externo.descripcion()) ||
                !Objects.equals(local.getPrecio(), externo.precio()) ||
                !Objects.equals(local.getImagenUrl(), externo.imagen()) ||
                !Objects.equals(local.getFilas(), externo.filas()) ||
                !Objects.equals(local.getColumnas(), externo.columnas()) ||
                // Compara fechas (convirtiendo DTO Timestamp a LocalDateTime si es necesario)
                (externo.fecha() != null && !local.getFechaHora().isEqual(externo.fecha().toLocalDateTime()));
    }

    // Copia los datos del DTO a la Entidad
    private void actualizarDatos(Evento evento, EventoExternoDTO dto) {
        evento.setTitulo(dto.titulo());
        evento.setDescripcion(dto.descripcion());
        evento.setImagenUrl(dto.imagen());
        evento.setPrecio(dto.precio());
        evento.setFilas(dto.filas());
        evento.setColumnas(dto.columnas());

        if (dto.fecha() != null) {
            evento.setFechaHora(dto.fecha().toLocalDateTime());
        }

        // Solo actualizamos la fecha de "ultima modificación" cuando realmente tocamos los datos
        evento.setUltimaActualizacion(LocalDateTime.now());
    }
}