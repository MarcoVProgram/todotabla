package com.decroly.todotabla.model;

import java.time.LocalDate;

public class Proyecto {
    private static int cont;
    private int id;
    private String titulo;
    private LocalDate fechaCreacion;
    private LocalDate fechaCierre;

    public Proyecto(String titulo, LocalDate fechaCreacion, LocalDate fechaCierre) {
        this.id = cont++;
        this.titulo = titulo;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
    }

    public int getId() {
        return id;
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

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaFin(LocalDate fechaCierre) {
        this.fechaCierre = fechaCierre;
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
