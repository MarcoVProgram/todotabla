package com.decroly.todotabla.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un registro histórico del cambio de estado de una {@link Tarea}.
 * Generado automáticamente por triggers de la base de datos; no se inserta manualmente.
 */
public class HistorialTareas {

    private int id;
    private Estado estado;
    private Tarea idTarea;
    private LocalDateTime fechaCambio;

    /**
     * Crea un registro de historial con ID conocido, usado al recuperar datos de la base de datos.
     *
     * @param id          identificador único del registro
     * @param estado      estado al que cambió la tarea
     * @param idTarea     tarea a la que pertenece el registro
     * @param fechaCambio fecha y hora exactas en que se produjo el cambio
     */
    public HistorialTareas(int id, Estado estado, Tarea idTarea, LocalDateTime fechaCambio) {
        this.id = id;
        this.estado = estado;
        this.idTarea = idTarea;
        this.fechaCambio = fechaCambio;
    }

    /**
     * Crea un registro de historial sin ID, usado antes de persistirlo en la base de datos.
     *
     * @param estado      estado al que cambió la tarea
     * @param idTarea     tarea a la que pertenece el registro
     * @param fechaCambio fecha y hora exactas en que se produjo el cambio
     */
    public HistorialTareas(Estado estado, Tarea idTarea, LocalDateTime fechaCambio) {
        this.estado = estado;
        this.idTarea = idTarea;
        this.fechaCambio = fechaCambio;
    }

    /** @return el identificador único del registro */
    public int getId() {
        return id;
    }

    /** @return el estado al que cambió la tarea */
    public Estado getEstado() {
        return estado;
    }

    /** @return la tarea a la que pertenece el registro */
    public Tarea getIdTarea() {
        return idTarea;
    }

    /** @return la fecha y hora en que se produjo el cambio de estado */
    public LocalDateTime getFechaCambio() {
        return fechaCambio;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HistorialTareas tareas = (HistorialTareas) o;
        return id == tareas.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
