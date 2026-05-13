package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Tarea;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TareasBDD {
    public static boolean insertar(Tarea t) {
        boolean estado = false;

        try (Connection conexion = BDD.getConnection(false);
             PreparedStatement stmnt = conexion.prepareStatement(
            "INSERT INTO tarea VALUES (NULL, ?, ?, ?, ?)"
             )
        ) {
            stmnt.setString(1, t.getNombre());
            stmnt.setInt(2, t.getPrioridad());
            stmnt.setString(3, t.getEstado().getNombre());
            stmnt.setInt(4, t.getIdProyecto().getId());

            estado = (stmnt.executeUpdate() == 1);

        } catch (Exception e) {
            return false;
        }

        return estado;
    }

    public static boolean actualizar(Tarea t) {
        boolean estado = false;

        if (t != null) {
            try (Connection conexion = BDD.getConnection(false);
                 PreparedStatement stmnt = conexion.prepareStatement(
                         "UPDATE `todotabla`.`tarea` " +
                                 "SET " +
                                 "`nombre` = ?, " +
                                 "`prioridad` = ?, " +
                                 "`estado` = ?, " +
                                 "WHERE `id` = ?; "
                 )
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, t.getNombre());
                stmnt.setInt(2, t.getPrioridad());
                stmnt.setString(3, t.getEstado().getNombre());

                stmnt.setInt(4, t.getId());

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

    public static boolean borrar(Tarea t) {
        boolean estado = false;

        if (t != null) {
            try (Connection conexion = BDD.getConnection(false);
            PreparedStatement stmnt = conexion.prepareStatement(
                "DELETE FROM usuario WHERE id = ?"
            )) {
               conexion.nativeSQL("START TRANSACTION;");
               stmnt.setInt(1, t.getId());

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

    public static Map<Integer, Tarea> getTareas() {
        Map<Integer, Tarea> tareas = new LinkedHashMap<>();

        try (Connection conexion = BDD.getConnection(false);
             Statement stmnt = conexion.createStatement()) {
            ResultSet table = stmnt.executeQuery("TABLE tarea;");

            while (table.next()) {
                Tarea tarea = new Tarea(table.getInt("id"),
                        table.getString("nombre"),
                        table.getInt("prioridad"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );


                tareas.put(tarea.getId(), tarea);
            }

        } catch (Exception e) {
            return null;
        }

        return tareas;
    }

    public static Tarea getTarea(int id) {
        Tarea tarea = null;

        try (Connection conexion = BDD.getConnection(false);
                PreparedStatement stmnt = conexion.prepareStatement("SELECT * FROM tarea WHERE id = ?;")) {
            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                tarea = new Tarea(table.getInt("id"),
                        table.getString("nombre"),
                        table.getInt("prioridad"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID")));
            }

        } catch (Exception e) {
            return null;
        }

        return tarea;
    }
}
