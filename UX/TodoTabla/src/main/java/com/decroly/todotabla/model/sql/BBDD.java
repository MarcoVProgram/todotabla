package com.decroly.todotabla.model.sql;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BBDD {
    private static Connection conexion = null;

    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc");

        if (conexion == null) {
            conexion = DriverManager.getConnection("", "", "");
        }
        return conexion;
    }

    public static Connection getConnection(boolean restart)  {
        try {
            if (conexion != null) {
                if (restart || conexion.isValid(7200)) {
                    conexion = DriverManager.getConnection("", "", "");
                }
            } else {
                return getConnection();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return conexion;
    }
}
