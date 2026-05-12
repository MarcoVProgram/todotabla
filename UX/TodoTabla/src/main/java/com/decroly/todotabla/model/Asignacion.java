package com.decroly.todotabla.model;

import java.time.LocalDate;

public class Asignacion {

    private int id;
    private Miembro idMiembro;
    private Tarea idTarea;
    private LocalDate fechaAsignacion;
    private LocalDate fechaFin;

    public Asignacion(int id, Miembro idMiembro, Tarea idTarea, LocalDate fechaAsignacion, LocalDate fechaFin) {
        this.id = id;
        this.idMiembro = idMiembro;
        this.idTarea = idTarea;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFin = fechaFin;
    }

    public Asignacion(Miembro idMiembro, Tarea idTarea, LocalDate fechaAsignacion, LocalDate fechaFin) {
        this.idMiembro = idMiembro;
        this.idTarea = idTarea;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFin = fechaFin;
    }

    public int getId() {
        return id;
    }

    public Miembro getIdMiembro() {
        return idMiembro;
    }

    public void setIdMiembro(Miembro idMiembro) {
        this.idMiembro = idMiembro;
    }

    public Tarea getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Tarea idTarea) {
        this.idTarea = idTarea;
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

    @Override
    public String toString() {
        return "Asignacion{" +
                "id=" + id +
                ", idMiembro=" + idMiembro.getId() +
                ", idTarea=" + idTarea.getId() +
                ", fechaAsignacion=" + fechaAsignacion +
                ", fechaFin=" + fechaFin +
                '}';
    }
}
