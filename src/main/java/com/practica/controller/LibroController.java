package com.practica.controller;

import com.practica.domain.Categoria;
import com.practica.domain.Libro;
import com.practica.service.CategoriaService;
import com.practica.service.LibroService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
 
@Controller
@RequestMapping("/libros")
public class LibroController {
 
    private final LibroService libroService;
    private final CategoriaService categoriaService;
 
    @Autowired
    public LibroController(LibroService libroService, CategoriaService categoriaService) {
        this.libroService = libroService;
        this.categoriaService = categoriaService;
    }
 
    @GetMapping
    public String listarLibros(Model model) {
        model.addAttribute("libros", libroService.listarTodos());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "libros/lista";
    }
 
    @GetMapping("/buscar")
    public String buscarLibros(@RequestParam(required = false) String titulo, 
                             @RequestParam(required = false) String autor,
                             @RequestParam(required = false) Long categoriaId,
                             Model model) {
        List<Libro> libros;
        if (titulo != null && !titulo.isEmpty()) {
            libros = libroService.buscarPorTitulo(titulo);
        } else if (autor != null && !autor.isEmpty()) {
            libros = libroService.buscarPorAutor(autor);
        } else if (categoriaId != null) {
            Optional<Categoria> categoria = categoriaService.buscarPorId(categoriaId);
            libros = categoria.map(libroService::buscarPorCategoria).orElse(List.of());
        } else {
            libros = libroService.listarTodos();
        }
        model.addAttribute("libros", libros);
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "libros/busqueda";
    }
 
    @GetMapping("/{id}")
    public String verLibro(@PathVariable Long id, Model model) {
        Optional<Libro> libro = libroService.buscarPorId(id);
        if (libro.isPresent()) {
            model.addAttribute("libro", libro.get());
            return "libros/detalle";
        }
        return "redirect:/libros";
    }
 
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Libro libro = new Libro();
        libro.setEjemplaresDisponibles(0); 
        model.addAttribute("libro", libro);
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "libros/formulario";
    }
 
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/guardar")
    public String guardarLibro(@ModelAttribute Libro libro, BindingResult result,
            RedirectAttributes attributes, Model model) {
        if (libro.getEjemplaresDisponibles() == null) {
            libro.setEjemplaresDisponibles(0);
        }
 
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "libros/formulario";
        }
 
        // Verificar si el ISBN ya existe
        if (libro.getId() == null && libroService.existeIsbn(libro.getIsbn())) {
            result.rejectValue("isbn", "error.libro", "Ya existe un libro con este ISBN");
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "libros/formulario";
        }
 
        libroService.guardar(libro);
        attributes.addFlashAttribute("mensaje", "Libro guardado exitosamente");
        return "redirect:/libros";
    }


 
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Libro> libro = libroService.buscarPorId(id);
        if (libro.isPresent()) {
            model.addAttribute("libro", libro.get());
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "libros/formulario";
        }
        return "redirect:/libros";
    }
 
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarLibro(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            libroService.eliminar(id);
            attributes.addFlashAttribute("mensaje", "Libro eliminado exitosamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "No se pudo eliminar el libro");
        }
        return "redirect:/libros";
    }
}
