package com.practica.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
public class Respuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "contacto_id", nullable = false)
    private Contacto contacto;
    
    @Column(nullable = false)
    private String mensaje;
    
    @Column(nullable = false)
    private LocalDateTime fechaRespuesta;
    
    @Column(nullable = false)
    private String respondidoPor; // nombre de usuario que respondió
}
