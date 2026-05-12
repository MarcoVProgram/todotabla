package com.decroly.todotabla.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigator { //para cambiar entre escenas de manera fácil
    public static void changeScene(Stage current, String fxml) throws IOException {

        FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
        Parent root = loader.load();

        Scene scene = current.getScene();

        if (scene == null) {
            current.setScene(new Scene(root));
        } else {
            scene.setRoot(root);
        }
    }
}
