package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Data
@Table(name = "evento")
public class Evento {

    @Id
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 500)
    private String resumen;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    private String direccion;
    private Double precio;

    private Integer filas;
    private Integer columnas;

    @Column(name = "imagen_url", length = 2048)
    private String imagenUrl;

    private LocalDateTime ultimaActualizacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEvento estado = EstadoEvento.ACTIVO;

    @ManyToOne
    @JoinColumn(name = "evento_tipo_id")
    private TipoEvento eventoTipo;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "evento_integrante",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "integrante_id")
    )
    private Set<Integrante> integrantes = new HashSet<>();



}