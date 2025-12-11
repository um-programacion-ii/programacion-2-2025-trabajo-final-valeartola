package com.example.backend.repository;

import com.example.backend.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Evento, Long> {
}
