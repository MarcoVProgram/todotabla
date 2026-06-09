package com.decroly.todotabla.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad de auditoría encargada de congelar e inmortalizar las transiciones de estados ocurridas en la plataforma.
 * <p>
 * Cada vez que una tarjeta se arrastra o conmuta a una nueva columna, esta clase registra de forma inalterable
 * el nuevo {@link Estado} alcanzado por la {@link Tarea} junto con una marca de tiempo de precisión milimétrica (Timestamp).
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class HistorialTareas {

    /** Identificador correlativo del evento de auditoría interna de la aplicación. */
    private int id;
    /** Estado o columna Kanban de destino al que fue movida la tarea. */
    private Estado estado;
    /** Referencia de la {@link Tarea} que sufrió el cambio operacional. */
    private Tarea idTarea;
    /** Fecha y hora exacta (con resolución de segundos) en la que se ejecutó la transición de estado. */
    private LocalDateTime fechaCambio;

    /**
     * Constructor completo empleado para la generación de líneas de tiempo históricas de auditoría desde la base de datos.
     *
     * @param id          Identificador único de la traza de auditoría.
     * @param estado      Estado al que transmutó la tarea.
     * @param idTarea     La tarea afectada.
     * @param fechaCambio Instancia temporal de alta precisión con el momento exacto del cambio.
     */
    public HistorialTareas(int id, Estado estado, Tarea idTarea, LocalDateTime fechaCambio) {
        this.id = id;
        this.estado = estado;
        this.idTarea = idTarea;
        this.fechaCambio = fechaCambio;
    }

    /**
     * Constructor transaccional ágil empleado para disparar y estampar nuevos eventos log desde el controlador Kanban,
     * delegando la clave del log al generador secuencial del motor SQL.
     *
     * @param estado      Nuevo estado alcanzado.
     * @param idTarea     Tarea modificada.
     * @param fechaCambio Timestamp del movimiento de la tarjeta.
     */
    public HistorialTareas(Estado estado, Tarea idTarea, LocalDateTime fechaCambio) {
        this.estado = estado;
        this.idTarea = idTarea;
        this.fechaCambio = fechaCambio;
    }

    /**
     * Obtiene el código identificador de la entrada del historial.
     * @return ID numérico entero.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el estado Kanban consolidado en este evento histórico.
     * @return El objeto {@link Estado} de auditoría.
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Obtiene la tarea asociada sobre la cual se ejerció la acción de cambio.
     * @return Objeto {@link Tarea} auditado.
     */
    public Tarea getIdTarea() {
        return idTarea;
    }

    /**
     * Obtiene el momento cronológico de alta precisión en el que se gatilló el evento.
     * @return Objeto {@link LocalDateTime}.
     */
    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    /**
     * Desglosa de forma descriptiva los datos atómicos del evento de auditoría.
     * * @return Cadena de caracteres óptima para volcados de logs de consola.
     */
    @Override
    public String toString() {
        return "HistorialTareas{" +
                "id=" + id +
                ", estado=" + estado +
                ", idTarea=" + (idTarea != null ? idTarea.getId() : "NULL") +
                ", fechaCambio=" + fechaCambio +
                '}';
    }

    /**
     * Evalúa la igualdad física del log de auditoría. Dos entradas se consideran idénticas si coinciden
     * de manera exacta en su número de ID correlativo único en la base de datos.
     *
     * @param o Instancia externa evaluada.
     * @return {@code true} si las entradas de log coinciden; {@code false} de lo contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistorialTareas that = (HistorialTareas) o;
        return id == that.id;
    }

    /**
     * Devuelve el código hash numérico calculado a partir de la identidad lógica del evento de traza.
     * * @return Firma hash entera para indexaciones.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}