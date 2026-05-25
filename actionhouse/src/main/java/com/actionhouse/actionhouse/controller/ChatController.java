package com.actionhouse.actionhouse.controller;

import com.actionhouse.actionhouse.model.Mensaje;
import com.actionhouse.actionhouse.model.Objeto;
import com.actionhouse.actionhouse.model.Usuario;
import com.actionhouse.actionhouse.repository.MensajeRepository;
import com.actionhouse.actionhouse.repository.ObjetoRepository;
import com.actionhouse.actionhouse.repository.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ChatController {

    private final MensajeRepository mensajeRepo;
    private final ObjetoRepository objetoRepo;
    private final UsuarioRepository usuarioRepo;

    public ChatController(MensajeRepository mensajeRepo,
                          ObjetoRepository objetoRepo,
                          UsuarioRepository usuarioRepo) {
        this.mensajeRepo = mensajeRepo;
        this.objetoRepo = objetoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // Ver conversación sobre un objeto con otro usuario
    @GetMapping("/chat/{idObjeto}/{idOtroUsuario}")
    public String chat(@PathVariable int idObjeto,
                       @PathVariable int idOtroUsuario,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model) {

        Usuario yo = usuarioRepo.findByEmail(userDetails.getUsername())
                .orElse(null);
        Usuario otro = usuarioRepo.findById(idOtroUsuario).orElse(null);
        Objeto objeto = objetoRepo.findById(idObjeto).orElse(null);

        if (yo == null || otro == null || objeto == null) {
            return "redirect:/catalogo";
        }

        // No puedes chatear contigo mismo
        if (yo.getId() == idOtroUsuario) {
            return "redirect:/catalogo";
        }

        List<Mensaje> mensajes = mensajeRepo.findConversacion(
                idObjeto, yo.getId(), idOtroUsuario);

        // Marcar como leídos los mensajes que me enviaron
        mensajeRepo.marcarLeidos(idObjeto, yo.getId());

        model.addAttribute("yo", yo);
        model.addAttribute("otro", otro);
        model.addAttribute("objeto", objeto);
        model.addAttribute("mensajes", mensajes);
        return "chat";
    }

    // Enviar mensaje
    @PostMapping("/chat/{idObjeto}/{idOtroUsuario}")
    public String enviar(@PathVariable int idObjeto,
                         @PathVariable int idOtroUsuario,
                         @RequestParam String contenido,
                         @AuthenticationPrincipal UserDetails userDetails) {

        Usuario yo = usuarioRepo.findByEmail(userDetails.getUsername())
                .orElse(null);
        if (yo == null || contenido.isBlank()) {
            return "redirect:/chat/" + idObjeto + "/" + idOtroUsuario;
        }

        Mensaje m = new Mensaje();
        m.setIdObjeto(idObjeto);
        m.setIdEmisor(yo.getId());
        m.setIdReceptor(idOtroUsuario);
        m.setContenido(contenido.trim());
        mensajeRepo.save(m);

        return "redirect:/chat/" + idObjeto + "/" + idOtroUsuario;
    }

    // Mis chats activos
    @GetMapping("/mis-chats")
    public String misChats(@AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        Usuario yo = usuarioRepo.findByEmail(userDetails.getUsername())
                .orElse(null);
        if (yo == null) return "redirect:/login";

        List<Mensaje> chats = mensajeRepo.findChatsActivos(yo.getId());
        int noLeidos = mensajeRepo.contarNoLeidos(yo.getId());

        model.addAttribute("usuario", yo);
        model.addAttribute("chats", chats);
        model.addAttribute("noLeidos", noLeidos);
        return "mischats";
    }
}