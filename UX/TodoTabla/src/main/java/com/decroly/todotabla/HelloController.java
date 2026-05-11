package com.decroly.todotabla;

import com.decroly.todotabla.model.Miembro;
import com.decroly.todotabla.model.Navigator;
import com.decroly.todotabla.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    //lista miembros
    List<Miembro> miembros = new ArrayList<>();
    ObservableList<Miembro> obsMiembros = FXCollections.observableList(miembros);

    //lista tareas
    List<Tarea> tareas = new ArrayList<>();
    ObservableList<Tarea> obsTareas = FXCollections.observableList(tareas);
    
    //lista proyectos
    List<Proyecto> proyectos = new ArrayList<>();
    ObservableList<Proyecto> obsProyectos = FXCollections.observableList(proyectos);
    
    //PESTAÑA INICIO
    @FXML
    private Button panelKanbanBtn;

    @FXML
    private Node root;
    
    //PESTAÑA KANBAN
    @FXML
    private ImageView returnBtn;

    //PESTAÑA TAREA
    @FXML
    private Button addTarea;

    @FXML
    private Button filterTarea;

    @FXML
    private Button buscarTarea;

    @FXML
    public Label nombreProyecto;

    @FXML
    public TextField nombreTarea;
    
    @FXML
    public ComboBox<String> comboBoxPrioridadTarea;

    @FXML
    public DatePicker fechaAsignacionTarea;

    @FXML
    public DatePicker fechaFinTarea;

    @FXML
    public ListView<Tarea> listViewTareas;

    //VARIABLES AUXILIARES
    private Stage ventanaSecundaria;
    
    
    //FORMATTER
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize(){
    }


//----------------DESPLAZAMIENTO ENTRE VENTANAS-------------
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
    
    //--------AGREGAR TAREA-------------
    @FXML
    private boolean addTarea(){
        boolean added = false;

        //obtener valores campos
        String nombre = nombreTarea.getText();
        int prioridad = Integer.parseInt(comboBoxPrioridadTarea.getSelectionModel().getSelectedItem());
        LocalDate fechaInit = LocalDate.parse(fechaAsignacionTarea.getValue().format(dtf));
        LocalDate fechaFin = LocalDate.parse(fechaFinTarea.getValue().format(dtf));
        
        //valores extra necesarios
        Miembro idMiembro = null;
        Proyecto idProyecto = null;
        
        Tarea tarea = new Tarea(nombre, idMiembro, prioridad, null, fechaInit, fechaFin, idProyecto);
        obsTareas.add(tarea);

        return added;
    }

}