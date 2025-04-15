package com.practica.controller;

import com.practica.dao.RolDao;
import com.practica.domain.Rol;
import com.practica.domain.Usuario;
import com.practica.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolDao rolDao;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, RolDao rolDao) {
        this.usuarioService = usuarioService;
        this.rolDao = rolDao;
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, BindingResult result, 
                                 RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "usuario/registro";
        }
        
        if (usuarioService.existeUsername(usuario.getUsername())) {
            result.rejectValue("username", "error.usuario", "Este nombre de usuario ya está en uso");
            return "usuario/registro";
        }
        
        if (usuarioService.existeEmail(usuario.getEmail())) {
            result.rejectValue("email", "error.usuario", "Este correo electrónico ya está registrado");
            return "usuario/registro";
        }
        
        // Asignar rol de usuario por defecto
        Optional<Rol> rolUsuario = rolDao.findByNombre("USER");
        if (rolUsuario.isPresent()) {
            Set<Rol> roles = new HashSet<>();
            roles.add(rolUsuario.get());
            usuario.setRoles(roles);
            
            usuarioService.guardar(usuario);
            attributes.addFlashAttribute("mensaje", "Registro exitoso. Ahora puede iniciar sesión.");
            return "redirect:/login";
        } else {
            attributes.addFlashAttribute("error", "Error en el registro. Por favor, intente más tarde.");
            return "redirect:/registro";
        }
    }

    @GetMapping("/admin/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuario/lista";
    }

    @GetMapping("/admin/usuarios/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String mostrarFormularioNuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolDao.findAll());
        return "usuario/formulario";
    }

    @PostMapping("/admin/usuarios/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardarUsuario(@ModelAttribute Usuario usuario, 
                               @RequestParam(required = false) Set<Long> roles,
                               BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "usuario/formulario";
        }
        
        // Verificar si el username ya existe esto seria solo para nuevos usuarios 
        if (usuario.getId() == null && usuarioService.existeUsername(usuario.getUsername())) {
            result.rejectValue("username", "error.usuario", "Este nombre de usuario ya está en uso");
            return "usuario/formulario";
        }
        
        // Verificar si el email ya existe esto seria para nuevos usuarios 
        if (usuario.getId() == null && usuarioService.existeEmail(usuario.getEmail())) {
            result.rejectValue("email", "error.usuario", "Este correo electrónico ya está registrado");
            return "usuario/formulario";
        }
        
        // Asignar roles seleccionados
        if (roles != null && !roles.isEmpty()) {
            Set<Rol> userRoles = new HashSet<>();
            for (Long rolId : roles) {
                rolDao.findById(rolId).ifPresent(userRoles::add);
            }
            usuario.setRoles(userRoles);
        } else {
            // Si no se seleccionó ningún rol, asignar rol USER por defecto
            rolDao.findByNombre("USER").ifPresent(rol -> {
                Set<Rol> userRoles = new HashSet<>();
                userRoles.add(rol);
                usuario.setRoles(userRoles);
            });
        }
        
        usuarioService.guardar(usuario);
        attributes.addFlashAttribute("mensaje", "Usuario guardado exitosamente");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/usuarios/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String mostrarFormularioEditarUsuario(@PathVariable Long id, Model model) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            model.addAttribute("roles", rolDao.findAll());
            // Extraer IDs de roles para selección múltiple
            Set<Long> rolesIds = new HashSet<>();
            usuario.get().getRoles().forEach(rol -> rolesIds.add(rol.getId()));
            model.addAttribute("selectedRoles", rolesIds);
            return "usuario/formulario";
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/usuarios/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            usuarioService.eliminar(id);
            attributes.addFlashAttribute("mensaje", "Usuario eliminado exitosamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "No se pudo eliminar el usuario");
        }
        return "redirect:/admin/usuarios";
    }
}
