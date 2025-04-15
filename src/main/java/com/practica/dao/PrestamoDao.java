package com.practica.dao;

import com.practica.domain.Prestamo;
import com.practica.domain.Usuario;
import com.practica.domain.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PrestamoDao extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByUsuario(Usuario usuario);
    List<Prestamo> findByLibro(Libro libro);
    List<Prestamo> findByEstado(Prestamo.EstadoPrestamo estado);
    List<Prestamo> findByUsuarioAndEstado(Usuario usuario, Prestamo.EstadoPrestamo estado);
    List<Prestamo> findByFechaDevolucionPrevistaBeforeAndEstado(LocalDate fecha, Prestamo.EstadoPrestamo estado);
}
