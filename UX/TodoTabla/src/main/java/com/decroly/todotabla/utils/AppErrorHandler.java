package com.decroly.todotabla.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(AppErrorHandler.class.getName());

    public static void manejar(Exception ex, String metodoCulpable) {
        logger.error("Error en: {}", metodoCulpable, ex);

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error en: " + metodoCulpable);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        });

        Runnable showAlert = () -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
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