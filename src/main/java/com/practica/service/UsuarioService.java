package com.practica.service;

import com.practica.domain.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> listarTodos();
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorUsername(String username);
    Usuario guardar(Usuario usuario);
    boolean existeUsername(String username);
    boolean existeEmail(String email);
    void eliminar(Long id);
}
