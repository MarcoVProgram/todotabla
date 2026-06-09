package com.decroly.todotabla.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad relacional encargada de modelar la asignación operativa de un {@link Usuario} a una {@link Tarea} concreta.
 * <p>
 * Gestiona el ciclo de vida del esfuerzo de trabajo directo sobre una tarea, registrando cuándo
 * asume el operador la responsabilidad de la tarjeta y cuándo concluye su intervención.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class Asignacion {

    /** Identificador de registro único para la asignación de esfuerzo laboral. */
    private int id;
    /** Referencia al {@link Usuario} encargado de ejecutar la actividad. */
    private Usuario idUsuario;
    /** Referencia a la {@link Tarea} operativa asignada al trabajador. */
    private Tarea idTarea;
    /** Fecha en la que el usuario toma propiedad e inicia la ejecución de la tarea. */
    private LocalDate fechaAsignacion;
    /** Fecha en la que el usuario finaliza o delega la ejecución; permanece en {@code null} mientras esté en curso. */
    private LocalDate fechaFin;

    /**
     * Constructor completo de hidratación utilizado para recuperar históricos de asignación de tareas desde la base de datos.
     *
     * @param id              Identificador primario de la asignación.
     * @param idUsuario       El operador responsable.
     * @param idTarea         La tarea asignada.
     * @param fechaAsignacion Hito de inicio de actividades.
     * @param fechaFin        Hito de conclusión de actividades (puede tomar valor {@code null}).
     */
    public Asignacion(int id, Usuario idUsuario, Tarea idTarea, LocalDate fechaAsignacion, LocalDate fechaFin) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idTarea = idTarea;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFin = fechaFin;
    }

    /**
     * Constructor de negocio ágil utilizado para despachar y asignar nuevas tareas en caliente a un operador,
     * omitiendo la clave primaria para su posterior indexación física en el servidor.
     *
     * @param idUsuario       El operador asignado.
     * @param idTarea         La tarea a ejecutar.
     * @param fechaAsignacion Fecha de inicio de la responsabilidad.
     * @param fechaFin        Fecha de cierre programado o efectivo.
     */
    public Asignacion(Usuario idUsuario, Tarea idTarea, LocalDate fechaAsignacion, LocalDate fechaFin) {
        this.idUsuario = idUsuario;
        this.idTarea = idTarea;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFin = fechaFin;
    }

    /**
     * Obtiene el código primario de la asignación.
     * @return ID numérico de la fila.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene la instancia del usuario operador responsable de la tarea.
     * @return Objeto {@link Usuario}.
     */
    public Usuario getIdUsuario() {
        return idUsuario;
    }

    /**
     * Obtiene la instancia de la tarea vinculada al operador.
     * @return Objeto {@link Tarea}.
     */
    public Tarea getIdTarea() {
        return idTarea;
    }

    /**
     * Obtiene la fecha exacta de inicio del encargo de la tarea.
     * @return Objeto {@link LocalDate}.
     */
    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * Obtiene la fecha de finalización o liberación de la tarea.
     * @return La fecha registrada, o {@code null} si el operador aún se encuentra trabajando en ella.
     */
    public LocalDate getFechaFin() {
        return fechaFin;
    }

    /**
     * Establece formalmente la fecha de conclusión de las responsabilidades sobre la tarea.
     * @param fechaFin Instancia de tiempo de término.
     */
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Formatea el estado analítico del objeto en una cadena legible descriptiva.
     * * @return Texto estructurado listo para auditorías en tiempo de ejecución.
     */
    @Override
    public String toString() {
        return "Asignacion{" +
                "id=" + id +
                ", idMiembro=" + (idUsuario != null ? idUsuario.getId() : "NULL") +
                ", idTarea=" + (idTarea != null ? idTarea.getId() : "NULL") +
                ", fechaAsignacion=" + fechaAsignacion +
                ", fechaFin=" + fechaFin +
                '}';
    }

    /**
     * Compara lógicamente dos asignaciones. Se declaran idénticas si coinciden en su ID único relacional.
     *
     * @param o Objeto de evaluación.
     * @return {@code true} si las identidades físicas son equivalentes; {@code false} de lo contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asignacion that = (Asignacion) o;
        return id == that.id;
    }

    /**
     * Genera la codificación hash aritmética única basada en la clave relacional interna de la asignación.
     * * @return Valor hash entero de indexación estructurada.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}