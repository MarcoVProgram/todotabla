package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.utils.AppErrorHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
/**
 * Capa de persistencia encargada de gestionar los flujos de estados (Kanban) en la base de datos.
 * <p>
 * Permite administrar las columnas visuales y lógicas de los flujos del programa mapeando la entidad {@link Estado}.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class EstadosBDD {
    /**
     * Registra un nuevo estado disponible para las tareas del tablero.
     *
     * @param e El objeto {@link Estado} con sus propiedades base definidas.
     * @return {@code true} si la operación modificó las filas con éxito; {@code false} de lo contrario.
     * @throws Exception Si el estado duplica una clave primaria existente o hay errores de conexión.
     */
    public static boolean insertar(Estado e) throws Exception {
        boolean estado = false;

        String sql = "INSERT INTO estado VALUES (?, ?, ?)";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)
        ) {
            stmnt.setString(1, e.getNombre());
            stmnt.setString(2, e.getColor());
            stmnt.setInt(3, e.getOrden());

            estado = (stmnt.executeUpdate() == 1);

        }

        return estado;
    }
    /**
     * Actualiza de manera transaccional los valores cosméticos u ordinales de un estado.
     *
     * @param e El objeto {@link Estado} a modificar. El campo 'nombre' actúa como criterio de búsqueda rígido.
     * @return {@code true} si se actualizó y consolidó la transacción; {@code false} si falló.
     * @throws Exception Si ocurre un fallo en la base de datos.
     */
    public static boolean actualizar(Estado e) throws Exception {
        boolean estado = false;

        if (e != null) {
            String sql = "UPDATE `todotabla`.`estado` " +
                    "SET " +
                    "`color` = ?, " +
                    "`orden` = ? " +
                    "WHERE `nombre` = ?; ";
            try (Connection conexion = BDD.getConnection();
                 PreparedStatement stmnt = conexion.prepareStatement(sql)
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, e.getColor());
                stmnt.setInt(2, e.getOrden());

                stmnt.setString(3, e.getNombre());

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
     * Elimina permanentemente un estado de la base de datos bajo un entorno transaccional protegido.
     *
     * @param e El objeto {@link Estado} cuyo nombre definirá la fila a remover.
     * @return {@code true} si la fila fue borrada con éxito; {@code false} si hubo rollback.
     * @throws Exception Si existen restricciones de integridad referencial (claves foráneas activas).
     */
    public static boolean borrar(Estado e) throws Exception {
        boolean estado = false;

        if (e != null) {
            String sql = "DELETE FROM `todotabla`.`estado` " +
                    "WHERE `nombre` = ?; ";
            try (Connection conexion = BDD.getConnection();
                 PreparedStatement stmnt = conexion.prepareStatement(sql)
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, e.getNombre());

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
     * Recupera la colección completa de estados definidos en el sistema ordenados según el flujo interno.
     *
     * @return Una {@link List} secuencial de objetos {@link Estado}.
     * @throws Exception Si ocurre un error en la ejecución de la consulta.
     */
    public static List<Estado> getEstados() throws Exception {
        List<Estado> estados = new LinkedList<>();

        String sql = "SELECT * FROM `todotabla`.`estado` ";

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql);
             ResultSet rs = stmnt.executeQuery();
        ) {

            while (rs.next()) {
                Estado newEstado = new Estado(rs.getString("nombre"),
                        rs.getString("color"),
                        rs.getInt("orden"));

                estados.add(newEstado);
            }
        }
        return estados;
    }
    /**
     * Recupera de forma segura un estado específico de la tabla filtrado por su nombre único.
     *
     * @param estadoNombre Cadena de caracteres que identifica al estado.
     * @return El objeto {@link Estado} mapeado si existe en el sistema; {@code null} en caso contrario.
     * @throws Exception Si ocurre un error de comunicación con el servidor de la BDD.
     */
    public static Estado getEstado(String estadoNombre) throws Exception {
        Estado estado = null;

        String sql = "SELECT * FROM `todotabla`.`estado` WHERE nombre = ? ";
        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql);
        ) {

            stmnt.setString(1, estadoNombre);
            ResultSet rs = stmnt.executeQuery();
            
            while (rs.next()) {
                estado = new Estado(rs.getString("nombre"),
                        rs.getString("color"),
                        rs.getInt("orden"));
            }

        }

        return estado;
    }
}