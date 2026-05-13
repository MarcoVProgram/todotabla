package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Proyecto;

import java.sql.*;
import java.time.LocalDate;

import java.util.LinkedHashMap;
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
            stmnt.setDate(2, Date.valueOf(p.getFechaCreacion()));

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
                         "UPDATE `todotabla`.`proyecto` " +
                                 "SET " +
                                 "`titulo` = ?, " +
                                 "`fecha_cierre` = ?, " +
                                 "WHERE `id` = ?; "
                 )
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, p.getTitulo());
                stmnt.setDate(2, Date.valueOf(p.getFechaCierre()));

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
        boolean estado = false;

        if (p != null && p.getFechaCierre() == null) {
            p.setFechaCierre(LocalDate.now());
            actualizar(p);
        }

        return estado;
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

    public static Map<Integer, Proyecto> getProyectos() {
        Map<Integer, Proyecto> proyectos = new LinkedHashMap<>();

        try (Statement stmnt = BDD.getConnection(false).createStatement()) {
            ResultSet table = stmnt.executeQuery("TABLE proyecto;");

            while (table.next()) {
                Proyecto pro = new Proyecto(table.getInt("id"),
                        table.getString("titulo"),
                        table.getDate("fecha_creacion").toLocalDate(),
                        table.getDate("fecha_cierre").toLocalDate()
                );

                proyectos.put(pro.getId(), pro);
            }

        } catch (Exception e) {
            return null;
        }

        return proyectos;
    }

    public static Proyecto getProyecto(int id) {
        Proyecto proyecto = null;

        try (PreparedStatement stmnt = BDD.getConnection(false).prepareStatement("SELECT * FROM proyecto WHERE id = ?;")) {
            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                proyecto = new Proyecto(table.getInt("id"),
                        table.getString("titulo"),
                        table.getDate("fecha_creacion").toLocalDate(),
                        table.getDate("fecha_cierre").toLocalDate()
                );
            }

        } catch (Exception e) {
            return null;
        }

        return proyecto;
    }

}