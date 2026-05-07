package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Miembro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UsuariosBBDD {
    public static boolean insertar(Miembro m) throws SQLException{
        boolean estado = false;

        try (Connection conexion = BBDD.getConnection(true);
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
            try (Connection conexion = BBDD.getConnection(true);
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
                stmnt.setString(1, m.getNombre());
                stmnt.setString(2, m.getApellidos());
                stmnt.setString(3, m.getEmail());

                stmnt.setInt(4, m.getId());

                estado = (stmnt.executeUpdate() == 1);

            } catch (Exception e) {
                throw new SQLException(e);
            }
        }

        return estado;
    }

//    public static boolean borrar(Miembro m) {
//        boolean estado = false;
//
//        if (m != null) {
//            try () {
//
//            } catch () {}
//        }
//
//        return estado;
//    }
}
