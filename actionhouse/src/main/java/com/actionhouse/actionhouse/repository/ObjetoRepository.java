package com.actionhouse.actionhouse.repository;

import com.actionhouse.actionhouse.model.Objeto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ObjetoRepository {

    private final JdbcTemplate jdbc;

    public ObjetoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Objeto> objetoMapper = (rs, rowNum) -> {
        Objeto o = new Objeto();
        o.setId(rs.getInt("id"));
        o.setTitulo(rs.getString("titulo"));
        o.setDescripcion(rs.getString("descripcion"));
        o.setTipo(rs.getString("tipo"));
        o.setEstado(rs.getString("estado"));
        o.setPrecioInicial(rs.getBigDecimal("precio_inicial"));
        o.setImagenUrl(rs.getString("imagen_url"));
        o.setIdUsuario(rs.getInt("id_usuario"));
        o.setNombreUsuario(rs.getString("nombre_usuario"));
        o.setFechaPublicacion(rs.getTimestamp("fecha_publicacion").toLocalDateTime());
        return o;
    };

    public List<Objeto> findAllDisponibles() {
        String sql = """
            SELECT o.*, u.nombre AS nombre_usuario
            FROM objetos o
            JOIN usuarios u ON o.id_usuario = u.id
            WHERE o.estado = 'disponible'
            ORDER BY o.fecha_publicacion DESC
            """;
        return jdbc.query(sql, objetoMapper);
    }

    public Optional<Objeto> findById(int id) {
        String sql = """
            SELECT o.*, u.nombre AS nombre_usuario
            FROM objetos o
            JOIN usuarios u ON o.id_usuario = u.id
            WHERE o.id = ?
            """;
        return jdbc.query(sql, objetoMapper, id).stream().findFirst();
    }

    public void save(Objeto o) {
        String sql = """
            INSERT INTO objetos (titulo, descripcion, tipo,
                                 precio_inicial, imagen_url, id_usuario)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        jdbc.update(sql, o.getTitulo(), o.getDescripcion(),
                o.getTipo(), o.getPrecioInicial(),
                o.getImagenUrl(), o.getIdUsuario());
    }

    public List<Objeto> findByUsuario(int idUsuario) {
        String sql = """
            SELECT o.*, u.nombre AS nombre_usuario
            FROM objetos o
            JOIN usuarios u ON o.id_usuario = u.id
            WHERE o.id_usuario = ?
            ORDER BY o.fecha_publicacion DESC
            """;
        return jdbc.query(sql, objetoMapper, idUsuario);
    }

    public void deleteById(int id, int idUsuario) {
        // Borrar mensajes del objeto primero
        jdbc.update("DELETE FROM mensajes WHERE id_objeto = ?", id);
        // Borrar ofertas
        jdbc.update("DELETE FROM ofertas WHERE id_objeto = ?", id);
        // Borrar objeto
        jdbc.update("DELETE FROM objetos WHERE id = ? AND id_usuario = ?", id, idUsuario);
    }

    public void updateEstado(int id, int idUsuario, String estado) {
        String sql = "UPDATE objetos SET estado = ? WHERE id = ? AND id_usuario = ?";
        jdbc.update(sql, estado, id, idUsuario);
    }
}