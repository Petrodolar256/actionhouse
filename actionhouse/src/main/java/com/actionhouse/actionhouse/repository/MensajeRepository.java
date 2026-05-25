package com.actionhouse.actionhouse.repository;

import com.actionhouse.actionhouse.model.Mensaje;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MensajeRepository {

    private final JdbcTemplate jdbc;

    public MensajeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Mensaje> mensajeMapper = (rs, rowNum) -> {
        Mensaje m = new Mensaje();
        m.setId(rs.getInt("id"));
        m.setIdObjeto(rs.getInt("id_objeto"));
        m.setIdEmisor(rs.getInt("id_emisor"));
        m.setIdReceptor(rs.getInt("id_receptor"));
        m.setContenido(rs.getString("contenido"));
        m.setLeido(rs.getBoolean("leido"));
        m.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        m.setNombreEmisor(rs.getString("nombre_emisor"));
        try { m.setNombreReceptor(rs.getString("nombre_receptor")); }
        catch (Exception ignored) {}
        try { m.setTituloObjeto(rs.getString("titulo_objeto")); }
        catch (Exception ignored) {}
        return m;
    };

    public List<Mensaje> findConversacion(int idObjeto,
                                          int idUsuario1,
                                          int idUsuario2) {
        String sql = """
            SELECT m.*,
                   ue.nombre AS nombre_emisor,
                   ur.nombre AS nombre_receptor,
                   o.titulo  AS titulo_objeto
            FROM mensajes m
            JOIN usuarios ue ON m.id_emisor   = ue.id
            JOIN usuarios ur ON m.id_receptor = ur.id
            JOIN objetos  o  ON m.id_objeto   = o.id
            WHERE m.id_objeto = ?
            AND ((m.id_emisor = ? AND m.id_receptor = ?)
              OR (m.id_emisor = ? AND m.id_receptor = ?))
            ORDER BY m.fecha ASC
            """;
        return jdbc.query(sql, mensajeMapper,
                idObjeto, idUsuario1, idUsuario2,
                idUsuario2, idUsuario1);
    }

    public void save(Mensaje m) {
        String sql = """
            INSERT INTO mensajes (id_objeto, id_emisor, id_receptor, contenido)
            VALUES (?, ?, ?, ?)
            """;
        jdbc.update(sql, m.getIdObjeto(), m.getIdEmisor(),
                m.getIdReceptor(), m.getContenido());
    }

    public void marcarLeidos(int idObjeto, int idReceptor) {
        String sql = """
            UPDATE mensajes SET leido = TRUE
            WHERE id_objeto = ? AND id_receptor = ?
            """;
        jdbc.update(sql, idObjeto, idReceptor);
    }

    public int contarNoLeidos(int idUsuario) {
        String sql = """
            SELECT COUNT(*) FROM mensajes
            WHERE id_receptor = ? AND leido = FALSE
            """;
        Integer count = jdbc.queryForObject(sql, Integer.class, idUsuario);
        return count != null ? count : 0;
    }

    public List<Mensaje> findChatsActivos(int idUsuario) {
        String sql = """
            SELECT m.*,
                   ue.nombre AS nombre_emisor,
                   ur.nombre AS nombre_receptor,
                   o.titulo  AS titulo_objeto
            FROM mensajes m
            JOIN usuarios ue ON m.id_emisor   = ue.id
            JOIN usuarios ur ON m.id_receptor = ur.id
            JOIN objetos  o  ON m.id_objeto   = o.id
            WHERE m.id = (
                SELECT MAX(m2.id) FROM mensajes m2
                WHERE m2.id_objeto = m.id_objeto
                AND LEAST(m2.id_emisor, m2.id_receptor)
                    = LEAST(m.id_emisor, m.id_receptor)
                AND GREATEST(m2.id_emisor, m2.id_receptor)
                    = GREATEST(m.id_emisor, m.id_receptor)
                AND (m2.id_emisor = ? OR m2.id_receptor = ?)
            )
            AND (m.id_emisor = ? OR m.id_receptor = ?)
            ORDER BY m.fecha DESC
            """;
        return jdbc.query(sql, mensajeMapper,
                idUsuario, idUsuario, idUsuario, idUsuario);
    }
}