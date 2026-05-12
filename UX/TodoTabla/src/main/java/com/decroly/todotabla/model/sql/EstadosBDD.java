package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Estado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class EstadosBDD {
    public static boolean insertar(Estado e) throws SQLException {
        boolean estado = false;

        try (Connection conexion = BDD.getConnection(false);
             PreparedStatement stmnt = conexion.prepareStatement(
                     "INSERT INTO estado VALUES (?, ?)"
             )
        ) {
            stmnt.setString(1, e.getNombre());
            stmnt.setString(2, e.getColor());

            estado = (stmnt.executeUpdate() == 1);

        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return estado;
    }
    public static boolean actualizar(Estado e) throws SQLException {
        boolean estado = false;

        if (e != null) {
            try (Connection conexion = BDD.getConnection(false);
                 PreparedStatement stmnt = conexion.prepareStatement(
                         "UPDATE `todotabla`.`estado` " +
                                 "SET " +
                                 "`color` = ? " +
                                 "WHERE `nombre` = ?; "
                 )
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, e.getColor());

                stmnt.setString(2, e.getNombre());

                estado = (stmnt.executeUpdate() == 1);

                if (estado) {
                    conexion.nativeSQL("COMMIT;");
                } else {
                    conexion.nativeSQL("ROLLBACK;");
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }

        return estado;
    }

    public static boolean borrar(Estado e) throws Exception {
        boolean estado = false;

        if (e != null) {
            try (Connection conexion = BDD.getConnection(false);
                 PreparedStatement stmnt = conexion.prepareStatement(
                         "DELETE FROM `todotabla`.`estado` " +
                                 "WHERE `nombre` = ?; "
                 )
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, e.getNombre());

                estado = (stmnt.executeUpdate() == 1);

                if (estado) {
                    conexion.nativeSQL("COMMIT;");
                } else {
                    conexion.nativeSQL("ROLLBACK;");
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }

        return estado;
    }

    public static List<Estado> getEstados() throws SQLException {
        List<Estado> estados = new LinkedList<>();

        try (Connection conexion = BDD.getConnection(false);
             PreparedStatement stmnt = conexion.prepareStatement(
                     "SELECT * FROM `todotabla`.`estado` ");
             ResultSet rs = stmnt.executeQuery();
        ) {

            while (rs.next()) {
                Estado newEstado = new Estado(rs.getString("nombre"), rs.getString("color"));

                estados.add(newEstado);
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return estados;
    }

    public static Estado getEstado(String estadoNombre) throws SQLException {
        Estado estado = null;

        try (Connection conexion = BDD.getConnection(false);
             PreparedStatement stmnt = conexion.prepareStatement(
                     "SELECT * FROM `todotabla`.`estado` WHERE nombre = ? ");
        ) {

            stmnt.setString(1, estadoNombre);
            ResultSet rs = stmnt.executeQuery();
            
            while (rs.next()) {
                estado = new Estado(rs.getString("nombre"), rs.getString("color"));
            }

        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return estado;
    }
}