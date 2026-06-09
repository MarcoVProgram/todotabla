package com.decroly.todotabla.model;

import java.util.Objects;
/**
 * Entidad de dominio que representa a un operador, miembro de equipo o usuario dentro del sistema.
 * <p>
 * Almacena la información de perfil básica necesaria para la identificación, asignación de tareas
 * y participación en los flujos de trabajo de los proyectos.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class Usuario {
    /** Identificador único auto-incremental asignado por el motor de persistencia relacional. */
    private int id;
    /** Nombre de pila del usuario. */
    private String nombre;
    /** Apellidos oficiales del usuario. */
    private String apellidos;
    /** Dirección de correo electrónico única utilizada para comunicaciones e identificación interna. */
    private String email;

    /**
     * Constructor completo empleado por la capa de persistencia para reconstruir (hidratar)
     * una instancia de usuario existente desde la base de datos.
     *
     * @param id        El identificador primario del registro.
     * @param nombre    Nombre del usuario.
     * @param apellidos Apellidos del usuario.
     * @param email     Correo electrónico institucional o personal del usuario.
     */
    public Usuario(int id, String nombre, String apellidos, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
    }

    /**
     * Constructor de negocio optimizado para la creación de nuevas instancias en memoria
     * antes de ser persistidas, omitiendo el ID al delegar su generación en la base de datos.
     *
     * @param nombre    Nombre del usuario.
     * @param apellidos Apellidos del usuario.
     * @param email     Correo electrónico del usuario.
     */
    public Usuario(String nombre, String apellidos, String email) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
    }
    /**
     * Obtiene el identificador único del usuario.
     * @return El ID numérico entero.
     */
    public int getId() {
        return id;
    }
    /**
     * Obtiene el nombre del usuario.
     * @return El nombre en formato String.
     */
    public String getNombre() {
        return nombre;
    }
    /**
     * Define o modifica el nombre del usuario.
     * @param nombre El nuevo nombre asignado.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
/**
 * Obtiene los apellidos del usuario.
 * @return Los apellidos en formato String.*/
    public String getApellidos() {
        return apellidos;
    }
    /**
     * Define o modifica los apellidos del usuario.
     * @param apellidos Los nuevos apellidos asignados.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    /**
     * Obtiene el correo electrónico del usuario.
     * @return El email en formato String.
     */
    public String getEmail() {
        return email;
    }
    /**
     * Define o modifica el correo electrónico del usuario.
     * @param email El nuevo email a asignar.
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Genera una representación textual formateada de la entidad.
     * * @return Cadena de caracteres descriptiva con el estado interno del objeto.
     */
    @Override
    public String toString() {
        return "Miembro{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * Compara la igualdad lógica de esta instancia con otro objeto.
     * Dos usuarios se consideran idénticos si comparten el mismo identificador único o el mismo email.
     *
     * @param o Objeto de comparación.
     * @return {@code true} si los objetos son lógicamente equivalentes; {@code false} de lo contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id;
    }

    /**
     * Calcula el código hash de la instancia basado en el ID y el correo electrónico.
     * Garantiza la coherencia operacional al trabajar con colecciones estructuradas como {@link java.util.HashSet}.
     *
     * @return El valor entero hash correlativo.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
