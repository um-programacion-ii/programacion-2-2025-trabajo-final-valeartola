package com.example.backend.event.model;

import com.example.backend.sale.model.Venta;
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

    @Column(nullable = false)
    private Integer fila;

    @Column(nullable = false)
    private Integer columna;

    private String persona; // Nombre de la persona que ocupa el asiento

    private String estado = "Vendido";

    // Relación N:1 con Venta
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Venta venta;
}