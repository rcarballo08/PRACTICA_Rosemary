package com.practica.controller;

import com.practica.domain.Contacto;
import com.practica.domain.Respuesta;
import com.practica.service.ContactoService;
import com.practica.service.RespuestaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/contacto")
public class ContactoController {

    private final ContactoService contactoService;
    private final RespuestaService respuestaService;

    @Autowired
    public ContactoController(ContactoService contactoService, RespuestaService respuestaService) {
        this.contactoService = contactoService;
        this.respuestaService = respuestaService;
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
    
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarMensaje(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            contactoService.eliminar(id);
            attributes.addFlashAttribute("mensaje", "Mensaje eliminado exitosamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "No se pudo eliminar el mensaje: " + e.getMessage());
        }
        return "redirect:/contacto/mensajes";
    }
    
    @GetMapping("/ver/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String verMensaje(@PathVariable Long id, Model model) {
        Optional<Contacto> contacto = contactoService.buscarPorId(id);
        if (contacto.isPresent()) {
            // Obtenemos el mensaje de contacto
            model.addAttribute("contacto", contacto.get());
            
            // Obtenemos las respuestas asociadas
            List<Respuesta> respuestas = respuestaService.buscarPorContacto(contacto.get());
            model.addAttribute("respuestas", respuestas);
            
            // Creamos una nueva respuesta vacía
            Respuesta nuevaRespuesta = new Respuesta();
            nuevaRespuesta.setContacto(contacto.get());
            model.addAttribute("nuevaRespuesta", nuevaRespuesta);
            
            return "contacto/detalle";
        }
        return "redirect:/contacto/mensajes";
    }
    
    @PostMapping("/responder")
    @PreAuthorize("hasRole('ADMIN')")
    public String responderMensaje(@ModelAttribute Respuesta respuesta, RedirectAttributes attributes) {
        try {
            // Obtenemos el usuario actual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Configuramos datos de la respuesta
            respuesta.setFechaRespuesta(LocalDateTime.now());
            respuesta.setRespondidoPor(username);
            
            // Guardamos la respuesta
            respuestaService.guardar(respuesta);
            
            attributes.addFlashAttribute("mensaje", "Respuesta guardada exitosamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al guardar la respuesta: " + e.getMessage());
        }
        
        return "redirect:/contacto/ver/" + respuesta.getContacto().getId();
    }
}
