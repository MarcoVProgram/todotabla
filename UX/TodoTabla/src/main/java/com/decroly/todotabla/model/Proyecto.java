package com.decroly.todotabla.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;

public class Proyecto {

    private Integer id;
    private String titulo;
    private LocalDate fechaCreacion;
    private LocalDate fechaCierre;

    private LinkedList<Integrante> integrantes;

    public Proyecto(int id, String titulo, LocalDate fechaCreacion, LocalDate fechaCierre) {
        this.id = id;
        this.titulo = titulo;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
        this.integrantes = new LinkedList<>();
    }

    public Proyecto(String titulo, LocalDate fechaCreacion, LocalDate fechaCierre) {
        this.id = null;
        this.titulo = titulo;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
        this.integrantes = new LinkedList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDate fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public LinkedList<Integrante> getIntegrantes() {
        return integrantes;
    }

    @Override
    public String toString() {
        return "Proyecto{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaFin=" + fechaCierre +
                '}';
    }
}
