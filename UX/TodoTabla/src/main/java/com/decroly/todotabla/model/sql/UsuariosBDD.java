package com.decroly.todotabla.model.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import com.decroly.todotabla.model.Usuario;

/**
 * Clase de acceso a datos para la entidad {@link Usuario}.
 * Proporciona operaciones CRUD y consultas de búsqueda contra la tabla {@code usuario} de la base de datos.
 */
public class UsuariosBDD {

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param m el usuario a insertar
     * @return {@code true} si la inserción afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Actualiza el nombre, los apellidos y el correo electrónico de un usuario existente.
     *
     * @param m el usuario con los datos actualizados
     * @return {@code true} si la actualización afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Elimina un usuario de la base de datos.
     *
     * @param m el usuario a eliminar
     * @return {@code true} si la eliminación afectó exactamente una fila, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Recupera todos los usuarios almacenados en la base de datos.
     *
     * @return un {@link Map} ordenado de ID a {@link Usuario}
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Recupera un usuario concreto a partir de su ID.
     *
     * @param id el identificador del usuario a buscar
     * @return el {@link Usuario} correspondiente
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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
     * Busca un usuario cuyo nombre contenga el texto indicado.
     *
     * @param nombre el texto parcial a buscar en el nombre del usuario
     * @return el primer {@link Usuario} que coincida con la búsqueda
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
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

    /**
     * Comprueba si un correo electrónico ya está registrado en la base de datos.
     *
     * @param correo el correo electrónico a verificar
     * @return {@code true} si el correo ya existe, {@code false} en caso contrario
     * @throws Exception si ocurre un error en la conexión o en la ejecución de la consulta
     */
    public static boolean correoExiste(String correo) throws Exception {

        boolean estado = false;

        try (Connection conexion = BDD.getConnection();
            PreparedStatement stmnt = conexion.prepareStatement(
                "SELECT * FROM usuario WHERE email LIKE ?;"
        )) {
            
            stmnt.setString(1, correo);

            ResultSet usuarios = stmnt.executeQuery();

            estado = usuarios.next();
        }

        return estado;
    }
}
