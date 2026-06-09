package com.decroly.todotabla.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigator {

    private static Stage ventanaSecundaria;

    private static Stage getVentanaSecundaria() {
        return ventanaSecundaria;
    }

    //para cambiar entre escenas de manera fácil
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


    public static void arbrirVentanaSecundaria(String fxml, String title, Class clase) throws IOException {
        if(ventanaSecundaria != null && ventanaSecundaria.isShowing()){
            System.out.println("No se puede volver a abrir, hay una sesion existente");
            return;
        }

        // Cargar el archivo FXML
        FXMLLoader loader = new FXMLLoader(clase.getResource(fxml));
        Parent root = loader.load();

        // Crear una nueva ventana (Stage)
        ventanaSecundaria = new Stage();
        ventanaSecundaria.setTitle(title);
        ventanaSecundaria.setScene(new Scene(root));

        ventanaSecundaria.setResizable(false);
        ventanaSecundaria.setAlwaysOnTop(false);

//            listViewTareas.setItems(obsTareas);

        // Mostrar la ventana
        ventanaSecundaria.showAndWait();
    }
}
