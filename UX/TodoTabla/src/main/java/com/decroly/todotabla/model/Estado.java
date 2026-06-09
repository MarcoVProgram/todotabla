package com.decroly.todotabla.model;

import java.util.Objects;

/**
 * Representa una columna, etapa o fase lógica dentro del flujo de trabajo Kanban (ej. "To Do", "In Progress", "Done").
 * <p>
 * Define las propiedades de visualización y jerarquía ordinal indispensables para renderizar de manera coherente
 * las tarjetas de las tareas en la interfaz gráfica.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class Estado {
    /** Nombre descriptor exclusivo de la columna de estado, actúa como clave de negocio única. */
    private String nombre;
    /** Representación cromática en formato web (Hexadecimal/RGBA) para la personalización de la interfaz. */
    private String color;
    /** Índice numérico que determina el orden posicional de izquierda a derecha en el tablero. */
    private int orden;

    /**
     * Constructor parametrizado para la inicialización completa de un estado del ciclo de vida Kanban.
     *
     * @param nombre Nombre descriptivo del estado.
     * @param color  Código de color visual asociado.
     * @param orden  Posición ordinal en el eje horizontal del tablero.
     */
    public Estado(String nombre, String color, int orden) {
        this.nombre = nombre;
        this.color = color;
        this.orden = orden;
    }

    /**
     * Obtiene el nombre del estado.
     * @return El nombre identificador.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene la codificación de color del estado.
     * @return Cadena con el color en formato gráfico.
     */
    public String getColor() {
        return color;
    }

    /**
     * Define o modifica el color visual del estado.
     * @param color Nuevo valor cromático.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Obtiene el orden de colocación horizontal en el tablero Kanban.
     * @return Índice de ordenación entero.
     */
    public int getOrden() {
        return orden;
    }

    /**
     * Modifica el índice posicional del estado.
     * @param orden Nuevo orden del flujo.
     */
    public void setOrden(int orden) {
        this.orden = orden;
    }

    /**
     * Genera la representación textual detallada del estado Kanban.
     * * @return Cadena que describe el nombre, color y orden del objeto.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Estado{");
        sb.append("nombre='").append(nombre).append('\'');
        sb.append(", color='").append(color).append('\'');
        sb.append(", orden=").append(orden);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Evalúa la igualdad entre estados. Dos estados se definen como idénticos si sus nombres coinciden textualmente,
     * garantizando un control estricto de las fases de negocio.
     *
     * @param o Objeto evaluado.
     * @return {@code true} si los nombres lógicos coinciden; {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estado estado = (Estado) o;
        return Objects.equals(nombre, estado.nombre);
    }

    /**
     * Calcula la firma hash de la instancia en función de su nombre exclusivo.
     * * @return Código numérico entero.
     */
    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}