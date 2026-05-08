package com.decroly.todotabla;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML
    private Button addTarea;

    @FXML
    private Button filterTarea;

    @FXML
    private Button buscarTarea;

    @FXML
    private Button panelKanbanBtn;

    @FXML
    private ImageView returnBtn;



    @FXML
    private void initialize(){
    }

    @FXML
    private void cerrarPrincipal(){
        Stage principal = (Stage) panelKanbanBtn.getScene().getWindow();

        principal.close();
    }

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

    private Stage ventanaKanban;

    @FXML
    private void abrirVentanaMain() {
        try {

            if(ventanaKanban != null && ventanaKanban.isShowing()){
                System.out.println("No se puede volver a abrir, hay una sesion existente");
                return;
            }

            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            ventanaKanban = new Stage();
            ventanaKanban.setTitle("KanBan");
            ventanaKanban.setScene(new Scene(root));

            ventanaKanban.setResizable(false);

            cerrarPrincipal();

            // Mostrar la ventana
            ventanaKanban.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void returnToMain(){
        Stage principal = (Stage) panelKanbanBtn.getScene().getWindow();
        principal.show();
    }
}