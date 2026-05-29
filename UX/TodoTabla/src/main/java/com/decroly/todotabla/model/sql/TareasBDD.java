package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.utils.AppErrorHandler;

import java.sql.*;
import java.util.*;

public class TareasBDD {
    public static int insertar(Tarea t) throws Exception {
        boolean estado = false;
        int clave = -1;

        String sql = "INSERT INTO tarea VALUES (NULL, ?, ?, ?, ?)";
        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)
        ) {
            stmnt.setString(1, t.getNombre());
            stmnt.setInt(2, t.getPrioridad());
            stmnt.setString(3, t.getEstado().getNombre());
            stmnt.setInt(4, t.getIdProyecto().getId());

            estado = (stmnt.executeUpdate() == 1);

            if (estado) {
                ResultSet keys = stmnt.getGeneratedKeys();
                while (keys.next()) {
                    clave = keys.getInt(1);
                }
            }

        }
        return clave;
    }

    public static boolean actualizar(Tarea t) throws Exception {
        boolean estado = false;

        if (t != null) {

            String sql = "UPDATE `todotabla`.`tarea` " +
                    "SET " +
                    "`nombre` = ?, " +
                    "`prioridad` = ? " +
                    "`estado` = ? " +
                    "WHERE `id` = ?; ";

            try (Connection conexion = BDD.getConnection();
                 PreparedStatement stmnt = conexion.prepareStatement(sql)) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, t.getNombre());
                stmnt.setInt(2, t.getPrioridad());
                stmnt.setString(3, t.getEstado().getNombre());

                stmnt.setInt(4, t.getId());

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

    public static boolean borrar(Tarea t) throws Exception {
        boolean estado = false;

        if (t != null) {

            String sql = "DELETE FROM tarea WHERE id = ?";

            try (Connection conexion = BDD.getConnection();
                 PreparedStatement stmnt = conexion.prepareStatement(sql)) {
               conexion.nativeSQL("START TRANSACTION;");
               stmnt.setInt(1, t.getId());

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

    public static Map<Integer, Tarea> getTareas() throws Exception {
        Map<Integer, Tarea> tareas = new LinkedHashMap<>();

        String sql = "TABLE tarea";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Tarea tarea = new Tarea(table.getInt("id"),
                        table.getString("nombre"),
                        table.getInt("prioridad"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );


                tareas.put(tarea.getId(), tarea);
            }

        }
        return tareas;
    }

    public static Map<Integer, Tarea> getTareas(Estado idEstado) throws Exception {
        Map<Integer, Tarea> tareas = new LinkedHashMap<>();

        String sql = "SELECT * FROM tarea WHERE estado = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            stmnt.setString(1, idEstado.getNombre());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Tarea tarea = new Tarea(table.getInt("id"),
                        table.getString("nombre"),
                        table.getInt("prioridad"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );


                tareas.put(tarea.getId(), tarea);
            }

        }
        return tareas;
    }

    public static Map<Integer, Tarea> getTareas(Estado idEstado, Proyecto idProyecto) throws Exception{
        Map<Integer, Tarea> tareas = new LinkedHashMap<>();

        String sql = "SELECT * FROM tarea WHERE estado = ? AND proyecto_ID = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            stmnt.setString(1, idEstado.getNombre());
            stmnt.setInt(2, idProyecto.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Tarea tarea = new Tarea(table.getInt("id"),
                        table.getString("nombre"),
                        table.getInt("prioridad"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );


                tareas.put(tarea.getId(), tarea);
            }

        }
        return tareas;
    }

    public static Map<Integer, Tarea> getTareas(Proyecto idProyecto) throws Exception {
        Map<Integer, Tarea> tareas = new LinkedHashMap<>();

        String sql = "SELECT * FROM tarea WHERE proyecto_ID = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            stmnt.setInt(1, idProyecto.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Tarea tarea = new Tarea(table.getInt("id"),
                        table.getString("nombre"),
                        table.getInt("prioridad"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );


                tareas.put(tarea.getId(), tarea);
            }

        }
        return tareas;
    }

    public static Tarea getTarea(int id) throws Exception {
        Tarea tarea = null;

        String sql = "SELECT * FROM tarea WHERE id = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                tarea = new Tarea(table.getInt("id"),
                        table.getString("nombre"),
                        table.getInt("prioridad"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID")));
            }

        }
        return tarea;
    }

    public static List<Tarea> getTareas(String nombre, Proyecto p, Estado e) throws Exception {
        List<Tarea> tareas = new LinkedList<>();

        String sql = "SELECT * FROM tarea WHERE nombre LIKE ? AND proyecto_ID = ? AND  estado = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            stmnt.setString(1, "%"+nombre+"%");
            stmnt.setInt(2, p.getId());
            stmnt.setString(3, e.getNombre());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                tareas.add(new Tarea(table.getInt("id"),
                        table.getString("nombre"),
                        table.getInt("prioridad"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))));
            }

        }
        return tareas;
    }

    public static int getMayorPrioridad(Proyecto p) throws Exception {
        int prioridad = 0;

        String sql = "SELECT prioridad FROM tarea WHERE proyecto_ID = ? ORDER BY prioridad DESC LIMIT 1;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {
            stmnt.setInt(1, p.getId());

            ResultSet table = stmnt.executeQuery();
            while (table.next()) {
                prioridad = table.getInt("prioridad");
            }


        }
        return prioridad;
    }
}
