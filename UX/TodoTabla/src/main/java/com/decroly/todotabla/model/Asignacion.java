package com.decroly.todotabla.model;

import java.time.LocalDate;
import java.util.Objects;

public class Asignacion {

    private int id;
    private Usuario idUsuario;
    private Tarea idTarea;
    private LocalDate fechaAsignacion;
    private LocalDate fechaFin;

    public Asignacion(int id, Usuario idUsuario, Tarea idTarea, LocalDate fechaAsignacion, LocalDate fechaFin) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idTarea = idTarea;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFin = fechaFin;
    }

    public Asignacion(Usuario idUsuario, Tarea idTarea, LocalDate fechaAsignacion, LocalDate fechaFin) {
        this.idUsuario = idUsuario;
        this.idTarea = idTarea;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFin = fechaFin;
    }

    public int getId() {
        return id;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public Tarea getIdTarea() {
        return idTarea;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
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
                ", idMiembro=" + idUsuario.getId() +
                ", idTarea=" + idTarea.getId() +
                ", fechaAsignacion=" + fechaAsignacion +
                ", fechaFin=" + fechaFin +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Asignacion that = (Asignacion) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
