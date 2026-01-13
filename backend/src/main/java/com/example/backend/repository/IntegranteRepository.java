package com.example.backend.repository;

import com.example.backend.model.Integrante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IntegranteRepository extends JpaRepository<Integrante, Long> {
    Optional<Integrante> findByIdentificacion(String identificacion);
    Optional<Integrante> findByNombreAndApellido(String nombre, String apellido);
}
