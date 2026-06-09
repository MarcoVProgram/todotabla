package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Asignacion;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.utils.AppErrorHandler;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Capa de persistencia y acceso a datos para la entidad {@link Asignacion}.
 * <p>
 * Gestiona el ciclo de vida de las relaciones entre usuarios y tareas dentro de la base de datos,
 * incluyendo el control transaccional manual (ACID) para operaciones de actualización y borrado.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class AsignacionesBDD {
    /**
     * Registra una nueva asignación activa en el sistema.
     *
     * @param i Objeto {@link Asignacion} que contiene las entidades vinculadas y la fecha de inicio.
     * @return {@code true} si la asignación se registró correctamente; {@code false} en caso contrario.
     * @throws Exception Si ocurre un error en la conexión o en la sintaxis de la consulta SQL.
     */
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
    /**
     * Actualiza los datos de una asignación existente mediante control transaccional.
     *
     * @param i Objeto {@link Asignacion} con los datos actualizados, incluyendo su ID identificador.
     * @return {@code true} si la fila fue afectada y la transacción fue confirmada (COMMIT);
     * {@code false} si falló y se realizó un ROLLBACK.
     * @throws Exception Si se pierde la conexión o falla el motor transaccional.
     */
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
    /**
     * Da de baja una asignación activa estableciendo su fecha de finalización en el día actual.
     *
     * @param i Objeto {@link Asignacion} a archivar.
     * @return {@code true} si la asignación estaba activa y se archivó con éxito; {@code false} de lo contrario.
     * @throws Exception Si ocurre un error en la capa de datos subyacente.
     */
    public static boolean archivar(Asignacion i) throws Exception {
        boolean estado = false;

        if (i != null && i.getFechaFin() == null) {
            i.setFechaFin(LocalDate.now());
            estado = actualizar(i);
        }

        return estado;
    }
    /**
     * Elimina físicamente el registro de una asignación de la base de datos de manera transaccional.
     *
     * @param i Objeto {@link Asignacion} a eliminar.
     * @return {@code true} si la transacción culminó con éxito; {@code false} si se ejecutó un ROLLBACK.
     * @throws Exception Si ocurre un fallo crítico de SQL.
     */
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
    /**
     * Recupera la totalidad de las asignaciones registradas en el sistema.
     *
     * @return Un {@link Map} ordenado por inserción que mapea el ID de la asignación con su correspondiente objeto.
     * @throws Exception Si hay errores al mapear las claves foráneas de usuarios o tareas.
     */
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
    /**
     * Recupera el historial de asignaciones completo vinculado a una tarea específica.
     *
     * @param tarea_ID El objeto {@link Tarea} que actúa como filtro de búsqueda.
     * @return Un {@link Map} con las asignaciones de la tarea dada.
     * @throws Exception Si la consulta SQL o las subtareas de hidratación fallan.
     */
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
    /**
     * Obtiene únicamente las asignaciones vigentes (aquellas sin fecha de fin o vigentes a fecha actual) de una tarea.
     *
     * @param tarea_ID El objeto {@link Tarea} a consultar.
     * @return {@link Map} con las asignaciones que se encuentran activas en este momento.
     * @throws Exception Si ocurre un error en la comunicación con la BDD.
     */
    public static Map<Integer, Asignacion> getAsignacionesActivas(Tarea tarea_ID) throws Exception {
        Map<Integer, Asignacion> asignaciones = new LinkedHashMap<>();

        String sql = "SELECT * FROM asignacion WHERE tarea_ID = ? AND (fecha_fin is null OR fecha_fin > CURRENT_DATE());";

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
    /**
     * Recupera el histórico de todas las asignaciones en las que ha participado un usuario.
     *
     * @param usuario_ID El objeto {@link Usuario} evaluado.
     * @return {@link Map} indexado por ID de asignación pertenecientes al usuario.
     * @throws Exception Si el mapeo de relaciones genera un error.
     */
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
    /**
     * Obtiene las asignaciones en estado activo asociadas a un determinado usuario.
     *
     * @param usuario_ID El objeto {@link Usuario} objetivo.
     * @return {@link Map} con las asignaciones que el usuario tiene pendientes o ejecutando actualmente.
     * @throws Exception Si ocurre un fallo de lectura en la base de datos.
     */
    public static Map<Integer, Asignacion> getAsignacionsActivas(Usuario usuario_ID) throws Exception {
        Map<Integer, Asignacion> asignaciones = new LinkedHashMap<>();

        String sql = "SELECT * FROM asignacion WHERE usuario_ID = ? AND fecha_fin is null;";

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
    /**
     * Busca y extrae una asignación singular a partir de su identificador primario.
     *
     * @param id Identificador único numérico de la asignación.
     * @return El objeto {@link Asignacion} correspondiente si se encuentra, o {@code null} si no existe.
     * @throws Exception Si falla la consulta SQL.
     */
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
    /**
     * Realiza un proceso de desasignación directa "en caliente", finalizando la relación
     * laboral/técnica entre un usuario y una tarea específica en el día de hoy.
     *
     * @param idUsuario Identificador único del usuario.
     * @param idTarea   Identificador único de la tarea.
     * @throws Exception Si ocurre un error durante el proceso de actualización del registro.
     */
    public static void darDeBajaAsignacion(int idUsuario, int idTarea) throws Exception {
        String sql = "UPDATE asignacion SET fecha_fin = ? WHERE usuario_ID = ? AND tarea_ID = ? AND fecha_fin IS NULL";

        try (Connection con = BDD.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            ps.setInt(2, idUsuario);
            ps.setInt(3, idTarea);
            
            ps.executeUpdate();
        }
    }
}
