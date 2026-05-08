package com.decroly.todotabla;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML
    private Button addTarea;

    @FXML
    private Button filterTarea;

    @FXML
    private Button buscarTarea;

//    @FXML
//    private void initialize(){
//
//    }

    private Stage ventanaSecundaria;

    @FXML
    private void abrirVentanaSecundaria() {
        try {

            if(ventanaSecundaria != null && ventanaSecundaria.isShowing()){
                System.out.println("No se puede volver a abrir, hay una sesion existente");
                return;
            }

            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view2.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            ventanaSecundaria = new Stage();
            ventanaSecundaria.setTitle("Añadir tarea");
            ventanaSecundaria.setScene(new Scene(root));

            ventanaSecundaria.setResizable(false);
            ventanaSecundaria.setAlwaysOnTop(true);

            // Mostrar la ventana
            ventanaSecundaria.show();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}