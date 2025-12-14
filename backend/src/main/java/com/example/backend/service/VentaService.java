package com.example.backend.service;

import com.example.backend.client.CatedraClient;
import com.example.backend.model.Evento;
import com.example.backend.model.Venta;
import com.example.backend.repository.EventoRepository;
import com.example.backend.repository.VentaRepository;
import com.example.backend.dto.CarritoDTO;
import com.example.backend.dto.SolicitudCompraDTO;
import com.example.backend.dto.TicketItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final RedisService redisService; // Usamos RedisService, no el Template directo
    private final VentaRepository ventaRepository;
    private final EventoRepository eventoRepository;
    private final CatedraClient catedraClient;

    @Transactional
    public Object procesarCompra(String sessionId) {
        // 1. Recuperar el carrito de Redis
        CarritoDTO carrito = redisService.obtenerCarrito(sessionId);

        if (carrito == null || carrito.getTickets() == null || carrito.getTickets().isEmpty()) {
            throw new RuntimeException("El carrito está vacío o expiró.");
        }

        // 2. VERIFICACIÓN CORRECTA: Revisamos ticket por ticket
        for (TicketItemDTO ticket : carrito.getTickets()) {
            boolean esMio = redisService.verificarBloqueo(
                    carrito.getEventoId(),
                    ticket.fila(),
                    ticket.columna(),
                    sessionId
            );

            if (!esMio) {
                throw new RuntimeException("El tiempo de reserva expiró para el asiento Fila " + ticket.fila() + " Col " + ticket.columna());
            }
        }

        // 3. Buscar el Evento REAL en la base de datos
        Evento evento = eventoRepository.findById(carrito.getEventoId())
                .orElseThrow(() -> new RuntimeException("El evento no existe en la base de datos"));

        // 4. Calcular total
        double total = carrito.getTickets().size() * evento.getPrecioEntrada();

        // 5. Armar objeto para la Cátedra (Mock activado)
        Map<String, Object> solicitudCatedra = Map.of(
                "eventoId", carrito.getEventoId(),
                "tickets", carrito.getTickets()
        );

        // 6. Llamar al Mock
        Object respuestaCatedra = catedraClient.enviarCompra(solicitudCatedra);

        // 7. Guardar en MySQL
        Venta venta = new Venta();
        venta.setEvento(evento);
        venta.setFechaCompra(LocalDateTime.now());
        venta.setMontoTotal(total);

        ventaRepository.save(venta);

        // 8. Limpiar Redis
        redisService.limpiarCarrito(sessionId);

        return respuestaCatedra;
    }
}