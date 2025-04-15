package com.practica.dao;

import com.practica.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolDao extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}
