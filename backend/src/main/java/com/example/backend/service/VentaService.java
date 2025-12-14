package com.example.backend.service;

import com.example.backend.client.CatedraClient;
import com.example.backend.model.Venta;
import com.example.backend.repository.VentaRepository;
import com.example.backend.dto.CarritoDTO;
import com.example.backend.dto.SolicitudCompraDTO;
import com.example.backend.dto.TicketItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final RedisService redisService;
    private final CatedraClient catedraClient;

    @Transactional
    public Object procesarCompra(String sessionId) {
        // 1. Recuperar el carrito de Redis (La mochila)
        CarritoDTO carrito = redisService.obtenerCarrito(sessionId);
        if (carrito.getTickets() == null || carrito.getTickets().isEmpty()) {
            throw new RuntimeException("El carrito está vacío, no se puede comprar.");
        }

        // 2. SEGURIDAD: Verificar que los bloqueos sigan siendo de este usuario
        // Si pasaron más de 5 mins, 'verificarBloqueo' dará false.
        for (TicketItemDTO ticket : carrito.getTickets()) {
            boolean esMio = redisService.verificarBloqueo(
                    carrito.getEventoId(),
                    ticket.fila(),
                    ticket.columna(),
                    sessionId
            );
            if (!esMio) {
                throw new RuntimeException("El asiento Fila " + ticket.fila() + " - Col " + ticket.columna() + " expiró o ya no te pertenece.");
            }
        }

        // 3. ADAPTADOR: Preparamos el paquete para la API externa
        SolicitudCompraDTO solicitudExterna = new SolicitudCompraDTO(
                carrito.getEventoId(), // OJO: Asegúrate que sea el ID de Cátedra, no el tuyo local
                carrito.getTickets(),
        );

        // 4. EXTERNO: Enviamos la compra al Proxy (CatedraClient)
        // Si el profesor responde error (ej: 400 o 500), esto lanza excepción y corta el flujo.
        Object respuestaCatedra = catedraClient.enviarCompra(solicitudExterna);

        // 5. LOCAL: Si llegamos acá, el profe dijo OK. Guardamos en NUESTRA base de datos.
        Venta ventaLocal = new Venta();
        // ventaLocal.setTotal( ... ); // Calcular total
        // ventaLocal.setFecha(Instant.now());
        ventaRepository.save(ventaLocal);

        // 6. LIMPIEZA: Borramos el carrito y liberamos memoria
        redisService.limpiarCarrito(sessionId);

        return respuestaCatedra;
    }
}