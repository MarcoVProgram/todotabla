package com.decroly.todotabla.model;

import java.time.LocalDateTime;

public class HistorialTareas {

    private int id;
    private Estado estado;
    private Tarea idTarea;
    private LocalDateTime fechaCambio;

    public HistorialTareas(int id, Estado estado, Tarea idTarea, LocalDateTime fechaCambio) {
        this.id = id;
        this.estado = estado;
        this.idTarea = idTarea;
        this.fechaCambio = fechaCambio;
    }

    public HistorialTareas(Estado estado, Tarea idTarea, LocalDateTime fechaCambio) {
        this.estado = estado;
        this.idTarea = idTarea;
        this.fechaCambio = fechaCambio;
    }

    public int getId() {
        return id;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Tarea getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Tarea idTarea) {
        this.idTarea = idTarea;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    @Override
    public String toString() {
        return "HistorialTareas{" +
                "id=" + id +
                ", estado=" + estado +
                ", idTarea=" + idTarea.getId() +
                ", fechaCambio=" + fechaCambio +
                '}';
    }
}
