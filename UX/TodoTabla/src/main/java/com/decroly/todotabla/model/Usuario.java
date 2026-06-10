package com.decroly.todotabla.model;

import java.util.Objects;

/**
 * Representa un usuario registrado en el sistema.
 * Un usuario puede participar en proyectos como {@link Integrante} y ser asignado a tareas.
 */
public class Usuario {

    private int id;
    private String nombre;
    private String apellidos;
    private String email;

    /**
     * Crea un usuario con ID conocido, usado al recuperar datos de la base de datos.
     *
     * @param id        identificador único del usuario
     * @param nombre    nombre del usuario
     * @param apellidos apellidos del usuario
     * @param email     correo electrónico del usuario
     */
    public Usuario(int id, String nombre, String apellidos, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
    }

    /**
     * Crea un usuario sin ID, usado antes de persistirlo en la base de datos.
     *
     * @param nombre    nombre del usuario
     * @param apellidos apellidos del usuario
     * @param email     correo electrónico del usuario
     */
    public Usuario(String nombre, String apellidos, String email) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
    }

    /** @return el identificador único del usuario */
    public int getId() {
        return id;
    }

    /** @return el nombre del usuario */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     *
     * @param nombre el nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** @return los apellidos del usuario */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del usuario.
     *
     * @param apellidos los nuevos apellidos
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /** @return el correo electrónico del usuario */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email el nuevo correo electrónico
     */
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Miembro{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
