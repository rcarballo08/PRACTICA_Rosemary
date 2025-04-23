package com.practica.controller;

import com.practica.domain.Categoria;
import com.practica.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/categorias")
@PreAuthorize("hasRole('ADMIN')")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listarCategorias(Model model) {
        model.addAttribute("categoria", categoriaService.listarTodas());
        return "categoria/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categoria/formulario";
    }

    @PostMapping("/guardar")
    public String guardarCategoria(@ModelAttribute Categoria categoria, BindingResult result, 
                                  RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "categoria/formulario";
        }
        
        // Verificamos si el nombre ya existe
        if (categoria.getId() == null && categoriaService.existeNombre(categoria.getNombre())) {
            result.rejectValue("nombre", "error.categoria", "Ya existe una categoría con este nombre");
            return "categoria/formulario";
        }
        
        categoriaService.guardar(categoria);
        attributes.addFlashAttribute("mensaje", "Categoría guardada exitosamente");
        return "redirect:/categorias"; 
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Categoria> categoria = categoriaService.buscarPorId(id);
        if (categoria.isPresent()) {
            model.addAttribute("categoria", categoria.get());
            return "categoria/formulario";
        }
        return "redirect:/categorias";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            categoriaService.eliminar(id);
            attributes.addFlashAttribute("mensaje", "Categoría eliminada exitosamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "No se pudo eliminar la categoría, asegúrese de que no tenga libros asociados");
        }
        return "redirect:/categorias";
    }
}
