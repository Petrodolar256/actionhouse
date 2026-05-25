package com.actionhouse.actionhouse.controller;

import com.actionhouse.actionhouse.repository.UsuarioRepository;
import com.actionhouse.actionhouse.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepo;

    public AuthController(UsuarioService usuarioService,
                          UsuarioRepository usuarioRepo) {
        this.usuarioService = usuarioService;
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/catalogo";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Sesión cerrada correctamente");
        }
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String nombre,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   Model model) {
        boolean ok = usuarioService.registrar(nombre, email, password);
        if (!ok) {
            model.addAttribute("error", "Este email ya está registrado");
            return "registro";
        }
        return "redirect:/login?registro=true";
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal UserDetails userDetails,
                         Model model) {
        var usuario = usuarioRepo.findByEmail(userDetails.getUsername())
                .orElse(null);
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping("/perfil/nombre")
    public String actualizarNombre(@RequestParam String nombre,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        var usuario = usuarioRepo.findByEmail(userDetails.getUsername())
                .orElse(null);
        if (usuario == null) return "redirect:/login";

        if (nombre.isBlank()) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("errorNombre", "El nombre no puede estar vacío");
            return "perfil";
        }

        if (usuarioRepo.existsByNombreAndNotId(nombre, usuario.getId())) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("errorNombre", "Ese nombre ya está en uso por otro usuario");
            return "perfil";
        }

        usuarioRepo.updateNombre(usuario.getId(), nombre);
        return "redirect:/perfil?actualizado=true";
    }

    @PostMapping("/perfil/eliminar")
    public String eliminarCuenta(@AuthenticationPrincipal UserDetails userDetails,
                                 HttpServletRequest request) throws Exception {
        var usuario = usuarioRepo.findByEmail(userDetails.getUsername())
                .orElse(null);
        if (usuario != null) {
            request.logout();
            usuarioRepo.deleteById(usuario.getId());
        }
        return "redirect:/login?cuenta=eliminada";
    }
}