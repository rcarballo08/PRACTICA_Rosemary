package com.practica.dao;

import com.practica.domain.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ContactoDao extends JpaRepository<Contacto, Long> {
    List<Contacto> findByFechaEnvioBetween(LocalDateTime inicio, LocalDateTime fin);
}
