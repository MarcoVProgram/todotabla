package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Estado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Clase de acceso a datos para la entidad {@link Estado}.
 * Proporciona operaciones CRUD contra la tabla {@code estado} de la base de datos.
 */
public class EstadosBDD {

    /**
     * Inserta un nuevo estado en la base de datos.
     *
     * @param e el estado a insertar
     * @return {@code true} si la inserción afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Actualiza el color y el orden de un estado existente identificado por su nombre.
     *
     * @param e el estado con los datos actualizados
     * @return {@code true} si la actualización afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Elimina un estado de la base de datos identificado por su nombre.
     *
     * @param e el estado a eliminar
     * @return {@code true} si la eliminación afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Recupera todos los estados almacenados en la base de datos.
     *
     * @return una {@link List} de {@link Estado} con todos los registros encontrados
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Recupera un estado concreto a partir de su nombre.
     *
     * @param estadoNombre el nombre del estado a buscar
     * @return el {@link Estado} correspondiente, o {@code null} si no existe
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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