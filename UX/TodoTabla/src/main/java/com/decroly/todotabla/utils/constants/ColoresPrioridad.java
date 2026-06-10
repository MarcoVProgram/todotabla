package com.decroly.todotabla.utils.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase de utilidad que asocia niveles de prioridad a colores para el avatar del usuario asignado a una tarea.
 * No representa la prioridad de la tarea en sí, sino un color de identificación visual del perfil
 * del usuario mostrado en la tarjeta.
 * No se puede instanciar.
 */
public class ColoresPrioridad {

    /**
     * Constructor privado para evitar la instanciación de esta clase de utilidad.
     */
    private ColoresPrioridad() {}

    private final static Map<Integer, String> COLORES = new HashMap<>() {{
        put(0, "#1f6feb");
        put(1, "#8957e5");
        put(2, "#bc4c00");
        put(3, "#1a7f37");
        put(4, "#cf222e");
        put(5, "#0969da");
        put(6, "#6e40c9");
        put(7, "#8b949e");
    }};

    /**
     * Devuelve el color hexadecimal asociado al nivel de prioridad indicado.
     * Los valores fuera del rango {@code [0, 7]} se ajustan al extremo más cercano.
     *
     * @param prioridad el nivel de prioridad
     * @return el color hexadecimal correspondiente
     */
    public static String getColores(int prioridad) {
        if (prioridad <= 0) return COLORES.get(0);
        if (prioridad >= 7) return COLORES.get(7);
        return COLORES.get(prioridad);
    }
}
