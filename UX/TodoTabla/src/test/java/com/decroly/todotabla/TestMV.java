package com.decroly.todotabla;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.sql.ProyetosBDD;
import com.decroly.todotabla.model.sql.TareasBDD;

import com.decroly.todotabla.model.Tarea;

public class TestMV {

    public static boolean main(){ // TODO
        boolean estado = false;

        try {
            Map<Integer, Proyecto> proyectos = ProyetosBDD.getProyectos();
            
            for (Tarea t : TareasBDD.getTareas().values()){
                assertTrue(proyectos.containsValue(t.getIdProyecto()));
            }

            estado = true;
        } catch (Exception e) {
            return false;
        }


        return estado;
    }
}
