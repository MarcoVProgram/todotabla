package com.decroly.todotabla.model;

import java.time.LocalDate;
import java.util.Objects;

/*
 * Representa la participación de un {@link Usuario} en un {@link Proyecto} bajo un rol concreto.
 * Registra el período de pertenencia del usuario al proyecto.
 */
public class Integrante {

    private int id;
    private String rol;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private Usuario idUsuario;
    private Proyecto idProyecto;

    /**
     * Crea un integrante con ID conocido, usado al recuperar datos de la base de datos.
     *
     * @param id           identificador único del integrante
     * @param rol          rol que desempeña el usuario en el proyecto
     * @param fechaEntrada fecha en que el usuario se incorporó al proyecto
     * @param fechaSalida  fecha en que el usuario abandonó el proyecto, o {@code null} si sigue activo
     * @param idUsuario    usuario asociado
     * @param idProyecto   proyecto al que pertenece
     */
    public Integrante(int id, String rol, LocalDate fechaEntrada, LocalDate fechaSalida, Usuario idUsuario, Proyecto idProyecto) {
        this.id = id;
        this.rol = rol;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.idUsuario = idUsuario;
        this.idProyecto = idProyecto;
    }

    /**
     * Crea un integrante sin ID, usado antes de persistirlo en la base de datos.
     *
     * @param rol          rol que desempeña el usuario en el proyecto
     * @param fechaEntrada fecha en que el usuario se incorporó al proyecto
     * @param fechaSalida  fecha en que el usuario abandonó el proyecto, o {@code null} si sigue activo
     * @param idUsuario    usuario asociado
     * @param idProyecto   proyecto al que pertenece
     */
    public Integrante(String rol, LocalDate fechaEntrada, LocalDate fechaSalida, Usuario idUsuario, Proyecto idProyecto) {
        this.rol = rol;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.idUsuario = idUsuario;
        this.idProyecto = idProyecto;
    }

    /** @return el identificador único del integrante */
    public int getId() {
        return id;
    }

    /** @return el rol del usuario en el proyecto */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol del usuario en el proyecto.
     *
     * @param rol el nuevo rol
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /** @return la fecha de incorporación al proyecto */
    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    /** @return la fecha de salida del proyecto, o {@code null} si sigue activo */
    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    /**
     * Establece la fecha de salida del proyecto.
     *
     * @param fechaSalida la fecha en que el usuario abandona el proyecto
     */
    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    /** @return el usuario asociado a este integrante */
    public Usuario getIdUsuario() {
        return idUsuario;
    }

    /** @return el proyecto al que pertenece este integrante */
    public Proyecto getIdProyecto() {
        return idProyecto;
    }

    /**
     * Establece el proyecto al que pertenece este integrante.
     *
     * @param idProyecto el nuevo proyecto
     */
    public void setIdProyecto(Proyecto idProyecto) {
        this.idProyecto = idProyecto;
    }

    @Override
    public String toString() {
        return "Integrante{" +
                "id=" + id +
                ", rol='" + rol + '\'' +
                ", fechaEntrada=" + fechaEntrada +
                ", fechaSalida=" + fechaSalida +
                ", idMiembro=" + (idUsuario != null ? idUsuario.getId() : null) +
                ", idProyecto=" + (idProyecto != null ? idProyecto.getId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Integrante that)) return false;

        if (this.id != 0 && that.id != 0) {
            return this.id == that.id;
        }

        return this.idUsuario.getId() == that.idUsuario.getId()
                && this.idProyecto != null
                && that.idProyecto != null
                && this.idProyecto.getId().equals(that.idProyecto.getId());
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Objects.hash(id);
        }

        return Objects.hash(
                this.idUsuario != null ? idUsuario.getId() : 0,
                this.idProyecto != null ? idProyecto.getId() : 0
        );
    }
}
