package com.decroly.todotabla.model;

import java.time.LocalDate;
import java.util.*;

/**
 * Representa un proyecto dentro del sistema.
 * Contiene la información básica del proyecto y una lista temporal de integrantes
 * usada antes de persistir los datos en la base de datos.
 */
public class Proyecto {

    private Integer id;
    private String titulo;
    private LocalDate fechaCreacion;
    private LocalDate fechaCierre;
    private List<Integrante> integrantesTemp;

    /**
     * Crea un proyecto con ID conocido, usado al recuperar datos de la base de datos.
     *
     * @param id            identificador único del proyecto
     * @param titulo        nombre del proyecto
     * @param fechaCreacion fecha en que se creó el proyecto
     * @param fechaCierre   fecha de cierre del proyecto, o {@code null} si sigue activo
     */
    public Proyecto(int id, String titulo, LocalDate fechaCreacion, LocalDate fechaCierre) {
        this.id = id;
        this.titulo = titulo;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
        this.integrantesTemp = new LinkedList<>();
    }

    /**
     * Crea un proyecto sin ID, usado antes de persistirlo en la base de datos.
     *
     * @param titulo        nombre del proyecto
     * @param fechaCreacion fecha en que se creó el proyecto
     * @param fechaCierre   fecha de cierre del proyecto, o {@code null} si sigue activo
     */
    public Proyecto(String titulo, LocalDate fechaCreacion, LocalDate fechaCierre) {
        this.titulo = titulo;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
        this.integrantesTemp = new LinkedList<>();
    }

    /** @return el identificador único del proyecto */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador del proyecto, asignado tras la inserción en la base de datos.
     *
     * @param id el ID generado por la base de datos
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /** @return el título del proyecto */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Establece el título del proyecto.
     *
     * @param titulo el nuevo título
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /** @return la fecha de creación del proyecto */
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    /** @return la fecha de cierre del proyecto, o {@code null} si sigue activo */
    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    /**
     * Establece la fecha de cierre del proyecto.
     *
     * @param fechaCierre la fecha en que se cierra el proyecto
     */
    public void setFechaCierre(LocalDate fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    /**
     * Devuelve la lista temporal de integrantes pendientes de persistir en la base de datos.
     *
     * @return lista de {@link Integrante} en memoria
     */
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
