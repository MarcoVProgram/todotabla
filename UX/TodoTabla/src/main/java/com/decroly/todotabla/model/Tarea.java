package com.decroly.todotabla.model;

import java.util.Objects;

/**
 * Representa una tarea dentro de un {@link Proyecto}.
 * Cada tarea tiene un estado que determina su columna en el tablero Kanban
 * y una prioridad que determina su posición dentro de dicha columna.
 */
public class Tarea {

    private int id;
    private String nombre;
    private int prioridad;
    private Estado estado;
    private Proyecto idProyecto;

    /**
     * Crea una tarea con ID conocido, usado al recuperar datos de la base de datos.
     *
     * @param id         identificador único de la tarea
     * @param nombre     nombre descriptivo de la tarea
     * @param prioridad  posición de la tarea dentro de su columna
     * @param estado     estado actual de la tarea
     * @param idProyecto proyecto al que pertenece la tarea
     */
    public Tarea(int id, String nombre, int prioridad, Estado estado, Proyecto idProyecto) {
        this.id = id;
        this.nombre = nombre;

        this.prioridad = prioridad;
        this.estado = estado;

        this.idProyecto = idProyecto;
    }

    /**
     * Crea una tarea sin ID, usado antes de persistirla en la base de datos.
     *
     * @param nombre     nombre descriptivo de la tarea
     * @param prioridad  posición de la tarea dentro de su columna
     * @param estado     estado inicial de la tarea
     * @param idProyecto proyecto al que pertenece la tarea
     */
    public Tarea(String nombre, int prioridad, Estado estado, Proyecto idProyecto) {
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.estado = estado;
        this.idProyecto = idProyecto;
    }

    /** @return el identificador único de la tarea */
    public int getId() {
        return id;
    }

    /** @return el nombre de la tarea */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la tarea.
     *
     * @param nombre el nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** @return la prioridad de la tarea dentro de su columna */
    public int getPrioridad() {
        return prioridad;
    }

    /**
     * Establece la prioridad de la tarea dentro de su columna.
     *
     * @param prioridad el nuevo valor de prioridad
     */
    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    /** @return el estado actual de la tarea */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la tarea, lo que equivale a moverla a otra columna del tablero.
     *
     * @param estado el nuevo estado
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /** @return el proyecto al que pertenece la tarea */
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        return id == tarea.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
