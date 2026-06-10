package com.decroly.todotabla.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestiona los errores de la aplicación registrándolos en el log y mostrando
 * un diálogo de error al usuario. Es seguro llamarlo desde cualquier hilo.
 */
public class AppErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(AppErrorHandler.class.getName());

    /**
     * Registra la excepción en el log y muestra un diálogo de error con el mensaje correspondiente.
     * Si se llama desde un hilo que no es el de la aplicación JavaFX, delega la alerta
     * a {@link Platform#runLater}.
     *
     * @param ex              la excepción producida
     * @param metodoCulpable  nombre del método donde se originó el error, usado en el encabezado del diálogo
     */
    public static void manejar(Exception ex, String metodoCulpable) {
        logger.error("Error en: {}", metodoCulpable, ex);

        Runnable showAlert = () -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add(
                AppErrorHandler.class.getResource("/com/decroly/todotabla/style.css").toExternalForm()
            );
            alert.getDialogPane().getStyleClass().add("error-alert");

            alert.setTitle("Error");
            alert.setHeaderText("Error en: " + metodoCulpable);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            showAlert.run();
        } else {
            Platform.runLater(showAlert);
        }
    }
}