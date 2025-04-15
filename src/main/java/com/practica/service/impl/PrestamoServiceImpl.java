package com.practica.service.impl;

import com.practica.dao.PrestamoDao;
import com.practica.domain.Libro;
import com.practica.domain.Prestamo;
import com.practica.domain.Usuario;
import com.practica.service.LibroService;
import com.practica.service.PrestamoService;
import com.practica.service.UsuarioService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoDao prestamoDao;
    private final LibroService libroService;
    private final UsuarioService usuarioService;

    @Autowired
    public PrestamoServiceImpl(PrestamoDao prestamoDao, LibroService libroService, UsuarioService usuarioService) {
        this.prestamoDao = prestamoDao;
        this.libroService = libroService;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<Prestamo> listarTodos() {
        return prestamoDao.findAll();
    }

    @Override
    public Optional<Prestamo> buscarPorId(Long id) {
        return prestamoDao.findById(id);
    }

    @Override
    public List<Prestamo> buscarPorUsuario(Long usuarioId) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(usuarioId);
        return usuario.map(prestamoDao::findByUsuario).orElse(List.of());
    }

    @Override
    public List<Prestamo> buscarPorEstado(Prestamo.EstadoPrestamo estado) {
        return prestamoDao.findByEstado(estado);
    }

    @Override
    public List<Prestamo> buscarPrestamosPendientes(Long usuarioId) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(usuarioId);
        return usuario.map(u -> prestamoDao.findByUsuarioAndEstado(u, Prestamo.EstadoPrestamo.PRESTADO))
                .orElse(List.of());
    }

    @Override
    public List<Prestamo> buscarPrestamosVencidos() {
        return prestamoDao.findByFechaDevolucionPrevistaBeforeAndEstado(
                LocalDate.now(), Prestamo.EstadoPrestamo.PRESTADO);
    }

    @Override
    @Transactional
    public Prestamo realizarPrestamo(Long libroId, Long usuarioId, LocalDate fechaDevolucionPrevista) {
        // Verificar disponibilidad del libro
        if (!libroService.tieneEjemplaresDisponibles(libroId)) {
            throw new IllegalStateException("No hay ejemplares disponibles para este libro");
        }

        // Obtener libro y usuario
        Optional<Libro> optionalLibro = libroService.buscarPorId(libroId);
        Optional<Usuario> optionalUsuario = usuarioService.buscarPorId(usuarioId);

        if (optionalLibro.isEmpty() || optionalUsuario.isEmpty()) {
            throw new IllegalArgumentException("Libro o usuario no encontrado");
        }

        Libro libro = optionalLibro.get();
        Usuario usuario = optionalUsuario.get();

        // Actualizar ejemplares disponibles
        libroService.actualizarEjemplaresDisponibles(libroId, -1);

        // Crear y guardar el préstamo
        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucionPrevista(fechaDevolucionPrevista);
        prestamo.setEstado(Prestamo.EstadoPrestamo.PRESTADO);

        return prestamoDao.save(prestamo);
    }

    @Override
    @Transactional
    public Prestamo registrarDevolucion(Long prestamoId) {
        Optional<Prestamo> optionalPrestamo = prestamoDao.findById(prestamoId);

        if (optionalPrestamo.isEmpty()) {
            throw new IllegalArgumentException("Préstamo no encontrado");
        }

        Prestamo prestamo = optionalPrestamo.get();

        // Verificar que el préstamo esté en estado PRESTADO
        if (prestamo.getEstado() != Prestamo.EstadoPrestamo.PRESTADO) {
            throw new IllegalStateException("Este préstamo ya ha sido devuelto o está en otro estado");
        }

        // Actualizar el estado del préstamo
        prestamo.setEstado(Prestamo.EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucionReal(LocalDate.now());

        // Actualizar ejemplares disponibles
        libroService.actualizarEjemplaresDisponibles(prestamo.getLibro().getId(), 1);

        return prestamoDao.save(prestamo);
    }

    @Override
    public void eliminar(Long id) {
        prestamoDao.deleteById(id);
    }
}
