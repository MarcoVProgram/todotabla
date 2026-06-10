package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Asignacion;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase de acceso a datos para la entidad {@link Asignacion}.
 * Proporciona operaciones CRUD y consultas filtradas contra la tabla {@code asignacion} de la base de datos.
 */
public class AsignacionesBDD {

    /**
     * Inserta una nueva asignación en la base de datos.
     *
     * @param i la asignación a insertar
     * @return {@code true} si la inserción afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Actualiza la fecha de fin de una asignación existente.
     *
     * @param i la asignación con la fecha de fin actualizada
     * @return {@code true} si la actualización afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Archiva una asignación asignándole la fecha de fin actual si aún no tiene ninguna.
     *
     * @param i la asignación a archivar
     * @return {@code true} si la asignación fue archivada correctamente, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Elimina una asignación de la base de datos.
     *
     * @param i la asignación a eliminar
     * @return {@code true} si la eliminación afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Recupera todas las asignaciones almacenadas en la base de datos.
     *
     * @return un {@link Map} ordenado de ID a {@link Asignacion}
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Recupera todas las asignaciones asociadas a una tarea concreta.
     *
     * @param tarea_ID la tarea por la que filtrar
     * @return un {@link Map} ordenado de ID a {@link Asignacion}
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Recupera las asignaciones activas de una tarea, excluyendo aquellas cuya fecha de fin
     * ya ha pasado.
     *
     * @param tarea_ID la tarea por la que filtrar
     * @return un {@link Map} ordenado de ID a {@link Asignacion} activas
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Recupera todas las asignaciones asociadas a un usuario concreto.
     *
     * @param usuario_ID el usuario por el que filtrar
     * @return un {@link Map} ordenado de ID a {@link Asignacion}
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Recupera las asignaciones activas de un usuario, es decir, aquellas sin fecha de fin registrada.
     *
     * @param usuario_ID el usuario por el que filtrar
     * @return un {@link Map} ordenado de ID a {@link Asignacion} activas
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Recupera una asignación concreta a partir de su ID.
     *
     * @param id el identificador de la asignación a buscar
     * @return la {@link Asignacion} correspondiente, o {@code null} si no existe
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Cierra una asignación activa entre un usuario y una tarea estableciendo la fecha de fin al día actual.
     * Solo afecta a asignaciones que aún no tienen fecha de fin registrada.
     *
     * @param idUsuario el ID del usuario cuya asignación se quiere cerrar
     * @param idTarea   el ID de la tarea asociada a la asignación
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
