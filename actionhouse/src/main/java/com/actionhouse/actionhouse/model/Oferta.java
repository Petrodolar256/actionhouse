package com.actionhouse.actionhouse.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Oferta {

    private int id;
    private BigDecimal monto;
    private int idObjeto;
    private int idUsuario;
    private String nombreUsuario;
    private LocalDateTime fecha;
    private boolean aceptada;

    public Oferta() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public int getIdObjeto() { return idObjeto; }
    public void setIdObjeto(int idObjeto) { this.idObjeto = idObjeto; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String n) { this.nombreUsuario = n; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public boolean isAceptada() { return aceptada; }
    public void setAceptada(boolean aceptada) { this.aceptada = aceptada; }
}