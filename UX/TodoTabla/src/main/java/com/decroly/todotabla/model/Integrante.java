package com.decroly.todotabla.model;

import java.time.LocalDate;
import java.util.Objects;

public class Integrante {

    private int id;
    private String rol;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private Usuario idUsuario;
    private Proyecto idProyecto;

    public Integrante(int id, String rol, LocalDate fechaEntrada, LocalDate fechaSalida, Usuario idUsuario, Proyecto idProyecto) {
        this.id = id;
        this.rol = rol;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.idUsuario = idUsuario;
        this.idProyecto = idProyecto;
    }

    public Integrante(String rol, LocalDate fechaEntrada, LocalDate fechaSalida, Usuario idUsuario, Proyecto idProyecto) {
        this.rol = rol;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.idUsuario = idUsuario;
        this.idProyecto = idProyecto;
    }

    public int getId() {
        return id;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public Proyecto getIdProyecto() {
        return idProyecto;
    }

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
