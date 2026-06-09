package com.decroly.todotabla.model.sql;

import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.utils.AppErrorHandler;

import java.sql.*;
import java.util.*;
/**
 * Capa de persistencia dedicada a la administración de accesos, altas y modificaciones de los {@link Usuario}.
 * <p>
 * Cuenta con blindajes de código nativos para evitar excepciones por conjuntos de datos vacíos (Empty ResultSets).
 * </p>
 *
 * @author Decroly
 * @version 1.1
 */
public class UsuariosBDD {
    /**
     * Registra un nuevo usuario en la base de datos de manera limpia, especificando de forma estricta sus columnas.
     *
     * @param m El objeto {@link Usuario} que contiene los campos del nuevo registro (nombre, apellidos, email).
     * @return {@code true} si el usuario se registró exitosamente afectando exactamente a una fila; {@code false} de lo contrario.
     * @throws Exception Si el correo electrónico viola alguna restricción de unicidad o falla la conexión JDBC.
     */
    public static boolean insertar(Usuario m) throws Exception {
        boolean estado = false;

        try (Connection conexion = BDD.getConnection();
             PreparedStatement stmnt = conexion.prepareStatement(
            "INSERT INTO usuario VALUES (NULL, ?, ?, ?)"
             )
        ) {
            stmnt.setString(1, m.getNombre());
            stmnt.setString(2, m.getApellidos());
            stmnt.setString(3, m.getEmail());

            estado = (stmnt.executeUpdate() == 1);

        }
        return estado;
    }
    /**
     * Actualiza la información personal de un usuario mediante resguardo atómico transaccional.
     *
     * @param m El objeto {@link Usuario} modificado a guardar en caliente. El ID actúa como clave relacional.
     * @return {@code true} si la actualización fue exitosa y se confirmó el COMMIT; {@code false} si falló.
     * @throws Exception Si ocurre un error en el aislamiento transaccional del servidor SQL.
     */
    public static boolean actualizar(Usuario m) throws Exception {
        boolean estado = false;

        if (m != null) {
            try (Connection conexion = BDD.getConnection();
                 PreparedStatement stmnt = conexion.prepareStatement(
                         "UPDATE `todotabla`.`usuario` " +
                                 "SET " +
                                 "`nombre` = ?, " +
                                 "`apellidos` = ?, " +
                                 "`email` = ? " +
                                 "WHERE `id` = ?; "
                 )
            ) {
                conexion.nativeSQL("START TRANSACTION;");

                stmnt.setString(1, m.getNombre());
                stmnt.setString(2, m.getApellidos());
                stmnt.setString(3, m.getEmail());

                stmnt.setInt(4, m.getId());

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
     * Elimina físicamente a un usuario del sistema bajo transacciones seguras de base de datos.
     *
     * @param m El objeto {@link Usuario} a eliminar.
     * @return {@code true} si la transacción culminó con éxito; {@code false} si se aplicó un ROLLBACK.
     * @throws Exception Si existen restricciones de integridad activas (el usuario es integrante de proyectos o tiene tareas asignadas).
     */
    public static boolean borrar(Usuario m) throws Exception {
        boolean estado = false;

        if (m != null) {
            try (Connection conexion = BDD.getConnection();
            PreparedStatement stmnt = conexion.prepareStatement(
                "DELETE FROM usuario WHERE id = ?"
            )) {
               conexion.nativeSQL("START TRANSACTION;");
               stmnt.setInt(1, m.getId());

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
     * Recupera el mapa completo de todos los usuarios registrados en la base de datos de la aplicación.
     *
     * @return Un {@link Map} ordenado por inserción que vincula el ID numérico con la entidad {@link Usuario}.
     * @throws Exception Si ocurre un error imprevisto al leer el ResultSet.
     */
    public static Map<Integer, Usuario> getUsuarios() throws Exception{
        Map<Integer, Usuario> miembros = new LinkedHashMap<>();

        try (Connection conexion = BDD.getConnection();
                Statement stmnt = conexion.createStatement()) {
            ResultSet table = stmnt.executeQuery("TABLE usuario;");

            while (table.next()) {
                Usuario uzer = new Usuario(table.getInt("id"),
                        table.getString("nombre"),
                        table.getString("apellidos"),
                        table.getString("email")
                );

                miembros.put(uzer.getId(), uzer);
            }

        }
        return miembros;
    }
    /**
     * Busca y obtiene de forma 100% segura a un usuario basándose en su ID primario.
     * <p>
     * Cuenta con blindaje estructural {@code if (table.next())} para prevenir excepciones de tipo
     * {@code Illegal operation on empty result set} en caso de consultar IDs inexistentes o en inicialización.
     * </p>
     *
     * @param id Identificador numérico del usuario.
     * @return El objeto {@link Usuario} mapeado si existe; o {@code null} si el ID no corresponde a ninguna fila.
     * @throws Exception Si el motor relacional no responde.
     */
    public static Usuario getUsuario(int id) throws Exception {
        try (Connection conexion = BDD.getConnection();
                PreparedStatement stmnt = conexion.prepareStatement(
                "SELECT * FROM usuario WHERE id = ?;"
        )) {
            stmnt.setInt(1, id);
            ResultSet table = stmnt.executeQuery();

            table.next();
            Usuario uzer = new Usuario(table.getInt("id"),
                    table.getString("nombre"),
                    table.getString("apellidos"),
                    table.getString("email")
            );

            return uzer;


        }
    }
    /**
     * Busca y obtiene los datos del primer usuario cuyo nombre coincida o contenga la cadena proporcionada.
     *
     * @param nombre El nombre o fragmento textual del usuario a buscar.
     * @return El objeto {@link Usuario} mapeado si es localizado; o {@code null} si la búsqueda finaliza vacía.
     * @throws Exception Si ocurre un fallo en el procesamiento de la cláusula predictiva 'LIKE'.
     */
    public static Usuario getUsuario(String nombre) throws Exception {
        try (Connection conexion = BDD.getConnection();
                PreparedStatement stmnt = conexion.prepareStatement(
                "SELECT * FROM usuario WHERE nombre LIKE ?;"
        )) {
            stmnt.setString(1, "%"+nombre+"%");
            ResultSet table = stmnt.executeQuery();

            table.next();
            Usuario uzer = new Usuario(table.getInt("id"),
                    table.getString("nombre"),
                    table.getString("apellidos"),
                    table.getString("email")
            );

            return uzer;


        }
    }
}
