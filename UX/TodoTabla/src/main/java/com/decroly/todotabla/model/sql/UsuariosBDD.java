package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Usuario;

import java.sql.*;
import java.util.*;

public class UsuariosBDD {
    public static boolean insertar(Usuario m){
        boolean estado = false;

        try (Connection conexion = BDD.getConnection(false);
             PreparedStatement stmnt = conexion.prepareStatement(
            "INSERT INTO usuario VALUES (NULL, ?, ?, ?)"
             )
        ) {
            stmnt.setString(1, m.getNombre());
            stmnt.setString(2, m.getApellidos());
            stmnt.setString(3, m.getEmail());

            estado = (stmnt.executeUpdate() == 1);

        }  catch (Exception e) {
            return false;
        }


        return estado;
    }

    public static boolean actualizar(Usuario m) {
        boolean estado = false;

        if (m != null) {
            try (Connection conexion = BDD.getConnection(false);
                 PreparedStatement stmnt = conexion.prepareStatement(
                         "UPDATE `todotabla`.`usuario` " +
                                 "SET " +
                                 "`nombre` = ?, " +
                                 "`apellidos` = ?, " +
                                 "`email` = ? " +
                                 "WHERE `id` = ?; "
                 )
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, m.getNombre());
                stmnt.setString(2, m.getApellidos());
                stmnt.setString(3, m.getEmail());

                stmnt.setInt(4, m.getId());

                estado = (stmnt.executeUpdate() == 1);

                if (estado) {
                    conexion.nativeSQL("COMMIT;");
                } else {
                    conexion.nativeSQL("ROLLBACK;");
                }
            }  catch (Exception e) {
                return false;
            }

        }

        return estado;
    }

    public static boolean archivar(Usuario m) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public static boolean borrar(Usuario m)  {
        boolean estado = false;

        if (m != null) {
            try (Connection conexion = BDD.getConnection(false);
            PreparedStatement stmnt = conexion.prepareStatement(
                "DELETE FROM usuario WHERE id = ?"
            )) {
               conexion.nativeSQL("START TRANSACTION;");
               stmnt.setInt(1, m.getId());

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

    public static Map<Integer, Usuario> getUsuarios() {
        Map<Integer, Usuario> miembros = new HashMap<>();

        try (Statement stmnt = BDD.getConnection(false).createStatement()) {
            ResultSet table = stmnt.executeQuery("TABLE usuario;");

            while (table.next()) {
                Usuario uzer = new Usuario(table.getInt("id"),
                        table.getString("nombre"),
                        table.getString("apellidos"),
                        table.getString("email")
                );

                miembros.put(uzer.getId(), uzer);
            }

        } catch (Exception e) {
            return null;
        }

        return miembros;
    }

    public static Map<Integer, Usuario> getUsuario() {
        return getUsuarios();
    }

    public static Usuario getUsuario(int id) throws SQLException {
        try (PreparedStatement stmnt = BDD.getConnection(false).prepareStatement(
                "SELECT * FROM usuario WHERE id = ?;"
        )) {
            ResultSet table = stmnt.executeQuery();

            table.next();
            Usuario uzer = new Usuario(table.getInt("id"),
                    table.getString("nombre"),
                    table.getString("apellidos"),
                    table.getString("email")
            );

            if (table.next()) {
                throw new Exception("Esto no me lo esperaba. (demasiados miembros)");
            }

            return uzer;


        } catch (Exception e) {
            return null;
        }

    }

    // TODO Si es necesario hay que agregaer otra forma de obtener los miembros al buscar.
}
