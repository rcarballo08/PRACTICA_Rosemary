package com.practica.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
public class Contacto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String asunto;
    
    @Column(nullable = false, length = 1000)
    private String mensaje;
    
    private LocalDateTime fechaEnvio;
}

