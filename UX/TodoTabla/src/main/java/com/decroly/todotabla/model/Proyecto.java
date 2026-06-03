package com.decroly.todotabla.model;

import java.time.LocalDate;
import java.util.*;

public class Proyecto {

    private Integer id;
    private String titulo;
    private LocalDate fechaCreacion;
    private LocalDate fechaCierre;
    private List<Integrante> integrantesTemp;

    public Proyecto(int id, String titulo, LocalDate fechaCreacion, LocalDate fechaCierre) {
        this.id = id;
        this.titulo = titulo;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
        this.integrantesTemp = new LinkedList<>();
    }

    public Proyecto(String titulo, LocalDate fechaCreacion, LocalDate fechaCierre) {
        this.titulo = titulo;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
        this.integrantesTemp = new LinkedList<>();
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

    public List<Integrante> getIntegrantesTemp() {
        return integrantesTemp;
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
        if (this == o) return true;
        if (!(o instanceof Proyecto proyecto)) return false;

        return Objects.equals(id, proyecto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
