package com.decroly.todotabla.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa la asignación de un {@link Usuario} a una {@link Tarea} dentro del sistema.
 * Registra el período durante el cual el usuario estuvo activamente asignado a la tarea.
 */
public class Asignacion {

    private int id;
    private Usuario idUsuario;
    private Tarea idTarea;
    private LocalDate fechaAsignacion;
    private LocalDate fechaFin;

    /**
     * Crea una asignación con ID conocido, usado al recuperar datos de la base de datos.
     *
     * @param id               identificador único de la asignación
     * @param idUsuario        usuario asignado a la tarea
     * @param idTarea          tarea a la que se asigna el usuario
     * @param fechaAsignacion  fecha en que se realizó la asignación
     * @param fechaFin         fecha en que finalizó la asignación, o {@code null} si sigue activa
     */
    public Asignacion(int id, Usuario idUsuario, Tarea idTarea, LocalDate fechaAsignacion, LocalDate fechaFin) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idTarea = idTarea;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFin = fechaFin;
    }

    /**
     * Crea una asignación sin ID, usado antes de persistirla en la base de datos.
     *
     * @param idUsuario        usuario asignado a la tarea
     * @param idTarea          tarea a la que se asigna el usuario
     * @param fechaAsignacion  fecha en que se realizó la asignación
     * @param fechaFin         fecha en que finalizó la asignación, o {@code null} si sigue activa
     */
    public Asignacion(Usuario idUsuario, Tarea idTarea, LocalDate fechaAsignacion, LocalDate fechaFin) {
        this.idUsuario = idUsuario;
        this.idTarea = idTarea;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFin = fechaFin;
    }

    /** @return el identificador único de la asignación */
    public int getId() {
        return id;
    }

    /** @return el usuario asignado a la tarea */
    public Usuario getIdUsuario() {
        return idUsuario;
    }

    /** @return la tarea a la que está asignado el usuario */
    public Tarea getIdTarea() {
        return idTarea;
    }

    /** @return la fecha en que se realizó la asignación */
    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    /** @return la fecha de fin de la asignación, o {@code null} si sigue activa */
    public LocalDate getFechaFin() {
        return fechaFin;
    }

    /**
     * Establece la fecha de fin de la asignación.
     *
     * @param fechaFin la fecha en que finaliza la asignación
     */
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
