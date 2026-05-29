package com.decroly.todotabla.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Notificator {

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
