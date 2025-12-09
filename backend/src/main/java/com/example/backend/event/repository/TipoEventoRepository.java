package com.example.backend.event.repository;

import com.example.backend.event.model.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoEventoRepository extends JpaRepository<TipoEvento, Long> {

    TipoEvento findByNombre(String nombre);
}