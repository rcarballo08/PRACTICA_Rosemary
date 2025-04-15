package com.practica.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Set;
import lombok.Data;
 
@Entity
@Data
@Table(name = "libro")
public class Libro implements Serializable {
    private static final long serialVersionUID = 1L; 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    
    private Long id;
    @Column(name = "TITULO", nullable = false)
    
    private String titulo;
    @Column(name = "autor", nullable = false)
    
    private String autor;
    @Column(name = "isbn", nullable = false, unique = true)
    
    private String isbn;
    @Column(name = "ANIO_PUBLICACION")
    
    private Integer anioPublicacion;
    @Column(name = "editorial")
    
    private String editorial;
    @Column(name = "EJEMPLARES_DISPONIBLES", nullable = false)
    
    private Integer ejemplaresDisponibles = 0; 
    @ManyToOne
    @JoinColumn(name = "CATEGORIA_ID", nullable = false)
    
    private Categoria categoria;
    @OneToMany(mappedBy = "libro")
    
    private Set<Prestamo> prestamos;

    public Libro() {
        this.ejemplaresDisponibles = 0;
    }
}

