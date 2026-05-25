package com.actionhouse.actionhouse.controller;

import com.actionhouse.actionhouse.model.Objeto;
import com.actionhouse.actionhouse.model.Oferta;
import com.actionhouse.actionhouse.repository.ObjetoRepository;
import com.actionhouse.actionhouse.repository.OfertaRepository;
import com.actionhouse.actionhouse.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ObjetoRepository objetoRepo;
    private final OfertaRepository ofertaRepo;
    private final UsuarioService usuarioService;

    public ApiController(ObjetoRepository objetoRepo,
                         OfertaRepository ofertaRepo,
                         UsuarioService usuarioService) {
        this.objetoRepo = objetoRepo;
        this.ofertaRepo = ofertaRepo;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/objetos")
    public List<Objeto> objetos() {
        return objetoRepo.findAllDisponibles();
    }

    @GetMapping("/objetos/{id}")
    public ResponseEntity<?> objeto(@PathVariable int id) {
        return objetoRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/objetos/{id}/ofertas")
    public ResponseEntity<?> ofertas(@PathVariable int id) {
        if (objetoRepo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ofertaRepo.findByObjeto(id));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        String email  = body.get("email");
        String pass   = body.get("password");

        if (nombre == null || email == null || pass == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "nombre, email y password requeridos"));
        }
        boolean ok = usuarioService.registrar(nombre, email, pass);
        if (!ok) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email ya registrado"));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Usuario creado correctamente"));
    }
}