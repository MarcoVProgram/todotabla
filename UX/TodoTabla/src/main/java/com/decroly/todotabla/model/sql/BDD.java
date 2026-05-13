package com.decroly.todotabla.model.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDD {
    private static Connection conexion = null;

    private static String url;
    private static String user;
    private static String password;

    private static final URL configURL = BDD.class.getResource("config.dat");

    private static synchronized Connection getConnection() throws IOException, SQLException, ClassNotFoundException, URISyntaxException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        if (configURL != null) {

            try (
                FileReader configFile = new FileReader(new File(configURL.toURI()));
                BufferedReader configReader = new BufferedReader(configFile);
            ) {

                url = "jdbc:mysql://" + configReader.readLine() + "/" + configReader.readLine();
                user = configReader.readLine();
                password = configReader.readLine();

                if (conexion == null) {
                    conexion = DriverManager.getConnection(url, user, password);
                }
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
        return conexion;
    }

    public static Connection getConnection(boolean restart) throws Exception {
        try {
            if (conexion != null) {
                if (restart || !conexion.isValid(5)) {
                    conexion = getConnection();
                }
            } else {
                return getConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Fallo al acceder a la base de datos",e);
            // return null;
        }
        return conexion;
    }
}
