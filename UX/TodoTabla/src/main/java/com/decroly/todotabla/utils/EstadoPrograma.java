package com.decroly.todotabla.utils;

import com.decroly.todotabla.model.Proyecto;

public class EstadoPrograma {
    private static final EstadoPrograma INSTANCE = new EstadoPrograma();

    private Proyecto proyectoActivo;

    private EstadoPrograma() {}

    public static EstadoPrograma getInstance() {
        return INSTANCE;
    }

    public Proyecto getProyectoActivo() {
        return proyectoActivo;
    }

    public void setProyectoActivo(Proyecto proyectoActivo) {
        this.proyectoActivo = proyectoActivo;
    }
}
