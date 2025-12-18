package com.example.backend.controller;

import com.example.backend.dto.EventoDetalleDTO;
import com.example.backend.dto.EventoResumenDTO;
import com.example.backend.model.EstadoEvento;
import com.example.backend.model.Evento;
import com.example.backend.repository.EventoRepository;
import com.example.backend.service.SincronizacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoRepository eventoRepository;
    private final SincronizacionService sincronizacionService;

    public EventoController(EventoRepository eventoRepository, SincronizacionService sincronizacionService) {

        this.eventoRepository = eventoRepository;
        this.sincronizacionService = sincronizacionService;
    }

    @GetMapping
    public List<EventoDetalleDTO> obtenerEventosActivos() {
        return eventoRepository.findByEstado(EstadoEvento.ACTIVO)
                .stream()
                .map(e -> new EventoDetalleDTO(
                        e.getId(),
                        e.getTitulo(),
                        e.getResumen(),
                        e.getDescripcion(),
                        e.getFechaHora().toString(),
                        e.getDireccion(),
                        e.getPrecio(),
                        e.getImagenUrl(),
                        e.getEventoTipo() != null ?
                                new EventoDetalleDTO.DatosTipoEvento(e.getEventoTipo().getNombre(), e.getEventoTipo().getDescripcion()) : null,
                        e.getIntegrantes().stream()
                                .map(i -> new EventoDetalleDTO.IntegranteDetalleDTO(i.getNombre(), i.getApellido(), i.getIdentificacion()))
                                .toList()
                ))
                .toList();
    }
    @GetMapping("/resumidos")
    public List<EventoResumenDTO> obtenerEventosResumidos() {
        return eventoRepository.findByEstado(EstadoEvento.ACTIVO)
                .stream()
                .map(evento -> {
                    EventoResumenDTO.DatosTipoEvento infoEventoTipo = null;

                    if (evento.getEventoTipo() != null) {
                        infoEventoTipo = new EventoResumenDTO.DatosTipoEvento(
                                evento.getEventoTipo().getNombre(),
                                evento.getEventoTipo().getDescripcion()
                        );
                    }
                    return new EventoResumenDTO(
                            evento.getTitulo(),
                            evento.getResumen(),
                            evento.getDescripcion(),
                            evento.getFechaHora().toString(), // Convertimos Instant a String
                            evento.getPrecio(),   // Mapeamos precioEntrada a precio
                            infoEventoTipo,
                            evento.getId()
                    );
                })
                .toList();
    };

    @GetMapping("/{id}")
    public Evento obtenerEventoPorId(@PathVariable Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Validación extra de estado
        if (evento.getEstado() == EstadoEvento.BAJA) {
            throw new RuntimeException("Evento dado de baja");
        }
        return evento;
    }

    @PostMapping("/internal/notificacion/evento")
    public ResponseEntity<Void> recibirNotificacionProxy(@RequestBody String mensaje) {
        // Aquí llamamos al servicio de sincronización que ya tienes
        // SincronizacionService procesará la notificación y llamará a eventoService.syncEvents()
        sincronizacionService.procesarNotificacion(mensaje);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/asientos")
    public String obtenerMapaAsientos(@PathVariable Long id) {
        return "Mapa de asientos (implementación pendiente - ver EventoService)";
    }
}