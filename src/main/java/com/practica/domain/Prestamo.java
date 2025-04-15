package com.practica.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
@Table(name = "prestamo")
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "LIBRO_ID", nullable = false)
    private Libro libro;
    
    @ManyToOne
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;
    
    @Column(name = "FECHA_PRESTAMO", nullable = false)
    private LocalDate fechaPrestamo = LocalDate.now(); 
    
    @Column(name = "FECHA_DEVOLUCION_PREVISTA")
    private LocalDate fechaDevolucionPrevista;
    
    @Column(name = "FECHA_DEVOLUCION_REAL")
    private LocalDate fechaDevolucionReal;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoPrestamo estado = EstadoPrestamo.PRESTADO; 
    
   
    public Prestamo() {
        this.fechaPrestamo = LocalDate.now();
        this.estado = EstadoPrestamo.PRESTADO;
    }
    
    public enum EstadoPrestamo {
        PRESTADO, DEVUELTO, VENCIDO
    }
}