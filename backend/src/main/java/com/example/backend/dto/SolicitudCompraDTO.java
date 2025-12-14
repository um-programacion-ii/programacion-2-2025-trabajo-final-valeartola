package com.example.backend.dto;

import java.util.List;

public record SolicitudCompraDTO(
        Long eventoId,
        List<TicketItemDTO> tickets // Confirmación final de asientos
) {}
