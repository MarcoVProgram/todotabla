package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Miembro;
import com.decroly.todotabla.model.Proyecto;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.util.Map;

public class ProyetosBDD {
    public static boolean insertar(Proyecto p){
        boolean estado = false;

        try (Connection conexion = BDD.getConnection(false);
             PreparedStatement stmnt = conexion.prepareStatement(
                     "INSERT INTO proyecto VALUES (NULL, ?, ?, NULL)"
             )
        ) {
            stmnt.setString(1, p.getTitulo());
            stmnt.setLong(2, p.getFechaCreacion().toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.of("+01:00")));

            estado = (stmnt.executeUpdate() == 1);

        }  catch (Exception e) {
            return false;
        }

        return estado;
    }
    public static boolean actualizar(Proyecto p) {
        boolean estado = false;

        if (p != null) {
            try (Connection conexion = BDD.getConnection(false);
                 PreparedStatement stmnt = conexion.prepareStatement(
                         "UPDATE `todotabla`.`usuario` " +
                                 "SET " +
                                 "`titulp` = ?, " +
                                 "`fecha_crecion` = ?, " +
                                 "WHERE `id` = ?; "
                 )
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, p.getTitulo());
                stmnt.setLong(2, p.getFechaCreacion().toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.of("+01:00")));

                stmnt.setInt(3, p.getId());

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

    public static boolean archivar(Proyecto p)  {
        try {
            if (p.getFechaCierre().isAfter(LocalDate.now())) {
                p.setFechaFin(LocalDate.now());
                return actualizar(p);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    public static boolean borrar(Proyecto p) {
        if (p != null) {
            try (PreparedStatement stmnt = BDD.getConnection(false).prepareStatement("" +
                    "DELETE FROM `todotabla`.`proyecto` " +
                    "WHERE id = ?;\n")
            ) {
                stmnt.setInt(1, p.getId());

                return (stmnt.executeUpdate() == 1);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static Map<Integer, Miembro> getProyectos() {
        throw new UnsupportedOperationException();
    }

    public static Map<Integer, Miembro> getProyecto() {
        return getProyectos();
    }

    public static Miembro getProyecto(int id) {
        throw new UnsupportedOperationException();
    }

}