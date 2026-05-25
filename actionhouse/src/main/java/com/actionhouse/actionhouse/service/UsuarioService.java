package com.actionhouse.actionhouse.service;

import com.actionhouse.actionhouse.model.Usuario;
import com.actionhouse.actionhouse.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repo,
                          PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    // Spring Security llama esto al hacer login
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        Usuario u = repo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(u.getEmail())
                .password(u.getPassword())
                .roles(u.getRol())
                .build();
    }

    // Registrar nuevo usuario
    public boolean registrar(String nombre, String email, String password) {
        if (repo.existsByEmail(email)) {
            return false; // Email ya registrado
        }
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password)); // BCrypt aqui
        repo.save(u);
        return true;
    }
}