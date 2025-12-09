package com.example.backend.user.service;

import com.example.backend.user.model.Usuario;
import com.example.backend.user.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Este metodo es llamado por Spring Security para cargar los detalles
     * del usuario (username y password hasheada) durante el login.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Busca tu entidad Usuario en la base de datos usando el Repositorio
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        // 2. Mapea tu entidad Usuario a un objeto UserDetails de Spring Security.
        // Aquí debes retornar el username, la contraseña hasheada, y las autoridades/roles.

        // IMPORTANTE: Se usa el constructor de org.springframework.security.core.userdetails.User.
        // Asume que la entidad Usuario.password ya tiene el hash.
        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                new ArrayList<>() // Puedes pasar una lista con los roles/autoridades si los implementas
        );
    }
}
