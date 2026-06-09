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
 * Núcleo operativo del motor Kanban encargado de administrar las operaciones CRUD de las {@link Tarea}.
 * <p>
 * Gestiona prioridades, búsquedas inteligentes indexadas por texto, filtros cruzados por estados y proyectos.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class TareasBDD {
    /**
     * Inserta una nueva tarea operativa dentro de un proyecto, capturando el ID autoincremental de forma atómica.
     *
     * @param t El objeto {@link Tarea} parametrizado desde la interfaz de usuario.
     * @return El ID generado automáticamente por la base de datos si tiene éxito; {@code -1} si la inserción falla.
     * @throws Exception Si el estado asignado o el proyecto padre no existen en el sistema.
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
     * Modifica los datos principales y flujos de una tarea mediante control de transacciones aislado.
     *
     * @param t Objeto {@link Tarea} con las modificaciones a realizar.
     * @return {@code true} si se realizó el cambio con éxito; {@code false} si ocurrió un error y se revirtió.
     * @throws Exception Si se interrumpe la comunicación con el servidor relacional.
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
     * Elimina físicamente una tarea del tablero Kanban utilizando resguardos atómicos transaccionales.
     *
     * @param t El objeto {@link Tarea} que se desea eliminar de la aplicación.
     * @return {@code true} si el registro fue eliminado satisfactoriamente; {@code false} de lo contrario.
     * @throws Exception Si la tarea posee registros vinculados en el historial o asignaciones debido a restricciones FK.
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
     * Recupera el inventario absoluto de todas las tareas del servidor sin importar su proyecto de pertenencia.
     *
     * @return Un {@link Map} indexado por ID con todas las tareas almacenadas.
     * @throws Exception Si ocurre un fallo en la deserialización relacional u objetos nulos.
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
     * Filtra y obtiene todas las tareas globales que compartan un determinado estado Kanban.
     *
     * @param idEstado El objeto {@link Estado} que dictará la condición de filtrado.
     * @return Un {@link Map} estructurado con las tareas que cumplen con dicho estado.
     * @throws Exception Si ocurre un error al procesar la sentencia SQL parametrizada.
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
     * Ejecuta una consulta de filtro cruzado de alta especificidad obteniendo las tareas pertenecientes a un proyecto
     * y un estado concretos simultáneamente.
     *
     * @param idEstado   El {@link Estado} Kanban por el cual filtrar.
     * @param idProyecto El {@link Proyecto} dueño de las tareas.
     * @return {@link Map} optimizado con las tareas resultantes del doble filtro.
     * @throws Exception Si hay fallos en la lectura JDBC.
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
     * Obtiene el listado total de tareas adscritas de forma exclusiva a un proyecto.
     *
     * @param idProyecto El objeto {@link Proyecto} a desglosar.
     * @return {@link Map} ordenado con la lista de tareas asignadas al proyecto.
     * @throws Exception Si ocurre un fallo en los submapeos de datos.
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
     * Recupera una única tarea a partir de su clave primaria identificadora.
     *
     * @param id Identificador numérico secuencial de la tarea.
     * @return La entidad {@link Tarea} construida si se localiza; {@code null} si el ID no existe en la base de datos.
     * @throws Exception Si la base de datos se encuentra fuera de servicio.
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
     * Realiza una búsqueda avanzada y predictiva por coincidencia de caracteres (Fuzzy Text Search) sobre el nombre
     * de las tareas en un determinado escenario de proyecto y estado.
     *
     * @param nombre Cadena de texto o fragmento de nombre a buscar en el sistema.
     * @param p      El {@link Proyecto} delimitador del alcance de búsqueda.
     * @param e      El {@link Estado} actual que debe poseer la tarea buscada.
     * @return Una {@link List} dinámica con todas las tareas que contienen la subcadena de texto.
     * @throws Exception Si el motor relacional falla al procesar la sentencia comodín 'LIKE'.
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
     * Obtiene el valor numérico más alto de prioridad registrado dentro de un proyecto. Útil para la auto-ordenación
     * al instanciar nuevas tareas (colas FIFO/LIFO).
     *
     * @param p El {@link Proyecto} analizado.
     * @return El número entero representativo de la prioridad máxima; {@code 0} si el proyecto no tiene tareas creadas todavía.
     * @throws Exception Si la consulta estructurada devuelve un error.
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
     * Verifica de manera predictiva e inequívoca si existe una tarea registrada con un nombre idéntico dentro de un proyecto.
     * Previene la duplicidad indeseada de registros operacionales en la vista Kanban.
     *
     * @param nombreTarea El nombre literal a comprobar.
     * @param idProyecto  El {@link Proyecto} en el cual buscar la colisión de nombres.
     * @return Entero representativo de las ocurrencias encontradas (0 si no existe duplicidad, mayor a 0 si ya está registrada).
     * @throws Exception Si falla el procesamiento del conjunto de datos relacional.
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
