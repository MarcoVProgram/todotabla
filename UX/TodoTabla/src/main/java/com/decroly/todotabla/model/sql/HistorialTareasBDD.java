package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.HistorialTareas;
import com.decroly.todotabla.model.Tarea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase de acceso a datos para la entidad {@link HistorialTareas}.
 * Proporciona operaciones de consulta y eliminación contra la tabla {@code historial_tareas}
 * de la base de datos. El historial se genera automáticamente mediante triggers y no admite inserción manual.
 */
public class HistorialTareasBDD {

    /**
     * Elimina un registro del historial de tareas de la base de datos.
     *
     * @param ht el registro de historial a eliminar
     * @return {@code true} si la eliminación afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static boolean borrar(HistorialTareas ht) throws Exception {
        boolean estado = false;

        if (ht != null) {
            String sql = "DELETE FROM historial_tareas WHERE id = ?";
            try (Connection conexion = BDD.getConnection();
                 PreparedStatement stmnt = conexion.prepareStatement(sql)) {

               conexion.nativeSQL("START TRANSACTION;");
               stmnt.setInt(1, ht.getId());

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

    /**
     * Recupera todos los registros del historial de tareas almacenados en la base de datos.
     *
     * @return un {@link Map} ordenado de ID a {@link HistorialTareas}
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static Map<Integer, HistorialTareas> getHistorialTareas() throws Exception {
        Map<Integer, HistorialTareas> historialTareas = new LinkedHashMap<>();

        String sql = "TABLE historial_tareas;";

        try (Connection conexion = BDD.getConnection();
                PreparedStatement stmnt = conexion.prepareStatement(sql)) {
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                HistorialTareas tarea = new HistorialTareas(table.getInt("id"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        TareasBDD.getTarea(table.getInt("tarea_id")),
                        table.getTimestamp("fecha_cambio").toLocalDateTime()
                );

                historialTareas.put(tarea.getId(), tarea);
            }

        }
        return historialTareas;
    }

    /**
     * Recupera todos los registros del historial asociados a una tarea concreta.
     *
     * @param tarea_id la tarea por la que filtrar
     * @return un {@link Map} ordenado de ID a {@link HistorialTareas}
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static Map<Integer, HistorialTareas> getHistorialTareas(Tarea tarea_id) throws Exception {
        Map<Integer, HistorialTareas> historialTareas = new LinkedHashMap<>();

        String sql = "SELECT * FROM historial_tareas WHERE tarea_id = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            stmnt.setInt(1, tarea_id.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                HistorialTareas tarea = new HistorialTareas(table.getInt("id"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        TareasBDD.getTarea(table.getInt("tarea_id")),
                        table.getTimestamp("fecha_cambio").toLocalDateTime()
                );

                historialTareas.put(tarea.getId(), tarea);
            }

        }

        return historialTareas;
    }

    /**
     * Recupera un registro concreto del historial a partir de su ID.
     *
     * @param id el identificador del registro a buscar
     * @return el {@link HistorialTareas} correspondiente, o {@code null} si no existe
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static HistorialTareas getHistorialTarea(int id) throws Exception {
        HistorialTareas tareas = null;

        String sql = "SELECT * FROM historial_tareas WHERE id = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {
            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                tareas = new HistorialTareas(table.getInt("id"),
                        EstadosBDD.getEstado(table.getString("estado")),
                        TareasBDD.getTarea(table.getInt("tarea_id")),
                        table.getTimestamp("fecha_cambio").toLocalDateTime()
                );
            }

        }

        return tareas;
    }
}