package com.practica.service;

import com.practica.domain.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaService {
    List<Categoria> listarTodas();
    Optional<Categoria> buscarPorId(Long id);
    Optional<Categoria> buscarPorNombre(String nombre);
    Categoria guardar(Categoria categoria);
    boolean existeNombre(String nombre);
    void eliminar(Long id);
}
