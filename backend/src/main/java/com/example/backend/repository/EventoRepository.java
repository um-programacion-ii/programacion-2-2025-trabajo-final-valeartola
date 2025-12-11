package com.example.backend.repository;

import com.example.backend.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    // Útil para la sincronización y búsqueda rápida por el ID de la Cátedra
    Evento findByIdCatedra(Long idCatedra);
}