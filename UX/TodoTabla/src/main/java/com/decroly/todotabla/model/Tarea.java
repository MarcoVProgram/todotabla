package com.decroly.todotabla.model;

import java.time.LocalDate;

public class Tarea {

    private int id;
    private String nombre;
    private Miembro idMiembro;
    private int prioridad;
    private Estado estado;
    private LocalDate fechaAsignacion;
    private LocalDate fechaFin;
    private Proyecto idProyecto;

    public Tarea(int id, String nombre, Miembro idMiembro, int prioridad, Estado estado, LocalDate fechaAsignacion, LocalDate fechaFin, Proyecto idProyecto) {
        this.id = id;
        this.nombre = nombre;
        this.idMiembro = idMiembro;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFin = fechaFin;
        this.idProyecto = idProyecto;
    }

    public Tarea(String nombre, Miembro idMiembro, int prioridad, Estado estado, LocalDate fechaAsignacion, LocalDate fechaFin, Proyecto idProyecto) {
        this.nombre = nombre;
        this.idMiembro = idMiembro;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFin = fechaFin;
        this.idProyecto = idProyecto;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Miembro getIdMiembro() {
        return idMiembro;
    }

    public void setIdMiembro(Miembro idMiembro) {
        this.idMiembro = idMiembro;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Proyecto getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Proyecto idProyecto) {
        this.idProyecto = idProyecto;
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", idMiembro=" + idMiembro +
                ", prioridad=" + prioridad +
                ", estado=" + estado +
                ", fechaAsignacion=" + fechaAsignacion +
                ", fechaFin=" + fechaFin +
                ", idProyecto=" + idProyecto +
                '}';
    }
}
