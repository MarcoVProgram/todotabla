package com.decroly.todotabla.utils.constants;

import java.util.HashMap;
import java.util.Map;

public class ColoresPrioridad {
    private ColoresPrioridad() {}

    final static Map<Integer, String> COLORES = new HashMap<>() {{
        put(0,"#da3633");
        put(1,"#f85149");
        put(2,"#f0883e");
        put(3,"#e3b341");
        put(4,"#85c78a");
        put(5,"#3fb950");
        put(6,"#8b949e");
    }};

    public static String getColores(int prioridad) {
        if (prioridad <= 0) return ColoresPrioridad.getColores(0);
        if (prioridad >= 6) return COLORES.get(6);
        return COLORES.get(prioridad);
    }
}
