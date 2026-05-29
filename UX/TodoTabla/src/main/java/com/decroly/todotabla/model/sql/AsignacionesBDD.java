package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Asignacion;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.utils.AppErrorHandler;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class AsignacionesBDD {
    
        public static boolean insertar(Asignacion i) throws Exception {
        boolean estado = false;

        String sql = "INSERT INTO asignacion VALUES (NULL, ?, ?, ?, NULL)";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)
        ) {
            stmnt.setInt(1, i.getIdUsuario().getId());
            stmnt.setInt(2, i.getIdTarea().getId());
            stmnt.setDate(3, Date.valueOf(i.getFechaAsignacion()));

            estado = (stmnt.executeUpdate() == 1);

        }

        return estado;
    }

    public static boolean actualizar(Asignacion i) throws Exception {
        boolean estado = false;

        if (i != null) {
            String sql = "UPDATE `todotabla`.`asignacion` " +
                    "SET " +
                    "`fecha_fin` = ? " +
                    "WHERE `id` = ?";

            try (Connection conexion = BDD.getConnection();
                 PreparedStatement stmnt = conexion.prepareStatement(sql)
            ) {

                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setDate(1, Date.valueOf(i.getFechaFin()));
                stmnt.setInt(2, i.getId());

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

    public static boolean archivar(Asignacion i) throws Exception {
        boolean estado = false;

        if (i != null && i.getFechaFin() == null) {
            i.setFechaFin(LocalDate.now());
            estado = actualizar(i);
        }

        return estado;
    }

    public static boolean borrar(Asignacion i) throws Exception {
        boolean estado = false;

        if (i != null) {
            String sql = "DELETE FROM asignacion WHERE id = ?";
            try (Connection conexion = BDD.getConnection();
                 PreparedStatement stmnt = conexion.prepareStatement(sql)) {

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

    public static Map<Integer, Asignacion> getAsignaciones() throws Exception {
        Map<Integer, Asignacion> asignaciones = new LinkedHashMap<>();

        String sql = "TABLE asignacion";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Date rawDate = table.getDate("fecha_fin");
                LocalDate dateFin = null;
                if (rawDate != null) {
                    dateFin = rawDate.toLocalDate();
                }

                Asignacion asignacion = new Asignacion(
                        table.getInt("id"),
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        TareasBDD.getTarea(table.getInt("tarea_ID")),
                        table.getDate("fecha_asignacion").toLocalDate(),
                        dateFin
                );

                asignaciones.put(asignacion.getId(), asignacion);
            }

        }

        return asignaciones;
    }
    
    public static Map<Integer, Asignacion> getAsignaciones(Tarea tarea_ID) throws Exception {
        Map<Integer, Asignacion> asignaciones = new LinkedHashMap<>();

        String sql = "SELECT * FROM asignacion WHERE tarea_ID = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            stmnt.setInt(1, tarea_ID.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Date rawDate = table.getDate("fecha_fin");
                LocalDate dateFin = null;
                if (rawDate != null) {
                    dateFin = rawDate.toLocalDate();
                }

                Asignacion asignacion = new Asignacion(
                        table.getInt("id"),
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        tarea_ID,
                        table.getDate("fecha_asignacion").toLocalDate(),
                        dateFin
                );
                
                asignaciones.put(asignacion.getId(), asignacion);
            }

        }

        return asignaciones;
    }
    
    public static Map<Integer, Asignacion> getAsignacions(Usuario usuario_ID) throws Exception {
        Map<Integer, Asignacion> asignaciones = new LinkedHashMap<>();

        String sql = "SELECT * FROM asignacion WHERE usuario_ID = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            stmnt.setInt(1, usuario_ID.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Date rawDate = table.getDate("fecha_fin");
                LocalDate dateFin = null;
                if (rawDate != null) {
                    dateFin = rawDate.toLocalDate();
                }

                Asignacion asignacion = new Asignacion(
                        table.getInt("id"),
                        usuario_ID,
                        TareasBDD.getTarea(table.getInt("tarea_ID")),
                        table.getDate("fecha_asignacion").toLocalDate(),
                        dateFin
                );
                
                asignaciones.put(asignacion.getId(), asignacion);
            }

        }

        return asignaciones;
    }

    public static Asignacion getAsignacion(int id) throws Exception {
        Asignacion asignacion = null;

        String sql = "SELECT * FROM asignacion WHERE id = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Date rawDate = table.getDate("fecha_fin");
                LocalDate dateFin = null;
                if (rawDate != null) {
                    dateFin = rawDate.toLocalDate();
                }

                asignacion = new Asignacion(
                        table.getInt("id"),
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        TareasBDD.getTarea(table.getInt("tarea_ID")),
                        table.getDate("fecha_asignacion").toLocalDate(),
                        dateFin
                );
            }

        }

        return asignacion;
    }
}
