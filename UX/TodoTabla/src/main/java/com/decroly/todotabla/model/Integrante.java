package com.decroly.todotabla.model;

import java.time.LocalDate;

public class Integrante {

    private int id;
    private String rol;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private Miembro idMiembro;
    private Proyecto idProyecto;

    public Integrante(int id, String rol, LocalDate fechaEntrada, LocalDate fechaSalida, Miembro idMiembro, Proyecto idProyecto) {
        this.id = id;
        this.rol = rol;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.idMiembro = idMiembro;
        this.idProyecto = idProyecto;
    }

    public Integrante(String rol, LocalDate fechaEntrada, LocalDate fechaSalida, Miembro idMiembro, Proyecto idProyecto) {
        this.rol = rol;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.idMiembro = idMiembro;
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

    public Miembro getIdMiembro() {
        return idMiembro;
    }

    public void setIdMiembro(Miembro idMiembro) {
        this.idMiembro = idMiembro;
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
                ", idMiembro=" + idMiembro +
                ", idProyecto=" + idProyecto +
                '}';
    }
}
