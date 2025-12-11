package com.example.backend.service;

import com.example.backend.client.CatedraClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final CatedraClient catedraClient;

    public Object obtenerEventosDesdeCatedra() {
        return catedraClient.getEventosResumidos();
    }

    public Object obtenerEventoDesdeCatedra(Long id) {
        return catedraClient.getEventoCompleto(id);
    }
}