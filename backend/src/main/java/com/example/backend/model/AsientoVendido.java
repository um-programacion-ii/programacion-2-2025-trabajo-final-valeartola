package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "asientos_vendidos")
public class AsientoVendido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer fila;
    private Integer columna;
    private Double precioIndividual;

    @Column(nullable = false)
    private String nombreAsistente;

    @Column(nullable = false)
    private String apellidoAsistente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;
}