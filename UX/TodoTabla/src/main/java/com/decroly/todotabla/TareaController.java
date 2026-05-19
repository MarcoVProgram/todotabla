package com.decroly.todotabla;

import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.sql.EstadosBDD;
import com.decroly.todotabla.model.sql.ProyetosBDD;
import com.decroly.todotabla.model.sql.TareasBDD;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

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
    private ObservableList<Tarea> listaTareas;



    //lista tareas
    List<Tarea> tareas = new ArrayList<>();
    ObservableList<Tarea> obsTareas = FXCollections.observableList(tareas);

    public void initialize(URL url, ResourceBundle rb) {

        ObservableList<Integer> prioridades = FXCollections.observableList(new LinkedList<>());

        for (int i = 0; i <= 100; i++) {
            prioridades.add(i);
        }
        comboBoxPrioridadTarea.setItems(prioridades);

        listarTareas();

    }

    private void listarTareas() {
        listaTareas = FXCollections.observableList(new ArrayList<>(TareasBDD.getTareas().values())); // No puedo filtrar las tareas por proyecto

        listViewTareas.setItems(listaTareas);
        listViewTareas.setCellFactory(listaTareas ->  new ListCell<Tarea>() {
            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    setGraphic(null);
                } else {

                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    VBox card = new VBox(8, titulo);
                    card.getStyleClass().add("kanban-list");

                    setGraphic(card);
                }
            }
        });

    }

    private void actualizarTareas() {
        Map<Integer, Tarea> todasTareasDelUniverso = TareasBDD.getTareas();
        if (todasTareasDelUniverso != null) {
            obsTareas.addAll((TareasBDD.getTareas()).values());
            listViewTareas.refresh();
        }
    }

    //--------AGREGAR TAREA-------------
    @FXML
    private void addTarea(){
        boolean added = false;

        //obtener valores campos
        String nombre = nombreTarea.getText();
        int prioridad = comboBoxPrioridadTarea.getSelectionModel().getSelectedItem();

        //valores extra necesarios // TODO cambiar esto para seleccionar otros proyectos
        Proyecto idProyecto = ProyetosBDD.getProyecto(1);

        boolean insertarExito = TareasBDD.insertar(new Tarea(nombre, prioridad, 
                EstadosBDD.getEstado("Backlog"), ProyetosBDD.getProyecto(1)));
        if (insertarExito) {
            this.actualizarTareas();
        }
        else {
            (new Alert(Alert.AlertType.ERROR,"No se pudo añadir", ButtonType.OK)).show();
        }

        actualizarTareas();
    }

    @FXML
    public void modTarea(ActionEvent event) { // TODO terminar

        //obtener valores campos
        String nombre = nombreTarea.getText();
        int prioridad = comboBoxPrioridadTarea.getSelectionModel().getSelectedItem();

        //valores extra necesarios // TODO cambiar esto para seleccionar otros proyectos
        Proyecto idProyecto = ProyetosBDD.getProyecto(1);

        boolean insertarExito = TareasBDD.insertar(new Tarea(nombre, prioridad,
                EstadosBDD.getEstado("Backlog"), ProyetosBDD.getProyecto(1)));
        if (insertarExito) {
            this.actualizarTareas();
        }
        else {
            (new Alert(Alert.AlertType.ERROR,"No se pudo añadir", ButtonType.OK)).show();
        }

        actualizarTareas();
    }

    @FXML
    public void removeTarea(ActionEvent event) {
        boolean estado = false;
        ObservableList<Tarea> listaDeTareas = listViewTareas.getSelectionModel().getSelectedItems();
        
        for (Tarea t: listaDeTareas) {
            estado = TareasBDD.borrar(t);
        }

        if (estado) {
            listarTareas();
        } else {
            (new Alert(Alert.AlertType.WARNING, "No se ha podido borrar", ButtonType.OK)).showAndWait();
        }
    }
}
