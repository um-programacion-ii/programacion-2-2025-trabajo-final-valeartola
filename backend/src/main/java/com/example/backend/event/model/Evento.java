package com.example.backend.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del Evento en el servidor de la Cátedra (Para sincronización)
    @Column(unique = true, nullable = false)
    private Long idCatedra;

    private String titulo;

    @Lob
    private String descripcion;

    @Column(nullable = false)
    private Instant fecha;

    private String direccion;
    private Double precioEntrada;

    private Integer filaAsientos;
    private Integer columnAsientos;

    @ManyToOne
    @JoinColumn(name = "evento_tipo_id")
    private TipoEvento eventoTipo;

    // ... Getters y Setters ...
}