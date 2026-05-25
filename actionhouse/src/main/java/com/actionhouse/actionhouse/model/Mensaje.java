package com.actionhouse.actionhouse.model;

import java.time.LocalDateTime;

public class Mensaje {
    private int id;
    private int idObjeto;
    private int idEmisor;
    private int idReceptor;
    private String contenido;
    private boolean leido;
    private LocalDateTime fecha;
    private String nombreEmisor;
    private String nombreReceptor;
    private String tituloObjeto;

    public Mensaje() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdObjeto() { return idObjeto; }
    public void setIdObjeto(int idObjeto) { this.idObjeto = idObjeto; }
    public int getIdEmisor() { return idEmisor; }
    public void setIdEmisor(int idEmisor) { this.idEmisor = idEmisor; }
    public int getIdReceptor() { return idReceptor; }
    public void setIdReceptor(int idReceptor) { this.idReceptor = idReceptor; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getNombreEmisor() { return nombreEmisor; }
    public void setNombreEmisor(String nombreEmisor) { this.nombreEmisor = nombreEmisor; }
    public String getNombreReceptor() { return nombreReceptor; }
    public void setNombreReceptor(String n) { this.nombreReceptor = n; }
    public String getTituloObjeto() { return tituloObjeto; }
    public void setTituloObjeto(String t) { this.tituloObjeto = t; }
}