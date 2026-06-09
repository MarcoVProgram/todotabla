package com.decroly.todotabla.utils;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;

import java.util.LinkedList;
import java.util.List;

public class EstadoPrograma {
    private static final EstadoPrograma INSTANCE = new EstadoPrograma();

    private Proyecto proyectoActivo;
    private Tarea tareaActiva;
    private String tituloProyecto;
    private List<Integrante> integrantesTemp;
    private List<Usuario> usuariosTemp;

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

    public Tarea getTareaActiva() {
        return tareaActiva;
    }

    public void setTareaActiva(Tarea tareaActiva) {
        this.tareaActiva = tareaActiva;
    }

    public List<Usuario> getUsuariosTemp() {
        return usuariosTemp;
    }

    public void setUsuariosTemp(List<Usuario> usuariosTemp) {
        this.usuariosTemp = usuariosTemp;
    }
}
