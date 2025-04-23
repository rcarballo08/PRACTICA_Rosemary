package com.practica.dao;

import com.practica.domain.Contacto;
import com.practica.domain.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RespuestaDao extends JpaRepository<Respuesta, Long> {
    List<Respuesta> findByContactoOrderByFechaRespuestaDesc(Contacto contacto);
}