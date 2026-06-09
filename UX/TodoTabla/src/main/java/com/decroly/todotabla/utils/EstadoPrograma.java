package com.decroly.todotabla.utils;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;

import java.util.LinkedList;
import java.util.List;

/**
 * Registro central de estado o contexto global de la aplicación fundamentado bajo el patrón de diseño Singleton.
 * <p>
 * Actúa como memoria volátil persistente entre transferencias de escenas o ventanas del ciclo FX. Guarda las referencias
 * unívocas de las entidades seleccionadas (proyecto activo, tarea bajo edición) y buffers temporales de usuarios
 * o integrantes involucrados en transacciones pendientes de consolidación.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class EstadoPrograma {

    /** Instancia única precargada (Eager Initialization) accesible de forma estática global. */
    private static final EstadoPrograma INSTANCE = new EstadoPrograma();

    /** Entidad del proyecto seleccionado en el panel principal que rige el alcance de las consultas actuales. */
    private Proyecto proyectoActivo;
    /** Entidad de la tarea que se encuentra bajo el foco de interacción o modificación analítica. */
    private Tarea tareaActiva;
    /** Título textual representativo del proyecto en memoria (Buffer intermedio). */
    private String tituloProyecto;
    /** Listado provisional de objetos {@link Integrante} asignados temporalmente a un formulario de alta o edición. */
    private List<Integrante> integrantesTemp;
    /** Listado provisional de objetos {@link Usuario} acumulados para operaciones masivas. */
    private List<Usuario> usuariosTemp;

    /**
     * Constructor de visibilidad privada absoluta para mitigar el riesgo de dobles instanciaciones externas.
     * Inicializa las colecciones dinámicas internas para eludir excepciones de tipo {@link NullPointerException}.
     */
    private EstadoPrograma() {
        this.integrantesTemp = new LinkedList<>();
        this.usuariosTemp = new LinkedList<>(); // Asegura la consistencia del buffer
    }

    /**
     * Proporciona acceso controlado a la instancia única operativa del almacén de estado.
     *
     * @return El punto de acceso global {@link EstadoPrograma} de la sesión actual del hilo.
     */
    public static EstadoPrograma getInstance() {
        return INSTANCE;
    }

    /**
     * Obtiene el título nominal del proyecto temporal que se está procesando.
     *
     * @return Cadena descriptiva del título asignado.
     */
    public String getTituloProyecto() {
        return tituloProyecto;
    }

    /**
     * Sobrescribe el valor nominal del título del proyecto actual en el buffer intermedio.
     *
     * @param tituloProyecto Nueva cadena de identificación de título.
     */
    public void setTituloProyecto(String tituloProyecto) {
        this.tituloProyecto = tituloProyecto;
    }

    /**
     * Devuelve el objeto del proyecto de desarrollo que se encuentra con foco prioritario.
     *
     * @return Instancia del POJO {@link Proyecto} en memoria.
     */
    public Proyecto getProyectoActivo() {
        return proyectoActivo;
    }

    /**
     * Inyecta o cambia de forma unívoca el proyecto activo de la sesión del programa.
     *
     * @param proyectoActivo Instancia {@link Proyecto} objetivo del cambio.
     */
    public void setProyectoActivo(Proyecto proyectoActivo) {
        this.proyectoActivo = proyectoActivo;
    }

    /**
     * Recupera el búfer o colección dinámica de integrantes vinculados provisionalmente a una vista.
     *
     * @return Una {@link List} estructurada de objetos {@link Integrante}.
     */
    public List<Integrante> getIntegrantesTemp() {
        return integrantesTemp;
    }

    /**
     * Reemplaza el listado entero de miembros provisionales del estado transitorio.
     *
     * @param integrantesTemp Nueva colección estructurada de integrantes.
     */
    public void setIntegrantesTemp(List<Integrante> integrantesTemp) {
        this.integrantesTemp = integrantesTemp;
    }

    /**
     * Devuelve la tarea seleccionada actualmente en el ecosistema de tableros.
     *
     * @return Instancia activa de la entidad {@link Tarea}.
     */
    public Tarea getTareaActiva() {
        return tareaActiva;
    }

    /**
     * Establece la tarea que se encuentra bajo la edición o inspección del usuario.
     *
     * @param tareaActiva Instancia {@link Tarea} objetivo.
     */
    public void setTareaActiva(Tarea tareaActiva) {
        this.tareaActiva = tareaActiva;
    }

    /**
     * Obtiene el búfer complementario de entidades de usuario registradas en el estado actual.
     *
     * @return Lista dinámica de objetos de tipo {@link Usuario}.
     */
    public List<Usuario> getUsuariosTemp() {
        return usuariosTemp;
    }

    /**
     * Modifica el búfer temporal de usuarios asociados de la sesión de ventanas.
     *
     * @param usuariosTemp Nueva lista estructurada de objetos {@link Usuario}.
     */
    public void setUsuariosTemp(List<Usuario> usuariosTemp) {
        this.usuariosTemp = usuariosTemp;
    }
}