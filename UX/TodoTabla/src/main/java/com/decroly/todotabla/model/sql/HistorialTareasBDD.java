package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.HistorialTareas;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.utils.AppErrorHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class HistorialTareasBDD {

    public static boolean borrar(HistorialTareas ht) throws Exception {
        boolean estado = false;

        if (ht != null) {
            String sql = "DELETE FROM historial_tareas WHERE id = ?";
            try (Connection conexion = BDD.getConnection();
                 PreparedStatement stmnt = conexion.prepareStatement(sql)) {

               conexion.nativeSQL("START TRANSACTION;");
               stmnt.setInt(1, ht.getId());

               estado = (stmnt.executeUpdate() == 1);
               if (estado) {
                   conexion.nativeSQL("COMMIT;");
               } else {
                   conexion.nativeSQL("ROLLBACK;");
               }
            }
        }

        return estado;
    }

    public static Map<Integer, HistorialTareas> getHistorialTareas() throws Exception {
        Map<Integer, HistorialTareas> historialTareas = new LinkedHashMap<>();

        String sql = "TABLE historial_tareas;";

        try (Connection conexion = BDD.getConnection();
                PreparedStatement stmnt = conexion.prepareStatement(sql)) {
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                HistorialTareas tarea = new HistorialTareas(table.getInt("id"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        TareasBDD.getTarea(table.getInt("tarea_id")),
                        table.getTimestamp("fecha_cambio").toLocalDateTime()
                );

                historialTareas.put(tarea.getId(), tarea);
            }

        }
        return historialTareas;
    }

    public static Map<Integer, HistorialTareas> getHistorialTareas(Tarea tarea_id) throws Exception {
        Map<Integer, HistorialTareas> historialTareas = new LinkedHashMap<>();

        String sql = "SELECT * FROM historial_tareas WHERE tarea_id = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            stmnt.setInt(1, tarea_id.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                HistorialTareas tarea = new HistorialTareas(table.getInt("id"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        TareasBDD.getTarea(table.getInt("tarea_id")),
                        table.getTimestamp("fecha_cambio").toLocalDateTime()
                );

                historialTareas.put(tarea.getId(), tarea);
            }

        }

        return historialTareas;
    }

    public static HistorialTareas getHistorialTarea(int id) throws Exception {
        HistorialTareas tareas = null;

        String sql = "SELECT * FROM historial_tareas WHERE id = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {
            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                tareas = new HistorialTareas(table.getInt("id"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        TareasBDD.getTarea(table.getInt("tarea_id")),
                        table.getTimestamp("fecha_cambio").toLocalDateTime()
                );
            }

        }

        return tareas;
    }
}