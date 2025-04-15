package com.practica.controller;

import com.practica.domain.Contacto;
import com.practica.service.ContactoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/contacto")
public class ContactoController {

    private final ContactoService contactoService;

    @Autowired
    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("contacto", new Contacto());
        return "contacto/formulario";
    }

    @PostMapping
    public String procesarFormulario(@ModelAttribute Contacto contacto, BindingResult result, 
                                   RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "contacto/formulario";
        }
        
        contacto.setFechaEnvio(LocalDateTime.now());
        contactoService.guardar(contacto);
        
        attributes.addFlashAttribute("mensaje", "Mensaje enviado exitosamente. Nos pondremos en contacto pronto.");
        return "redirect:/contacto/gracias";
    }

    @GetMapping("/gracias")
    public String mostrarAgradecimiento() {
        return "contacto/gracias";
    }

    @GetMapping("/mensajes")
    @PreAuthorize("hasRole('ADMIN')")
    public String listarMensajes(Model model) {
        List<Contacto> mensajes = contactoService.listarTodos();
        model.addAttribute("mensajes", mensajes);
        return "contacto/mensajes";
    }
}