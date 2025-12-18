package com.example.backend.service;

// --- ESTOS SON LOS IMPORTS QUE EVITAN EL COLOR ROJO ---
import com.example.backend.client.CatedraClient;
import com.example.backend.dto.CarritoDTO;
import com.example.backend.dto.DatosCompradorDTO;
import com.example.backend.dto.TicketItemDTO;
import com.example.backend.model.Evento;
import com.example.backend.model.Venta;
import com.example.backend.repository.EventoRepository;
import com.example.backend.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentaService {

    private final RedisService redisService;
    private final VentaRepository ventaRepository;
    private final EventoRepository eventoRepository;
    private final CatedraClient catedraClient;

    /**
     * ISSUE 6 - PAYLOAD 6: BLOQUEO
     */
    public Map<String, Object> reservarAsientosEnCatedra(Long eventoId, List<TicketItemDTO> tickets) {
        log.info("Iniciando Bloqueo (Payload 6) para evento: {}", eventoId);

        List<Map<String, Object>> asientosCatedra = tickets.stream() // Cambia Integer por Object aquí arriba si hace falta
                .map(t -> Map.<String, Object>of( // <-- Agregamos el <String, Object>
                        "fila", t.fila(),
                        "columna", t.columna()
                ))
                .toList();
        Map<String, Object> payload6 = Map.of(
                "eventoId", eventoId,
                "asientos", asientosCatedra
        );

        // Usamos Object para recibir y luego casteamos para evitar errores de tipo
        Object respuesta = catedraClient.bloquearAsientosExterno(payload6);

        if (respuesta instanceof Map) {
            return (Map<String, Object>) respuesta;
        }

        return Map.of("resultado", false, "descripcion", "Error de formato");
    }

    /**
     * ISSUE 8 - PAYLOAD 7: VENTA
     */
    @Transactional
    public Object procesarCompra(String sessionId, DatosCompradorDTO comprador) {
        log.info("Procesando Venta Final (Payload 7) para sesión: {}", sessionId);

        CarritoDTO carrito = redisService.obtenerCarrito(sessionId);
        if (carrito == null) throw new RuntimeException("Carrito vacío o sesión expirada");

        Evento evento = eventoRepository.findById(carrito.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        String nombrePersona = comprador.nombre() + " " + comprador.apellido();

        List<Map<String, Object>> asientosVenta = carrito.getTickets().stream()
                .map(t -> Map.<String, Object>of( // <-- AGREGAR ESTO AQUÍ TAMBIÉN
                        "fila", t.fila(),
                        "columna", t.columna(),
                        "persona", nombrePersona
                ))
                .toList();

        Map<String, Object> payload7 = Map.of(
                "eventoId", carrito.getEventoId(),
                "fecha", OffsetDateTime.now().toString(),
                "precioVenta", evento.getPrecio(),
                "asientos", asientosVenta
        );

        Object respuestaObj = catedraClient.enviarCompraReal(payload7);

        if (respuestaObj instanceof Map<?, ?> respuesta) {
            if (Boolean.TRUE.equals(respuesta.get("resultado"))) {

                Venta nuevaVenta = new Venta();
                nuevaVenta.setEvento(evento);
                nuevaVenta.setFechaCompra(LocalDateTime.now());
                nuevaVenta.setMontoTotal(carrito.getTickets().size() * evento.getPrecio());
                ventaRepository.save(nuevaVenta);

                redisService.limpiarCarrito(sessionId);
            }
            return respuesta;
        }

        throw new RuntimeException("Error en la comunicación con la cátedra");
    }

    // Sobrecarga por si se llama sin datos de comprador
    public Object procesarCompra(String sessionId) {
        return procesarCompra(sessionId, new DatosCompradorDTO("Anónimo", "S/D"));
    }

    public List<Map<String, Object>> listarVentasCatedra() {
        log.info("Consultando historial de ventas a la cátedra");
        return catedraClient.obtenerHistorialVentas();
    }

    public Map<String, Object> obtenerDetalleVenta(Long ventaId) {
        log.info("Consultando detalle de la venta ID: {}", ventaId);
        return catedraClient.obtenerDetalleVentaExterno(ventaId);
    }
}