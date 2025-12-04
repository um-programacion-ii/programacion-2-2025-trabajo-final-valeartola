package com.example.backend.sale.model;

import com.example.backend.event.model.AsientoVendido;
import com.example.backend.event.model.Evento;
import com.example.backend.user.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID de la Venta en el servidor de la Cátedra (Para trazabilidad y reintentos)
    @Column(unique = true)
    private Long idCatedra;

    @Column(nullable = false)
    private Instant fechaVenta;

    private Double precioVenta;
    private Boolean resultado;

    @Lob
    private String descripcion;

    // Relación N:1 con Evento
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    // Relación N:1 con Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Relación 1:N con AsientoVendido
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AsientoVendido> asientosVendidos = new HashSet<>();
}