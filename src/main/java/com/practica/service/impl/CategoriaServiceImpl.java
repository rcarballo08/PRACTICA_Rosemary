package com.practica.service.impl;

import com.practica.dao.CategoriaDao;
import com.practica.domain.Categoria;
import com.practica.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaDao categoriaDao;

    @Autowired
    public CategoriaServiceImpl(CategoriaDao categoriaDao) {
        this.categoriaDao = categoriaDao;
    }

    @Override
    public List<Categoria> listarTodas() {
        return categoriaDao.findAll();
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaDao.findById(id);
    }

    @Override
    public Optional<Categoria> buscarPorNombre(String nombre) {
        return categoriaDao.findByNombre(nombre);
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        return categoriaDao.save(categoria);
    }

    @Override
    public boolean existeNombre(String nombre) {
        return categoriaDao.existsByNombre(nombre);
    }

    @Override
    public void eliminar(Long id) {
        categoriaDao.deleteById(id);
    }
}
