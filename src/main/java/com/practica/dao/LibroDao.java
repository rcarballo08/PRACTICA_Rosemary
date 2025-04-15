package com.practica.dao;

import com.practica.domain.Libro;
import com.practica.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LibroDao extends JpaRepository<Libro, Long> {
    List<Libro> findByCategoria(Categoria categoria);
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    List<Libro> findByAutorContainingIgnoreCase(String autor);
    Optional<Libro> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
}
