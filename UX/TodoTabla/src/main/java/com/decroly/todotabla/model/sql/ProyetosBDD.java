package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Proyecto;

import java.sql.*;
import java.time.LocalDate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase de acceso a datos para la entidad {@link Proyecto}.
 * Proporciona operaciones CRUD contra la tabla {@code proyecto} de la base de datos.
 */
public class ProyetosBDD {

    /**
     * Inserta un nuevo proyecto en la base de datos y asigna el ID generado al objeto.
     *
     * @param p el proyecto a insertar
     * @return el ID generado por la base de datos, o {@code -1} si la inserción falló
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static int insertar(Proyecto p) throws Exception{
        boolean estado = false;
        int clave = -1;

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(
                     "INSERT INTO proyecto VALUES (NULL, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
             )
        ){

            stmnt.setString(1, p.getTitulo());
            stmnt.setDate(2, Date.valueOf(p.getFechaCreacion()));

            if (p.getFechaCierre() != null) {
                stmnt.setDate(3, Date.valueOf(p.getFechaCierre()));
            } else {
                stmnt.setNull(3, Types.DATE);
            }

            estado = (stmnt.executeUpdate() == 1);

            if (estado) {
                ResultSet keys = stmnt.getGeneratedKeys();
                while (keys.next()) {
                    clave = keys.getInt(1);
                    p.setId(clave);
                }
            }

        return clave;

        }
    }

    /**
     * Actualiza el título y la fecha de cierre de un proyecto existente.
     *
     * @param p el proyecto con los datos actualizados
     * @return {@code true} si la actualización afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static boolean actualizar(Proyecto p) throws Exception {
        boolean estado = false;

        if (p != null) {
            try (Connection conexion = BDD.getConnection();
                 PreparedStatement stmnt = conexion.prepareStatement(
                         "UPDATE `todotabla`.`proyecto` " +
                                 "SET " +
                                 "`titulo` = ?, " +
                                 "`fecha_cierre` = ? " +
                                 "WHERE `id` = ?; "
                 )
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, p.getTitulo());
                if (p.getFechaCierre() != null) {
                    stmnt.setDate(2, Date.valueOf(p.getFechaCierre()));
                } else {
                    stmnt.setNull(2, Types.DATE);
                }

                stmnt.setInt(3, p.getId());

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
     * Archiva un proyecto asignándole la fecha de cierre actual si aún no tiene ninguna.
     *
     * @param p el proyecto a archivar
     * @return {@code true} si el proyecto fue archivado correctamente, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static boolean archivar(Proyecto p) throws Exception {
        boolean estado = false;

        if (p != null && p.getFechaCierre() == null) {
            p.setFechaCierre(LocalDate.now());
            actualizar(p);
        }

        return estado;
    }

    /**
     * Elimina un proyecto de la base de datos.
     *
     * @param p el proyecto a eliminar
     * @return {@code true} si la eliminación afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static boolean borrar(Proyecto p) throws Exception {
        if (p != null) {
            try (Connection  conexion = BDD.getConnection();
                    PreparedStatement stmnt = conexion.prepareStatement("" +
                    "DELETE FROM `todotabla`.`proyecto` " +
                    "WHERE id = ?;\n")
            ) {
                stmnt.setInt(1, p.getId());

                return (stmnt.executeUpdate() == 1);
            }
        }
        return false;
    }

    /**
     * Recupera todos los proyectos almacenados en la base de datos.
     *
     * @return un {@link Map} ordenado de ID a {@link Proyecto}
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static Map<Integer, Proyecto> getProyectos() throws Exception {
        Map<Integer, Proyecto> proyectos = new LinkedHashMap<>();

        try (Connection conexion = BDD.getConnection();
                Statement stmnt = conexion.createStatement()) {
            ResultSet table = stmnt.executeQuery("TABLE proyecto;");

            while (table.next()) {
                Date rawDate = table.getDate("fecha_cierre");
                LocalDate dateClose = null;
                if (rawDate != null) {
                    dateClose = rawDate.toLocalDate();
                }

                Proyecto pro = new Proyecto(table.getInt("id"),
                        table.getString("titulo"),
                        table.getDate("fecha_creacion").toLocalDate(),
                        dateClose
                );

                proyectos.put(pro.getId(), pro);
            }

        }

        return proyectos;
    }

    /**
     * Recupera un proyecto concreto a partir de su ID.
     *
     * @param id el identificador del proyecto a buscar
     * @return el {@link Proyecto} correspondiente, o {@code null} si no existe
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static Proyecto getProyecto(int id) throws Exception{
        Proyecto proyecto = null;

        try (Connection conexion = BDD.getConnection();
                PreparedStatement stmnt = conexion.prepareStatement("SELECT * FROM proyecto WHERE id = ?;")) {
            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Date rawDate = table.getDate("fecha_cierre");
                LocalDate dateClose = null;
                if (rawDate != null) {
                    dateClose = rawDate.toLocalDate();
                }

                proyecto = new Proyecto(table.getInt("id"),
                        table.getString("titulo"),
                        table.getDate("fecha_creacion").toLocalDate(),
                        dateClose
                );
            }

        }

        return proyecto;
    }

}