package com.example.backend.user.model;

import com.example.backend.sale.model.Venta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(unique = true)
    private String email;

    private String nombre;
    private String apellido;
    private String rol;

    @OneToMany(mappedBy = "usuario")
    private Set<Venta> ventas = new HashSet<>();
}