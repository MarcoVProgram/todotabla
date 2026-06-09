package com.decroly.todotabla.model.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import com.decroly.todotabla.model.Usuario;

public class UsuariosBDD {
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
