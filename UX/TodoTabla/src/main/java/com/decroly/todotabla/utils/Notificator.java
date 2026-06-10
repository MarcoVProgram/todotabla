package com.decroly.todotabla.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Clase de utilidad para mostrar diálogos de notificación al usuario.
 * Todos los métodos son seguros para llamar desde cualquier hilo, delegando
 * a {@link Platform#runLater} cuando es necesario.
 */
public class Notificator {

    /**
     * Muestra un diálogo de información general.
     *
     * @param cabecera encabezado del diálogo
     * @param mensage  mensaje a mostrar al usuario
     */
    public static void informar(String cabecera, String mensage) {

        Runnable showAlert = () -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().getStylesheets().add(
                AppErrorHandler.class.getResource("/com/decroly/todotabla/style.css").toExternalForm()
            );
            alert.getDialogPane().getStyleClass().add("inform-alert");

            alert.setTitle("Información");
            alert.setHeaderText(cabecera);
            alert.setContentText(mensage);
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            showAlert.run();
        } else {
            Platform.runLater(showAlert);
        }
    }

    /**
     * Muestra un diálogo de operación completada con éxito.
     *
     * @param cabecera encabezado del diálogo
     * @param mensage  mensaje a mostrar al usuario
     */
    public static void exito(String cabecera, String mensage) {

        Runnable showAlert = () -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().getStylesheets().add(
                AppErrorHandler.class.getResource("/com/decroly/todotabla/style.css").toExternalForm()
            );
            alert.getDialogPane().getStyleClass().add("success-alert");

            alert.setTitle("Éxito");
            alert.setHeaderText(cabecera);
            alert.setContentText(mensage);
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            showAlert.run();
        } else {
            Platform.runLater(showAlert);
        }
    }

    /**
     * Muestra un diálogo de advertencia.
     *
     * @param cabecera encabezado del diálogo
     * @param mensage  mensaje a mostrar al usuario
     */
    public static void advertencia(String cabecera, String mensage) {

        Runnable showAlert = () -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().getStylesheets().add(
                AppErrorHandler.class.getResource("/com/decroly/todotabla/style.css").toExternalForm()
            );
            alert.getDialogPane().getStyleClass().add("warning-alert");

            alert.setTitle("Aviso");
            alert.setHeaderText(cabecera);
            alert.setContentText(mensage);
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            showAlert.run();
        } else {
            Platform.runLater(showAlert);
        }
    }

    /**
     * Muestra un diálogo de error.
     *
     * @param cabecera encabezado del diálogo
     * @param mensage  mensaje a mostrar al usuario
     */
    public static void error(String cabecera, String mensage) {

        Runnable showAlert = () -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add(
                AppErrorHandler.class.getResource("/com/decroly/todotabla/style.css").toExternalForm()
            );
            alert.getDialogPane().getStyleClass().add("error-alert");

            alert.setTitle("Información");
            alert.setHeaderText(cabecera);
            alert.setContentText(mensage);
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            showAlert.run();
        } else {
            Platform.runLater(showAlert);
        }
    }
}
