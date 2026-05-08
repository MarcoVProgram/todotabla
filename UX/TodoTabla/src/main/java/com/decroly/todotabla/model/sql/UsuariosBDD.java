package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Miembro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class UsuariosBDD {
    public static boolean insertar(Miembro m) throws SQLException{
        boolean estado = false;

        try (Connection conexion = BDD.getConnection(true);
             PreparedStatement stmnt = conexion.prepareStatement(
            "INSERT INTO usuarios VALUES (NULL, ?, ?, ?)"
             )
        ) {
            stmnt.setString(1, m.getNombre());
            stmnt.setString(2, m.getApellidos());
            stmnt.setString(3, m.getEmail());

            estado = (stmnt.executeUpdate() == 1);

        } catch (Exception e) {
            throw new SQLException(e);
        }

        return estado;
    }

    public static boolean actualizar(Miembro m) throws SQLException {
        boolean estado = false;

        if (m != null) {
            try (Connection conexion = BDD.getConnection(true);
                 PreparedStatement stmnt = conexion.prepareStatement(
                         "UPDATE `todotabla`.`usuario` " +
                                 "SET " +
                                 "`id` = ?, " +
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
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }

        return estado;
    }

    public static boolean borrar(Miembro m) throws Exception {
        boolean estado = false;

        if (m != null) {
            try (Connection conexion = BDD.getConnection(true);
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
                throw new SQLException(e);
            }
        }

        return estado;
    }

    public static Set<Miembro> obtnerAll() throws SQLException{
        LinkedHashSet miembros = new LinkedHashSet();

        try (Connection conexion = BDD.getConnection(true);
            PreparedStatement stmnt = conexion.prepareStatement("" +
                    "TABLE usuario;")
        ) {

        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
