package com.example.backend.dto.venta;

import java.util.List;

public record CompraRequestDTO(
        Long eventoId,
        List<TicketAsistenteDTO> asientos
) {}
