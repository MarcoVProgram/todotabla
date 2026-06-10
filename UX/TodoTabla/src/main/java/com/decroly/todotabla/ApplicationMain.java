package com.decroly.todotabla;

import com.decroly.todotabla.model.sql.BDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Punto de entrada de la aplicación JavaFX.
 * Verifica la conexión a la base de datos antes de cargar la interfaz principal
 * y cierra la aplicación si la conexión no puede establecerse.
 */
public class ApplicationMain extends Application {

    /**
     * Inicializa y muestra la ventana principal de la aplicación.
     * Si la verificación de conexión falla, la aplicación se cierra antes de cargar la interfaz.
     *
     * @param stage el {@link Stage} principal proporcionado por JavaFX
     * @throws IOException si el fichero FXML no se encuentra o no puede cargarse
     */
    @Override
    public void start(Stage stage) throws IOException {
        if (launchTest()) return;

        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationMain.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500,800);
        stage.setTitle("Bienvenido a TodoTabla!");
        stage.setScene(scene);

        if(stage.isFocused()){
            stage.setAlwaysOnTop(true);
        }else{
            stage.setAlwaysOnTop(false);
        }

        stage.setResizable(false);
        stage.show();
    }

    /**
     * Verifica que la conexión a la base de datos es válida antes de arrancar la aplicación.
     * En caso de error muestra un diálogo descriptivo y marca el arranque como inválido.
     *
     * @return {@code true} si la verificación falló y la aplicación debe cerrarse,
     *         {@code false} si la conexión es correcta y el arranque puede continuar
     */
    private boolean launchTest() {
        boolean invalid = false;
        try {
            Connection x = BDD.getConnection();
            x.close();
        } catch (SQLException e) {
            AppErrorHandler.manejar(e, "getConnection (Syntax SQL)");
            invalid = true;
        } catch (IOException e) {
            AppErrorHandler.manejar(e, "getConnection (IO)");
            invalid = true;
        } catch (URISyntaxException e) {
            AppErrorHandler.manejar(e, "getConnection (URI)");
            invalid = true;
        } catch (ClassNotFoundException e) {
            AppErrorHandler.manejar(e, "getConnection (ClassNotFound)");
            invalid = true;
        }
        if (invalid) {
            exitProgram();
            return true;
        }
        return false;
    }

    /**
     * Cierra la aplicación JavaFX de forma ordenada.
     */
    private void exitProgram() {
        Platform.exit();
    }

    /**
     * Método principal que lanza la aplicación.
     *
     * @param args argumentos de línea de comandos, no utilizados
     */
    public static void main(String[] args) {
        launch();
    }
}