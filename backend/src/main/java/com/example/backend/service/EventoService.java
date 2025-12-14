package com.example.backend.service;

import com.example.backend.client.CatedraClient;
import com.example.backend.dto.AsientoOcupadoExternoDTO;
import com.example.backend.dto.EstadoAsientoDTO;
import com.example.backend.dto.EventoResumenDTO;
import com.example.backend.model.Evento;
import com.example.backend.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final CatedraClient catedraClient;
    private final RedisService redisService;

    /**
     * 1. LISTAR EVENTOS (Catálogo)
     */
    public List<EventoResumenDTO> listarEventos() {
        return eventoRepository.findAll().stream()
                .map(evento -> new EventoResumenDTO(
                        evento.getId(),
                        evento.getTitulo(),
                        evento.getResumen(),
                        evento.getFecha().toString(), // Convierte Instant a String ISO
                        evento.getPrecioEntrada()
                ))
                .toList();
    }

    /**
     * 2. OBTENER MAPA DE ASIENTOS (Tiempo Real)
     */
    public List<EstadoAsientoDTO> obtenerAsientos(Long idEvento) {

        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado en BD local con ID: " + idEvento));


        List<AsientoOcupadoExternoDTO> ocupadosCatedra = catedraClient.getAsientosOcupados(evento.getIdCatedra());

        Set<String> ocupadosSet = ocupadosCatedra.stream()
                .map(a -> a.fila() + "-" + a.columna())
                .collect(Collectors.toSet());


        Set<String> bloqueadosRedis = redisService.obtenerBloqueados(evento.getId());

        List<EstadoAsientoDTO> mapaAsientos = new ArrayList<>();

        for (int fila = 1; fila <= evento.getFilaAsientos(); fila++) {
            for (int col = 1; col <= evento.getColumnAsientos(); col++) {

                String clave = fila + "-" + col;
                String estado = "LIBRE"; // Estado por defecto



                if (ocupadosSet.contains(clave)) {
                    estado = "OCUPADO";
                } else if (bloqueadosRedis.contains(clave)) {
                    estado = "BLOQUEADO";
                }

                mapaAsientos.add(new EstadoAsientoDTO(fila, col, estado));
            }
        }

        return mapaAsientos;
    }
}