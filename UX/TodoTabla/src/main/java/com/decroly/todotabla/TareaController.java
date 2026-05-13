package com.decroly.todotabla;

import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TareaController implements Initializable {

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
    @FXML
    public ListView<Tarea> listViewBacklog;
    ObservableList<Tarea> obsTareasBacklog = FXCollections.observableArrayList();
    @FXML
    public ListView<Tarea> listViewProgress;
    ObservableList<Tarea> obsTareasProgress = FXCollections.observableArrayList();
    @FXML
    public ListView<Tarea> listViewReview;
    ObservableList<Tarea> obsTareasReview = FXCollections.observableArrayList();
    @FXML
    public ListView<Tarea> listViewDone;
    ObservableList<Tarea> obsTareasDone = FXCollections.observableArrayList();

    //lista tareas
    List<Tarea> tareas = new ArrayList<>();
    ObservableList<Tarea> obsTareas = FXCollections.observableList(tareas);

    public void initialize(URL url, ResourceBundle rb) {
//        listView tareas backlog
        listViewBacklog.setItems(obsTareasBacklog);
        listViewBacklog.setCellFactory(tareaListView -> new ListCell<Tarea>() {

            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    setGraphic(null);
                } else {

                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    VBox card = new VBox(8, titulo, prioridad);
                    card.getStyleClass().add("kanban-list");

                    setGraphic(card);
                }
            }
        });

        //listView tareas progress
        listViewProgress.setItems(obsTareasProgress);
        listViewProgress.setCellFactory(obstareasInProgress -> new ListCell<Tarea>() {

            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    setGraphic(null);
                } else {

                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    VBox card = new VBox(8, titulo, prioridad);
                    card.getStyleClass().add("kanban-list");

                    setGraphic(card);
                }
            }
        });

        //listView tareas review
        listViewReview.setItems(obsTareasReview);
        listViewReview.setCellFactory(obstareasInReview -> new ListCell<Tarea>() {

            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    setGraphic(null);
                } else {

                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    VBox card = new VBox(8, titulo, prioridad);
                    card.getStyleClass().add("kanban-list");

                    setGraphic(card);
                }
            }
        });

        //listView tareas done
        listViewDone.setItems(obsTareasDone);
        listViewDone.setCellFactory(obstareasDone -> new ListCell<Tarea>() {

            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    setGraphic(null);
                } else {

                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    VBox card = new VBox(8, titulo, prioridad);
                    card.getStyleClass().add("kanban-list");

                    setGraphic(card);
                }
            }
        });
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
