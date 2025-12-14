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
     * Fuente: Tu Base de Datos MySQL Local.
     * Razón: Es mucho más rápido que consultar a la cátedra cada vez que alguien entra.
     * (La sincro MySQL <-> Cátedra se hace aparte con Kafka o Jobs).
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
     * Fuente: Fusión de MySQL (Dimensiones) + Cátedra (Ocupados) + Redis (Bloqueados).
     */
    public List<EstadoAsientoDTO> obtenerAsientos(Long idEvento) {

        // A. Buscamos la configuración local del evento (Filas y Columnas)
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado en BD local con ID: " + idEvento));

        // B. Preguntamos a la Cátedra qué asientos ya se vendieron de verdad
        // IMPORTANTE: Usamos 'getIdCatedra()' porque el ID externo puede ser distinto al tuyo
        List<AsientoOcupadoExternoDTO> ocupadosCatedra = catedraClient.getAsientosOcupados(evento.getIdCatedra());

        // Optimizamos: Convertimos la lista en un Set de Strings "F-C" para búsqueda rápida O(1)
        Set<String> ocupadosSet = ocupadosCatedra.stream()
                .map(a -> a.fila() + "-" + a.columna())
                .collect(Collectors.toSet());

        // C. Preguntamos a Redis qué asientos están bloqueados temporalmente
        // Esto evita que dos personas seleccionen el mismo asiento a la vez
        Set<String> bloqueadosRedis = redisService.obtenerBloqueados(evento.getId());

        // D. Generamos el mapa completo celda por celda
        List<EstadoAsientoDTO> mapaAsientos = new ArrayList<>();

        // Iteramos sobre las dimensiones definidas en TU base de datos
        for (int fila = 1; fila <= evento.getFilaAsientos(); fila++) {
            for (int col = 1; col <= evento.getColumnAsientos(); col++) {

                String clave = fila + "-" + col;
                String estado = "LIBRE"; // Estado por defecto

                // PRIORIDAD DE ESTADOS:
                // 1. Si la cátedra dice que está ocupado, es OCUPADO (Venta confirmada).
                // 2. Si no, si Redis dice que está bloqueado, es BLOQUEADO (Alguien comprando).
                // 3. Si no, está LIBRE.

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