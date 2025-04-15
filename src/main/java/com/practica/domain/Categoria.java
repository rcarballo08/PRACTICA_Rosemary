package com.practica.domain;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Data;

@Entity
@Data
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    private String descripcion;
    
    @OneToMany(mappedBy = "categoria")
    private Set<Libro> libros;
}
