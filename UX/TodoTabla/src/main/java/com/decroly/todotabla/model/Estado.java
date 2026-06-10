package com.decroly.todotabla.model;

import java.util.Objects;

/**
 * Representa un estado del tablero Kanban.
 * Cada estado tiene un nombre único que actúa como clave, un color de representación visual
 * y un orden que determina su posición en el tablero.
 */
public class Estado {
    private String nombre;
    private String color;
    private int orden;

    /**
     * Crea un estado con todos sus atributos.
     *
     * @param nombre el nombre único del estado, usado como clave en la base de datos
     * @param color  el color hexadecimal de representación visual
     * @param orden  la posición del estado en el tablero
     */
    public Estado(String nombre, String color, int orden) {
        this.nombre = nombre;
        this.color = color;
        this.orden = orden;
    }

    /** @return el nombre único del estado */
    public String getNombre() {
        return nombre;
    }

    /** @return el color hexadecimal del estado */
    public String getColor() {
        return color;
    }

    /**
     * Establece el color de representación visual del estado.
     *
     * @param color el nuevo color hexadecimal
     */
    public void setColor(String color) {
        this.color = color;
    }

    /** @return la posición del estado en el tablero */
    public int getOrden() {
        return orden;
    }

    /**
     * Establece la posición del estado en el tablero.
     *
     * @param orden la nueva posición
     */
    public void setOrden(int orden) {
        this.orden = orden;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Estado{");
        sb.append("nombre='").append(nombre).append('\'');
        sb.append(", color='").append(color).append('\'');
        sb.append(", orden=").append(orden);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Estado estado = (Estado) o;
        return Objects.equals(nombre, estado.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nombre);
    }
}
