package com.decroly.todotabla;

import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.utils.Navigator;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    //lista miembros
    List<Usuario> usuarios = new ArrayList<>();
    ObservableList<Usuario> obsUsuarios = FXCollections.observableList(usuarios);

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
    public ComboBox<Integer> comboBoxPrioridadTarea;

    @FXML
    public ListView<Tarea> listViewTareas;

    //LISTAS
    @FXML
    public ListView<Tarea> listViewBacklog;
        List<Tarea> tareasBacklog = new ArrayList<>();
        ObservableList<Tarea> obstareasBacklog = FXCollections.observableList(tareasBacklog);
    @FXML
    public ListView<Tarea> listViewProgress;
        List<Tarea> tareasInProgress = new ArrayList<>();
        ObservableList<Tarea> obstareasInProgress = FXCollections.observableList(tareasInProgress);
    @FXML
    public ListView<Tarea> listViewReview;
        List<Tarea> tareasInReview = new ArrayList<>();
        ObservableList<Tarea> obstareasInReview = FXCollections.observableList(tareasInReview);
    @FXML
    public ListView<Tarea> listViewDone;
        List<Tarea> tareasDone = new ArrayList<>();
        ObservableList<Tarea> obstareasDone = FXCollections.observableList(tareasDone);

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

//            listViewTareas.setItems(obsTareas);

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

        //listView tareas backlog
//        listViewBacklog.setItems(obstareasBacklog);
//        listViewBacklog.setCellFactory(tareaListView -> new ListCell<Tarea>() {
//
//            @Override
//            protected void updateItem(Tarea tarea, boolean empty) {
//                super.updateItem(tarea, empty);
//
//                if (empty || tarea == null) {
//                    setGraphic(null);
//                } else {
//
//                    Label titulo = new Label(tarea.getNombre());
//                    titulo.getStyleClass().add("titulo-tarea");
//
//                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());
//
//                    VBox card = new VBox(8, titulo, prioridad);
//                    card.getStyleClass().add("kanban-list");
//
//                    setGraphic(card);
//                }
//            }
//        });
//
//        //listView tareas progress
//        listViewProgress.setItems(obstareasInProgress);
//        listViewProgress.setCellFactory(obstareasInProgress -> new ListCell<Tarea>() {
//
//            @Override
//            protected void updateItem(Tarea tarea, boolean empty) {
//                super.updateItem(tarea, empty);
//
//                if (empty || tarea == null) {
//                    setGraphic(null);
//                } else {
//
//                    Label titulo = new Label(tarea.getNombre());
//                    titulo.getStyleClass().add("titulo-tarea");
//
//                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());
//
//                    VBox card = new VBox(8, titulo, prioridad);
//                    card.getStyleClass().add("kanban-list");
//
//                    setGraphic(card);
//                }
//            }
//        });
//
//        //listView tareas review
//        listViewReview.setItems(obstareasInReview);
//        listViewReview.setCellFactory(obstareasInReview -> new ListCell<Tarea>() {
//
//            @Override
//            protected void updateItem(Tarea tarea, boolean empty) {
//                super.updateItem(tarea, empty);
//
//                if (empty || tarea == null) {
//                    setGraphic(null);
//                } else {
//
//                    Label titulo = new Label(tarea.getNombre());
//                    titulo.getStyleClass().add("titulo-tarea");
//
//                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());
//
//                    VBox card = new VBox(8, titulo, prioridad);
//                    card.getStyleClass().add("kanban-list");
//
//                    setGraphic(card);
//                }
//            }
//        });
//
//        //listView tareas done
//        listViewDone.setItems(obstareasDone);
//        listViewDone.setCellFactory(obstareasDone -> new ListCell<Tarea>() {
//
//            @Override
//            protected void updateItem(Tarea tarea, boolean empty) {
//                super.updateItem(tarea, empty);
//
//                if (empty || tarea == null) {
//                    setGraphic(null);
//                } else {
//
//                    Label titulo = new Label(tarea.getNombre());
//                    titulo.getStyleClass().add("titulo-tarea");
//
//                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());
//
//                    VBox card = new VBox(8, titulo, prioridad);
//                    card.getStyleClass().add("kanban-list");
//
//                    setGraphic(card);
//                }
//            }
//        });
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
        int prioridad = comboBoxPrioridadTarea.getSelectionModel().getSelectedItem();

        //valores extra necesarios
        Proyecto idProyecto = null;

        Tarea tarea = new Tarea(nombre, prioridad, null, idProyecto);
        obsTareas.add(tarea);

        return added;
    }

}