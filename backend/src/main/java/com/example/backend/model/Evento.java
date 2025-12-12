package com.example.backend.model;

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
    private String titulo;

    @Column(nullable = false, unique = true)
    private Long idCatedra;

    private String resumen;

    @Lob
    private String descripcion;

    @Column(nullable = false)
    private Instant fecha;

    private String direccion;
    private Double precioEntrada;

    private Integer filaAsientos;
    private Integer columnAsientos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEvento estado = EstadoEvento.ACTIVO;

    @ManyToOne
    @JoinColumn(name = "evento_tipo_id")
    private TipoEvento eventoTipo;



}