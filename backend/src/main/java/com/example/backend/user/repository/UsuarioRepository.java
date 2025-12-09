package com.example.backend.user.repository;

import com.example.backend.user.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Metodo para encontrar un usuario por su nombre de usuario
    Optional<Usuario> findByUsername(String username);

    // Metodo para verificar si un usuario existe por su nombre de usuario
    Boolean existsByUsername(String username);

    // Metodo para verificar si un usuario existe por su email
    Boolean existsByEmail(String email);

}
