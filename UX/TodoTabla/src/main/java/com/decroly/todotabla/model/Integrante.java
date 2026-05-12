package com.decroly.todotabla.model;

import java.time.LocalDate;

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

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Usuario getIdMiembro() {
        return idUsuario;
    }

    public void setIdMiembro(Usuario idUsuario) {
        this.idUsuario = idUsuario;
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
                ", idMiembro=" + idUsuario.getId() +
                ", idProyecto=" + idProyecto.getId() +
                '}';
    }
}
