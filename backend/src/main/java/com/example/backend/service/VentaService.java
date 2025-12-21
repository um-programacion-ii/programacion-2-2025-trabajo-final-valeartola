package com.example.backend.service;

import com.example.backend.client.CatedraClient;
import com.example.backend.dto.venta.CompraRequestDTO;
import com.example.backend.model.Evento;
import com.example.backend.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentaService {

    private final EventoRepository eventoRepository;
    private final CatedraClient catedraClient;

    @Transactional
    public Object procesarCompra(CompraRequestDTO request) {
        log.info("Procesando compra para evento: {}", request.eventoId());

        Evento evento = eventoRepository.findById(request.eventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // En Java 21, .toList() es la forma estándar y eficiente
        List<Map<String, Object>> asientosCatedra = request.asientos().stream()
                .map(a -> Map.<String, Object>of(
                        "fila", a.fila(),
                        "columna", a.columna(),
                        "persona", (a.nombre() + " " + a.apellido()).trim()
                ))
                .toList();

        Map<String, Object> payload7 = Map.of(
                "eventoId", request.eventoId(),
                "fecha", OffsetDateTime.now().toString(),
                "precioVenta", evento.getPrecio(),
                "asientos", asientosCatedra
        );

        return catedraClient.enviarCompraReal(payload7);
    }

    /**
     * Obtiene el detalle de una venta específica desde el servidor de la cátedra.
     */
    public Map<String, Object> obtenerDetalleVenta(Long ventaId) {
        log.info("Consultando detalle de la venta ID: {} en la cátedra", ventaId);
        // Esta llamada requiere que tu CatedraClient tenga definido obtenerDetalleVentaExterno
        return catedraClient.obtenerDetalleVentaExterno(ventaId);
    }
    /**
     * Métodos auxiliares para consultas (opcional)
     */
    public List<Map<String, Object>> listarVentasCatedra() {
        return catedraClient.obtenerHistorialVentas();
    }
}


