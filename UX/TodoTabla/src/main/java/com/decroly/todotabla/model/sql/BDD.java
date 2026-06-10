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

/**
 * Gestiona la conexión a la base de datos MySQL.
 * Lee los parámetros de conexión desde un fichero de configuración {@code config.dat}
 * ubicado en el mismo paquete que la clase.
 */
public class BDD {

    private static String url;
    private static String user;
    private static String password;

    private static final URL configURL = BDD.class.getResource("config.dat");

    /**
     * Crea y devuelve una nueva conexión a la base de datos.
     * Lee la dirección, nombre de usuario y contraseña línea a línea desde {@code config.dat}.
     *
     * @return una {@link Connection} activa a la base de datos
     * @throws IOException            si el fichero de configuración no se encuentra o no puede leerse
     * @throws SQLException           si ocurre un error al establecer la conexión JDBC
     * @throws ClassNotFoundException si el driver de MySQL no está disponible en el classpath
     * @throws URISyntaxException     si la URL del fichero de configuración tiene formato incorrecto
     */
    public static synchronized Connection getConnection() throws IOException, SQLException, ClassNotFoundException, URISyntaxException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (
                FileReader configFile = new FileReader(new File(configURL.toURI()));
                BufferedReader configReader = new BufferedReader(configFile);
            ) {

                url = "jdbc:mysql://" + configReader.readLine() + "/" + configReader.readLine();
                user = configReader.readLine();
                password = configReader.readLine();

                return DriverManager.getConnection(url, user, password);
            } catch (NullPointerException e) {
                throw new IOException(e);
            }
    }
}