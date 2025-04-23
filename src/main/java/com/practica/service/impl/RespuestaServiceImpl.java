package com.practica.service.impl;

import com.practica.dao.RespuestaDao;
import com.practica.domain.Contacto;
import com.practica.domain.Respuesta;
import com.practica.service.RespuestaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RespuestaServiceImpl implements RespuestaService {

    private final RespuestaDao respuestaDao;

    @Autowired
    public RespuestaServiceImpl(RespuestaDao respuestaDao) {
        this.respuestaDao = respuestaDao;
    }

    @Override
    public List<Respuesta> listarTodos() {
        return respuestaDao.findAll();
    }

    @Override
    public Optional<Respuesta> buscarPorId(Long id) {
        return respuestaDao.findById(id);
    }

    @Override
    public List<Respuesta> buscarPorContacto(Contacto contacto) {
        return respuestaDao.findByContactoOrderByFechaRespuestaDesc(contacto);
    }

    @Override
    public Respuesta guardar(Respuesta respuesta) {
        if (respuesta.getFechaRespuesta() == null) {
            respuesta.setFechaRespuesta(LocalDateTime.now());
        }
        return respuestaDao.save(respuesta);
    }

    @Override
    public void eliminar(Long id) {
        respuestaDao.deleteById(id);
    }
}
