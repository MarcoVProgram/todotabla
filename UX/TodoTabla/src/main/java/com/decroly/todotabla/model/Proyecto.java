package com.decroly.todotabla.model;

import java.time.LocalDate;
import java.util.*;

/**
 * Entidad principal que delimita e identifica un espacio de trabajo o proyecto empresarial.
 * <p>
 * Actúa como contenedor de tareas globales e integrantes asociados, y realiza la trazabilidad temporal
 * desde su apertura hasta la fecha formal de clausura o archivo.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class Proyecto {

    /** Código de identificación secuencial asignado en base de datos. */
    private Integer id;
    /** Título descriptivo o nombre corporativo del proyecto. */
    private String titulo;
    /** Fecha en la que el proyecto fue registrado y dado de alta en la plataforma. */
    private LocalDate fechaCreacion;
    /** Fecha de cierre de operaciones, toma un valor {@code null} mientras el proyecto permanezca activo. */
    private LocalDate fechaCierre;
    /** Lista de almacenamiento temporal usada por la interfaz gráfica para gestionar los participantes en memoria. */
    private List<Integrante> integrantesTemp;

    /**
     * Constructor completo empleado para hidratar instancias de proyectos desde registros existentes.
     *
     * @param id            ID único del proyecto.
     * @param titulo        Título o denominación del proyecto.
     * @param fechaCreacion Fecha de inicio de actividades.
     * @param fechaCierre   Fecha de finalización o suspensión (puede ser {@code null}).
     */
    public Proyecto(int id, String titulo, LocalDate fechaCreacion, LocalDate fechaCierre) {
        this.id = id;
        this.titulo = titulo;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
        this.integrantesTemp = new LinkedList<>();
    }

    /**
     * Constructor de negocio diseñado para la pre-inicialización de proyectos en la interfaz de usuario,
     * abstrayendo el ID para su posterior persistencia física.
     *
     * @param titulo        Título del proyecto.
     * @param fechaCreacion Fecha de apertura.
     * @param fechaCierre   Fecha de clausura programada o efectiva.
     */
    public Proyecto(String titulo, LocalDate fechaCreacion, LocalDate fechaCierre) {
        this.titulo = titulo;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
        this.integrantesTemp = new LinkedList<>();
    }

    /**
     * Obtiene el identificador numérico de la entidad.
     * @return ID del proyecto o {@code null} si aún no ha sido persistido.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece de forma explícita el identificador primario del proyecto.
     * @param id Nuevo ID numérico.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene el título del proyecto.
     * @return El título en formato String.
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Modifica el título asignado al espacio de trabajo.
     * @param titulo Cadena de texto descriptiva.
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene la fecha original de creación del proyecto.
     * @return Objeto {@link LocalDate} con el registro cronológico.
     */
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Obtiene la fecha en la que se dio por cerrado el proyecto.
     * @return La fecha de conclusión, o {@code null} si el proyecto continúa abierto.
     */
    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    /**
     * Define la fecha oficial de finalización o congelación de actividades del proyecto.
     * @param fechaCierre Objeto {@link LocalDate} representativo del término.
     */
    public void setFechaCierre(LocalDate fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    /**
     * Proporciona acceso a la estructura interna de integrantes almacenados temporalmente en memoria.
     * @return Una {@link List} mutable con las membresías en proceso de edición.
     */
    public List<Integrante> getIntegrantesTemp() {
        return integrantesTemp;
    }

    /**
     * Genera la traza textual estructurada con las propiedades base del proyecto.
     * * @return Cadena formateada para depuración técnica.
     */
    @Override
    public String toString() {
        return "Proyecto{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaFin=" + fechaCierre +
                '}';
    }

    /**
     * Compara lógicamente dos proyectos. Se consideran idénticos si coinciden en su ID único de base de datos.
     *
     * @param o Instancia externa a comparar.
     * @return {@code true} si los proyectos son equivalentes por ID; {@code false} de lo contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proyecto proyecto = (Proyecto) o;
        return Objects.equals(id, proyecto.id);
    }

    /**
     * Calcula el código hash estructural de la clase fundamentado en su identificador único.
     * * @return Entero hash de indexación.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}