package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Tarea;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TareasBDD {
    public static boolean insertar(Tarea t) throws SQLException {
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
            throw new SQLException(e);
        }

        return estado;
    }

    public static boolean actualizar(Tarea t) throws SQLException {
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
                throw new SQLException(e);
            }
        }

        return estado;
    }

//    public static boolean archivar(Tarea t) throws UnsupportedOperationException {
//        throw new UnsupportedOperationException();
//    }

    public static boolean borrar(Tarea t) throws Exception {
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
                throw new SQLException(e);
            }
        }

        return estado;
    }

    /*public static Map<Integer, Tarea> getUsuarios() throws SQLException{
        Map<Integer, Tarea> tareas = new HashMap<>();

        try (Statement stmnt = BDD.getConnection(false).createStatement()) {
            ResultSet table = stmnt.executeQuery("TABLE usuario;");

            while (table.next()) {
                Tarea tarea = new Tarea(table.getInt("id"),
                        table.getString("nombre"),
                        UsuariosBDD.getUsuario(table.getInt("")));

                tareas.put(tarea.getId(), tarea);
            }

        } catch (Exception e) {
            throw new SQLException(e);
        }

        return tareas;
    }

    public static Map<Integer, Miembro> getUsuario() throws SQLException{
        return getUsuarios();
    }

    public static Miembro getUsuario(int id) throws SQLException {
        try (PreparedStatement stmnt = BDD.getConnection(false).prepareStatement(
                "SELECT * FROM usuario WHERE id = ?;"
        )) {
            ResultSet table = stmnt.executeQuery();

            table.next();
            Miembro uzer = new Miembro(table.getInt("id"),
                    table.getString("nombre"),
                    table.getString("apellidos"),
                    table.getString("email")
            );

            if (table.next()) {
                throw new Exception("Esto no me lo esperaba. (demasiados miembros)");
            }

            return uzer;


        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
     */
}
