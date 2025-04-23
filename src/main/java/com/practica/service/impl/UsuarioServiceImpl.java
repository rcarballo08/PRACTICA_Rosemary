
package com.practica.service.impl;

import com.practica.dao.UsuarioDao;
import com.practica.domain.Usuario;
import com.practica.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServiceImpl(UsuarioDao usuarioDao, PasswordEncoder passwordEncoder) {
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioDao.findAll();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioDao.findById(id);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioDao.findByUsername(username);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        // Encriptamos contraseña solo si es nueva o ha sido modificada
        if (usuario.getId() == null || 
            (usuario.getId() != null && usuario.getPassword() != null && !usuario.getPassword().isEmpty())) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else if (usuario.getId() != null) {
            // Si estamos actualizando y no se proporcionó ninguna contraseña mantenemos la que esta existente 
            usuarioDao.findById(usuario.getId()).ifPresent(u -> usuario.setPassword(u.getPassword()));
        }
        return usuarioDao.save(usuario);
    }

    @Override
    public boolean existeUsername(String username) {
        return usuarioDao.existsByUsername(username);
    }

    @Override
    public boolean existeEmail(String email) {
        return usuarioDao.existsByEmail(email);
    }

    @Override
    public void eliminar(Long id) {
        usuarioDao.deleteById(id);
    }
}
