package com.decroly.todotabla.utils.constants;

import java.util.HashMap;
import java.util.Map;
/**
 * Clase utilitaria y de constantes encargada de la gestión del mapa cromático del sistema.
 * <p>
 * Proporciona una paleta de colores en formato hexadecimal vinculada unívocamente a los niveles
 * de prioridad numérica de las tareas. Permite estandarizar la codificación visual
 * (desde alertas críticas rojas hasta estados ordinarios grises) a lo largo de las distintas
 * celdas personalizadas de la interfaz gráfica de usuario JavaFX.
 * </p>
 * * <p>Esta clase no está diseñada para ser instanciada ni heredada (patrón Utility Class).</p>
 *
 * @author Decroly
 * @version 1.0
 */
public class ColoresPrioridad {
    /**
     * Constructor privado para garantizar la inviabilidad de instanciación de la clase utilitaria.
     * Evita la creación accidental de objetos en memoria de forma externa u omisión de las firmas estáticas.
     */
    private ColoresPrioridad() {}
    /**
     * Diccionario inmutable indexado por nivel de prioridad (clave entera) que asocia cada índice
     * con una cadena de texto que representa el código de color hexadecimal equivalente.
     * <ul>
     * <li>0 - Rojo Crítico (#da3633)</li>
     * <li>1 - Rojo Alerta (#f85149)</li>
     * <li>2 - Naranja Importante (#f0883e)</li>
     * <li>3 - Amarillo Moderado (#e3b341)</li>
     * <li>4 - Verde Suave (#85c78a)</li>
     * <li>5 - Verde Operativo (#3fb950)</li>
     * <li>6 - Gris Informativo/Bajo (#8b949e)</li>
     * </ul>
     */
    private final static Map<Integer, String> COLORES = new HashMap<>() {{
        put(0,"#da3633");
        put(1,"#f85149");
        put(2,"#f0883e");
        put(3,"#e3b341");
        put(4,"#85c78a");
        put(5,"#3fb950");
        put(6,"#8b949e");
    }};
    /**
     * Resuelve y devuelve el código de color hexadecimal correspondiente al nivel de urgencia o prioridad provisto.
     * <p>
     * El método cuenta con mecanismos de control de desbordamiento (clamping) integrados:
     * Si la prioridad es inferior o igual a 0, se le asigna automáticamente el color de máxima criticidad (índice 0).
     * Si la prioridad supera o iguala el índice 6, se le asigna el tono de menor urgencia (índice 6).
     * </p>
     *
     * @param prioridad Valor entero representativo del orden de urgencia asignado a la tarea.
     * @return Una cadena con el código hexadecimal del color (ej: "#da3633") listo para ser procesado por el motor CSS de JavaFX.
     */
    public static String getColores(int prioridad) {
        if (prioridad <= 0) return COLORES.get(0);
        if (prioridad >= 6) return COLORES.get(6);
        return COLORES.get(prioridad);
    }
}
