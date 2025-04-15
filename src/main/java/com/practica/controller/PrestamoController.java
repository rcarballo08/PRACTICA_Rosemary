package com.practica.controller;

import com.practica.domain.Prestamo;
import com.practica.domain.Usuario;
import com.practica.service.LibroService;
import com.practica.service.PrestamoService;
import com.practica.service.UsuarioService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;
    private final LibroService libroService;
    private final UsuarioService usuarioService;

    @Autowired
    public PrestamoController(PrestamoService prestamoService, LibroService libroService, UsuarioService usuarioService) {
        this.prestamoService = prestamoService;
        this.libroService = libroService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listarPrestamos(Model model) {
        model.addAttribute("prestamos", prestamoService.listarTodos());
        return "prestamos/lista";
    }

    @GetMapping("/mis-prestamos")
    @PreAuthorize("isAuthenticated()")
    public String misPrestamos(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(username);
        if (usuario.isPresent()) {
            List<Prestamo> prestamos = prestamoService.buscarPorUsuario(usuario.get().getId());
            model.addAttribute("prestamos", prestamos);
            return "prestamos/mis-prestamos";
        }
        
        return "redirect:/home";
    }

    @GetMapping("/solicitar/{libroId}")
    @PreAuthorize("isAuthenticated()")
    public String solicitarPrestamo(@PathVariable Long libroId, Model model) {
        if (!libroService.tieneEjemplaresDisponibles(libroId)) {
            model.addAttribute("error", "No hay ejemplares disponibles de este libro");
            return "redirect:/libros/" + libroId;
        }
        
        model.addAttribute("libro", libroService.buscarPorId(libroId).orElse(null));
        model.addAttribute("fechaPrestamo", LocalDate.now());
        model.addAttribute("fechaDevolucionPrevista", LocalDate.now().plusDays(15));
        
        return "prestamos/solicitar";
    }

    @PostMapping("/confirmar")
    @PreAuthorize("isAuthenticated()")
    public String confirmarPrestamo(@RequestParam Long libroId,
                                  @RequestParam LocalDate fechaDevolucionPrevista,
                                  RedirectAttributes attributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(username);
        if (usuario.isEmpty()) {
            attributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/libros";
        }
        
        try {
            prestamoService.realizarPrestamo(libroId, usuario.get().getId(), fechaDevolucionPrevista);
            attributes.addFlashAttribute("mensaje", "Préstamo realizado exitosamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al realizar el préstamo: " + e.getMessage());
        }
        
        return "redirect:/prestamos/mis-prestamos";
    }

    @GetMapping("/devolver/{prestamoId}")
    @PreAuthorize("isAuthenticated()")
    public String devolverPrestamo(@PathVariable Long prestamoId, RedirectAttributes attributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(username);
        Optional<Prestamo> prestamo = prestamoService.buscarPorId(prestamoId);
        
        if (usuario.isEmpty() || prestamo.isEmpty()) {
            attributes.addFlashAttribute("error", "Préstamo no encontrado");
            return "redirect:/prestamos/mis-prestamos";
        }
        
        if (!prestamo.get().getUsuario().getId().equals(usuario.get().getId()) && 
            !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            attributes.addFlashAttribute("error", "No tiene permiso para devolver este préstamo");
            return "redirect:/prestamos/mis-prestamos";
        }
        
        try {
            prestamoService.registrarDevolucion(prestamoId);
            attributes.addFlashAttribute("mensaje", "Libro devuelto exitosamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al devolver el libro: " + e.getMessage());
        }
        
        return "redirect:/prestamos/mis-prestamos";
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    public String listarPrestamosPendientes(Model model) {
        model.addAttribute("prestamos", prestamoService.buscarPorEstado(Prestamo.EstadoPrestamo.PRESTADO));
        return "prestamos/pendientes";
    }

    @GetMapping("/vencidos")
    @PreAuthorize("hasRole('ADMIN')")
    public String listarPrestamosVencidos(Model model) {
        model.addAttribute("prestamos", prestamoService.buscarPrestamosVencidos());
        return "prestamos/vencidos";
    }
}