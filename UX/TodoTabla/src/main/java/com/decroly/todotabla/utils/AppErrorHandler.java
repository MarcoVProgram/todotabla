package com.decroly.todotabla.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AppErrorHandler {

    public static void manejar(Exception ex, String metodoCulpable) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error en: " + metodoCulpable);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        });
    }
}