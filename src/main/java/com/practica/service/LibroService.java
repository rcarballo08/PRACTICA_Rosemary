package com.practica.service;

import com.practica.domain.Categoria;
import com.practica.domain.Libro;
import java.util.List;
import java.util.Optional;

public interface LibroService {
    List<Libro> listarTodos();
    Optional<Libro> buscarPorId(Long id);
    Optional<Libro> buscarPorIsbn(String isbn);
    List<Libro> buscarPorCategoria(Categoria categoria);
    List<Libro> buscarPorTitulo(String titulo);
    List<Libro> buscarPorAutor(String autor);
    Libro guardar(Libro libro);
    boolean existeIsbn(String isbn);
    void eliminar(Long id);
    boolean tieneEjemplaresDisponibles(Long id);
    void actualizarEjemplaresDisponibles(Long id, int cantidad);
}
