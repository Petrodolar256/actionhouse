package com.actionhouse.actionhouse.repository;

import com.actionhouse.actionhouse.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Usuario> usuarioMapper = (rs, rowNum) -> {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombre(rs.getString("nombre"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getBoolean("activo"));
        return u;
    };

    public Optional<Usuario> findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        return jdbc.query(sql, usuarioMapper, email)
                .stream().findFirst();
    }

    public void save(Usuario u) {
        String sql = """
            INSERT INTO usuarios (nombre, email, password, rol)
            VALUES (?, ?, ?, 'user')
            """;
        jdbc.update(sql, u.getNombre(), u.getEmail(), u.getPassword());
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public void updateNombre(int id, String nombre) {
        String sql = "UPDATE usuarios SET nombre = ? WHERE id = ?";
        jdbc.update(sql, nombre, id);
    }

    public void deleteById(int id) {
        jdbc.update("DELETE FROM ofertas WHERE id_usuario = ?", id);
        jdbc.update("DELETE FROM ofertas WHERE id_objeto IN " +
                "(SELECT id FROM objetos WHERE id_usuario = ?)", id);
        jdbc.update("DELETE FROM objetos WHERE id_usuario = ?", id);
        jdbc.update("DELETE FROM usuarios WHERE id = ?", id);
    }
    public Optional<Usuario> findById(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        return jdbc.query(sql, usuarioMapper, id).stream().findFirst();
    }
    public boolean existsByNombreAndNotId(String nombre, int id) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE nombre = ? AND id != ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, nombre, id);
        return count != null && count > 0;
    }
}