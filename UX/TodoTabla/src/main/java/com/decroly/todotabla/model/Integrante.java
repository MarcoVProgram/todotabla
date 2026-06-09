package com.decroly.todotabla.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad de nexo asociativo que modela la relación de membresía (Muchos a Muchos) entre un {@link Usuario}
 * y un {@link Proyecto} específico.
 * <p>
 * Almacena atributos históricos de la relación, tales como el rol funcional desempeñado (ej. "Manager", "Developer"),
 * la fecha de incorporación al equipo y el cese temporal o definitivo de su participación.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class Integrante {

    /** Clave primaria única que identifica este registro de membresía corporativa. */
    private int id;
    /** Rol operativo, perfil o cargo técnico asignado al participante dentro del proyecto. */
    private String rol;
    /** Fecha exacta en la que el usuario fue dado de alta en las actividades del proyecto. */
    private LocalDate fechaEntrada;
    /** Fecha en la que el usuario fue desvinculado; toma valor {@code null} si es un miembro activo actualmente. */
    private LocalDate fechaSalida;
    /** Referencia al objeto {@link Usuario} que posee los datos de identidad del miembro. */
    private Usuario idUsuario;
    /** Referencia al objeto {@link Proyecto} sobre el cual se ejecuta el trabajo del equipo. */
    private Proyecto idProyecto;

    /**
     * Constructor completo de hidratación utilizado para recuperar e instanciar membresías de la base de datos.
     *
     * @param id           Clave única del registro relacional.
     * @param rol          Cargo o rol técnico ejercido.
     * @param fechaEntrada Fecha de ingreso al equipo.
     * @param fechaSalida  Fecha de egreso o desvinculación (puede ser {@code null}).
     * @param idUsuario    Instancia completa del usuario implicado.
     * @param idProyecto   Instancia completa del proyecto asociado.
     */
    public Integrante(int id, String rol, LocalDate fechaEntrada, LocalDate fechaSalida, Usuario idUsuario, Proyecto idProyecto) {
        this.id = id;
        this.rol = rol;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.idUsuario = idUsuario;
        this.idProyecto = idProyecto;
    }

    /**
     * Constructor de negocio ágil utilizado para la creación en memoria de nuevas vinculaciones de equipo,
     * abstrayendo el ID físico para la inserción limpia mediante sentencias SQL SQL-DAO.
     *
     * @param rol          Rol asignado.
     * @param fechaEntrada Fecha de alta.
     * @param fechaSalida  Fecha de baja estipulada (generalmente {@code null} de partida).
     * @param idUsuario    Usuario en proceso de vinculación.
     * @param idProyecto   Proyecto de destino.
     */
    public Integrante(String rol, LocalDate fechaEntrada, LocalDate fechaSalida, Usuario idUsuario, Proyecto idProyecto) {
        this.rol = rol;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.idUsuario = idUsuario;
        this.idProyecto = idProyecto;
    }

    /**
     * Obtiene el identificador numérico de la membresía.
     * @return ID del integrante.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el rol o cargo desempeñado por el integrante en el proyecto.
     * @return El rol en formato String.
     */
    public String getRol() {
        return rol;
    }

    /**
     * Modifica o actualiza el rol funcional asignado al miembro del equipo.
     * @param rol Nueva cadena descriptiva de cargo.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Obtiene la fecha original en la que el usuario fue incorporado al proyecto.
     * @return Objeto {@link LocalDate} con el hito temporal de ingreso.
     */
    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    /**
     * Obtiene la fecha de salida o conclusión de actividades del integrante.
     * @return La fecha de desvinculación, o {@code null} si es un participante activo.
     */
    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    /**
     * Define de manera formal la fecha en la que el integrante cesa sus funciones en el proyecto.
     * @param fechaSalida Objeto {@link LocalDate} del cese de actividades.
     */
    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    /**
     * Obtiene el objeto usuario asociado que detalla la identidad del participante.
     * @return Instancia del {@link Usuario}.
     */
    public Usuario getIdUsuario() {
        return idUsuario;
    }

    /**
     * Obtiene el objeto proyecto que delimita el campo de acción de esta membresía.
     * @return Instancia del {@link Proyecto}.
     */
    public Proyecto getIdProyecto() {
        return idProyecto;
    }

    /**
     * Permite reconfigurar el espacio de trabajo o proyecto al cual se asocia estratégicamente esta membresía.
     * @param idProyecto Instancia del nuevo {@link Proyecto} asignado.
     */
    public void setIdProyecto(Proyecto idProyecto) {
        this.idProyecto = idProyecto;
    }

    /**
     * Convierte los datos analíticos del integrante a un formato legible por consola o logger.
     * * @return Cadena formateada detallando IDs relacionales, rol y marcas temporales.
     */
    @Override
    public String toString() {
        return "Integrante{" +
                "id=" + id +
                ", rol='" + rol + '\'' +
                ", fechaEntrada=" + fechaEntrada +
                ", fechaSalida=" + fechaSalida +
                ", idMiembro=" + (idUsuario != null ? idUsuario.getId() : null) +
                ", idProyecto=" + (idProyecto != null ? idProyecto.getId() : null) +
                '}';
    }

    /**
     * Valida la igualdad semántica de la membresía. Dos instancias son consideradas idénticas si:
     * <ul>
     * <li>Comparten la misma clave primaria implícita diferente de cero.</li>
     * <li>O en su defecto, vinculan al mismo ID de usuario exactamente dentro del mismo ID de proyecto.</li>
     * </ul>
     *
     * @param o Objeto de verificación.
     * @return {@code true} si representan el mismo nexo relacional de negocio; {@code false} de lo contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Integrante that)) return false;

        if (this.id != 0 && that.id != 0) {
            return this.id == that.id;
        }

        return this.idUsuario.getId() == that.idUsuario.getId()
                && this.idProyecto != null
                && that.idProyecto != null
                && this.idProyecto.getId().equals(that.idProyecto.getId());
    }

    /**
     * Calcula la firma hash a partir de los datos atómicos de la membresía para prevenir
     * colisiones de objetos duplicados en sets tipados.
     *
     * @return Código numérico entero.
     */
    @Override
    public int hashCode() {
        if (id != 0) {
            return Objects.hash(id);
        }
        return Objects.hash(idUsuario != null ? idUsuario.getId() : 0, idProyecto != null ? idProyecto.getId() : 0);
    }
}