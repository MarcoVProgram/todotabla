package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.utils.AppErrorHandler;

import java.sql.*;
import java.time.LocalDate;

import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Capa de datos del núcleo encargada del gobierno, creación y cierre de los Proyectos del sistema.
 * <p>
 * Implementa de forma nativa la captura de claves autogeneradas por hardware en el motor SQL primario.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class ProyetosBDD {
    /**
     * Inserta un nuevo proyecto en la base de datos de forma segura, inyectando el ID generado por el motor
     * de vuelta al objeto {@link Proyecto} recibido.
     *
     * @param p El objeto {@link Proyecto} inicializado desde el formulario de creación.
     * @return El entero correlativo asignado como ID único por el motor de base de datos; {@code -1} si la operación fracasa.
     * @throws Exception Si ocurre una colisión de datos o pérdida crítica de conectividad.
     */
    public static int insertar(Proyecto p) throws Exception{ // TODO obtener numero del id al insertar
        boolean estado = false;
        int clave = -1;

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(
                     "INSERT INTO proyecto VALUES (NULL, ?, ?, NULL)", Statement.RETURN_GENERATED_KEYS
             )
        ){

            stmnt.setString(1, p.getTitulo());
            stmnt.setDate(2, Date.valueOf(p.getFechaCreacion()));

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
     * Modifica las propiedades descriptivas o de finalización de un proyecto mediante transacciones seguras.
     *
     * @param p El objeto {@link Proyecto} modificado a persistir.
     * @return {@code true} si se actualizó y consolidó el registro; {@code false} si se revirtió.
     * @throws Exception Si falla el aislamiento transaccional del servidor SQL.
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
     * Archiva administrativamente un proyecto abierto fijando su fecha de clausura en el día actual.
     *
     * @param p El {@link Proyecto} a dar de baja.
     * @return {@code true} si el proyecto estaba abierto y fue archivado correctamente; {@code false} de lo contrario.
     * @throws Exception Si ocurre un error en la capa de datos.
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
     * Remueve de forma definitiva un proyecto del sistema a través de su identificador primario.
     *
     * @param p El objeto {@link Proyecto} a destruir.
     * @return {@code true} si la fila fue eliminada de la base de datos física; {@code false} en caso contrario.
     * @throws Exception Si existen dependencias activas de claves foráneas (como tareas e integrantes vinculados).
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
     * Recupera el catálogo completo de todos los proyectos desarrollados o en curso dentro de la aplicación.
     *
     * @return Un {@link Map} ordenado por inserción que indexa los proyectos mediante su identificador numérico.
     * @throws Exception Si ocurre un error en la consulta general de datos.
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
     * Busca y obtiene los datos completos de un único proyecto según su ID único.
     *
     * @param id Identificador numérico del proyecto buscado.
     * @return El objeto {@link Proyecto} hidratado si existe; {@code null} si el ID no corresponde a ningún registro.
     * @throws Exception Si falla la comunicación por red o el procesamiento del ResultSet.
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