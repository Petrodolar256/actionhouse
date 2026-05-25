package com.actionhouse.actionhouse.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Objeto {

    private int id;
    private String titulo;
    private String descripcion;
    private String tipo; // "donacion" o "subasta"
    private String estado;
    private BigDecimal precioInicial;
    private String imagenUrl;
    private int idUsuario;
    private String nombreUsuario;
    private LocalDateTime fechaPublicacion;

    public Objeto() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getPrecioInicial() { return precioInicial; }
    public void setPrecioInicial(BigDecimal precioInicial) { this.precioInicial = precioInicial; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
}