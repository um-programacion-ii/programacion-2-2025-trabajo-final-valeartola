package com.example.backend.catedra;

import com.example.backend.event.model.Evento;
import com.example.backend.event.model.TipoEvento;
import com.example.backend.event.repository.EventoRepository;
import com.example.backend.event.repository.TipoEventoRepository; // <--- NUEVO IMPORT
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class CatedraService {

    private final EventoRepository eventoRepository;
    private final TipoEventoRepository tipoEventoRepository; // <--- Nuevo Repositorio
    private final RestClient restClient;

    // Inyectamos también el TipoEventoRepository
    public CatedraService(EventoRepository eventoRepository,
                          TipoEventoRepository tipoEventoRepository,
                          @Value("${catedra.url}") String catedraUrl,
                          @Value("${catedra.token}") String token) {
        this.eventoRepository = eventoRepository;
        this.tipoEventoRepository = tipoEventoRepository; // <--- Asignación

        this.restClient = RestClient.builder()
                .baseUrl(catedraUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    public List<Evento> sincronizarEventos() {
        System.out.println("⏳ Consultando eventos a la Cátedra...");

        try {
            List<Evento> eventos = restClient.get()
                    .uri("/api/endpoints/v1/eventos")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Evento>>() {});

            if (eventos != null && !eventos.isEmpty()) {

                for (Evento evento : eventos) {
                    // 1. ARREGLO DE ID DEL EVENTO
                    evento.setIdCatedra(evento.getId());
                    evento.setId(null); // Limpiamos ID para que MySQL genere uno nuevo

                    // 2. ARREGLO DEL TIPO DE EVENTO (El error que tenías)
                    if (evento.getEventoTipo() != null) {
                        TipoEvento tipoTraido = evento.getEventoTipo();

                        // Buscamos si ya existe ese tipo en nuestra BD (por nombre)
                        TipoEvento tipoExistente = tipoEventoRepository.findByNombre(tipoTraido.getNombre());

                        if (tipoExistente != null) {
                            // Si existe, usamos el de la BD
                            evento.setEventoTipo(tipoExistente);
                        } else {
                            // Si no existe, lo guardamos como nuevo
                            tipoTraido.setId(null); // Limpiamos ID para que MySQL genere uno
                            TipoEvento nuevoTipo = tipoEventoRepository.save(tipoTraido);
                            evento.setEventoTipo(nuevoTipo);
                        }
                    }
                }

                // Ahora sí, guardamos los eventos con sus tipos ya arreglados
                eventoRepository.saveAll(eventos);
                System.out.println("✅ ¡Sincronización Exitosa! Se guardaron " + eventos.size() + " eventos.");
                return eventos;
            } else {
                return List.of();
            }
        } catch (Exception e) {
            System.err.println("❌ Error al sincronizar: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}