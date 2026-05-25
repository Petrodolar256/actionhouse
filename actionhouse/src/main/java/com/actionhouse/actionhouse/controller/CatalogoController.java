package com.actionhouse.actionhouse.controller;

import com.actionhouse.actionhouse.model.Objeto;
import com.actionhouse.actionhouse.model.Oferta;
import com.actionhouse.actionhouse.model.Usuario;
import com.actionhouse.actionhouse.repository.MensajeRepository;
import com.actionhouse.actionhouse.repository.ObjetoRepository;
import com.actionhouse.actionhouse.repository.OfertaRepository;
import com.actionhouse.actionhouse.repository.UsuarioRepository;
import com.actionhouse.actionhouse.service.CloudinaryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class CatalogoController {

    private final ObjetoRepository objetoRepo;
    private final UsuarioRepository usuarioRepo;
    private final OfertaRepository ofertaRepo;
    private final MensajeRepository mensajeRepo;
    private final CloudinaryService cloudinaryService;

    public CatalogoController(ObjetoRepository objetoRepo,
                              UsuarioRepository usuarioRepo,
                              OfertaRepository ofertaRepo,
                              MensajeRepository mensajeRepo,
                              CloudinaryService cloudinaryService) {
        this.objetoRepo = objetoRepo;
        this.usuarioRepo = usuarioRepo;
        this.ofertaRepo = ofertaRepo;
        this.mensajeRepo = mensajeRepo;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(required = false) String tipo,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        List<Objeto> objetos = objetoRepo.findAllDisponibles();
        if (tipo != null && !tipo.isEmpty()) {
            objetos = objetos.stream()
                    .filter(o -> o.getTipo().equals(tipo))
                    .toList();
        }
        Usuario usuario = usuarioRepo
                .findByEmail(userDetails.getUsername()).orElse(null);
        int noLeidos = usuario != null ?
                mensajeRepo.contarNoLeidos(usuario.getId()) : 0;
        model.addAttribute("objetos", objetos);
        model.addAttribute("usuario", usuario);
        model.addAttribute("filtroActivo", tipo);
        model.addAttribute("noLeidos", noLeidos);
        return "catalogo";
    }

    @GetMapping("/objeto/{id}")
    public String detalle(@PathVariable int id,
                          @AuthenticationPrincipal UserDetails userDetails,
                          Model model) {
        Objeto objeto = objetoRepo.findById(id).orElse(null);
        if (objeto == null) return "redirect:/catalogo";

        Usuario usuario = usuarioRepo
                .findByEmail(userDetails.getUsername()).orElse(null);

        List<Oferta> ofertas = ofertaRepo.findByObjeto(id);
        Oferta mejorOferta = ofertaRepo.findMejorOferta(id);
        Oferta ofertaAceptada = ofertaRepo.findOfertaAceptada(id);
        boolean yaOferto = usuario != null &&
                ofertaRepo.yaOferto(id, usuario.getId());
        int noLeidos = usuario != null ?
                mensajeRepo.contarNoLeidos(usuario.getId()) : 0;

        model.addAttribute("objeto", objeto);
        model.addAttribute("usuario", usuario);
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("mejorOferta", mejorOferta);
        model.addAttribute("ofertaAceptada", ofertaAceptada);
        model.addAttribute("yaOferto", yaOferto);
        model.addAttribute("noLeidos", noLeidos);
        return "detalle";
    }

    @GetMapping("/publicar")
    public String publicar(@AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        Usuario usuario = usuarioRepo
                .findByEmail(userDetails.getUsername()).orElse(null);
        int noLeidos = usuario != null ?
                mensajeRepo.contarNoLeidos(usuario.getId()) : 0;
        model.addAttribute("usuario", usuario);
        model.addAttribute("noLeidos", noLeidos);
        return "publicar";
    }

    @PostMapping("/publicar")
    public String procesarPublicacion(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String tipo,
            @RequestParam(defaultValue = "0") BigDecimal precioInicial,
            @RequestParam(required = false) MultipartFile imagen,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        Usuario usuario = usuarioRepo
                .findByEmail(userDetails.getUsername()).orElse(null);
        if (usuario == null) return "redirect:/login";

        // Subir imagen a Cloudinary
        String imagenUrl = null;
        if (imagen != null && !imagen.isEmpty()) {
            imagenUrl = cloudinaryService.upload(imagen);
        }

        Objeto obj = new Objeto();
        obj.setTitulo(titulo);
        obj.setDescripcion(descripcion);
        obj.setTipo(tipo);
        obj.setPrecioInicial(tipo.equals("donacion") ? BigDecimal.ZERO : precioInicial);
        obj.setImagenUrl(imagenUrl);
        obj.setIdUsuario(usuario.getId());
        objetoRepo.save(obj);
        return "redirect:/catalogo";
    }

    @GetMapping("/mis-objetos")
    public String misObjetos(@AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        Usuario usuario = usuarioRepo
                .findByEmail(userDetails.getUsername()).orElse(null);
        List<Objeto> objetos = objetoRepo.findByUsuario(usuario.getId());
        int noLeidos = mensajeRepo.contarNoLeidos(usuario.getId());
        model.addAttribute("objetos", objetos);
        model.addAttribute("usuario", usuario);
        model.addAttribute("noLeidos", noLeidos);
        return "misobjetos";
    }

    @PostMapping("/objeto/{id}/eliminar")
    public String eliminar(@PathVariable int id,
                           @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepo
                .findByEmail(userDetails.getUsername()).orElse(null);
        if (usuario != null) {
            objetoRepo.findById(id).ifPresent(obj -> {
                // Borrar imagen de Cloudinary
                if (obj.getImagenUrl() != null &&
                        obj.getIdUsuario() == usuario.getId()) {
                    cloudinaryService.delete(obj.getImagenUrl());
                }
            });
            objetoRepo.deleteById(id, usuario.getId());
        }
        return "redirect:/mis-objetos";
    }

    @PostMapping("/objeto/{id}/entregar")
    public String entregar(@PathVariable int id,
                           @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepo
                .findByEmail(userDetails.getUsername()).orElse(null);
        if (usuario != null) {
            objetoRepo.updateEstado(id, usuario.getId(), "entregado");
        }
        return "redirect:/mis-objetos";
    }

    @PostMapping("/objeto/{id}/ofertar")
    public String ofertar(@PathVariable int id,
                          @RequestParam BigDecimal monto,
                          @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepo
                .findByEmail(userDetails.getUsername()).orElse(null);
        if (usuario == null) return "redirect:/login";
        Oferta oferta = new Oferta();
        oferta.setMonto(monto);
        oferta.setIdObjeto(id);
        oferta.setIdUsuario(usuario.getId());
        ofertaRepo.save(oferta);
        return "redirect:/objeto/" + id;
    }

    @PostMapping("/objeto/{id}/aceptar/{idOferta}")
    public String aceptarOferta(@PathVariable int id,
                                @PathVariable int idOferta,
                                @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepo
                .findByEmail(userDetails.getUsername()).orElse(null);
        Objeto objeto = objetoRepo.findById(id).orElse(null);
        if (usuario != null && objeto != null &&
                objeto.getIdUsuario() == usuario.getId()) {
            ofertaRepo.aceptarOferta(idOferta, id);
        }
        return "redirect:/objeto/" + id;
    }
}