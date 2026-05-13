package com.decroly.todotabla.model;

import java.time.LocalDate;

public class Tarea {

    private int id;
    private String nombre;
    private int prioridad;
    private Estado estado;
    private Proyecto idProyecto;

    public Tarea(int id, String nombre, int prioridad, Estado estado, Proyecto idProyecto) {
        this.id = id;
        this.nombre = nombre;

        this.prioridad = prioridad;
        this.estado = estado;

        this.idProyecto = idProyecto;
    }

    public Tarea(String nombre, int prioridad, Estado estado, Proyecto idProyecto) {
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.estado = estado;
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

    public Proyecto getIdProyecto() {
        return idProyecto;
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", prioridad=" + prioridad +
                ", estado=" + estado +
                ", idProyecto=" + idProyecto.getId() +
                '}';
    }
}
