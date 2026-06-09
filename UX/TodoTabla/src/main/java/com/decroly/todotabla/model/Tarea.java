package com.decroly.todotabla.model;

import java.util.Objects;

/**
 * Entidad medular que representa un requerimiento operativo, entregable o tarea ejecutable dentro del tablero Kanban.
 * <p>
 * Vincula lógicamente un flujo de prioridad numérica con un {@link Estado} del tablero y se adscribe jerárquicamente
 * a un {@link Proyecto} padre obligatorio.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class Tarea {

    /** Clave única auto-incremental del registro operacional de la tarea. */
    private int id;
    /** Título, nombre o acción concreta a realizar en la tarea. */
    private String nombre;
    /** Nivel o peso de ordenación jerárquica (priorización) de la tarjeta en el tablero Kanban. */
    private int prioridad;
    /** Etapa actual del ciclo Kanban en la que reside el entregable. */
    private Estado estado;
    /** Instancia del proyecto contenedor al cual pertenece de manera unívoca la tarea. */
    private Proyecto idProyecto;

    /**
     * Constructor completo empleado por la capa de datos para reconstruir e hidratar objetos de tareas existentes.
     *
     * @param id         Identificador único de la tarea.
     * @param nombre     Nombre identificador de la actividad.
     * @param prioridad  Peso ordinal de prioridad.
     * @param estado     Fase del flujo Kanban en la que se ubica.
     * @param idProyecto Referencia al objeto proyecto organizador.
     */
    public Tarea(int id, String nombre, int prioridad, Estado estado, Proyecto idProyecto) {
        this.id = id;
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.estado = estado;
        this.idProyecto = idProyecto;
    }

    /**
     * Constructor optimizado para la creación en caliente de tareas dentro de los controladores de interfaz,
     * delegando la asignación de claves primarias al motor relacional de datos.
     *
     * @param nombre     Nombre descriptivo de la actividad.
     * @param prioridad  Índice de prioridad.
     * @param estado     Estado Kanban inicial asignado.
     * @param idProyecto Instancia del proyecto contenedor.
     */
    public Tarea(String nombre, int prioridad, Estado estado, Proyecto idProyecto) {
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.estado = estado;
        this.idProyecto = idProyecto;
    }

    /**
     * Obtiene el identificador único de la tarea.
     * @return El ID numérico entero.
     */
    public int getId() {
        return id;
    }

    /**
     * Define de forma explícita el identificador primario de la tarea.
     * @param id Nuevo ID numérico asignado.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre o enunciado de la tarea.
     * @return El nombre textual.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Define o actualiza el nombre descriptivo de la tarea.
     * @param nombre Cadena de texto limpia con el nuevo enunciado.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la prioridad o valor ordinal de posicionamiento vertical en las listas.
     * @return El índice entero de importancia.
     */
    public int getPrioridad() {
        return prioridad;
    }

    /**
     * Modifica el índice jerárquico de prioridad de la tarea.
     * @param prioridad Nuevo peso numérico.
     */
    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    /**
     * Obtiene la fase Kanban actual en la que está clasificada la tarea.
     * @return Instancia completa del {@link Estado} asociado.
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Modifica el estado o columna Kanban de la tarea, gatillando los flujos de transición visual.
     * @param estado Nueva columna o fase lógica de destino.
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la instancia del proyecto principal al cual pertenece de manera rígida esta tarea.
     * @return Objeto {@link Proyecto} padre.
     */
    public Proyecto getIdProyecto() {
        return idProyecto;
    }

    /**
     * Construye un volcado de datos textual con los atributos vitales del entregable.
     * * @return Cadena descriptiva formateada para trazas de registro.
     */
    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", prioridad=" + prioridad +
                ", estado=" + estado +
                ", idProyecto=" + (idProyecto != null ? idProyecto.getId() : "NULL") +
                '}';
    }

    /**
     * Determina la igualdad entre tareas de forma estricta. Dos tareas se consideran el mismo elemento
     * si y solo si poseen exactamente la misma clave primaria relacional.
     *
     * @param o Objeto de contraste.
     * @return {@code true} si las identidades físicas coinciden; {@code false} de lo contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        return id == tarea.id;
    }

    /**
     * Genera la codificación hash basada en el identificador secuencial interno de la tarea.
     * * @return Firma hash única para indexaciones en memoria.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}