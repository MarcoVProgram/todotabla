package com.decroly.todotabla;

import com.decroly.todotabla.model.Tarea;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class HelloController {

    
    //pestaña inicio
    @FXML
    private Button panelKanbanBtn;

    @FXML
    private Node root;
    
    //pestaña kanban
    @FXML
    private ImageView returnBtn;

    //pestaña tarea
    @FXML
    private Button addTarea;

    @FXML
    private Button filterTarea;

    @FXML
    private Button buscarTarea;

    @FXML
    public Label nombreProyecto;


    public TextField nombreTarea;

    public ComboBox<String> comboBoxPrioridadTarea;

    public DatePicker fechaAsignacionTarea;
    
    public DatePicker fechaFinTarea;

    public ListView<Tarea> listViewTareas;

    //variables aux
    private Stage ventanaSecundaria;

    private Stage ventanaKanban;

    private Stage main;
    
    

    @FXML
    private void initialize(){
    }

    

    @FXML
    private void cerrarPrincipal() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void cerrarKanBan(){
        Stage main = (Stage) returnBtn.getScene().getWindow();
        main.close();
    }

    @FXML
    private void abrirVentanaTarea() { //panel tarea
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

    

    @FXML
    private void abrirVentanaPrincipal() { //panel kanban
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
    private void abrirVentanaMain() { //panel principal botones
        try {

            if(main != null && main.isShowing()){
                System.out.println("No se puede volver a abrir, hay una sesion existente");
                return;
            }

            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            main = new Stage();
            main.setTitle("Bienvenido a TodoTabla");
            main.setScene(new Scene(root));

            main.setResizable(false);

            // Mostrar la ventana
            main.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void returnToMain(){
        cerrarKanBan();
        abrirVentanaMain();
    }
}