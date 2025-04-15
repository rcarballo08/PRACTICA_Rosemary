package com.practica.service.impl;

import com.practica.dao.LibroDao;
import com.practica.domain.Categoria;
import com.practica.domain.Libro;
import com.practica.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class LibroServiceImpl implements LibroService {

    private final LibroDao libroDao;

    @Autowired
    public LibroServiceImpl(LibroDao libroDao) {
        this.libroDao = libroDao;
    }

    @Override
    public List<Libro> listarTodos() {
        return libroDao.findAll();
    }

    @Override
    public Optional<Libro> buscarPorId(Long id) {
        return libroDao.findById(id);
    }

    @Override
    public Optional<Libro> buscarPorIsbn(String isbn) {
        return libroDao.findByIsbn(isbn);
    }

    @Override
    public List<Libro> buscarPorCategoria(Categoria categoria) {
        return libroDao.findByCategoria(categoria);
    }

    @Override
    public List<Libro> buscarPorTitulo(String titulo) {
        return libroDao.findByTituloContainingIgnoreCase(titulo);
    }

    @Override
    public List<Libro> buscarPorAutor(String autor) {
        return libroDao.findByAutorContainingIgnoreCase(autor);
    }

    @Override
    public Libro guardar(Libro libro) {
        return libroDao.save(libro);
    }

    @Override
    public boolean existeIsbn(String isbn) {
        return libroDao.existsByIsbn(isbn);
    }

    @Override
    public void eliminar(Long id) {
        libroDao.deleteById(id);
    }

    @Override
    public boolean tieneEjemplaresDisponibles(Long id) {
        Optional<Libro> libro = libroDao.findById(id);
        return libro.filter(l -> l.getEjemplaresDisponibles() > 0).isPresent();
    }

    @Override
    @Transactional
    public void actualizarEjemplaresDisponibles(Long id, int cantidad) {
        Optional<Libro> optionalLibro = libroDao.findById(id);
        if (optionalLibro.isPresent()) {
            Libro libro = optionalLibro.get();
            int nuevaCantidad = libro.getEjemplaresDisponibles() + cantidad;
            if (nuevaCantidad < 0) {
                throw new IllegalArgumentException("No hay suficientes ejemplares disponibles");
            }
            libro.setEjemplaresDisponibles(nuevaCantidad);
            libroDao.save(libro);
        }
    }
}
