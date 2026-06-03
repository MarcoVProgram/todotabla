package com.decroly.todotabla.utils;

import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;

public class EstadoPrograma {
    private static final EstadoPrograma INSTANCE = new EstadoPrograma();

    private Proyecto proyectoActivo;
    private Tarea tareaActiva;

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

    public Tarea getTareaActiva() {
        return tareaActiva;
    }

    public void setTareaActiva(Tarea tareaActiva) {
        this.tareaActiva = tareaActiva;
    }
}
