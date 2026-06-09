package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.HistorialTareas;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.utils.AppErrorHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Gestiona el registro histórico de los eventos y transiciones de estados ocurridos sobre las tareas.
 * <p>
 * Proporciona capacidades de auditoría interna para reconstruir la línea de tiempo de cambios de cada ítem.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class HistorialTareasBDD {
    /**
     * Elimina de manera transaccional un registro de auditoría concreto del historial de tareas.
     *
     * @param ht El objeto {@link HistorialTareas} que se desea eliminar.
     * @return {@code true} si la remoción fue consolidada; {@code false} si se descartó el cambio.
     * @throws Exception Si falla la integridad de la base de datos.
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
     * Extrae el historial global completo de todas las tareas monitorizadas por el software.
     *
     * @return Un {@link Map} ordenado con la traza histórica total registrada.
     * @throws Exception Si falla la hidratación de relaciones complejas anidadas.
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
     * Extrae de forma cronológica la traza completa de cambios de estado sufridos por una tarea en particular.
     *
     * @param tarea_id El objeto {@link Tarea} que se desea auditar.
     * @return Un {@link Map} que representa la línea de tiempo exclusiva de la tarea seleccionada.
     * @throws Exception Si ocurre un fallo en los subprocesos concurrentes de bases de datos.
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
     * Obtiene una única entrada de auditoría del historial de tareas mediante su ID primario.
     *
     * @param id Identificador numérico de la entrada del historial.
     * @return El objeto {@link HistorialTareas} mapeado, o {@code null} si el ID no corresponde a ningún evento.
     * @throws Exception Si se genera un error imprevisto en el controlador JDBC.
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