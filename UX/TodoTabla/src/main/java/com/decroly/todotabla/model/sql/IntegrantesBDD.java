package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.utils.AppErrorHandler;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Controla el acceso a datos y la persistencia de las membresías de los usuarios en los proyectos.
 * <p>
 * Administra los roles internos del equipo, fechas de alta y bajas administrativas de personal en cada {@link Proyecto}.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class IntegrantesBDD {
    /**
     * Vincula a un usuario dentro de un proyecto asignándole un rol inicial.
     *
     * @param i El objeto {@link Integrante} que detalla el nexo corporativo.
     * @return {@code true} si el usuario fue dado de alta en el proyecto correctamente; {@code false} de lo contrario.
     * @throws Exception Si ocurre un fallo de clave foránea o problemas de conectividad con el servidor SQL.
     */
    public static boolean insertar(Integrante i) throws Exception {
        boolean estado = false;

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(
            "INSERT INTO integrante VALUES (NULL, ?, ?, NULL, ?, ?)"
             )
        ) {
            stmnt.setString(1, i.getRol());
            stmnt.setDate(2, Date.valueOf(i.getFechaEntrada()));
            stmnt.setInt(3, i.getIdUsuario().getId());
            stmnt.setInt(4, i.getIdProyecto().getId());

            estado = (stmnt.executeUpdate() == 1);

        }

        return estado;
    }
    /**
     * Modifica transaccionalmente las propiedades de participación (como rol o fecha de desvinculación) de un integrante.
     *
     * @param i El objeto {@link Integrante} con las propiedades modificadas.
     * @return {@code true} si el proceso se completó; {@code false} en caso de descarte o fallo.
     * @throws Exception Si ocurre un error fatal en la base de datos.
     */
    public static boolean actualizar(Integrante i) throws Exception {
        boolean estado = false;

        if (i != null) {
            try (Connection conexion = BDD.getConnection();
                 PreparedStatement stmnt = conexion.prepareStatement(
                         "UPDATE `todotabla`.`integrante` " +
                                 "SET " +
                                 "`rol` = ?, " +
                                 "`fecha_salida` = ? " +
                                 "WHERE `id` = ?"
                 )
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, i.getRol());
                stmnt.setDate(2, Date.valueOf(i.getFechaSalida()));

                stmnt.setInt(3, i.getId());

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
     * Borra permanentemente el registro de un participante mediante transacciones atómicas.
     *
     * @param i El objeto {@link Integrante} a purgar.
     * @return {@code true} si la fila se borró del sistema; {@code false} si falló.
     * @throws Exception Si existen dependencias relacionales activas.
     */
    public static boolean borrar(Integrante i) throws Exception {
        boolean estado = false;

        if (i != null) {
            try (Connection conexion = BDD.getConnection();
            PreparedStatement stmnt = conexion.prepareStatement(
                "DELETE FROM integrante WHERE id = ?"
            )) {
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
     * Obtiene el listado total histórico de todas las afiliaciones y membresías del ecosistema de la aplicación.
     *
     * @return Un {@link Map} secuencial con todos los integrantes registrados.
     * @throws Exception Si falla la consulta o el proceso interno de reconstrucción orientada a objetos.
     */
    public static Map<Integer, Integrante> getIntegrantes() throws Exception {
        Map<Integer, Integrante> integrantes = new LinkedHashMap<>();

        try (Connection conexion = BDD.getConnection();
                Statement stmnt = conexion.createStatement()) {
            ResultSet table = stmnt.executeQuery("TABLE integrante;");

            while (table.next()) {
                Date rawDate = table.getDate("fecha_salida");
                LocalDate dateExit = null;
                if (rawDate != null) {
                    dateExit = rawDate.toLocalDate();
                }

                Integrante integrante = new Integrante(table.getInt("id"),
                        table.getString("rol"),
                        table.getDate("fecha_entrada").toLocalDate(),
                        dateExit,
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );


                integrantes.put(integrante.getId(), integrante);
            }

        }
        return integrantes;
    }
    /**
     * Obtiene la nómina de integrantes (activos y dados de baja) asignados a un proyecto específico.
     *
     * @param proyecto_ID El {@link Proyecto} objetivo de filtrado.
     * @return Un {@link Map} con los miembros adscritos a dicho proyecto.
     * @throws Exception Si el motor SQL encuentra una inconsistencia de datos.
     */
    public static Map<Integer, Integrante> getIntegrantes(Proyecto proyecto_ID) throws Exception {
        String sql = "SELECT * FROM integrante WHERE proyecto_ID = ?;";
        return getIntegerIntegranteMap(proyecto_ID, sql);
    }
    /**
     * Recupera exclusivamente los integrantes en estado activo (sin fecha de salida o posterior a hoy) de un proyecto.
     *
     * @param proyecto_ID El {@link Proyecto} a verificar.
     * @return {@link Map} que contiene la fuerza laboral actual y activa del proyecto.
     * @throws Exception Si falla el filtro de fechas o la comunicación por red.
     */
    public static Map<Integer, Integrante> getIntegrantesActivos(Proyecto proyecto_ID) throws Exception {
        String sql = "SELECT * FROM integrante WHERE proyecto_ID = ? AND (fecha_salida IS null OR fecha_salida > CURRENT_DATE());";
        return getIntegerIntegranteMap(proyecto_ID, sql);
    }
    /**
     * Método auxiliar privado de abstracción para la reutilización de consultas de mapeo de integrantes.
     */
    private static Map<Integer, Integrante> getIntegerIntegranteMap(Proyecto proyecto_ID, String sql) throws Exception {
        Map<Integer, Integrante> integrantes = new LinkedHashMap<>();

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(sql)) {
            stmnt.setInt(1, proyecto_ID.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Date rawDate = table.getDate("fecha_salida");
                LocalDate dateExit = null;
                if (rawDate != null) {
                    dateExit = rawDate.toLocalDate();
                }

                Integrante integrante = new Integrante(table.getInt("id"),
                        table.getString("rol"),
                        table.getDate("fecha_entrada").toLocalDate(),
                        dateExit,
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );

                integrantes.put(integrante.getId(), integrante);
            }

        }
        return integrantes;
    }
    /**
     * Obtiene los registros de membresía de un usuario a lo largo de los diferentes proyectos de la empresa.
     *
     * @param usuario_ID El objeto {@link Usuario} del cual se desea extraer el currículum del proyecto.
     * @return Un {@link Map} que detalla la participación del usuario en la plataforma.
     * @throws Exception Si el motor JDBC falla.
     */
    public static Map<Integer, Integrante> getIntegrantes(Usuario usuario_ID) throws Exception {
        Map<Integer, Integrante> integrantes = new LinkedHashMap<>();

        try (Connection conexion = BDD.getConnection();
                PreparedStatement stmnt = conexion.prepareStatement("SELECT * FROM integrante WHERE usuario_ID = ?;")) {
            stmnt.setInt(1, usuario_ID.getId());
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Date rawDate = table.getDate("fecha_salida");
                LocalDate dateExit = null;
                if (rawDate != null) {
                    dateExit = rawDate.toLocalDate();
                }

                Integrante integrante = new Integrante(table.getInt("id"),
                        table.getString("rol"),
                        table.getDate("fecha_entrada").toLocalDate(),
                        dateExit,
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );

                integrantes.put(integrante.getId(), integrante);
            }

        }
        return integrantes;
    }
    /**
     * Extrae un integrante singular utilizando su clave primaria numérica.
     *
     * @param id Clave primaria del integrante.
     * @return Objeto {@link Integrante} completamente estructurado, u {@code null} si la búsqueda queda vacía.
     * @throws Exception Si ocurre un fallo grave en la comunicación relacional.
     */
    public static Integrante getIntegrante(int id) throws Exception {
        Integrante integrante = null;

        try (Connection conexion = BDD.getConnection();
                PreparedStatement stmnt = conexion.prepareStatement("SELECT * FROM integrante WHERE id = ?;")) {
            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            while (table.next()) {
                Date rawDate = table.getDate("fecha_salida");
                LocalDate dateExit = null;
                if (rawDate != null) {
                    dateExit = rawDate.toLocalDate();
                }

                integrante = new Integrante(table.getInt("id"),
                        table.getString("rol"),
                        table.getDate("fecha_entrada").toLocalDate(),
                        dateExit,
                        UsuariosBDD.getUsuario(table.getInt("usuario_ID")),
                        ProyetosBDD.getProyecto(table.getInt("proyecto_ID"))
                );
            }

        }

        return integrante;
    }
}
