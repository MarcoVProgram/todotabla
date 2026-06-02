package com.decroly.todotabla.model;

import java.time.LocalDate;
import java.util.Objects;

public class Proyecto {

    private int id;
    private String titulo;
    private LocalDate fechaCreacion;
    private LocalDate fechaCierre;

    public Proyecto(int id, String titulo, LocalDate fechaCreacion, LocalDate fechaCierre) {
        this.id = id;
        this.titulo = titulo;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
    }

    public Proyecto(String titulo, LocalDate fechaCreacion, LocalDate fechaCierre) {
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

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDate fechaCierre) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Proyecto proyecto = (Proyecto) o;
        return id == proyecto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
