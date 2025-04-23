package com.practica.service;

import com.practica.domain.Contacto;
import com.practica.domain.Respuesta;
import java.util.List;
import java.util.Optional;

public interface RespuestaService {
    List<Respuesta> listarTodos();
    Optional<Respuesta> buscarPorId(Long id);
    List<Respuesta> buscarPorContacto(Contacto contacto);
    Respuesta guardar(Respuesta respuesta);
    void eliminar(Long id);
}
