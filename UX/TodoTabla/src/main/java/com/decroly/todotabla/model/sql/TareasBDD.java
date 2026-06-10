package com.decroly.todotabla.model.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;

/**
 * Clase de acceso a datos para la entidad {@link Tarea}.
 * Proporciona operaciones CRUD y consultas filtradas contra la tabla {@code tarea} de la base de datos.
 */
public class TareasBDD {

    /**
     * Inserta una nueva tarea en la base de datos y devuelve el ID generado.
     *
     * @param t la tarea a insertar
     * @return el ID generado por la base de datos, o {@code -1} si la inserción falló
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static int insertar(Tarea t) throws Exception {
        boolean estado = false;
        int clave = -1;

        String sql = "INSERT INTO tarea VALUES (NULL, ?, ?, ?, ?)";
        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
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

    /**
     * Actualiza el nombre, la prioridad y el estado de una tarea existente.
     *
     * @param t la tarea con los datos actualizados
     * @return {@code true} si la actualización afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static boolean actualizar(Tarea t) throws Exception {
        boolean estado = false;

        if (t != null) {

            String sql = "UPDATE `todotabla`.`tarea` " +
                    "SET " +
                    "`nombre` = ?, " +
                    "`prioridad` = ?, " +
                    "`estado` = ? " +
                    "WHERE `id` = ?;";

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

    /**
     * Elimina una tarea de la base de datos.
     *
     * @param t la tarea a eliminar
     * @return {@code true} si la eliminación afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
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

    /**
     * Recupera todas las tareas almacenadas en la base de datos.
     *
     * @return un {@link Map} ordenado de ID a {@link Tarea}
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
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

    /**
     * Recupera todas las tareas que se encuentran en un estado concreto.
     *
     * @param idEstado el estado por el que filtrar
     * @return un {@link Map} ordenado de ID a {@link Tarea}
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
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

    /**
     * Recupera todas las tareas que coinciden con un estado y un proyecto concretos.
     *
     * @param idEstado   el estado por el que filtrar
     * @param idProyecto el proyecto por el que filtrar
     * @return un {@link Map} ordenado de ID a {@link Tarea}
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
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

    /**
     * Recupera todas las tareas pertenecientes a un proyecto concreto.
     *
     * @param idProyecto el proyecto por el que filtrar
     * @return un {@link Map} ordenado de ID a {@link Tarea}
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
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

    /**
     * Recupera una tarea concreta a partir de su ID.
     *
     * @param id el identificador de la tarea a buscar
     * @return la {@link Tarea} correspondiente, o {@code null} si no existe
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
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

    /**
     * Busca tareas por nombre parcial dentro de un proyecto y estado determinados.
     *
     * @param nombre el texto a buscar dentro del nombre de la tarea
     * @param p      el proyecto en el que buscar
     * @param e      el estado por el que filtrar
     * @return una {@link List} de {@link Tarea} que coinciden con los criterios
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
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

    /**
     * Devuelve el valor de prioridad más alto registrado entre las tareas de un proyecto.
     *
     * @param p el proyecto del que obtener la mayor prioridad
     * @return el valor de prioridad más alto, o {@code 0} si el proyecto no tiene tareas
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
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

    /**
     * Comprueba cuántas tareas existen con un nombre exacto dentro de un proyecto.
     * Útil para validar duplicados antes de insertar.
     *
     * @param nombreTarea el nombre exacto de la tarea a comprobar
     * @param idProyecto  el proyecto en el que buscar
     * @return el número de tareas encontradas con ese nombre en el proyecto
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static int tareaExiste(String nombreTarea, Proyecto idProyecto) throws Exception{
        int counter = 0;

        String sql = "SELECT DISTINCT id FROM tarea WHERE nombre = ? AND proyecto_ID = ?;";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {

            stmnt.setString(1, nombreTarea);
            stmnt.setInt(2, idProyecto.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                counter++;
            }

        }
        return counter;
    }
}
