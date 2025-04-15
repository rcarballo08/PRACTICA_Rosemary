
package com.practica.service;

import com.practica.domain.Prestamo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
 
public interface PrestamoService {
    List<Prestamo> listarTodos();
    Optional<Prestamo> buscarPorId(Long id);
    List<Prestamo> buscarPorUsuario(Long usuarioId);
    List<Prestamo> buscarPorEstado(Prestamo.EstadoPrestamo estado);
    List<Prestamo> buscarPrestamosPendientes(Long usuarioId);
    List<Prestamo> buscarPrestamosVencidos();
    Prestamo realizarPrestamo(Long libroId, Long usuarioId, LocalDate fechaDevolucionPrevista);
    Prestamo registrarDevolucion(Long prestamoId);
    void eliminar(Long id);
}
