package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.utils.AppErrorHandler;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class IntegrantesBDD {

    public static boolean insertar(Integrante i) throws Exception {
        boolean estado = false;

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(
            "INSERT INTO integrante VALUES (NULL, ?, ?, NULL, ?, ?)"
             )
        ) {
            stmnt.setString(1, i.getRol());
            stmnt.setDate(2, Date.valueOf(i.getFechaEntrada()));
            stmnt.setInt(3, i.getIdUsuario().getId());
            stmnt.setInt(4, i.getIdProyecto().getId());

            estado = (stmnt.executeUpdate() == 1);

        }

        return estado;
    }

    public static boolean actualizar(Integrante i) throws Exception {
        boolean estado = false;

        if (i != null) {
            try (Connection conexion = BDD.getConnection();
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
            }
        }

        return estado;
    }

    public static boolean borrar(Integrante i) throws Exception {
        boolean estado = false;

        if (i != null) {
            try (Connection conexion = BDD.getConnection();
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
            }
        }

        return estado;
    }

    public static Map<Integer, Integrante> getIntegrantes() throws Exception {
        Map<Integer, Integrante> integrantes = new LinkedHashMap<>();

        try (Connection conexion = BDD.getConnection();
                Statement stmnt = conexion.createStatement()) {
            ResultSet table = stmnt.executeQuery("TABLE integrante;");

            while (table.next()) {
                Date rawDate = table.getDate("fecha_salida");
                LocalDate dateExit = null;
                if (rawDate != null) {
                    dateExit = rawDate.toLocalDate();
                }

                Integrante integrante = new Integrante(table.getInt("id"),
                        table.getString("rol"),
                        table.getDate("fecha_entrada").toLocalDate(),
                        dateExit,
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );


                integrantes.put(integrante.getId(), integrante);
            }

        }
        return integrantes;
    }

    public static Map<Integer, Integrante> getIntegrantes(Proyecto proyecto_ID) throws Exception {
        String sql = "SELECT * FROM integrante WHERE proyecto_ID = ?;";
        return getIntegerIntegranteMap(proyecto_ID, sql);
    }

    public static Map<Integer, Integrante> getIntegrantesActivos(Proyecto proyecto_ID) throws Exception {
        String sql = "SELECT * FROM integrante WHERE proyecto_ID = ? AND (fecha_salida = null OR fecha_salida > CURRENT_DATE());";
        return getIntegerIntegranteMap(proyecto_ID, sql);
    }

    private static Map<Integer, Integrante> getIntegerIntegranteMap(Proyecto proyecto_ID, String sql) throws Exception {
        Map<Integer, Integrante> integrantes = new LinkedHashMap<>();

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {
            stmnt.setInt(1, proyecto_ID.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Date rawDate = table.getDate("fecha_salida");
                LocalDate dateExit = null;
                if (rawDate != null) {
                    dateExit = rawDate.toLocalDate();
                }

                Integrante integrante = new Integrante(table.getInt("id"),
                        table.getString("rol"),
                        table.getDate("fecha_entrada").toLocalDate(),
                        dateExit,
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );

                integrantes.put(integrante.getId(), integrante);
            }

        }
        return integrantes;
    }

    public static Map<Integer, Integrante> getIntegrantes(Usuario usuario_ID) throws Exception {
        Map<Integer, Integrante> integrantes = new LinkedHashMap<>();

        try (Connection conexion = BDD.getConnection();
                PreparedStatement stmnt = conexion.prepareStatement("SELECT * FROM integrante WHERE usuario_ID = ?;")) {
            stmnt.setInt(1, usuario_ID.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Date rawDate = table.getDate("fecha_salida");
                LocalDate dateExit = null;
                if (rawDate != null) {
                    dateExit = rawDate.toLocalDate();
                }

                Integrante integrante = new Integrante(table.getInt("id"),
                        table.getString("rol"),
                        table.getDate("fecha_entrada").toLocalDate(),
                        dateExit,
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );

                integrantes.put(integrante.getId(), integrante);
            }

        }
        return integrantes;
    }

    public static Integrante getIntegrante(int id) throws Exception {
        Integrante integrante = null;

        try (Connection conexion = BDD.getConnection();
                PreparedStatement stmnt = conexion.prepareStatement("SELECT * FROM integrante WHERE id = ?;")) {
            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Date rawDate = table.getDate("fecha_salida");
                LocalDate dateExit = null;
                if (rawDate != null) {
                    dateExit = rawDate.toLocalDate();
                }

                integrante = new Integrante(table.getInt("id"),
                        table.getString("rol"),
                        table.getDate("fecha_entrada").toLocalDate(),
                        dateExit,
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );
            }

        }

        return integrante;
    }
}
