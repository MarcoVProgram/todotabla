package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.utils.AppErrorHandler;

import java.sql.*;
import java.time.LocalDate;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProyetosBDD {
    public static int insertar(Proyecto p) throws Exception{ // TODO obtener numero del id al insertar
        boolean estado = false;
        int clave = -1;

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(
                     "INSERT INTO proyecto VALUES (NULL, ?, ?, NULL)", Statement.RETURN_GENERATED_KEYS
             )
        ){

            stmnt.setString(1, p.getTitulo());
            stmnt.setDate(2, Date.valueOf(p.getFechaCreacion()));

            estado = (stmnt.executeUpdate() == 1);

            if (estado) {
                ResultSet keys = stmnt.getGeneratedKeys();
                while (keys.next()) {
                    clave = keys.getInt(1);
                    p.setId(clave);
                }
            }

        return clave;

        }
    }
    public static boolean actualizar(Proyecto p) throws Exception {
        boolean estado = false;

        if (p != null) {
            try (Connection conexion = BDD.getConnection();
                 PreparedStatement stmnt = conexion.prepareStatement(
                         "UPDATE `todotabla`.`proyecto` " +
                                 "SET " +
                                 "`titulo` = ?, " +
                                 "`fecha_cierre` = ? " +
                                 "WHERE `id` = ?; "
                 )
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, p.getTitulo());
                if (p.getFechaCierre() != null) {
                    stmnt.setDate(2, Date.valueOf(p.getFechaCierre()));
                } else {
                    stmnt.setNull(2, Types.DATE);
                }

                stmnt.setInt(3, p.getId());

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

    public static boolean archivar(Proyecto p) throws Exception {
        boolean estado = false;

        if (p != null && p.getFechaCierre() == null) {
            p.setFechaCierre(LocalDate.now());
            actualizar(p);
        }

        return estado;
    }

    public static boolean borrar(Proyecto p) throws Exception {
        if (p != null) {
            try (Connection  conexion = BDD.getConnection();
                    PreparedStatement stmnt = conexion.prepareStatement("" +
                    "DELETE FROM `todotabla`.`proyecto` " +
                    "WHERE id = ?;\n")
            ) {
                stmnt.setInt(1, p.getId());

                return (stmnt.executeUpdate() == 1);
            }
        }
        return false;
    }

    public static Map<Integer, Proyecto> getProyectos() throws Exception {
        Map<Integer, Proyecto> proyectos = new LinkedHashMap<>();

        try (Connection conexion = BDD.getConnection();
                Statement stmnt = conexion.createStatement()) {
            ResultSet table = stmnt.executeQuery("TABLE proyecto;");

            while (table.next()) {
                Date rawDate = table.getDate("fecha_cierre");
                LocalDate dateClose = null;
                if (rawDate != null) {
                    dateClose = rawDate.toLocalDate();
                }

                Proyecto pro = new Proyecto(table.getInt("id"),
                        table.getString("titulo"),
                        table.getDate("fecha_creacion").toLocalDate(),
                        dateClose
                );

                proyectos.put(pro.getId(), pro);
            }

        }

        return proyectos;
    }

    public static Proyecto getProyecto(int id) throws Exception{
        Proyecto proyecto = null;

        try (Connection conexion = BDD.getConnection();
                PreparedStatement stmnt = conexion.prepareStatement("SELECT * FROM proyecto WHERE id = ?;")) {
            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Date rawDate = table.getDate("fecha_cierre");
                LocalDate dateClose = null;
                if (rawDate != null) {
                    dateClose = rawDate.toLocalDate();
                }

                proyecto = new Proyecto(table.getInt("id"),
                        table.getString("titulo"),
                        table.getDate("fecha_creacion").toLocalDate(),
                        dateClose
                );
            }

        }

        return proyecto;
    }

}