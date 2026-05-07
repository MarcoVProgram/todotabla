package com.decroly.todotabla.model.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;


public class BBDD {
    private static Connection conexion = null;

    private static String url;
    private static String user;
    private static String password;

    private static final URL configURL = BBDD.class.getResource("config.dat");

    private static Connection getConnection() throws Exception {
        Class.forName("com.mysql.jdbc");

        if (configURL != null) {

            FileReader configFile = new FileReader(new File(configURL.toURI()));
            BufferedReader configReader = new BufferedReader(configFile);

            url = "jdbc:mysql:" + "//" + configReader.readLine() + "/" + configReader.readLine();
            user = configReader.readLine();
            password = configReader.readLine();

            if (conexion == null) {
                conexion = DriverManager.getConnection(url, user, password);
            }
        }
        return conexion;
    }

    public static Connection getConnection(boolean restart)  {
        try {
            if (conexion != null) {
                if (restart || conexion.isValid(7200)) {
                    conexion = DriverManager.getConnection(url, user, password);
                }
            } else {
                return getConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return conexion;
    }
}
