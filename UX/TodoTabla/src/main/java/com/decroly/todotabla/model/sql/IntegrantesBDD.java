package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Usuario;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class IntegrantesBDD {

    public static boolean insertar(Integrante i) {
        boolean estado = false;

        try (Connection conexion = BDD.getConnection(false);
             PreparedStatement stmnt = conexion.prepareStatement(
            "INSERT INTO integrante VALUES (NULL, ?, ?, NULL, ?, ?)"
             )
        ) {
            stmnt.setString(1, i.getRol());
            stmnt.setDate(2, Date.valueOf(i.getFechaEntrada()));
            stmnt.setInt(3, i.getIdUsuario().getId());
            stmnt.setInt(4, i.getIdProyecto().getId());

            estado = (stmnt.executeUpdate() == 1);

        } catch (Exception e) {
            return false;
        }

        return estado;
    }

    public static boolean actualizar(Integrante i) {
        boolean estado = false;

        if (i != null) {
            try (Connection conexion = BDD.getConnection(false);
                 PreparedStatement stmnt = conexion.prepareStatement(
                         "UPDATE `todotabla`.`integrante` " +
                                 "SET " +
                                 "`rol` = ?, " +
                                 "`fecha_salida` = ? " +
                                 "WHERE `id` = ?"
                 )
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, i.getRol());
                stmnt.setDate(2, Date.valueOf(i.getFechaSalida()));

                stmnt.setInt(3, i.getId());

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

    public static boolean borrar(Integrante i) {
        boolean estado = false;

        if (i != null) {
            try (Connection conexion = BDD.getConnection(false);
            PreparedStatement stmnt = conexion.prepareStatement(
                "DELETE FROM integrante WHERE id = ?"
            )) {
               conexion.nativeSQL("START TRANSACTION;");
               stmnt.setInt(1, i.getId());

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

    public static Map<Integer, Integrante> getIntegrantes() {
        Map<Integer, Integrante> integrantes = new LinkedHashMap<>();

        try (Statement stmnt = BDD.getConnection(false).createStatement()) {
            ResultSet table = stmnt.executeQuery("TABLE integrante;");

            while (table.next()) {
                Integrante integrante = new Integrante(table.getInt("id"),
                        table.getString("rol"),
                        table.getDate("fecha_entrada").toLocalDate(),
                        table.getDate("fecha_salida").toLocalDate(),
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );


                integrantes.put(integrante.getId(), integrante);
            }

        } catch (Exception e) {
            return null;
        }

        return integrantes;
    }

    public static Map<Integer, Integrante> getIntegrantes(Proyecto proyecto_ID) {
        Map<Integer, Integrante> integrantes = new LinkedHashMap<>();

        try (PreparedStatement stmnt = BDD.getConnection(false).prepareStatement("SELECT * FROM integrante WHERE proyecto_ID = ?;")) {
            stmnt.setInt(1, proyecto_ID.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Integrante integrante = new Integrante(table.getInt("id"),
                        table.getString("rol"),
                        table.getDate("fecha_entrada").toLocalDate(),
                        table.getDate("fecha_salida").toLocalDate(),
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );

                integrantes.put(integrante.getId(), integrante);
            }

        } catch (Exception e) {
            return null;
        }

        return integrantes;
    }

    public static Map<Integer, Integrante> getIntegrantes(Usuario usuario_ID) {
        Map<Integer, Integrante> integrantes = new LinkedHashMap<>();

        try (PreparedStatement stmnt = BDD.getConnection(false).prepareStatement("SELECT * FROM integrante WHERE usuario_ID = ?;")) {
            stmnt.setInt(1, usuario_ID.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Integrante integrante = new Integrante(table.getInt("id"),
                        table.getString("rol"),
                        table.getDate("fecha_entrada").toLocalDate(),
                        table.getDate("fecha_salida").toLocalDate(),
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );

                integrantes.put(integrante.getId(), integrante);
            }

        } catch (Exception e) {
            return null;
        }

        return integrantes;
    }

    public static Integrante getIntegrante(int id) {
        Integrante integrante = null;

        try (PreparedStatement stmnt = BDD.getConnection(false).prepareStatement("SELECT * FROM integrante WHERE id = ?;")) {
            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                integrante = new Integrante(table.getInt("id"),
                        table.getString("rol"),
                        table.getDate("fecha_entrada").toLocalDate(),
                        table.getDate("fecha_salida").toLocalDate(),
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );
            }

        } catch (Exception e) {
            return null;
        }

        return integrante;
    }
}
