package com.decroly.todotabla;

import com.decroly.todotabla.model.Navigator;
import com.decroly.todotabla.model.Tarea;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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


    @FXML
    private void initialize(){
    }



    @FXML
    private void abrirVentanaTarea() { //panel tarea
        try {

            if(ventanaSecundaria != null && ventanaSecundaria.isShowing()){
                System.out.println("No se puede volver a abrir, hay una sesion existente");
                return;
            }

            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tarea-view.fxml"));
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
    private void abrirVentanaPrincipal() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/kanban-view.fxml");
    }
    

    @FXML
    private void returnToMain() throws IOException { //abrir pantalla principal (menú)
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/main-view.fxml");
    }
}