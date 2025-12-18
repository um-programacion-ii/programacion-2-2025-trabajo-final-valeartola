package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import java.util.HashSet;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "integrante")
public class Integrante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Usaremos el ID que viene de la cátedra

    private String nombre;
    private String apellido;

    @Column(unique = true)
    private String identificacion; // Para el campo "Dra.", "Profesor", etc.
}
