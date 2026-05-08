package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Miembro;
import com.decroly.todotabla.model.Proyecto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Map;

public class ProyetosBDD {
    public static boolean insertar(Proyecto p) throws SQLException {
        boolean estado = false;

        try (Connection conexion = BDD.getConnection(false);
             PreparedStatement stmnt = conexion.prepareStatement(
                     "INSERT INTO proyecto VALUES (NULL, ?, ?, ?)"
             )
        ) {
            stmnt.setString(1, p.getTitulo());
            stmnt.setLong(2, p.getFechaCreacion().toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.of("+01:00")));
            stmnt.setLong(3, p.getFechaCierre().toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.of("+01:00")));

            estado = (stmnt.executeUpdate() == 1);

        } catch (Exception e) {
            throw new SQLException(e);
        }

        return estado;
    }
    public static boolean actualizar(Proyecto p) throws SQLException {
        boolean estado = false;

        if (p != null) {
            try (Connection conexion = BDD.getConnection(false);
                 PreparedStatement stmnt = conexion.prepareStatement(
                         "UPDATE `todotabla`.`usuario` " +
                                 "SET " +
                                 "`titulp` = ?, " +
                                 "`fecha_crecion` = ?, " +
                                 "`fecha_final` = ? " +
                                 "WHERE `id` = ?; "
                 )
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, p.getTitulo());
                stmnt.setLong(2, p.getFechaCreacion().toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.of("+01:00")));
                stmnt.setLong(3, p.getFechaCierre().toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.of("+01:00")));

                stmnt.setInt(4, p.getId());

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
    public static boolean archivar(Proyecto p) throws SQLException {
        p.setFechaFin(LocalDate.now());

        return actualizar(p);
    }

    public static boolean borrar(Proyecto p) throws Exception {
        throw new UnsupportedOperationException();
    }

    public static Map<Integer, Miembro> getProyectos() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public static Map<Integer, Miembro> getProyecto() throws SQLException{
        return getProyectos();
    }

    public static Miembro getProyecto(int id) throws SQLException {
        throw new UnsupportedOperationException();
    }
}