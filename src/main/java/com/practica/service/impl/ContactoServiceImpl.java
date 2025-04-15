package com.practica.service.impl;

import com.practica.dao.ContactoDao;
import com.practica.domain.Contacto;
import com.practica.service.ContactoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContactoServiceImpl implements ContactoService {

    private final ContactoDao contactoDao;

    @Autowired
    public ContactoServiceImpl(ContactoDao contactoDao) {
        this.contactoDao = contactoDao;
    }

    @Override
    public List<Contacto> listarTodos() {
        return contactoDao.findAll();
    }

    @Override
    public Optional<Contacto> buscarPorId(Long id) {
        return contactoDao.findById(id);
    }

    @Override
    public Contacto guardar(Contacto contacto) {
        if (contacto.getFechaEnvio() == null) {
            contacto.setFechaEnvio(LocalDateTime.now());
        }
        return contactoDao.save(contacto);
    }

    @Override
    public List<Contacto> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return contactoDao.findByFechaEnvioBetween(inicio, fin);
    }

    @Override
    public void eliminar(Long id) {
        contactoDao.deleteById(id);
    }
}
