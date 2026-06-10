package com.decroly.todotabla.utils;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;

import java.util.LinkedList;
import java.util.List;

/**
 * Singleton que mantiene el estado global de la sesión activa de la aplicación.
 * Almacena el proyecto y la tarea actualmente seleccionados, así como listas temporales
 * de integrantes y usuarios pendientes de persistir.
 */
public class EstadoPrograma {
    private static final EstadoPrograma INSTANCE = new EstadoPrograma();

    private Proyecto proyectoActivo;
    private Tarea tareaActiva;
    private List<Integrante> integrantesTemp;
    private List<Usuario> usuariosTemp;

    /**
     * Devuelve la única instancia de {@code EstadoPrograma}.
     *
     * @return la instancia singleton
     */
    public static EstadoPrograma getInstance() {
        return INSTANCE;
    }

    private EstadoPrograma() {
        integrantesTemp = new LinkedList<>();
    }


    /** @return el proyecto actualmente activo en la sesión */
    public Proyecto getProyectoActivo() {
        return proyectoActivo;
    }

    /**
     * Establece el proyecto activo de la sesión.
     *
     * @param proyectoActivo el nuevo proyecto activo
     */
    public void setProyectoActivo(Proyecto proyectoActivo) {
        this.proyectoActivo = proyectoActivo;
    }

    /** @return la tarea actualmente seleccionada en la sesión */
    public Tarea getTareaActiva() {
        return tareaActiva;
    }

    /**
     * Establece la tarea activa de la sesión.
     *
     * @param tareaActiva la nueva tarea activa
     */
    public void setTareaActiva(Tarea tareaActiva) {
        this.tareaActiva = tareaActiva;
    }

    /** @return la lista temporal de integrantes pendientes de persistir */
    public List<Integrante> getIntegrantesTemp() {
        return integrantesTemp;
    }

    /**
     * Reemplaza la lista temporal de integrantes.
     *
     * @param integrantesTemp la nueva lista de integrantes temporales
     */
    public void setIntegrantesTemp(List<Integrante> integrantesTemp) {
        this.integrantesTemp = integrantesTemp;
    }

    /** @return la lista temporal de usuarios pendientes de persistir */
    public List<Usuario> getUsuariosTemp() {
        return usuariosTemp;
    }

    /**
     * Reemplaza la lista temporal de usuarios.
     *
     * @param usuariosTemp la nueva lista de usuarios temporales
     */
    public void setUsuariosTemp(List<Usuario> usuariosTemp) {
        this.usuariosTemp = usuariosTemp;
    }
}
