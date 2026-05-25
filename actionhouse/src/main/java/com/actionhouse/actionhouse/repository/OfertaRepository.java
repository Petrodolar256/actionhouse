package com.actionhouse.actionhouse.repository;

import com.actionhouse.actionhouse.model.Oferta;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OfertaRepository {

    private final JdbcTemplate jdbc;

    public OfertaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Oferta> ofertaMapper = (rs, rowNum) -> {
        Oferta o = new Oferta();
        o.setId(rs.getInt("id"));
        o.setMonto(rs.getBigDecimal("monto"));
        o.setIdObjeto(rs.getInt("id_objeto"));
        o.setIdUsuario(rs.getInt("id_usuario"));
        o.setNombreUsuario(rs.getString("nombre_usuario"));
        o.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        o.setAceptada(rs.getBoolean("aceptada"));
        return o;
    };

    public void save(Oferta oferta) {
        String sql = """
            INSERT INTO ofertas (monto, id_objeto, id_usuario)
            VALUES (?, ?, ?)
            """;
        jdbc.update(sql, oferta.getMonto(),
                oferta.getIdObjeto(), oferta.getIdUsuario());
    }

    public List<Oferta> findByObjeto(int idObjeto) {
        String sql = """
            SELECT ofr.*, u.nombre AS nombre_usuario
            FROM ofertas ofr
            JOIN usuarios u ON ofr.id_usuario = u.id
            WHERE ofr.id_objeto = ?
            ORDER BY ofr.monto DESC
            """;
        return jdbc.query(sql, ofertaMapper, idObjeto);
    }

    public Oferta findMejorOferta(int idObjeto) {
        List<Oferta> ofertas = findByObjeto(idObjeto);
        return ofertas.isEmpty() ? null : ofertas.get(0);
    }

    public void deleteByObjeto(int idObjeto) {
        jdbc.update("DELETE FROM ofertas WHERE id_objeto = ?", idObjeto);
    }

    public void aceptarOferta(int idOferta, int idObjeto) {
        jdbc.update("UPDATE ofertas SET aceptada = TRUE WHERE id = ?", idOferta);
        jdbc.update("UPDATE objetos SET estado = 'reservado' WHERE id = ?", idObjeto);
    }

    public boolean yaOferto(int idObjeto, int idUsuario) {
        String sql = "SELECT COUNT(*) FROM ofertas WHERE id_objeto = ? AND id_usuario = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, idObjeto, idUsuario);
        return count != null && count > 0;
    }

    public Oferta findOfertaAceptada(int idObjeto) {
        String sql = """
            SELECT ofr.*, u.nombre AS nombre_usuario
            FROM ofertas ofr
            JOIN usuarios u ON ofr.id_usuario = u.id
            WHERE ofr.id_objeto = ? AND ofr.aceptada = TRUE
            """;
        return jdbc.query(sql, ofertaMapper, idObjeto)
                .stream().findFirst().orElse(null);
    }
}