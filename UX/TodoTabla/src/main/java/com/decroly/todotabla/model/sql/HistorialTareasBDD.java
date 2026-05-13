package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.HistorialTareas;
import com.decroly.todotabla.model.Tarea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class HistorialTareasBDD {

    public static boolean borrar(HistorialTareas ht) {
        boolean estado = false;

        if (ht != null) {
            try (Connection conexion = BDD.getConnection(false);
            PreparedStatement stmnt = conexion.prepareStatement(
                "DELETE FROM historial_tareas WHERE id = ?"
            )) {
               conexion.nativeSQL("START TRANSACTION;");
               stmnt.setInt(1, ht.getId());

               estado = (stmnt.executeUpdate() == 1);
               if (estado) {
                   conexion.nativeSQL("COMMIT;");
               } else {
                   conexion.nativeSQL("ROLLBACK;");
               }
            } catch (Exception e) {
                return false;
            }
        }

        return estado;
    }

    public static Map<Integer, HistorialTareas> getHistorialTareas() {
        Map<Integer, HistorialTareas> historialTareas = new LinkedHashMap<>();

        try (Statement stmnt = BDD.getConnection(false).createStatement()) {
            ResultSet table = stmnt.executeQuery("TABLE historial_tareas;");

            while (table.next()) {
                HistorialTareas tarea = new HistorialTareas(table.getInt("id"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        TareasBDD.getTarea(table.getInt("tarea_id")),
                        table.getTimestamp("fecha_cambio").toLocalDateTime()
                );

                historialTareas.put(tarea.getId(), tarea);
            }

        } catch (Exception e) {
            return null;
        }

        return historialTareas;
    }

    public static Map<Integer, HistorialTareas> getHistorialTareas(Tarea tarea_id) {
        Map<Integer, HistorialTareas> historialTareas = new LinkedHashMap<>();

        try (PreparedStatement stmnt = BDD.getConnection(false).prepareStatement("SELECT * FROM historial_tareas WHERE tarea_id = ?;")) {
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

        } catch (Exception e) {
            return null;
        }

        return historialTareas;
    }

    public static HistorialTareas getHistorialTarea(int id) {
        HistorialTareas tareas = null;

        try (PreparedStatement stmnt = BDD.getConnection(false).prepareStatement("SELECT * FROM historial_tareas WHERE id = ?;")) {
            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                tareas = new HistorialTareas(table.getInt("id"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        TareasBDD.getTarea(table.getInt("tarea_id")),
                        table.getTimestamp("fecha_cambio").toLocalDateTime()
                );
            }

        } catch (Exception e) {
            return null;
        }

        return tareas;
    }
}