package com.decroly.todotabla.utils;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Proyecto;

import java.util.LinkedList;
import java.util.List;

public class EstadoPrograma {
    private static final EstadoPrograma INSTANCE = new EstadoPrograma();

    private Proyecto proyectoActivo;

    private String tituloProyecto;

    private List<Integrante> integrantesTemp;

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public void setTituloProyecto(String tituloProyecto) {
        this.tituloProyecto = tituloProyecto;
    }

    private EstadoPrograma() {
        integrantesTemp = new LinkedList<>();
    }

    public static EstadoPrograma getInstance() {
        return INSTANCE;
    }

    public Proyecto getProyectoActivo() {
        return proyectoActivo;
    }

    public List<Integrante> getIntegrantesTemp() {
        return integrantesTemp;
    }

    public void setIntegrantesTemp(List<Integrante> integrantesTemp) {
        this.integrantesTemp = integrantesTemp;
    }

    public void setProyectoActivo(Proyecto proyectoActivo) {
        this.proyectoActivo = proyectoActivo;
    }
}
