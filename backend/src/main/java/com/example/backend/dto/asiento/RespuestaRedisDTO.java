package com.example.backend.dto.asiento;

import java.util.List;

public record RespuestaRedisDTO(
        Long eventoId,
        List<AsientoOcupadoExternoDTO> asientos
) {}
