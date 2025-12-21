package com.example.backend.dto.bloqueo;

import java.util.List;

// Este es el único PUBLIC del archivo
public record ReservaRequestDTO(
        Long eventoId,
        List<AsientoSeleccionadoDTO> asientos
) {}

